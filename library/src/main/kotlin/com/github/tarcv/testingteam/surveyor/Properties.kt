/*
 *  UiAutomator plugin for TestingTeam-Surveyor
 *  This program, except the code under src/main/kotlin/android directory, is
 *
 *  Copyright (C) 2021 TarCV
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.tarcv.testingteam.surveyor

@Suppress("ClassName")
sealed class Properties<T>(){
    object CLASS_NAME: Properties<String>()
    object IS_CLICKABLE: Properties<Boolean>()
    object ACCESSIBILITY_DESCRIPTION: Properties<String>()
    object IS_ENABLED: Properties<Boolean>()
    object IS_FOCUSED: Properties<Boolean>()
    object IS_FOCUSABLE: Properties<Boolean>()
    object IS_LONGCLICKABLE: Properties<Boolean>()
    object IS_SCROLLABLE: Properties<Boolean>()
    object IS_SELECTED: Properties<Boolean>()
    object PACKAGE_NAME: Properties<String>()
    object RESOURCE_ID: Properties<String>()
    object TEXT: Properties<String>()
    object IS_CHECKED: Properties<Boolean>()
    object IS_CHECKABLE: Properties<Boolean>()
}