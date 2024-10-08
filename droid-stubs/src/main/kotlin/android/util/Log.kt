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
package android.util

import com.github.tarcv.testingteam.surveyor.Logger

class Log {
    companion object {
        @Suppress("UNUSED_PARAMETER")
        @JvmStatic
        fun isLoggable(a: String, b: Int): Boolean = true

        @JvmStatic
        fun d(tag: String, message: String): Int {
            Logger.debug("$tag $message")
            return 0
        }

        @JvmStatic
        fun e(tag: String, message: String): Int {
            Logger.info("$tag $message")
            return 0
        }

        @JvmStatic
        fun i(tag: String, message: String): Int {
            Logger.info("$tag $message")
            return 0
        }

        @JvmStatic
        fun i(tag: String, message: String, exception: Throwable): Int {
            Logger.info("$tag $message $exception")
            return 0
        }

        @JvmStatic
        fun w(tag: String, message: String): Int {
            Logger.info("$tag $message")
            return 0
        }
    }
}