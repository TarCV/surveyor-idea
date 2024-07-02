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

data class Notice(
    val title: String,
    val introText: String,
    val noticeText: String
) {
    override fun toString(): String = title

    companion object {
        inline fun <reified T : Any> T.loadNoticeFromResource(title: String, introText: String, noticePath: String): Notice {
            val noticeText = T::class.java.classLoader
                .getResourceAsStream(noticePath)
                ?.bufferedReader()
                ?.readText()
                ?: "<Failed to load the license or notice>"
            return Notice(title, introText, noticeText)
        }
    }
}