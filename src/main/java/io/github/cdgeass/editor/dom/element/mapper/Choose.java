package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author cdgeass
 * @since 2020-06-20
 */
public interface Choose extends DomElement {

    @SubTagList("when")
    List<When> getWhens();

    @SubTag("otherwise")
    Otherwise getOtherwise();
}
