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
