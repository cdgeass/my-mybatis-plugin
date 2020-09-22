package io.github.cdgeass.editor;

import lombok.experimental.UtilityClass;

import java.util.Locale;

/**
 * @author cdgeass
 * @since 2020-07-16
 */
@UtilityClass
public class OGNLUtil {

    private final String GET = "get";
    private final String SET = "set";
    private final String IS = "is";

    public boolean isGetter(String name) {
        return (name.startsWith(GET) && name.length() > 3) || (name.startsWith(IS) && name.length() > 2);
    }

    public boolean isSetter(String name) {
        return name.startsWith(SET) && name.length() > 3;
    }

    public String methodToProperty(String name) {
        if (name.startsWith(IS)) {
            name = name.substring(2);
        } else if (name.startsWith(GET) || name.startsWith(SET)) {
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
