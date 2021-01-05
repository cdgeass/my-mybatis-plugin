package io.github.cdgeass.formatter;

import com.google.common.collect.Lists;
import com.intellij.openapi.diagnostic.Logger;
import io.github.cdgeass.formatter.visitor.CustomStatementVisitor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author cdgeass
 * @since 2020-04-21
 */
public class Formatter {

    private static final Logger log = Logger.getInstance(Formatter.class);

    private static final String NULL = "null";
    private static final Pattern GET_PARAM_TYPE_PATTERN = Pattern.compile("(.*)\\((\\S+)\\)");

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
            return preparing;
        }
    }

    private static List<String> parameters(List<String> parametersWithType) {
        List<String> parameters = Lists.newArrayList();
        if (parametersWithType == null) {
            return parameters;
        }

        for (String parameterWithType : parametersWithType) {
            if (NULL.equals(parameterWithType)) {
                parameters.add(NULL);
                continue;
            }
            var matcher = GET_PARAM_TYPE_PATTERN.matcher(parameterWithType);
            if (matcher.find()) {
                var parameter = Optional.ofNullable(matcher.group(1)).orElse("");
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
