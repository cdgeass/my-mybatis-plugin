package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.util.xml.*;
import io.github.cdgeass.editor.dom.element.convert.SqlReferenceConvert;

import java.util.List;

/**
 * @author cdgeass
 * @since  2020-06-20
 */
public interface Include extends DomElement {

    @Attribute("refid")
    @Convert(SqlReferenceConvert.class)
    GenericAttributeValue<Sql> getRefid();

    @SubTagList("property")
    List<Property> getProperties();
}
