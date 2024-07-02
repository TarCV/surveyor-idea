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
package com.github.tarcv.testingteam.surveyoridea.data

import com.github.tarcv.testingteam.surveyor.Evaluator
import com.github.tarcv.testingteam.surveyor.InvalidLocatorException
import com.github.tarcv.testingteam.surveyor.InvalidSnapshotException
import com.github.tarcv.testingteam.surveyor.Node
import com.github.tarcv.testingteam.surveyor.evaluateIClassChain
import com.github.tarcv.testingteam.surveyor.evaluateIPredicate
import kotlin.jvm.Throws

sealed interface LocatorType {
    val title: String

    @Throws(InvalidLocatorException::class, InvalidSnapshotException::class)
    fun evaluate(nodes: List<Node>, locator: String): Node?
}

data object DroidUiSelectorLocatorType: LocatorType {
    override val title = "BySelector or UISelector"

    override fun evaluate(nodes: List<Node>, locator: String): Node? {
        return Evaluator().evaluate(nodes, locator)
    }
}

data object IPredicateLocatorType: LocatorType {
    override val title = "Appium/WDA Predicate"
    override fun evaluate(nodes: List<Node>, locator: String): Node? {
        return nodes
            .mapNotNull { evaluateIPredicate(it, locator) }
            .lastOrNull()
    }
}

data object IClassChainLocatorType: LocatorType {
    override val title = "Appium/WDA Class Chain"
    override fun evaluate(nodes: List<Node>, locator: String): Node? {
        return nodes
            .mapNotNull { evaluateIClassChain(it, locator) }
            .lastOrNull()
    }
}
