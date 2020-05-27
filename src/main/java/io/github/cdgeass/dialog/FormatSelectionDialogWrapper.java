package io.github.cdgeass.dialog;

import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.DialogWrapper;
import io.github.cdgeass.formatter.WithParamFormatter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author cdgeass
 * @since  2020-04-15
 */
public class FormatSelectionDialogWrapper extends DialogWrapper {

    private final SelectionModel selectionModel;

    public FormatSelectionDialogWrapper(SelectionModel selectionModel) {
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

        if (WithParamFormatter.canFormatter(selectedText)) {
            return new FormatSelectionWithParamDialog(WithParamFormatter.format(selectedText), selectionModel.getEditor().getProject()).getContentPanel();
        }

        return null;
    }
}
