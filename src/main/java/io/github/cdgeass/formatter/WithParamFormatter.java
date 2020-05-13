package io.github.cdgeass.formatter;


import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.util.Pair;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author cdgeass
 * @since 2020-04-15
 */
public class WithParamFormatter extends Formatter {

    private final static String PREPARING_LABEL = "Preparing:";
    private final static String PARAMETERS_LABEL = "Parameters:";

    private final static String LINE_SPLIT = "\n";
    private final static String SEMICOLON = ";";

    private final static Pattern GET_THREAD_NAME_PATTERN = Pattern.compile("\\[([a-zA-Z\\d-]+)]");
    private final static Pattern GET_METHOD_NAME_PATTERN = Pattern.compile("(([a-zA-Z]+\\.)+[a-zA-Z]+)");

    private WithParamFormatter() {

    }

    public static boolean canFormatter(String selectedText) {
        return selectedText.contains(PREPARING_LABEL) && selectedText.contains(PARAMETERS_LABEL);
    }

    public static String format(String selectedText) {
        String[] lines = selectedText.split(LINE_SPLIT);

        LinkedHashMap<String, Pair<String, String>> sqlMap = Maps.newLinkedHashMap();
        int j = 0; int k = 0;
        for (String line : lines) {
            if (line.contains(PREPARING_LABEL) && !line.contains(PARAMETERS_LABEL)) {
                String preparingTag;

                var matcher = GET_THREAD_NAME_PATTERN.matcher(line);
                if (matcher.find()) {
                    preparingTag = matcher.group(1);
                } else {
                    preparingTag = "" + j;
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
                    parametersTag = "" + j++;
                }

                var parameters = line.substring(StringUtils.indexOf(line, PARAMETERS_LABEL) + PARAMETERS_LABEL.length());
                if (!sqlMap.containsKey(parametersTag + k)) {
                    parametersTag = "" + j++;
                }
                var pair = sqlMap.get(parametersTag + k);
                if (pair == null) {
                    continue;
                }
                sqlMap.put(parametersTag + k, Pair.create(pair.getFirst(), parameters));
            }
        }

        List<String> sqlList = format(sqlMap);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < sqlList.size(); i++) {
            stringBuilder.append(sqlList.get(i)).append(LINE_SPLIT).append(SEMICOLON);
            if (i != 0 || i != sqlList.size() - 1) {
                stringBuilder.append("-- -----------------------------------").append(LINE_SPLIT);
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
