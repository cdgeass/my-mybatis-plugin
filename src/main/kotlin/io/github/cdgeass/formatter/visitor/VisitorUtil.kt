package io.github.cdgeass.formatter.visitor

import io.github.cdgeass.constants.StringConstants
import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.statement.select.Join
import net.sf.jsqlparser.statement.select.OrderByElement
import net.sf.jsqlparser.statement.select.PlainSelect

/**
 * @author cdgeass
 * @since 2021-03-05
 */

fun getStringList(list: List<Any>, useComma: Boolean, useBrackets: Boolean): String {
    return getStringList(list, useComma, useBrackets, false, 0)
}

fun getStringList(list: List<Any>, useComma: Boolean, useBrackets: Boolean, useLineBreak: Boolean, level: Int): String {
    val ans = StringBuilder()
    var comma = ","
    val lineBreak = if (useLineBreak) System.lineSeparator() else ""
    val tabCharacter = StringConstants.TAB_CHARACTER.repeat(level.coerceAtLeast(0))
    if (!useComma) {
        comma = ""
    }

    if (useBrackets) {
        ans.append("(").append(lineBreak).append(tabCharacter)
    }
    list.forEachIndexed { i: Int, item: Any ->
        if (i != 0) {
            ans.append(lineBreak).append(tabCharacter).append(comma)
        }
        ans.append(item)
    }
    if (useBrackets) {
        ans.append(lineBreak).append(tabCharacter).append(")")
    }
    return ans.toString()
}

fun orderByToString(oracleSiblings: Boolean, orderByElements: List<OrderByElement>): String {
    val sql = getStringList(orderByElements, useComma = true, useBrackets = false)

    return if (sql.isNotEmpty()) {
        (if (oracleSiblings) "ORDER SIBLINGS BY" else "ORDER BY") + " " + sql
    } else sql
}

fun join(join: Join, level: Int): String {
    val tabCharacter = StringConstants.TAB_CHARACTER.repeat(Math.max(0, level))
    val customFromItemSelectVisitor = CustomFromItemVisitor(level + 1)
    return if (join.isSimple && join.isOuter) {
        join.rightItem.accept(customFromItemSelectVisitor)
        "OUTER $customFromItemSelectVisitor"
    } else if (join.isSimple) {
        join.rightItem.accept(customFromItemSelectVisitor)
        "" + customFromItemSelectVisitor
    } else {
        var type: String = System.lineSeparator() + tabCharacter
        when {
            join.isRight -> {
                type += "RIGHT "
            }
            join.isNatural -> {
                type += "NATURAL "
            }
            join.isFull -> {
                type += "FULL "
            }
            join.isLeft -> {
                type += "LEFT "
            }
            join.isCross -> {
                type += "CROSS "
            }
        }
        when {
            join.isOuter -> {
                type += "OUTER "
            }
            join.isInner -> {
                type += "INNER "
            }
            join.isSemi -> {
                type += "SEMI "
            }
        }
        when {
            join.isStraight -> {
                type = "STRAIGHT_JOIN "
            }
            join.isApply -> {
                type += "APPLY "
            }
            else -> {
                type += "JOIN "
            }
        }
        join.rightItem.accept(customFromItemSelectVisitor)
        type += customFromItemSelectVisitor
        if (join.joinWindow != null) {
            type += " WITHIN" + join.joinWindow
        }
        if (join.onExpression != null) {
            type += StringConstants.LINE_BREAK + tabCharacter + "ON " + join.onExpression
        }
        type += PlainSelect.getFormatedList(join.usingColumns, "USING", true, true)
        type
    }
}

fun expression(expression: Expression, level: Int): String {
    val tabCharacter = StringConstants.TAB_CHARACTER.repeat(level.coerceAtLeast(0))
    if (expression is BinaryExpression) {
        val leftExpression = expression.leftExpression
        val rightExpression = expression.rightExpression
        var stringExpression = expression.stringExpression
        if (StringConstants.AND == stringExpression) {
            stringExpression = System.lineSeparator() + tabCharacter + stringExpression + " "
        }
        return expression((leftExpression as BinaryExpression), level) + stringExpression + rightExpression
    }
    return expression.toString()
}