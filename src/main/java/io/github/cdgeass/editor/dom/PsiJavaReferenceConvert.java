package io.github.cdgeass.editor.dom;

import com.alibaba.druid.util.StringUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

/**
 * @author cdgeass
 * @since 2020-05-21
 */
public class PsiJavaReferenceConvert extends Converter<PsiElement> implements CustomReferenceConverter<PsiElement> {

    @Nullable
    @Override
    public PsiElement fromString(@Nullable String s, ConvertContext context) {
        var xmlFile = context.getFile();
        var domManager = DomManager.getDomManager(context.getProject());
        DomFileElement<Mapper> fileElement = domManager.getFileElement(xmlFile, Mapper.class);
        if (fileElement == null) {
            return null;
        }

        var mapper = fileElement.getRootElement();
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
    public String toString(@Nullable PsiElement element, ConvertContext context) {
        if (element == null) {
            return null;
        }
        return element.getText();
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<PsiElement> value, PsiElement element, ConvertContext context) {
        var psiElement = value.getValue();
        if (psiElement == null) {
            return new PsiReference[0];
        }

        return new PsiReference[]{new XmlReference(element, Collections.singletonList(psiElement))};
    }
}
