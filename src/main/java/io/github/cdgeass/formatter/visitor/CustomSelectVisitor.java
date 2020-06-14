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
public class CustomSelectVisitor implements SelectVisitor {

    private final int level;
    private final String TAB_CHARACTER;
    private final StringBuilder sqlStringBuilder;

    public CustomSelectVisitor() {
        this(0);
    }

    public CustomSelectVisitor(int level) {
        sqlStringBuilder = new StringBuilder();
        TAB_CHARACTER = "\t".repeat(Math.max(0, level));
        this.level = level;
    }

    public String getSql() {
        return sqlStringBuilder.toString();
    }

    @Override
    public void visit(PlainSelect plainSelect) {

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
