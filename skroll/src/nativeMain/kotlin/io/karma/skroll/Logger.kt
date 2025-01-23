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

import co.touchlab.stately.collections.SharedLinkedList
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import kotlinx.io.files.Path
import platform.posix.atexit
import kotlin.reflect.KClass

@ConsistentCopyVisibility
data class LoggerConfig internal constructor( // @formatter:off
    val initialLevel: LogLevel = LogLevel.default(),
    val appenders: List<LogAppender> = emptyList()
) // @formatter:on

sealed interface Logger {
    @OptIn(ExperimentalForeignApi::class)
    companion object {
        @PublishedApi
        internal var defaultConfig: LoggerConfig = LoggerConfig()

        @PublishedApi
        internal val loggers: SharedLinkedList<Logger> = SharedLinkedList()

        init {
            // @formatter:off
            atexit(staticCFunction<Unit> { Logger.cleanup() })
            // @formatter:on
        }

        private fun cleanup() {
            for (logger in loggers) {
                for (appender in logger.config.appenders) {
                    appender.close() // Make sure all streams/handles are closed
                }
            }
            for (appender in defaultConfig.appenders) {
                appender.close() // Make sure all streams/handles for default config are closed
            }
            loggers.clear()
        }

        @PublishedApi
        internal fun LoggerConfigBuilder.buildDefaultConfig() {
            consoleAppender(
                "{{levelColor}}>>  {{levelSymbol}}\t{{datetime(hh:mm:ss.SSS)}} ({{name}} @ {{thread}}) {{message}}{{r}}"
            )
            fileAppender(
                pattern = "[{{level}}][{{datetime(yyyy/MM/dd hh:mm:ss.SSS)}}] ({{name}} @ {{thread}}) {{message}}",
                path = Path("latest.log"),
                filter = LogFilter.levelsExcept(LogLevel.DEBUG)
            )
            fileAppender(
                pattern = "[{{level}}][{{datetime(yyyy/MM/dd hh:mm:ss.SSS)}}] ({{name}} @ {{thread}}) {{message}}",
                path = Path("debug.log"),
                filter = LogFilter.levels(LogLevel.DEBUG)
            )
        }

        inline fun setDefaultConfig(closure: LoggerConfigBuilder.() -> Unit = { buildDefaultConfig() }) {
            defaultConfig = LoggerConfigBuilder().apply(closure).build()
        }

        inline fun create(name: String, closure: LoggerConfigBuilder.() -> Unit = {}): Logger {
            return SimpleLogger(name, LoggerConfigBuilder().setFrom(defaultConfig).apply(closure).build()).apply {
                loggers += this
            }
        }

        inline fun create(clazz: KClass<*>, closure: LoggerConfigBuilder.() -> Unit = {}): Logger {
            val nameParts = requireNotNull(clazz.qualifiedName) { "Could not retrieve qualified class name" }.split('.')
            var name = ""
            for (i in nameParts.indices) {
                val part = nameParts[i]
                if (i == nameParts.lastIndex) {
                    name += part
                    continue
                }
                name += if (part.length > 2) part.substring(0..<2) else part
                if (i < nameParts.lastIndex) name += '.'
            }
            return create(name, closure)
        }
    }

    val name: String
    val config: LoggerConfig
    var level: LogLevel

    fun log(level: LogLevel, message: AnsiScope.() -> Any)
    fun log(marker: LogMarker?, level: LogLevel, message: AnsiScope.() -> Any)

    fun debug(message: AnsiScope.() -> Any) = log(LogLevel.DEBUG, message)
    fun info(message: AnsiScope.() -> Any) = log(LogLevel.INFO, message)
    fun warn(message: AnsiScope.() -> Any) = log(LogLevel.WARN, message)
    fun error(message: AnsiScope.() -> Any) = log(LogLevel.ERROR, message)
    fun fatal(message: AnsiScope.() -> Any) = log(LogLevel.FATAL, message)

    fun debug(marker: LogMarker?, message: AnsiScope.() -> Any) = log(marker, LogLevel.DEBUG, message)
    fun info(marker: LogMarker?, message: AnsiScope.() -> Any) = log(marker, LogLevel.INFO, message)
    fun warn(marker: LogMarker?, message: AnsiScope.() -> Any) = log(marker, LogLevel.WARN, message)
    fun error(marker: LogMarker?, message: AnsiScope.() -> Any) = log(marker, LogLevel.ERROR, message)
    fun fatal(marker: LogMarker?, message: AnsiScope.() -> Any) = log(marker, LogLevel.FATAL, message)
}

class LoggerConfigBuilder @PublishedApi internal constructor() {
    var level: LogLevel = LogLevel.default()

    @PublishedApi
    internal val appenders: ArrayList<LogAppender> = ArrayList()

    fun setFrom(config: LoggerConfig): LoggerConfigBuilder {
        level = config.initialLevel
        appenders += config.appenders
        return this
    }

    fun consoleAppender( // @formatter:off
        pattern: String,
        formatter: LogFormatter = LogFormatter.default,
        filter: LogFilter = LogFilter.always
    ) { // @formatter:on
        appenders += ConsoleAppender(pattern, formatter, filter)
    }

    fun fileAppender( // @formatter:off
        pattern: String,
        path: Path,
        formatter: LogFormatter = LogFormatter.default,
        filter: LogFilter = LogFilter.always
    ) { // @formatter:on
        appenders += FileAppender(pattern, formatter, path, filter)
    }

    @PublishedApi
    internal fun build(): LoggerConfig = LoggerConfig(level, appenders)
}

@PublishedApi
internal class SimpleLogger( // @formatter:off
    override val name: String,
    override val config: LoggerConfig
) : Logger { // @formatter:on
    override var level: LogLevel = config.initialLevel

    override fun log(level: LogLevel, message: AnsiScope.() -> Any) {
        if (level < this.level) return
        val messageContent = message(AnsiScope)
        for (appender in config.appenders) {
            appender.append(
                level, appender.formatter.transform(this, level, messageContent, null, appender.pattern), null
            )
        }
    }

    override fun log(marker: LogMarker?, level: LogLevel, message: AnsiScope.() -> Any) {
        if (level < this.level || marker?.isEnabled == false) return
        val messageContent = message(AnsiScope)
        for (appender in config.appenders) {
            appender.append(
                level, appender.formatter.transform(this, level, messageContent, marker, appender.pattern), marker
            )
        }
    }
}