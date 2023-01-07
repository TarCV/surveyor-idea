/*
 *  Copyright (C) 2023 TarCV
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
import com.github.tarcv.testingteam.surveyor.Notice
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.Gray
import com.intellij.ui.IdeBorderFactory.createBorder
import com.intellij.ui.components.JBList
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.border.CompoundBorder

class NoticeDialogWrapper : DialogWrapper(false) {
    private lateinit var contentPane: JPanel
    private lateinit var topPane: JPanel
    private lateinit var listPane: JPanel
    private lateinit var dialogIntro: JTextArea
    private lateinit var introText: JTextArea
    private lateinit var noticeList: JBList<Notice>
    private lateinit var noticeText: JTextArea

    init {
        isModal = true
        title = "Licenses and Notices"
        setCancelButtonText("Close")
        init()
        pack()
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

    override fun createCenterPanel(): JComponent {
        contentPane.apply {
            topPane.apply {
                (layout as BorderLayout).vgap = JBUIScale.scale(6)

                dialogIntro.background = listPane.background

                listPane.apply {
                    (layout as BorderLayout).vgap = JBUIScale.scale(3)
                }
                noticeList.apply {
                    background = UIUtil.getListBackground()
                    selectionBackground = UIUtil.getListSelectionBackground(true)

                    border = createBorder()

                    model = JBList.createDefaultListModel(
                        DroidNotices.uiAutomatorNotice
                    )

                    addListSelectionListener {
                        val notice = noticeList.selectedValue
                        introText.text = "UI Surveyor plugin " + notice.introText
                        noticeText.text = notice.noticeText
                        noticeText.select(0, 0)
                    }
                }
            }

            introText.apply {
                margin.top = JBUIScale.scale(3)
            }
            noticeText.apply {
                border = CompoundBorder(BorderFactory.createLineBorder(Gray._200), JBUI.Borders.empty(3))
            }
            (layout as BorderLayout).vgap = JBUIScale.scale(3)
            preferredSize = JBUI.size(600, 450)
        }

        return contentPane
    }

    override fun createActions(): Array<Action> = arrayOf(cancelAction)
}
