package nl.tudelft.watchdog.intellij.ui.util;

import com.intellij.ide.BrowserUtil;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.openapi.ui.ComboBox;

import nl.tudelft.watchdog.core.ui.wizards.WizardStrings;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 * Utility methods for the UI.
 */
public class UIUtils {

    /**
     * Creates and returns a bold text label.
     */
    public static void createBoldLabel(JComponent parent, String text) {
        JLabel label = createLabel(parent, text);
        Font font = label.getFont();
        label.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
    }

    /**
     * Creates and returns a label with the given text.
     */
    public static JLabel createLabel(JComponent parent, String text) {
        return createLabel(parent, text, JLabel.LEADING);
    }

    /**
     * Creates and returns a label with a given style and text.
     */
    private static JLabel createLabel(JComponent parent, String text, int style) {
        JLabel label = new JLabel(text, style);
        parent.add(label);
        return label;
    }

    /**
     * Creates and returns a bold label that can be used as a title. Also, an
     * extra empty label is added above and below the title to create vertical spacing.
     */
    public static void createTitleLabel(JComponent parent, String text) {
        createLabel(parent, "\n");
        JLabel label = createLabel(parent, text);
        Font font = label.getFont();
        label.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 10));
        createLabel(parent, "\n");
    }

    /**
     * Creates and returns a user text input field for text of the given length.
     */
    public static JTextField createLimitedTextInputField(JComponent parent, int limit) {
        JTextField textField = createTextInputField(parent, limit);
        textField.setDocument(new JTextFieldLimit(limit));
        return textField;
    }

    /**
     * Creates and returns a user text input field of minimum size.
     */
    public static JTextField createTextInputField(JComponent parent, int minSize) {
        JTextField textField = new JTextField(minSize);
        parent.add(textField);
        return textField;
    }

    /**
     * Creates and returns a check box with the given text.
     */
    public static JCheckBox createCheckBox(JComponent parent, String text) {
        JCheckBox checkBox = new JCheckBox(text);
        parent.add(checkBox);
        return checkBox;
    }

    /**
     * @return A {@link GridLayout}ed JPanel with the given number of
     * columns.
     */
    public static JPanel createGridedJPanel(JComponent parent, int columns) {
        JPanel panel = new JPanel(new GridLayout(0, columns));
        parent.add(panel);
        return panel;
    }

    /**
     * @return A {@link FlowLayout}ed JPanel.
     */
    private static JPanel createFlowJPanelLeft(JComponent parent, int alignment) {
        JPanel panel = new JPanel(new FlowLayout(alignment, 0, 3));
        parent.add(panel);
        return panel;
    }

    /**
     * @return A {@link FlowLayout}ed JPanel aligned to the left.
     */
    public static JPanel createFlowJPanelLeft(JComponent parent) {
        return createFlowJPanelLeft(parent, FlowLayout.LEFT);
    }

    /**
     * @return A vertical {@link BoxLayout}ed JPanel.
     */
    public static JPanel createVerticalBoxJPanel(JComponent parent) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        parent.add(panel);
        return panel;
    }


    /**
     * Creates a clickable label with URL link and the given description text.
     */
    public static void createHyperLinkLabel(Container parent, String description, final String url) {
        HyperlinkLabel label = new HyperlinkLabel(description);

        label.addHyperlinkListener(e -> BrowserUtil.open(url));
        parent.add(label);
    }

    /**
     * Creates a clickable label with URL link and the given description text.
     */
    public static void createHyperLinkLabel(Container parent, WizardStrings.Links link) {
        createHyperLinkLabel(parent, link.text  + ".", link.url);
    }

    /**
     * Creates a button with the given description text.
     */
    public static void createButton(JComponent parent, String description, MouseListener listener) {
        JButton button = new JButton(description);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(listener);
        parent.add(button);
    }


    /**
     * Creates a Combo Box of String items.
     */
    public static ComboBox<String> createComboBox(JComponent parent,
                                          ItemListener listener, String[] items, int defaultSelection) {
        ComboBox<String> comboBox = new ComboBox<>(items);
        comboBox.setSelectedIndex(defaultSelection);
        comboBox.addItemListener(listener);
        parent.add(comboBox);
        return comboBox;
    }

    /**
     * Creates a linked label that opens the project report in a browser.
     */
    public static void createOpenReportLink(JComponent parent) {
        String projectReport = "http://www.testroots.org/reports/project/"
                + WatchDogUtils.getProjectSetting().projectId + ".html";
        UIUtils.createHyperLinkLabel(parent, "Open Report.", projectReport);
    }

    /**
     * Creates a linked label that opens the debug survey in the browser with
     * the correct User ID.
     */
    public static void createStartDebugSurveyLink(JComponent parent) {
        UIUtils.createHyperLinkLabel(parent, "Share your thoughts on debugging and win an additional Amazon voucher!", WatchDogUtils.getDebugSurveyLink());
    }

    /**
     * Creates and returns a JPanel Group with an enclosed Grid layout with
     * the given number of columns.
     */
    public static JPanel createGroup(JComponent parent, String title, int columns) {
        JPanel group = UIUtils.createGridedJPanel(parent, columns);
        group.setBorder(BorderFactory.createTitledBorder(title));
        return group;
    }

    public static class JTextFieldLimit extends PlainDocument {
        private int limit;

        public JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        @Override
        public void insertString(int offset, String text, AttributeSet attributeSet) throws BadLocationException {
            if (text == null) {
                return;
            }

            if ((getLength() + text.length()) <= limit) {
                super.insertString(offset, text, attributeSet);
            }
        }
    }
}
