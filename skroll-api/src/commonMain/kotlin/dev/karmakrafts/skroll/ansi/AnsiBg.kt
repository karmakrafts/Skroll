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
 * A list of all available ANSI background colors which may be used in conjunction with a regular terminal.
 */
@JvmInline
value class AnsiBg @PublishedApi internal constructor(@PublishedApi internal val value: Int) {
    companion object {
        // @formatter:off
        val default: AnsiBg  = AnsiBg(49)

        val black: AnsiBg    = AnsiBg(40)
        val red: AnsiBg      = AnsiBg(41)
        val green: AnsiBg    = AnsiBg(42)
        val yellow: AnsiBg   = AnsiBg(43)
        val blue: AnsiBg     = AnsiBg(44)
        val purple: AnsiBg   = AnsiBg(45)
        val cyan: AnsiBg     = AnsiBg(46)
        val white: AnsiBg    = AnsiBg(47)

        val hiBlack: AnsiBg  = AnsiBg(100)
        val hiRed: AnsiBg    = AnsiBg(101)
        val hiGreen: AnsiBg  = AnsiBg(102)
        val hiYellow: AnsiBg = AnsiBg(103)
        val hiBlue: AnsiBg   = AnsiBg(104)
        val hiPurple: AnsiBg = AnsiBg(105)
        val hiCyan: AnsiBg   = AnsiBg(106)
        val hiWhite: AnsiBg  = AnsiBg(107)
        // @formatter:on
    }

    /**
     * Creates an ANSI sequence where this color will be used
     * as the background and the right hand side color will be the foreground.
     *
     * @param color The foreground color to be used on this background.
     * @return A new ANSI sequence containing both colors.
     */
    @Suppress("NOTHING_TO_INLINE")
    inline operator fun rangeTo(color: AnsiFg): AnsiSequence =
        AnsiSequence("${AnsiMod.default(this)}${AnsiMod.default(color)}")

    /**
     * Creates an ANSI sequence where this color will be used
     * as the background and the right hand side color will be the foreground and bold.
     *
     * @param color The foreground color to be used on this background.
     * @return A new ANSI sequence containing both colors.
     */
    @Suppress("NOTHING_TO_INLINE")
    inline operator fun rangeUntil(color: AnsiFg): AnsiSequence =
        AnsiSequence("${AnsiMod.default(this)}${AnsiMod.bold(color)}")

    override fun toString(): String = value.toString()
}