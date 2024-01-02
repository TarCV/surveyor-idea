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
package com.github.tarcv.testingteam.surveyor.ipredicate

import com.github.tarcv.testingteam.surveyor.ipredicate.NSString.Companion.toNSString
import com.github.tarcv.testingteam.surveyor.ipredicate.StringCompareProperties.Companion.library
import icu.UParseError
import icu.uregex_h.*
import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import java.lang.foreign.Arena
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.foreign.ValueLayout.JAVA_SHORT

class StringCompareProperties {
    companion object {
        val library by lazy {
            System.loadLibrary("icui18n")
        }
    }

    @Property
    fun compareIsReversible(
        @ForAll("nsString") string1: NSString,
        @ForAll("nsString") string2: NSString,
        @ForAll("optionSet") options: Set<StringCompareOption>
    ) {
        val gsResult = string1.compare(string2, options, string1.startIndex..string1.endIndex)
        val expectedReverse = when (gsResult) {
            ComparisonResult.orderedAscending -> ComparisonResult.orderedDescending
            ComparisonResult.orderedSame -> ComparisonResult.orderedSame
            ComparisonResult.orderedDescending -> ComparisonResult.orderedAscending
        }
        val gsReverseResult = string2.compare(string1, options, string2.startIndex..string2.endIndex)
        assertEquals(expectedReverse, gsReverseResult)
    }

    @Property
    fun regexIsIcuCompatible(
        @ForAll("nsString") string1: NSString,
        @ForAll("nsValidSRegexes") string2: NSString,
        @ForAll("optionSet") options: Set<StringCompareOption>
    ) {
        val icuResult = kotlin.runCatching { GSICUStringMatchesRegex(string1, string2, options) }
        val jvmResult = kotlin.runCatching {
            staticPredicate.GSICUStringMatchesRegex(string1, string2, options.toMutableSet())
        }
        if (icuResult.isFailure) {
            assertEquals(icuResult.isFailure, jvmResult.isFailure,
                "Test if it's error to check '$string1' against /$string2/")
        } else {
            assertEquals(icuResult, jvmResult, "Test if '$string1' matching /$string2/")
        }
    }

    @Provide
    fun nsString(): Arbitrary<NSString> {
        return Arbitraries
            .strings()
            .withCharRange(Char(1), Char.MAX_VALUE)
            .ofMinLength(1)
            .map { it.toNSString() }
    }

    @Provide
    fun nsValidSRegexes(): Arbitrary<NSString> {
        return Arbitraries
            .strings()
            .withCharRange(Char(32), Char.MAX_VALUE)
            .ofMinLength(1)
            .map { it.toNSString() }
    }

    @Provide
    fun optionSet(): Arbitrary<Set<*>> {
        return Arbitraries.defaultFor(Set::class.java, StringCompareOption::class.java)
    }
}
private val staticPredicate = NSComparisonPredicate(
    left = GSConstantValueExpression(""),
    right = GSConstantValueExpression(""),
    modifier = NSComparisonPredicateModifier.NS_ALL_PREDICATE_MODIFIER,
    type = NSPredicateOperatorType.NS_GREATER_THAN_PREDICATE_OPERATOR_TYPE,
    opts = 0
)

val lock = Any()
fun U_SUCCESS(code: Int): Boolean {
    return (code <= 0); }
fun GSICUStringMatchesRegex(string: NSString, regex: NSString, opts: Set<StringCompareOption>): Boolean = synchronized(lock) {
    println(library)

    val UREGEX_CASE_INSENSITIVE = 2
    val UREGEX_DOTALL = 32

    val stringLength: Int = string.length
    val regexLength: Int = regex.length

    Arena.ofConfined().use { arena ->
        val regexStr = regex.getCharacters(NSMakeRange(regex.startIndex, regexLength))
            .map { it.toShort() }
            .plus(0)
            .let {
                arena.allocateArray(JAVA_SHORT, *it.toShortArray())
            }
        val errorCode = arena.allocate(JAVA_INT).apply {
            set(JAVA_INT, 0, 0)
        }
        val parseError = arena.allocate(UParseError.`$LAYOUT`())

        var flags= UREGEX_DOTALL // . is supposed to recognize newlines
        if (opts.contains(StringCompareOption.caseInsensitive)) {
            flags = flags or UREGEX_CASE_INSENSITIVE; }

        println("java.library.path=${System.getProperty("java.library.path")}")
        val regexObj = uregex_open_70(regexStr, regexLength, flags, parseError, errorCode)
        require(U_SUCCESS(errorCode.get(JAVA_INT, 0))) {
            "Got error: ${errorCode.get(JAVA_INT, 0)} (${u_errorName_70(errorCode.get(JAVA_INT, 0)).getUtf8String(0)})\n" +
                    regex
        }

        try {
            val textStr = string.getCharacters(NSMakeRange(string.startIndex, stringLength))
            .map { it.toShort() }
            .plus(0)
            .let {
                arena.allocateArray(JAVA_SHORT, *it.toShortArray())
            }

            uregex_setText_70(regexObj, textStr, stringLength, errorCode)
            require(U_SUCCESS(errorCode.get(JAVA_INT, 0))) { "Got error: ${errorCode.get(JAVA_INT, 0)}" }

            val result = uregex_matches_70(regexObj, 0, errorCode)
            println("Result: $result")
            require(U_SUCCESS(errorCode.get(JAVA_INT, 0))) { "Got error: ${errorCode.get(JAVA_INT, 0)}" }
            return@synchronized (result.toInt() != 0)
        } finally {
            uregex_close_70(regexObj)
        }

    }
}

fun main() {
    println(GSICUStringMatchesRegex(
        NSString(listOf(0x20.toUShort())),
        NSString(listOf(0x20.toUShort())),
        emptySet()
    ))
}