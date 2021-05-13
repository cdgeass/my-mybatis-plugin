package io.github.cdgeass.codeInsight.reference

import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.codeInsight.lookup.AutoCompletionPolicy
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.pom.java.LanguageLevel
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiParameter
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.PsiType
import com.intellij.psi.ResolveResult
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.util.PlatformIcons
import com.intellij.util.xml.DomManager
import com.jetbrains.rd.util.first
import io.github.cdgeass.codeInsight.dom.element.Statement
import io.github.cdgeass.util.resolveGeneric

/**
 * @author cdgeass
 * @since 2021/3/28
 */
private const val GENERIC_NAME_PREFIX = "param"

class ParamReference(
    element: PsiElement,
    private val textRange: TextRange,
    private val preExpression: String,
    val myKey: String = element.text.substring(
        textRange.startOffset,
        textRange.endOffset
    ).replace(CompletionUtilCore.DUMMY_IDENTIFIER, "")
        .replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "")
) : PsiPolyVariantReferenceBase<PsiElement>(element, textRange) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val paramMap = getParamMap(element)
        if (preExpression.isBlank()) {
            return PsiElementResolveResult.createResults(paramMap.filter { it.key == myKey }.values)
        }

        val paramPsiElement = iterateParam(preExpression, paramMap)
        return PsiElementResolveResult.createResults(extractFieldAndMethod(paramPsiElement, myKey, true))
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
                    ).withAutoCompletionPolicy(AutoCompletionPolicy.SETTINGS_DEPENDENT)
            }.toTypedArray()
        }

        val paramPsiElement: PsiElement? = iterateParam(preExpression, paramMap)
        return extractFieldAndMethod(paramPsiElement, myKey, false).toTypedArray()
    }

    override fun isSoft(): Boolean {
        return true
    }

    private fun isSpecialParamType(paramType: PsiClass): Boolean {
        return paramType.qualifiedName == "org.apache.ibatis.session.RowBounds" ||
                paramType.qualifiedName == "org.apache.ibatis.session.ResultHandler"
    }

    /**
     * @see org.apache.ibatis.reflection.ParamNameResolver
     */
    private fun resolveParamName(method: PsiMethod): Pair<Map<String, PsiElement>, Map<String, PsiType>> {
        val paramNames = sortedMapOf<Int, String>()

        var hasParamAnnotation = false
        val paramList = method.parameterList
        var index = 0
        val paramClassCache = arrayOfNulls<Pair<PsiType, PsiElement>>(paramList.parameters.size)
        for (param in paramList.parameters) {
            val paramPsiType = param.type
            val annotation = param.getAnnotation("org.apache.ibatis.annotations.Param")

            // 解析参数类型
            var paramClass: PsiClass? = null
            if (paramPsiType is PsiClassType) {
                paramClass = (param.type as PsiClassType).resolve() ?: continue
                // cached
                paramClassCache[index] = Pair(paramPsiType, paramClass)
            }

            if (paramClass != null && isSpecialParamType(paramClass)) {
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
        val paramTypeMap = mutableMapOf<String, PsiType>()
        if (paramNames.isEmpty()) {
            return Pair(paramNameMap, paramTypeMap)
        } else if (!hasParamAnnotation && paramNames.size == 1) {
            val paramTypePair = paramClassCache[paramNames.firstKey()]
            val paramPsiType = paramTypePair!!.first
            val paramClass = paramTypePair.second
            if (paramClass is PsiClass) {
                when (paramClass.qualifiedName) {
                    "java.util.Collection" -> {
                        paramNameMap["collection"] = paramClass
                        paramTypeMap["collection"] = paramPsiType
                    }
                    "java.util.List" -> {
                        paramNameMap["list"] = paramClass
                        paramTypeMap["list"] = paramPsiType
                    }
                    else -> {
                        extractFieldAndMethod(paramClass, "", false).forEach {
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
                                paramTypeMap[name] = paramPsiType
                            }
                        }
                    }
                }
            } else if (paramPsiType is PsiArrayType) {
                val psiElementFactory = PsiElementFactory.getInstance(myElement.project)
                paramNameMap["array"] = psiElementFactory.getArrayClass(LanguageLevel.JDK_1_8)
                paramTypeMap["array"] = paramPsiType
            }
        } else {
            var i = 0
            paramNames.forEach { (key, value) ->
                paramNameMap[value] = paramClassCache[key]!!.second
                paramTypeMap[value] = paramClassCache[key]!!.first
                val genericParamName = GENERIC_NAME_PREFIX + (++i)
                if (!paramNames.containsValue(genericParamName)) {
                    paramNameMap[genericParamName] = paramClassCache[key]!!.second
                    paramTypeMap[genericParamName] = paramClassCache[key]!!.first
                }
            }
        }

        return Pair(paramNameMap, paramTypeMap)
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

        // 解析方法参数及其 PsiType
        val paramNamePair = resolveParamName(statementMethod)
        paramMap.putAll(paramNamePair.first)

        // foreach
        val foreachTag =
            PsiTreeUtil.findFirstParent(element) { it is XmlTag && (it.name == "foreach") }?.let { it as XmlTag }
        if (foreachTag != null) {
            val collectionExpression = foreachTag.getAttributeValue("collection")
            val itemName = foreachTag.getAttributeValue("item")
            if (collectionExpression != null && itemName != null) {
                val split = collectionExpression.split(".")
                val collectionPsiElement = if (split.size == 1) {
                    paramMap[split.first()]
                } else {
                    iterateParam(collectionExpression, paramMap)
                }
                if (collectionPsiElement != null) {
                    if (collectionPsiElement is PsiField || collectionPsiElement is PsiMethod || collectionPsiElement is PsiParameter) {
                        val fieldTye = when (collectionPsiElement) {
                            is PsiField -> {
                                collectionPsiElement.type
                            }
                            is PsiMethod -> {
                                collectionPsiElement.returnType
                            }
                            else -> {
                                (collectionPsiElement as PsiParameter).type
                            }
                        }
                        if (fieldTye is PsiClassType) {
                            val fieldClass = fieldTye.resolve()
                            val genericMap = fieldTye.resolveGenerics().substitutor.substitutionMap
                            // 参数类型为 Collection
                            if (fieldClass?.supers?.any { superClass ->
                                    superClass.qualifiedName == "java.util.Collection"
                                } == true
                            ) {
                                val genericType = genericMap.first().value
                                if (genericType is PsiClassType) {
                                    genericType.resolve()?.let { genericClass ->
                                        paramMap[itemName] = genericClass
                                    }
                                } else if (genericType is PsiArrayType) {
                                    val psiElementFactory = PsiElementFactory.getInstance(myElement.project)
                                    paramMap[itemName] = psiElementFactory.createClass("array")
                                }
                            }
                        }
                    } else if (collectionPsiElement is PsiClass) {
                        val psiType = paramNamePair.second[split.first()]
                        if (psiType != null) {
                            resolveGeneric(psiType)?.let {
                                paramMap[itemName] = it
                            }
                        }
                    }
                }
            }
        }

        return paramMap
    }

    private fun iterateParam(expression: String, paramMap: Map<String, PsiElement>): PsiElement? {
        var paramPsiElement: PsiElement? = null
        expression.split(".").filter { it.isNotBlank() }.forEachIndexed { index, param ->
            paramPsiElement = if (index == 0) {
                paramMap[param] ?: return null
            } else {
                val tempPsiElements = extractFieldAndMethod(paramPsiElement, param, false)
                if (tempPsiElements.isEmpty()) return null
                tempPsiElements.first()
            }
        }
        return paramPsiElement
    }

    private fun extractFieldAndMethod(
        psiElement: PsiElement?,
        paramName: String,
        strict: Boolean = true
    ): Collection<PsiElement> {
        if (psiElement == null) return emptyList()

        if (psiElement is PsiClass) {
            val results = mutableMapOf<String, PsiElement>()

            // 字段
            val fields = psiElement.allFields.filter {
                if (strict) it.name == paramName else it.name.startsWith(paramName)
            }
            if (fields.isNotEmpty()) {
                if (!strict) {
                    fields.forEach { filed ->
                        results[filed.name] = filed
                    }
                } else {
                    return fields
                }
            }

            // 方法
            val paramNameWithoutBrackets = paramName.replace("()", "")
            val methods = psiElement.allMethods
                // 过滤构造方法和带参方法
                .filter { !it.isConstructor && !it.hasParameters() }
                .filter {
                    // 过滤 void 方法
                    val returnType = it.returnType
                    if (returnType is PsiPrimitiveType) {
                        returnType.name != "void"
                    } else {
                        true
                    }
                }
                .filter {
                    val name = methodName(it)
                    if (strict) name == paramNameWithoutBrackets else name.startsWith(paramNameWithoutBrackets)
                }
            if (methods.isNotEmpty()) {
                if (!strict) {
                    methods.forEach { method ->
                        val name = methodName(method)
                        results.putIfAbsent(name, method)
                    }
                } else {
                    return methods
                }
            }
            return results.values
        } else if (psiElement is PsiMethod) {
            val returnType = psiElement.returnType ?: return emptyList()
            if (returnType is PsiClassType) {
                val returnClass = returnType.resolve() ?: return emptyList()
                return extractFieldAndMethod(returnClass, paramName, strict)
            }
        } else if (psiElement is PsiField) {
            val fieldType = psiElement.type
            if (fieldType is PsiClassType) {
                val fieldClass = fieldType.resolve() ?: return emptyList()
                return extractFieldAndMethod(fieldClass, paramName, strict)
            }
        }

        return emptyList()
    }

    private fun methodName(method: PsiMethod): String {
        var name = method.name

        name = when {
            name.startsWith("is") -> {
                name.substringAfter("is")
            }
            name.startsWith("get") -> {
                name.substringAfter("get")
            }
            else -> {
                name
            }
        }

        return if (name.length == 1) {
            name.toLowerCase()
        } else {
            name[0].toLowerCase() + name.substring(1)
        }
    }
}
