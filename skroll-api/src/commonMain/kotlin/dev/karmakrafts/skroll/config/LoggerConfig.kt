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

package dev.karmakrafts.skroll.config

import dev.karmakrafts.skroll.LogLevel
import dev.karmakrafts.skroll.appender.LogAppender

/**
 * The immutable configuration of a given [dev.karmakrafts.skroll.Logger] instance.
 * Mainly used for storing references to all appenders used by the logger.
 */
@ConsistentCopyVisibility
data class LoggerConfig internal constructor( // @formatter:off
    val initialLevel: LogLevel = LogLevel.default(),
    val initialEnableState: Boolean = true,
    val appenders: List<LogAppender> = emptyList()
) // @formatter:on