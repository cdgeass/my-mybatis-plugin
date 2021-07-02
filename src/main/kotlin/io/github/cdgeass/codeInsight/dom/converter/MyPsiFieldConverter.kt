package io.github.cdgeass.codeInsight.dom.converter

import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import com.intellij.util.xml.*
import io.github.cdgeass.codeInsight.dom.element.Association
import io.github.cdgeass.codeInsight.dom.element.Case
import io.github.cdgeass.codeInsight.dom.element.Collection
import io.github.cdgeass.codeInsight.dom.element.ResultMap
import io.github.cdgeass.codeInsight.reference.MyPsiFieldReference

/**
 * @author cdgeass
 * @since 2021-03-26
 */
class MyPsiFieldConverter : Converter<PsiField>(), CustomReferenceConverter<PsiField> {

    override fun toString(t: PsiField?, context: ConvertContext?): String? {
        return t?.text
    }

    override fun fromString(s: String?, context: ConvertContext?): PsiField? {
        if (s.isNullOrBlank() || context?.referenceXmlElement == null) {
            return null
        }
        val psiFields = resolvePsiFields(context.referenceXmlElement!!)
        return if (psiFields.isEmpty()) {
            null
        } else {
            psiFields[0]
        }
    }

    override fun createReferences(
        value: GenericDomValue<PsiField>?,
        element: PsiElement?,
        context: ConvertContext?
    ): Array<PsiReference> {
        if (element == null || value?.value == null) {
            return PsiReference.EMPTY_ARRAY
        }

        return arrayOf(MyPsiFieldReference(element, value.value!!))
    }

    private fun resolvePsiFields(element: PsiElement, ignored: Boolean = false): List<PsiField> {
        if (element !is XmlAttributeValue) return emptyList()

        val parent = getParentWithJavaType(element) ?: return emptyList()
        val psiClass = getPsiClass(parent) ?: return emptyList()

        val name = element.value
        return psiClass.allFields.filter { ignored || it.name == name }
    }

    /**
     * 获取带有 JavaType 类型的 parent
     */
    private fun getParentWithJavaType(element: PsiElement): DomElement? {
        val xmlAttribute = PsiTreeUtil.findFirstParent(element) { it is XmlAttribute }?.let { it as XmlAttribute }
        if (xmlAttribute?.name != "property" && xmlAttribute?.name != "name") {
            return null
        }
        // 先获取 attribute 所在的 xmlTag
        val ownerTag = PsiTreeUtil.findFirstParent(xmlAttribute) { it is XmlTag }?.let { it as XmlTag } ?: return null
        // idArg arg name 字段对映 field
        if (xmlAttribute.name == "name" && ownerTag.name != "idArg" && ownerTag.name != "arg") {
            return null
        }

        // 查找外层带有 type 的 xmlTag
        val xmlTag = PsiTreeUtil.findFirstParent(ownerTag) {
            it is XmlTag &&
                    (
                            !it.getAttributeValue("resultType").isNullOrBlank() ||
                                    !it.getAttributeValue("ofType").isNullOrBlank() ||
                                    !it.getAttributeValue("javaType").isNullOrBlank() ||
                                    !it.getAttributeValue("type").isNullOrBlank() ||
                                    !it.getAttributeValue("resultMap").isNullOrBlank()
                            )
        }?.let { it as XmlTag } ?: return null

        val domManager = DomManager.getDomManager(element.project)
        return domManager.getDomElement(xmlTag)
    }

    /**
     * 从 DomElement 中获取 PsiClass
     */
    private fun getPsiClass(domElement: DomElement): PsiClass? {
        when (domElement) {
            is Association -> {
                val resultMap = domElement.getResultMap().value
                if (resultMap != null) {
                    return getPsiClass(resultMap)
                }
                return domElement.getJavaType().value
            }
            is Case -> {
                val resultMap = domElement.getResultMap().value
                if (resultMap != null) {
                    return getPsiClass(resultMap)
                }
                return domElement.getResultType().value
            }
            is Collection -> {
                val resultMap = domElement.getResultMap().value
                if (resultMap != null) {
                    return getPsiClass(resultMap)
                }
                return domElement.getJavaType().value ?: domElement.getOfType().value
            }
            is ResultMap -> {
                val type = domElement.getType().value
                if (type != null) {
                    return type
                }
                return getPsiClass(domElement.getExtends().value ?: return null)
            }
            else -> {
                return null
            }
        }
    }
}
