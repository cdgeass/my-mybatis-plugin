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

    override fun checkMethod(
        method: PsiMethod,
        manager: InspectionManager,
        isOnTheFly: Boolean
    ): Array<ProblemDescriptor>? {
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
        return "Add statement in ${myXmlFile.element?.name ?: ""}"
    }

    override fun getFamilyName(): String {
        return "Add statement"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val rootTag = myXmlFile.element?.rootTag ?: return

        val domManager = DomManager.getDomManager(project)
        val mapper = domManager.getDomElement(rootTag)
        if (mapper !is Mapper) {
            return
        }

        val statementName = myMethod.element?.name ?: ""
        when {
            statementName.startsWith("select") -> {
                val select = mapper.addSelect()
                addReturnType(select)
                select.getId().stringValue = statementName
            }
            statementName.startsWith("find") -> {
                val select = mapper.addSelect()
                addReturnType(select)
                select.getId().stringValue = statementName
            }
            statementName.startsWith("insert") -> {
                val insert = mapper.addInsert()
                insert.getId().stringValue = statementName
            }
            statementName.startsWith("delete") -> {
                val delete = mapper.addDelete()
                delete.getId().stringValue = statementName
            }
            statementName.startsWith("update") -> {
                val update = mapper.addUpdate()
                update.getId().stringValue = statementName
            }
            else -> {
                val select = mapper.addSelect()
                addReturnType(select)
                select.getId().stringValue = statementName
            }
        }
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
            val returnClass = returnType.resolve()
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
