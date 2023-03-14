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
package com.github.tarcv.testingteam.surveyoridea.gui

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiSelector
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.EditorTextField

@Suppress("UnstableApiUsage")
class SimpleLocateToolWindow(project: Project) : LocateToolWindow(project) {
    override val fileType: LanguageFileType = PlainTextFileType.INSTANCE

    private val locatorText: @NlsSafe String
        get() = getPlainTextLocatorText()

    override fun switchToDroidUiAutomator(editorField: EditorTextField) {
        with(editorField) {
            document = EditorFactory.getInstance().createDocument(StringUtil.convertLineSeparators(text))
            fileType = FileTypes.PLAIN_TEXT
        }
    }

    override fun getCurrentDroidUiAutomatorLocator(): String {
        val fragment = locatorText
        val imports = listOf(UiSelector::class.java, By::class.java)
            .joinToString("") { "import ${it.name}; " }
        return imports + fragment
    }
}