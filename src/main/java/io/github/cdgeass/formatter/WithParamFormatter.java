package io.github.cdgeass.formatter;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

    private static final String REGEX = "(\\S+)\\(\\S+\\)";

    public static Map<Integer, String> format(String selectedText) {
        Map<Integer, String> result = new HashMap<>();
        var lines = selectedText.split("\n");
        for (String line : lines) {
            if (line.contains("Preparing:")) {
                result.put(0, line.substring(line.indexOf("Preparing:")));
            } else if (line.contains("Parameters:")) {
                // TODO how to get parameters
            }
        }

        return result;
    }
}
