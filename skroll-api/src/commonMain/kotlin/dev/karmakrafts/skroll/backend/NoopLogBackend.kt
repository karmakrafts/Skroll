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

package dev.karmakrafts.skroll.backend

import dev.karmakrafts.skroll.LogLevel
import dev.karmakrafts.skroll.LogMarker
import dev.karmakrafts.skroll.Logger
import dev.karmakrafts.skroll.ansi.AnsiScope
import dev.karmakrafts.skroll.appender.LogAppender
import dev.karmakrafts.skroll.appender.LogFilter
import dev.karmakrafts.skroll.config.LoggerConfig
import dev.karmakrafts.skroll.config.LoggerConfigBuilder
import dev.karmakrafts.skroll.format.LogFormatter
import kotlinx.io.files.Path

private object NoopMarker : LogMarker {
    override val key: String = ""
    override val name: String = ""

    override var isEnabled: Boolean
        get() = false
        set(value) {}
}

private object NoopLogger : Logger {
    override val name: String = ""
    override val config: LoggerConfig = LoggerConfig()

    override var level: LogLevel
        get() = LogLevel.INFO
        set(value) {}

    override var isEnabled: Boolean
        get() = false
        set(value) {}

    override fun log(level: LogLevel, message: AnsiScope.() -> Any) = Unit
    override fun log(marker: LogMarker?, level: LogLevel, message: AnsiScope.() -> Any) = Unit
}

private object NoopAppender : LogAppender {
    override val formatter: LogFormatter = LogFormatter.identity
    override val pattern: String = ""

    override fun append(logger: Logger, level: LogLevel, message: String, marker: LogMarker?) = Unit
}

/**
 * A no-operation implementation of the [LogBackend] interface.
 * This implementation doesn't perform any actual logging operations and is useful
 * for situations where logging should be completely disabled or as a default
 * implementation when no other backend is configured.
 */
object NoopLogBackend : LogBackend {
    override val name: String = "NOOP"
    override val defaultLogLevel: LogLevel = LogLevel.WARN
    override val defaultFormatter: LogFormatter = LogFormatter.identity

    override var defaultConfigSpec: LoggerConfigBuilder.() -> Unit
        get() = {}
        set(value) {}

    override fun createMarker(key: String, name: String, isEnabled: Boolean): LogMarker = NoopMarker

    override fun createLogger(name: String, configSpec: LoggerConfigBuilder.() -> Unit): Logger = NoopLogger

    override fun createFileAppender(
        pattern: String, formatter: LogFormatter, filter: LogFilter, path: Path
    ): LogAppender = NoopAppender

    override fun createConsoleAppender(
        pattern: String, formatter: LogFormatter, filter: LogFilter
    ): LogAppender = NoopAppender

    override fun createSystemAppender(
        pattern: String, formatter: LogFormatter, filter: LogFilter
    ): LogAppender = NoopAppender
}
