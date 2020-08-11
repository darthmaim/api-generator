/*
 * Copyright (c) 2019-2020 Leon Linhart
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
package com.gw2tb.apigen.internal.dsl

import com.gw2tb.apigen.model.*
import com.gw2tb.apigen.model.v2.*
import com.gw2tb.apigen.schema.*
import java.util.*
import kotlin.time.*

@Suppress("FunctionName")
internal fun GW2APIVersion(configure: GW2APIVersionBuilder.() -> Unit): () -> Set<Endpoint> {
    return fun() = GW2APIVersionBuilder().also(configure).endpoints
}

internal class GW2APIVersionBuilder {

    private val _endpoints = mutableListOf<GW2APIEndpointBuilder>()
    val endpoints get() = _endpoints.map { it.endpoint }.toSet()

    operator fun String.invoke(configure: GW2APIEndpointBuilder.() -> Unit) =
        GW2APIEndpointBuilder(this).also(configure).also { _endpoints.add(it) }

    fun array(
        items: SchemaType,
        description: String? = null,
        nullableItems: Boolean = false
    ): SchemaType =
        SchemaArray(items, nullableItems, description)

    fun map(
        keys: SchemaPrimitive,
        values: SchemaType,
        description: String? = null,
        nullableValues: Boolean = false
    ): SchemaType =
        SchemaMap(keys, values, nullableValues, description)

    fun record(
        description: String? = null,
        configure: SchemaRecordBuilder.() -> Unit
    ): SchemaType =
        SchemaRecord(SchemaRecordBuilder().also(configure).properties, description)

    fun conditional(
        disambiguationBy: String = "type",
        disambiguationBySideProperty: Boolean = false,
        sharedConfigure: (SchemaRecordBuilder.() -> Unit)? = null,
        configure: SchemaConditionalBuilder.() -> Unit
    ): SchemaType =
        SchemaConditional(
            disambiguationBy,
            disambiguationBySideProperty,
            sharedConfigure?.let(SchemaRecordBuilder()::also)?.properties ?: emptyMap(),
            SchemaConditionalBuilder().also(configure).interpretations
        )

}

internal class GW2APIEndpointBuilder(private val route: String) {

    val endpoint get() =
        Endpoint(
            route = route,
            summary = summary,
            cache = cache,
            security = security ?: emptySet(),
            isLocalized = isLocalized,
            queryTypes = if (this::queryTypes.isInitialized) queryTypes else emptySet(),
            pathParameters = pathParameters.values.toList(),
            _schema = schema
        )

    private lateinit var schema: EnumMap<V2SchemaVersion, SchemaType>

    lateinit var summary: String

    var cache: Duration? = null
    var security: Set<TokenScope>? = null

    var isLocalized: Boolean = false

    private lateinit var queryTypes: Set<QueryType>
    private val pathParameters: MutableMap<String, PathParameter> = mutableMapOf()

    fun security(vararg required: TokenScope) { security = required.toSet() }

    fun schema(schema: SchemaType) {
        val tmp = V2SchemaVersion.values().toList().associateWithTo(EnumMap(V2SchemaVersion::class.java)) { version ->
            fun SchemaType.copyOrGet(superIncluded: Boolean = false): SchemaType? {
                return when (this) {
                    is SchemaRecord -> {
                        fun SchemaType.hasChangedInVersion(): Boolean = when (this) {
                            is SchemaRecord -> properties.any { (_, property) ->
                                property.since === version || property.until === V2SchemaVersion.values()[version.ordinal - 1] || property.type.hasChangedInVersion()
                            }
                            else -> false
                        }

                        val includeVersion = version === V2SchemaVersion.V2_SCHEMA_CLASSIC || hasChangedInVersion()
                        val copiedProperties = properties.mapValues { (_, property) ->
                            val includedSinceVersion = property.since?.let { it === version } ?: false
                            val includedInVersion = ((includedSinceVersion || property.since?.let { version >= it } ?: true)) && (property.until?.let { it < version } ?: true)

                            when {
                                includedInVersion && (includeVersion || includedSinceVersion || superIncluded) -> property.copy(type = property.type.copyOrGet(superIncluded = true)!!)
                                else -> null
                            }
                        }.filterValues { it !== null }.mapValues { it.value!! }

                        return when {
                            !superIncluded && copiedProperties.isEmpty() -> null
                            else -> SchemaRecord(copiedProperties, description)
                        }
                    }
                    else -> this
                }
            }

            schema.copyOrGet()
        }

        this.schema = EnumMap<V2SchemaVersion, SchemaType>(V2SchemaVersion::class.java).also { map ->
            tmp.forEach { (key, value) ->
                // TODO the compiler trips here without casts. Try this with NI once 1.4 is out
                if (value !== null) map[key as V2SchemaVersion] = value as SchemaType
            }
        }
    }

    fun schema(vararg schemas: Pair<V2SchemaVersion, SchemaType>) {
        this.schema = EnumMap<V2SchemaVersion, SchemaType>(V2SchemaVersion::class.java).also { map ->
            schemas.forEach { map[it.first] = it.second }
        }
    }

    fun supportedQueries(vararg types: QueryType) {
        check(!this::queryTypes.isInitialized)
        queryTypes = types.toSet()
    }

    fun pathParameter(key: String, type: SchemaPrimitive, description: String, name: String = key) {
        check(":$key" in (route.split('/')))
        check(key !in pathParameters)

        pathParameters[key] = PathParameter(key, type, description, name)
    }

}