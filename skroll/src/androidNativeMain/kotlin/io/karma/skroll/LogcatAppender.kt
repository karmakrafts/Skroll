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

import platform.android.__android_log_write

internal class LogcatAppender( // @formatter:off
    override val pattern: String,
    override val formatter: LogFormatter,
    private val filter: LogFilter
) : LogAppender { // @formatter:on
    override fun append(logger: Logger, level: LogLevel, message: String, marker: LogMarker?) {
        if (!filter(level, message, marker)) return
        __android_log_write(level.logcatLevel, marker?.name, message.toAnsi().cleanString())
    }

    override fun close() {}
}