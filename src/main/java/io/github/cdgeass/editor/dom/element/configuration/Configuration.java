package io.github.cdgeass.editor.dom.element.configuration;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;

/**
 * @author cdgeass
 * @since 2020-06-20
 */
public interface Configuration extends DomElement {

    @SubTag("typeAliases")
    TypeAliases getTypeAliases();
}
