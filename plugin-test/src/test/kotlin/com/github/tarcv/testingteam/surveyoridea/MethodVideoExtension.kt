package com.github.tarcv.testingteam.surveyoridea

import com.automation.remarks.video.RecorderFactory
import com.automation.remarks.video.recorder.IVideoRecorder
import com.automation.remarks.video.recorder.VideoRecorder
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

class MethodVideoExtension : BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private lateinit var recorder: IVideoRecorder

    override fun beforeTestExecution(context: ExtensionContext) {
        recorder = RecorderFactory.getRecorder(VideoRecorder.conf().recorderType()).apply {
            start()
        }
    }

    override fun afterTestExecution(context: ExtensionContext) {
        val className = context.testClass.orElse(null)
            ?.simpleName
        val methodName = context.testMethod.orElse(null)
            ?.name
        val filename = if (className != null && methodName != null) {
            "${className}_$methodName"
        } else {
            "Unknown_unknown${System.currentTimeMillis()}"
        }
        recorder.stopAndSave(filename)
    }
}