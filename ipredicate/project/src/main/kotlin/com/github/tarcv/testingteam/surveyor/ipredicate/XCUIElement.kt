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

import com.github.tarcv.testingteam.surveyor.Node
import com.github.tarcv.testingteam.surveyor.IProperty

internal abstract class XCUIElement {
    companion object

    val query: XCUIElementQuery by lazy { XCUIElementQuery(this) }

    abstract val fb_cachedSnapshot: XCElementSnapshot

    abstract val elementType: XCUIElementType

    abstract val label: NSString?

    abstract fun childrenMatchingType(type: XCUIElementType): List<XCUIElement>
    open fun exists(): Boolean = true
}

internal data class Element(val node: Node) : FBElement, XCUIElement() {
    override val wdFrame: CGRect by lazy {
        CGRect(
            Point(
                node.getProperty(IProperty.X).toDouble(),
                node.getProperty(IProperty.Y).toDouble()
            ),
            Size(
                node.getProperty(IProperty.WIDTH).toDouble(),
                node.getProperty(IProperty.HEIGHT).toDouble()
            )
        )
    }

    override val wdRect: NSDictionary by lazy {
        mutableMapOf(
            "x" to node.getProperty(IProperty.X),
            "y" to node.getProperty(IProperty.Y),
            "width" to node.getProperty(IProperty.WIDTH),
            "height" to node.getProperty(IProperty.HEIGHT),
        )
    }

    override val wdName: NSString? by lazy {
        node.getProperty(IProperty.IDENTIFIER_OR_LABEL)
            ?.toNSString()
    }

    override val wdLabel: NSString? by lazy {
        node.getProperty(IProperty.LABEL)
            ?.toNSString()
    }

    override val wdSelected: Boolean
        get() = node.getProperty(IProperty.IS_SELECTED)

    override val wdType: NSString by lazy {
        node.getProperty(IProperty.ELEMENT_TYPE)
            .toNSString()
    }

    override val wdValue: NSString? by lazy {
        node.getProperty(IProperty.VALUE)
            ?.toNSString()
    }

    override val wdUID: NSString? by lazy {
        node.getProperty(IProperty.WDA_UID)
            ?.toNSString()
    }

    override val wdEnabled: Boolean
        get() = node.getProperty(IProperty.IS_ENABLED)

    override val wdVisible: Boolean
        get() = node.isVisible

    override val wdAccessible: Boolean
        get() = node.getProperty(IProperty.IS_ACCESSIBLE)

    override val wdAccessibilityContainer: Boolean
        get() = node.getProperty(IProperty.IS_ACCESSIBILITY_CONTAINER)

    override val wdFocused: Boolean
        get() = node.getProperty(IProperty.HAS_FOCUS)

    override val wdIndex: Int
        get() = node.getProperty(IProperty.CHILD_INDEX)

    private val _children: List<Element> by lazy {
        node.children.map { Element(it) }
    }

    override val fb_cachedSnapshot: XCElementSnapshot by lazy { ElementSnapshot(this) }
    override val elementType: XCUIElementType by lazy { FBElementTypeTransformer.elementTypeWithTypeName(wdType) }
    override val label: NSString?
        get() = wdLabel

    init {
        require(wdIndex >= 0)
    }

    override fun childrenMatchingType(type: XCUIElementType): List<XCUIElement> {
        return _children
            .filter { type == XCUIElementType.XCUI_ELEMENT_TYPE_ANY || type == it.elementType }
    }

}
internal class ElementSnapshot(private val wrappedElement: Element): FBElement by wrappedElement, XCElementSnapshot
