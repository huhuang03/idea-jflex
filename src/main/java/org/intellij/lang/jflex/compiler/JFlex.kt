package org.intellij.lang.jflex.compiler

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.LogLevel
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.io.StreamUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import org.intellij.lang.jflex.options.JFlexConfigurable
import org.intellij.lang.jflex.options.JFlexSettings
import org.intellij.lang.jflex.util.JFlexBundle
import java.io.File
import java.io.IOException
import java.util.regex.Pattern

object JFlex {
    private val LINE_NUMBER_PATTERN = Pattern.compile(".*?\\(line\\s(\\d+)\\):\\s*?$")
    private const val JFLEX_MAIN_CLASS = "JFlex.Main"
    private const val JFLEX_JAR_PATH = "lib/JFlex.jar"
    private const val OPTION_SKEL = " --skel "
    private const val OPTION_D = " -d "
    private const val OPTION_QUIET = " --quiet "
    private const val OPTION_Q = " -q "

    fun compile(file: VirtualFile, projectSdk: Sdk): Map<LogLevel, List<JFlexMessage>> {
        val settings = JFlexSettings.getInstance()

        val command = mutableListOf<String>()

        // 使用 java 执行 JFlex.Main
        val javaHome = projectSdk.homePath ?: throw IOException("Project SDK home path is null")
        val javaExe = if (SystemInfo.isWindows) "java.exe" else "java"
        command.add(File(javaHome, "bin/$javaExe").absolutePath)
        command.add("-cp")
        command.add(File(settings.JFLEX_HOME, JFLEX_JAR_PATH).absolutePath)
        command.add(JFLEX_MAIN_CLASS)

        if (!StringUtil.isEmptyOrSpaces(settings.COMMAND_LINE_OPTIONS)) {
            command.addAll(settings.COMMAND_LINE_OPTIONS.split("\\s+".toRegex()))
        }

        if (!StringUtil.isEmptyOrSpaces(settings.SKELETON_PATH) && settings.COMMAND_LINE_OPTIONS.indexOf(OPTION_SKEL) == -1) {
            command.add(OPTION_SKEL.trim())
            command.add(settings.SKELETON_PATH)
        }

        if (settings.COMMAND_LINE_OPTIONS.indexOf(OPTION_Q) == -1 && settings.COMMAND_LINE_OPTIONS.indexOf(OPTION_QUIET) == -1) {
            command.add(OPTION_QUIET.trim())
        }

        file.parent?.let {
            command.add(OPTION_D.trim())
            command.add(it.path)
        }

        command.add(file.path)

        val processBuilder = ProcessBuilder(command)
        processBuilder.directory(File(settings.JFLEX_HOME))

        val process = processBuilder.start()
        try {
            @Suppress("DEPRECATION")
            val output = StreamUtil.readText(process.inputStream)
            @Suppress("DEPRECATION")
            val error = StreamUtil.readText(process.errorStream)

            val infoMessages = mutableListOf<JFlexMessage>()
            val errorMessages = mutableListOf<JFlexMessage>()
            filter(output, infoMessages, errorMessages)
            filter(error, infoMessages, errorMessages)

            val messages = mutableMapOf(
                LogLevel.ERROR to errorMessages, LogLevel.INFO to infoMessages
            )

            val exitCode = process.waitFor()
            if (exitCode != 0 && errorMessages.isEmpty()) {
                throw IOException(
                    JFlexBundle.message(
                        "command.0.execution.failed.with.exit.code.1", command.joinToString(" "), exitCode
                    )
                )
            }

            return messages
        } finally {
            process.destroy()
        }
    }

    private fun filter(output: String?, information: MutableList<JFlexMessage>, error: MutableList<JFlexMessage>) {
        if (!output.isNullOrBlank()) {
            val lines = output.lines()
            var i = 0
            while (i < lines.size) {
                val line = lines[i]
                if (line.startsWith("Error in file") && i + 3 < lines.size) {
                    val message = lines[++i]
                    var lineNumber = -1
                    var columnNumber = -1
                    val matcher = LINE_NUMBER_PATTERN.matcher(line)
                    if (matcher.matches()) {
                        lineNumber = matcher.group(1).toIntOrNull() ?: -1
                    }
                    i++
                    val columnPointer = lines[++i].toCharArray()
                    for (j in columnPointer.indices) {
                        if (columnPointer[j] != ' ') {
                            columnNumber = if (columnPointer[j] == '^') j + 1 else -1
                            break
                        }
                    }
                    error.add(JFlexMessage(message, lineNumber, columnNumber))
                } else if (!line.startsWith("Reading skeleton file")) {
                    information.add(JFlexMessage(line))
                }
                i++
            }
        }
    }

    fun validateConfiguration(project: Project): Boolean {
        val settings = JFlexSettings.getInstance()
        val home = File(settings.JFLEX_HOME)
        if (!home.isDirectory || !home.exists()) {
            return showWarningMessageAndConfigure(project, JFlexBundle.message("jflex.home.path.is.invalid"))
        }

        if (!StringUtil.isEmptyOrSpaces(settings.SKELETON_PATH) && settings.COMMAND_LINE_OPTIONS.indexOf(OPTION_SKEL) == -1) {
            val skel = File(settings.SKELETON_PATH)
            if (!skel.isFile || !skel.exists()) {
                return showWarningMessageAndConfigure(project, JFlexBundle.message("jflex.skeleton.file.was.not.found"))
            }
        }

        val jarFile = File(settings.JFLEX_HOME, JFLEX_JAR_PATH)
        if (!jarFile.isFile || !jarFile.exists()) {
            return showWarningMessageAndConfigure(project, JFlexBundle.message("jar.not.found", JFLEX_JAR_PATH))
        }

        return true
    }

    private fun showWarningMessageAndConfigure(project: Project, message: String): Boolean {
        Messages.showWarningDialog(project, message, JFlexBundle.message("jflex"))
        ApplicationManager.getApplication().invokeLater {
            ShowSettingsUtil.getInstance().editConfigurable(project, JFlexConfigurable())
        }
        return false
    }

    fun isCompilationEnabled(): Boolean = JFlexSettings.getInstance().ENABLED_COMPILATION
}
