package io.github.cdgeass.editor;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author cdgeass
 * @since  2020-05-17
 */
public class XmlLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if (!(element instanceof PsiFile)) {
            return;
        }

        XmlNavHolder.scan(element.getProject());
        if (element instanceof XmlFile) {
            result.addAll(XmlNavHolder.build((XmlFile) element));
        }
    }
}
