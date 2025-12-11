package org.intellij.lang.jflex.fileTypes

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import org.intellij.lang.jflex.JFlexLanguage
import org.intellij.lang.jflex.util.JFlexBundle
import javax.swing.Icon

object JFlexFileType : LanguageFileType(JFlexLanguage.LANGUAGE) {

    const val DEFAULT_EXTENSION = "flex"

    val ICON: Icon by lazy {
        IconLoader.getIcon("/icons/fileTypes/jflex.png", JFlexFileType::class.java)
    }

    override fun getName(): String = "JFlex"

    override fun getDescription(): String =
        try {
            JFlexBundle.message("jflex.filetype.description")
        } catch (e: Exception) {
            "JFlex file" // 资源加载失败时的默认描述
        }

    override fun getDefaultExtension(): String = DEFAULT_EXTENSION

    override fun getIcon(): Icon = ICON
}
