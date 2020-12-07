package io.github.cdgeass.editor.dom.element;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.editor.dom.element.configuration.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author cdgeass
 * @since  2020-06-20
 */
public class ConfigurationDescription extends DomFileDescription<Configuration> {

    public ConfigurationDescription() {
        super(Configuration.class, StringConstants.CONFIGURATION);
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        var document = file.getDocument();
        if (document == null) {
            return false;
        }

        var prolog = document.getProlog();
        if (prolog == null || prolog.getDoctype() == null || prolog.getDoctype().getDtdUri() == null) {
            return true;
        }
        return prolog.getDoctype().getDtdUri().contains(StringConstants.MYBATIS);
    }
}
