package io.github.cdgeass.generator.component

import com.intellij.openapi.ui.ComboBox
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import org.apache.commons.lang3.tuple.MutablePair
import javax.swing.DefaultCellEditor
import javax.swing.JCheckBox
import javax.swing.JTextField
import javax.swing.table.TableCellEditor

/**
 * @author cdgeass
 * @since 2020-09-30
 */
class PropertiesTableModel(
        private val properties: LinkedHashMap<String, out Any>,
        private val usedProperties: MutableSet<String> = mutableSetOf()
) : ListTableModel<MutablePair<String, String>>(
        PropertyColumnInfo(properties, usedProperties),
        ValueColumnInfo(properties)
) {

    override fun removeRow(idx: Int) {
        usedProperties.remove(super.getRowValue(idx).left)
        super.removeRow(idx)
    }

    override fun addRow() {
        addRow(MutablePair.of("", ""))
    }

    override fun addRow(item: MutablePair<String, String>) {
        if (item.left.isBlank()) {
            val tempProperties = properties.keys.filter { !usedProperties.contains(it) }
            if (tempProperties.isNotEmpty()) {
                item.left = tempProperties[0]
                usedProperties.add(item.left)
                super.addRow(item)
            }
        }
    }

    override fun addRows(items: Collection<MutablePair<String, String>>) {
        super.addRows(items)
        if (items.isNotEmpty()) {
            usedProperties.addAll(items.map { it.left })
        }
    }
}

class PropertyColumnInfo(
        private val properties: LinkedHashMap<String, out Any>,
        private val usedProperties: MutableSet<String>
) : ColumnInfo<MutablePair<String, String>, String>("property") {

    override fun valueOf(item: MutablePair<String, String>): String {
        return item.left
    }

    override fun isCellEditable(item: MutablePair<String, String>): Boolean {
        return true
    }

    override fun setValue(item: MutablePair<String, String>, value: String) {
        if (value.isNotBlank()) {
            usedProperties.remove(item.left)
            item.left = value
            usedProperties.add(value)
        }
    }

    override fun getEditor(item: MutablePair<String, String>): TableCellEditor {
        val tempProperties = properties.keys.filter { !usedProperties.contains(it) }
                .toMutableList().apply { add(0, item.left) }
                .toTypedArray()
        return DefaultCellEditor(ComboBox(tempProperties))
    }
}

class ValueColumnInfo(
        private val properties: LinkedHashMap<String, out Any>
) : ColumnInfo<MutablePair<String, String>, Any>("value") {

    override fun valueOf(item: MutablePair<String, String>): String {
        return item.right
    }

    override fun isCellEditable(item: MutablePair<String, String>): Boolean {
        return true
    }

    override fun setValue(item: MutablePair<String, String>, value: Any) {
        item.right = if (value is Boolean) {
            if (value) "true" else "false"
        } else {
            value as String
        }
    }

    override fun getEditor(item: MutablePair<String, String>): TableCellEditor {
        return when (val value = properties[item.left]) {
            Boolean::class.java -> {
                DefaultCellEditor(JCheckBox())
            }
            String::class.java -> {
                DefaultCellEditor(JTextField())
            }
            is List<*> -> {
                DefaultCellEditor(ComboBox(value.toTypedArray()))
            }
            else -> {
                DefaultCellEditor(JTextField())
            }
        }
    }
}