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
@file:Suppress("FunctionName")
package com.gw2tb.apigen.internal.dsl

import com.gw2tb.apigen.model.*
import com.gw2tb.apigen.model.v2.*
import com.gw2tb.apigen.schema.*

/** Alias for [SchemaBoolean]. */
internal val BOOLEAN get() = SchemaBoolean

/** Alias for [SchemaDecimal]. */
internal val DECIMAL get() = SchemaDecimal

/** Alias for [SchemaInteger]. */
internal val INTEGER get() = SchemaInteger

/** Alias for [SchemaString]. */
internal val STRING get() = SchemaString

/** Alias for [QueryType.ByID]. */
internal val BY_ID get() = QueryType.ByID

/** Alias for [QueryType.ByPage]. */
internal val BY_PAGE get() = QueryType.ByPage

/** Alias for [QueryType.ByIDs]. */
internal val BY_IDS = QueryType.ByIDs(supportsAll = true)

/** Alias for [QueryType.ByIDs]. */
internal fun BY_IDS(all: Boolean = true) = QueryType.ByIDs(supportsAll = all)

@APIGenDSL
internal class SchemaConditionalBuilder : SchemaBuilder {

    private val _interpretations = mutableMapOf<String, SchemaConditionalInterpretationBuilder>()
    val interpretations get() = _interpretations.mapValues { it.value.interpretation }

    /**
     * Registers a conditional interpretation using the @receiver as key.
     *
     * The name should be in _TitleCase_.
     *
     * @param type  the type of the interpretation
     */
    @APIGenDSL
    operator fun String.invoke(type: SchemaType): SchemaConditionalInterpretationBuilder {
        return SchemaConditionalInterpretationBuilder(this, type).also { _interpretations[this] = it }
    }

    /** Marks a deprecated interpretation. */
    val deprecated = Modifiers.deprecated

    /** The minimal [V2SchemaVersion] (inclusive) required for the interpretation. */
    fun since(version: V2SchemaVersion): IInterpretationModifier = object : IInterpretationModifier {
        override fun applyTo(interpretation: SchemaConditionalInterpretationBuilder) {
            interpretation.since = version
        }
    }

    /** The maximum [V2SchemaVersion] (exclusive) required for the interpretation. */
    fun until(version: V2SchemaVersion): IInterpretationModifier = object : IInterpretationModifier {
        override fun applyTo(interpretation: SchemaConditionalInterpretationBuilder) {
            interpretation.until = version
        }
    }

    operator fun IInterpretationModifier.rangeTo(modifier: IInterpretationModifier): Set<IInterpretationModifier> = setOf(this, modifier)
    operator fun Set<IInterpretationModifier>.rangeTo(modifier: IInterpretationModifier): Set<IInterpretationModifier> = setOf(modifier, *this.toTypedArray())

    operator fun IInterpretationModifier.rangeTo(interpretation: SchemaConditionalInterpretationBuilder): SchemaConditionalInterpretationBuilder = interpretation.also { this.applyTo(it) }
    operator fun Set<IInterpretationModifier>.rangeTo(interpretation: SchemaConditionalInterpretationBuilder): SchemaConditionalInterpretationBuilder = interpretation.also { forEach { mod -> mod.applyTo(it) } }

}

internal class SchemaConditionalInterpretationBuilder(
    private val interpretationKey: String,
    private val type: SchemaType
) {

    private var isUnused = true


    var isDeprecated = false
        set(value) {
            require(isUnused)
            field = value
        }

    var since: V2SchemaVersion? = null
        set(value) {
            require(isUnused)
            field = value
        }

    var until: V2SchemaVersion? = null
        set(value) {
            require(isUnused)
            field = value
        }


    val interpretation by lazy {
        isUnused = false

        SchemaConditional.Interpretation(
            interpretationKey = interpretationKey,
            type = type,
            isDeprecated = isDeprecated,
            since = since,
            until = until
        )
    }

}

@APIGenDSL
internal class SchemaRecordBuilder : SchemaBuilder {

    private val _properties = mutableMapOf<String, SchemaRecordPropertyBuilder>()
    val properties get() = _properties.mapValues { it.value.property }

    /**
     * Registers a new record property with the @receiver as name.
     *
     * The name should be in _TitleCase_. By default, the _camelCase_ variant is generated by converting the first
     * character of the name to lower-case. Similarly, the _serial_name_ variant is generated by converting the entire
     * name to lower-case. For properties that require a custom behavior, the [CamelCase] and [SerialName] modifiers
     * should be used.
     *
     * @param type          the type of the record member
     * @param description   the description of the parameter. (Should be worded to complete the sentence "This field
     *                      holds {description}.")
     */
    @APIGenDSL
    operator fun String.invoke(
        type: SchemaType,
        description: String
    ): SchemaRecordPropertyBuilder {
        return SchemaRecordPropertyBuilder(this, type, description).also { _properties[this] = it }
    }

    /** Marks a deprecated property. */
    val deprecated = Modifiers.deprecated

    /** Marks an optional property. */
    val optional = Modifiers.optional

    /** Marks an optional property whose presents is mandated by the given `scope`. */
    fun optional(scope: TokenScope): IPropertyModifier = object : IPropertyModifier {
        override fun applyTo(property: SchemaRecordPropertyBuilder) {
            property.optionality = Optionality.MANDATED(scope)
        }
    }

    /** The minimal [V2SchemaVersion] (inclusive) required for the property. */
    fun since(version: V2SchemaVersion): IPropertyModifier = object : IPropertyModifier {
        override fun applyTo(property: SchemaRecordPropertyBuilder) {
            property.since = version
        }
    }

    /** The maximum [V2SchemaVersion] (exclusive) required for the property. */
    fun until(version: V2SchemaVersion): IPropertyModifier = object : IPropertyModifier {
        override fun applyTo(property: SchemaRecordPropertyBuilder) {
            property.until = version
        }
    }

    /** Explicitly specifies the _camelCase_ name for the property. */
    @Suppress("FunctionName")
    fun CamelCase(value: String): IPropertyModifier = object : IPropertyModifier {
        override fun applyTo(property: SchemaRecordPropertyBuilder) {
            property.camelCase = value
        }
    }

    /** Explicitly specifies the serial name for the property. */
    @Suppress("FunctionName")
    fun SerialName(value: String): IPropertyModifier = object : IPropertyModifier {
        override fun applyTo(property: SchemaRecordPropertyBuilder) {
            property.serialName = value
        }
    }

    operator fun IPropertyModifier.rangeTo(modifier: IPropertyModifier): Set<IPropertyModifier> = setOf(this, modifier)
    operator fun Set<IPropertyModifier>.rangeTo(modifier: IPropertyModifier): Set<IPropertyModifier> = setOf(modifier, *this.toTypedArray())

    operator fun IPropertyModifier.rangeTo(property: SchemaRecordPropertyBuilder): SchemaRecordPropertyBuilder = property.also { this.applyTo(it) }
    operator fun Set<IPropertyModifier>.rangeTo(property: SchemaRecordPropertyBuilder): SchemaRecordPropertyBuilder = property.also { forEach { mod -> mod.applyTo(it) } }

}

internal class SchemaRecordPropertyBuilder(
    private val propertyName: String,
    private val type: SchemaType,
    private val description: String
) {

    init {
        require(propertyName[0].isUpperCase()) { "propertyName should be in TitleCase" }
    }

    private var isUnused = true

    var isDeprecated = false
        set(value) {
            require(isUnused)
            field = value
        }

    var optionality: Optionality? = null
        set(value) {
            require(isUnused)
            field = value
        }

    var since: V2SchemaVersion? = null
        set(value) {
            require(isUnused)
            field = value
        }

    var until: V2SchemaVersion? = null
        set(value) {
            require(isUnused)
            field = value
        }

    var serialName: String? = null
        set(value) {
            require(isUnused)
            field = value
        }

    var camelCase: String? = null
        set(value) {
            require(isUnused)
            field = value
        }

    val property by lazy {
        isUnused = false

        SchemaRecord.Property(
            propertyName = propertyName,
            type = type,
            description = description,
            optionality = optionality ?: Optionality.REQUIRED,
            isDeprecated = isDeprecated,
            since = since,
            until = until,
            serialName = serialName ?: propertyName.toLowerCase(),
            camelCaseName = camelCase ?: propertyName.run { "${toCharArray()[0].toLowerCase()}${substring(1)}" }
        )
    }

}

internal interface IInterpretationModifier {
    fun applyTo(interpretation: SchemaConditionalInterpretationBuilder)
}

internal interface IPropertyModifier {
    fun applyTo(property: SchemaRecordPropertyBuilder)
}

internal object Modifiers {
    abstract class PropertyModifier : IPropertyModifier
    abstract class SharedModifier : IInterpretationModifier, IPropertyModifier

    val deprecated = object : SharedModifier() {

        override fun applyTo(interpretation: SchemaConditionalInterpretationBuilder) {
            interpretation.isDeprecated = true
        }

        override fun applyTo(property: SchemaRecordPropertyBuilder) {
            property.isDeprecated = true
        }

    }

    val optional = object : PropertyModifier() {
        override fun applyTo(property: SchemaRecordPropertyBuilder) {
            property.optionality = Optionality.OPTIONAL
        }
    }
}