package io.github.cdgeass.codeInsight

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightMethodBuilder
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.util.xml.DomManager
import com.jetbrains.rd.util.first
import io.github.cdgeass.codeInsight.dom.element.Statement
import io.github.cdgeass.codeInsight.util.getConfigurations
import io.github.cdgeass.codeInsight.util.isStatementTag
import io.github.cdgeass.util.createArrayClass
import io.github.cdgeass.util.createIntClass
import java.util.*

/**
 * @author cdgeass
 * @since 2021-11-02
 */
data class MyLiteralExpressionParameter(
    /**
     * 参数名称
     */
    val name: String,
    /**
     * 参数类型
     */
    val type: PsiType,
    /**
     * 参数指向的 psiElement
     */
    val ref: PsiElement,
    /**
     * 参数名是否来源于注解
     */
    val hasAnno: Boolean
)

class MyLiteralExpressionResolver(
    private val myElement: PsiElement,
) {

    private lateinit var paramNameMap: Map<String, MyLiteralExpressionParameter>

    fun resolve(): Map<String, MyLiteralExpressionParameter> {
        // 读取 dao 方法参数列表
        val bindMethodParameterList = getBindMethodParameterList() ?: return emptyMap()
        // 解析参数列表
        val paramList = resolveParameterList(bindMethodParameterList)

        // 将参数绑定到表达式可用的参数名上
        val paramNameMap = bindParamName(paramList).toMutableMap()
        // element 是否在 <foreach/> 内并进行处理
        resolveForeachContext(myElement, paramNameMap)

        // 结果缓存
        this.paramNameMap = paramNameMap

        return this.paramNameMap
    }

    /**
     * 获取 statement 绑定方法上的参数列表
     */
    private fun getBindMethodParameterList(): PsiParameterList? {
        val statementTag = (PsiTreeUtil.findFirstParent(myElement) { isStatementTag(it) } ?: return null) as XmlTag
        val domManager = DomManager.getDomManager(myElement.project)
        val statement = domManager.getDomElement(statementTag) as Statement
        val bindMethod = statement.getId().value
        return bindMethod?.parameterList
    }

    /**
     * 根据 org.apache.ibatis.reflection.ParamNameResolver 规则解析参数名称
     * 排除 MyBatis 内嵌参数
     * 1. 读取 @Param 注解如果有该注解使用 value 值作为参数名
     * 2. 如果配置允许, 直接使用方法签名中参数名作为参数名
     * 3. 如果上述均无对应参数名则使用参数 index 作为参数名
     */
    private fun resolveParameterList(parameterList: PsiParameterList): List<MyLiteralExpressionParameter> {
        val paramList = mutableListOf<MyLiteralExpressionParameter>()

        for (param in parameterList.parameters) {
            // 参数类型
            val psiType = param.type
            // 如果是 MyBatis 内嵌的参数则跳过
            if (isSpecialParamType(psiType)) {
                continue
            }

            // 读取参数 @Param 注解
            val annoValueElement = param.getAnnotation("org.apache.ibatis.annotations.Param")
                ?.findAttributeValue("value")
            val annoValue = annoValueElement?.text?.removeSurrounding("\"")

            val name: String
            val refElement: PsiElement
            var hasAnno = false
            if (annoValue != null) {
                name = annoValue
                refElement = annoValueElement.navigationElement
                hasAnno = true
            } else if (isUseActualParamName(myElement.project)) {
                // 直接使用方法签名中参数名称
                name = param.name
                refElement = param.navigationElement
            } else {
                // 使用参数 index 作为参数名称
                name = paramList.size.toString()
                refElement = param.navigationElement
            }

            paramList.add(MyLiteralExpressionParameter(name, psiType, refElement, hasAnno))
        }
        return paramList
    }

    /**
     * MyBatis 内嵌参数不进行解析
     */
    private fun isSpecialParamType(psiType: PsiType): Boolean {
        if (psiType is PsiClassType) {
            val typeClassName = psiType.resolve()?.qualifiedName
            return typeClassName == "org.apache.ibatis.session.RowBounds" ||
                    typeClassName == "org.apache.ibatis.session.ResultHandler"
        }
        return false
    }

    /**
     * 读取配置是否使用方法签名中的名称作为参数名称, 默认为true
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
     * 将 dao 方法中参数绑定到表达式中可用的参数名称上
     * 1. 如果仅有一个参数提取参数类型装的字段方法作用可用参数名
     * 2. 如果参数是 Collection List 类则添加 collection list 作为可用参数名
     * 3. 如果参数是 [] 则添加 array 作为可用参数名
     * 4. 添加 param数字 作为可用参数名
     */
    private fun bindParamName(paramList: List<MyLiteralExpressionParameter>): Map<String, MyLiteralExpressionParameter> {
        val paramNameMap = mutableMapOf<String, MyLiteralExpressionParameter>()

        if (paramList.size == 1) {
            val param = paramList.first()
            val name = param.name
            val type = param.type

            paramNameMap[name] = param
            if (type is PsiClassType) {
                val psiClass = type.resolve()
                val typeClassName = psiClass?.qualifiedName
                when {
                    typeClassName == "java.util.Collection" -> {
                        paramNameMap["collection"] = param
                    }
                    typeClassName == "java.util.List" -> {
                        paramNameMap["collection"] = param
                    }
                    psiClass != null -> {
                        paramNameMap.putAll(getAllSubElement(psiClass))
                    }
                }
            } else if (type is PsiArrayType) {
                paramNameMap["array"] = param
            }
        } else {
            paramList.forEachIndexed { index, param ->
                val name = param.name

                paramNameMap[name] = param

                val genericParamName = "param" + (index + 1)
                if (paramNameMap[genericParamName] == null) {
                    paramNameMap[genericParamName] = param
                }
            }
        }

        return paramNameMap
    }

    /**
     * 处理 <foreach/>
     */
    private fun resolveForeachContext(element: PsiElement, paramNameMap: MutableMap<String, MyLiteralExpressionParameter>) {
        val tag = PsiTreeUtil.findFirstParent(element, true) { it is XmlTag && it.name == "foreach" }
            ?.let { it as XmlTag }
            ?: return

        // 向上遍历是否还存在 <foreach/>
        resolveForeachContext(tag, paramNameMap)

        val expression = tag.getAttributeValue("collection") ?: return
        val item = tag.getAttributeValue("item") ?: return
        val index = tag.getAttributeValue("index")

        // 处理泛型
        val resolveGeneric: (PsiType) -> PsiClass? = { type: PsiType ->
            when (type) {
                is PsiClassType -> {
                    type.resolve()
                }
                is PsiArrayType -> {
                    createArrayClass(element.project)
                }
                else -> {
                    null
                }
            }
        }

        // 计算 collection 内表达式
        val expressionElement = calculateExpression(expression, paramNameMap)?.ref
        // 获取 index 和 item 的值
        if (expressionElement != null) {
            val expressionType = when (expressionElement) {
                is PsiField -> expressionElement.type
                is PsiMethod -> expressionElement.returnType
                else -> null
            }
            if (expressionType is PsiClassType) {
                val expressionClass = expressionType.resolve()!!
                val genericTypeMap = expressionType.resolveGenerics().substitutor.substitutionMap
                if (expressionClass.supers.any { it.qualifiedName == "java.util.Collection" }) {
                    // 集合
                    val itemType = genericTypeMap.first().value
                    paramNameMap[item] = MyLiteralExpressionParameter(item, itemType, resolveGeneric(itemType)!!, false)

                    index?.let {
                        paramNameMap[index] =
                            MyLiteralExpressionParameter(item, PsiPrimitiveType.INT, createIntClass(element.project), false)
                    }
                } else if (expressionClass.supers.any { it.qualifiedName == "java.util.Map" }) {
                    // map
                    val typeParameters = expressionClass.typeParameters
                    val kGenericType = genericTypeMap[typeParameters[0]]!!
                    index?.let {
                        paramNameMap[index] = MyLiteralExpressionParameter(index, kGenericType, resolveGeneric(kGenericType)!!, false)
                    }
                    val vGenericType = genericTypeMap[typeParameters[1]]!!
                    paramNameMap[item] = MyLiteralExpressionParameter(item, vGenericType, resolveGeneric(vGenericType)!!, false)
                }
            } else if (expressionType is PsiArrayType) {
                // 数组
                val componentType = expressionType.componentType
                paramNameMap[item] = MyLiteralExpressionParameter(item, componentType, resolveGeneric(componentType)!!, false)
                index?.let {
                    paramNameMap[index] = MyLiteralExpressionParameter(item, PsiPrimitiveType.INT, createIntClass(element.project), false)
                }
            }
        }
    }

    /**
     * 计算表达式结果
     */
    private fun calculateExpression(
        expression: String,
        paramNameMap: Map<String, MyLiteralExpressionParameter> = this.paramNameMap
    ): MyLiteralExpressionParameter? {
        var param: MyLiteralExpressionParameter? = null
        expression.split(".").filter { it.isNotBlank() }.forEachIndexed { index, paramName ->
            if (index == 0) {
                param = paramNameMap[paramName]
            } else {
                if (param?.type is PsiClassType) {
                    val subElementNameMap = getAllSubElement((param?.type as PsiClassType).resolve()!!)
                    if (subElementNameMap.isEmpty()) {
                        param = null
                    } else {
                        subElementNameMap.forEach { (subParamName, subParam) ->
                            if (paramName == subParamName) {
                                param = subParam
                                return@forEachIndexed
                            }
                        }
                        param = null
                    }
                }
            }
        }
        return param
    }
}

/**
 * 获取 psi element 下所有可用来访问的 element
 * 1. 如果 element 是类则获取其所有的字段和方法, 如果有字段的 getter 方法则优先是由字段来访问
 * 2. 如果 element 是字段则递归访问字段的类内的字段和方法
 * 3. 如果 element 是方法则递归访问方法返回的类内的字段和方法
 */
fun getAllSubElement(element: PsiElement): Map<String, MyLiteralExpressionParameter> {
    if (element is PsiClass) {
        val subElementNameMap = mutableMapOf<String, MyLiteralExpressionParameter>()

        // 属性
        element.allFields.forEach { field ->
            subElementNameMap[field.name] = MyLiteralExpressionParameter(field.name, field.type, field, false)
        }

        // 方法 过滤构造方法, void 方法, 带参方法
        element.allMethods
            .filter { !it.isConstructor && !it.hasParameters() }
            .filter { it.returnType !is PsiPrimitiveType || (it.returnType as PsiPrimitiveType).name != "void" }
            .forEach { method ->
                // 将方法名转换, 如果已存在字段可用于方法则使用字段来访问
                val name = convertMethodName(method.name)
                if (method is LightMethodBuilder) {
                    // 兼容 lombok
                    subElementNameMap[name] = MyLiteralExpressionParameter(name, method.returnType!!, method, false)
                } else {
                    subElementNameMap.putIfAbsent(
                        name,
                        MyLiteralExpressionParameter(name, method.returnType!!, method, false)
                    )
                }
            }

        return subElementNameMap
    } else if (element is PsiField) {
        if (element.type is PsiClassType) {
            val elementClass = (element.type as PsiClassType).resolve() ?: return emptyMap()
            return getAllSubElement(elementClass)
        }
    } else if (element is PsiMethod) {
        if (element.returnType is PsiClassType) {
            val returnClass = (element.returnType as PsiClassType).resolve() ?: return emptyMap()
            return getAllSubElement(returnClass)
        }
    }

    return emptyMap()
}

/**
 * 如果方法是 getter 方法则转换成对应的字段名
 */
private fun convertMethodName(name: String): String {
    val tempName = when {
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

    return if (tempName.length == 1) {
        tempName.lowercase(Locale.getDefault())
    } else {
        tempName[0].lowercaseChar() + tempName.substring(1)
    }
}