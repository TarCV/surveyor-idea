/*
 Copyright (C) 2024 TarCV

 This file is part of UI Surveyor.
 UI Surveyor is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.

 This file is based on GNUstep libraries that are covered under the following terms (Copies of referenced
 license or notice files can be found in ipredicate/licenses/gnustepbase subdirectory in this repository):
   The GNUstep libraries and library resources are covered under the
   GNU Public License. This means if you make changes to these programs,
   you cannot charge a fee, other than distribution fees, for others to use
   the program.  You should read the COPYING file for more information.
*/
@file:Suppress("FunctionName", "EnumEntryName")
package com.github.tarcv.testingteam.surveyor.ipredicate

import com.github.tarcv.testingteam.surveyor.ipredicate.NSString.Companion.toNSString
import com.github.tarcv.testingteam.surveyor.Logger
import java.beans.Introspector
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.ParsePosition
import java.time.Duration
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Objects
import java.util.concurrent.TimeUnit

fun UInt(value: UInt): UInt = value
fun UInt(value: ULong): UInt {
    require(value <= UInt.MAX_VALUE.toULong())
    return value.toUInt()
}

fun Int(value: Int): Int = value
fun Int(value: UInt): Int {
    require(value <= Int.MAX_VALUE.toUInt())
    return value.toInt()
}
fun Int(value: ULong): Int {
    require(value <= Int.MAX_VALUE.toULong())
    return value.toInt()
}

val UInt.Companion.min: UInt
    get() = MIN_VALUE
val UInt.Companion.max: UInt
    get() = MAX_VALUE

fun UInt64(value: UInt64): UInt64 = value

fun UInt64_(value: Int64): UInt64 {
    require(value >= 0)
    return value.toULong()
}
fun UInt64(value: UInt): UInt64 = value.toULong()

fun Int64(value: ULong): Long {
    require(value <= Int64.MAX_VALUE.toULong())
    return value.toLong()
}

operator fun Int.plus(other: UInt): Int = this + other.toInt()
operator fun UInt.plus(other: Int): Int = this.toInt() + other

typealias Decimal = BigDecimal
typealias UInt64 = ULong
typealias Int64 = Long
typealias UniChar = UShort

typealias NSDictionary = MutableMap<*, *>

fun NSNumber(value: Boolean): NSNumber = value.toNSNumber()

val NSNumber.int32Value: Int
    get() {
        val tmp = this.toLong()
        require(Int.MIN_VALUE <= tmp && tmp <= Int.MAX_VALUE)

        return this.toInt()
    }

val NSNumber.intValue: Int
    get() = this.int32Value

val NSNumber.uint32Value: UInt
    get() {
        val tmp = this.toLong()
        require(UInt.MIN_VALUE.toLong() <= tmp && tmp <= UInt.MAX_VALUE.toLong())
        return tmp.toUInt()
    }

val NSNumber.doubleValue: Double
    get() = this.toDouble()

fun Any.toNSNumber(): NSNumber = when(this) {
    is Boolean -> if (this) {
        BigDecimal.ONE
    } else {
        BigDecimal.ZERO
    }
    is BigDecimal -> this
    is Double -> this.toBigDecimal()
    is Float -> this.toBigDecimal()
    is Long -> this.toBigDecimal()
    is Int -> this.toBigDecimal()
    else -> this.toString().toBigDecimal()
}

class InOut<T : Any> {
    private var wrappedValue: T? = null

    infix fun `=`(value: T) {
        wrappedValue = value
    }

    operator fun unaryPlus(): T = requireNotNull(wrappedValue)

    override fun equals(other: Any?): Boolean = throw AssertionError("This object must not be used as is")

    override fun hashCode(): Int = throw AssertionError("This object must not be used as is")

    override fun toString(): String = throw AssertionError("This object must not be used as is")
}

fun <T : Any> type(instance: T) = instance.javaClass
fun <T : Any> Class<T>.init(vararg args: Any): T {
    @Suppress("UNCHECKED_CAST")
    return this.constructors
        .single { it.parameterCount == args.size }
        .newInstance(*args) as T
}

typealias Range<T> = ClosedRange<T>
val <T : Comparable<T>> Range<T>.isEmpty
    get() = this.isEmpty()

val <T : Comparable<T>> Range<T>.location
    get() = this.start

val <T : Comparable<T>> Range<T>.lowerBound
    get() = this.start

val Range<StringUTF16ViewIndex>.length
    get() = this.endInclusive.index - this.start.index + 1

val Range<StringUTF16ViewIndex>.upperBound
    get() = StringUTF16ViewIndex(this.endInclusive.index + 1)

infix fun StringUTF16ViewIndex.until(to: StringUTF16ViewIndex): ClosedRange<StringUTF16ViewIndex> = this.rangeTo(
    StringUTF16ViewIndex(to.index - 1)
)

fun NSMakeRange(start: Int, count: Int) = start until start+count
fun NSMakeRange(start: StringUTF16ViewIndex, count: Int) = start until StringUTF16ViewIndex(start.index + count)

typealias AnyObject = Any
fun Any.isEqual(other: Any?) = Objects.equals(this, other)
fun Any.valueForKeyPath(keyPath: NSString): Any? {
    // TODO: implement @-prefixed functions

    if (this is Collection<*> && this !is NSString) {
        return this.map {
            it?.valueForKeyPath(keyPath)
        }
    }

    val (thisPath, otherPath) = keyPath.toString()
        .split(".", limit = 2)
        .plusElement("") // make sure at least two elements are always present
        .map { it.toNSString() }
    
    val result = if (this is Map<*, *>) {
        this[thisPath]
    } else if ("description" == thisPath.toString()) {
        this.toNSString() // TODO: what it should actually return?
    } else {
        Introspector.getBeanInfo(javaClass)
            .propertyDescriptors
            .singleOrNull { it.name == thisPath.toString() }
            ?.readMethod
            ?.invoke(this)
    }

    if (otherPath.isEmpty()) {
        return result
    }
    return result?.valueForKeyPath(otherPath)
}

typealias NSObject = Any
typealias NSNumber = BigDecimal
typealias NSDate = ZonedDateTime

class Selector(selector: NSString) {
    val arguments: List<String>
    val name: String

    init {
        val parts = selector.toString().split(":").map { it.trim() }
        name = parts.first()
        arguments = parts.drop(1)
    }
}
fun NSSelectorFromString(str: NSString) = Selector(str)
fun Any.getMethodBySelector(to: Selector) = this.javaClass.methods
    .filter { it.name == to.name }
    .singleOrNull { it.parameterCount == to.arguments.size }

fun Any.performSelector(sel: Selector, args: List<Any?>, waitUntilDone: Boolean): Any? {
    require(waitUntilDone)
    require(sel.arguments.size == args.size) { "Number of arguments should match" }
    val method = getMethodBySelector(sel) ?: throw IllegalArgumentException("Selector is not supported")
    return method.invoke(this, *args.toTypedArray())
}

private val NSDateReferenceDate = NSDate.of(2001, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
val NSDate.timeIntervalSinceReferenceDate: Double
    get() {
        val interval = Duration.between(
            NSDateReferenceDate,
            this
        )
        return interval.toNanos() / TimeUnit.SECONDS.toNanos(1).toDouble()
    }
fun NSDate(value: Double): ZonedDateTime {
    return NSDateReferenceDate
        .plusNanos(
            (value * TimeUnit.SECONDS.toNanos(1).toDouble())
                .toLong()
        )
}

fun NSLog(str: NSString) = NSLog(str.toString())
fun NSLog(str: String) = Logger.info(str)
fun NSLog(str: NSString, vararg args: Any) = NSLog(NSString(str.toString(), *args).toString())

fun precondition(block: () -> Boolean) = require(block())

enum class StringCompareOption {
    literal,
    caseInsensitive;

    fun toOptions() = NSString.CompareOptions(this.ordinal.toUInt())
}
enum class ComparisonResult {
    orderedAscending,
    orderedSame,
    orderedDescending
}

const val NSLocalizedDescriptionKey = "NSLocalizedDescriptionKey"
class NSError(
    type: String,
    @Suppress("UNUSED_PARAMETER") code: Int,
    info: Map<String, Any>
): Exception(type + System.lineSeparator() + info) {
    val localizedDescription: String = info[NSLocalizedDescriptionKey]?.toString() ?: ""

    constructor(
        type: NSString,
        code: Int,
        info: Map<String, Any>
    ): this(type.toString(), code, info)
}
fun GSPropertyListMake(
    obj: NSString,
    a1: Nothing?,
    asXml: Boolean,
    asDescription: Boolean,
    @Suppress("UNUSED_PARAMETER") indent: Int,
    out: InOut<NSString>
) {
    @Suppress("SENSELESS_COMPARISON") require(a1 == null)
    require(!asXml)
    require(asDescription)
    val result = if (obj.utf16.isEmpty()) {
        "\"\""
    } else {
        "\"${obj.toString().replace("\"", "\\\"")}\""// TODO: implement better quoting
    }
    out `=` result.toNSString()
}

fun NSAssert(condition: Boolean, format: String, vararg args: Any?) {
    if (!condition) {
        val message = NSString(format, *args).toString()
        throw NSError("Condition failed", 0, mapOf(
            NSLocalizedDescriptionKey to message
        ))
    }
}

data class Point(
    val x: Double,
    val y: Double
)
data class Size(
    val width: Double,
    val height: Double
)
data class CGRect(
    val origin: Point,
    val size: Size
)

class NumberFormatter {
    fun number(str: NSString): NSNumber? {
        return numberStyle.number(str)
    }

    var numberStyle: Style = Style.none

    enum class Style {
        none {
            override fun number(str: NSString): NSNumber? = str.toString()
                .toBigDecimalOrNull()
                ?.takeUnless { it != it.setScale(0, RoundingMode.FLOOR) }
        },
        decimal {
            override fun number(str: NSString): NSNumber? {
                val parsePosition = ParsePosition(0)
                val result = (DecimalFormat.getNumberInstance() as DecimalFormat)
                    .apply { isParseBigDecimal = true }
                    .parse(str.toString(), parsePosition)
                if (parsePosition.errorIndex >= 0) {
                    return null
                }
                return result as BigDecimal
            }
        };

        abstract fun number(str: NSString): NSNumber?
    }
}