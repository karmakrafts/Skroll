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

package dev.karmakrafts.skroll.ansi

import kotlin.jvm.JvmInline

/**
 * A list of all available ANSI foreground colors which may be used in conjunction with a regular terminal.
 */
@JvmInline
value class AnsiFg @PublishedApi internal constructor(@PublishedApi internal val value: Int) {
    companion object {
        // @formatter:off
        val default: AnsiFg  = AnsiFg(39)

        val black: AnsiFg    = AnsiFg(30)
        val red: AnsiFg      = AnsiFg(31)
        val green: AnsiFg    = AnsiFg(32)
        val yellow: AnsiFg   = AnsiFg(33)
        val blue: AnsiFg     = AnsiFg(34)
        val purple: AnsiFg   = AnsiFg(35)
        val cyan: AnsiFg     = AnsiFg(36)
        val white: AnsiFg    = AnsiFg(37)

        val hiBlack: AnsiFg  = AnsiFg(90)
        val hiRed: AnsiFg    = AnsiFg(91)
        val hiGreen: AnsiFg  = AnsiFg(92)
        val hiYellow: AnsiFg = AnsiFg(93)
        val hiBlue: AnsiFg   = AnsiFg(94)
        val hiPurple: AnsiFg = AnsiFg(95)
        val hiCyan: AnsiFg   = AnsiFg(96)
        val hiWhite: AnsiFg  = AnsiFg(97)
        // @formatter:on
    }

    override fun toString(): String = value.toString()
}