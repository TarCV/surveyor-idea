package com.github.tarcv.testingteam.surveyoridea.gui

import com.github.tarcv.testingteam.surveyoridea.ScreenshotTest
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.locateElementToolWindow
import com.github.tarcv.testingteam.surveyoridea.hasJavaSupport
import com.github.tarcv.testingteam.surveyoridea.waitingAssertEquals
import com.intellij.remoterobot.fixtures.EditorFixture
import com.intellij.remoterobot.utils.keyboard
import com.intellij.remoterobot.utils.waitFor
import org.apache.commons.text.StringEscapeUtils
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test
import java.awt.Point
import java.lang.Thread.sleep
import java.time.Duration
import kotlin.test.assertEquals

class EditingTest : BaseTestProjectTests() {
    @Test
    fun testDroidSelectorEditing() {
        assertDroidSelectorEditing("""new UiSelector().""", 1)
    }

    @Test
    fun testDroidSelectorMultilineEditing() {
        assertDroidSelectorEditing("""new UiSelector()${'\n'}.index(1).""", 2)
    }

    @ScreenshotTest
    fun screenshotDroidSelectorAutocomplete() {
        Assumptions.assumeTrue(hasJavaSupport, "Java support is required for autocomplete list to appear")
        assertDroidSelectorEditing("""new UiSelector().textContains("ICE").""", 1)
    }

    private fun assertDroidSelectorEditing(selector: String, expectedLineCount: Int) = with(remoteRobot) {
        idea {
            openFileAndToolWindow(relativeToProject(droidAutomatorSnapshotFile), editorWithSnapshot)
            locateElementToolWindow {
                // Escaping is required due to simple concatenation in the text#set implementation
                editor.apply {
                    if (hasJavaSupport) {
                        waitingAssertEquals("Edit doesn't have expected language highlighting", "Language: JAVA") {
                            callJs(
                                runInEdt = true, script = """
                        importPackage(com.intellij.openapi.fileEditor.impl.text)
                        importPackage(com.intellij.openapi.project.ex)
                        importPackage(com.intellij.psi)
                        const project = ProjectManagerEx.getInstance().openProjects[0]
                        const psiDocumentManager = PsiDocumentManager.getInstance(project);
                        const document = psiDocumentManager.getPsiFile(local.get('editor').getDocument())
                        if (document == null) {
                          "Plain text"      
                        } else {
                          document
                              .getLanguage()
                              .toString()
                        }
                    """.trimIndent()
                            )
                        }
                    }

                    sleep(5_000)
                    click(Point(5, 5))
                    sleep(5_000)

                    keyboard {
                        selectAll()

                        // Escaping is required due to how enterText is implemented
                        enterText(
                            StringEscapeUtils.escapeEcmaScript(selector)
                                .replace("\\\"", "\"")
                        )
                    }
                }

                if (hasJavaSupport) {
                    val popupItems = waitFor(Duration.ofSeconds(10), functionWithCondition = {
                        val items: List<String> = editor.callJs(
                            runInEdt = true, script = """
                        importPackage(com.intellij.codeInsight.lookup)
                        try {
                            const model = LookupManager.getActiveLookup(local.get('editor')).getList().getModel()
                            const listItems = new ArrayList();
                            for(let i = 0; i < model.size; ++i) { 
                                listItems.add(model.getElementAt(i).toString())
                            }
                            listItems
                        } catch {
                            new ArrayList()
                        }
                    """.trimIndent()
                        )
                        (items.isNotEmpty() && items.contains("resourceId")) to items
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

                assertEquals(expectedLineCount, editor.getLineCount())
            }

        }
    }
}

private fun EditorFixture.getLineCount(): Int {
    return callJs(
        runInEdt = true, script = """
            const document = local.get('document')
            document.getLineCount()
                    """.trimIndent()
    )
}
