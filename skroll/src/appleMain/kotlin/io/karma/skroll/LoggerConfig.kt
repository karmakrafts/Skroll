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

/**
 * Adds a new os_log console appender to this logger config.
 *
 * @param pattern The formatting pattern to apply to all messages passed to the new appender.
 * @param formatter The formatter used to apply the specified pattern to each message. See [LogFormatter].
 * @param filter The filter to apply for every message to determine whether it should be logged.
 */
fun LoggerConfigBuilder.osLogAppender( // @formatter:off
    pattern: String,
    formatter: LogFormatter = LogFormatter.plain,
    filter: LogFilter = LogFilter.always
) { // @formatter:on
    appenders += OsLogAppender(pattern, formatter, filter)
}

actual fun LoggerConfigBuilder.platformConsoleAppender(
    pattern: String,
    formatter: LogFormatter,
    filter: LogFilter
) = osLogAppender(pattern, formatter, filter)