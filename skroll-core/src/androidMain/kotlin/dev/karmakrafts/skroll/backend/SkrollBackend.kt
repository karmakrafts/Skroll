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
import dev.karmakrafts.skroll.appender.LogAppender
import dev.karmakrafts.skroll.appender.LogFilter
import dev.karmakrafts.skroll.appender.LogcatAppender
import dev.karmakrafts.skroll.format.LogFormatter

internal actual fun createSystemLogAppender( // @formatter:off
    pattern: String,
    formatter: LogFormatter,
    filter: LogFilter
): LogAppender { // @formatter:on
    return LogcatAppender(pattern, formatter, filter)
}

internal actual fun getDefaultLogLevel(): LogLevel {
    return System.getProperty("skroll.default.level")?.let { levelName ->
        LogLevel.entries.find { it.name == levelName }
    } ?: LogLevel.INFO
}