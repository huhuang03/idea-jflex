package org.intellij.lang.jflex.compiler

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import org.intellij.lang.jflex.fileTypes.JFlexFileType

/**
 * @author wenfan.hu
 */
class JFlexStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        val connection = project.messageBus.connect()
        connection.subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun after(events: List<VFileEvent>) {
                for (event in events) {
                    val file = event.file ?: continue
                    if (file.fileType == JFlexFileType) {
                        // 这里已经有 project，上下文中直接使用
                        ProjectRootManager.getInstance(project).projectSdk?.apply {
                            JFlex.compile(file, this)
                        }
                    }
                }
            }
        })
    }
}