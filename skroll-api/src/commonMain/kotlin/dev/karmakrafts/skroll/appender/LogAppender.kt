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

package dev.karmakrafts.skroll.appender

import dev.karmakrafts.skroll.LogLevel
import dev.karmakrafts.skroll.LogMarker
import dev.karmakrafts.skroll.Logger
import dev.karmakrafts.skroll.format.LogFormatter

/**
 * Interface representing a log appender, which is responsible for outputting log messages
 * to a specific destination (e.g., console, file, network, etc.).
 *
 * Appenders receive formatted log messages and write them to their respective output targets.
 * Each appender has a formatter that controls how messages are formatted before being appended,
 * and typically works with a filter to determine which messages should be processed.
 */
interface LogAppender {
    /**
     * The formatter used by this appender to transform log messages according to a pattern.
     * The formatter applies the pattern to the raw message content before it is appended.
     */
    val formatter: LogFormatter

    /**
     * The formatting pattern string used by this appender.
     * This pattern defines how log messages will be structured when output to the destination.
     * The pattern is processed by the formatter to produce the final formatted message.
     */
    val pattern: String

    /**
     * Appends a log message to the appender's destination.
     *
     * @param logger The logger instance that generated this log message.
     * @param level The log level of the message.
     * @param message The formatted message content to be appended.
     * @param marker An optional marker that can be used for additional filtering or processing.
     */
    fun append(logger: Logger, level: LogLevel, message: String, marker: LogMarker?)

    /**
     * A function which may be explicitly invoked to free all underlying
     * resources associated with this log appender instance.
     * If not called explicitly, will be called automatically by the parent logger
     * at the end of the program lifecycle.
     */
    fun dispose() {}
}
