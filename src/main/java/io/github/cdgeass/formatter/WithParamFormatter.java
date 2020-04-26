package io.github.cdgeass.formatter;


import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

/**
 * @author cdgeass
 * @since 2020-04-15
 */
public class WithParamFormatter extends Formatter {

    private final static Pattern GET_THREAD_NAME_PATTERN = Pattern.compile("\\[(.*)]");

    private WithParamFormatter() {

    }

    public static boolean canFormatter(String selectedText) {
        return selectedText.contains("Preparing: ") && selectedText.contains("Parameters: ");
    }

    public static String format(String selectedText) {
        String preparing = "";
        String preparingThreadName = "";
        String[] parameters = null;
        var lines = selectedText.split("\n");
        for (String line : lines) {
            if (line.contains("Preparing:")) {
                preparing = line.substring(line.indexOf("Preparing:") + 10).trim();
                var threadNameMatcher = GET_THREAD_NAME_PATTERN.matcher(line);
                if (threadNameMatcher.find()) {
                    preparingThreadName = threadNameMatcher.group(1);
                }
            } else if (line.contains("Parameters:")
                    && (StringUtils.isBlank(preparingThreadName) || line.contains(preparingThreadName))) {
                String parameterString = line.substring(
                        line.indexOf("Parameters:") + 11);
                parameters = parameterString.split(",");
            }
        }

        return format(preparing, parameters);
    }
}
