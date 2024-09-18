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

class Node(
    parent: Node?,
    val properties: Map<out Property<*>, Any?>,
    val children: List<Node>,
    val isVisible: Boolean
) {
    var parent: Node? = parent
        private set

    inline fun <reified T: Any?> getProperty(key: Property<T>): T = getProperty(key, T::class.java)

    @Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
    fun <T> getProperty(key: Property<T>, clazz: Class<T>): T {
        val found = properties[key]
        return if (found == null && !properties.containsKey(key)) {
            throw InvalidSnapshotException(
                "${key.javaClass.simpleName} property is not supported within the current snapshot"
            )
        } else {
            found as T
        }
    }

    fun finalizeChildren() {
        children.forEach {
            it.parent = this
        }
    }
}