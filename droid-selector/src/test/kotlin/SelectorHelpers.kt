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
package com.github.tarcv.testingteam.surveyor.uiautomator

import com.github.tarcv.testingteam.surveyor.Node
import com.github.tarcv.testingteam.surveyor.DroidProperty
import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import java.lang.reflect.ParameterizedType
import java.util.regex.Pattern

val singleRootNode = Node(
    null,
    DroidProperty::class.nestedClasses
        .filter { !it.isCompanion }
        .associate {
            val property = it.objectInstance as DroidProperty<*>
            property to generateValueForProperty(property)
        },
    emptyList(),
    true
)

fun generateValueForProperty(it: DroidProperty<*>): Any {
    val propertiesType = it.javaClass.genericInterfaces.single() as ParameterizedType
    return generateValueFor(propertiesType.actualTypeArguments.single() as Class<*>)
}

private fun isGoodPattern(pattern: String) = try {
    Pattern.compile(pattern)
    true
} catch (t: Throwable) {
    false
}

fun commonArbitraryFor(it: Class<*>?, expectsPattern: Boolean): Arbitrary<out Any?> = when {
    it == null -> Arbitraries.just(null)

    expectsPattern && it.isAssignableFrom(String::class.java) -> patternStringArbitrary()

    it.isAssignableFrom(Class::class.java) -> Arbitraries.just(Node::class.java)

    it.isAssignableFrom(Pattern::class.java) -> {
        patternStringArbitrary().map(Pattern::compile)
    }

    it.isAssignableFrom(java.lang.String::class.java) || it.isAssignableFrom(String::class.java) -> {
        Arbitraries.strings().filter { it.isNotEmpty() }
    }

    it.isAssignableFrom(java.lang.Integer::class.java) || it.isAssignableFrom(Int::class.java) -> {
        Arbitraries.integers().between(1, 100)
    }

    else -> Arbitraries.defaultFor(it)
}

private fun generateValueFor(type: Class<*>): Any {
    return type.let {
        when {
            it.isAssignableFrom(Class::class.java) -> Node::class.java
            it.isAssignableFrom(java.lang.String::class.java) || it.isAssignableFrom(String::class.java) -> "foo"
            it.isAssignableFrom(java.lang.Integer::class.java) || it.isAssignableFrom(Int::class.java) -> 0
            it.isAssignableFrom(java.lang.Boolean::class.java) || it.isAssignableFrom(Boolean::class.java) -> false
            else -> TODO()
        }
    }
}

private fun patternStringArbitrary() = Arbitraries.strings().filter(::isGoodPattern)

