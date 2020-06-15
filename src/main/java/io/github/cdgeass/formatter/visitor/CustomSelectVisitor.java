package io.github.cdgeass.formatter.visitor;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.values.ValuesStatement;

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
//        if (plainSelect.isUseBrackets()) {
//            append("(\n");
//        }
//        appendTab().append("SELECT ");
//
//        if (plainSelect.getOracleHint() != null) {
//            append(plainSelect.getOracleHint().toString()).append(" ");
//        }
//
//        if (plainSelect.getSkip() != null) {
//            append(plainSelect.getSkip().toString()).append(" ");
//        }
//
//        if (plainSelect.getFirst() != null) {
//            append(plainSelect.getFirst().toString()).append(" ");
//        }
//
//        if (plainSelect.getDistinct() != null) {
//            append(plainSelect.getDistinct().toString()).append(" ");
//        }
//        if (plainSelect.getTop() != null) {
//            append(plainSelect.getTop().toString()).append(" ");
//        }
//        if (plainSelect.getMySqlSqlNoCache()) {
//            append("SQL_NO_CACHE").append(" ");
//        }
//        if (plainSelect.getMySqlSqlCalcFoundRows()) {
//            append("SQL_CALC_FOUND_ROWS").append(" ");
//        }
//        append(PlainSelect.getStringList(plainSelect.getSelectItems()));
//
//        if (intoTables != null) {
//            sql.append(" INTO ");
//            for (Iterator<Table> iter = intoTables.iterator(); iter.hasNext();) {
//                sql.append(iter.next().toString());
//                if (iter.hasNext()) {
//                    sql.append(", ");
//                }
//            }
//        }
//
//        if (fromItem != null) {
//            sql.append(" FROM ").append(fromItem);
//            if (joins != null) {
//                Iterator<Join> it = joins.iterator();
//                while (it.hasNext()) {
//                    Join join = it.next();
//                    if (join.isSimple()) {
//                        sql.append(", ").append(join);
//                    } else {
//                        sql.append(" ").append(join);
//                    }
//                }
//            }
//
//            if (ksqlWindow != null) {
//                sql.append(" WINDOW ").append(ksqlWindow.toString());
//            }
//            if (where != null) {
//                sql.append(" WHERE ").append(where);
//            }
//            if (oracleHierarchical != null) {
//                sql.append(oracleHierarchical.toString());
//            }
//            if (groupBy != null) {
//                sql.append(" ").append(groupBy.toString());
//            }
//            if (having != null) {
//                sql.append(" HAVING ").append(having);
//            }
//            sql.append(orderByToString(oracleSiblings, orderByElements));
//            if (limit != null) {
//                sql.append(limit);
//            }
//            if (offset != null) {
//                sql.append(offset);
//            }
//            if (fetch != null) {
//                sql.append(fetch);
//            }
//            if (isForUpdate()) {
//                sql.append(" FOR UPDATE");
//
//                if (forUpdateTable != null) {
//                    sql.append(" OF ").append(forUpdateTable);
//                }
//
//                if (wait != null) {
//                    // Wait's toString will do the formatting for us
//                    sql.append(wait);
//                }
//            }
//            if (optimizeFor != null) {
//                sql.append(optimizeFor);
//            }
//        } else {
//            //without from
//            if (where != null) {
//                sql.append(" WHERE ").append(where);
//            }
//        }
//        if (forXmlPath != null) {
//            sql.append(" FOR XML PATH(").append(forXmlPath).append(")");
//        }
//        if (useBrackets) {
//            sql.append(")");
//        }
//        return sql.toString();
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
