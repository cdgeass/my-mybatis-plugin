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
import java.util.*

/**
 * @author cdgeass
 * @since  2021-03-03
 */
class CustomStatementVisitor(level: Int) : AbstractCustomVisitor(level), StatementVisitor {

    override fun visit(comment: Comment?) {
        appendTab().append(comment?.toString() ?: "")
    }

    override fun visit(commit: Commit?) {
        appendTab().append(commit?.toString() ?: "")
    }

    override fun visit(delete: Delete?) {
        append("DELETE ")
        if (delete?.tables?.isNotEmpty() == true) {
            append(" " + delete.tables.joinToString(", ") { it.toString() })
        }

        appendTab().append("FROM ").append(delete?.table?.toString() ?: "")

        delete?.joins?.forEach { join ->
            if (join.isSimple) {
                append(", ").append(join(join, currentLevel()))
            } else {
                appendTab().append(join(join, nextLevel()))
            }
        }

        if (delete?.where != null) {
            appendTab().append("WHERE ").append(delete.where.toString())
        }

        if (delete?.orderByElements?.isNotEmpty() == true) {
            appendTab().append(PlainSelect.orderByToString(delete.orderByElements))
        }

        if (delete?.limit != null) {
            appendTab().append(delete.limit.toString())
        }
    }

    override fun visit(update: Update?) {
        appendTab().append("UPDATE ").append(update?.table?.toString() ?: "")

        update?.startJoins?.forEach { startJoin ->
            if (startJoin.isSimple) {
                append(", ").append(join(startJoin, currentLevel()))
            } else {
                appendTab().append(join(startJoin, nextLevel()))
            }
        }

        appendTab().append("SET ")
        if (update?.isUseSelect != true) {
            append(
                update?.columns?.mapIndexed { index: Int, column: Column ->
                    column.toString() + " = " + update.expressions[index].toString()
                }?.joinToString(",") ?: ""
            )
        } else {
            if (update.isUseColumnsBrackets) {
                append("(")
            }
            append(update.columns?.joinToString(",") { toString() } ?: "")
            if (update.isUseColumnsBrackets) {
                append(")")
            }

            append(" = ")

            append("(")
            append(CustomStatementVisitor(nextLevel()).apply { update.select?.accept(this) }.toString())
            append(")")
        }

        if (update?.fromItem != null) {
            appendTab().append("FROM ")
            append(CustomFromItemVisitor(nextLevel()).apply { update.fromItem.accept(this) }.toString())
            update.joins?.forEach { join ->
                if (join.isSimple) {
                    append(", ").append(join(join, currentLevel()))
                } else {
                    appendTab().append(join(join, nextLevel()))
                }
            }
        }
    }

    override fun visit(insert: Insert?) {
        appendTab().append("INSERT ")

        insert?.modifierPriority?.apply {
            append(this.name).append(" ")
        }

        if (insert?.isModifierIgnore == true) {
            append("IGNORE ")
        }

        append("INTO ").append(insert?.table?.toString() ?: "")

        if (insert?.columns?.isNotEmpty() == true) {
            append(getStringList(insert.columns, useComma = true, useBrackets = true)).append(" ")
        }

        if (insert?.isUseValues == true) {
            appendTab().append("VALUES ")
        }

        if (insert?.itemsList != null) {
            append(insert.itemsList.toString())
        } else {
            if (insert?.isUseSelectBrackets == true) {
                append("(")
            }

            append(CustomStatementVisitor(nextLevel()).apply { insert?.select?.accept(this) }.toString())

            if (insert?.isUseSelectBrackets == true) {
                append(")")
            }
        }

        if (insert?.isUseSet == true) {
            appendTab().append("SET ")
            append(
                insert.setColumns?.mapIndexed { index: Int, column: Column ->
                    column.toString() + " = " + insert.setExpressionList[index].toString()
                }?.joinToString(",") ?: ""
            )
        }

        if (insert?.isUseDuplicate == true) {
            appendTab().append("ON DUPLICATE KEY UPDATE ")
            append(
                insert.duplicateUpdateColumns?.mapIndexed { index: Int, column: Column ->
                    column.toString() + " = " + insert.duplicateUpdateExpressionList[index].toString()
                }?.joinToString(",") ?: ""
            )
        }

        if (insert?.isReturningAllColumns == true) {
            appendTab().append("RETURNING *")
        } else if (insert?.returningExpressionList?.isNotEmpty() == true) {
            appendTab().append("RETURNING ")
                .append(getStringList(insert.returningExpressionList, useComma = true, useBrackets = true))
        }
    }

    override fun visit(replace: Replace?) {
        appendTab().append("REPLACE ")

        if (replace?.isUseIntoTables == true) {
            append("INTO ")
        }
        append(replace?.table?.toString() ?: "")

        if (replace?.expressions?.isNotEmpty() == true) {
            appendTab().append("SET ").append(
                replace.columns?.mapIndexed { index: Int, column: Column ->
                    column.toString() + " = " + replace.expressions[index].toString()
                }?.joinToString(",") ?: ""
            )
        } else if (replace?.columns?.isNotEmpty() == true) {
            append(getStringList(replace.columns ?: Collections.emptyList(), useComma = true, useBrackets = true))
        }

        if (replace?.itemsList != null) {
            if (replace.isUseValues) {
                appendTab().append("VALUES ")
            }
            appendTab().append(replace.itemsList.toString())
        }
    }

    override fun visit(drop: Drop?) {
        appendTab().append(drop?.toString() ?: "")
    }

    override fun visit(truncate: Truncate?) {
        appendTab().append(truncate?.toString() ?: "")
    }

    override fun visit(createIndex: CreateIndex?) {
        appendTab().append(createIndex?.toString() ?: "")
    }

    override fun visit(aThis: CreateSchema?) {
        appendTab().append(aThis?.toString() ?: "")
    }

    override fun visit(createTable: CreateTable?) {
        appendTab().append(createTable?.toString() ?: "")
    }

    override fun visit(createView: CreateView?) {
        appendTab().append(createView?.toString() ?: "")
    }

    override fun visit(alterView: AlterView?) {
        appendTab().append(alterView?.toString() ?: "")
    }

    override fun visit(alter: Alter?) {
        appendTab().append(alter?.toString() ?: "")
    }

    override fun visit(stmts: Statements?) {
        stmts?.statements?.forEach { it.accept(this) }
    }

    override fun visit(execute: Execute?) {
        appendTab().append(execute?.toString() ?: "")
    }

    override fun visit(set: SetStatement?) {
        appendTab().append(set?.toString() ?: "")
    }

    override fun visit(set: ShowColumnsStatement?) {
        appendTab().append(set?.toString() ?: "")
    }

    override fun visit(showTables: ShowTablesStatement?) {
        appendTab().append(showTables?.toString() ?: "")
    }

    override fun visit(merge: Merge?) {
        appendTab().append("MERGE INTO").append(merge?.table?.toString() ?: "").append(" USING ")
        if (merge?.usingTable != null) {
            append(merge.usingTable?.toString() ?: "")
        } else if (merge?.usingSelect != null) {
            append("(")
                .append(CustomFromItemVisitor(nextLevel()).apply { merge.usingSelect.accept(this) }.toString())
                .append(")")
        }

        if (merge?.usingAlias != null) {
            append(merge.usingAlias.toString())
        }

        appendTab().append("ON (").append(merge?.onCondition?.toString() ?: "").append(")")

        if (merge?.isInsertFirst == true) {
            if (merge.mergeInsert != null) {
                appendTab().append(merge.mergeInsert.toString())
            }
        }

        if (merge?.mergeUpdate != null) {
            appendTab().append(merge.mergeUpdate?.toString() ?: "")
        }

        if (merge?.isInsertFirst == true) {
            if (merge.mergeInsert != null) {
                appendTab().append(merge.mergeInsert.toString())
            }
        }
    }

    override fun visit(select: Select?) {
        if (select?.withItemsList?.isNotEmpty() == true) {
            appendTab().append("WITH ").append(
                select.withItemsList.joinToString(",") { toString() }
            )
        }
        append(CustomSelectVisitor(currentLevel()).apply { select?.selectBody?.accept(this) }.toString())
    }

    override fun visit(upsert: Upsert?) {
        appendTab().append("UPSERT INTO ").append(upsert?.table.toString())

        if (upsert?.columns?.isNotEmpty() == true) {
            append(getStringList(upsert.columns, useComma = true, useBrackets = true)).append(" ")
        }

        if (upsert?.isUseValues == true) {
            appendTab().append("VALUES ")
        }

        if (upsert?.itemsList != null) {
            append(upsert.itemsList.toString())
        } else {
            if (upsert?.isUseSelectBrackets == true) {
                append("(")
            }
            if (upsert?.select != null) {
                append(CustomStatementVisitor(nextLevel()).apply { upsert.select.accept(this) }.toString())
            }

            if (upsert?.isUseSelectBrackets == true) {
                append(")")
            }
        }

        if (upsert?.isUseDuplicate == true) {
            appendTab().append("ON DUPLICATE KEY UPDATE")
            append(
                upsert.duplicateUpdateColumns?.mapIndexed { index: Int, column: Column ->
                    column.toString() + " = " + upsert.duplicateUpdateExpressionList[index].toString()
                }?.joinToString(",") ?: ""
            )
        }
    }

    override fun visit(use: UseStatement?) {
        appendTab().append(use?.toString() ?: "")
    }

    override fun visit(block: Block?) {
        appendTab().append(block?.toString() ?: "")
    }

    override fun visit(values: ValuesStatement?) {
        appendTab().append(getStringList(values?.expressions ?: emptyList(), useComma = true, useBrackets = true))
    }

    override fun visit(describe: DescribeStatement?) {
        appendTab().append(describe?.toString() ?: "")
    }

    override fun visit(aThis: ExplainStatement?) {
        appendTab().append("EXPLAIN ")
            .append(CustomStatementVisitor(currentLevel()).apply { aThis?.accept(this) }.toString())
    }

    override fun visit(aThis: ShowStatement?) {
        appendTab().append(aThis.toString())
    }

    override fun visit(aThis: DeclareStatement?) {
        appendTab().append("DECLARE ")
        if (aThis?.declareType == DeclareType.AS) {
            append(aThis.userVariable.toString() + " AS " + aThis.typeName)
        } else {
            if (aThis?.declareType == DeclareType.TABLE) {
                append(aThis.userVariable?.toString() ?: "")
                    .append(" TABLE (")
                    .append(aThis.columnDefinitions?.joinToString(",") { toString() } ?: "")
                    .append(")")
            } else {
                append(
                    aThis?.typeDefExprList?.joinToString(",") { type ->
                        val sb = StringBuilder()
                        if (type.userVariable != null) {
                            sb.append(type.userVariable.toString()).append(" ")
                        }
                        sb.append(type.colDataType.toString())
                        if (type.defaultExpr != null) {
                            sb.append(" = ").append(type.defaultExpr.toString())
                        }
                        sb.toString()
                    } ?: ""
                )
            }
        }
    }

    override fun visit(grant: Grant?) {
        appendTab().append("GRANT ")
        if (grant?.role != null) {
            append(grant.role.toString())
        } else {
            append(grant?.privileges?.joinToString(",") { toString() } ?: "")
                .append(" ON ")
                .append(grant?.objectName ?: "")
        }
        append(" TO ")
            .append(grant?.users?.joinToString(",") { toString() } ?: "")
    }

    override fun visit(createSequence: CreateSequence?) {
        appendTab().append(createSequence?.toString() ?: "")
    }

    override fun visit(alterSequence: AlterSequence?) {
        appendTab().append(alterSequence?.toString() ?: "")
    }

    override fun visit(createFunctionalStatement: CreateFunctionalStatement?) {
        appendTab().append(createFunctionalStatement?.toString() ?: "")
    }

    override fun visit(createSynonym: CreateSynonym?) {
        appendTab().append(createSynonym?.toString() ?: "")
    }
}