package io.github.cdgeass.component

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.TitledSeparator
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.TableView
import com.intellij.util.ui.FormBuilder
import org.apache.commons.lang3.tuple.MutablePair
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * @author cdgeass
 * @since 2020-11-06
 */
class JavaClientGeneratorPanel : JPanel(BorderLayout()) {

    private val typeComboBox = ComboBox(arrayOf("ANNOTATEDMAPPER", "MIXEDMAPPER",
            "XMLMAPPER"))

    private val propertiesTableModel = PropertiesTableModel(mutableListOf("EnableSubPackages", "RootInterface",
            "UseLegacyBuilder"))

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
                    .addLabeledComponent("Type", typeComboBox)
                    .addComponent(TitledSeparator("Properties"))
                    .addComponent(propertiesToolbarDecorator.createPanel())
                    .addComponentFillVertically(JPanel(), 0)
                    .panel
        )
    }

    fun getType(): String {
        return typeComboBox.item
    }

    fun getProperties(): Map<String, String> {
        return propertiesTableModel.items.associateBy({ it.left }, { it.right })
    }

    fun setType(type: String): JavaClientGeneratorPanel {
        typeComboBox.item = type
        return this
    }

    fun setProperties(properties: Map<String, String>): JavaClientGeneratorPanel {
        if (properties.isNotEmpty()) {
            propertiesTableModel.addRows(properties.map { (property, value) -> MutablePair.of(property, value) })
        }
        return this
    }
}