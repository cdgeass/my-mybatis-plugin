package io.github.cdgeass.editor.dom.element.configuration;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author cdgeass
 * @since  2020-06-20
 */
public interface TypeAliases extends DomElement {

    @SubTagList("typeAlias")
    List<TypeAlias> getTypeAliases();

    @SubTagList("package")
    List<Package> getPackages();
}
