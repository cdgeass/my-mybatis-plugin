package io.github.cdgeass.constants;

/**
 * @author cdgeass
 * @since 2020-06-11
 */
public class StringConstants {

    // -------------- sql format ---------------

    public final static String LINE_BREAK = "\n";
    public final static String TAB_CHARACTER = "    ";
    public final static String WHITESPACE = " ";
    public final static String SEMICOLON = ";";
    public final static String SEPARATOR_LINE = "-- -----------------------------------";

    public final static String AND = "AND";

    // ------------- mapper impl ---------------------

    public final static String MYBATIS = "mybatis";
    public final static String CONFIGURATION = "configuration";
    public final static String MAPPER = "mapper";
    public final static String NAMESPACE = "namespace";
    public final static String RESULT_MAP = "resultMap";
    public final static String TYPE = "type";
    public final static String PROPERTY = "property";
    public final static String COLUMN = "column";

    // ------------- mapper ----------------------

    public final static String PARAM_ANNOTATION = "org.apache.ibatis.annotations.Param";

    public final static String PARAM_PREFIX = "${";
    public final static String PREPARED_PARAM_PREFIX = "#{";
    public final static String PARAM_SUFFIX = "}";
    public final static String DOT = ".";

    public final static String SQL = "sql";
    public final static String INCLUDE = "include";
    public final static String TYPE_ALIASES = "typeAliases";

    public final static String IF = "if";
    public final static String TEST = "test";
    public final static String EQUALS = "=";
    public final static String ID = "id";
    public final static String REFID = "refid";
    public final static String COLLECTION = "collection";
    public final static String VALUE = "value";

    private StringConstants() {

    }
}
