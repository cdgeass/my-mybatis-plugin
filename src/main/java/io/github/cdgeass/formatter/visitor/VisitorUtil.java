package io.github.cdgeass.formatter.visitor;

import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * @author cdgeass
 * @since 2020-06-05
 */
public class VisitorUtil {

    private VisitorUtil() {

    }

    public static String join(Join join) {
        if (join.isSimple() && join.isOuter()) {
            return "OUTER " + join.getRightItem();
        } else if (join.isSimple()) {
            return "" + join.getRightItem();
        } else {
            String type = "";

            if (join.isRight()) {
                type += "RIGHT ";
            } else if (join.isNatural()) {
                type += "NATURAL ";
            } else if (join.isFull()) {
                type += "FULL ";
            } else if (join.isLeft()) {
                type += "LEFT ";
            } else if (join.isCross()) {
                type += "CROSS ";
            }

            if (join.isOuter()) {
                type += "OUTER ";
            } else if (join.isInner()) {
                type += "INNER ";
            } else if (join.isSemi()) {
                type += "SEMI ";
            }

            if (join.isStraight()) {
                type = "STRAIGHT_JOIN ";
            } else if (join.isApply()) {
                type += "APPLY ";
            } else {
                type += "JOIN ";
            }

            var rightItem = join.getRightItem();
            var customFromItemSelectVisitor = new CustomFromItemSelectVisitor();
            rightItem.accept(customFromItemSelectVisitor);
            return type + customFromItemSelectVisitor.getSql() + ((join.getJoinWindow() != null) ? " WITHIN " + join.getJoinWindow() : "")
                    + ((join.getOnExpression() != null) ? " ON " + join.getOnExpression() + "" : "")
                    + PlainSelect.getFormatedList(join.getUsingColumns(), "USING", true, true);
        }
    }
}
