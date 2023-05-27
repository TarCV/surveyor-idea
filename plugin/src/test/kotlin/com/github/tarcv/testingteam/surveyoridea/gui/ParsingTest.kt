package com.github.tarcv.testingteam.surveyoridea.gui

import com.automation.remarks.junit5.Video
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.locateElementToolWindow
import com.intellij.remoterobot.utils.keyboard
import com.intellij.remoterobot.utils.waitFor
import org.apache.commons.lang.StringEscapeUtils
import org.junit.jupiter.api.Test
import java.awt.event.KeyEvent
import java.time.Duration
import kotlin.test.assertEquals

class ParsingTest : BaseTestProjectTests() {
    @Test
    @Video
    fun testDroidSelectorParsing() = with(remoteRobot) {
        idea {
            openFileInTestProject(droidAutomatorSnapshotFile, "editorWithSnapshot")

            menuBar.select(
                "View",
                "Tool Windows",
                "Locate Element"
            )

            locateElementToolWindow {
                // Escaping is required due to simple concatenation in the text#set implementation
                editor.text =
                    StringEscapeUtils.escapeJavaScript(
                        """new UiSelector().resourceIdMatches(".+/celsiusText")"""
                    )


                val editorLanguage = editor.callJs<String>(
                    runInEdt = true, script = """
                        importPackage(com.intellij.openapi.fileEditor.impl.text)
                        TextEditorImpl.getDocumentLanguage(local.get('editor')).toString()
                    """.trimIndent()
                )
                assertEquals("Language: JAVA", editorLanguage)

                editor.keyboard {
                    pressing(KeyEvent.VK_CONTROL) {
                        key(KeyEvent.VK_END)
                    }
                    enterText(".")
                }
                val popupItems = waitFor(Duration.ofSeconds(5), functionWithCondition = {
                    val items: List<String> = editor.callJs(
                        runInEdt = true, script = """
                        importPackage(com.intellij.codeInsight.lookup)
                        const model = LookupManager.getActiveLookup(local.get('editor')).getList().getModel()
                        const listItems = new ArrayList();
                        for(let i = 0; i < model.size; ++i) { 
                            listItems.add(model.getElementAt(i).toString())
                        }
                        listItems
                    """.trimIndent()
                    )
                    items.isNotEmpty() to items
                })
                assert(popupItems.contains("index")) { "contains 'index'" }
                assert(popupItems.contains("resourceId")) { "contains 'resourceId'" }
                assert(popupItems.contains("text")) { "contains 'text'" }
                this@idea.jList {
                    // Check the lookup popup is actually displayed by comparing it with actually found jList
                    assertEquals(collectItems().size, popupItems.size)
                }

                // At this point it can be assumed locator in the editor is correctly highlighted
                // (as it has correct language (Java) and has working auto-completion)
            }

        }
    }
}