package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import com.thaiopensource.relaxng.edit.Param;

import java.util.List;

/**
 * @author cdgeass
 * @since  2020-06-20
 */
public interface ParameterMap extends DomElement {

    GenericAttributeValue<String> getId();

    GenericAttributeValue<String> getType();

    @SubTagList("parameter")
    List<Parameter> getParameters();
}
