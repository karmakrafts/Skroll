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

@file:JvmName("SkrollBackend$")

package dev.karmakrafts.skroll.backend

import co.touchlab.stately.collections.SharedHashMap
import dev.karmakrafts.filament.Thread
import dev.karmakrafts.skroll.LogLevel
import dev.karmakrafts.skroll.LogMarker
import dev.karmakrafts.skroll.Logger
import dev.karmakrafts.skroll.SkrollLogMarker
import dev.karmakrafts.skroll.SkrollLogger
import dev.karmakrafts.skroll.ansi.AnsiSequence
import dev.karmakrafts.skroll.appender.ConsoleAppender
import dev.karmakrafts.skroll.appender.FileAppender
import dev.karmakrafts.skroll.appender.LogAppender
import dev.karmakrafts.skroll.appender.LogFilter
import dev.karmakrafts.skroll.config.LoggerConfigBuilder
import dev.karmakrafts.skroll.format.LogFormatter
import dev.karmakrafts.skroll.format.LogPatternElement
import kotlinx.datetime.Clock
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.char
import kotlinx.io.files.Path
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.jvm.JvmName

private typealias DateTimeElement = Pair<String, DateTimeFormatBuilder.WithDateTimeComponents.() -> Unit>

internal expect fun getDefaultLogLevel(): LogLevel

internal expect fun createSystemLogAppender( // @formatter:off
    pattern: String,
    formatter: LogFormatter = LogFormatter.default,
    filter: LogFilter = LogFilter.always
): LogAppender // @formatter:on

// TODO: document this
@OptIn(ExperimentalAtomicApi::class)
object SkrollLogBackend : LogBackend {
    override val name: String = "Skroll"
    override val defaultLogLevel: LogLevel = getDefaultLogLevel()

    private val _defaultConfigSpec: AtomicReference<LoggerConfigBuilder.() -> Unit> = AtomicReference {
        platformConsoleAppender(
            "{{levelColor}}>>  {{levelSymbol}}\t{{datetime(hh:mm:ss.SSS)}} ({{name}} @ {{thread}}) {{message}}{{r}}"
        )
        fileAppender(
            pattern = "[{{level}}][{{datetime(yyyy/MM/dd hh:mm:ss.SSS)}}] ({{name}} @ {{thread}}) {{message}}",
            path = Path("latest.log"),
            filter = LogFilter.levelsExcept(LogLevel.DEBUG, LogLevel.TRACE)
        )
        fileAppender(
            pattern = "[{{level}}][{{datetime(yyyy/MM/dd hh:mm:ss.SSS)}}] ({{name}} @ {{thread}}) {{message}}",
            path = Path("debug.log"),
            filter = LogFilter.levels(LogLevel.DEBUG)
        )
    }
    override var defaultConfigSpec: LoggerConfigBuilder.() -> Unit
        get() = _defaultConfigSpec.load()
        set(value) {
            _defaultConfigSpec.store(value)
        }

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

    private fun String.replaceTemplate(name: String, value: String): String {
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

    override val defaultFormatter: LogFormatter = LogPatternElement { logger, level, content, marker, s ->
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
    }.asFormatter()

    override fun createMarker(
        key: String, name: String, isEnabled: Boolean
    ): LogMarker {
        return SkrollLogMarker(key, name, isEnabled)
    }

    override fun createLogger(
        name: String, configSpec: LoggerConfigBuilder.() -> Unit
    ): Logger {
        return SkrollLogger(name, LoggerConfigBuilder().apply(configSpec).build())
    }

    override fun createFileAppender(
        pattern: String, formatter: LogFormatter, filter: LogFilter, path: Path
    ): LogAppender {
        return FileAppender(pattern, formatter, path, filter)
    }

    override fun createConsoleAppender(
        pattern: String, formatter: LogFormatter, filter: LogFilter
    ): LogAppender {
        return ConsoleAppender(pattern, formatter, filter)
    }

    override fun createSystemAppender(
        pattern: String, formatter: LogFormatter, filter: LogFilter
    ): LogAppender {
        return createSystemLogAppender(pattern, formatter, filter)
    }
}