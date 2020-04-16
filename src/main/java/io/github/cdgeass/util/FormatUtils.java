package io.github.cdgeass.util;

import java.util.regex.Pattern;

/**
 * @author cdgeass
 * @since  2020-04-16
 */
public class FormatUtils {

    private FormatUtils() {

    }

    private static final Pattern GET_PARAM_TYPE_PATTERN = Pattern.compile("(\\b.*)\\((\\S+)\\)");

    public static String beautifyParam(String param) {
        var matcher = GET_PARAM_TYPE_PATTERN.matcher(param);
        if (matcher.find()) {
            param = matcher.group(1);
            String paramType = matcher.group(2);
            if ("String".equals(paramType)) {
                param = "\"" + param + "\"";
            }
            return param;
        }

        return null;
    }
}
