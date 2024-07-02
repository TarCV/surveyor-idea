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

fun MutableList<UniChar>.subList(startIndex: Int): MutableList<UniChar> {
    return subList(startIndex, size)
}

fun MutableList<UniChar>.findIndexFromBase(base: List<UniChar>): UInt {
    val startIndex = base.size - this.size
    assert(this == base.subList(startIndex, base.size))
    require(startIndex >= 0)
    return startIndex.toUInt()
}

fun MutableList<UniChar>.addZeroesUntilSize(targetSize: UInt): MutableList<UniChar> {
    require(size.toUInt() <= targetSize)
    repeat((targetSize - size.toUInt()).toInt()) {
        add(0u)
    }
    return this
}

fun asUShortArray(value: Int): Array<UShort> {
    return arrayOf(value.toUShort())
}

fun asUShortArray(value: IntArray): Array<UShort> {
    return value
        .map { it.toUShort() }
        .toTypedArray()
}

fun uShortArrayOfZeroes(size: Int): Array<UShort> {
    return arrayOfNulls<UShort>(size)
        .map { 0.toUShort() }
        .toTypedArray()
}