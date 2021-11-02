package io.github.cdgeass.codeInsight.reference

import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.codeInsight.lookup.AutoCompletionPolicy
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.LanguageLevelProjectExtension
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.util.PlatformIcons
import com.intellij.util.xml.DomManager
import com.jetbrains.rd.util.first
import io.github.cdgeass.codeInsight.dom.element.Statement
import io.github.cdgeass.codeInsight.util.getConfigurations
import io.github.cdgeass.util.resolveGeneric
import io.github.cdgeass.util.resolveLombokField
import io.github.cdgeass.util.resolveLombokFields
import java.util.*

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
    ).replace(CompletionUtilCore.DUMMY_IDENTIFIER, "").replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "")
) : PsiPolyVariantReferenceBase<PsiElement>(element, textRange) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val paramMap = resolveParams(element)
        if (preExpression.isBlank()) {
            return PsiElementResolveResult.createResults(paramMap.filter { it.key == myKey }.map { it.value.first })
        }

        val paramPsiElement = iterateParam(preExpression, paramMap)
        return PsiElementResolveResult.createResults(extractFieldAndMethod(paramPsiElement, myKey, true))
    }

    override fun getVariants(): Array<Any> {
        val paramMap = resolveParams(element)
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
     * 根据 org.apache.ibatis.reflection.ParamNameResolver 规则解析参数名称
     */
    private fun resolveParamName(method: PsiMethod): Pair<Map<String, PsiElement>, Map<String, Pair<PsiElement?, PsiType>>> {
        val paramNames = sortedMapOf<Int, String>()

        var hasParamAnnotation = false
        val paramList = method.parameterList
        var index = 0
        val paramClassReferenceCache = arrayOfNulls<Triple<PsiElement?, PsiType, PsiElement>>(paramList.parameters.size)
        for (param in paramList.parameters) {
            val paramPsiType = param.type
            val annotation = param.getAnnotation("org.apache.ibatis.annotations.Param")

            // 解析参数类型
            var paramClass: PsiClass? = null
            if (paramPsiType is PsiClassType) {
                paramClass = (param.type as PsiClassType).resolve() ?: continue
                // cached
                paramClassReferenceCache[index] = Triple(paramClass, paramPsiType, param)
            }

            if (paramClass != null && isSpecialParamType(paramClass)) {
                continue
            }

            val annotationValue = annotation?.findAttributeValue("value")
            var name = annotationValue?.text?.removeSurrounding("\"")
            if (name == null) {
                if (isUseActualParamName(method.project)) {
                    name = param.name
                }
                if (name == null) {
                    name = paramNames.size.toString()
                }
            } else {
                hasParamAnnotation = true
                paramClassReferenceCache[index] = Triple(paramClass, paramPsiType, annotationValue!!)
            }

            paramNames[index++] = name
        }

        // 参数名: referenceTarget
        val paramNameMap = mutableMapOf<String, PsiElement>()
        // 参数名: 参数类型
        val paramTypeMap = mutableMapOf<String, Pair<PsiElement?, PsiType>>()
        if (paramNames.isEmpty()) {
            return Pair(paramNameMap, paramTypeMap)
        } else if (!hasParamAnnotation && paramNames.size == 1) {
            val paramTypeReferenceTriple = paramClassReferenceCache[paramNames.firstKey()]
            val paramClassElement = paramTypeReferenceTriple!!.first
            val paramPsiType = paramTypeReferenceTriple.second
            val paramReferenceTarget = paramTypeReferenceTriple.third

            if (paramReferenceTarget is PsiClass) {
                when (paramReferenceTarget.qualifiedName) {
                    "java.util.Collection" -> {
                        paramNameMap["collection"] = paramReferenceTarget
                        paramTypeMap["collection"] = Pair(paramClassElement, paramPsiType)
                    }
                    "java.util.List" -> {
                        paramNameMap["list"] = paramReferenceTarget
                        paramTypeMap["list"] = Pair(paramClassElement, paramPsiType)
                    }
                    else -> {
                        extractFieldAndMethod(paramReferenceTarget, "", false).forEach {
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
                                paramTypeMap[name] = Pair(paramClassElement, paramPsiType)
                            }
                        }
                    }
                }
            } else if (paramPsiType is PsiArrayType) {
                val psiElementFactory = PsiElementFactory.getInstance(myElement.project)
                paramNameMap["array"] =
                    psiElementFactory.getArrayClass(LanguageLevelProjectExtension.getInstance(element.project).languageLevel)
                paramTypeMap["array"] = Pair(paramClassElement, paramPsiType)
            }
        }

        var i = 0
        paramNames.forEach { (key, value) ->
            paramNameMap[value] = paramClassReferenceCache[key]!!.third
            paramTypeMap[value] = Pair(paramClassReferenceCache[key]!!.first, paramClassReferenceCache[key]!!.second)
            val genericParamName = GENERIC_NAME_PREFIX + (++i)
            if (!paramNames.containsValue(genericParamName)) {
                paramNameMap[genericParamName] = paramClassReferenceCache[key]!!.third
                paramTypeMap[genericParamName] =
                    Pair(paramClassReferenceCache[key]!!.first, paramClassReferenceCache[key]!!.second)
            }
        }

        return Pair(paramNameMap, paramTypeMap)
    }

    /**
     * 解析参数列表
     */
    private fun resolveParams(element: PsiElement): Map<String, Pair<PsiElement, PsiElement?>> {
        val paramMap = mutableMapOf<String, Pair<PsiElement, PsiElement?>>()

        // 获取 Statement 绑定方法上的参数
        val statementTag = PsiTreeUtil.findFirstParent(element) {
            it is XmlTag && (it.name == "select" || it.name == "delete" || it.name == "update" || it.name == "insert")
        } ?: return emptyMap()
        val domManager = DomManager.getDomManager(element.project)
        val statement = domManager.getDomElement(statementTag as XmlTag) as Statement
        val statementMethod = statement.getId().value ?: return emptyMap()

        // 解析方法参数及其 PsiType
        val paramNamePair = resolveParamName(statementMethod)
        val first = paramNamePair.first
        val second = paramNamePair.second
        first.forEach { (name, reference) ->
            paramMap[name] = Pair(reference, second[name]!!.first)
        }

        // foreach
        val foreachTag =
            PsiTreeUtil.findFirstParent(element) { it is XmlTag && (it.name == "foreach") }?.let { it as XmlTag }
        if (foreachTag != null) {
            val collectionExpression = foreachTag.getAttributeValue("collection")
            val itemName = foreachTag.getAttributeValue("item")
            if (collectionExpression != null && itemName != null) {
                val split = collectionExpression.split(".")
                val collectionPsiElement = if (split.size == 1) {
                    paramMap[split.first()]?.first
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
                                        paramMap[itemName] = Pair(collectionPsiElement, genericClass)
                                    }
                                } else if (genericType is PsiArrayType) {
                                    val psiElementFactory = PsiElementFactory.getInstance(myElement.project)
                                    paramMap[itemName] =
                                        Pair(collectionPsiElement, psiElementFactory.createClass("array"))
                                }
                            }
                        }
                    } else if (collectionPsiElement is PsiClass) {
                        val psiType = paramNamePair.second[split.first()]?.second
                        if (psiType != null) {
                            resolveGeneric(psiType)?.let {
                                paramMap[itemName] = Pair(collectionPsiElement, it)
                            }
                        }
                    }
                }
            }
        }

        return paramMap
    }

    /**
     * 是否使用方法签名中的名称作为参数名称, 默认为true
     */
    private fun isUseActualParamName(project: Project): Boolean {
        val configurations = getConfigurations(project)
        var isUseActualParamName = true
        configurations.forEach {
            val useActualParamName = it.getSettings().getUseActualParamName().value
            if (useActualParamName != null) {
                isUseActualParamName = useActualParamName
            }
        }
        return isUseActualParamName
    }

    /**
     * 按调用遍历表达式
     */
    private fun iterateParam(expression: String, paramMap: Map<String, Pair<PsiElement, PsiElement?>>): PsiElement? {
        var paramPsiElement: PsiElement? = null
        expression.split(".").filter { it.isNotBlank() }.forEachIndexed { index, param ->
            paramPsiElement = if (index == 0) {
                paramMap[param]?.second ?: return null
            } else {
                val tempPsiElements = extractFieldAndMethod(paramPsiElement, param, false)
                if (tempPsiElements.isEmpty()) return null
                tempPsiElements.first()
            }
        }
        return paramPsiElement
    }

    /**
     * 提取参数的方法和属性
     */
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
                        results[filed.name] = resolveLombokField(filed)
                    }
                } else {
                    return resolveLombokFields(fields)
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
            name.lowercase(Locale.getDefault())
        } else {
            name[0].lowercaseChar() + name.substring(1)
        }
    }
}
