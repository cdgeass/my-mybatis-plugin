package io.github.cdgeass.editor.dom;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.*;
import io.github.cdgeass.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author cdgeass
 * @since 2020-05-21
 */
public class PsiJavaReferenceConvert extends Converter<PsiElement> implements CustomReferenceConverter<PsiElement> {

    @Nullable
    @Override
    public PsiElement fromString(@Nullable String s, ConvertContext context) {
        return null;
    }

    @Nullable
    @Override
    public String toString(@Nullable PsiElement element, ConvertContext context) {
        return null;
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        return createReferences(element);
    }

    public PsiReference[] createReferences(@NotNull PsiElement psiElement) {
        var xmlFile = (XmlFile) PsiTreeUtil.findFirstParent(psiElement, element -> element instanceof XmlFile);
        if (xmlFile == null || xmlFile.getRootTag() == null) {
            return PsiReference.EMPTY_ARRAY;
        }

        var namespaceValue = xmlFile.getRootTag().getAttribute("namespace");
        if (namespaceValue == null) {
            return PsiReference.EMPTY_ARRAY;
        }

        var qualifiedName = namespaceValue.getValue() + "." + ((XmlAttributeValue) psiElement).getValue();
        var psiMethod = PsiUtil.findMethod(psiElement.getProject(), qualifiedName);
        if (psiMethod == null || psiMethod.getNameIdentifier() == null) {
            return PsiReference.EMPTY_ARRAY;
        }

        return new PsiReference[]{new XmlReference(psiElement, Lists.newArrayList(psiMethod))};
    }
}
