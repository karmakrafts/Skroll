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

package dev.karmakrafts.skroll

import dev.karmakrafts.skroll.ansi.AnsiScope
import dev.karmakrafts.skroll.config.LoggerConfig
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
@PublishedApi
internal class SkrollLogger( // @formatter:off
    override val name: String,
    override val config: LoggerConfig
) : Logger { // @formatter:on
    private val _level: AtomicInt = AtomicInt(config.initialLevel.ordinal)
    override var level: LogLevel
        get() = LogLevel.entries[_level.load()]
        set(value) { _level.store(value.ordinal) }

    private val _isEnabled: AtomicBoolean = AtomicBoolean(config.initialEnableState)
    override var isEnabled: Boolean
        get() = _isEnabled.load()
        set(value) { _isEnabled.store(value) }

    override fun log(level: LogLevel, message: AnsiScope.() -> Any) {
        if (level < this.level) return
        val messageContent = message(AnsiScope)
        for (appender in config.appenders) {
            appender.append(
                this, level, appender.formatter.transform(this, level, messageContent, null, appender.pattern), null
            )
        }
    }

    override fun log(marker: LogMarker?, level: LogLevel, message: AnsiScope.() -> Any) {
        if (level < this.level || marker?.isEnabled == false) return
        val messageContent = message(AnsiScope)
        for (appender in config.appenders) {
            appender.append(
                this, level, appender.formatter.transform(this, level, messageContent, marker, appender.pattern), marker
            )
        }
    }
}