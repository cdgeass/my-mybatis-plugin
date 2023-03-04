package io.github.cdgeass.formatter

import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.lang.Language
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.sql.psi.SqlLanguage
import com.intellij.ui.EditorTextField
import com.intellij.ui.LanguageTextField
import org.apache.commons.lang.StringUtils
import java.awt.Dimension

/**
 * @author cdgeass
 * @since 2021-03-09
 */
val SEPARATOR_TEXT_ATTRIBUTES_KEY = TextAttributesKey.createTextAttributesKey("MY_MYBATIS::MYBATIS_LOG_SEPARATOR")
val TEXT_ATTRIBUTES_KEY_1 = TextAttributesKey.createTextAttributesKey("MY_MYBATIS::MYBATIS_LOG_TEXT_1")
val TEXT_ATTRIBUTES_KEY_2 = TextAttributesKey.createTextAttributesKey("MY_MYBATIS::MYBATIS_LOG_TEXT_2")

const val SEPARATOR_LINE = "-- -----------------------------------"
const val LINE_SEPARATOR = "\n"

/**
 * 空编辑框
 */
fun editorTextField(
    language: Language? = null,
    project: Project? = null,
    text: String = "",
    dimension: Dimension? = null
): EditorTextField {
    // 根据当前主题设置编辑框字体
    val editorColorsManager = EditorColorsManager.getInstance()
    val font = editorColorsManager.schemeForCurrentUITheme.getFont(EditorFontType.PLAIN)

    val editorTextField: EditorTextField = if (language != null && project != null) {
        LanguageTextField(language, project, text, false)
    } else {
        EditorTextField(text).apply { this.setOneLineMode(false) }
    }
    if (dimension != null) {
        editorTextField.preferredSize = dimension
    }
    editorTextField.font = font
    editorTextField.setCaretPosition(0)

    editorTextField.addSettingsProvider { editor ->
        editor.setHorizontalScrollbarVisible(true)
        editor.setVerticalScrollbarVisible(true)

        // 设置成 RendererMode 时, vim 插件会在右上角出现 ReloadVimRc 的浮动按钮
//        if (!editable) {
//            editor.isRendererMode = true
//            editor.setCaretEnabled(false)
//        }
    }

    return editorTextField
}

/**
 * 清除编辑框内容
 */
fun EditorTextField.clean(): EditorTextField {
    this.text = ""
    return this
}

/**
 * 格式化 SQL 并高亮
 */
fun EditorTextField.format(project: Project, text: String? = null): EditorTextField {
    var sql = text ?: this.text
    if (canFormat(sql)) {
        sql = format(sql).joinToString("$LINE_SEPARATOR$SEPARATOR_LINE$LINE_SEPARATOR")

        // 使用 codeStyle 进行 Reformat
        val psiFileFromText = PsiFileFactory.getInstance(project).createFileFromText(SqlLanguage.INSTANCE, sql)
        WriteCommandAction.runWriteCommandAction(project) {
            CodeStyleManager.getInstance(project).reformat(psiFileFromText)
        }
        this.text = psiFileFromText.text

        this.highlight(project)
    }
    return this
}

/**
 * 文本框初未始化完成时没有持有 editor 需要提供 SettingsProvider 在初始化中进行高亮
 * 初始化完成后可直接操作 editor 进行高亮
 */
fun EditorTextField.highlight(project: Project): EditorTextField {
    if (this.editor != null) {
        highlight(this.editor!!, project)
    } else {
        this.addSettingsProvider { editor -> highlight(editor, project) }
    }
    return this
}

/**
 * SQL 高亮
 */
fun highlight(editor: Editor, project: Project) {
    val highlightManager = HighlightManager.getInstance(project)

    val text = editor.document.text
    val length = text.length
    var offset = 0
    var i = 1
    val lineCount = StringUtils.countMatches(text, SEPARATOR_LINE) + 1
    while (i <= lineCount) {
        val index: Int = if (i != lineCount) {
            StringUtils.ordinalIndexOf(text, SEPARATOR_LINE, i)
        } else {
            length
        }

        // sql 高亮 颜色切换
        highlightManager.addRangeHighlight(
            editor, offset, index,
            if ((i) % 2 == 0) TEXT_ATTRIBUTES_KEY_1 else TEXT_ATTRIBUTES_KEY_2, false, null
        )

        // 分割线 最后一条 sql 后无分割线
        highlightManager.addRangeHighlight(
            editor, index, (index + SEPARATOR_LINE.length + 1).coerceAtMost(length),
            SEPARATOR_TEXT_ATTRIBUTES_KEY, false, null
        )

        offset = index + SEPARATOR_LINE.length + 1
        i++
    }
}
