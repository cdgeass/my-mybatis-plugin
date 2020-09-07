package io.github.cdgeass.dialog;

import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBColor;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.formatter.WithParamFormatter;
import org.apache.commons.lang3.StringUtils;
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
        var sqlEditorTextField = new EditorTextField(formattedSql);

        var editorColorsManager = EditorColorsManager.getInstance();
        var font = editorColorsManager.getSchemeForCurrentUITheme().getFont(EditorFontType.PLAIN);
        sqlEditorTextField.setFont(font);
        sqlEditorTextField.setPreferredSize(new Dimension(500, 450));
        sqlEditorTextField.setCaretPosition(0);

        var project = selectionModel.getEditor().getProject();
        if (project != null) {
            var textAttributes1 = new TextAttributes();
            textAttributes1.setForegroundColor(JBColor.LIGHT_GRAY);
            var textAttributes2 = new TextAttributes();

            var highlightManager = HighlightManager.getInstance(project);
            sqlEditorTextField.addSettingsProvider(editor -> {
                editor.setHorizontalScrollbarVisible(true);
                editor.setVerticalScrollbarVisible(true);
                editor.setOneLineMode(false);
                editor.setRendererMode(true);
                editor.setCaretEnabled(false);

                var text = editor.getDocument().getText();
                var emptyLineCount = StringUtils.countMatches(text, StringConstants.SEPARATOR_LINE);

                var startOffset = 0;
                var i = 0;
                for (i = 1; i <= emptyLineCount; i++) {
                    var indexOf = StringUtils.ordinalIndexOf(text, StringConstants.SEPARATOR_LINE, i);
                    highlightManager.addRangeHighlight(editor, indexOf, indexOf + StringConstants.SEPARATOR_LINE.length() + 1,
                            textAttributes1, false, null);

                    var textAttributes2Copy = textAttributes2.clone();
                    textAttributes2Copy.setForegroundColor(i % 2 == 0 ? JBColor.PINK : JBColor.ORANGE);
                    highlightManager.addRangeHighlight(editor, startOffset, indexOf, textAttributes2Copy, false, null);
                    startOffset = indexOf + StringConstants.SEPARATOR_LINE.length() + 1;
                }
                var lastTextAttributes2Copy = textAttributes2.clone();
                lastTextAttributes2Copy.setForegroundColor((i) % 2 == 0 ? JBColor.PINK : JBColor.ORANGE);
                highlightManager.addRangeHighlight(editor, startOffset, text.length(), lastTextAttributes2Copy, false, null);
            });
        }
        return sqlEditorTextField;
    }


}
