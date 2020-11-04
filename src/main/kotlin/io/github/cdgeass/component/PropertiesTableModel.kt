package io.github.cdgeass.component

import com.intellij.openapi.ui.ComboBox
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import org.apache.commons.lang3.tuple.MutablePair
import javax.swing.DefaultCellEditor
import javax.swing.table.TableCellEditor

/**
 * @author cdgeass
 * @since 2020-09-30
 */
class PropertiesTableModel(properties: Array<String>) : ListTableModel<MutablePair<String, String>>(PropertyColumnInfo(properties), ValueColumnInfo())

class PropertyColumnInfo(private val properties: Array<String>) : ColumnInfo<MutablePair<String, String>, String>("property") {

    override fun valueOf(item: MutablePair<String, String>): String {
        return item.left
    }

    override fun isCellEditable(item: MutablePair<String, String>): Boolean {
        return true
    }

    override fun setValue(item: MutablePair<String, String>, value: String) {
        if (properties.contains(value)) {
            item.left = value
        } else {
            item.left = properties[0]
        }
    }

    override fun getEditor(item: MutablePair<String, String>): TableCellEditor {
        return DefaultCellEditor(ComboBox(properties))
    }
}

class ValueColumnInfo : ColumnInfo<MutablePair<String, String>, String>("value") {

    override fun valueOf(item: MutablePair<String, String>): String {
        return item.right
    }

    override fun isCellEditable(item: MutablePair<String, String>): Boolean {
        return true
    }

    override fun setValue(item: MutablePair<String, String>, value: String) {
        item.right = value
    }
}