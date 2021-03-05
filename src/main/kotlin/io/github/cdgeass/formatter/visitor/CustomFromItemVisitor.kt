package io.github.cdgeass.formatter.visitor

import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.*

/**
 * @author cdgeass
 * @since 2021-03-05
 */
class CustomFromItemVisitor(level: Int) : AbstractCustomVisitor(level), FromItemVisitor {

    override fun visit(tableName: Table?) {
        val tableName = tableName ?: return

        appendTab().append(tableName.toString())
    }

    override fun visit(subSelect: SubSelect?) {
        val subSelect = subSelect ?: return

        if (subSelect.isUseBrackets) {
            append("(")
        }
        if (subSelect.withItemsList.isNotEmpty()) {
            appendTab().append("WITH ").append(subSelect.withItemsList.joinToString(",") { toString() })
        }

        append(CustomSelectVisitor(currentLevel()).apply { subSelect.selectBody.accept(this) }.toString())
        if (subSelect.isUseBrackets) {
            appendPreTab().append(")")
        }

        if (subSelect.alias != null) {
            append(subSelect.alias.toString())
        }
        if (subSelect.pivot != null) {
            appendTab().append(subSelect.pivot.toString())
        }
        if (subSelect.unPivot != null) {
            appendTab().append(subSelect.unPivot.toString())
        }
    }

    override fun visit(subjoin: SubJoin?) {
        val subjoin = subjoin ?: return

        appendTab().append("(").append(subjoin.left.toString())
        subjoin.joinList.forEach {
            if (it.isSimple) {
                append(", ").append(join(it, currentLevel()))
            } else {
                append(" ").append(join(it, currentLevel()))
            }
        }
        appendTab().append(")")

        if (subjoin.alias != null) {
            append(subjoin.alias.toString())
        }
        if (subjoin.pivot != null) {
            append(subjoin.pivot.toString())
        }
        if (subjoin.unPivot != null) {
            append(subjoin.unPivot.toString())
        }
    }

    override fun visit(lateralSubSelect: LateralSubSelect?) {
        val lateralSubSelect = lateralSubSelect ?: return

        appendTab().append(CustomFromItemVisitor(currentLevel()).apply { lateralSubSelect.accept(this) }.toString())
    }

    override fun visit(valuesList: ValuesList?) {
        val valuesList = valuesList ?: return

        appendTab()
            .append("(VALUES ")
            .append(
                valuesList.multiExpressionList.expressionLists.joinToString(",") { exprList ->
                    getStringList(exprList.expressions, true, !valuesList.isNoBrackets)
                }
            )
            .append(")")

        if (valuesList.alias != null) {
            append(valuesList.alias.toString())

            if (valuesList.columnNames.isNotEmpty()) {
                appendTab().append("(").append(valuesList.columnNames.joinToString(",")).append(")")
            }
        }
    }

    override fun visit(tableFunction: TableFunction?) {
        val tableFunction = tableFunction ?: return

        appendTab().append(CustomFromItemVisitor(currentLevel()).apply { tableFunction.accept(this) }.toString())
    }

    override fun visit(aThis: ParenthesisFromItem?) {
        val aThis = aThis ?: return

        appendTab()
            .append("(")
            .append(CustomFromItemVisitor(currentLevel()).apply { aThis.fromItem.accept(this) }.toString())
            .append(")")

        if (aThis.alias != null) {
            append(aThis.alias.toString())
        }
    }
}