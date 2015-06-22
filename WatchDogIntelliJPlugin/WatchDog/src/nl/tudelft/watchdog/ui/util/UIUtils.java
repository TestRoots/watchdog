package nl.tudelft.watchdog.ui.util;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.openapi.ui.ComboBox;

import nl.tudelft.watchdog.WatchDog;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.preferences.ProjectPreferenceSetting;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.MouseInputAdapter;
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
    public static JLabel createBoldLabel(JComponent parent, String text) {
        JLabel label = createLabel(parent, text);
        Font font = label.getFont();
        label.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
        return label;
    }

    /**
     * Creates and returns a bold text label with associated style.
     */
    public static JLabel createBoldLabel(JComponent parent, String text, int style) {
        JLabel label = createLabel(parent, text, style);
        Font font = label.getFont();
        label.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
        return label;
    }

    /**
     * Creates and returns an italic text label.
     */
    public static JLabel createItalicLabel(JComponent parent, String text) {
        JLabel label = createLabel(parent, text);
        Font font = label.getFont();
        label.setFont(new Font(font.getName(), Font.ITALIC, font.getSize()));
        return label;
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
    public static JLabel createLabel(JComponent parent, String text, int style) {
        JLabel label = new JLabel(text, style);
        parent.add(label);
        return label;
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
     * Creates uneditable text field.
     */
    public static JTextField createTextField(JComponent parent, String content) {
        JTextField textField = createTextInputField(parent, content.length());
        textField.setText(content);
        textField.setEditable(false);
        return textField;
    }

    /**
     * Creates and returns a radio button with the given text.
     */
    public static JRadioButton createRadioButton(JComponent parent, String text) {
        JRadioButton button = new JRadioButton();
        button.setText(text);
        parent.add(button);
        return button;
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
     * @return A {@link FlowLayout}ed JPanel aligned to the center.
     */
    public static JPanel createFlowJPanelCenter(JComponent parent) {
        return createFlowJPanelLeft(parent, FlowLayout.CENTER);
    }

    /**
     * @return A {@link FlowLayout}ed JPanel aligned to the left.
     */
    public static JPanel createFlowJPanelRight(JComponent parent) {
        return createFlowJPanelLeft(parent, FlowLayout.RIGHT);
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
     * Creates a pair of a linked Label and text input field of specified length.
     *
     * @param labelText The text of the label associated with the input.
     * @param toolTip   The tooltip displayed on both the label and the input.
     * @param parent    The component on which both should be put.
     * @return input The linked input.
     */
    public static JTextField createLinkedFieldInput(JComponent parent, String labelText, int minSize, String toolTip) {
        JLabel label = UIUtils.createLabel(parent, labelText);
        label.setToolTipText(toolTip);
        JTextField input = UIUtils.createTextInputField(parent, minSize);
        input.setToolTipText(toolTip);
        UIUtils.attachListenerOnLabelClickFocusTextElement(label, input);
        return input;
    }

    /**
     * Attaches a listener to the specified label that directs the focus to the
     * supplied text (resulting in an HTML form-like connection of the label and
     * its input field).
     */
    private static void attachListenerOnLabelClickFocusTextElement(JLabel label,
                                                                   final JTextField textField) {

        label.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                focusAccompanyingInput();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                focusAccompanyingInput();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // intentionally left empty
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                focusAccompanyingInput();
            }

            private void focusAccompanyingInput() {
                textField.grabFocus();
            }
        });
    }

    /**
     * Returns the Project's name.
     */
    public static String getProjectName() {
        return WatchDog.project.getName();
    }

    /**
     * Returns the {@link ProjectPreferenceSetting} of the currently active
     * project.
     */
    public static ProjectPreferenceSetting getProjectSetting() {
        return Preferences.getInstance().getOrCreateProjectSetting(UIUtils.getProjectName());
    }

    /**
     * The WatchDog Icon.
     */
    public static final Icon WATCHDOG_ICON = IconLoader.getIcon("/images/watchdog_icon.png");

    /**
     * The WatchDog Icon Disabled.
     */
    public static final Icon WATCHDOG_ICON_DISABLED = IconLoader.getIcon("/images/watchdog_icon_disabled.png");

    /**
     * The WatchDog Icon Warning.
     */
    public static final Icon WATCHDOG_ICON_WARNING = IconLoader.getIcon("/images/watchdog_icon_warning.png");

    /**
     * Creates and returns a label with the given text and color.
     */
    public static JLabel createLabel(JComponent parent, String text, Color color) {
        JLabel label = createLabel(parent, text);
        label.setForeground(color);
        return label;
    }

    /**
     * Creates a centered label containing the WatchDogLogo.
     */
    public static JLabel createWatchDogLogo(JComponent logoContainer) {
        return createLogo(logoContainer, "/images/watchdog_small.png");
    }

    /**
     * Creates a label containing the TU Delft Logo.
     */
    public static JLabel createTUDLogo(JComponent logoContainer) {
        return createLogo(logoContainer, "/images/tudelft_with_frame.png");
    }

    /**
     * Creates a logo from the given image url.
     */
    public static JLabel createLogo(JComponent parent, String imageLocation) {
        JLabel watchdogLogo = new JLabel();
        Icon image = IconLoader.getIcon(imageLocation);
        watchdogLogo.setIcon(image);
        watchdogLogo.setHorizontalAlignment(JLabel.CENTER);
        parent.add(watchdogLogo);
        return watchdogLogo;
    }

    /**
     * Creates a clickable label with URL link and the given description text.
     */
    public static HyperlinkLabel createHyperLinkLabel(JComponent parent, String description, final String url) {
        HyperlinkLabel label = new HyperlinkLabel(description);

        label.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                BrowserUtil.open(url);
            }
        });
        parent.add(label);
        return label;
    }

    /**
     * Creates a JEditorPane with clickable links and font of JLabel
     */
    public static JEditorPane createHtmlTextWithLinks(JComponent parent, String htmlText) {
        JEditorPane jEditorPane = new JEditorPane("text/html", htmlText);
        jEditorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        jEditorPane.setFont(new JLabel().getFont());
        jEditorPane.setEditable(false);
        jEditorPane.setOpaque(false);
        jEditorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType()))
                    BrowserUtil.open(e.getURL().toString());
            }
        });
        parent.add(jEditorPane);
        return jEditorPane;
    }

    /**
     * Creates a button with the given description text.
     */
    public static JButton createButton(JComponent parent, String description, MouseListener listener) {
        JButton button = new JButton(description);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(listener);
        parent.add(button);
        return button;
    }


    /**
     * Creates a Combo Box of String items.
     */
    public static ComboBox createComboBox(JComponent parent,
                                          ItemListener listener, String[] items, int defaultSelection) {
        ComboBox comboBox = new ComboBox(items);
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
                + UIUtils.getProjectSetting().projectId + ".html";
        UIUtils.createHyperLinkLabel(parent, "Open Report.", projectReport);
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
