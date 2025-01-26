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

@file:Suppress("NOTHING_TO_INLINE")

package io.karma.skroll

import co.touchlab.stately.collections.SharedHashMap
import io.karma.pthread.Thread
import kotlinx.datetime.Clock
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.char

private typealias DateTimeElement = Pair<String, DateTimeFormatBuilder.WithDateTimeComponents.() -> Unit>

/**
 * A function which represents a transformation applied for a given template variable
 * in the log pattern.
 */
@Suppress("NOTHING_TO_INLINE")
fun interface LogPatternElement {
    companion object {
        private val maxLevelNameLength: Int = LogLevel.entries //
            .maxOf { it.name.length }

        private val paddedLevelNames: Array<String> = LogLevel.entries //
            .map { it.name.padEnd(maxLevelNameLength, '-') } //
            .toTypedArray()

        private val dateTimeElements: List<DateTimeElement> = listOf( // @formatter:off
            "yyyy" to { year() },
            "yy" to { yearTwoDigits(2000) },
            "MM" to { monthNumber() },
            "dd" to { dayOfMonth() },
            "hh" to { amPmHour() },
            "HH" to { hour() },
            "mm" to { minute() },
            "ss" to { second() },
            "SSS" to { secondFraction(3) }
        ) // @formatter:on

        private val dateTimeFormatCache: SharedHashMap<String, DateTimeFormat<DateTimeComponents>> = SharedHashMap(1)

        private inline fun String.replaceTemplate(name: String, value: String): String {
            return replace("{{$name}}", value)
        }

        private inline fun String.replaceTemplate(name: String, transform: (String) -> String): String {
            var result = this
            val matchString = "{{$name("
            val matchLength = matchString.length
            while (true) {
                val matchBegin = result.indexOf(matchString)
                if (matchBegin == -1) break
                val paramsBegin = matchBegin + matchLength
                val paramsEnd = result.indexOf(')', paramsBegin)
                val params = result.substring(paramsBegin, paramsEnd)
                result = result.replaceRange(
                    matchBegin, matchBegin + matchLength + (paramsEnd - paramsBegin) + 3, transform(params)
                )
            }
            return result
        }

        private fun String.toDateTimeFormat(): DateTimeFormat<DateTimeComponents> {
            return dateTimeFormatCache.getOrPut(this) {
                DateTimeComponents.Format {
                    // Slide from left to right and add chars and components as we go
                    var skip = 0
                    outer@ for (charIndex in indices) {
                        if (skip > 0) { // Skip any next chars from previous iteration
                            skip--
                            continue
                        }
                        // Try to match one of the elements from the current char index
                        for (element in dateTimeElements) {
                            if (!this@toDateTimeFormat.startsWith(element.first, charIndex)) continue
                            element.second(this)
                            skip = element.first.length - 1 // Skip n chars until end of interpolation variable
                            continue@outer
                        }
                        char(this@toDateTimeFormat[charIndex]) // Add regular character
                    }
                }
            }
        }

        internal val rootElement: LogPatternElement = LogPatternElement { logger, level, content, marker, s ->
            s.replaceTemplate("r", AnsiSequence.reset.toString())
                .replaceTemplate("levelColor", level.ansi.toString())
                .replaceTemplate("marker", marker?.name ?: "n/a")
                .replaceTemplate("message", content.toString())
                .replaceTemplate("thread", Thread.name)
                .replaceTemplate("threadId", Thread.id.toString())
                .replaceTemplate("level", paddedLevelNames[level.ordinal])
                .replaceTemplate("levelSymbol", level.symbol)
                .replaceTemplate("name", logger.name)
                .replaceTemplate("datetime") { Clock.System.now().format(it.toDateTimeFormat()) }
        }
    }

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

value class LogFormatter @PublishedApi internal constructor(@PublishedApi internal val rootElement: LogPatternElement) {
    companion object {
        /**
         * The default formatter which provides all basic format elements like {{name}} and {{thread}}.
         */
        val default: LogFormatter = LogFormatter(LogPatternElement.rootElement)
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