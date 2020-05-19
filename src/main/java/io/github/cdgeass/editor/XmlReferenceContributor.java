package io.github.cdgeass.editor;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cdgeass
 * @since  2020-05-19
 */
public class XmlReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiMethod.class),
                new PsiReferenceProvider() {
                    @Override
                    public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (!(element instanceof PsiMethod)) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        var clazz = PsiTreeUtil.findFirstParent(element, element1 -> element instanceof PsiClass);
                        if (clazz == null) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        var qualifiedName = ((PsiClass) clazz).getQualifiedName();
                        if (qualifiedName == null) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        var xmlTags = XmlNavHolder.getXmlTag(qualifiedName + "." + ((PsiMethod) element).getName());
                        return xmlTags.stream()
                                .map(target -> new MyBatisReference(element, TextRange.EMPTY_RANGE, target))
                                .collect(Collectors.toList())
                                .toArray(new PsiReference[xmlTags.size()]);
                    }
                });
    }
}
