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
import icu.UParseError
import icu.uregex_h_1.*
import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import kotlin.streams.toList


class StringCompareProperties {
    companion object {
        @BeforeAll
        @JvmStatic
        fun initIcu() {
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
            .filter {
                val regexBuffer = it
                    .chars()
                    .toList()
                    .map { it.toShort() }
                    .plus(0)
                    .toTypedArray()
                val success = synchronized(lock) {
                    Arena.openConfined().use { arena ->
                        val errorCode = arena.allocate(JAVA_INT).apply {
                            set(JAVA_INT, 0, 0)
                        }
                        val parseError = arena.allocate(UParseError.`$LAYOUT`())
                        val flags = UREGEX_DOTALL // . is supposed to recognize newlines
                        val regex = uregex_open_70(regexStr, regexLength, flags, parseError, errorCode)
                        uregex_close_70(regex)
                        U_SUCCESS(errorCode.get(JAVA_INT, 0))
                    }
                }
                success
            }
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
    val UREGEX_CASE_INSENSITIVE = 2
    val UREGEX_DOTALL = 32

    val stringLength: Int = string.length
    val regexLength: Int = regex.length

    Arena.openConfined().use { arena ->
        val regexStr = regex.getCharacters(NSMakeRange(regex.startIndex, regexLength))
            .map { it.toShort() }
            .plus(0)
            .let {
                arena.allocateArray(JAVA_SHORT, it)
            }
        val errorCode = arena.allocate(JAVA_INT).apply {
            set(JAVA_INT, 0, 0)
        }
        val parseError = arena.allocate(UParseError.`$LAYOUT`())

        var flags= UREGEX_DOTALL // . is supposed to recognize newlines
        if (opts.contains(StringCompareOption.caseInsensitive)) {
            flags = flags or UREGEX_CASE_INSENSITIVE; }

        val regex = uregex_open_70(regexStr, regexLength, flags, parseError, errorCode)
        require(U_SUCCESS(errorCode.get(JAVA_INT, 0))) { "Got error: ${errorCode.get(JAVA_INT, 0)}" }

        try {
            val textStr = string.getCharacters(NSMakeRange(string.startIndex, stringLength))
            .map { it.toShort() }
            .plus(0)
            .let {
                arena.allocateArray(JAVA_SHORT, it)
            }

            uregex_setText_70(regex, textStr, stringLength, errorCode)
            require(U_SUCCESS(errorCode.get(JAVA_INT, 0))) { "Got error: ${errorCode.get(JAVA_INT, 0)}" }

            val result = uregex_matches_70(regex, 0, errorCode)
            println("Result: $result")
            require(U_SUCCESS(errorCode.get(JAVA_INT, 0))) { "Got error: ${errorCode.getInt(0)}" }
            return@synchronized (result != 0)
        } finally {
            uregex_close_70(regex)
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