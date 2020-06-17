package io.github.cdgeass.formatter.visitor;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.values.ValuesStatement;
import org.apache.commons.lang.StringUtils;

/**
 * @author cdgeass
 * @since 2020-06-04
 */
public class CustomSelectVisitor extends AbstractCustomVisitor implements SelectVisitor {

    public CustomSelectVisitor(int level) {
        super(level);
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        if (plainSelect.isUseBrackets()) {
            append("(\n");
        }
        appendTab().append("SELECT ");

        if (plainSelect.getOracleHint() != null) {
            append(plainSelect.getOracleHint().toString()).append(" ");
        }

        if (plainSelect.getSkip() != null) {
            append(plainSelect.getSkip().toString()).append(" ");
        }

        if (plainSelect.getFirst() != null) {
            append(plainSelect.getFirst().toString()).append(" ");
        }

        if (plainSelect.getDistinct() != null) {
            append(plainSelect.getDistinct().toString()).append(" ");
        }
        if (plainSelect.getTop() != null) {
            append(plainSelect.getTop().toString()).append(" ");
        }
        if (plainSelect.getMySqlSqlNoCache()) {
            append("SQL_NO_CACHE").append(" ");
        }
        if (plainSelect.getMySqlSqlCalcFoundRows()) {
            append("SQL_CALC_FOUND_ROWS").append(" ");
        }
        append(PlainSelect.getStringList(plainSelect.getSelectItems()));

        if (plainSelect.getIntoTables() != null) {
            append(" INTO ");
            for (var iter = plainSelect.getIntoTables().iterator(); iter.hasNext(); ) {
                append(iter.next().toString());
                if (iter.hasNext()) {
                    append(", ");
                }
            }
        }

        if (plainSelect.getFromItem() != null) {
            var customFromItemVisitor = new CustomFromItemVisitor(currentLevel());
            plainSelect.getFromItem().accept(customFromItemVisitor);
            append("\n").appendTab().append("FROM ").append(customFromItemVisitor.toString());
            if (plainSelect.getJoins() != null) {
                for (var join : plainSelect.getJoins()) {
                    if (join.isSimple()) {
                        append(", ").append(VisitorUtil.join(join, currentLevel()));
                    } else {
                        append(" ").append(VisitorUtil.join(join, currentLevel()));
                    }
                }
            }

            if (plainSelect.getKsqlWindow() != null) {
                append("\n").appendTab().append("WINDOW ").append(plainSelect.getKsqlWindow().toString());
            }
            if (plainSelect.getWhere() != null) {
                append("\n").appendTab().append("WHERE ").append(plainSelect.getWhere().toString());
            }
            if (plainSelect.getOracleHierarchical() != null) {
                append(plainSelect.getOracleHierarchical().toString());
            }
            if (plainSelect.getGroupBy() != null) {
                append("\n").appendTab().append(plainSelect.getGroupBy().toString());
            }
            if (plainSelect.getHaving() != null) {
                append("\n").appendTab().append("HAVING ").append(plainSelect.getHaving().toString());
            }
            append(PlainSelect.orderByToString(plainSelect.isOracleSiblings(), plainSelect.getOrderByElements()));
            if (plainSelect.getLimit() != null) {
                append("\n").appendTab().append(StringUtils.trim(plainSelect.getLimit().toString()));
            }
            if (plainSelect.getOffset() != null) {
                if (plainSelect.getLimit() == null) {
                    append("\n").appendTab();
                }
                append(StringUtils.trim(plainSelect.getOffset().toString()));
            }
            if (plainSelect.getFetch() != null) {
                append("\n").appendTab().append(plainSelect.getFetch().toString());
            }
            if (plainSelect.isForUpdate()) {
                append("\n").appendTab().append("FOR UPDATE");

                if (plainSelect.getForUpdateTable() != null) {
                    append(" OF ").append(plainSelect.getForUpdateTable().toString());
                }

                if (plainSelect.getWait() != null) {
                    append(plainSelect.getWait().toString());
                }
            }
            if (plainSelect.getOptimizeFor() != null) {
                append("\n").appendTab().append(plainSelect.getOptimizeFor().toString());
            }
        } else {
            if (plainSelect.getWhere() != null) {
                append("\n").appendTab().append("WHERE ").append(plainSelect.getWhere().toString());
            }
        }
        if (plainSelect.getForXmlPath() != null) {
            append("\n").appendTab().append("FOR XML PATH(").append(plainSelect.getForXmlPath()).append(")");
        }
        if (plainSelect.isUseBrackets()) {
            append("\n").appendTab().append(")");
        }
    }

    @Override
    public void visit(SetOperationList setOpList) {
        for (var i = 0; i < setOpList.getSelects().size(); i++) {
            if (i != 0) {
                appendTab().append(setOpList.getOperations().get(i - 1).toString()).append(" ");
            }
            var customSelectVisitor = new CustomSelectVisitor(currentLevel());
            setOpList.getSelects().get(i).accept(customSelectVisitor);
            if (setOpList.getBrackets() == null || setOpList.getBrackets().get(i)) {
                append("(").append(customSelectVisitor.toString()).append(")");
            } else {
                append(customSelectVisitor.toString());
            }
        }

        if (setOpList.getOrderByElements() != null) {
            append("\n").appendTab().append(PlainSelect.orderByToString(setOpList.getOrderByElements()));
        }
        if (setOpList.getLimit() != null) {
            append("\n").appendTab().append(StringUtils.trim(setOpList.getLimit().toString()));
        }
        if (setOpList.getOffset() != null) {
            if (setOpList.getLimit() == null) {
                append("\n").appendTab();
            }
            append(StringUtils.trim(setOpList.getOffset().toString()));
        }
        if (setOpList.getFetch() != null) {
            append("\n").appendTab().append(setOpList.getFetch().toString());
        }
    }

    @Override
    public void visit(WithItem withItem) {
        appendTab();
        if (withItem.isRecursive()) {
            append("RECURSIVE ");
        }

        append(withItem.getName());
        if (withItem.getWithItemList() != null) {
            append(PlainSelect.getStringList(withItem.getWithItemList(), true, true));
        }

        append("\n").appendTab().append("AS (");
        var customSelectVisitor = new CustomSelectVisitor(nextLevel());
        withItem.getSelectBody().accept(customSelectVisitor);
        append(customSelectVisitor.toString());
        append("\n").appendTab().append(")");
    }

    @Override
    public void visit(ValuesStatement aThis) {
        appendTab().append("VALUES ").append(PlainSelect.getStringList(aThis.getExpressions(), true, true));
    }
}
