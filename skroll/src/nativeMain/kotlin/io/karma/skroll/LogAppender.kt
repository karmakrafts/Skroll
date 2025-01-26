/*
 * Copyright 2025 Karma Krafts & associates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.karma.skroll

import co.touchlab.stately.collections.SharedHashMap
import co.touchlab.stately.collections.SharedSet
import io.karma.pthread.Mutex
import io.karma.pthread.guarded
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import kotlinx.io.Sink
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import platform.posix.atexit

interface LogAppender : AutoCloseable {
    @OptIn(ExperimentalForeignApi::class)
    companion object {
        @PublishedApi
        internal var appenders: SharedSet<LogAppender> = SharedSet()

        init {
            atexit(staticCFunction<Unit> { // @formatter:off
                LogAppender.cleanup()
            }) // @formatter:on
        }

        private fun cleanup() {
            for (appender in appenders) {
                appender.close()
            }
        }
    }

    val formatter: LogFormatter
    val pattern: String
    fun append(level: LogLevel, message: String, marker: LogMarker?)
}

@PublishedApi
internal class ConsoleAppender( // @formatter:off
    override val pattern: String,
    override val formatter: LogFormatter,
    private val filter: LogFilter
) : LogAppender { // @formatter:on
    init {
        LogAppender.appenders += this
    }

    private var isClosed: Boolean = false
    private val mutex: Mutex = Mutex.create()

    override fun append(level: LogLevel, message: String, marker: LogMarker?) {
        if (!filter(level, message, marker)) return
        mutex.guarded {
            println(message)
        }
    }

    override fun close() {
        if (isClosed) return
        mutex.close()
        isClosed = true
    }
}

private data class RefCountedSink(
    val sink: Sink, var refCount: Int = 0, private var isReleased: Boolean = false
) {
    @Suppress("NOTHING_TO_INLINE")
    inline fun acquire(): RefCountedSink {
        refCount++
        return this
    }

    inline fun release(releaseAction: (Sink) -> Unit = {}): RefCountedSink {
        if (isReleased) return this
        if (refCount == 0) {
            releaseAction(sink)
            sink.close()
            isReleased = true
            return this
        }
        refCount--
        return this
    }
}

@PublishedApi
internal class FileAppender( // @formatter:off
    override val pattern: String,
    override val formatter: LogFormatter,
    private val path: Path,
    private val filter: LogFilter
) : LogAppender { // @formatter:on
    companion object {
        private val sinks: SharedHashMap<Path, RefCountedSink> = SharedHashMap()
    }

    init {
        LogAppender.appenders += this
    }

    private val sink: Sink = sinks.getOrPut(path) {
        RefCountedSink(SystemFileSystem.sink(path).buffered())
    }.acquire().sink

    private var isClosed: Boolean = false
    private val mutex: Mutex = Mutex.create()

    override fun append(level: LogLevel, message: String, marker: LogMarker?) {
        if (!filter(level, message, marker)) return
        // Make sure to strip out any ANSI codes when writing to file
        mutex.guarded {
            sink.writeString("${message.toAnsi().cleanString()}\n")
        }
    }

    override fun close() {
        if (isClosed) return
        val ref = sinks[path] ?: return
        ref.release {
            sinks -= path
        }
        mutex.close()
        isClosed = true
    }
}