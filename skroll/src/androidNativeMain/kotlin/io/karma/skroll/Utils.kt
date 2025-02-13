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

import platform.android.ANDROID_LOG_DEBUG
import platform.android.ANDROID_LOG_ERROR
import platform.android.ANDROID_LOG_FATAL
import platform.android.ANDROID_LOG_INFO
import platform.android.ANDROID_LOG_VERBOSE
import platform.android.ANDROID_LOG_WARN

val LogLevel.logcatLevel: Int
    get() = when (this) {
        LogLevel.TRACE -> ANDROID_LOG_VERBOSE
        LogLevel.DEBUG -> ANDROID_LOG_DEBUG
        LogLevel.WARN -> ANDROID_LOG_WARN
        LogLevel.ERROR -> ANDROID_LOG_ERROR
        LogLevel.FATAL -> ANDROID_LOG_FATAL
        LogLevel.INFO -> ANDROID_LOG_INFO
    }.toInt()