package com.github.tarcv.testingteam.surveyoridea

import com.automation.remarks.video.RecorderFactory
import com.automation.remarks.video.recorder.IVideoRecorder
import com.automation.remarks.video.recorder.VideoRecorder
import com.intellij.util.containers.orNull
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

@Suppress("unused") // This extension is useful for occasional CI debugging
class ClassVideoExtension : BeforeAllCallback, AfterAllCallback {
    private lateinit var recorder: IVideoRecorder

    override fun beforeAll(context: ExtensionContext) {
        recorder = RecorderFactory.getRecorder(VideoRecorder.conf().recorderType()).apply {
            start()
        }
    }

    override fun afterAll(context: ExtensionContext) {
        val fileName = context.testClass.orNull()
            ?.simpleName
            ?: "Unknown${System.currentTimeMillis()}"
        recorder.stopAndSave(fileName)
    }
}