package io.github.cdgeass.dialog;

import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;

import javax.swing.*;

/**
 * @author cdgeass
 * @since 2020-04-15
 */
public class FormatSelectionWithParamDialog {

    private final String formattedSql;
    private final Project project;

    private JPanel centerPane;
    private JScrollPane formattedSqlPane;
    private EditorTextField formattedSqlTextField;

    public FormatSelectionWithParamDialog(String formattedSql, Project project) {
        this.formattedSql = formattedSql;
        this.project = project;
    }

    public JPanel getContentPanel() {
        return centerPane;
    }

    private void createUIComponents() {
        formattedSqlTextField = new EditorTextField(formattedSql, project, PlainTextFileType.INSTANCE) {
            @Override
            protected EditorEx createEditor() {
                var editor = super.createEditor();
                editor.setVerticalScrollbarVisible(true);
                editor.setVerticalScrollbarOrientation(EditorEx.VERTICAL_SCROLLBAR_RIGHT);
                editor.setHorizontalScrollbarVisible(true);
                return editor;
            }
        };

        var highlightManager = HighlightManager.getInstance(project);
    }
}
