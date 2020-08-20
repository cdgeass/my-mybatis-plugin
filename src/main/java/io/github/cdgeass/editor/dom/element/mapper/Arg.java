package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import io.github.cdgeass.editor.dom.element.convert.ResultMapReferenceConvert;
import io.github.cdgeass.editor.dom.element.convert.StatementReferenceConvert;

/**
 * @author cdgeass
 * @since  2020-06-19
 */
public interface Arg extends DomElement {

    GenericAttributeValue<String> getJavaType();

    GenericAttributeValue<String> getColumn();

    GenericAttributeValue<String> getJdbcType();

    GenericAttributeValue<String> getTypeHandler();

    @Convert(StatementReferenceConvert.class)
    GenericAttributeValue<SelectStatement> getSelect();

    @Convert(ResultMapReferenceConvert.class)
    GenericAttributeValue<ResultMap> getResultMap();

    GenericAttributeValue<String> getName();
}
