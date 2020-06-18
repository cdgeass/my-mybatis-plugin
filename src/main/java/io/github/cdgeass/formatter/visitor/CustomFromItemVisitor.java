package io.github.cdgeass.formatter.visitor;

import io.github.cdgeass.constants.StringConstants;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

/**
 * @author cdgeass
 * @since 2020-06-04
 */
public class CustomFromItemVisitor extends AbstractCustomVisitor implements FromItemVisitor {

    public CustomFromItemVisitor(int level) {
        super(level);
    }

    @Override
    public void visit(Table tableName) {
        append(tableName.toString());
    }

    @Override
    public void visit(SubSelect subSelect) {
        if (subSelect.isUseBrackets()) {
            append("(\n");
        }
        if (subSelect.getWithItemsList() != null && !subSelect.getWithItemsList().isEmpty()) {
            appendTab().append("WITH ");
            for (var iter = subSelect.getWithItemsList().iterator(); iter.hasNext(); ) {
                var withItem = iter.next();
                append(withItem.toString());
                if (iter.hasNext()) {
                    append(",");
                }
                append(" ");
            }
        }
        var customSelectVisitor = new CustomSelectVisitor(currentLevel());
        subSelect.getSelectBody().accept(customSelectVisitor);
        append(customSelectVisitor.toString());
        if (subSelect.isUseBrackets()) {
            append(StringConstants.LINE_BREAK).appendPreTab().append(")");
        }

        if (subSelect.getAlias() != null) {
            append(subSelect.getAlias().toString());
        }
        if (subSelect.getPivot() != null) {
            append(StringConstants.LINE_BREAK).appendTab().append(subSelect.getPivot().toString());
        }
        if (subSelect.getUnPivot() != null) {
            append(StringConstants.LINE_BREAK).appendTab().append(subSelect.getUnPivot().toString());
        }
    }

    @Override
    public void visit(SubJoin subjoin) {
        appendTab().append("(").append(subjoin.getLeft().toString());
        for (var join : subjoin.getJoinList()) {
            if (join.isSimple()) {
                append(", ").append(VisitorUtil.join(join, currentLevel()));
            } else {
                append(" ").append(VisitorUtil.join(join, currentLevel()));
            }
        }

        append(StringConstants.LINE_BREAK).appendTab().append(")");
        if (subjoin.getAlias() != null) {
            append(subjoin.getAlias().toString());
        }
        if (subjoin.getPivot() != null) {
            append(StringConstants.LINE_BREAK).appendTab().append(subjoin.getPivot().toString());
        }
        if (subjoin.getUnPivot() != null) {
            append(StringConstants.LINE_BREAK).appendTab().append(subjoin.getUnPivot().toString());
        }
    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {
        var customFromItemVisitor = new CustomFromItemVisitor(currentLevel());
        lateralSubSelect.accept(customFromItemVisitor);
        append(customFromItemVisitor.toString());
    }

    @Override
    public void visit(ValuesList valuesList) {
        appendTab().append("(VALUES ");
        for (var iter = valuesList.getMultiExpressionList().getExprList().iterator(); iter.hasNext(); ) {
            append(VisitorUtil.getStringList(iter.next().getExpressions(), true, !valuesList.isNoBrackets()));
            if (iter.hasNext()) {
                append(", ");
            }
        }
        append(StringConstants.LINE_BREAK).appendTab().append(")");
        if (valuesList.getAlias() != null) {
            append(valuesList.getAlias().toString());

            if (valuesList.getColumnNames() != null) {
                append(StringConstants.LINE_BREAK).appendTab().append("(");
                for (var iter = valuesList.getColumnNames().iterator(); iter.hasNext(); ) {
                    append(iter.next());
                    if (iter.hasNext()) {
                        append(", ");
                    }
                }
                append(StringConstants.LINE_BREAK).appendTab().append(")");
            }
        }
    }

    @Override
    public void visit(TableFunction tableFunction) {
        var customFromItemVisitor = new CustomFromItemVisitor(currentLevel());
        tableFunction.accept(customFromItemVisitor);
        append(customFromItemVisitor.toString());
    }

    @Override
    public void visit(ParenthesisFromItem aThis) {
        appendTab().append("(");
        var customFromItemVisitor = new CustomFromItemVisitor(nextLevel());
        aThis.getFromItem().accept(customFromItemVisitor);
        append(customFromItemVisitor.toString());
        append(StringConstants.LINE_BREAK).appendTab().append(")");

        if (aThis.getAlias() != null) {
            append(aThis.getAlias().toString());
        }
    }
}
