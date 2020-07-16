package io.github.cdgeass.editor.dom.contributor;

import java.util.Locale;

/**
 * @author cdgeass
 * @since  2020-07-16
 */
public final class ParamUtil {

    private ParamUtil() {}

    public static boolean isGetter(String name) {
        return (name.startsWith("get") && name.length() > 3) || (name.startsWith("is") && name.length() > 2);
    }

    public static boolean isSetter(String name) {
        return name.startsWith("set") && name.length() > 3;
    }

    public static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else {
            return null;
        }

        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }
}
