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

import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import com.github.tarcv.testingteam.surveyor.Evaluator
import com.github.tarcv.testingteam.surveyor.Logger
import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.Combinators
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class BySelectorProperties {
    private val evaluator = Evaluator()
    private val maxDepth = 10

    @Property
    fun bySelectorsDontCrash(
        @ForAll("bySelector") selector: BySelector
    ) {
        evaluator.evaluateBySelector(listOf(singleRootNode), selector)
    }

    @Provide
    fun bySelector(): Arbitrary<BySelector> {
        return bySelectorInternal(0)
    }

    private fun bySelectorInternal(depth: Int): Arbitrary<BySelector> {
        return Arbitraries.of(staticMethods)
            .filter {
                // This method is too hard to generate when used for BySelector in an argument
                depth == 0 || it.name != "hasAncestor"
            }
            .flatMap { method ->
                if (depth > maxDepth) {
                    return@flatMap Arbitraries.create { createDefaultBySelector() }
                }
                arbitraryCallingSelectorMethod(method, depth, null)
            }
            .flatMap { selector -> maybeAddMethodRecursive(selector!!, depth + 1) }
    }

    private fun maybeAddMethodRecursive(selector: BySelector, depth: Int): Arbitrary<BySelector> {
        return Arbitraries.lazy {
            Arbitraries.oneOf(
                Arbitraries.just(selector),
                addMethod(selector, depth + 1).flatMap {
                    when {
                        it == null -> Arbitraries.just(selector)
                        depth > maxDepth -> Arbitraries.create { createDefaultBySelector() }
                        else -> maybeAddMethodRecursive(it, depth + 1)
                    }
                }
            )
        }
    }

    private fun createDefaultBySelector(): BySelector = By.checked(true)

    private fun addMethod(selector: BySelector, depth: Int): Arbitrary<BySelector> {
        return Arbitraries.of(methods)
            .filter {
                // This method causes too many filter misses when used for BySelector in an argument
                depth == 0 || it.name != "hasAncestor"
            }
            .flatMap { method ->
                arbitraryCallingSelectorMethod(method, depth, selector)
            }
    }

    private fun arbitraryCallingSelectorMethod(
        method: Method,
        depth: Int,
        selector: BySelector?
    ): Arbitrary<BySelector> {
        val parameters = method.parameters
        val expectsPattern = method.name.contains("Matches")
        val result: Arbitrary<BySelector> = parameters
            .map {
                if (it.type?.isAssignableFrom(BySelector::class.java) == true) {
                    Arbitraries.lazy { bySelectorInternal(depth + 1 + 1) }
                } else {
                    commonArbitraryFor(it.type, expectsPattern)
                }
            }
            .let { arbitraries ->
                when (arbitraries.size) {
                    1 -> arbitraries[0].map {
                        invokeCatchingWrongArgumentsAsNull(method, selector, arrayOf(it))
                    }
                    2 -> Combinators.combine(arbitraries[0], arbitraries[1]).`as` { a, b ->
                        invokeCatchingWrongArgumentsAsNull(method, selector, arrayOf<Any?>(a, b))
                    }
                    else -> TODO()
                }
            }
            .filter { it !is InvokeResult.IllegalArguments }
            .map { when(it) {
                InvokeResult.IllegalMethod -> selector!!
                is InvokeResult.Success -> it.result

                InvokeResult.IllegalArguments -> throw IllegalStateException()
            } }
        return result
    }

    private fun invokeCatchingWrongArgumentsAsNull(
        method: Method,
        selector: BySelector?,
        it: Array<Any?>
    ): InvokeResult = try {
        InvokeResult.Success(method.invoke(selector, *it) as BySelector)
    } catch (e: InvocationTargetException) {
        if (e.targetException is IllegalStateException ||
            e.targetException is IllegalArgumentException
        ) {
            if (e.targetException.message!!.contains("already", ignoreCase = true)) {
                println("Called By.${method.name} after an incompatible method was already called: ${e.targetException.message}")
                InvokeResult.IllegalMethod
            } else {
                println("Called By.${method.name} with wrong arguments: ${e.targetException.message}")
                InvokeResult.IllegalArguments
            }
        } else {
            throw e.targetException
        }
    }

    companion object {
        init {
            Logger.onDebugMessage = { println(it) }
            Logger.onInfoMessage = { println(it) }
        }

        val staticMethods = By::class.java
            .methods
            .filter { Modifier.isStatic(it.modifiers) }
            .filter { it.name != "copy"}
        val methods = BySelector::class.java
            .methods
            .filter { Object::class.java.methods.none { rootMethod -> it.name == rootMethod.name } }
    }

    sealed interface InvokeResult {
        data class Success(val result: BySelector): InvokeResult
        data object IllegalArguments: InvokeResult
        data object IllegalMethod: InvokeResult
    }
}