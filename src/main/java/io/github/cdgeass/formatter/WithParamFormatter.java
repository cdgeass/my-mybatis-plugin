package io.github.cdgeass.formatter;


import io.github.cdgeass.util.FormatUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author cdgeass
 * @since 2020-04-15
 */
public class WithParamFormatter {

    private WithParamFormatter() {

    }

    public static boolean canFormatter(String selectedText) {
        return selectedText.contains("Preparing: ") && selectedText.contains("Parameters: ");
    }

    private static final String SET_PARAM_REGEX = "(?<=[=(,]\\s+)\\?|\\?(?:\\s+[=><])";

    public static String format(String selectedText) {
        String preparing = "";
        var lines = selectedText.split("\n");
        for (String line : lines) {
            if (line.contains("Preparing:")) {
                preparing = line.substring(line.indexOf("Preparing:") + 10).trim();
            } else if (line.contains("Parameters:")) {
                String parameterString = line.substring(
                        line.indexOf("Parameters:") + 11);
                String[] parameters = parameterString.split(",");
                for (String parameter : parameters) {
                    preparing = RegExUtils.replaceFirst(preparing, SET_PARAM_REGEX,
                            Matcher.quoteReplacement(FormatUtils.beautifyParam(parameter)));
                }
            }
        }

        return preparing;
    }
}
