package io.github.cdgeass.component

import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel

/**
 * @author cdgeass
 * @since 2020-09-30
 */
class PropertiesTableModel : ListTableModel<Pair<String, String>>()

class FirstColumnInfo(name: String?) : ColumnInfo<Pair<String, String>, String>(name) {

    override fun valueOf(item: Pair<String, String>?): String? {
        return item?.first
    }

}

class SecondColumnInfo(name: String?) : ColumnInfo<Pair<String, String>, String>(name) {

    override fun valueOf(item: Pair<String, String>?): String? {
        return item?.second
    }

}