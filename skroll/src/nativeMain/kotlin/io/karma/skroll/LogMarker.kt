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

import co.touchlab.stately.collections.SharedHashMap

/**
 * Represents a log marker to tag messages with to allow
 * operations like filtering of the messages.
 */
interface LogMarker {
    companion object {
        @PublishedApi
        internal val markers: SharedHashMap<String, LogMarker> = SharedHashMap()

        /**
         * Create a default log marker which is enabled by default.
         *
         * @param key The key the marker is identified by.
         * @param name The name of the marker that is actually printed when {{marker}} is used.
         * @param isEnabled When true means that all messages with this marker will be logged.
         * @return A new log marker instance with the given key, name and state.
         */
        inline fun create(
            key: String, name: String = key, isEnabled: Boolean = true
        ): LogMarker = markers.getOrPut(key) {
            SimpleLogMarker(key, name, isEnabled)
        }
    }

    /**
     * The internal key the marker is referenced by in cache.
     */
    val key: String

    /**
     * The name of the marker that is printed when {{marker}} is used.
     */
    val name: String

    /**
     * When true means that all messages with this marker will be logged.
     */
    var isEnabled: Boolean
}

@ConsistentCopyVisibility
@PublishedApi
internal data class SimpleLogMarker @PublishedApi internal constructor( // @formatter:off
    override val key: String,
    override val name: String,
    override var isEnabled: Boolean
) : LogMarker // @formatter:on