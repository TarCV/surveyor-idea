/*
 *  Copyright (C) 2024 TarCV
 *
 *  This file is part of UI Surveyor.
 *  UI Surveyor is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.tarcv.testingteam.surveyor.ipredicate

import kotlin.jvm.JvmInline

@JvmInline
internal value class XCUIElementType(private val type: String) {
    init {
        require(type == type.uppercase())
    }
    @Suppress("DEPRECATION")
    companion object {
        val XCUI_ELEMENT_TYPE_ANY = fromTypeString("XCUIElementTypeAny")
        val XCUI_ELEMENT_TYPE_OTHER = fromTypeString("XCUIElementTypeOther")
        val XCUI_ELEMENT_TYPE_APPLICATION = fromTypeString("XCUIElementTypeApplication")
        val XCUI_ELEMENT_TYPE_BUTTON = fromTypeString("XCUIElementTypeButton")

        @Deprecated(message = "Use elementTypeWithTypeName", replaceWith = ReplaceWith("FBElementTypeTransformer.elementTypeWithTypeName(type)"))
        fun fromTypeString(type: String): XCUIElementType {
            if (!type.startsWith("XCUIElementType")) {
                throw IllegalArgumentException("$type is not a XCUIElementType")
            }
            return XCUIElementType(type.uppercase())
        }
    }
}
