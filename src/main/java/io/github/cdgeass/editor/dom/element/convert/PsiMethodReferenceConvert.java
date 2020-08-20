package io.github.cdgeass.editor.dom.element.convert;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import io.github.cdgeass.editor.dom.DomUtil;
import io.github.cdgeass.editor.dom.XmlReference;
import io.github.cdgeass.editor.dom.element.mapper.Mapper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

/**
 * @author cdgeass
 * @since 2020-05-21
 */
public class PsiMethodReferenceConvert extends Converter<PsiMethod> implements CustomReferenceConverter<PsiMethod> {

    @Nullable
    @Override
    public PsiMethod fromString(@Nullable String s, ConvertContext context) {
        if (s == null) {
            return null;
        }

        var mapper = DomUtil.findFileElement(context.getFile(), Mapper.class);
        if (mapper == null) {
            return null;
        }
        var namespaceAttributeValue = mapper.getNamespace();
        var psiClass = namespaceAttributeValue.getValue();
        if (psiClass == null) {
            return null;
        }
        var allMethods = psiClass.getAllMethods();
        for (var psiMethod : allMethods) {
            if (StringUtils.equals(s, psiMethod.getName())) {
                return psiMethod;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public String toString(@Nullable PsiMethod element, ConvertContext context) {
        if (element == null) {
            return null;
        }
        return element.getText();
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<PsiMethod> value, PsiElement element, ConvertContext context) {
        var psiElement = value.getValue();
        if (psiElement == null) {
            return new PsiReference[0];
        }

        return new PsiReference[]{new XmlReference(element, Collections.singletonList(psiElement))};
    }
}
