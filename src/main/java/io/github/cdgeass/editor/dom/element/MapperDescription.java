package io.github.cdgeass.editor.dom.element;

import com.intellij.util.xml.DomFileDescription;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.element.mapper.Mapper;

/**
 * @author cdgeass
 * @since 2020-05-20
 */
public class MapperDescription extends DomFileDescription<Mapper> {

    public MapperDescription() {
        super(Mapper.class, StringConstants.MAPPER);
    }

}
