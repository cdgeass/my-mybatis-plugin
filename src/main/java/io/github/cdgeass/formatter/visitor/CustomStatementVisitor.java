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
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.values.ValuesStatement;

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

    }

    @Override
    public void visit(Upsert upsert) {

    }

    @Override
    public void visit(UseStatement use) {

    }

    @Override
    public void visit(Block block) {

    }

    @Override
    public void visit(ValuesStatement values) {

    }

    @Override
    public void visit(DescribeStatement describe) {

    }

    @Override
    public void visit(ExplainStatement aThis) {

    }

    @Override
    public void visit(ShowStatement aThis) {

    }

    @Override
    public void visit(DeclareStatement aThis) {

    }
}
