package io.github.cdgeass.editor;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cdgeass
 * @since 2020-05-14
 */
public class DaoLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if (!(element instanceof PsiFile)) {
            return;
        }

        XmlNavHolder.scan(element.getProject());
        if (element instanceof PsiJavaFile) {
            result.addAll(XmlNavHolder.build((PsiJavaFile) element));
        }
    }
}
