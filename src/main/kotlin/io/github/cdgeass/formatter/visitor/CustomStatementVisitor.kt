package io.github.cdgeass.formatter.visitor

import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.statement.*
import net.sf.jsqlparser.statement.alter.Alter
import net.sf.jsqlparser.statement.alter.sequence.AlterSequence
import net.sf.jsqlparser.statement.comment.Comment
import net.sf.jsqlparser.statement.create.index.CreateIndex
import net.sf.jsqlparser.statement.create.schema.CreateSchema
import net.sf.jsqlparser.statement.create.sequence.CreateSequence
import net.sf.jsqlparser.statement.create.synonym.CreateSynonym
import net.sf.jsqlparser.statement.create.table.CreateTable
import net.sf.jsqlparser.statement.create.view.AlterView
import net.sf.jsqlparser.statement.create.view.CreateView
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.drop.Drop
import net.sf.jsqlparser.statement.execute.Execute
import net.sf.jsqlparser.statement.grant.Grant
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.merge.Merge
import net.sf.jsqlparser.statement.replace.Replace
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.show.ShowTablesStatement
import net.sf.jsqlparser.statement.truncate.Truncate
import net.sf.jsqlparser.statement.update.Update
import net.sf.jsqlparser.statement.upsert.Upsert
import net.sf.jsqlparser.statement.values.ValuesStatement

/**
 * @author cdgeass
 * @since  2021-03-03
 */
class CustomStatementVisitor(level: Int) : AbstractCustomVisitor(level), StatementVisitor {

    override fun visit(comment: Comment?) {
        val comment = comment ?: return
        appendTab().append(comment.toString())
    }

    override fun visit(commit: Commit?) {
        val commit = commit ?: return
        appendTab().append(commit.toString())
    }

    override fun visit(delete: Delete?) {
        val delete = delete ?: return

        append("DELETE ")

        if (delete.tables.isNotEmpty()) {
            append(" " + delete.tables.joinToString(", ") { it.toString() })
        }

        appendTab().append("FROM ").append(delete.table.toString())

        delete.joins.forEach { join ->
            if (join.isSimple) {
                append(", ").append(join(join, currentLevel()))
            } else {
                appendTab().append(join(join, nextLevel()))
            }
        }

        if (delete.where != null) {
            appendTab().append("WHERE ").append(delete.where.toString())
        }

        if (delete.orderByElements.isNotEmpty()) {
            appendTab().append(PlainSelect.orderByToString(delete.orderByElements))
        }

        if (delete.limit != null) {
            appendTab().append(delete.limit.toString())
        }
    }

    override fun visit(update: Update?) {
        val update = update ?: return

        appendTab().append("UPDATE ").append(update.table.toString())

        update.startJoins.forEach { startJoin ->
            if (startJoin.isSimple) {
                append(", ").append(join(startJoin, currentLevel()))
            } else {
                appendTab().append(join(startJoin, nextLevel()))
            }
        }

        appendTab().append("SET ")
        if (!update.isUseSelect) {
            append(
                update.columns.mapIndexed { index: Int, column: Column ->
                    column.toString() + " = " + update.expressions[index].toString()
                }.joinToString(",")
            )
        } else {
            if (update.isUseColumnsBrackets) {
                append("(")
            }
            append(update.columns.joinToString(",") { toString() })
            if (update.isUseColumnsBrackets) {
                append(")")
            }

            append(" = ")

            append("(")
            append(CustomStatementVisitor(nextLevel()).apply { update.select.accept(this) }.toString())
            append(")")
        }

        if (update.fromItem != null) {
            appendTab().append("FROM ")
            append(CustomFromItemVisitor(nextLevel()).apply { update.fromItem.accept(this) }.toString())
            update.joins.forEach { join ->
                if (join.isSimple) {
                    append(", ").append(join(join, currentLevel()))
                } else {
                    appendTab().append(join(join, nextLevel()))
                }
            }
        }
    }

    override fun visit(insert: Insert?) {
        val insert = insert ?: return

        appendTab().append("INSERT ")

        insert.modifierPriority?.apply {
            append(this.name).append(" ")
        }

        if (insert.isModifierIgnore) {
            append("IGNORE ")
        }

        append("INTO ").append(insert.table.toString())

        if (insert.columns.isNotEmpty()) {
            append(getStringList(insert.columns, true, true)).append(" ")
        }

        if (insert.isUseValues) {
            appendTab().append("VALUES ")
        }

        if (insert.itemsList != null) {
            append(insert.itemsList.toString())
        } else {
            if (insert.isUseSelectBrackets) {
                append("(")
            }

            append(CustomStatementVisitor(nextLevel()).apply { insert.select.accept(this) }.toString())

            if (insert.isUseSelectBrackets) {
                append(")")
            }
        }

        if (insert.isUseSet) {
            appendTab().append("SET ")
            append(
                insert.setColumns.mapIndexed { index: Int, column: Column ->
                    column.toString() + " = " + insert.setExpressionList[index].toString()
                }.joinToString(",")
            )
        }

        if (insert.isUseDuplicate) {
            appendTab().append("ON DUPLICATE KEY UPDATE ")
            append(
                insert.duplicateUpdateColumns.mapIndexed { index: Int, column: Column ->
                    column.toString() + " = " + insert.duplicateUpdateExpressionList[index].toString()
                }.joinToString(",")
            )
        }

        if (insert.isReturningAllColumns) {
            appendTab().append("RETURNING *")
        } else if (insert.returningExpressionList.isNotEmpty()) {
            appendTab().append("RETURNING ")
                .append(getStringList(insert.returningExpressionList, true, true))
        }
    }

    override fun visit(replace: Replace?) {
        val replace = replace ?: return

        appendTab().append("REPLACE ")

        if (replace.isUseIntoTables) {
            append("INTO ")
        }
        append(replace.table.toString())

        if (replace.expressions.isNotEmpty()) {
            appendTab().append("SET ").append(
                replace.columns.mapIndexed { index: Int, column: Column ->
                    column.toString() + " = " + replace.expressions[index].toString()
                }.joinToString(",")
            )
        } else if (replace.columns.isNotEmpty()) {
            append(getStringList(replace.columns, true, true))
        }

        if (replace.itemsList != null) {
            if (replace.isUseValues) {
                appendTab().append("VALUES ")
            }
            appendTab().append(replace.itemsList.toString())
        }
    }

    override fun visit(drop: Drop?) {
        val drop = drop ?: return
        appendTab().append(drop.toString())
    }

    override fun visit(truncate: Truncate?) {
        val truncate = truncate ?: return
        appendTab().append(truncate.toString())
    }

    override fun visit(createIndex: CreateIndex?) {
        val createIndex = createIndex ?: return
        appendTab().append(createIndex.toString())
    }

    override fun visit(aThis: CreateSchema?) {
        val aThis = aThis ?: return
        appendTab().append(aThis.toString())
    }

    override fun visit(createTable: CreateTable?) {
        val createTable = createTable ?: return
        appendTab().append(createTable.toString())
    }

    override fun visit(createView: CreateView?) {
        val createView = createView ?: return
        appendTab().append(createView.toString())
    }

    override fun visit(alterView: AlterView?) {
        val alterView = alterView ?: return
        appendTab().append(alterView.toString())
    }

    override fun visit(alter: Alter?) {
        val alter = alter ?: return
        appendTab().append(alter.toString())
    }

    override fun visit(stmts: Statements?) {
        val stmts = stmts ?: return

        stmts.statements.forEach { it.accept(this) }
    }

    override fun visit(execute: Execute?) {
        val execute = execute ?: return
        appendTab().append(execute.toString())
    }

    override fun visit(set: SetStatement?) {
        val set = set ?: return
        appendTab().append(set.toString())
    }

    override fun visit(set: ShowColumnsStatement?) {
        val set = set ?: return
        appendTab().append(set.toString())
    }

    override fun visit(showTables: ShowTablesStatement?) {
        val showTables = showTables ?: return
        appendTab().append(showTables.toString())
    }

    override fun visit(merge: Merge?) {
        val merge = merge ?: return

        appendTab().append("MERGE INTO").append(merge.table.toString()).append(" USING ")
        if (merge.usingTable != null) {
            append(merge.usingTable.toString())
        } else if (merge.usingSelect != null) {
            append("(")
                .append(CustomFromItemVisitor(nextLevel()).apply { merge.usingSelect.accept(this) }.toString())
                .append(")")
        }

        if (merge.usingAlias != null) {
            append(merge.usingAlias.toString())
        }

        appendTab().append("ON (").append(merge.onCondition.toString()).append(")")

        if (merge.isInsertFirst) {
            if (merge.mergeInsert != null) {
                appendTab().append(merge.mergeInsert.toString())
            }
        }

        if (merge.mergeUpdate != null) {
            appendTab().append(merge.mergeUpdate.toString())
        }

        if (merge.isInsertFirst) {
            if (merge.mergeInsert != null) {
                appendTab().append(merge.mergeInsert.toString())
            }
        }
    }

    override fun visit(select: Select?) {
        val select = select ?: return

        if (select.withItemsList.isNotEmpty()) {
            appendTab().append("WITH ").append(
                select.withItemsList.joinToString(",") { toString() }
            )
        }
        append(CustomSelectVisitor(currentLevel()).apply { select.selectBody.accept(this) }.toString())
    }

    override fun visit(upsert: Upsert?) {
        val upsert = upsert ?: return

        appendTab().append("UPSERT INTO ").append(upsert.table.toString())

        if (upsert.columns.isNotEmpty()) {
            append(getStringList(upsert.columns, true, true)).append(" ")
        }

        if (upsert.isUseValues) {
            appendTab().append("VALUES ")
        }

        if (upsert.itemsList != null) {
            append(upsert.itemsList.toString())
        } else {
            if (upsert.isUseSelectBrackets) {
                append("(")
            }
            if (upsert.select != null) {
                append(CustomStatementVisitor(nextLevel()).apply { upsert.select.accept(this) }.toString())
            }

            if (upsert.isUseSelectBrackets) {
                append(")")
            }
        }

        if (upsert.isUseDuplicate) {
            appendTab().append("ON DUPLICATE KEY UPDATE")
            append(
                upsert.duplicateUpdateColumns.mapIndexed { index: Int, column: Column ->
                    column.toString() + " = " + upsert.duplicateUpdateExpressionList[index].toString()
                }.joinToString(",")
            )
        }
    }

    override fun visit(use: UseStatement?) {
        val use = use ?: return

        appendTab().append(use.toString())
    }

    override fun visit(block: Block?) {
        val block = block ?: return

        appendTab().append(block.toString())
    }

    override fun visit(values: ValuesStatement?) {
        val values = values ?: return

        appendTab().append(getStringList(values.expressions, true, true))
    }

    override fun visit(describe: DescribeStatement?) {
        val describe = describe ?: return

        appendTab().append(describe.toString())
    }

    override fun visit(aThis: ExplainStatement?) {
        val aThis = aThis ?: return

        appendTab().append("EXPLAIN ")
            .append(CustomStatementVisitor(currentLevel()).apply { aThis.accept(this) }.toString())
    }

    override fun visit(aThis: ShowStatement?) {
        val aThis = aThis ?: return

        appendTab().append(aThis.toString())
    }

    override fun visit(aThis: DeclareStatement?) {
        val aThis = aThis ?: return

        appendTab().append("DECLARE ")
        if (aThis.declareType == DeclareType.AS) {
            append(aThis.userVariable.toString() + " AS " + aThis.typeName)
        } else {
            if (aThis.declareType == DeclareType.TABLE) {
                append(aThis.userVariable.toString()).append(" TABLE (")
                    .append(aThis.columnDefinitions.joinToString(",") { toString() })
                    .append(")")
            } else {
                append(
                    aThis.typeDefExprList.joinToString(",") { type ->
                        val sb = StringBuilder()
                        if (type.userVariable != null) {
                            sb.append(type.userVariable.toString()).append(" ")
                        }
                        sb.append(type.colDataType.toString())
                        if (type.defaultExpr != null) {
                            sb.append(" = ").append(type.defaultExpr.toString())
                        }
                        sb.toString()
                    }
                )
            }
        }
    }

    override fun visit(grant: Grant?) {
        val grant = grant ?: return

        appendTab().append("GRANT ")
        if (grant.role != null) {
            append(grant.role.toString())
        } else {
            append(grant.privileges.joinToString(",") { toString() })
                .append(" ON ")
                .append(grant.objectName)
        }
        append(" TO ")
            .append(grant.users.joinToString(",") { toString() })
    }

    override fun visit(createSequence: CreateSequence?) {
        val createSequence = createSequence ?: return

        appendTab().append(createSequence.toString())
    }

    override fun visit(alterSequence: AlterSequence?) {
        val alterSequence = alterSequence ?: return

        appendTab().append(alterSequence.toString())
    }

    override fun visit(createFunctionalStatement: CreateFunctionalStatement?) {
        val createFunctionalInterface = createFunctionalStatement ?: return

        appendTab().append(createFunctionalInterface.toString())
    }

    override fun visit(createSynonym: CreateSynonym?) {
        val createSynonym = createSynonym ?: return

        appendTab().append(createSynonym.toString())
    }
}