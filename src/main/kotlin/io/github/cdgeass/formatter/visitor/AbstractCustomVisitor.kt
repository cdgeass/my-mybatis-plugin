package io.github.cdgeass.formatter.visitor

import io.github.cdgeass.constants.StringConstants

/**
 * jsalparser visitor 抽象
 * 计算换行缩进
 *
 * @author cdgeass
 * @since 2021-03-03
 */
abstract class AbstractCustomVisitor(
    private val level: Int,
    private val tab: String = StringConstants.TAB_CHARACTER.repeat(level),
    private val preTab: String = StringConstants.TAB_CHARACTER.repeat((level - 1).coerceAtLeast(0)),
    private val stringBuilder: StringBuilder = StringBuilder()
) {

    fun append(statement: String): AbstractCustomVisitor {
        stringBuilder.append(statement)
        return this
    }

    fun appendTab(linefeed: Boolean = true): AbstractCustomVisitor {
        if (linefeed && stringBuilder.isNotBlank()) {
            stringBuilder.append(LINE_SEPARATOR)
        }
        if (level != 0) {
            stringBuilder.append(tab)
        }
        return this
    }

    fun appendPreTab(): AbstractCustomVisitor {
        stringBuilder.append(preTab)
        return this
    }

    fun currentLevel(): Int {
        return level
    }

    fun nextLevel(): Int {
        return level + 1
    }

    override fun toString(): String {
        return stringBuilder.toString()
    }

}