/*
 * Copyright (c) 2019 Leon Linhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.gw2toolbelt.apigen.model

import com.github.gw2toolbelt.apigen.schema.*
import java.util.*

@ExperimentalUnsignedTypes
data class Endpoint(
    val summary: String,
    val cache: Duration?,
    val security: Set<TokenScope>,
    val isLocalized: Boolean,
    private val _schema: EnumMap<V2SchemaVersion, SchemaType>
) {

    val schema get() = _schema[V2SchemaVersion.V2_SCHEMA_CLASSIC]
    val versions get() = _schema.keys.toSet()

    operator fun get(version: V2SchemaVersion): Pair<V2SchemaVersion, SchemaType> {
        return _schema.entries.sortedByDescending { it.key }.first { it.key <= version }.toPair()
    }

}