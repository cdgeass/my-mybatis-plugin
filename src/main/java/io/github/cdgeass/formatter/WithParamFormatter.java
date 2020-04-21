package io.github.cdgeass.formatter;


/**
 * @author cdgeass
 * @since 2020-04-15
 */
public class WithParamFormatter extends Formatter {

    private WithParamFormatter() {

    }

    public static boolean canFormatter(String selectedText) {
        return selectedText.contains("Preparing: ") && selectedText.contains("Parameters: ");
    }

    public static String format(String selectedText) {
        String preparing = "";
        String[] parameters = null;
        var lines = selectedText.split("\n");
        for (String line : lines) {
            if (line.contains("Preparing:")) {
                preparing = line.substring(line.indexOf("Preparing:") + 10).trim();
            } else if (line.contains("Parameters:")) {
                String parameterString = line.substring(
                        line.indexOf("Parameters:") + 11);
                parameters = parameterString.split(",");
            }
        }

        return format(preparing, parameters);
    }
}
