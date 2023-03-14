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

internal data class XCUIElementQuery(
    private val rootElement: XCUIElement,
    private val filters: List<Resolver> = emptyList()
) {
    val rootElementSnapshot: XCElementSnapshot by lazy { rootElement.fb_cachedSnapshot }

    val allElementsBoundByIndex: List<XCUIElement>
        get() = allElementsBoundByAccessibilityElement

    val allElementsBoundByAccessibilityElement: List<XCUIElement>
        get() {
            return filters
                .fold(listOf(rootElement)) { prevResults, filter ->
                    val result = if (prevResults.isNotEmpty()) {
                        filter.resolve(prevResults)
                    } else {
                        prevResults
                    }
                    result
                }
        }

    open fun firstMatch(): XCUIElement = allElementsBoundByAccessibilityElement.first()

    open fun descendantsMatchingType(type: XCUIElementType): XCUIElementQuery {
        return copy(filters = filters + object : Resolver {
            override fun resolve(prevResults: List<XCUIElement>): List<XCUIElement> {
                return prevResults
                    .flatMap { it.childrenMatchingType(XCUIElementType.XCUI_ELEMENT_TYPE_ANY) }
                    .flatMap {
                        search(it) {
                            type == XCUIElementType.XCUI_ELEMENT_TYPE_ANY || it.elementType == type
                        }
                    }
            }

            private fun search(parent: XCUIElement, condition: (XCUIElement) -> Boolean): List<XCUIElement> {
                val thisResult = if (condition(parent)) {
                    listOf(parent)
                } else {
                    emptyList()
                }
                return thisResult + parent.childrenMatchingType(XCUIElementType.XCUI_ELEMENT_TYPE_ANY).flatMap { search(it, condition) }
            }

        })
    }

    open fun matchingPredicate(predicate: NSPredicate): XCUIElementQuery {
        return copy(filters = filters + object : Resolver {
            override fun resolve(prevResults: List<XCUIElement>): List<XCUIElement> {
                return prevResults
                    .filter {
                        predicate.evaluateWithObject(it)
                    }
            }
        })
    }

    fun childrenMatchingType(type: XCUIElementType): XCUIElementQuery {
        return childrenMatching {
            type == XCUIElementType.XCUI_ELEMENT_TYPE_ANY || it.elementType == type
        }
    }

    private fun childrenMatching(condition: (XCUIElement) -> Boolean) =
        this.copy(filters = this.filters + object : Resolver {
            override fun resolve(prevResults: List<XCUIElement>): List<XCUIElement> {
                return prevResults
                    .flatMap { it.childrenMatchingType(XCUIElementType.XCUI_ELEMENT_TYPE_ANY) }
                    .filter(condition)
            }
        })

    fun containingPredicate(value: NSPredicate): XCUIElementQuery {
        return copy(filters = filters + object : Resolver {
            override fun resolve(prevResults: List<XCUIElement>): List<XCUIElement> {
                return prevResults.filter {
                    it.childrenMatchingType(XCUIElementType.XCUI_ELEMENT_TYPE_ANY)
                        .any {
                            search(it) {
                                value.evaluateWithObject(it)
                            }
                        }
                }
            }

            private fun search(parent: XCUIElement, condition: (XCUIElement) -> Boolean): Boolean {
                if (condition(parent)) {
                    return true
                }
                return parent.childrenMatchingType(XCUIElementType.XCUI_ELEMENT_TYPE_ANY)
                    .any { search(it, condition) }
            }

        })
    }

    interface Resolver {
        fun resolve(prevResults: List<XCUIElement>): List<XCUIElement>
    }
}
