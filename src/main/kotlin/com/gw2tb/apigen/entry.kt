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
@file:Suppress("RedundantVisibilityModifier", "unused")
@file:JvmName("GW2APIGen")
package com.gw2tb.apigen

import com.gw2tb.apigen.internal.dsl.*
import com.gw2tb.apigen.internal.spec.*
import com.gw2tb.apigen.model.*
import com.gw2tb.apigen.schema.*
import java.util.*

/**
 * The definition for the API version 2 of the Guild Wars 2 API.
 *
 * @since   0.1.0
 */
public val API_V2_DEFINITION: APIVersion by lazy {
    APIVersion(
        endpoints = GW2v2(),
        supportedLanguages = EnumSet.allOf(Language::class.java)
    )
}

/**
 * The definition Guild Wars 2's use of the `identity` field of the MumbleLink protocol.
 *
 * @since   0.1.0
 */
public val MUMBLELINK_IDENTITY_DEFINITION: SchemaRecord by lazy {
    SchemaRecord(SchemaRecordBuilder().apply {
        "Name"(STRING, "the name of the currently played character")
        "Profession"(INTEGER, "the current profession (class) of the currently played character")
        "Spec"(INTEGER, "the ID of the current elite-specialization of the currently played character, or 0")
        "Race"(INTEGER, "the ID of the race of the currently played character")
        SerialName("map_id").."MapId"(INTEGER, "the ID of the current map")
        SerialName("world_id").."WorldId"(INTEGER, "the ID of the current world")
        SerialName("team_color_id").."TeamColorId"(INTEGER, "the ID of the current team")
        "Commander"(BOOLEAN, "whether or not the player currently is commanding a squad")
        "Map"(INTEGER, "the ID of the current map")
        "FoV"(DECIMAL, "the scaling of the FOV")
        SerialName("uisz").."UISize"(INTEGER, "the selected UI size")
    }.properties, null)
}