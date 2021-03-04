package io.github.cdgeass.editor.dom.element.mapper;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author cdgeass
 * @since 2020-06-19
 */
public interface Constructor extends DomElement {

    @SubTagList("idArg")
    List<IdArg> getIdArgs();

    @SubTagList("arg")
    List<Arg> getArgs();
}
