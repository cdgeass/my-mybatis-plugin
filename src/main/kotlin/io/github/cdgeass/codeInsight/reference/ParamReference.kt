package io.github.cdgeass.codeInsight.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.util.xml.DomManager
import io.github.cdgeass.codeInsight.dom.element.Statement

/**
 * @author cdgeass
 * @since 2021/3/28
 */
class ParamReference(
    element: PsiElement,
    private val textRange: TextRange,
    private val preExpression: String,
    private val myKey: String = element.text.substring(
        textRange.startOffset,
        textRange.endOffset
    )
) : PsiPolyVariantReferenceBase<PsiElement>(element, textRange) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return PsiElementResolveResult.createResults(resolveParam())
    }

    override fun getVariants(): Array<Any> {
        return resolveParam().toTypedArray()
    }

    private fun resolveParam(): Collection<PsiElement> {
        val paramMap = getParamMap(element)
        if (preExpression.isBlank()) {
            return paramMap.values
        }

        var paramPsiElement: PsiElement? = null
        preExpression.split(".").filter { it.isNotBlank() }.forEachIndexed { index, param ->
            paramPsiElement = if (index == 0) {
                paramMap[param] ?: return emptyList()
            } else {
                val tempPsiElements = resolvePsiElement(paramPsiElement, param)
                if (tempPsiElements.isEmpty()) return emptyList()
                tempPsiElements[0]
            }
        }

        return resolvePsiElement(paramPsiElement, myKey)
    }

    private fun getParamMap(element: PsiElement): Map<String, PsiClass> {
        val paramMap = mutableMapOf<String, PsiClass>()

        // 获取 Statement 绑定方法上的参数
        val statementTag = PsiTreeUtil.findFirstParent(element) {
            it is XmlTag && (it.name == "select" || it.name == "delete" || it.name == "update" || it.name == "insert")
        } ?: return emptyMap()

        val domManager = DomManager.getDomManager(element.project)
        val statement = domManager.getDomElement(statementTag as XmlTag) as Statement
        val statementMethod = statement.getId().value ?: return emptyMap()
        statementMethod.parameterList.parameters.forEach { param ->
            val paramClass = (param.type as PsiClassType).resolve()
            if (paramClass != null) {
                val paramAnnotation = param.getAnnotation("org.apache.ibatis.annotations.Param")
                if (paramAnnotation != null) {
                    val paramName = paramAnnotation.findAttributeValue("value")?.text?.removeSurrounding("\"")
                    if (paramName != null) {
                        paramMap[paramName] = paramClass
                    }
                } else {
                    paramMap[param.name] = paramClass
                }
            }
        }

        // TODO 查找其他参数

        return paramMap
    }

    private fun resolvePsiElement(psiElement: PsiElement?, paramName: String): List<PsiElement> {
        if (psiElement == null) return emptyList()

        if (psiElement is PsiClass) {
            // TODO 方法和参数判断逻辑
            val fieldResults = psiElement.allFields.filter { it.name.startsWith(paramName) }
            if (fieldResults.isNotEmpty()) {
                return fieldResults
            }

            val methodResults = psiElement.allMethods.filter { it.name.startsWith(paramName) }
            if (methodResults.isNotEmpty()) {
                return methodResults
            }
        }

        return emptyList()
    }

}