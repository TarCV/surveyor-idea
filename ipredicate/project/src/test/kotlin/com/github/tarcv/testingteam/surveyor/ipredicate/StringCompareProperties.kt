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
import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide
import kotlin.test.assertEquals


class StringCompareProperties {
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
            .oneOf(
                Arbitraries.integers().map { it.toUShort() }.list().ofMaxSize(6).map { NSString(it) },
                Arbitraries.strings().withCharRange(' ', 0x7f.toChar()).ofMaxLength(5).map { it.toNSString() },
            )
    }

    @Provide
    fun optionSet(): Arbitrary<Set<*>> {
        return Arbitraries.defaultFor(Set::class.java, StringCompareOption::class.java)
    }
}