package io.github.cdgeass.formatter.visitor;

import io.github.cdgeass.constants.StringConstants;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.List;

/**
 * @author cdgeass
 * @since 2020-06-05
 */
public class VisitorUtil {

    private VisitorUtil() {

    }

    public static String getStringList(List<?> list, boolean useComma, boolean useBrackets) {
        return getStringList(list, useComma, useBrackets, false, 0);
    }

    public static String getStringList(List<?> list, boolean useComma, boolean useBrackets, boolean useLineBreak, int level) {
        StringBuilder ans = new StringBuilder();
        String comma = ",";
        String lineBreak = useLineBreak ? StringConstants.LINE_BREAK : "";
        String tabCharacter = StringConstants.TAB_CHARACTER.repeat(Math.max(0, level));
        if (!useComma) {
            comma = "";
        }
        if (list != null) {
            if (useBrackets) {
                ans.append("(").append(lineBreak).append(tabCharacter);
            }

            for (int i = 0; i < list.size(); i++) {
                if (i != 0) {
                    ans.append(lineBreak).append(tabCharacter).append(comma);
                }
                ans.append(list.get(i));
            }

            if (useBrackets) {
                ans.append(lineBreak).append(tabCharacter).append(")");
            }
        }

        return ans.toString();
    }

    public static String orderByToString(boolean oracleSiblings, List<OrderByElement> orderByElements) {
        var sql = getStringList(orderByElements, true, false);
        if (sql.length() > 0) {
            return (oracleSiblings ? "ORDER SIBLINGS BY" : "ORDER BY") + " " + sql;
        }
        return sql;
    }

    public static String join(Join join, int level) {
        String tabCharacter = StringConstants.TAB_CHARACTER.repeat(Math.max(0, level));

        CustomFromItemVisitor customFromItemSelectVisitor = new CustomFromItemVisitor(level + 1);
        if (join.isSimple() && join.isOuter()) {
            join.getRightItem().accept(customFromItemSelectVisitor);
            return "OUTER " + customFromItemSelectVisitor;
        } else if (join.isSimple()) {
            join.getRightItem().accept(customFromItemSelectVisitor);
            return "" + customFromItemSelectVisitor;
        } else {
            String type = StringConstants.LINE_BREAK + tabCharacter;

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
                type += StringConstants.LINE_BREAK + tabCharacter + "ON " + join.getOnExpression();
            }

            type += PlainSelect.getFormatedList(join.getUsingColumns(), "USING", true, true);
            return type;
        }
    }

    public static String expression(Expression expression, int level) {
        String tabCharacter = StringConstants.TAB_CHARACTER.repeat(Math.max(0, level));
        if (expression instanceof BinaryExpression) {

            Expression leftExpression = ((BinaryExpression) expression).getLeftExpression();
            Expression rightExpression = ((BinaryExpression) expression).getRightExpression();
            String stringExpression = ((BinaryExpression) expression).getStringExpression();

            if (StringConstants.AND.equals(stringExpression)) {
                stringExpression = StringConstants.LINE_BREAK + tabCharacter + stringExpression + " ";
            }

            return (leftExpression instanceof BinaryExpression ? expression(leftExpression, level) : leftExpression)
                    + stringExpression + rightExpression;
        }

        return expression.toString();
    }
}
