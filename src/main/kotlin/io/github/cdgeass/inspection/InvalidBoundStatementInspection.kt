package io.github.cdgeass.inspection

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.xml.XmlFile
import com.intellij.util.xml.DomManager
import io.github.cdgeass.PluginBundle
import io.github.cdgeass.codeInsight.dom.element.Mapper
import io.github.cdgeass.codeInsight.dom.element.Select
import io.github.cdgeass.codeInsight.util.findByNamespace

/**
 * @author cdgeass
 * @since 2021-06-22
 */
class InvalidBoundStatementInspection : AbstractBaseJavaLocalInspectionTool() {

    override fun getShortName(): String {
        return "InvalidBoundStatement"
    }

    /**
     * 根据方法注解判断是否是基于注解的 statement
     */
    private fun isStatementAnnotation(annotation: PsiAnnotation): Boolean {
        var annotationQualifiedName = annotation.qualifiedName ?: return false
        if (!annotationQualifiedName.startsWith("org.apache.ibatis.annotations")) {
            return false
        }
        annotationQualifiedName = annotationQualifiedName.split(".").let { it[it.size - 1] }
        return annotationQualifiedName.endsWith("Provider")
                || annotationQualifiedName == "Insert"
                || annotationQualifiedName == "Update"
                || annotationQualifiedName == "Delete"
                || annotationQualifiedName == "Select"
    }

    override fun checkMethod(
        method: PsiMethod,
        manager: InspectionManager,
        isOnTheFly: Boolean
    ): Array<ProblemDescriptor>? {
        // 跳过基于注解的方法
        if (method.annotations.any { annotation -> isStatementAnnotation(annotation) }) {
            return null
        }
        // 跳过 default 方法
        if (method.modifierList.hasExplicitModifier("default")) {
            return null
        }

        val qName = method.containingClass?.qualifiedName ?: return null
        val xmlFiles = findByNamespace(qName, method.project)

        val domManager = DomManager.getDomManager(method.project)
        val mappers = xmlFiles.mapNotNull { it.rootTag }
            .mapNotNull { domManager.getDomElement(it)?.let { domElement -> domElement as Mapper } }
        if (mappers.isEmpty()) {
            return null
        }

        var hasStatement: Boolean
        for (mapper in mappers) {
            hasStatement = mapper.getStatements().stream().anyMatch { statement ->
                statement.getId().value == method
            }
            if (hasStatement) {
                return null
            }
        }

        val quickFixArray =
            xmlFiles.map { AddStatementFix(method.project, it, method) }.toTypedArray()

        return arrayOf(
            manager.createProblemDescriptor(
                method,
                PluginBundle.message("inspection.invalidBoundStatement"),
                quickFixArray,
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                true,
                false
            )
        )
    }
}

class AddStatementFix(
    project: Project,
    xmlFile: XmlFile,
    method: PsiMethod,
) : LocalQuickFix {

    private val myXmlFile: SmartPsiElementPointer<XmlFile> = SmartPointerManager.getInstance(project)
        .createSmartPsiElementPointer(xmlFile)

    private val myMethod: SmartPsiElementPointer<PsiMethod> = SmartPointerManager.getInstance(project)
        .createSmartPsiElementPointer(method)

    override fun getName(): String {
        return PluginBundle.message("inspection.addStatementIn", myXmlFile.element?.name ?: "")
    }

    override fun getFamilyName(): String {
        return PluginBundle.message("inspection.addStatement")
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val rootTag = myXmlFile.element?.rootTag ?: return

        val domManager = DomManager.getDomManager(project)
        val mapper = domManager.getDomElement(rootTag)
        if (mapper !is Mapper) {
            return
        }

        val statementName = myMethod.element?.name ?: ""
        val statement = when {
            statementName.startsWith("select") -> {
                mapper.addSelect().apply {
                    addReturnType(this)
                }
            }
            statementName.startsWith("find") -> {
                mapper.addSelect().apply {
                    addReturnType(this)
                }
            }
            statementName.startsWith("insert") -> {
                mapper.addInsert()
            }
            statementName.startsWith("delete") -> {
                mapper.addDelete()
            }
            statementName.startsWith("update") -> {
                mapper.addUpdate()
            }
            else -> {
                mapper.addSelect()
            }
        }
        statement.getId().stringValue = statementName
        statement.xmlTag?.value?.text = "\n"
    }

    private fun addReturnType(select: Select) {
        val returnType = myMethod.element?.returnType
        if (returnType == null || returnType == PsiType.VOID || returnType == PsiType.NULL) {
            return
        }

        val namespace = myXmlFile.element?.rootTag?.getAttributeValue("namespace") ?: return
        val xmlFiles = findByNamespace(namespace, myXmlFile.project)

        val domManager = DomManager.getDomManager(myXmlFile.project)
        val mappers = xmlFiles.mapNotNull { it.rootTag }.map { domManager.getDomElement(it) as Mapper }

        if (returnType is PsiClassType) {
            // 判断返回值是否为泛型类型
            val returnClass = if (returnType.isRaw) {
                returnType.resolve()
            } else {
                val substitutionMap = returnType.resolveGenerics().substitutor.substitutionMap
                // 如果有多个泛型参数直接使用原类型
                if (substitutionMap.size != 1) {
                    returnType.resolve()
                } else {
                    val psiTye = substitutionMap.values.first()
                    if (psiTye is PsiClassType) {
                        psiTye.resolve()
                    } else {
                        null
                    }
                }
            }
            if (returnClass != null) {
                mappers.forEach { mapper ->
                    mapper.getResultMaps().forEach { rm ->
                        if (rm.getType().value == returnClass) {
                            select.getResultMap().value = rm
                            return
                        }
                    }
                }
                select.getResultType().stringValue = returnClass.qualifiedName
            }
        } else if (returnType is PsiPrimitiveType) {
            select.getResultType().stringValue = returnType.boxedTypeName
        } else if (returnType is PsiArrayType) {
            val componentType = returnType.componentType
            if (componentType is PsiClassType) {
                select.getResultType().stringValue = componentType.resolve()?.qualifiedName
            } else if (componentType is PsiPrimitiveType) {
                select.getResultType().stringValue = componentType.boxedTypeName
            }
        }
    }
}
