package io.github.cdgeass.formatter.visitor;

import io.github.cdgeass.constants.StringConstants;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * @author cdgeass
 * @since 2020-06-05
 */
public class VisitorUtil {

    private VisitorUtil() {

    }

    public static String join(Join join, int level) {
        String tabCharacter = StringConstants.TAB_CHARACTER.repeat(Math.max(0, level));

        CustomFromItemVisitor customFromItemSelectVisitor;
        if (join.getRightItem() instanceof Table) {
            customFromItemSelectVisitor = new CustomFromItemVisitor(level);
        } else {
            customFromItemSelectVisitor = new CustomFromItemVisitor(level + 1);
        }

        if (join.isSimple() && join.isOuter()) {
            join.getRightItem().accept(customFromItemSelectVisitor);
            return "OUTER " + customFromItemSelectVisitor;
        } else if (join.isSimple()) {
            join.getRightItem().accept(customFromItemSelectVisitor);
            return "" + customFromItemSelectVisitor;
        } else {
            String type = "\n" + tabCharacter;

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

            join.getRightItem().accept(customFromItemSelectVisitor);
            type += customFromItemSelectVisitor;

            if (join.getJoinWindow() != null) {
                type += " WITHIN" + join.getJoinWindow();
            }

            if (join.getOnExpression() != null) {
                type += "\n" + tabCharacter + "ON " + join.getOnExpression();
            }

            type += PlainSelect.getFormatedList(join.getUsingColumns(), "USING", true, true);
            return type;
        }
    }
}
