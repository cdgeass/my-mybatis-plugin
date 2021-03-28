package io.github.cdgeass.codeInsight.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag

/**
 * @author cdgeass
 * @since 2021/3/28
 */
class ParamReference(
    element: PsiElement,
    private val textRange: TextRange,
    private val expression: String,
    private val myKey: String = (element as XmlAttributeValue).value.substring(
        textRange.startOffset,
        textRange.endOffset
    )
) : PsiPolyVariantReferenceBase<PsiElement>(element, textRange) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val paramMap = getParamMap(element)

        // TODO resolve

        return emptyArray()
    }

    private fun getParamMap(element: PsiElement): Map<String, PsiClass> {
        val paramMap = mutableMapOf<String, PsiClass>()

        // 获取 Statement 绑定方法上的参数
        val statementTag = PsiTreeUtil.findFirstParent(element) {
            it is XmlTag && (it.name == "select" || it.name == "delete" || it.name == "update" || it.name == "insert")
        } ?: return emptyMap()

        val statementMethodReference =
            ReferencesSearch.search(statementTag).find { it is MyPsiMethodReference } ?: return emptyMap()
        val statementMethod = (statementMethodReference.resolve() ?: return emptyMap()) as PsiMethod
        statementMethod.parameterList.parameters.forEach { param ->
            val paramAnnotation = param.getAnnotation("org.apache.ibatis.annotations.Param")
            if (paramAnnotation != null) {
                val paramName = paramAnnotation.findAttributeValue("value")?.text
                if (paramName != null) {
                    val paramClass = (param.type as PsiClassType).resolve()
                    if (paramClass != null) {
                        paramMap[paramName] = paramClass
                    }
                }
            }
        }

        // TODO 查找其他参数

        return paramMap
    }

}