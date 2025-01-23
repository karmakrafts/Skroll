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
import kotlinx.io.Sink
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString

interface LogAppender : AutoCloseable {
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
    override fun append(level: LogLevel, message: String, marker: LogMarker?) {
        if (!filter(level, message, marker)) return
        println(message)
    }

    override fun close() {}
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

    private val sink: Sink = sinks.getOrPut(path) {
        RefCountedSink(SystemFileSystem.sink(path).buffered())
    }.acquire().sink

    override fun append(level: LogLevel, message: String, marker: LogMarker?) {
        if (!filter(level, message, marker)) return
        // Make sure to strip out any ANSI codes when writing to file
        sink.writeString("${message.toAnsi().cleanString()}\n")
    }

    override fun close() {
        val ref = sinks[path] ?: return
        ref.release {
            sinks -= path
        }
    }
}