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

import kotlin.experimental.ExperimentalNativeApi

/**
 * The log level designates the importance of a logged message in
 * ascending order.
 */
enum class LogLevel( // @formatter:off
    val symbol: String,
    val ansi: AnsiSequence
) { // @formatter:on
    // @formatter:off
    TRACE("\uD83D\uDD0E", AnsiBg.default..AnsiFg.hiPurple),
    DEBUG("\uD83E\uDEB2", AnsiBg.default..AnsiFg.hiGreen),
    INFO ("\uD83D\uDCDC", AnsiBg.default..AnsiFg.default),
    WARN ("\u26A0\uFE0F", AnsiBg.default..AnsiFg.hiYellow),
    ERROR("\uD83D\uDD25", AnsiBg.default..AnsiFg.hiRed),
    FATAL("\uD83D\uDC80", AnsiBg.default..<AnsiFg.hiRed);
    // @formatter:on

    companion object {
        /**
         * The default log level is determined by [Platform.isDebugBinary];
         * When the application is linked as a debug build, [LogLevel.DEBUG]
         * will be returned, otherwise [LogLevel.INFO] is returned.
         *
         * @return [LogLevel.DEBUG] if [Platform.isDebugBinary] is true, [LogLevel.INFO] otherwise.
         */
        @OptIn(ExperimentalNativeApi::class)
        inline fun default(): LogLevel = if (Platform.isDebugBinary) DEBUG else INFO
    }
}
