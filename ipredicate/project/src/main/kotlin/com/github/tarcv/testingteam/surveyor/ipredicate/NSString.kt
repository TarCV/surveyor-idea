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

import com.github.tarcv.testingteam.surveyor.ipredicate.NSString.Companion.toNSString
import java.util.Collections
import kotlin.jvm.JvmInline
import kotlin.math.ceil
import kotlin.math.floor

typealias NSStringCompareOptions = MutableSet<StringCompareOption>

interface HasDescription {
    fun description(): NSString
}
fun NSStringCompareOptions.insert(item: StringCompareOption) {
    this.add(item)
}

class NSString private constructor(val utf16: StringUTF16View, @Suppress("UNUSED_PARAMETER") unused: Nothing?)
    : StringUTF16View by utf16 {
    constructor(utf16: StringUTF16View): this(Collections.unmodifiableList(utf16), null)

    private val asString: String by lazy {
        val codepoints = utf16
            .map { it.toInt() }
            .toIntArray()
        String(codepoints, 0, codepoints.size)
    }

    companion object {
        fun String.toNSString(): NSString {
            return this.codePoints().toArray()
                .map { it.toUShort() }
                .let { NSString(it) }
        }

        @Suppress("FunctionName")
        fun CompareOptions(flags: UInt = 0u): NSStringCompareOptions {
            return StringCompareOption.entries
                .filter { (flags and it.ordinal.toUInt()) == it.ordinal.toUInt() }
                .toMutableSet()
        }

        val emptyString = NSString(emptyList())
    }

    fun range(
        of: NSString,
        options: Set<StringCompareOption> = emptySet(),
        range: Range<StringUTF16ViewIndex> = startIndex .. endIndex
    ): Range<StringUTF16ViewIndex> {
        // TODO: port this method from OpenSTEP

        // TODO: NSLog("Diacritic insensitive search is not implemented yet, but it was requested")
        val thisStr = this[range]

        if (of.utf16.isEmpty()) {
            return StringUTF16ViewIndex(0) until StringUTF16ViewIndex(1)
        }
        val thisJavaStr = thisStr.toString()
        val otherJavaStr = of.toString()
        val position =
            thisJavaStr.indexOf(otherJavaStr, ignoreCase = options.contains(StringCompareOption.caseInsensitive))
        if (position < 0) {
            return StringUTF16ViewIndex(-1) .. StringUTF16ViewIndex( -2)
        }
        return StringUTF16ViewIndex(range.start.index + thisJavaStr.codePointCount(0, position)) until
                StringUTF16ViewIndex(range.start.index + thisJavaStr.codePointCount(0, position + otherJavaStr.length))
    }

    fun appendFormat(format: String, vararg args: Any?): NSString {
        return NSString(
            utf16 + NSString(format, *args).utf16
        )
    }

    fun lowercased(): NSString {
        return toString()
            .lowercase()
            .toNSString()
    }

    operator fun get(range: Range<StringUTF16ViewIndex>): NSString {
        return NSString(
            utf16.slice(range.start.index..range.endInclusive.index)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        return when (other) {
            is String -> this == other.toNSString()
            is NSString -> this.utf16 == other.utf16
            else -> super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return utf16.hashCode()
    }

    fun substring(from: Int, to: Int): NSString {
        return NSString(
            utf16.subList(from, to)
        )
    }

    fun substring(range: Range<StringUTF16ViewIndex>): NSString {
        return substring(range.start.index, range.endInclusive.index + 1)
    }

    fun substring(from: Int, to: StringUTF16ViewIndex): NSString = substring(from, to.index)

    override fun toString(): String = asString

    fun uppercased(): NSString {
        return toString()
            .uppercase()
            .toNSString()
    }

    fun indexOrNull(ch: Char): Int? {
        return utf16.indexOf(ch.code.toUShort())
            .takeUnless { it < 0 }
    }

    fun characterAtIndex(index: Int) = utf16[index]

    fun getCharacters(range: Range<StringUTF16ViewIndex>): Array<UniChar> {
        return this.characters
            .subList(range.start.index, range.endInclusive.index + 1)
            .toTypedArray()
    }
    fun suffix(index: Int) = NSString(utf16.drop(index))

    fun substringFromIndex(index: Int) = NSString(utf16.drop((index - 1).coerceAtLeast(0)))

    fun substringToIndex(index: Int) = NSString(utf16.take(index))

    fun prefix(index: Int) = NSString(utf16.take(index))
    fun prefix(index: StringUTF16ViewIndex) = prefix(index.index)

    fun compare(other: NSString, options: Set<StringCompareOption>, range: Range<StringUTF16ViewIndex>): ComparisonResult {
        return gsCompare(this, other, options, range)
    }

    fun replace(oldStr: NSString, newStr: NSString): NSString {
        return this.toString()
            .replace(oldStr.toString(), newStr.toString())
            .toNSString()
    }
    fun startsWith(other: NSString): Boolean {
        return utf16.size >= other.utf16.size && utf16.subList(0, other.utf16.size) == other.utf16
    }

    val characters
        get() = this

    val count
        get() = utf16.size

    val length
        get() = utf16.size

    val startIndex
        get() = utf16.startIndex
    val endIndex
        get() = utf16.endIndex
}
typealias StringUTF16View = List<UniChar>

operator fun StringUTF16View.get(index: StringUTF16ViewIndex): UniChar = this[index.index]

@JvmInline
value class StringUTF16ViewIndex(val index: Int): Comparable<StringUTF16ViewIndex> {
    override fun compareTo(other: StringUTF16ViewIndex): Int = index.compareTo(other.index)
}

@Suppress("UnusedReceiverParameter")
val StringUTF16View.startIndex: StringUTF16ViewIndex
    get() = StringUTF16ViewIndex(0)
val StringUTF16View.endIndex: StringUTF16ViewIndex
    get() = StringUTF16ViewIndex(this.size - 1)

fun StringUTF16View.index(from: Int, distance: UInt) = this.index(from, distance.toInt())

@Suppress("UnusedReceiverParameter")
fun StringUTF16View.index(from: Int, distance: Int): StringUTF16ViewIndex {
    return StringUTF16ViewIndex(from + distance)
}

@Suppress("UnusedReceiverParameter")
fun StringUTF16View.index(from: StringUTF16ViewIndex, distance: Int): StringUTF16ViewIndex {
    return StringUTF16ViewIndex(from.index + distance)
}

fun StringUTF16ViewIndex.utf16Offset(@Suppress("UNUSED_PARAMETER") unused: StringUTF16View): Int = this.index
fun StringUTF16ViewIndex.utf16Offset(@Suppress("UNUSED_PARAMETER") unused: NSString): Int = this.index

fun NSString(format: String, vararg args: Any?): NSString {
    val specifierRegex = Regex("(?<!%)%(.)")

    val nFormat = format.replace(specifierRegex) { ch ->
        val code = ch.groupValues[1].let {
            when (it) {
                "@" -> "s"
                "C" -> "c"
                else -> throw NotImplementedError("Format specifier '${ch.value}' is not implemented")
            }
        }
        "%${code}"
    }

    val specifiers = specifierRegex.findAll(format)
        .map { it.groupValues[1] }
        .toList()

    val nArgs = args.mapIndexed { index, it -> 
        if (it is HasDescription) {
            it.description()
        } else if (it is UniChar && specifiers[index] == "C") {
            Char(it)
        } else {
            it
        }
    }.toTypedArray()

    return nFormat.format(*nArgs).toNSString()
}
fun NSString(format: NSString, vararg args: Any?): NSString = NSString(format.toString(), *args)

fun NSString(str: NSString) = str

val String.utf16: StringUTF16View
    get() = this.toNSString().utf16

fun Any.toNSString(): NSString {
    return if (this is NSString) {
        this
    } else if (this is HasDescription) {
        this.description()
    } else if (this is Double && ceil(this) == floor(this)) {
        this.toInt().toString().toNSString()
    } else {
        this.toString().toNSString()
    }
}
