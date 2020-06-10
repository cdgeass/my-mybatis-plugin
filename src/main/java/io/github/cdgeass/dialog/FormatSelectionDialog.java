package io.github.cdgeass.dialog;

import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
import io.github.cdgeass.formatter.WithParamFormatter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author cdgeass
 * @since 2020-04-15
 */
public class FormatSelectionDialog extends DialogWrapper {

    private final SelectionModel selectionModel;

    public FormatSelectionDialog(SelectionModel selectionModel) {
        super(true);
        this.selectionModel = selectionModel;

        init();
        setTitle("Format Sql");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        var selectedText = selectionModel.getSelectedText();
        if (selectedText == null) {
            return null;
        }

        if (!WithParamFormatter.canFormatter(selectedText)) {
            return null;
        }

        var formattedSql = WithParamFormatter.format(selectedText);
        var sqlEditorTextField = new EditorTextField(formattedSql) {
            @Override
            protected EditorEx createEditor() {
                var editor = super.createEditor();
                editor.setHorizontalScrollbarVisible(true);
                editor.setVerticalScrollbarVisible(true);
                editor.setOneLineMode(false);

                return editor;
            }
        };

        var editorColorsManager = EditorColorsManager.getInstance();
        var font = editorColorsManager.getGlobalScheme().getFont(EditorFontType.PLAIN);
        sqlEditorTextField.setFont(font);
        sqlEditorTextField.setPreferredSize(new Dimension(500, 450));
        sqlEditorTextField.setCaretPosition(0);
        return sqlEditorTextField;
    }


}
