package io.github.cdgeass.util;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.net.ntp.TimeStamp;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cdgeass
 * @since  2020-04-16
 */
public class FormatUtils {

    private FormatUtils() {

    }

    private static final Pattern SET_PARAM_REGEX = Pattern.compile("(?<=[=(,]\\s)\\?|\\?(?:\\s+[=><])");

    private static final Pattern GET_PARAM_TYPE_PATTERN = Pattern.compile("(\\b.*)\\((\\S+)\\)");

    public static String format(String preparing, String[] parameters) {
        if (StringUtils.isBlank(preparing) || ArrayUtils.isEmpty(parameters)) {
            return "";
        }

        for (String parameter : parameters) {
            Matcher matcher = GET_PARAM_TYPE_PATTERN.matcher(parameter);
            if (matcher.find()) {
                String param = matcher.group(1);
                String paramType = matcher.group(2);

                if (Objects.equals(paramType, String.class.getSimpleName())
                        || Objects.equals(paramType, TimeStamp.class.getSimpleName())) {
                    param = "'" + param + "'";
                }
                preparing = RegExUtils.replaceFirst(preparing, SET_PARAM_REGEX, param);
            }
        }

        Statement parse;
        try {
            parse = CCJSqlParserUtil.parse(preparing);
        } catch (JSQLParserException e) {
            return "";
        }

        return parse.toString();
    }

}
