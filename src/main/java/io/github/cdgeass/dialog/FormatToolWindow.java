package io.github.cdgeass.dialog;

import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBColor;
import io.github.cdgeass.constants.StringConstants;
import io.github.cdgeass.formatter.WithParamFormatter;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.function.Consumer;

/**
 * @author cdgeass
 * @since 2020-06-17
 */
public class FormatToolWindow {

    private final Project project;

    private JPanel centerPanel;
    private JButton formatButton;
    private JButton copyButton;
    private EditorTextField unformattedTextField;
    private EditorTextField formattedTextField;
    private JButton cleanButton;

    public FormatToolWindow(Project project) {
        this.project = project;

        Consumer<Editor> highlightConsumer = editor -> {
            var textAttributes1 = new TextAttributes();
            textAttributes1.setForegroundColor(JBColor.LIGHT_GRAY);
            var textAttributes2 = new TextAttributes();
            var highlightManager = HighlightManager.getInstance(project);

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
        };

        formatButton.addActionListener(e -> {
            var text = unformattedTextField.getText();
            if (WithParamFormatter.canFormatter(text)) {
                var sql = WithParamFormatter.format(text);
                formattedTextField.setText(sql);
                formattedTextField.setCaretPosition(0);
                highlightConsumer.accept(formattedTextField.getEditor());
            }
        });

        copyButton.addActionListener(e -> {
            var systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            systemClipboard.setContents(new StringSelection(formattedTextField.getText()), null);
        });

        cleanButton.addActionListener(e -> {
            unformattedTextField.setText("");
            formattedTextField.setText("");
        });
    }

    public JComponent getContent() {
        return centerPanel;
    }

    private void createUIComponents() {
        var editorColorsManager = EditorColorsManager.getInstance();
        var font = editorColorsManager.getSchemeForCurrentUITheme().getFont(EditorFontType.PLAIN);

        unformattedTextField = new EditorTextField();

        unformattedTextField.setPreferredSize(new Dimension(500, 300));
        unformattedTextField.setOneLineMode(false);
        unformattedTextField.setFont(font);

        unformattedTextField.addSettingsProvider(editor -> {
            editor.setHorizontalScrollbarVisible(true);
            editor.setVerticalScrollbarVisible(true);
        });

        formattedTextField = new EditorTextField();

        formattedTextField.setPreferredSize(new Dimension(500, 300));
        formattedTextField.setOneLineMode(false);
        formattedTextField.setFont(font);

        formattedTextField.addSettingsProvider(editor -> {
            editor.setHorizontalScrollbarVisible(true);
            editor.setVerticalScrollbarVisible(true);
            editor.setCaretEnabled(false);
            editor.setRendererMode(true);
            editor.getCaretModel().moveToOffset(0);

            var textAttributes1 = new TextAttributes();
            textAttributes1.setForegroundColor(JBColor.LIGHT_GRAY);
            var textAttributes2 = new TextAttributes();
            var highlightManager = HighlightManager.getInstance(project);

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
}
