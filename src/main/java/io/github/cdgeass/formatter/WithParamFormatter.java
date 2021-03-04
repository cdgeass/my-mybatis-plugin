package io.github.cdgeass.formatter;


import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.util.Pair;
import io.github.cdgeass.constants.StringConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import static io.github.cdgeass.constants.StringConstants.*;

/**
 * @author cdgeass
 * @since 2020-04-15
 */
public class WithParamFormatter extends Formatter {

    private final static String PREPARING_LABEL = "Preparing:";
    private final static String PARAMETERS_LABEL = "Parameters:";

    private final static Pattern GET_THREAD_NAME_PATTERN = Pattern.compile("\\[\\s?([a-zA-Z\\d-]+)\\s?]");
    private final static Pattern GET_METHOD_NAME_PATTERN = Pattern.compile("(([a-zA-Z]+\\.)+[a-zA-Z]+)");

    private WithParamFormatter() {

    }

    public static boolean canFormatter(String selectedText) {
        return selectedText.contains(PREPARING_LABEL) && selectedText.contains(PARAMETERS_LABEL);
    }

    public static String format(String selectedText) {
        var lines = selectedText.split(LINE_BREAK);

        LinkedHashMap<String, Pair<String, String>> sqlMap = Maps.newLinkedHashMap();
        var j = 0;
        var k = 0;
        for (var line : lines) {
            if (line.contains(PREPARING_LABEL) && !line.contains(PARAMETERS_LABEL)) {
                String preparingTag;

                var matcher = GET_THREAD_NAME_PATTERN.matcher(line);
                if (matcher.find()) {
                    preparingTag = matcher.group(1);
                } else {
                    preparingTag = "" + ++j;
                }

                var preparing = line.substring(StringUtils.indexOf(line, PREPARING_LABEL) + PREPARING_LABEL.length());
                Pair<String, String> pair = Pair.create(preparing, null);
                if (sqlMap.containsKey(preparingTag + k)) {
                    sqlMap.put(preparingTag + ++k, pair);
                } else {
                    sqlMap.put(preparingTag + k, pair);
                }
            } else if (!line.contains(PREPARING_LABEL) && line.contains(PARAMETERS_LABEL)) {
                String parametersTag;

                var matcher = GET_THREAD_NAME_PATTERN.matcher(line);
                if (matcher.find()) {
                    parametersTag = matcher.group(1);
                } else {
                    parametersTag = "" + j;
                }

                var parameters = line.substring(StringUtils.indexOf(line, PARAMETERS_LABEL) + PARAMETERS_LABEL.length());
                if (!sqlMap.containsKey(parametersTag + k)) {
                    parametersTag = "" + j;
                }
                var pair = sqlMap.get(parametersTag + k);
                if (pair == null) {
                    continue;
                }
                sqlMap.put(parametersTag + k, Pair.create(pair.getFirst(), parameters));
            }
        }

        var sqlList = format(sqlMap);
        var stringBuilder = new StringBuilder();
        for (var iterator = sqlList.iterator(); iterator.hasNext(); ) {
            var sql = iterator.next().trim();
            if (sql.endsWith(StringConstants.LINE_BREAK)) {
                sql = StringUtils.substringBeforeLast(iterator.next().trim(), StringConstants.LINE_BREAK);
            }
            stringBuilder.append(sql).append(SEMICOLON).append(LINE_BREAK);
            if (iterator.hasNext()) {
                stringBuilder.append(SEPARATOR_LINE).append(LINE_BREAK);
            }
        }

        return stringBuilder.toString();
    }

    public static List<String> format(LinkedHashMap<String, Pair<String, String>> sqlMap) {
        List<String> sqlList = Lists.newArrayList();
        for (Pair<String, String> pair : sqlMap.values()) {
            String sql = null;
            if (pair.getFirst().contains("?") && pair.getSecond() != null) {
                sql = format(pair.first, Lists.newArrayList(Splitter.on(",")
                        .trimResults()
                        .omitEmptyStrings()
                        .split(pair.second)));
            } else if (!pair.getFirst().contains("?")) {
                sql = format(pair.getFirst(), null);
            }
            if (sql != null) {
                sqlList.add(sql);
            }
        }

        return sqlList;
    }
}
