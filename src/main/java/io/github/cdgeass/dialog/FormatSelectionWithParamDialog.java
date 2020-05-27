package io.github.cdgeass.dialog;

import javax.swing.*;
import java.awt.*;

/**
 * @author cdgeass
 * @since 2020-04-15
 */
public class FormatSelectionWithParamDialog {

    private final String formattedSql;

    private JPanel centerPane;
    private JScrollPane formattedSqlPane;
    private JTextArea formattedSqlTextArea;

    private Font font;

    public FormatSelectionWithParamDialog(String formattedSql) {
        this.formattedSql = formattedSql;
//        this.font = new Font("JetBrains Mono", Font.PLAIN, 14);

        init();
    }

    public void init() {
        formattedSqlTextArea.setText(formattedSql);
//        formattedSqlTextArea.setFont(font);
    }

    public JPanel getContentPanel() {
        return centerPane;
    }
}
