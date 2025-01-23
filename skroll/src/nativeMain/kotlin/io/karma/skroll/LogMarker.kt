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

interface LogMarker {
    companion object {
        inline fun create( // @formatter:off
            key: String,
            name: String = key,
            isEnabled: Boolean = true
        ): LogMarker = SimpleLogMarker(key, name, isEnabled) // @formatter:on
    }

    val key: String
    val name: String
    var isEnabled: Boolean
}

@ConsistentCopyVisibility
@PublishedApi
internal data class SimpleLogMarker @PublishedApi internal constructor( // @formatter:off
    override val key: String,
    override val name: String,
    override var isEnabled: Boolean
) : LogMarker // @formatter:on