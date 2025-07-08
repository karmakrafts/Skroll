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

object AnsiScope {
    /**
     * Insert the given ANSI sequence before this string and return
     * the newly created [AnsiString].
     *
     * @param sequence The ANSI sequence to insert before this string.
     * @return A new [AnsiString] containing the given sequence followed by the data of this string.
     */
    @Suppress("NOTHING_TO_INLINE")
    inline infix fun String.with(sequence: AnsiSequence): AnsiString = AnsiString("$sequence$this")

    /**
     * Insert a reset ANSI escape code after this string and return
     * the newly created string as an [AnsiString].
     *
     * @return A new [AnsiString] containing the data of this string followed by a reset escape code.
     */
    @Suppress("NOTHING_TO_INLINE")
    inline fun String.reset(): AnsiString = AnsiString("$this${AnsiSequence.reset}")
}