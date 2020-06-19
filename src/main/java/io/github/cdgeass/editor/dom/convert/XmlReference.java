package io.github.cdgeass.editor.dom.convert;

import com.intellij.psi.*;
import com.intellij.psi.xml.XmlTag;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author cdgeass
 * @since 2020-05-21
 */
public class XmlReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final List<PsiElement> targets;

    public XmlReference(@NotNull PsiElement element, List<PsiElement> targets) {
        super(element);
        this.targets = targets;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return CollectionUtils.isEmpty(targets) ? new ResolveResult[0] : targets
                .stream()
                .map(PsiElementResolveResult::new)
                .toArray(ResolveResult[]::new);
    }

    @Override
    public @Nullable PsiElement resolve() {
        var resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }
}
