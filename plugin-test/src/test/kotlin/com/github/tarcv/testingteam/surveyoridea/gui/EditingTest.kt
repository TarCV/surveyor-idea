package com.github.tarcv.testingteam.surveyoridea.gui

import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.locateElementToolWindow
import com.github.tarcv.testingteam.surveyoridea.hasJavaSupport
import com.intellij.remoterobot.client.IdeaSideException
import com.intellij.remoterobot.fixtures.EditorFixture
import com.intellij.remoterobot.utils.keyboard
import com.intellij.remoterobot.utils.waitFor
import org.apache.commons.text.StringEscapeUtils
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

    private fun assertDroidSelectorEditing(selector: String, expectedLineCount: Int) = with(remoteRobot) {
        idea {
            openFileInTestProject(droidAutomatorSnapshotFile, "editorWithSnapshot")

            selectInMenuBar(
                "View",
                "Tool Windows",
                "Locate Element"
            )

            locateElementToolWindow {
                // Escaping is required due to simple concatenation in the text#set implementation
                editor.apply {
                    if (hasJavaSupport) {
                        val editorLanguage = callJs<String>(
                            runInEdt = true, script = """
                        importPackage(com.intellij.openapi.fileEditor.impl.text)
                        importPackage(com.intellij.openapi.project.ex)
                        importPackage(com.intellij.psi)
                        const project = ProjectManagerEx.getInstance().openProjects[0]
                        const psiDocumentManager = PsiDocumentManager.getInstance(project);
                        psiDocumentManager.getPsiFile(local.get('editor').getDocument())
                          .getLanguage()
                          .toString()
                    """.trimIndent()
                        )
                        assertEquals("Language: JAVA", editorLanguage)
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
                        val items: List<String> = try {
                            editor.callJs(
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
                        } catch (e: IdeaSideException) {
                            emptyList()
                        }
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
                    """.trimIndent())
}
