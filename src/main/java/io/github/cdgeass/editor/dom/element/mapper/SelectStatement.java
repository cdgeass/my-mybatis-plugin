package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import io.github.cdgeass.editor.dom.convert.ResultMapReferenceConvert;

/**
 * @author cdgeass
 * @since 2020-05-21
 */
public interface SelectStatement extends Statement {

    @Attribute("resultMap")
    @Convert(ResultMapReferenceConvert.class)
    GenericAttributeValue<ResultMap> getResultMap();
}
