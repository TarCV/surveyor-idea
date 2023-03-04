/*
 *  Copyright (C) 2023 TarCV
 *
 *  This package (i.e. directory and its contents) is part of UI Surveyor.
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
package android.util

import com.github.tarcv.testingteam.surveyor.Logger

class Log {
    companion object {
        @JvmStatic
        fun isLoggable(a: String, b: Int): Boolean = true

        @JvmStatic
        fun d(tag: String, message: String): Int {
            Logger.debug("$tag $message")
            return 0
        }

        @JvmStatic
        fun i(tag: String, message: String): Int {
            Logger.info("$tag $message")
            return 0
        }
    }
}