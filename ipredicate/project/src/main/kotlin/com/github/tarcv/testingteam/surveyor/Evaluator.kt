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
package com.github.tarcv.testingteam.surveyor

import com.github.tarcv.testingteam.surveyor.ipredicate.Element
import com.github.tarcv.testingteam.surveyor.ipredicate.NSError
import com.github.tarcv.testingteam.surveyor.ipredicate.NSPredicate
import com.github.tarcv.testingteam.surveyor.ipredicate.NSString.Companion.toNSString
import com.github.tarcv.testingteam.surveyor.ipredicate.fb_descendantsMatchingClassChain
import com.github.tarcv.testingteam.surveyor.ipredicate.fb_descendantsMatchingPredicate

fun evaluateIClassChain(rootNode: Node, locator: String): Node? {
    try {
        return Element(rootNode)
            .fb_descendantsMatchingClassChain(
                locator.toNSString(),
                shouldReturnAfterFirstMatch = true
            )
            .firstOrNull()
            ?.let {
                (it as Element).node
            }
    } catch (e: NSError) {
        throw InvalidLocatorException(e.localizedDescription)
    }
}

fun evaluateIPredicate(rootNode: Node, locator: String): Node? {
    try {
        return Element(rootNode)
            .fb_descendantsMatchingPredicate(
                NSPredicate.predicateWithFormat(locator.toNSString()),
                shouldReturnAfterFirstMatch = true
            )
            .firstOrNull()
            ?.let {
                (it as Element).node
            }
    } catch (e: NSError) {
        throw InvalidLocatorException(e.localizedDescription)
    }
}
