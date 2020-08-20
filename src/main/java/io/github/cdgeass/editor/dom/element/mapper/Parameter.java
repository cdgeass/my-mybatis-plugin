package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import io.github.cdgeass.editor.dom.element.convert.ResultMapReferenceConvert;

/**
 * @author cdgeass
 * @since  2020-06-20
 */
public interface Parameter extends DomElement {

    GenericAttributeValue<String> getProperty();

    GenericAttributeValue<String> getJavaType();

    GenericAttributeValue<String> getJdbcType();

    GenericAttributeValue<String> getMode();

    @Attribute("resultMap")
    @Convert(ResultMapReferenceConvert.class)
    GenericAttributeValue<ResultMap> getResultMap();

    GenericAttributeValue<String> getScale();

    GenericAttributeValue<String> getTypeHandler();
}
