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

@Suppress("NOTHING_TO_INLINE")
fun interface LogFilter {
    companion object {
        val always: LogFilter = LogFilter { _, _, _ -> true }

        inline fun levels(vararg levels: LogLevel): LogFilter = object : LogFilter {
            private val filteredLevels: Set<LogLevel> = levels.toSet()
            override fun invoke(level: LogLevel, message: String, marker: LogMarker?): Boolean = level in filteredLevels
        }

        inline fun levelsExcept(vararg levels: LogLevel): LogFilter = object : LogFilter {
            private val filteredLevels: Set<LogLevel> = LogLevel.entries.toSet() - levels.toSet()
            override fun invoke(level: LogLevel, message: String, marker: LogMarker?): Boolean = level in filteredLevels
        }

        inline fun markers(vararg markers: LogMarker): LogFilter = object : LogFilter {
            private val filteredMarkers: Set<LogMarker> = markers.toSet()
            override fun invoke(level: LogLevel, message: String, marker: LogMarker?): Boolean =
                marker in filteredMarkers
        }

        inline fun containsString(s: String): LogFilter = LogFilter { _, message, _ -> s in message }
    }

    operator fun invoke(level: LogLevel, message: String, marker: LogMarker?): Boolean

    operator fun plus(other: LogFilter): LogFilter = LogFilter { level, message, marker ->
        this(level, message, marker) && other(level, message, marker)
    }
}