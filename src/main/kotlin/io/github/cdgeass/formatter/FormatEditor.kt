package io.github.cdgeass.formatter

import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorTextField
import org.apache.commons.lang.StringUtils
import java.awt.Dimension

/**
 * @author cdgeass
 * @since 2021-03-09
 */
val SEPARATOR_TEXT_ATTRIBUTES_KEY = TextAttributesKey.createTextAttributesKey("MY_MYBATIS::MYBATIS_LOG_SEPARATOR")
val TEXT_ATTRIBUTES_KEY_1 = TextAttributesKey.createTextAttributesKey("MY_MYBATIS::MYBATIS_LOG_TEXT_1")
val TEXT_ATTRIBUTES_KEY_2 = TextAttributesKey.createTextAttributesKey("MY_MYBATIS::MYBATIS_LOG_TEXT_2")

private const val SEPARATOR_LINE = "-- -----------------------------------"
private const val LINE_SEPARATOR = "\n"

fun editorTextField(project: Project, selectedText: String): EditorTextField {
    val sqlList = format(selectedText)

    val editorTextField = EditorTextField(
        sqlList.joinToString(LINE_SEPARATOR + SEPARATOR_LINE + LINE_SEPARATOR)
    ).apply {
        this.preferredSize = Dimension(500, 450)
        this.setCaretPosition(0)
        this.addSettingsProvider { editor ->
            editor.setHorizontalScrollbarVisible(true)
            editor.setVerticalScrollbarVisible(true)
            editor.isOneLineMode = false
            editor.isRendererMode = true
            editor.setCaretEnabled(false)
        }
    }

    // 根据当前主题设置编辑框字体
    val editorColorsManager = EditorColorsManager.getInstance()
    val font = editorColorsManager.schemeForCurrentUITheme.getFont(EditorFontType.PLAIN)
    editorTextField.font = font

    // 文本高亮
    editorTextField.addSettingsProvider { editor ->
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

    return editorTextField
}