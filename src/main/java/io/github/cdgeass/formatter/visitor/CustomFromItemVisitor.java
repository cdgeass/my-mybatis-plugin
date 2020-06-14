package io.github.cdgeass.formatter.visitor;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

/**
 * @author cdgeass
 * @since 2020-06-04
 */
public class CustomFromItemVisitor implements FromItemVisitor {

    private final int level;

    private final String TAB_CHARACTER;
    private final StringBuilder sqlStringBuilder;

    public CustomFromItemVisitor() {
        this(0);
    }

    public CustomFromItemVisitor(int level) {
        sqlStringBuilder = new StringBuilder();
        TAB_CHARACTER = "\t".repeat(Math.max(0, level));
        this.level = level;
    }

    public String getSql() {
        return sqlStringBuilder.toString();
    }

    @Override
    public void visit(Table tableName) {
        sqlStringBuilder.append(tableName);
    }

    @Override
    public void visit(SubSelect subSelect) {

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
