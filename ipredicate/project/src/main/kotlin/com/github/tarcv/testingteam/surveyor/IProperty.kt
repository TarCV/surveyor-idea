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
package com.github.tarcv.testingteam.surveyor

sealed interface IProperty<T> : Property<T> {
    companion object {
        val allProperties = IProperty::class.sealedSubclasses
            .mapNotNull { it.objectInstance }
    }

    object X: IProperty<Int>
    object Y: IProperty<Int>
    object WIDTH: IProperty<Int>
    object HEIGHT: IProperty<Int>
    object CHILD_INDEX : IProperty<Int>
    object VALUE: IProperty<String?>
    object IDENTIFIER_OR_LABEL : IProperty<String?>
    object LABEL : IProperty<String?>
    object IS_ACCESSIBLE : IProperty<Boolean>
    object IS_ACCESSIBILITY_CONTAINER : IProperty<Boolean>
    object IS_ENABLED : IProperty<Boolean>
    object HAS_FOCUS : IProperty<Boolean>
    object IS_SELECTED : IProperty<Boolean>
    object ELEMENT_TYPE : IProperty<String>
    object WDA_UID : IProperty<String?>
}