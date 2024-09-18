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

sealed interface DroidProperty<T> : Property<T> {

    object CLASS_NAME: DroidProperty<String>
    object IS_CLICKABLE: DroidProperty<Boolean>
    object ACCESSIBILITY_DESCRIPTION: DroidProperty<String>
    object IS_ENABLED: DroidProperty<Boolean>
    object IS_FOCUSED: DroidProperty<Boolean>
    object IS_FOCUSABLE: DroidProperty<Boolean>
    object IS_LONG_CLICKABLE: DroidProperty<Boolean>
    object IS_PASSWORD_FIELD: DroidProperty<Boolean>
    object IS_SCROLLABLE: DroidProperty<Boolean>
    object IS_SELECTED: DroidProperty<Boolean>
    object PACKAGE_NAME: DroidProperty<String>
    object RESOURCE_ID: DroidProperty<String>
    object TEXT: DroidProperty<String>
    object IS_CHECKED: DroidProperty<Boolean>
    object IS_CHECKABLE: DroidProperty<Boolean>
    object DISPLAY_ID: DroidProperty<Int>
    object HINT: DroidProperty<String>
}