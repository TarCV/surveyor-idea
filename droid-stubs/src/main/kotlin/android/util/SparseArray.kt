/*
 *  Copyright (C) 2023 TarCV
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
package android.util

import java.util.Collections.synchronizedMap

class SparseArray<E: Any> private constructor(private val synchronizedMap: MutableMap<Int, E>): Map<Int, E> by synchronizedMap, Cloneable {
    constructor() : this(synchronizedMap(LinkedHashMap()))

    override fun clone(): SparseArray<E> {
        @Suppress("UNCHECKED_CAST")
        return SparseArray(synchronizedMap)
    }

    fun indexOfKey(key: Int): Int = synchronizedMap.keys.indexOf(key)

    fun get(key: Int, defaultValue: E?): E? {
        return if (synchronizedMap.containsKey(key)) {
            synchronizedMap[key]
        } else {
            defaultValue
        }
    }

    fun put(key: Int, value: E) {
        synchronizedMap[key] = value
    }

    fun keyAt(index: Int): Int = synchronizedMap.keys.elementAt(index)

    fun valueAt(index: Int): E = synchronizedMap.values.elementAt(index)
}
