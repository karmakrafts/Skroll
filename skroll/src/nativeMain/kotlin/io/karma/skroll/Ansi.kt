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

/**
 * See https://en.wikipedia.org/wiki/ANSI_escape_code
 */

value class AnsiSequence @PublishedApi internal constructor(@PublishedApi internal val value: String) {
    companion object {
        const val ESC: Char = '\u001b'
        val reset: AnsiSequence = AnsiSequence("$ESC[0m")
    }

    inline operator fun plus(other: AnsiSequence): AnsiSequence = AnsiSequence("$value$other")

    override fun toString(): String = value
}

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

    inline operator fun rangeTo(color: AnsiFg): AnsiSequence =
        AnsiSequence("${AnsiMod.default(this)}${AnsiMod.default(color)}")

    inline operator fun rangeUntil(color: AnsiFg): AnsiSequence =
        AnsiSequence("${AnsiMod.default(this)}${AnsiMod.bold(color)}")

    override fun toString(): String = value.toString()
}

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

    inline operator fun invoke(color: AnsiFg): AnsiSequence = AnsiSequence("${AnsiSequence.ESC}[$value;${color}m")

    inline operator fun invoke(color: AnsiBg): AnsiSequence = AnsiSequence("${AnsiSequence.ESC}[$value;${color}m")

    override fun toString(): String = value.toString()
}

value class AnsiString @PublishedApi internal constructor(@PublishedApi internal val value: String) {
    companion object {
        private val pattern: Regex = Regex("""${AnsiSequence.ESC}\[[\w;]+?[ABCDEFGHIJKLm]""")
    }

    fun cleanString(): String = value.replace(pattern, "")

    inline operator fun plus(s: String): AnsiString = AnsiString("$value$s")

    inline operator fun plus(s: AnsiString): AnsiString = AnsiString("$value$s")

    inline infix fun with(sequence: AnsiSequence): AnsiString = AnsiString("$sequence$value")

    inline infix fun withBg(color: AnsiBg): AnsiString = AnsiString("${AnsiMod.default(color)}$value")

    inline infix fun withFg(color: AnsiFg): AnsiString = AnsiString("${AnsiMod.default(color)}$value")

    inline infix fun withBoldFg(color: AnsiFg): AnsiString = AnsiString("${AnsiMod.bold(color)}$value")

    inline infix fun withItalicFg(color: AnsiFg): AnsiString = AnsiString("${AnsiMod.bold(color)}$value")

    inline fun reset(): AnsiString = AnsiString("$value${AnsiSequence.reset}")

    override fun toString(): String = value
}

inline fun String.toAnsi(): AnsiString = AnsiString(this)

object AnsiScope {
    inline infix fun String.with(sequence: AnsiSequence): AnsiString = AnsiString("$sequence$this")

    inline infix fun String.withBg(color: AnsiBg): AnsiString = AnsiString("${AnsiMod.default(color)}$this")

    inline infix fun String.withFg(color: AnsiFg): AnsiString = AnsiString("${AnsiMod.default(color)}$this")

    inline infix fun String.withBoldFg(color: AnsiFg): AnsiString = AnsiString("${AnsiMod.bold(color)}$this")

    inline infix fun String.withItalicFg(color: AnsiFg): AnsiString = AnsiString("${AnsiMod.bold(color)}$this")

    inline fun String.reset(): AnsiString = AnsiString("$this${AnsiSequence.reset}")
}
