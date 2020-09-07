package io.github.cdgeass.formatter.visitor;

import io.github.cdgeass.constants.StringConstants;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.values.ValuesStatement;
import org.apache.commons.lang3.StringUtils;

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
        append(VisitorUtil.getStringList(plainSelect.getSelectItems(), true, false, true, nextLevel()));

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
            var customFromItemVisitor = new CustomFromItemVisitor(nextLevel());
            plainSelect.getFromItem().accept(customFromItemVisitor);
            append(StringConstants.LINE_BREAK).appendTab().append("FROM ").append(customFromItemVisitor.toString());
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
                append(StringConstants.LINE_BREAK).appendTab().append("WINDOW ").append(plainSelect.getKsqlWindow().toString());
            }
            if (plainSelect.getWhere() != null) {
                append(StringConstants.LINE_BREAK).appendTab().append("WHERE ").append(VisitorUtil.expression(plainSelect.getWhere(), currentLevel()));
            }
            if (plainSelect.getOracleHierarchical() != null) {
                append(plainSelect.getOracleHierarchical().toString());
            }
            if (plainSelect.getGroupBy() != null) {
                append(StringConstants.LINE_BREAK).appendTab().append(plainSelect.getGroupBy().toString());
            }
            if (plainSelect.getHaving() != null) {
                append(StringConstants.LINE_BREAK).appendTab().append("HAVING ").append(plainSelect.getHaving().toString());
            }
            if (plainSelect.getOrderByElements() != null && plainSelect.getOrderByElements().size() > 0) {
                append(StringConstants.LINE_BREAK).appendTab().append(VisitorUtil.orderByToString(plainSelect.isOracleSiblings(), plainSelect.getOrderByElements()));
            }
            if (plainSelect.getLimit() != null) {
                append(StringConstants.LINE_BREAK).appendTab().append(StringUtils.trim(plainSelect.getLimit().toString()));
            }
            if (plainSelect.getOffset() != null) {
                if (plainSelect.getLimit() == null) {
                    append(StringConstants.LINE_BREAK).appendTab();
                }
                append(StringUtils.trim(plainSelect.getOffset().toString()));
            }
            if (plainSelect.getFetch() != null) {
                append(StringConstants.LINE_BREAK).appendTab().append(plainSelect.getFetch().toString());
            }
            if (plainSelect.isForUpdate()) {
                append(StringConstants.LINE_BREAK).appendTab().append("FOR UPDATE");

                if (plainSelect.getForUpdateTable() != null) {
                    append(" OF ").append(plainSelect.getForUpdateTable().toString());
                }

                if (plainSelect.getWait() != null) {
                    append(plainSelect.getWait().toString());
                }
            }
            if (plainSelect.getOptimizeFor() != null) {
                append(StringConstants.LINE_BREAK).appendTab().append(plainSelect.getOptimizeFor().toString());
            }
        } else {
            if (plainSelect.getWhere() != null) {
                append(StringConstants.LINE_BREAK).appendTab().append("WHERE ").append(plainSelect.getWhere().toString());
            }
        }
        if (plainSelect.getForXmlPath() != null) {
            append(StringConstants.LINE_BREAK).appendTab().append("FOR XML PATH(").append(plainSelect.getForXmlPath()).append(")");
        }
        if (plainSelect.isUseBrackets()) {
            append(StringConstants.LINE_BREAK).appendPreTab().append(")");
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
            append(StringConstants.LINE_BREAK).appendTab().append(PlainSelect.orderByToString(setOpList.getOrderByElements()));
        }
        if (setOpList.getLimit() != null) {
            append(StringConstants.LINE_BREAK).appendTab().append(StringUtils.trim(setOpList.getLimit().toString()));
        }
        if (setOpList.getOffset() != null) {
            if (setOpList.getLimit() == null) {
                append(StringConstants.LINE_BREAK).appendTab();
            }
            append(StringUtils.trim(setOpList.getOffset().toString()));
        }
        if (setOpList.getFetch() != null) {
            append(StringConstants.LINE_BREAK).appendTab().append(setOpList.getFetch().toString());
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
            append(VisitorUtil.getStringList(withItem.getWithItemList(), true, true));
        }

        append(StringConstants.LINE_BREAK).appendTab().append("AS (");
        var customSelectVisitor = new CustomSelectVisitor(nextLevel());
        withItem.getSelectBody().accept(customSelectVisitor);
        append(customSelectVisitor.toString());
        append(StringConstants.LINE_BREAK).appendTab().append(")");
    }

    @Override
    public void visit(ValuesStatement aThis) {
        appendTab().append("VALUES ").append(VisitorUtil.getStringList(aThis.getExpressions(), true, true));
    }
}
