package io.github.cdgeass.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import io.github.cdgeass.formatter.FormatDialog;
import io.github.cdgeass.formatter.FormatterKt;
import org.jetbrains.annotations.NotNull;

/**
 * @author cdgeass
 * @since 2020-04-15
 */
public class FormatSelectionAction extends AnAction {

    public FormatSelectionAction() {
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        var editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        var selectionModel = editor.getSelectionModel();
        new FormatDialog(e.getProject(), selectionModel.getSelectedText()).show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        var editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        String selectedText = editor.getSelectionModel().getSelectedText();
        if (!FormatterKt.canFormat(selectedText)) {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
