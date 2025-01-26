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

/**
 * Represents one or more ANSI escape codes.
 */
value class AnsiSequence @PublishedApi internal constructor(@PublishedApi internal val value: String) {
    companion object {
        const val ESC: Char = '\u001b'
        val reset: AnsiSequence = AnsiSequence("$ESC[0m")
    }

    /**
     * Concatenate this ANSI sequence with the given sequence.
     *
     * @param other The sequence to concatenate this sequence with.
     * @return A new ANSI sequence containing all data from this sequence followed
     *  by the data of the other sequence.
     */
    inline operator fun plus(other: AnsiSequence): AnsiSequence = AnsiSequence("$value$other")

    override fun toString(): String = value
}

/**
 * A list of all available ANSI foreground colors which may be used in conjunction with a regular terminal.
 */
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

/**
 * A list of all available ANSI background colors which may be used in conjunction with a regular terminal.
 */
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
    inline operator fun rangeTo(color: AnsiFg): AnsiSequence =
        AnsiSequence("${AnsiMod.default(this)}${AnsiMod.default(color)}")

    /**
     * Creates an ANSI sequence where this color will be used
     * as the background and the right hand side color will be the foreground and bold.
     *
     * @param color The foreground color to be used on this background.
     * @return A new ANSI sequence containing both colors.
     */
    inline operator fun rangeUntil(color: AnsiFg): AnsiSequence =
        AnsiSequence("${AnsiMod.default(this)}${AnsiMod.bold(color)}")

    override fun toString(): String = value.toString()
}

/**
 * A list of all commonly available ANSI modifiers.
 */
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
    inline operator fun invoke(color: AnsiFg): AnsiSequence = AnsiSequence("${AnsiSequence.ESC}[$value;${color}m")

    /**
     * Turn the given background colors into a new [AnsiSequence] using this modifier.
     *
     * @param color The background color to join with this modifier.
     * @return A new ANSI sequence containing a new escape code for this modifier and the given color.
     */
    inline operator fun invoke(color: AnsiBg): AnsiSequence = AnsiSequence("${AnsiSequence.ESC}[$value;${color}m")

    override fun toString(): String = value.toString()
}

value class AnsiString @PublishedApi internal constructor(@PublishedApi internal val value: String) {
    companion object {
        private val pattern: Regex = Regex("""${AnsiSequence.ESC}\[[\w;]+?[ABCDEFGHIJKLm]""")
    }

    /**
     * Strips all ANSI escape codes from this ANSI string
     * using a RegEx pattern and returns the stripped string value.
     *
     * @return The raw string value of this ANSI string without any escape codes.
     */
    fun cleanString(): String = value.replace(pattern, "")

    /**
     * Concatenate the value of this ANSI string with the given string
     * and returns the result as a new [AnsiString].
     *
     * @param s The string to append to this ANSI string.
     * @return A new [AnsiString] which contains the data of this ANSI string followed by the given string.
     */
    inline operator fun plus(s: String): AnsiString = AnsiString("$value$s")

    /**
     * Concatenate the value of this ANSI string with the given ANSI string
     * and returns the result as a new [AnsiString].
     *
     * @param s The ANSI string to append to this ANSI string.
     * @return A new [AnsiString] which contains the data of this ANSI string followed by the given ANSI string.
     */
    inline operator fun plus(s: AnsiString): AnsiString = AnsiString("$value$s")

    /**
     * Insert the given ANSI sequence before this ANSI string and return
     * the newly created [AnsiString].
     *
     * @param sequence The ANSI sequence to insert before this ANSI string.
     * @return A new [AnsiString] containing the given sequence followed by the data of this ANSI string.
     */
    inline infix fun with(sequence: AnsiSequence): AnsiString = AnsiString("$sequence$value")

    /**
     * Insert a reset ANSI escape code after this ANSI string and return
     * the newly created ANSI string.
     *
     * @return A new ANSI string containing the data of this ANSI string followed by a reset escape code.
     */
    inline fun reset(): AnsiString = AnsiString("$value${AnsiSequence.reset}")

    override fun toString(): String = value
}

/**
 * Convert this string into an ANSI string.
 *
 * @return A new [AnsiString] instance containing the data of this string.
 */
inline fun String.toAnsi(): AnsiString = AnsiString(this)

object AnsiScope {
    /**
     * Insert the given ANSI sequence before this string and return
     * the newly created [AnsiString].
     *
     * @param sequence The ANSI sequence to insert before this string.
     * @return A new [AnsiString] containing the given sequence followed by the data of this string.
     */
    inline infix fun String.with(sequence: AnsiSequence): AnsiString = AnsiString("$sequence$this")

    /**
     * Insert a reset ANSI escape code after this string and return
     * the newly created string as an [AnsiString].
     *
     * @return A new [AnsiString] containing the data of this string followed by a reset escape code.
     */
    inline fun String.reset(): AnsiString = AnsiString("$this${AnsiSequence.reset}")
}
