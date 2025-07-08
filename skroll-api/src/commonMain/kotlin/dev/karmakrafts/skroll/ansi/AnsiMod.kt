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
 * A list of all commonly available ANSI modifiers.
 */
@JvmInline
value class AnsiMod @PublishedApi internal constructor(@PublishedApi internal val value: Int) {
    companion object { // @formatter:off
        val default: AnsiMod    = AnsiMod(0)
        val bold: AnsiMod       = AnsiMod(1)
        val faint: AnsiMod      = AnsiMod(2)
        val italic: AnsiMod     = AnsiMod(3)
        val underline: AnsiMod  = AnsiMod(4)
        val slowBlink: AnsiMod  = AnsiMod(5)
        val rapidBlink: AnsiMod = AnsiMod(6)
        val invert: AnsiMod     = AnsiMod(7)
    } // @formatter:on

    /**
     * Turn the given foreground colors into a new [AnsiSequence] using this modifier.
     *
     * @param color The foreground color to join with this modifier.
     * @return A new ANSI sequence containing a new escape code for this modifier and the given color.
     */
    @Suppress("NOTHING_TO_INLINE")
    inline operator fun invoke(color: AnsiFg): AnsiSequence = AnsiSequence("${AnsiSequence.ESC}[$value;${color}m")

    /**
     * Turn the given background colors into a new [AnsiSequence] using this modifier.
     *
     * @param color The background color to join with this modifier.
     * @return A new ANSI sequence containing a new escape code for this modifier and the given color.
     */
    @Suppress("NOTHING_TO_INLINE")
    inline operator fun invoke(color: AnsiBg): AnsiSequence = AnsiSequence("${AnsiSequence.ESC}[$value;${color}m")

    override fun toString(): String = value.toString()
}