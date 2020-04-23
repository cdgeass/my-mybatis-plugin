package io.github.cdgeass.formatter;

import com.intellij.openapi.diagnostic.Logger;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RegExUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cdgeass
 * @since  2020-04-21
 */
public class Formatter {

    private static final Logger log = Logger.getInstance(Formatter.class);

    private static final Pattern SET_PARAM_REGEX = Pattern.compile("(?<=[=(,]\\s)\\?|\\?(?:\\s+[=><])");
    private static final Pattern GET_PARAM_TYPE_PATTERN = Pattern.compile("(\\b.*)\\((\\S+)\\)");

    protected static String format(String preparing, String[] parameters) {
        if (StringUtils.isBlank(preparing) || ArrayUtils.isEmpty(parameters)) {
            return "";
        }

        for (String parameter : parameters) {
            Matcher matcher = GET_PARAM_TYPE_PATTERN.matcher(parameter);
            if (matcher.find()) {
                String param = matcher.group(1);
                String paramType = matcher.group(2);

                if (!StringUtils.isNumeric(param)) {
                    param = "'" + param + "'";
                }
                preparing = RegExUtils.replaceFirst(preparing, SET_PARAM_REGEX, param);
            }
        }

        Statement parse;
        try {
            parse = CCJSqlParserUtil.parse(preparing);
        } catch (JSQLParserException e) {
            log.debug(e);
            return e.getMessage();
        }

        String sql = parse.toString();
        sql = StringUtils.replaceOnce(sql, "WHERE", "\nWHERE");
        sql = StringUtils.replaceOnce(sql, "FROM", "\nFROM");
        return sql;
    }
}
