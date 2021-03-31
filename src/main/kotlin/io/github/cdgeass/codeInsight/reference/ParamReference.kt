package io.github.cdgeass.codeInsight.reference

import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.util.PlatformIcons
import com.intellij.util.xml.DomManager
import io.github.cdgeass.codeInsight.dom.element.Statement

/**
 * @author cdgeass
 * @since 2021/3/28
 */
private const val GENERIC_NAME_PREFIX = "param"

class ParamReference(
    element: PsiElement,
    private val textRange: TextRange,
    private val preExpression: String,
    private val myKey: String = element.text.substring(
        textRange.startOffset,
        textRange.endOffset
    ).replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "")
) : PsiPolyVariantReferenceBase<PsiElement>(element, textRange) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val paramMap = getParamMap(element)
        if (preExpression.isBlank()) {
            return PsiElementResolveResult.createResults(paramMap.filterKeys { it == myKey }.values)
        }

        var paramPsiElement: PsiElement? = null
        preExpression.split(".").filter { it.isNotBlank() }.forEachIndexed { index, param ->
            paramPsiElement = if (index == 0) {
                paramMap[param] ?: return emptyArray()
            } else {
                val tempPsiElements = resolvePsiElement(paramPsiElement, param, true)
                if (tempPsiElements.isEmpty()) return emptyArray()
                tempPsiElements[0]
            }
        }

        return PsiElementResolveResult.createResults(resolvePsiElement(paramPsiElement, myKey, true))
    }

    override fun getVariants(): Array<Any> {
        val paramMap = getParamMap(element)
        if (preExpression.isBlank()) {
            return paramMap.filterKeys { it.startsWith(myKey) }.map {
                LookupElementBuilder.create(it.value, it.key)
                    .withIcon(
                        when (it.value) {
                            is PsiClass -> {
                                PlatformIcons.CLASS_ICON
                            }
                            is PsiField -> {
                                PlatformIcons.FIELD_ICON
                            }
                            is PsiMethod -> {
                                PlatformIcons.METHOD_ICON
                            }
                            else -> {
                                null
                            }
                        }
                    )
            }.toTypedArray()
        }

        var paramPsiElement: PsiElement? = null
        preExpression.split(".").filter { it.isNotBlank() }.forEachIndexed { index, param ->
            paramPsiElement = if (index == 0) {
                paramMap[param] ?: return emptyArray()
            } else {
                val tempPsiElements = resolvePsiElement(paramPsiElement, param, false)
                if (tempPsiElements.isEmpty()) return emptyArray()
                tempPsiElements[0]
            }
        }

        return resolvePsiElement(paramPsiElement, myKey, false).toTypedArray()
    }

    override fun isSoft(): Boolean {
        return true
    }

    private fun getParamMap(element: PsiElement): Map<String, PsiElement> {
        val paramMap = mutableMapOf<String, PsiElement>()

        // 获取 Statement 绑定方法上的参数
        val statementTag = PsiTreeUtil.findFirstParent(element) {
            it is XmlTag && (it.name == "select" || it.name == "delete" || it.name == "update" || it.name == "insert")
        } ?: return emptyMap()
        val domManager = DomManager.getDomManager(element.project)
        val statement = domManager.getDomElement(statementTag as XmlTag) as Statement
        val statementMethod = statement.getId().value ?: return emptyMap()
        paramMap.putAll(resolveParamName(statementMethod))

        // TODO 查找其他参数

        return paramMap
    }

    /**
     * @see org.apache.ibatis.reflection.ParamNameResolver
     */
    private fun resolveParamName(method: PsiMethod): Map<String, PsiElement> {
        val paramNames = sortedMapOf<Int, String>()

        var hasParamAnnotation = false
        val paramList = method.parameterList
        var index = 0
        val paramTypeCache = arrayOfNulls<PsiElement>(paramList.parameters.size)
        for (param in paramList.parameters) {
            val paramPsiType = param.type
            val annotation = param.getAnnotation("org.apache.ibatis.annotations.Param")

            // 解析参数类型
            var paramType: PsiClass? = null
            if (paramPsiType is PsiClassType) {
                paramType = (param.type as PsiClassType).resolve() ?: continue
                // cached
                paramTypeCache[index] = paramType
            }

            if (paramType != null && isSpecialParamType(paramType)) {
                continue
            }

            var name = annotation?.findAttributeValue("value")?.text?.removeSurrounding("\"")
            if (name == null) {
                // TODO isUseActualParamName
                if (name == null) {
                    name = paramNames.size.toString()
                }
            } else {
                hasParamAnnotation = true
            }

            paramNames[index++] = name
        }

        val paramNameMap = mutableMapOf<String, PsiElement>()
        if (paramNames.isEmpty()) {
            return emptyMap()
        } else if (!hasParamAnnotation && paramNames.size == 1) {
            val paramType = paramTypeCache[0]
            if (paramType is PsiClass) {
                if (paramType.qualifiedName == "java.util.Collection") {
                    paramNameMap["collection"] = paramType
                } else if (paramType.qualifiedName == "java.util.List") {
                    paramNameMap["list"] = paramType
                } else {
                    resolvePsiElement(paramType, "", false).forEach {
                        val name = when (it) {
                            is PsiField -> {
                                it.name
                            }
                            is PsiMethod -> {
                                it.name
                            }
                            else -> {
                                null
                            }
                        }
                        if (name != null) {
                            paramNameMap[name] = it
                        }
                    }
                }
            } else if (paramList.parameters[0].type is PsiArrayType) {
                // TODO 数组
            }
        } else {
            var i = 0
            paramNames.forEach { (key, value) ->
                paramNameMap[value] = paramTypeCache[key]!!
                val genericParamName = GENERIC_NAME_PREFIX + (++i)
                if (!paramNames.containsValue(genericParamName)) {
                    paramNameMap[genericParamName] = paramTypeCache[key]!!
                }
            }
        }

        return paramNameMap
    }

    private fun isSpecialParamType(paramType: PsiClass): Boolean {
        return paramType.qualifiedName == "org.apache.ibatis.session.RowBounds" ||
                paramType.qualifiedName == "org.apache.ibatis.session.ResultHandler"
    }

    private fun resolvePsiElement(
        psiElement: PsiElement?,
        paramName: String,
        strict: Boolean = true
    ): List<PsiElement> {
        if (psiElement == null) return emptyList()

        if (psiElement is PsiClass) {
            // TODO 方法和参数判断逻辑
            val results = mutableListOf<PsiElement>()

            // 字段
            val fieldResults = psiElement.allFields.filter {
                if (strict) it.name == paramName else it.name.startsWith(paramName)
            }
            if (fieldResults.isNotEmpty()) {
                if (!strict) {
                    results.addAll(fieldResults)
                } else {
                    return fieldResults
                }
            }

            // 方法
            val methodResults = psiElement.allMethods
                // 过滤构造方法和带参方法 TODO 可能不需要过滤带参方法
                .filter { !it.isConstructor && !it.hasParameters() }
                .filter {
                    // 过滤 void 方法 TODO 可能不需要过滤
                    val returnType = it.returnType
                    if (returnType is PsiPrimitiveType) {
                        returnType.name != "void"
                    } else {
                        true
                    }
                }
                .filter {
                    if (strict) it.name == paramName else it.name.startsWith(paramName)
                }
            if (methodResults.isNotEmpty()) {
                if (!strict) {
                    results.addAll(methodResults)
                } else {
                    return methodResults
                }
            }
            return results
        } else if (psiElement is PsiMethod) {
            val returnType = psiElement.returnType ?: return emptyList()
            if (returnType is PsiClassType) {
                val returnClass = returnType.resolve() ?: return emptyList()
                return resolvePsiElement(returnClass, paramName, strict)
            }
        } else if (psiElement is PsiField) {
            val fieldType = psiElement.type
            if (fieldType is PsiClassType) {
                val fieldClass = fieldType.resolve() ?: return emptyList()
                return resolvePsiElement(fieldClass, paramName, strict)
            }
        }

        return emptyList()
    }

}