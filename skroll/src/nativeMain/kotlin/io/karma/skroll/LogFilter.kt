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

import io.karma.skroll.LogFilter.Companion.levels
import io.karma.skroll.LogFilter.Companion.markers


/**
 * A functional interface which allows expressing a finely grained
 * message filter which can be applied on a per-appender basis.
 */
@Suppress("NOTHING_TO_INLINE")
fun interface LogFilter {
    companion object {
        /**
         * A filter which will always let all messages through.
         */
        val always: LogFilter = LogFilter { _, _, _ -> true }

        /**
         * Creates a filter which lets through all messages at the specified level(s).
         *
         * @param levels The levels at which to pass messages.
         * @return A new filter instance only allowing the given levels.
         */
        inline fun levels(vararg levels: LogLevel): LogFilter = object : LogFilter {
            private val filteredLevels: Set<LogLevel> = levels.toSet()
            override fun invoke(level: LogLevel, message: String, marker: LogMarker?): Boolean = level in filteredLevels
        }

        /**
         * Creates a filter which lets through all messages at any level except the specified ones.
         *
         * @param levels The levels at which to omit messages.
         * @return A new filter instance allowing any level except the given ones.
         */
        inline fun levelsExcept(vararg levels: LogLevel): LogFilter = object : LogFilter {
            private val filteredLevels: Set<LogLevel> = LogLevel.entries.toSet() - levels.toSet()
            override fun invoke(level: LogLevel, message: String, marker: LogMarker?): Boolean = level in filteredLevels
        }

        /**
         * Creates a filter which lets through all messages with the given marker if enabled.
         *
         * @param markers The markers to pass messages for.
         * @return A new filter instance allowing all messages with the given markers to pass.
         */
        inline fun markers(vararg markers: LogMarker): LogFilter = object : LogFilter {
            private val filteredMarkers: Set<LogMarker> = markers.toSet()
            override fun invoke(level: LogLevel, message: String, marker: LogMarker?): Boolean =
                marker in filteredMarkers
        }

        /**
         * Creates a filter which lets through only messages containing the given substring.
         *
         * @param s The substring to look for in all messages-
         * @return A new filter instance allowing all messages with the given substring to pass.
         */
        inline fun containsString(s: String): LogFilter = LogFilter { _, message, _ -> s in message }
    }

    /**
     * Check if the given message should be passed to the appender this filter is associated with.
     *
     * @param level The current log level.
     * @param message The formatted message.
     * @param marker The marker of the current message.
     * @return True if the current message should be forwarded to the appender.
     */
    operator fun invoke(level: LogLevel, message: String, marker: LogMarker?): Boolean

    /**
     * Concatenates this filter with another using a short-circuit AND operation (`&&`).
     *
     * @param other The filter with which to join this filter instance.
     * @return A new filter instance checking both, this filter and the given one
     *  using a short-circuit AND operation.
     */
    operator fun plus(other: LogFilter): LogFilter = LogFilter { level, message, marker ->
        this(level, message, marker) && other(level, message, marker)
    }
}