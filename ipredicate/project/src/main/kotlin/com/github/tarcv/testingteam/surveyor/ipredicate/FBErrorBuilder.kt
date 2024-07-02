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

object FBErrorBuilder {
    val builder = Builder()
    class Builder {
        private lateinit var description: String

        fun withDescription(description: String): Builder {
            this.description = description
            return this
        }

        fun withDescription(description: NSString): Builder {
            this.description = description.toString()
            return this
        }

        fun build(): NSError {
            return NSError(
                "Error", 0, mapOf(
                    NSLocalizedDescriptionKey to description
                )
            )
        }
    }
}