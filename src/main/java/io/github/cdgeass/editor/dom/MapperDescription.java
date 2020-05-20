package io.github.cdgeass.editor.dom;

import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;

/**
 * @author cdgeass
 * @since  2020-05-20
 */
public class MapperDescription extends DomFileDescription<Mapper> {

    public MapperDescription() {
        super(Mapper.class, "mapper");
    }
}
