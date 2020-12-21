package io.github.cdgeass.component

import javax.swing.JComponent

/**
 * @author cdgeass
 * @since 2020-09-25
 */
class MyBatisGeneratorSettingsComponent {

    private val contextPanel: ContextPanel = ContextPanel()

    fun getComponent(): JComponent {
        return contextPanel
    }

    fun getContext(): ContextPanel {
        return contextPanel
    }

    fun getJavaTypeResolver(): JavaTypeResolverPanel {
        return contextPanel.getJavaTypeResolver()
    }

    fun getJavaModelGenerator(): JavaModelGeneratorPanel {
        return contextPanel.getJavaModelGenerator()
    }

    fun getSqlMapGenerator(): SqlMapGeneratorPanel {
        return contextPanel.getSqlMapGenerator()
    }

    fun getJavaClientGenerator(): JavaClientGeneratorPanel {
        return contextPanel.getJavaClientGenerator()
    }

    fun getTable(): TablePanel {
        return contextPanel.getTable()
    }

}