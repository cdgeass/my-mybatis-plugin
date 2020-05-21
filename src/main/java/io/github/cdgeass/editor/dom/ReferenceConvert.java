package io.github.cdgeass.editor.dom;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.converters.PathReferenceConverter;
import org.jetbrains.annotations.NotNull;

/**
 * @author cdgeass
 * @since 2020-05-21
 */
public class ReferenceConvert extends PathReferenceConverter {

    @NotNull
    @Override
    public PsiReference[] createReferences(@NotNull PsiElement psiElement, boolean soft) {
        return super.createReferences(psiElement, soft);
    }
}
