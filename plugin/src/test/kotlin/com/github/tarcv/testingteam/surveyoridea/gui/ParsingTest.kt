package com.github.tarcv.testingteam.surveyoridea.gui

import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.idea
import com.github.tarcv.testingteam.surveyoridea.gui.fixtures.locateElementToolWindow
import com.intellij.remoterobot.client.IdeaSideException
import com.intellij.remoterobot.utils.keyboard
import com.intellij.remoterobot.utils.waitFor
import org.apache.commons.lang.StringEscapeUtils
import org.junit.jupiter.api.Test
import java.awt.Point
import java.lang.Thread.sleep
import java.time.Duration
import kotlin.test.assertEquals

class ParsingTest : BaseTestProjectTests() {
    @Test
    fun testDroidSelectorParsing() = with(remoteRobot) {
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

                    sleep(5_000)
                    click(Point(5, 5))
                    sleep(5_000)

                    keyboard {
                        selectAll()

                        // Escaping is required due to how enterText is implemented
                        enterText(
                            StringEscapeUtils.escapeJavaScript("""new UiSelector().""")
                                .replace("\\\"", "\"")
                        )
                    }
                }

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

        }
    }
}