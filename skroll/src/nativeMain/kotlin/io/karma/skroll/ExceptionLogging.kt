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

inline fun Logger.debug(throwable: Throwable?, marker: LogMarker? = null, crossinline message: AnsiScope.() -> Any) {
    debug(marker) { "${message()}: ${throwable?.stackTraceToString() ?: "Stacktrace unavailable"}" }
}

inline fun Logger.info(throwable: Throwable?, marker: LogMarker? = null, crossinline message: AnsiScope.() -> Any) {
    info(marker) { "${message()}: ${throwable?.stackTraceToString() ?: "Stacktrace unavailable"}" }
}

inline fun Logger.warn(throwable: Throwable?, marker: LogMarker? = null, crossinline message: AnsiScope.() -> Any) {
    warn(marker) { "${message()}: ${throwable?.stackTraceToString() ?: "Stacktrace unavailable"}" }
}

inline fun Logger.error(throwable: Throwable?, marker: LogMarker? = null, crossinline message: AnsiScope.() -> Any) {
    error(marker) { "${message()}: ${throwable?.stackTraceToString() ?: "Stacktrace unavailable"}" }
}

inline fun Logger.fatal(throwable: Throwable?, marker: LogMarker? = null, crossinline message: AnsiScope.() -> Any) {
    fatal(marker) { "${message()}: ${throwable?.stackTraceToString() ?: "Stacktrace unavailable"}" }
}