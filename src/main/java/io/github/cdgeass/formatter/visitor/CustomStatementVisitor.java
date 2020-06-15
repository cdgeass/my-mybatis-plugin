package io.github.cdgeass.formatter.visitor;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.comment.Comment;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.values.ValuesStatement;

import java.util.Iterator;

import static java.util.stream.Collectors.joining;

/**
 * @author cdgeass
 * @since 2020-05-28
 */
public class CustomStatementVisitor extends AbstractCustomVisitor implements StatementVisitor {

    public CustomStatementVisitor(int level) {
        super(level);
    }

    @Override
    public void visit(Comment comment) {
        append(comment.toString());
    }

    @Override
    public void visit(Commit commit) {
        append(commit.toString());
    }

    @Override
    public void visit(Delete delete) {
        appendTab().append("DELETE");

        if (delete.getTables() != null && delete.getTables().size() > 0) {
            append(" ");
            append(delete.getTables().stream()
                    .map(Table::toString)
                    .collect(joining(", ")));
        }

        append("\n").appendTab().append("FROM ");
        append(delete.getTable().toString());

        if (delete.getJoins() != null) {
            for (var join : delete.getJoins()) {
                if (join.isSimple()) {
                    append(", ").append(VisitorUtil.join(join, currentLevel()));
                } else {
                    append("\n").appendTab().append(VisitorUtil.join(join, nextLevel()));
                }
            }
        }

        if (delete.getWhere() != null) {
            append("\n").appendTab().append("WHERE ").append(delete.getWhere().toString());
        }

        if (delete.getOrderByElements() != null) {
            append("\n").appendTab().append(PlainSelect.orderByToString(delete.getOrderByElements()));
        }

        if (delete.getLimit() != null) {
            append("\n").appendTab().append(delete.getLimit().toString());
        }
    }

    @Override
    public void visit(Update update) {
        appendTab().append("UPDATE ");
        append(update.getTable().toString());
        if (update.getStartJoins() != null) {
            for (var join : update.getStartJoins()) {
                if (join.isSimple()) {
                    append(", ").append(VisitorUtil.join(join, currentLevel()));
                } else {
                    append("\n").appendTab().append(VisitorUtil.join(join, nextLevel()));
                }
            }
        }
        append("\n").appendTab().append("SET ");

        if (!update.isUseSelect()) {
            for (int i = 0; i < update.getColumns().size(); i++) {
                if (i != 0) {
                    append(", ");
                }
                append(update.getColumns().get(i).toString()).append(" = ")
                        .append(update.getExpressions().get(i).toString());
            }
        } else {
            if (update.isUseColumnsBrackets()) {
                append("(").append("\n").appendTab();
            }
            for (int i = 0; i < update.getColumns().size(); i++) {
                if (i != 0) {
                    append(", ");
                }
                append(update.getColumns().get(i).toString());
            }
            if (update.isUseColumnsBrackets()) {
                append("\n").appendTab().append(")");
            }
            append(" = ");
            var customStatementVisitor = new CustomStatementVisitor(nextLevel());
            update.getSelect().accept(customStatementVisitor);
            append("(\n").append(customStatementVisitor.toString()).append("\n").appendTab().append(")");
        }

        if (update.getFromItem() != null) {
            var customFromItemSelectVisitor = new CustomFromItemVisitor(currentLevel());
            update.getFromItem().accept(customFromItemSelectVisitor);
            append("\n").appendTab().append("FROM ").append(customFromItemSelectVisitor.toString());
            if (update.getJoins() != null) {
                for (var join : update.getJoins()) {
                    if (join.isSimple()) {
                        append(", ").append(VisitorUtil.join(join, currentLevel()));
                    } else {
                        append("\n").appendTab().append(VisitorUtil.join(join, nextLevel()));
                    }
                }
            }
        }

        if (update.getWhere() != null) {
            append("WHERE ").append(update.getWhere().toString());
        }
        if (update.getOrderByElements() != null) {
            append("\n").appendTab().append(PlainSelect.orderByToString(update.getOrderByElements()));
        }
        if (update.getLimit() != null) {
            append("\n").appendTab().append(update.getLimit().toString());
        }

        if (update.isReturningAllColumns()) {
            append("\n").appendTab().append("RETURNING *");
        } else if (update.getReturningExpressionList() != null) {
            append("\n").appendTab().append("RETURNING ").append(PlainSelect.
                    getStringList(update.getReturningExpressionList(), true, false));
        }
    }

    @Override
    public void visit(Insert insert) {
        appendTab().append("INSERT ");

        if (insert.getModifierPriority() != null) {
            append(insert.getModifierPriority().name()).append(" ");
        }
        if (insert.isModifierIgnore()) {
            append("IGNORE ");
        }
        append("INTO ");
        append(insert.getTable().toString()).append(" ");
        if (insert.getColumns() != null) {
            append(PlainSelect.getStringList(insert.getColumns(), true, true)).append(" ");
        }

        if (insert.isUseValues()) {
            append("\n").appendTab().append("VALUES ");
        }

        if (insert.getItemsList() != null) {
            append("\n").appendTab().append(insert.getItemsList().toString());
        } else {
            if (insert.isUseSelectBrackets()) {
                append("(\n");
            }
            if (insert.getSelect() != null) {
                var customStatementVisitor = new CustomStatementVisitor(nextLevel());
                insert.getSelect().accept(customStatementVisitor);
                append(customStatementVisitor.toString());
            }
            if (insert.isUseSelectBrackets()) {
                append("\n").appendTab().append(")");
            }
        }

        if (insert.isUseSet()) {
            append("\n").appendTab().append("SET ");
            for (int i = 0; i < insert.getColumns().size(); i++) {
                if (i != 0) {
                    append(", ");
                }
                append(insert.getSetColumns().get(i).toString()).append(" = ");
                append(insert.getSetExpressionList().get(i).toString());
            }
        }

        if (insert.isUseDuplicate()) {
            append("\n").appendTab().append("ON DUPLICATE KEY UPDATE ");
            for (int i = 0; i < insert.getDuplicateUpdateColumns().size(); i++) {
                if (i != 0) {
                    append(", ");
                }
                append(insert.getDuplicateUpdateColumns().get(i).toString()).append(" = ");
                append(insert.getDuplicateUpdateExpressionList().get(i).toString());
            }
        }

        if (insert.isReturningAllColumns()) {
            append("\n").appendTab().append("RETURNING *");
        } else if (insert.getReturningExpressionList() != null) {
            append("\n").appendTab().append("RETURNING ").append(PlainSelect.
                    getStringList(insert.getReturningExpressionList(), true, false));
        }
    }

    @Override
    public void visit(Replace replace) {
        appendTab().append("REPLACE ");
        if (replace.isUseIntoTables()) {
            append("INTO ");
        }
        append(replace.getTable().toString());

        if (replace.getExpressions() != null && replace.getColumns() != null) {
            append("\n").appendTab().append("SET ");
            for (int i = 0, s = replace.getColumns().size(); i < s; i++) {
                append(replace.getColumns().get(i).toString()).append(" = ").append(replace.getExpressions().get(i).toString());
                append((i < s - 1) ? ", " : "");
            }
        } else if (replace.getColumns() != null) {
            append(" ").append(PlainSelect.getStringList(replace.getColumns(), true, true));
        }

        if (replace.getItemsList() != null) {

            if (replace.isUseValues()) {
                append("\n").appendTab().append("VALUES");
            }

            append("\n").appendTab().append(replace.getItemsList().toString());
        }
    }

    @Override
    public void visit(Drop drop) {
        appendTab().append(drop.toString());
    }

    @Override
    public void visit(Truncate truncate) {
        appendTab().append(truncate.toString());
    }

    @Override
    public void visit(CreateIndex createIndex) {
        appendTab().append(createIndex.toString());
    }

    @Override
    public void visit(CreateTable createTable) {
        appendTab().append(createTable.toString());
    }

    @Override
    public void visit(CreateView createView) {
        appendTab().append(createView.toString());
    }

    @Override
    public void visit(AlterView alterView) {
        appendTab().append(alterView.toString());
    }

    @Override
    public void visit(Alter alter) {
        appendTab().append(alter.toString());
    }

    @Override
    public void visit(Statements stmts) {
        for (var statement : stmts.getStatements()) {
            statement.accept(this);
            append("\n");
        }
    }

    @Override
    public void visit(Execute execute) {
        appendTab().append(execute.toString());
    }

    @Override
    public void visit(SetStatement set) {
        appendTab().append(set.toString());
    }

    @Override
    public void visit(ShowColumnsStatement set) {
        appendTab().append(set.toString());
    }

    @Override
    public void visit(Merge merge) {
        appendTab().append("MERGE INTO ");
        append(merge.getTable().toString());
        append(" USING ");
        if (merge.getUsingTable() != null) {
            append(merge.getUsingTable().toString());
        } else if (merge.getUsingSelect() != null) {
            var customFromItemVisitor = new CustomFromItemVisitor(nextLevel());
            merge.getUsingSelect().accept(customFromItemVisitor);
            append("(").append("\n").append(customFromItemVisitor.toString()).append("\n").appendTab().append(")");
        }

        if (merge.getUsingAlias() != null) {
            append(merge.getUsingAlias().toString());
        }
        append("\n").appendTab().append("ON (");
        append("\n").appendTab().append(merge.getOnCondition().toString());
        append("\n").appendTab().append(")");

        if (merge.isInsertFirst()) {
            if (merge.getMergeInsert() != null) {
                append("\n").appendTab().append(merge.getMergeInsert().toString());
            }
        }

        if (merge.getMergeUpdate() != null) {
            append("\n").appendTab().append(merge.getMergeUpdate().toString());
        }

        if (!merge.isInsertFirst()) {
            if (merge.getMergeInsert() != null) {
                append("\n").appendTab().append(merge.getMergeInsert().toString());
            }
        }
    }

    @Override
    public void visit(Select select) {
        if (select.getWithItemsList() != null && !select.getWithItemsList().isEmpty()) {
            appendTab().append("WITH ");
            for (Iterator<WithItem> iter = select.getWithItemsList().iterator(); iter.hasNext(); ) {
                WithItem withItem = iter.next();
                append(withItem.toString());
                if (iter.hasNext()) {
                    append(",");
                }
                append(" ");
            }
        }
        var customSelectVisitor = new CustomSelectVisitor(currentLevel());
        select.getSelectBody().accept(customSelectVisitor);
        append(customSelectVisitor.toString());
    }

    @Override
    public void visit(Upsert upsert) {
        appendTab().append("UPSERT INTO ");
        append(upsert.getTable().toString()).append(" ");
        if (upsert.getColumns() != null) {
            append(PlainSelect.getStringList(upsert.getColumns(), true, true)).append(" ");
        }
        if (upsert.isUseValues()) {
            append("\n").appendTab().append("VALUES ");
        }

        if (upsert.getItemsList() != null) {
            append(upsert.getItemsList().toString());
        } else {
            if (upsert.isUseSelectBrackets()) {
                append("(");
            }
            if (upsert.getSelect() != null) {
                var customStatementVisitor = new CustomStatementVisitor(nextLevel());
                upsert.getSelect().accept(customStatementVisitor);
                append(customStatementVisitor.toString());
            }
            if (upsert.isUseSelectBrackets()) {
                append("\n").appendTab().append(")");
            }
        }

        if (upsert.isUseDuplicate()) {
            append("\n").appendTab().append("ON DUPLICATE KEY UPDATE ");
            for (int i = 0; i < upsert.getDuplicateUpdateColumns().size(); i++) {
                if (i != 0) {
                    append(", ");
                }
                append(upsert.getDuplicateUpdateColumns().get(i).toString()).append(" = ");
                append(upsert.getDuplicateUpdateExpressionList().get(i).toString());
            }
        }
    }

    @Override
    public void visit(UseStatement use) {
        appendTab().append(use.toString());
    }

    @Override
    public void visit(Block block) {
        appendTab().append(block.toString());
    }

    @Override
    public void visit(ValuesStatement values) {
        appendTab().append("VALUES ").append(PlainSelect.getStringList(values.getExpressions(), true, true));
    }

    @Override
    public void visit(DescribeStatement describe) {
        appendTab().append(describe.toString());
    }

    @Override
    public void visit(ExplainStatement aThis) {
        appendTab().append("EXPLAIN");
        var customStatementVisitor = new CustomStatementVisitor(currentLevel());
        aThis.accept(customStatementVisitor);
        append("\n").append(customStatementVisitor.toString());
    }

    @Override
    public void visit(ShowStatement aThis) {
        appendTab().append(aThis.toString());
    }

    @Override
    public void visit(DeclareStatement aThis) {
        appendTab().append("DECLARE ");
        if (aThis.getType() == DeclareType.AS) {
            append(aThis.getUserVariable().toString());
            append(" AS ").append(aThis.getTypeName());
        } else {
            if (aThis.getType() == DeclareType.TABLE) {
                append(aThis.getUserVariable().toString());
                append(" TABLE (");
                for (int i = 0; i < aThis.getColumnDefinitions().size(); i++) {
                    if (i > 0) {
                        append(", ");
                    }
                    append(aThis.getColumnDefinitions().get(i).toString());
                }
                append(")");
            } else {
                for (int i = 0; i < aThis.getTypeDefinitions().size(); i++) {
                    if (i > 0) {
                        append(", ");
                    }
                    final DeclareStatement.TypeDefExpr type = aThis.getTypeDefinitions().get(i);
                    if (type.userVariable != null) {
                        append(type.userVariable.toString()).append(" ");
                    }
                    append(type.colDataType.toString());
                    if (type.defaultExpr != null) {
                        append(" = ").append(type.defaultExpr.toString());
                    }
                }
            }
        }
    }
}
