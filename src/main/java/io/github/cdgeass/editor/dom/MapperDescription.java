package io.github.cdgeass.editor.dom;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlProlog;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author cdgeass
 * @since  2020-05-20
 */
public class MapperDescription extends DomFileDescription<Mapper> {

    public MapperDescription() {
        super(Mapper.class, "mapper");
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        var document = file.getDocument();
        if (document == null) {
            return false;
        }

        var rootTag = document.getRootTag();
        return rootTag != null && rootTag.getName().equals("mapper");
    }
}
