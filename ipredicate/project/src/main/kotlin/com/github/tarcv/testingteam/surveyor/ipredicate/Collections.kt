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

typealias NSSet = Set<Any?>
typealias NSMutableSet = MutableSet<Any?>

val <T> List<T>.first: T?
    get() = this.firstOrNull()
val <T> List<T>.count: Int
    get() = this.size

fun <T> MutableList<T>.addObjectsFromArray(items: Collection<T>) {
    this.addAll(items)
}

fun <T> MutableList<T>.insertObject(item: T, index: Int) {
    this.add(index, item)
}

fun <T> MutableList<T>.removeObjectAtIndex(index: Int) {
    this.removeAt(index)
}

fun <T> MutableList<T>.reserveCapacity(size: Int) {
    if (this is ArrayList<T>) {
        this.ensureCapacity(size)
    }
}

fun <T> MutableList<T>.removeObjectsInRange(range: IntRange) {
    range.reversed().forEach { 
        this.removeAt(it)
    }
}

fun <T> MutableList<T>.removeAllObjects() {
    this.clear()
}

class IndexingIterator<E>(private val iterator: Iterator<E>) {
    fun next(): E? = if (iterator.hasNext()) {
        iterator.next()
    } else {
        null
    }
}

fun <T> List(entries: Collection<T>) = entries.toMutableList()
fun <T> Set(entries: Collection<T>) = entries.toMutableSet()
fun <T> List<T>.makeIterator() = IndexingIterator(
    this.listIterator()
)

fun <T> Set<T>.makeIterator() = IndexingIterator(
    this.iterator()
)

fun <T> Set<T>.copy() = this.toMutableSet()

val <T> Set<T>.count: Int
    get() = this.size

fun NSMutableSet(capacity: Int) = HashSet<Any?>(capacity)

fun <T> MutableList<T>.addObject(element : T) {
    this.add(element)
}

fun <T> MutableSet<T>.addObject(element : T) {
    this.add(element)
}

fun <K : Any, V : Any> Dictionary(values: Map<K, V>) = HashMap<K, V>(values)
fun <K : Any, V : Any> MutableMap<K, V>.objectForKey(key: K) = this[key]
fun <K : Any, V : Any?> MutableMap<K, V>.setObject(value: V, key: K) {
    this[key] = value
}

fun <K : Any, V : Any> MutableMap<K, V>.setValue(value: V, key: K) {
    this[key] = value
}