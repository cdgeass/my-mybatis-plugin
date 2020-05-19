package io.github.cdgeass.editor;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author cdgeass
 * @since 2020-05-19
 */
public class MyBatisReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final PsiElement target;

    public MyBatisReference(@NotNull PsiElement element, TextRange textRange, PsiElement target) {
        super(element, textRange);
        this.target = target;
    }

    @Override
    public @NotNull ResolveResult[] multiResolve(boolean incompleteCode) {
        return new ResolveResult[] { new PsiElementResolveResult(target) };
    }

    @Override
    public @Nullable PsiElement resolve() {
        var resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }
}
