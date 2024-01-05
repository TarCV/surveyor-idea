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