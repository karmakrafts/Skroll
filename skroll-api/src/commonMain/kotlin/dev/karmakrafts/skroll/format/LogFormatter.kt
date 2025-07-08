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

package dev.karmakrafts.skroll.format

import dev.karmakrafts.skroll.LogLevel
import dev.karmakrafts.skroll.LogMarker
import dev.karmakrafts.skroll.Logger
import dev.karmakrafts.skroll.backend.LogBackend
import kotlin.jvm.JvmInline

/**
 * A formatter for log messages that applies transformations based on pattern elements.
 * LogFormatter is responsible for transforming raw log messages into formatted output
 * by applying one or more pattern elements in sequence.
 */
@JvmInline
value class LogFormatter @PublishedApi internal constructor(@PublishedApi internal val rootElement: LogPatternElement) {
    companion object {
        /**
         * A formatter that returns the input string unchanged.
         * This identity formatter can be used as a base for building more complex formatters
         * or when no formatting is desired.
         */
        val identity: LogFormatter = LogPatternElement { _, _, _, _, s -> s }.asFormatter()

        /**
         * The default formatter provided by the current log backend.
         */
        inline val default: LogFormatter
            get() = LogBackend.current.defaultFormatter
    }

    /**
     * Transforms the given string and replaces all occurrences
     * of the template variables associated with this formatter instance.
     *
     * @param logger The logger instance associated with this formatter.
     * @param level The level at which the message will be logged.
     * @param content The raw content of the message.
     * @param marker The log marker the message being formatted is tagged with.
     * @param s The string being transformed.
     * @return The transformed (formatted) message.
     */
    @Suppress("NOTHING_TO_INLINE")
    inline fun transform( // @formatter:off
        logger: Logger,
        level: LogLevel,
        content: Any,
        marker: LogMarker?,
        s: String
    ): String { // @formatter:on
        return rootElement(logger, level, content, marker, s)
    }

    /**
     * Concatenates this formatter with another format element to form a new [LogFormatter] instance.
     *
     * @param other The element with which to join this formatter to form a new formatter instance.
     * @return A new [LogFormatter] instance containing both these formatters elements and the other format element.
     */
    operator fun plus(other: LogPatternElement): LogFormatter = LogFormatter { logger, level, content, marker, s ->
        other(logger, level, content, marker, rootElement(logger, level, content, marker, s))
    }

    /**
     * Concatenates this formatter with another formatter to form a new [LogFormatter] instance.
     *
     * @param other The formatter with which to join this formatter to form a new formatter instance.
     * @return A new [LogFormatter] instance containing both these formatters elements and the other formatters elements.
     */
    operator fun plus(other: LogFormatter): LogFormatter = LogFormatter { logger, level, content, marker, s ->
        other.transform(logger, level, content, marker, rootElement(logger, level, content, marker, s))
    }
}
