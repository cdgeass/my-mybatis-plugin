package io.github.cdgeass.formatter;


import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageFormatting;
import io.github.cdgeass.util.FormatUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
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

        return FormatUtils.format(preparing, parameters);
    }
}
