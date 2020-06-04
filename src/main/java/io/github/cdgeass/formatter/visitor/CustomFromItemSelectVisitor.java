package io.github.cdgeass.formatter.visitor;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

/**
 * @author cdgeass
 * @since 2020-06-04
 */
public class CustomFromItemSelectVisitor implements FromItemVisitor {

    private final StringBuilder sqlStringBuilder;

    public CustomFromItemSelectVisitor() {
        sqlStringBuilder = new StringBuilder();
    }

    public String getSql() {
        return sqlStringBuilder.toString();
    }

    @Override
    public void visit(Table tableName) {

    }

    @Override
    public void visit(SubSelect subSelect) {
        if (subSelect.isUseBrackets()) {
            sqlStringBuilder.append("(\n\t");
        }
        var withItemsList = subSelect.getWithItemsList();
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
        var selectBody = subSelect.getSelectBody();
        var customSelectVisitor = new CustomSelectVisitor();
        selectBody.accept(customSelectVisitor);
        sqlStringBuilder.append(customSelectVisitor.getSql());
        if (subSelect.isUseBrackets()) {
            sqlStringBuilder.append(")");
        }

        if (subSelect.getAlias() != null) {
            sqlStringBuilder.append(subSelect.getAlias().toString());
        }
        if (subSelect.getPivot() != null) {
            sqlStringBuilder.append(" ").append(subSelect.getPivot());
        }
        if (subSelect.getUnPivot() != null) {
            sqlStringBuilder.append(" ").append(subSelect.getUnPivot());
        }
    }

    @Override
    public void visit(SubJoin subjoin) {

    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {

    }

    @Override
    public void visit(ValuesList valuesList) {

    }

    @Override
    public void visit(TableFunction tableFunction) {

    }

    @Override
    public void visit(ParenthesisFromItem aThis) {

    }
}
