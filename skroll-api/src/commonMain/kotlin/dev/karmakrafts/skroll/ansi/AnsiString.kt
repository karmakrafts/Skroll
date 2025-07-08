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

@JvmInline
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
    @Suppress("NOTHING_TO_INLINE")
    inline operator fun plus(s: String): AnsiString = AnsiString("$value$s")

    /**
     * Concatenate the value of this ANSI string with the given ANSI string
     * and returns the result as a new [AnsiString].
     *
     * @param s The ANSI string to append to this ANSI string.
     * @return A new [AnsiString] which contains the data of this ANSI string followed by the given ANSI string.
     */
    @Suppress("NOTHING_TO_INLINE")
    inline operator fun plus(s: AnsiString): AnsiString = AnsiString("$value$s")

    /**
     * Insert the given ANSI sequence before this ANSI string and return
     * the newly created [AnsiString].
     *
     * @param sequence The ANSI sequence to insert before this ANSI string.
     * @return A new [AnsiString] containing the given sequence followed by the data of this ANSI string.
     */
    @Suppress("NOTHING_TO_INLINE")
    inline infix fun with(sequence: AnsiSequence): AnsiString = AnsiString("$sequence$value")

    /**
     * Insert a reset ANSI escape code after this ANSI string and return
     * the newly created ANSI string.
     *
     * @return A new ANSI string containing the data of this ANSI string followed by a reset escape code.
     */
    @Suppress("NOTHING_TO_INLINE")
    inline fun reset(): AnsiString = AnsiString("$value${AnsiSequence.reset}")

    override fun toString(): String = value
}

/**
 * Convert this string into an ANSI string.
 *
 * @return A new [AnsiString] instance containing the data of this string.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun String.toAnsi(): AnsiString = AnsiString(this)