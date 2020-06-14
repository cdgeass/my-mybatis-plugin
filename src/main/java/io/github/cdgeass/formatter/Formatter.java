package io.github.cdgeass.formatter;

import com.google.common.collect.Lists;
import com.intellij.openapi.diagnostic.Logger;
import io.github.cdgeass.formatter.visitor.CustomStatementVisitor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RegExUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author cdgeass
 * @since 2020-04-21
 */
public class Formatter {

    private static final Logger log = Logger.getInstance(Formatter.class);

    private static final String NULL = "null";
    private static final Pattern GET_PARAM_TYPE_PATTERN = Pattern.compile("(\\b.*)\\((\\S+)\\)");

    protected static String format(String preparing, List<String> parametersWithType) {
        if (StringUtils.isBlank(preparing) && CollectionUtils.isEmpty(parametersWithType)) {
            return "";
        }

        var parameterStrings = parameters(parametersWithType);
        for (var parameterString : parameterStrings) {
            preparing = RegExUtils.replaceFirst(preparing, "\\?", parameterString);
        }

        var statementVisitor = new CustomStatementVisitor(0);
        try {
            Statement statement = CCJSqlParserUtil.parse(preparing);
            statement.accept(statementVisitor);
            log.debug("sql: {}", statementVisitor);
            return statementVisitor.toString();
        } catch (JSQLParserException e) {
            log.error("sql parser error", e);
            e.printStackTrace();
        }

        return "";
    }

    private static List<String> parameters(List<String> parametersWithType) {
        List<String> parameters = Lists.newArrayList();

        for (String parameterWithType : parametersWithType) {
            if (NULL.equals(parameterWithType)) {
                parameters.add(NULL);
                continue;
            }
            var matcher = GET_PARAM_TYPE_PATTERN.matcher(parameterWithType);
            if (matcher.find()) {
                var parameter = matcher.group(1);
                var parameterType = matcher.group(2);

                switch (parameterType) {
                    case "Byte":
                    case "Long":
                    case "Short":
                    case "Integer":
                    case "Double":
                    case "Float":
                    case "Boolean":
                        parameters.add(parameter);
                        break;
                    default:
                        parameters.add("'" + parameter + "'");
                        break;
                }
            }
        }

        return parameters;
    }
}
