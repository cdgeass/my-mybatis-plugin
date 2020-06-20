package io.github.cdgeass.editor.dom.element;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.element.mapper.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author cdgeass
 * @since 2020-05-20
 */
public class MapperDescription extends DomFileDescription<Mapper> {

    public MapperDescription() {
        super(Mapper.class, StringConstants.MAPPER);
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        var document = file.getDocument();
        if (document == null) {
            return false;
        }

        var rootTag = document.getRootTag();
        return rootTag != null && StringConstants.MAPPER.equals(rootTag.getName());
    }
}
