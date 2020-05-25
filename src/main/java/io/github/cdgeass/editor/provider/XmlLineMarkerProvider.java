package io.github.cdgeass.editor.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import io.github.cdgeass.editor.XmlNavHolder;
import io.github.cdgeass.editor.dom.Mapper;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author cdgeass
 * @since 2020-05-17
 */
public class XmlLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if (!(element instanceof PsiFile)) {
            return;
        }

        var domManager = DomManager.getDomManager(element.getProject());
        DomFileElement<Mapper> fileElement = domManager.getFileElement((XmlFile) element, Mapper.class);
        Mapper rootElement = fileElement.getRootElement();

        XmlNavHolder.scan(element.getProject());
        if (element instanceof XmlFile) {
            result.addAll(XmlNavHolder.build((XmlFile) element));
        }
    }
}
