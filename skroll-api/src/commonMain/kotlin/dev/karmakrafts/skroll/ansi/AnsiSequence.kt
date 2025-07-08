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
 * Represents one or more ANSI escape codes.
 */
@JvmInline
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
    @Suppress("NOTHING_TO_INLINE")
    inline operator fun plus(other: AnsiSequence): AnsiSequence = AnsiSequence("$value$other")

    override fun toString(): String = value
}