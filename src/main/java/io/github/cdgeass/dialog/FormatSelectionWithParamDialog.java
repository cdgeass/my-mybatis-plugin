package io.github.cdgeass.dialog;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaCodeFragmentFactory;
import com.intellij.psi.PsiDocumentManager;
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
        var expressionCodeFragment = JavaCodeFragmentFactory.getInstance(project)
                .createExpressionCodeFragment(formattedSql, null, null, true);
        var document = PsiDocumentManager.getInstance(project).getDocument(expressionCodeFragment);
        formattedSqlTextField = new EditorTextField(document, project, JavaFileType.INSTANCE) {
            @Override
            protected EditorEx createEditor() {
                var editor = super.createEditor();
                editor.setVerticalScrollbarVisible(true);
                editor.setHorizontalScrollbarVisible(true);
                return editor;
            }
        };
    }
}
