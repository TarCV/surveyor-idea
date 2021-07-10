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

class Node(
    val parent: Node?,
    val properties: Map<Properties<*>, Any?>,
    val children: List<Node>,
    val isVisible: Boolean
) {
    inline fun <reified T: Any?> getProperty(key: Properties<T>): T = getProperty(key, T::class.java)

    @Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
    fun <T> getProperty(key: Properties<T>, clazz: Class<T>): T {
        return properties.getOrElse(key) {
            throw RuntimeException("${key.javaClass.simpleName} property is not supported within the current snapshot format")
        } as T
    }
}