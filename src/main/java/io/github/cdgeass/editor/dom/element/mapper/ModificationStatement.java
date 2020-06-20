package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author cdgeass
 * @since 2020-05-22
 */
public interface ModificationStatement extends Statement {

    GenericAttributeValue<String> getKeyProperty();

    GenericAttributeValue<Boolean> getUseGeneratedKeys();

    GenericAttributeValue<String> getKeyColumn();

    @SubTagList("selectKey")
    List<SelectKey> getSelectKeys();
}
