/**
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
 *
 *  This file is based on the parts of WebDriverAgent which are
 *  (copies of referenced files can be found in ipredicate/licenses/wda subdirectory
 *  in this repository):
 *
 *      Copyright (c) 2015-present, Facebook, Inc.
 *      All rights reserved.
 *
 *      This source code is licensed under the BSD-style license found in the
 *      LICENSE file in the root directory of this source tree. An additional grant
 *      of patent rights can be found in the PATENTS file in the same directory.
 */
package com.github.tarcv.testingteam.surveyor.ipredicate

import com.github.tarcv.testingteam.surveyor.IProperty
import com.github.tarcv.testingteam.surveyor.Property
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.test.assertEquals

class TestWda {
    @Test
    fun testDescendantsWithPredicateString() {
        val predicate = NSPredicate.predicateWithFormat("label = 'Alerts'".toNSString());
        val matchingSnapshots = testedView("/main.xml").fb_descendantsMatchingPredicate(predicate, false)
        val snapshotsCount = 2
        assertEquals(matchingSnapshots.size, snapshotsCount)
        assertEquals(matchingSnapshots.first().elementType, XCUIElementType.XCUI_ELEMENT_TYPE_BUTTON)
        assertEquals(matchingSnapshots.last().label, "Alerts".toNSString());

        val selfPredicate = NSPredicate.predicateWithFormat("label == 'Alerts'".toNSString())
        val selfElementsByPredicate = matchingSnapshots.last().fb_descendantsMatchingPredicate(selfPredicate, false);
        assertEquals(selfElementsByPredicate.count, 1);
    }

    @Test
    fun testSelfWithPredicateString() {
        val predicate = NSPredicate.predicateWithFormat("type == 'XCUIElementTypeApplication'".toNSString())
        val matchingSnapshots =
            app("/main.xml").fb_descendantsMatchingPredicate(predicate, shouldReturnAfterFirstMatch = false)
        assertEquals(matchingSnapshots.count, 1)
        assertEquals(matchingSnapshots.last().elementType, XCUIElementType.XCUI_ELEMENT_TYPE_APPLICATION)
    }

    @Test
    fun testSingleDescendantWithPredicateString() {
        val predicate = NSPredicate.predicateWithFormat("type = 'XCUIElementTypeButton'".toNSString())
        val matchingSnapshots =
            testedView("/main.xml").fb_descendantsMatchingPredicate(predicate, shouldReturnAfterFirstMatch = true)
        assertEquals(matchingSnapshots.count, 1)
        assertEquals(matchingSnapshots.last().elementType, XCUIElementType.XCUI_ELEMENT_TYPE_BUTTON)
    }

    @Test
    fun testSingleDescendantWithPredicateStringByIndex() {
        val predicate = NSPredicate.predicateWithFormat("type == 'XCUIElementTypeButton' AND index == 2".toNSString())
        val matchingSnapshots =
            testedView("/main.xml").fb_descendantsMatchingPredicate(predicate, shouldReturnAfterFirstMatch = false)
        assertEquals(matchingSnapshots.count, 1)
        assertEquals(matchingSnapshots.last().elementType, XCUIElementType.XCUI_ELEMENT_TYPE_BUTTON)
    }

    @Test
    fun testDescendantsWithClassChain() {
        val matchingSnapshots = app("/main.xml").fb_descendantsMatchingClassChain(
            "XCUIElementTypeWindow/XCUIElementTypeOther/**/XCUIElementTypeButton".toNSString(),
            shouldReturnAfterFirstMatch = false
        )
        assertEquals(matchingSnapshots.count, 5) // /XCUIElementTypeButton
        for (matchingSnapshot in matchingSnapshots) {
            assertEquals(matchingSnapshot.elementType, XCUIElementType.XCUI_ELEMENT_TYPE_BUTTON)
        }
    }

    @Test
    fun testDescendantsWithClassChainWithIndex() {
        // iPhone
        var queryString = "XCUIElementTypeWindow/*/*/*/*[2]/*/*/XCUIElementTypeButton".toNSString()
        val matchingSnapshots =
            app("/main.xml").fb_descendantsMatchingClassChain(queryString, shouldReturnAfterFirstMatch = false)
        if (matchingSnapshots.count == 0) {
            // iPad
            queryString = "XCUIElementTypeWindow/*/*/*/*/*[2]/*/*/XCUIElementTypeButton".toNSString()
            app("/main.xml").fb_descendantsMatchingClassChain(queryString, shouldReturnAfterFirstMatch = false)
        }
        assertEquals(matchingSnapshots.size, 5) // /XCUIElementTypeButton
        for (matchingSnapshot in matchingSnapshots) {
            assertEquals(matchingSnapshot.elementType, XCUIElementType.XCUI_ELEMENT_TYPE_BUTTON)
        }
    }

    @Test
    fun testDescendantsWithClassChainAndPredicates() {
        val queryString = "XCUIElementTypeWindow/**/XCUIElementTypeButton[`label BEGINSWITH 'A'`]".toNSString()
        val matchingSnapshots =
            app("/main.xml").fb_descendantsMatchingClassChain(queryString, shouldReturnAfterFirstMatch = false)
        assertEquals(matchingSnapshots.count, 2)
        assertEquals(matchingSnapshots.first().label, "Alerts".toNSString())
        assertEquals(matchingSnapshots.last().label, "Attributes".toNSString())
    }

    @Test
    fun testDescendantsWithIndirectClassChainAndPredicates() {
        val queryString = "XCUIElementTypeWindow/**/XCUIElementTypeButton[`label BEGINSWITH 'A'`]".toNSString()
        val simpleQueryMatches =
            app("/main.xml").fb_descendantsMatchingClassChain(queryString, shouldReturnAfterFirstMatch = false)
        val deepQueryMatches = app("/main.xml").fb_descendantsMatchingClassChain(
            "XCUIElementTypeWindow/**/XCUIElementTypeButton[`label BEGINSWITH 'A'`]".toNSString(),
            shouldReturnAfterFirstMatch = false
        )
        assertEquals(simpleQueryMatches.count, deepQueryMatches.count)
        assertEquals(simpleQueryMatches.first().label, deepQueryMatches.first().label)
        assertEquals(simpleQueryMatches.last().label, deepQueryMatches.last().label)
    }

    @Test
    fun testClassChainWithDescendantPredicate() {
        val simpleQueryMatches = app("/main.xml").fb_descendantsMatchingClassChain(
            "XCUIElementTypeWindow/*/*/*/*[2]".toNSString(),
            shouldReturnAfterFirstMatch = false
        )
        val predicateQueryMatches = app("/main.xml").fb_descendantsMatchingClassChain(
            "XCUIElementTypeWindow/*/*/*/*[\$type == 'XCUIElementTypeButton' AND label BEGINSWITH 'A'$]".toNSString(),
            shouldReturnAfterFirstMatch = false
        )
        assertEquals(simpleQueryMatches.count, predicateQueryMatches.count)
        assertEquals(simpleQueryMatches.first().elementType, predicateQueryMatches.first().elementType)
        assertEquals(simpleQueryMatches.last().elementType, predicateQueryMatches.last().elementType)
    }

    @Test
    fun testSingleDescendantWithComplexIndirectClassChain() {
        val queryMatches = app("/main.xml").fb_descendantsMatchingClassChain(
            "**/*/XCUIElementTypeButton[2]".toNSString(),
            shouldReturnAfterFirstMatch = false
        )
        assertEquals(queryMatches.count, 1)
        assertEquals(queryMatches.last().elementType, XCUIElementType.XCUI_ELEMENT_TYPE_BUTTON)
        assertEquals(queryMatches.last().label, "Deadlock app".toNSString())
    }

    @Test
    fun testSingleDescendantWithComplexIndirectClassChainAndZeroMatches() {
        val queryMatches = app("/main.xml").fb_descendantsMatchingClassChain(
            "**/*/XCUIElementTypeWindow".toNSString(),
            shouldReturnAfterFirstMatch = false
        )
        assertEquals(queryMatches.count, 0)
    }

    @Test
    fun testDescendantsWithClassChainAndPredicatesAndIndexes() {
        val queryString =
            "XCUIElementTypeWindow[`name != 'bla'`]/**/XCUIElementTypeButton[`label BEGINSWITH \"A\"`][1]".toNSString()
        val matchingSnapshots =
            app("/main.xml").fb_descendantsMatchingClassChain(queryString, shouldReturnAfterFirstMatch = false)
        assertEquals(matchingSnapshots.count, 1)
        assertEquals(matchingSnapshots.first().label, "Alerts".toNSString())
    }

    @Test
    fun testSingleDescendantWithClassChain() {
        val matchingSnapshots = testedView("/main.xml").fb_descendantsMatchingClassChain(
            "XCUIElementTypeButton".toNSString(),
            shouldReturnAfterFirstMatch = true
        )

        assertEquals(matchingSnapshots.count, 1)
        assertEquals(matchingSnapshots.last().elementType, XCUIElementType.XCUI_ELEMENT_TYPE_BUTTON)
        assertEquals(matchingSnapshots.last().label, "Alerts".toNSString())
    }

    @Test
    fun testSingleDescendantWithClassChainAndNegativeIndex() {
        var matchingSnapshots = testedView("/main.xml").fb_descendantsMatchingClassChain(
            "XCUIElementTypeButton[-1]".toNSString(),
            shouldReturnAfterFirstMatch = true
        )

        assertEquals(matchingSnapshots.count, 1)
        assertEquals(matchingSnapshots.last().elementType, XCUIElementType.XCUI_ELEMENT_TYPE_BUTTON)
        assertEquals(matchingSnapshots.last().label, "Touch".toNSString())

        matchingSnapshots = testedView("/main.xml").fb_descendantsMatchingClassChain(
            "XCUIElementTypeButton[-10]".toNSString(),
            shouldReturnAfterFirstMatch = true
        )
        assertEquals(matchingSnapshots.count, 0)
    }

    @Test
    fun testInvalidQueryWithClassChain() {
        assertThrows<NSError> {
            testedView("/main.xml").fb_descendantsMatchingClassChain(
                "NoXCUIElementTypePrefix".toNSString(),
                shouldReturnAfterFirstMatch = true
            )
        }
    }

    @Test
    fun testHandleInvalidQueryWithClassChainAsNoElementWithoutError() {
        val matchingSnapshots = testedView("/main.xml").fb_descendantsMatchingClassChain(
            "XCUIElementTypeBlabla".toNSString(),
            shouldReturnAfterFirstMatch = true
        )
        assertEquals(matchingSnapshots.count, 0)
    }

    @Test
    fun testClassChainWithInvalidPredicate() {
        assertThrows<NSError> {
            app("/main.xml").fb_descendantsMatchingClassChain(
                "XCUIElementTypeWindow[`bla != 'bla'`]".toNSString(),
                shouldReturnAfterFirstMatch = false
            )
        }
    }
}
private fun rootOf(resourceName: String): XCUIElement {
    val root = TestWda::class.java.getResourceAsStream(resourceName).use { stream ->
        DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(stream)
            .documentElement
    }
    return root.childNodes
        .single()
        .let { Element(convertNode(it)) }
}

private fun NodeList.single(): Node {
    if (this.length != 1) {
        throw IndexOutOfBoundsException()
    }
    return item(0)
}

private fun app(resourceName: String): XCUIElement {
    val element = rootOf(resourceName)
    assertEquals(XCUIElementType.XCUI_ELEMENT_TYPE_APPLICATION, element.elementType)
    return element
}

private fun testedView(resourceName: String): XCUIElement {
    fun search(element: XCUIElement): XCUIElement? {
        if (element.label.toString() == "MainView") {
            return element
        }

        val children = element.childrenMatchingType(XCUIElementType.XCUI_ELEMENT_TYPE_ANY)
        if (children.isEmpty()) {
            return null
        }
        return children
            .mapNotNull { search(it) }
            .single()
    }

    return rootOf(resourceName).let { search(it) }!!
        
}

private fun convertNode(node: Node): com.github.tarcv.testingteam.surveyor.Node {
    return com.github.tarcv.testingteam.surveyor.Node(
        null,
        buildPropertyMap {
            add(IProperty.X, (node["x"]?.toInt() ?: 0))
            add(IProperty.Y, (node["y"]?.toInt() ?: 0))
            add(IProperty.WIDTH, (node["width"]?.toInt() ?: 0))
            add(IProperty.HEIGHT, (node["height"]?.toInt() ?: 0))
            add(IProperty.CHILD_INDEX, (node["index"]?.toInt()?.takeUnless { it < 0 } ?: Int.MAX_VALUE))
            add(IProperty.VALUE, (node["value"]))
            add(IProperty.IDENTIFIER_OR_LABEL, (node["name"]))
            add(IProperty.LABEL, (node["label"]))
            add(IProperty.IS_ENABLED, (node["enabled"]?.toBooleanStrictOrNull() ?: false))
            add(IProperty.HAS_FOCUS, (node["focused"]?.toBooleanStrictOrNull() ?: false))
            add(IProperty.IS_SELECTED, (node["selected"]?.toBooleanStrictOrNull() ?: false))
            add(
                IProperty.ELEMENT_TYPE, (node["type"]
                    ?: XCUIElementType.XCUI_ELEMENT_TYPE_OTHER.toString())
            )

            node["uid"]?.let {
                add(IProperty.WDA_UID, it)
            }
            node["accessible"]?.toBooleanStrictOrNull()?.let {
                add(IProperty.IS_ACCESSIBLE, it)
            }
            node["accessibilityContainer"]?.toBooleanStrictOrNull()?.let {
                add(
                    IProperty.IS_ACCESSIBILITY_CONTAINER,
                    it
                )
            }
        },
        node.childNodes.map { convertNode(it) },
        node["visible"]?.toBooleanStrictOrNull() ?: true
    ).apply {
        finalizeChildren()
    }
}

private fun <T> NodeList.map(action: (Node) -> T): List<T> {
    val mapped = mutableListOf<T>()
    for (index in 0 until length) {
        val child = item(index)
        if (child.nodeType != Node.ELEMENT_NODE) {
            continue
        }
        mapped += action(child)
    }
    return mapped
}

class PropertyMapBuilder {
    private val result = mutableMapOf<Property<*>, Any?>()

    fun build(): Map<Property<*>, Any?> = result.toMap()
    fun <T> add(property: Property<T>, value: T) {
        result[property] = value
    }
}
fun buildPropertyMap(block: PropertyMapBuilder.() -> Unit): Map<Property<*>, Any?> {
    val builder = PropertyMapBuilder()
    block(builder)
    return builder.build()
}

operator fun Node.get(s: String) = attributes.getNamedItem(s)?.nodeValue
