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
import net.jqwik.api.Combinators
import net.jqwik.api.Data
import net.jqwik.api.ForAll
import net.jqwik.api.FromData
import net.jqwik.api.Property
import net.jqwik.api.Provide
import net.jqwik.api.Table
import net.jqwik.api.Tuple
import net.jqwik.api.Tuple.Tuple3
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

    fun regexZeroLengthExamples(string: String, quantifier: String) =
        listOf<Tuple3<NSString, NSString, Set<StringCompareOption>>>(
            Tuple.of(string.toNSString(), "$quantifier".toNSString(), emptySet()),
            Tuple.of(string.toNSString(), "^$quantifier".toNSString(), emptySet()),
            Tuple.of(string.toNSString(), "$$quantifier".toNSString(), emptySet()),
            Tuple.of(string.toNSString(), "\\b$quantifier".toNSString(), emptySet()),
            Tuple.of(string.toNSString(), "()$quantifier".toNSString(), emptySet()),
            Tuple.of(string.toNSString(), "(?:)$quantifier".toNSString(), emptySet()),
            Tuple.of(string.toNSString(), "(?=)$quantifier".toNSString(), emptySet()),
            Tuple.of(string.toNSString(), "(?!=)$quantifier".toNSString(), emptySet()),
            Tuple.of(string.toNSString(), "(?<=)$quantifier".toNSString(), emptySet()),
            Tuple.of(string.toNSString(), "(?!<=)$quantifier".toNSString(), emptySet()),
            Tuple.of(string.toNSString(), "(?!<)$quantifier".toNSString(), emptySet()),
        )

    // Known edge cases that caused failures in the past:
    @Data
    fun regexIsIcuCompatibleExamples() = listOf<Tuple3<NSString, NSString, Set<Any>>>(
        Tuple.of(NSString(listOf(0x20.toUShort())), NSString(listOf(0x20.toUShort())), emptySet()),
        Tuple.of("a".toNSString(), "}".toNSString(), emptySet()),
        Tuple.of("a".toNSString(), "\\".toNSString(), emptySet()),
        Tuple.of("1".toNSString(), "\\1".toNSString(), emptySet()),
        Tuple.of("1".toNSString(), "[\\1]".toNSString(), emptySet()),
        Tuple.of("a".toNSString(), "\\1(a)".toNSString(), emptySet()),
        Tuple.of("".toNSString(), "\\1()".toNSString(), emptySet()),
        Tuple.of("aa".toNSString(), "\\1(a)".toNSString(), emptySet()),
        Tuple.of("1a".toNSString(), "\\1(a)".toNSString(), emptySet()),
        Tuple.of("/".toNSString(), "/".toNSString(), emptySet()),
        Tuple.of("//".toNSString(), "//".toNSString(), emptySet()),
        Tuple.of("a".toNSString(), "?}".toNSString(), emptySet()),
        Tuple.of("{".toNSString(), "[(}{]".toNSString(), emptySet()),
        Tuple.of("}".toNSString(), "[]}]".toNSString(), emptySet()),
        Tuple.of("{".toNSString(), "[{]]".toNSString(), emptySet()),
//            Tuple.of("{".toNSString(), "[{]}]".toNSString(), emptySet()), // Not sure why this fails
        Tuple.of("!\"#%&'()*,./:;?@[\\]_{}-".toNSString(), "\\p{Punct}{23}".toNSString(), emptySet()),
        Tuple.of("\$+<=>^`|~".toNSString(), ".*\\p{Punct}.*".toNSString(), emptySet()),
        Tuple.of("c".toNSString(), "\\c".toNSString(), emptySet()),
        Tuple.of("\u0000".toNSString(), "\\c ".toNSString(), emptySet()),
        Tuple.of("\u0000\u0000".toNSString(), "\\c \\c ".toNSString(), emptySet()),
        Tuple.of("\u0131".toNSString(), "\\x{0049}".toNSString(), setOf(StringCompareOption.caseInsensitive)),
        Tuple.of("\u0049".toNSString(), "\\x{0131}".toNSString(), setOf(StringCompareOption.caseInsensitive)),
    ) +
            regexZeroLengthExamples("", "?") +
            regexZeroLengthExamples("", "*") +
            regexZeroLengthExamples("", "+") +
            regexZeroLengthExamples("", "{7}") +
            regexZeroLengthExamples("", "{0,7}")

    @Property
    @FromData("regexIsIcuCompatibleExamples")
    fun regexIsIcuCompatibleFromData(
        @ForAll("nsString") string1: NSString,
        @ForAll("nsRegexString") string2: NSString,
        @ForAll("optionSet") options: Set<StringCompareOption>
    ) = checkRegexIsIcuCompatible(string1, string2, options)

    @Property
    fun regexIsIcuCompatible(
        @ForAll("nsString") string1: NSString,
        @ForAll("nsRegexString") string2: NSString,
        @ForAll("optionSet") options: Set<StringCompareOption>
    ) {
        val icuResult = kotlin.runCatching { GSICUStringMatchesRegex(string1, string2, options) }.getOrDefault(false)
        val jvmResult = kotlin.runCatching {
                staticPredicate.GSICUStringMatchesRegex(string1, string2, options.toMutableSet())
            }.getOrDefault(false)
        assertEquals(icuResult, jvmResult, "Test if '$string1' matching '$string2'. The expected result is ICU one.")
    }

    fun checkRegexIsIcuCompatible(
        string1: NSString,
        string2: NSString,
        options: Set<StringCompareOption>
    ) {
        val icuResult = kotlin.runCatching { GSICUStringMatchesRegex(string1, string2, options) }
        val jvmResult = kotlin.runCatching {
            staticPredicate.GSICUStringMatchesRegex(string1, string2, options.toMutableSet())
        }
        if (icuResult.isFailure) {
            assertEquals(
                icuResult.isFailure, jvmResult.isFailure,
                "Test if it's error to check '$string1' against '$string2'\n" +
                        "ICU result: $icuResult\n" +
                        "JVM result: $jvmResult"
            )
        } else {
            assertEquals(icuResult, jvmResult, "Test if '$string1' matching '$string2'. The expected result is ICU one.")
        }
    }

    @Provide
    fun nsString(): Arbitrary<NSString> {
        return Arbitraries
            .oneOf(
                Arbitraries.integers().map { it.toUShort() }.list().ofMaxSize(6).map { NSString(it) },
                Arbitraries.strings().withCharRange(' ', 0x7f.toChar()).ofMaxLength(5).map { it.toNSString() },
            )
    }

    @Provide
    fun nsRegexString(): Arbitrary<NSString> {
        return Arbitraries
            .oneOf(
                Arbitraries.integers().map { it.toUShort() }.list().ofMaxSize(6).map { NSString(it) },
                Arbitraries.strings().withCharRange(Char.MIN_VALUE, Char.MAX_VALUE).ofMaxLength(5)
                    .map { it.toNSString() },
                Arbitraries.strings().withCharRange(' ', 0x7f.toChar()).ofMaxLength(5).map { it.toNSString() },
                Combinators
                    .combine(
                        Arbitraries.strings().withCharRange(Char.MIN_VALUE, Char.MAX_VALUE),
                        Arbitraries.strings().withChars('{', '[', '(', ')', ']', '}'),
                        Arbitraries.strings().withCharRange(Char.MIN_VALUE, Char.MAX_VALUE),
                        Arbitraries.strings().withChars('{', '[', '(', ')', ']', '}'),
                        Arbitraries.strings().withCharRange(Char.MIN_VALUE, Char.MAX_VALUE),
                    )
                    .`as` { t1, t2, t3, t4, t5 -> "$t1$t2$t3$t4$t5" }
                    .map { it.toNSString() },
                Combinators
                    .combine(
                        Arbitraries.strings().withCharRange(Char.MIN_VALUE, Char.MAX_VALUE),
                        Arbitraries.strings().ofMaxLength(2),
                        Arbitraries.strings().withCharRange(Char.MIN_VALUE, Char.MAX_VALUE),
                    )
                    .`as` { t1, t2, t3 -> "$t1\\c$t2$t3" }
                    .map { it.toNSString() }
            )
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
    library // load the library

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

        val regexObj = uregex_open_73(regexStr, regexLength, flags, parseError, errorCode)
        requireSuccess(errorCode.get(JAVA_INT, 0), "uregex_open", string, regex, opts)

        try {
            val textStr = string.getCharacters(NSMakeRange(string.startIndex, stringLength))
            .map { it.toShort() }
            .plus(0)
            .let {
                arena.allocateArray(JAVA_SHORT, *it.toShortArray())
            }

            uregex_setText_73(regexObj, textStr, stringLength, errorCode)
            requireSuccess(errorCode.get(JAVA_INT, 0), "uregex_setText", string, regex, opts)

            val result = uregex_matches_73(regexObj, 0, errorCode)
            requireSuccess(errorCode.get(JAVA_INT, 0), "uregex_matches", string, regex, opts)
            println("ICU result: $result ${describeParams(string, regex, opts)}")
            return@synchronized (result.toInt() != 0)
        } finally {
            uregex_close_73(regexObj)
        }

    }
}

private fun describeParams(string: NSString, regex: NSString, opts: Set<StringCompareOption>) =
    "for string='$string', regex='$regex' opts='$opts'"

private fun requireSuccess(code: Int, where: String, string: NSString, regex: NSString, opts: Set<StringCompareOption>) {
    require(U_SUCCESS(code)) {
        val msg = "ICU returned error: ${code} (${u_errorName_73(code).getUtf8String(0)})" +
                " from $where ${describeParams(string, regex, opts)}"
        println(msg)
        msg
    }
}