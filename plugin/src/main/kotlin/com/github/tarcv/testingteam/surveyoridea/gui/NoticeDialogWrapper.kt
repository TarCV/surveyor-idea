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

import com.github.tarcv.testingteam.surveyor.DroidNotices
import com.github.tarcv.testingteam.surveyor.IPredicateNotices
import com.github.tarcv.testingteam.surveyor.Notice
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.Gray
import com.intellij.ui.IdeBorderFactory.createBorder
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.components.BorderLayoutPanel
import java.awt.Font
import javax.swing.Action
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.border.CompoundBorder

class NoticeDialogWrapper : DialogWrapper(false) {
    private var introText: JBTextArea
    private var noticeList: JBList<Notice>
    private var noticeText: JBTextArea

    init {
        isModal = true
        title = "Licenses and Notices"
        setCancelButtonText("Close")

        introText = JBTextArea().apply {
            isEditable = false
            lineWrap = true
            margin.bottom = JBUIScale.scale(3)
            minimumSize = JBUI.size(1, 12)
            wrapStyleWord = true
        }
        noticeText = JBTextArea().apply {
            border = CompoundBorder(BorderFactory.createLineBorder(Gray._200), JBUI.Borders.empty(3))
            isEditable = false
            font = Font.decode("JetBrains Mono")
            rows = 10
        }

        noticeList = JBList<Notice>().apply {
            background = UIUtil.getListBackground()
            selectionBackground = UIUtil.getListSelectionBackground(true)

            border = createBorder()

            model = JBList.createDefaultListModel(
                DroidNotices.uiAutomatorNotice,
                IPredicateNotices.gnuStepNotice,
                IPredicateNotices.wdaNotice
            )

            addListSelectionListener {
                val notice = selectedValue
                introText.text = "UI Surveyor plugin " + notice.introText
                noticeText.text = notice.noticeText
                noticeText.select(0, 0)
            }
        }

        init()
        pack()
    }

    override fun createCenterPanel(): JComponent {
        val dialogIntro = JBTextArea().apply {
            background = null
            isEditable = false
            lineWrap = true
            minimumSize = JBUI.size(1, 12)
            rows = 2
            text = "UI Surveyor plugin depends on libraries " +
                    "which are covered by the following copyright and permission notices:"
            wrapStyleWord = true
        }
        val listPane = BorderLayoutPanel(0, 3).apply {
            addToTop(dialogIntro)
            addToCenter(noticeList)
        }

        val topPane = BorderLayoutPanel(0, 6).apply {
            addToCenter(listPane)
            addToBottom(introText)
        }

        return BorderLayoutPanel(0, 0).apply {
            minimumSize = JBUI.size(124, 124)
            preferredSize = JBUI.size(600, 450)

            addToTop(topPane)
            addToCenter(JBScrollPane(noticeText))
        }
    }

    override fun show() {
        super.show()
        invokeLater {
            noticeList.apply {
                requestFocus()
                selectedIndex = 0
            }
        }
    }

    override fun createActions(): Array<Action> = arrayOf(cancelAction)
}
