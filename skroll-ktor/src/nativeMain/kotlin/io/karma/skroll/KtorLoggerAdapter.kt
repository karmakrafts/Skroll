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

import io.ktor.util.logging.LogLevel as KtorLogLevel
import io.ktor.util.logging.Logger as KtorLogger

inline val LogLevel.ktorLevel: KtorLogLevel
    get() = when (this) {
        LogLevel.TRACE -> KtorLogLevel.TRACE
        LogLevel.DEBUG -> KtorLogLevel.DEBUG
        LogLevel.INFO -> KtorLogLevel.INFO
        LogLevel.WARN -> KtorLogLevel.WARN
        LogLevel.ERROR, LogLevel.FATAL -> KtorLogLevel.ERROR
    }

@PublishedApi
internal value class KtorLoggerAdapter(
    private val delegate: Logger
) : KtorLogger {
    override fun debug(message: String) = delegate.debug { message }

    override fun debug(message: String, cause: Throwable) = delegate.debug(cause) { message }

    override fun error(message: String) = delegate.error { message }

    override fun error(message: String, cause: Throwable) = delegate.error(cause) { message }

    override fun info(message: String) = delegate.info { message }

    override fun info(message: String, cause: Throwable) = delegate.info(cause) { message }

    override fun trace(message: String) = delegate.trace { message }

    override fun trace(message: String, cause: Throwable) = delegate.trace(cause) { message }

    override fun warn(message: String) = delegate.warn { message }

    override fun warn(message: String, cause: Throwable) = delegate.warn(cause) { message }

    override val level: KtorLogLevel
        get() = delegate.level.ktorLevel
}

@Suppress("NOTHING_TO_INLINE")
inline fun Logger.asKtorLogger(): KtorLogger = KtorLoggerAdapter(this)