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

package dev.karmakrafts.skroll.appender

import co.touchlab.stately.collections.SharedHashMap
import dev.karmakrafts.filament.Mutex
import dev.karmakrafts.filament.guarded
import dev.karmakrafts.skroll.LogLevel
import dev.karmakrafts.skroll.LogMarker
import dev.karmakrafts.skroll.Logger
import dev.karmakrafts.skroll.ansi.toAnsi
import dev.karmakrafts.skroll.format.LogFormatter
import kotlinx.io.Sink
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.decrementAndFetch
import kotlin.concurrent.atomics.incrementAndFetch

@OptIn(ExperimentalAtomicApi::class)
private data class RefCountedSink(
    val value: Sink
) {
    private var isReleased: AtomicBoolean = AtomicBoolean(false)
    private var refCount: AtomicInt = AtomicInt(0)

    @Suppress("NOTHING_TO_INLINE")
    inline fun acquire(): RefCountedSink {
        refCount.incrementAndFetch()
        return this
    }

    inline fun release(releaseAction: () -> Unit = {}): RefCountedSink {
        if (isReleased.load()) return this
        if (refCount.load() == 0) {
            releaseAction()
            value.close()
            isReleased.store(true)
            return this
        }
        refCount.decrementAndFetch()
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

    private val sink: RefCountedSink = sinks.getOrPut(path) {
        RefCountedSink(SystemFileSystem.sink(path).buffered())
    }.acquire()

    private val mutex: Mutex = Mutex()

    override fun append(logger: Logger, level: LogLevel, message: String, marker: LogMarker?) {
        if (!filter(level, message, marker)) return
        // Make sure to strip out any ANSI codes when writing to file
        mutex.guarded {
            sink.value.writeString("${message.toAnsi().cleanString()}\n")
        }
    }

    override fun dispose() {
        sink.release { sinks -= path }
    }
}