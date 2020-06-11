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
public class CustomStatementVisitor implements StatementVisitor {

    private final StringBuilder sqlStringBuilder;

    public CustomStatementVisitor() {
        sqlStringBuilder = new StringBuilder();
    }

    public String getSql() {
        return sqlStringBuilder.toString();
    }

    @Override
    public void visit(Comment comment) {
        sqlStringBuilder.append(comment);
    }

    @Override
    public void visit(Commit commit) {
        sqlStringBuilder.append(commit);
    }

    @Override
    public void visit(Delete delete) {
        sqlStringBuilder.append("DELETE");

        if (delete.getTables() != null && delete.getTables().size() > 0) {
            sqlStringBuilder.append(" ");
            sqlStringBuilder.append(delete.getTables().stream()
                    .map(Table::toString)
                    .collect(joining(", ")));
        }

        sqlStringBuilder.append(" FROM ");
        sqlStringBuilder.append(delete.getTable());

        if (delete.getJoins() != null) {
            for (var join : delete.getJoins()) {
                if (join.isSimple()) {
                    sqlStringBuilder.append(", ").append(VisitorUtil.join(join, 0));
                } else {
                    sqlStringBuilder.append(" ").append(VisitorUtil.join(join, 0));
                }
            }
        }

        if (delete.getWhere() != null) {
            sqlStringBuilder.append("\nWHERE ").append(delete.getWhere());
        }

        if (delete.getOrderByElements() != null) {
            sqlStringBuilder.append("\n").append(PlainSelect.orderByToString(delete.getOrderByElements()));
        }

        if (delete.getLimit() != null) {
            sqlStringBuilder.append("\n").append(delete.getLimit());
        }
    }

    @Override
    public void visit(Update update) {
        sqlStringBuilder.append("UPDATE ");
        sqlStringBuilder.append(update.getTable());
        if (update.getStartJoins() != null) {
            for (var join : update.getStartJoins()) {
                if (join.isSimple()) {
                    sqlStringBuilder.append(", ").append(VisitorUtil.join(join, 0));
                } else {
                    sqlStringBuilder.append(" ").append(VisitorUtil.join(join, 0));
                }
            }
        }
        sqlStringBuilder.append("\nSET ");

        if (!update.isUseSelect()) {
            for (var i = 0; i < update.getColumns().size(); i++) {
                if (i != 0) {
                    sqlStringBuilder.append(", ");
                }
                sqlStringBuilder.append(update.getColumns().get(i)).append(" = ");
                sqlStringBuilder.append(update.getExpressions().get(i));
            }
        } else {
            if (update.isUseColumnsBrackets()) {
                sqlStringBuilder.append("(");
            }
            for (var i = 0; i < update.getColumns().size(); i++) {
                if (i != 0) {
                    sqlStringBuilder.append(", ");
                }
                sqlStringBuilder.append(update.getColumns().get(i));
            }
            if (update.isUseColumnsBrackets()) {
                sqlStringBuilder.append(")");
            }
            sqlStringBuilder.append(" = ");
            sqlStringBuilder.append("(").append(update.getSelect()).append(")");
        }

        if (update.getFromItem() != null) {
            sqlStringBuilder.append(" FROM ").append(update.getFromItem());
            if (update.getJoins() != null) {
                for (var join : update.getJoins()) {
                    if (join.isSimple()) {
                        sqlStringBuilder.append(", ").append(join);
                    } else {
                        sqlStringBuilder.append(" ").append(join);
                    }
                }
            }
        }

        if (update.getWhere() != null) {
            sqlStringBuilder.append(" WHERE ");
            sqlStringBuilder.append(update.getWhere());
        }
        if (update.getOrderByElements() != null) {
            sqlStringBuilder.append(PlainSelect.orderByToString(update.getOrderByElements()));
        }
        if (update.getLimit() != null) {
            sqlStringBuilder.append(update.getLimit());
        }

        if (update.isReturningAllColumns()) {
            sqlStringBuilder.append(" RETURNING *");
        } else if (update.getReturningExpressionList() != null) {
            sqlStringBuilder.append(" RETURNING ").append(PlainSelect.
                    getStringList(update.getReturningExpressionList(), true, false));
        }
    }

    @Override
    public void visit(Insert insert) {

    }

    @Override
    public void visit(Replace replace) {

    }

    @Override
    public void visit(Drop drop) {

    }

    @Override
    public void visit(Truncate truncate) {

    }

    @Override
    public void visit(CreateIndex createIndex) {

    }

    @Override
    public void visit(CreateTable createTable) {

    }

    @Override
    public void visit(CreateView createView) {

    }

    @Override
    public void visit(AlterView alterView) {

    }

    @Override
    public void visit(Alter alter) {

    }

    @Override
    public void visit(Statements stmts) {

    }

    @Override
    public void visit(Execute execute) {

    }

    @Override
    public void visit(SetStatement set) {

    }

    @Override
    public void visit(ShowColumnsStatement set) {

    }

    @Override
    public void visit(Merge merge) {

    }

    @Override
    public void visit(Select select) {
        var withItemsList = select.getWithItemsList();
        if (withItemsList != null && !withItemsList.isEmpty()) {
            sqlStringBuilder.append("WITH ");
            for (var iterator = withItemsList.iterator(); iterator.hasNext(); ) {
                var withItem = iterator.next();
                sqlStringBuilder.append(withItem);
                if (iterator.hasNext()) {
                    sqlStringBuilder.append(",");
                }
                sqlStringBuilder.append("\n");
            }
        }
        var selectBody = select.getSelectBody();
        var customSelectVisitor = new CustomSelectVisitor();
        selectBody.accept(customSelectVisitor);
        sqlStringBuilder.append(customSelectVisitor.getSql());
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
