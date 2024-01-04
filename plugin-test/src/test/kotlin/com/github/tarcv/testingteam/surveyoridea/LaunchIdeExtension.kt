package com.github.tarcv.testingteam.surveyoridea

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.launcher.Ide
import com.intellij.remoterobot.launcher.IdeDownloader
import com.intellij.remoterobot.launcher.IdeLauncher.launchIde
import com.intellij.remoterobot.launcher.Os
import com.intellij.remoterobot.steps.CommonSteps
import okhttp3.OkHttpClient
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL
import java.io.File
import java.lang.Thread.sleep
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

private var started = false

private val requestedIdeCode: String
    get() = getEnvValue("IDE_CODE")

private fun getEnvValue(key: String) = requireNotNull(System.getenv(key)) {
    "$key environment variable should be set"
}

val hasJavaSupport: Boolean
    get() = when (requestedIdeCode) {
        "AI", "AQ", "IC", "IU" -> true
        else -> false
    }

class LaunchIdeExtension : BeforeAllCallback, ExtensionContext.Store.CloseableResource {
    private lateinit var process: Process
    private lateinit var tempDir: Path

    override fun beforeAll(context: ExtensionContext) {
        if (started) {
            return
        }

        tempDir = Files.createTempDirectory("junit")

        val requestedIdeCode = requestedIdeCode
        val requestedIdeVersion = getEnvValue("IDE_VERSION")

        val ideDownloader = IdeDownloader(OkHttpClient())
        val cacheDir = getCacheRoot(tempDir)
            .resolve("surveyor-idea-test")
            .apply { Files.createDirectories(this) }
        println("Caching downloaded files at $cacheDir")

        val pathToIde: Path = with(requestedIdeCode) {
            val ide = Ide.values().singleOrNull { it.code == this } ?: error("'$this' code is not supported")
            ideDownloader.getIde(ide, requestedIdeVersion, cacheDir)
        }

        // TODO: Remove this once https://github.com/JetBrains/intellij-ui-test-robot/issues/387 is fixed
        workaroundRemoteIdeIssue(pathToIde)

        val pluginPath = requireNotNull(System.getenv("PLUGIN_PATH")) {
            "PLUGIN_PATH environment variable should be set"
        }.let { Paths.get(it) }
        process = launchIde(
            pathToIde = pathToIde,
            additionalProperties = mapOf(
                "robot-server.port" to "8082",
                "ide.mac.file.chooser.native" to "false",
                "ide.mac.message.dialogs.as.sheets" to "false",
                "ide.show.tips.on.startup.default.value" to "false",
                "idea.trust.all.projects" to "true",
                "jb.consents.confirmation.enabled" to "false",
                "jb.privacy.policy.text" to "<!--999.999-->",

                // Disable native menus on Mac:
                "apple.laf.useScreenMenuBar" to false,
                "jbScreenMenuBar.enabled" to false,

                // Disable 'Code with Me' tooltip:
                "idea.suppressed.plugins.id" to "com.jetbrains.codeWithMe",
            ),
            additionalVmOptions = emptyList(),
            requiredPluginsArchives = listOf(
                ideDownloader.getRobotPlugin(cacheDir),
                pluginPath
            ),
            ideSandboxDir = tempDir.resolve("sandbox").apply { Files.createDirectories(this) }
        )

        started = true
        context.root.getStore(GLOBAL).put(LaunchIdeExtension::class.java.name, this)
    }

    private fun workaroundRemoteIdeIssue(pathToIde: Path) {
        val binDir = when (Os.hostOS()) {
            Os.MAC -> pathToIde.resolve("Contents").resolve("bin")
            else -> pathToIde.resolve("bin")
        }
        Files.list(binDir)
            .filter {
                it.fileName.toString().endsWith(".vmoptions")
                        && it.fileName.toString().contains("_client")
            }
            .forEach {
                println("Removing unsupported $it")
                Files.delete(it)
            }
    }

    override fun close() {
        try {
            kotlin.runCatching {
                CommonSteps(RemoteRobot("http://127.0.0.1:8082"))
                    .invokeAction("Exit")
            }
            if (!process.isAlive) { return }

            sleep(5_000)

            process.destroy()
            if (!process.isAlive) { return }

            sleep(5_000)

            // Only ProcessHandle correctly terminates a process on Win
            process.toHandle().destroyForcibly()
        } finally {
            tempDir.toFile().deleteRecursively()
        }
    }

    private fun getCacheRoot(defaultDir: Path): Path {
        val strPath = if (Os.hostOS() == Os.WINDOWS) {
            System.getenv("TEMP")
        } else {
            System.getenv("XDG_CACHE_HOME")
                ?: System.getenv("HOME")?.let { "$it/.cache" }
        }
        return if (strPath != null) {
            Paths.get(strPath)
        } else {
            defaultDir
        }
    }

    private fun IdeDownloader.getIde(ide: Ide, requestedIdeVersion: String, cacheDir: Path): Path {
        val ideCacheDir = cacheDir
            .resolve("${ide.code}-$requestedIdeVersion")
            .apply { Files.createDirectories(this) }
        val previousArchive = ideCacheDir.toFile().listFiles { f: File -> f.isFile && !f.isHidden }?.singleOrNull()

        val previousExtractedDir = getExtractedIdeDir(ideCacheDir)
        if (previousExtractedDir != null) {
            println("File was already downloaded and extracted, so using $previousExtractedDir")
            return previousExtractedDir.toPath()
        }

        return try {
            val extractedPath = when (requestedIdeVersion) {
                "LATEST_EAP" -> downloadAndExtractLatestEap(ide, ideCacheDir)
                else -> downloadAndExtract(
                    ide, ideCacheDir,
                    Ide.BuildType.RELEASE, requestedIdeVersion
                )
            }
            if (previousArchive != null) {
                println("Found a stale archive from the previous download, deleting...")
                previousArchive.delete()
                println("Deleted")
            }

            extractedPath
        } catch (e: FileAlreadyExistsException) {
            val extractedDir = getExtractedIdeDir(ideCacheDir)
                ?.toPath()
                ?: error("Archive was already downloaded, but extracted dir wasn't found. Please fix the cache.")
            println("File was already downloaded, so using $extractedDir")
            extractedDir
        }
    }

    private fun getExtractedIdeDir(ideCacheDir: Path): File? {
        val candidateDirs = ideCacheDir.toFile().listFiles { f: File -> f.isDirectory }
            ?: error("$ideCacheDir should be a directory")
        return when(candidateDirs.size) {
            0 -> null
            1 -> candidateDirs.single()
            else -> error("Found multiple extracted directories under $ideCacheDir, but expected no more then one")
        }
    }

    private fun IdeDownloader.getRobotPlugin(cacheDir: Path) = try {
        downloadRobotPlugin(cacheDir)
    } catch (e: FileAlreadyExistsException) {
        println("File was already downloaded, so using ${e.file}")
        Paths.get(e.file)
    }
}