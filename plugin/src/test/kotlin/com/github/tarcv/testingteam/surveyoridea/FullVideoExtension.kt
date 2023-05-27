package com.github.tarcv.testingteam.surveyoridea

import com.automation.remarks.video.RecorderFactory
import com.automation.remarks.video.recorder.IVideoRecorder
import com.automation.remarks.video.recorder.VideoRecorder
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.Random

class FullVideoExtension : BeforeAllCallback, AfterAllCallback {
    private lateinit var recorder: IVideoRecorder
    override fun beforeAll(context: ExtensionContext) {
        recorder = RecorderFactory.getRecorder(VideoRecorder.conf().recorderType()).apply {
            start()
        }
    }

    override fun afterAll(context: ExtensionContext) {
        val fileName = context.testClass
            .map { obj: Class<*> -> obj.name }
            .orElse("UnknownClass" + Random().nextInt())
        recorder.stopAndSave(fileName)
    }
}