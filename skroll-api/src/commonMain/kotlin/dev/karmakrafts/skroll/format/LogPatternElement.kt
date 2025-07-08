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

/**
 * A function which represents a transformation applied for a given template variable
 * in the log pattern.
 */
@Suppress("NOTHING_TO_INLINE")
fun interface LogPatternElement {
    /**
     * Transforms the given string and replaces all occurrences
     * of the template variable associated with this format element.
     *
     * @param logger The logger instance associated with this format element.
     * @param level The level at which the message will be logged.
     * @param content The raw content of the message.
     * @param marker The log marker the message being formatted is tagged with.
     * @param s The string being transformed.
     * @return The transformed string or the original string if no template variable was replaced.
     */
    operator fun invoke( // @formatter:off
        logger: Logger,
        level: LogLevel,
        content: Any,
        marker: LogMarker?,
        s: String
    ): String // @formatter:on

    /**
     * Concatenates this format element with another to form a new [LogFormatter] instance.
     *
     * @param other The element with which to join this element to form a new formatter instance.
     * @return A new [LogFormatter] instance containing both this and the other format element.
     */
    operator fun plus(other: LogPatternElement): LogFormatter = LogFormatter { logger, level, content, marker, s ->
        other(logger, level, content, marker, this(logger, level, content, marker, s))
    }

    /**
     * Creates a new log formatter from this format element.
     *
     * @return A new [LogFormatter] instance containing only this format element.
     */
    fun asFormatter(): LogFormatter = LogFormatter(this)
}