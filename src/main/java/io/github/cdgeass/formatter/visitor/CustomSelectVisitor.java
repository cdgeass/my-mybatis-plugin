package io.github.cdgeass.formatter.visitor;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.values.ValuesStatement;

import java.util.Iterator;

/**
 * @author cdgeass
 * @since 2020-06-04
 */
public class CustomSelectVisitor implements SelectVisitor {

    private final StringBuilder sqlStringBuilder;

    public CustomSelectVisitor() {
        sqlStringBuilder = new StringBuilder();
    }

    public String getSql() {
        return sqlStringBuilder.toString();
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        if (plainSelect.isUseBrackets()) {
            sqlStringBuilder.append("(\n\t");
        }
        sqlStringBuilder.append("SELECT ");

        if (plainSelect.getOracleHint() != null) {
            sqlStringBuilder.append(plainSelect.getOracleHint()).append(" ");
        }

        if (plainSelect.getSkip() != null) {
            sqlStringBuilder.append(plainSelect.getSkip()).append(" ");
        }

        if (plainSelect.getFirst() != null) {
            sqlStringBuilder.append(plainSelect.getFirst()).append(" ");
        }

        if (plainSelect.getDistinct() != null) {
            sqlStringBuilder.append(plainSelect.getDistinct()).append(" ");
        }
        if (plainSelect.getTop() != null) {
            sqlStringBuilder.append(plainSelect.getTop()).append(" ");
        }
        if (plainSelect.getMySqlSqlNoCache()) {
            sqlStringBuilder.append("SQL_NO_CACHE").append(" ");
        }
        if (plainSelect.getMySqlSqlCalcFoundRows()) {
            sqlStringBuilder.append("SQL_CALC_FOUND_ROWS").append(" ");
        }
        var selectItems = plainSelect.getSelectItems();
        if (selectItems != null) {
            for (var iterator = selectItems.iterator(); iterator.hasNext(); ) {
                var selectItem = iterator.next();
                sqlStringBuilder.append(selectItem).append("\n");
                if (iterator.hasNext()) {
                    sqlStringBuilder.append("\t,");
                }
            }
        }

        if (plainSelect.getIntoTables() != null) {
            sqlStringBuilder.append(" INTO ");
            for (Iterator<Table> iter = plainSelect.getIntoTables().iterator(); iter.hasNext(); ) {
                sqlStringBuilder.append(iter.next().toString());
                if (iter.hasNext()) {
                    sqlStringBuilder.append(", ");
                }
            }
        }

        if (plainSelect.getFromItem() != null) {
            var customFromItemSelectVisitor = new CustomFromItemSelectVisitor();
            plainSelect.getFromItem().accept(customFromItemSelectVisitor);
            sqlStringBuilder.append("FROM ").append(customFromItemSelectVisitor.getSql()).append("\n");
            if (plainSelect.getJoins() != null) {
                for (Join join : plainSelect.getJoins()) {
                    if (join.isSimple()) {
                        sqlStringBuilder.append(", ");
                    }
                    sqlStringBuilder.append(VisitorUtil.join(join)).append("\n");
                }
            }

            if (plainSelect.getKsqlWindow() != null) {
                sqlStringBuilder.append(" WINDOW ").append(plainSelect.getKsqlWindow().toString());
            }
            if (plainSelect.getWhere() != null) {
                sqlStringBuilder.append("WHERE ").append(plainSelect.getWhere()).append("\n");
            }
            if (plainSelect.getOracleHierarchical() != null) {
                sqlStringBuilder.append(plainSelect.getOracleHierarchical().toString());
            }
            if (plainSelect.getGroupBy() != null) {
                sqlStringBuilder.append(plainSelect.getGroupBy().toString()).append("\n");
            }
            if (plainSelect.getHaving() != null) {
                sqlStringBuilder.append("HAVING ").append(plainSelect.getHaving()).append("\n");
            }
            sqlStringBuilder.append(PlainSelect.getFormatedList(plainSelect.getOrderByElements(), "ORDER BY"));
            if (plainSelect.getLimit() != null) {
                sqlStringBuilder.append(plainSelect.getLimit());
            }
            if (plainSelect.getOffset() != null) {
                sqlStringBuilder.append(plainSelect.getOracleHint());
            }
            if (plainSelect.getFetch() != null) {
                sqlStringBuilder.append(plainSelect.getFetch());
            }
            if (plainSelect.isForUpdate()) {
                sqlStringBuilder.append("\nFOR UPDATE");

                if (plainSelect.getForUpdateTable() != null) {
                    sqlStringBuilder.append(" OF ").append(plainSelect.getForUpdateTable());
                }

                if (plainSelect.getWait() != null) {
                    // Wait's toString will do the formatting for us
                    sqlStringBuilder.append(plainSelect.getWait());
                }
                sqlStringBuilder.append("\n");
            }
            if (plainSelect.getOptimizeFor() != null) {
                sqlStringBuilder.append(plainSelect.getOptimizeFor()).append("\n");
            }
        } else {
            //without from
            if (plainSelect.getWhere() != null) {
                sqlStringBuilder.append("WHERE ").append(plainSelect.getWhere()).append("\n");
            }
        }
        if (plainSelect.getForXmlPath() != null) {
            sqlStringBuilder.append("FOR XML PATH(").append(plainSelect.getForXmlPath()).append(")").append("\n");
        }
        if (plainSelect.isUseBrackets()) {
            sqlStringBuilder.append(")");
        }
    }

    @Override
    public void visit(SetOperationList setOpList) {

    }

    @Override
    public void visit(WithItem withItem) {

    }

    @Override
    public void visit(ValuesStatement aThis) {

    }
}
