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

import kotlinx.atomicfu.atomic
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path
import kotlin.reflect.KClass

/**
 * An interface which provides functions to log to all appenders of
 * this instance.
 */
sealed interface Logger {
    @OptIn(ExperimentalForeignApi::class)
    companion object {
        @PublishedApi
        internal var defaultConfig: LoggerConfig = LoggerConfig()

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

        /**
         * Override the default configuration used by every newly created logger instance as a template
         * for its own configuration.
         *
         * @param config The configuration spec applied to every newly created logger instance. See [LoggerConfigBuilder].
         */
        inline fun setDefaultConfig(config: LoggerConfigSpec = { buildDefaultConfig() }) {
            defaultConfig = LoggerConfigBuilder().apply(config).build()
        }

        /**
         * Create a new [Logger] instance with the given name.
         *
         * @param name The name of the newly created logger instance.
         * @param config The configuration spec applied to the newly created logger instance. See [LoggerConfigBuilder].
         * @return A new logger instance with the given name.
         */
        inline fun create(name: String, config: LoggerConfigSpec = {}): Logger {
            return SimpleLogger(name, LoggerConfigBuilder().setFrom(defaultConfig).apply(config).build())
        }

        /**
         * Create a new [Logger] instance owned by the given class.
         * The package segments of the class FQN will be shortened to 2 characters max to keep the log readable.
         *
         * @param clazz The class the newly created logger instance is owned by.
         * @param config The configuration spec applied to the newly created logger instance. See [LoggerConfigBuilder].
         * @return A new logger instance owned by the given class.
         */
        inline fun create(clazz: KClass<*>, config: LoggerConfigSpec = {}): Logger {
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
            return create(name, config)
        }
    }

    /**
     * The name of this logger instance.
     * This value is expanded into {{name}} in the formatting pattern.
     */
    val name: String

    /**
     * The immutable configuration of this logger instance.
     */
    val config: LoggerConfig

    /**
     * The current log level of this logger instance.
     * This may be changed at any time from any thread.
     */
    var level: LogLevel

    /**
     * If true, this logger will forward all messages to its appenders,
     * otherwise all messages will be omitted for this logger instance.
     */
    var isEnabled: Boolean

    /**
     * Log a message at the given level to all appenders.
     * If the given level is less than [Logger.level], the message will be omitted.
     *
     * @param level The level at which to report the message.
     * @param message An ANSI-string closure which returns any object whose [toString] function will be invoked.
     */
    fun log(level: LogLevel, message: AnsiScope.() -> Any)

    /**
     * Log a message at the given level to all appenders.
     * If the given level is less than [Logger.level], the message will be omitted.
     * Additionally, if the given marker is disabled, this message will be omitted as well.
     *
     * @param marker The marker with which to tag the logged message if not null.
     * @param level The level at which to report the message.
     * @param message An ANSI-string closure which returns any object whose [toString] function will be invoked.
     */
    fun log(marker: LogMarker?, level: LogLevel, message: AnsiScope.() -> Any)

    /**
     * Log a message at the [LogLevel.DEBUG] level if enabled.
     * Semantically equal to
     * ```kotlin
     * Logger.log(LogLevel.DEBUG, message)
     * ```
     *
     * @param message An ANSI-string closure which returns any object whose [toString] function will be invoked.
     */
    fun debug(message: AnsiScope.() -> Any) = log(LogLevel.DEBUG, message)

    /**
     * Log a message at the [LogLevel.INFO] level if enabled.
     * Semantically equal to
     * ```kotlin
     * Logger.log(LogLevel.INFO, message)
     * ```
     *
     * @param message An ANSI-string closure which returns any object whose [toString] function will be invoked.
     */
    fun info(message: AnsiScope.() -> Any) = log(LogLevel.INFO, message)

    /**
     * Log a message at the [LogLevel.WARN] level if enabled.
     * Semantically equal to
     * ```kotlin
     * Logger.log(LogLevel.WARN, message)
     * ```
     *
     * @param message An ANSI-string closure which returns any object whose [toString] function will be invoked.
     */
    fun warn(message: AnsiScope.() -> Any) = log(LogLevel.WARN, message)

    /**
     * Log a message at the [LogLevel.ERROR] level if enabled.
     * Semantically equal to
     * ```kotlin
     * Logger.log(LogLevel.ERROR, message)
     * ```
     *
     * @param message An ANSI-string closure which returns any object whose [toString] function will be invoked.
     */
    fun error(message: AnsiScope.() -> Any) = log(LogLevel.ERROR, message)

    /**
     * Log a message at the [LogLevel.FATAL] level if enabled.
     * Semantically equal to
     * ```kotlin
     * Logger.log(LogLevel.FATAL, message)
     * ```
     *
     * @param message An ANSI-string closure which returns any object whose [toString] function will be invoked.
     */
    fun fatal(message: AnsiScope.() -> Any) = log(LogLevel.FATAL, message)

    /**
     * Log a message at the [LogLevel.DEBUG] level and with the given marker if both are enabled.
     * Semantically equal to
     * ```kotlin
     * Logger.log(marker, LogLevel.DEBUG, message)
     * ```
     *
     * @param marker The marker with which to tag the logged message if not null.
     * @param message An ANSI-string closure which returns any object whose [toString] function will be invoked.
     */
    fun debug(marker: LogMarker?, message: AnsiScope.() -> Any) = log(marker, LogLevel.DEBUG, message)

    /**
     * Log a message at the [LogLevel.INFO] level and with the given marker if both are enabled.
     * Semantically equal to
     * ```kotlin
     * Logger.log(marker, LogLevel.INFO, message)
     * ```
     *
     * @param marker The marker with which to tag the logged message if not null.
     * @param message An ANSI-string closure which returns any object whose [toString] function will be invoked.
     */
    fun info(marker: LogMarker?, message: AnsiScope.() -> Any) = log(marker, LogLevel.INFO, message)

    /**
     * Log a message at the [LogLevel.WARN] level and with the given marker if both are enabled.
     * Semantically equal to
     * ```kotlin
     * Logger.log(marker, LogLevel.WARN, message)
     * ```
     *
     * @param marker The marker with which to tag the logged message if not null.
     * @param message An ANSI-string closure which returns any object whose [toString] function will be invoked.
     */
    fun warn(marker: LogMarker?, message: AnsiScope.() -> Any) = log(marker, LogLevel.WARN, message)

    /**
     * Log a message at the [LogLevel.ERROR] level and with the given marker if both are enabled.
     * Semantically equal to
     * ```kotlin
     * Logger.log(marker, LogLevel.ERROR, message)
     * ```
     *
     * @param marker The marker with which to tag the logged message if not null.
     * @param message An ANSI-string closure which returns any object whose [toString] function will be invoked.
     */
    fun error(marker: LogMarker?, message: AnsiScope.() -> Any) = log(marker, LogLevel.ERROR, message)

    /**
     * Log a message at the [LogLevel.FATAL] level and with the given marker if both are enabled.
     * Semantically equal to
     * ```kotlin
     * Logger.log(marker, LogLevel.FATAL, message)
     * ```
     *
     * @param marker The marker with which to tag the logged message if not null.
     * @param message An ANSI-string closure which returns any object whose [toString] function will be invoked.
     */
    fun fatal(marker: LogMarker?, message: AnsiScope.() -> Any) = log(marker, LogLevel.FATAL, message)
}

@PublishedApi
internal class SimpleLogger( // @formatter:off
    override val name: String,
    override val config: LoggerConfig
) : Logger { // @formatter:on
    override var level: LogLevel by atomic(config.initialLevel)
    override var isEnabled: Boolean by atomic(config.initialEnableState)

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