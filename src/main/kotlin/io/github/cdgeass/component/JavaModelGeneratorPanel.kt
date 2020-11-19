package io.github.cdgeass.component

import com.intellij.ui.TitledSeparator
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.TableView
import com.intellij.util.ui.FormBuilder
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.tuple.MutablePair
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * @author cdgeass
 * @since  2020-11-06
 */
class JavaModelGeneratorPanel : JPanel(BorderLayout()) {

    private val propertiesTableModel = PropertiesTableModel(mutableListOf("ConstructorBased",
            "EnableSubPackages", "ExampleTargetPackage", "ExampleTargetProject", "Immutable", "RootClass", "TrimStrings"))

    init {
        val propertiesTable = TableView(propertiesTableModel)
        val propertiesToolbarDecorator = ToolbarDecorator.createDecorator(propertiesTable)
                .setAddAction {
                    propertiesTableModel.addRow(MutablePair("", ""))
                }
                .setRemoveAction {
                    propertiesTableModel.removeRow(propertiesTable.selectedRow)
                }
        this.add(
                FormBuilder.createFormBuilder()
                        .addComponent(TitledSeparator("Properties"))
                        .addComponent(propertiesToolbarDecorator.createPanel())
                        .addComponentFillVertically(JPanel(), 0)
                        .panel
        )
    }

    fun getProperties(): List<MutablePair<String, String>> {
        return propertiesTableModel.items.filter { StringUtils.isNotBlank(it.left) && StringUtils.isNotBlank(it.right) }
    }
}