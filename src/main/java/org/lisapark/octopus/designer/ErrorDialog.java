/* 
 * Copyright (c) 2013 Lisa Park, Inc. (www.lisa-park.net).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Lisa Park, Inc. (www.lisa-park.net) - initial API and implementation and/or initial documentation
 */
package org.lisapark.octopus.designer;

import com.google.common.collect.Lists;
import org.lisapark.octopus.swing.Borders;
import org.lisapark.octopus.swing.ComponentFactory;
import org.lisapark.octopus.swing.LayoutConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class can be used for displaying <code>Throwable</code> information and a user friendly message, and titled
 * dialog to the user. It also allows the user to view a stack trace of the specified exception.
 */
public class ErrorDialog extends JDialog {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(ErrorDialog.class);

    /**
     * New line
     */
    private static final String NEWLINE = System.getProperty("line.separator");


    private static final String DEFAULT_TITLE = "Octopus";

    /**
     * Message to display to user
     */
    private String message = "";

    /**
     * The detail message
     */
    private String detailMessage;

    /**
     * Exception to display information about
     */
    private Throwable exception;

    /**
     * Title of dialog
     */
    private String dialogTitle;

    /**
     * Detail showing flag
     */
    private boolean detailShowing = false;

    /**
     * Thread blocking flag used if this dialog is called from a thread that is not the
     * <p/>
     * Event Displatch Thread
     */
    private boolean shouldBlock = false;

    /**
     * Panel that displays the stack trace
     */
    private JPanel stackPanel;

    /**
     * Creates a model dialog with the specified <code>Dialog</code> as the owner, centered
     * <p/>
     * over the specified parent <code>Dialog</code>.
     *
     * @param title     - title to display on the dialog
     * @param dialog    - owner of dialog
     * @param exception - exception dialog is showing information on
     * @param message   - user friendly message
     */
    private ErrorDialog(String title, Dialog dialog, Throwable exception,
                        String message) {
        super(dialog, true);

        this.message = message;
        this.exception = exception;
        this.dialogTitle = title;

        init();

        if (dialog != null) {
            setLocationRelativeTo(dialog);
        } else {
            // center window
            centerWindow();
        }
    }

    /**
     * Creates a model dialog with the specified <code>Dialog</code> as the owner, centered
     * <p/>
     * over the specified parent <code>Dialog</code>.
     *
     * @param title         - title to display on the dialog
     * @param dialog        - owner of dialog
     * @param detailMessage - message to show in the detail section
     * @param message       - user friendly message
     */
    private ErrorDialog(String title, Dialog dialog, String detailMessage,
                        String message) {
        super(dialog, true);

        this.message = message;
        this.detailMessage = detailMessage;
        this.dialogTitle = title;

        init();

        if (dialog != null) {
            setLocationRelativeTo(dialog);
        } else {
            // center window
            centerWindow();
        }
    }

    /**
     * Creates a model dialog with the specified <code>Frame</code> as the owner, centered
     * <p/>
     * over the specified parent <code>Frame</code>.
     *
     * @param title     - title to display on the dialog
     * @param frame     - owner of dialog
     * @param exception - exception dialog is showing information on
     * @param message   - user friendly message
     */
    private ErrorDialog(String title, Frame frame, Throwable exception,
                        String message) {
        super(frame, true);

        this.message = message;
        this.exception = exception;
        this.dialogTitle = title;

        init();

        if (frame != null) {
            setLocationRelativeTo(frame);
        } else {
            // center window
            centerWindow();
        }
    }

    /**
     * Creates a model dialog with the specified <code>Frame</code> as the owner, centered
     * <p/>
     * over the specified parent <code>Frame</code>.
     *
     * @param title         - title to display on the dialog
     * @param frame         - owner of dialog
     * @param detailMessage - message to show in the detail section
     * @param message       - user friendly message
     */
    private ErrorDialog(String title, Frame frame, String detailMessage,
                        String message) {
        super(frame, true);

        this.message = message;
        this.detailMessage = detailMessage;
        this.dialogTitle = title;

        init();

        if (frame != null) {
            setLocationRelativeTo(frame);
        } else {
            // center window
            centerWindow();
        }
    }

    /**
     * Creates a model dialog with the specified <code>Dialog</code> as the owner, centered
     * <p/>
     * over the specified parent <code>Dialog</code>. Note that the title of the dialog
     * <p/>
     * is defaulted to Application Error
     *
     * @param dialog    - owner of dialog
     * @param exception - exception dialog is showing information on
     * @param message   - user friendly message
     */
    private ErrorDialog(Dialog dialog, Throwable exception, String message) {
        this(DEFAULT_TITLE, dialog, exception, message);
    }

    /**
     * Creates a model dialog with the specified <code>Frame</code> as the owner, centered
     * <p/>
     * over the specified parent <code>Frame</code>. Note that the title of the dialog
     * <p/>
     * is defaulted to Application Error
     *
     * @param frame     - owner of dialog
     * @param exception - exception dialog is showing information on
     * @param message   - user friendly message
     */
    private ErrorDialog(Frame frame, Throwable exception, String message) {
        this(DEFAULT_TITLE, frame, exception, message);
    }

    /**
     * Creates a modal dialog with the title set to Application Error and without a specified
     * <p/>
     * Frame owner.
     *
     * @param detailMessage - message that will be shown in the detail section
     * @param message       - user friendly message
     */
    private ErrorDialog(String detailMessage, String message) {
        super();

        this.message = message;
        this.detailMessage = detailMessage;
        this.dialogTitle = DEFAULT_TITLE;

        init();

        centerWindow();
    }

    /**
     * Creates a modal dialog with the specified <code>title</code> and without a specified
     * <p/>
     * Frame owner.
     *
     * @param exception - exception dialog is showing information on
     * @param message   - user friendly message
     * @param title     - title of the dialog
     */
    private ErrorDialog(String title, Throwable exception, String message) {
        super();

        this.message = message;
        this.exception = exception;
        this.dialogTitle = title;

        init();

        centerWindow();
    }

    /**
     * Creates a modal dialog with the title set to Application Error and without a specified
     * <p/>
     * Frame owner.
     *
     * @param exception - exception dialog is showing information on
     * @param message   - user friendly message
     */
    private ErrorDialog(Throwable exception, String message) {
        super();

        this.message = message;
        this.exception = exception;
        this.dialogTitle = DEFAULT_TITLE;

        init();

        centerWindow();
    }

    /**
     * Initialize the class.
     */
    private void init() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setTitle(dialogTitle);
        setResizable(false);

        // set content pane
        JPanel contentPane = ComponentFactory.createPanelWithLayout(new BorderLayout());
        contentPane.setBorder(Borders.PADDING_BORDER);
        contentPane.add(getCenterPanel(), BorderLayout.PAGE_START);

        setContentPane(contentPane);

        pack();

        // after the pack as to not affect layout
        getContentPane().add(getStackPanel());
    }

    /**
     * Creates and returns the center panel which contains all the content of dialog
     * <p/>
     * except the stack panel
     *
     * @return constructed panel
     */
    private JPanel getCenterPanel() {
        JPanel centerPanel = ComponentFactory.createPanelWithLayout(new BorderLayout());

        centerPanel.setBorder(UIManager.getBorder("OptionPane.messageAreaBorder"));

        // Inside panels
        JPanel body = ComponentFactory.createPanelWithLayout(new BorderLayout());
        JPanel realBody = ComponentFactory.createPanelWithLayout(new BorderLayout());

        Container sep = new JPanel() {
            public Dimension getPreferredSize() {
                // spacing is taken from JOptionPane
                return new Dimension(15, 3);
            }
        };

        Container sep1 = new JPanel() {
            public Dimension getPreferredSize() {
                return new Dimension(15, 3);
            }
        };

        realBody.add(body);
        addMessageComponents(body, getMessage());
        body.add(sep, BorderLayout.PAGE_END);
        body.add(sep1, BorderLayout.LINE_START);

        centerPanel.add(realBody, BorderLayout.CENTER);
        centerPanel.add(getButtonPanel(), BorderLayout.PAGE_END);

        addIcon(centerPanel);

        centerPanel.setPreferredSize(new Dimension(440, centerPanel.getPreferredSize().height));

        return centerPanel;
    }

    /**
     * Creates the panel that shows the stack trace
     *
     * @return constructed panel
     */
    private JPanel getStackPanel() {
        stackPanel = ComponentFactory.createPanelWithLayout(new BorderLayout());

        stackPanel.setVisible(false);

        stackPanel.setBorder(BorderFactory.createEmptyBorder(
                LayoutConstants.COMPONENT_GROUP_SPACE, 0, 0, 0));

        JTextArea textArea = new JTextArea(getDetailMessage());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        stackPanel.add(new JScrollPane(textArea));

        Dimension d = stackPanel.getPreferredSize();
        d.height = 100;
        stackPanel.setPreferredSize(d);

        return stackPanel;
    }

    /**
     * Returns the message that will be shown in the detail section of the dialog. If the excetpion
     * <p/>
     * is set, then the detail message will be the stack trace, otherwise it will use the detailMessage
     *
     * @return detail message
     */
    private String getDetailMessage() {
        if (detailMessage == null) {
            StringBuilder stack = new StringBuilder();

            if (exception != null) {
                try {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    PrintWriter pw = new PrintWriter(bout);

                    // get the exception's message as the first part of the message
                    stack.append(exception.toString());
                    // print the stack trace into the string
                    exception.printStackTrace(pw);
                    pw.flush();
                    stack.append(NEWLINE).append(bout.toString());

                    pw.close();
                } catch (Exception e) {
                    // show error
                    stack.append(e.toString());
                }
            }

            detailMessage = stack.toString();
        }

        return detailMessage;
    }

    /**
     * Adds the information icon to the panel
     *
     * @param top - container to add icon to
     */
    protected void addIcon(Container top) {
        /* Create the icon. */
        Icon icon = UIManager.getIcon("OptionPane.errorIcon");

        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setVerticalAlignment(SwingConstants.TOP);
            top.add(iconLabel, BorderLayout.BEFORE_LINE_BEGINS);
        }
    }

    /**
     * Adds the message to the dialog
     *
     * @param container - container to place message into
     * @param msg       - message that will be shown to use
     */
    protected void addMessageComponents(Container container, String msg) {
        // the box will contain a label for each line
        Box labelBox = new Box(BoxLayout.PAGE_AXIS);
        labelBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("", JLabel.LEADING);
        label.setForeground(UIManager.getColor("OptionPane.messageForeground"));

        // get the list of lines
        ArrayList<String> list = separateMessage(label, msg);

        String line;

        for (String aList : list) {
            line = aList;

            labelBox.add(new JLabel(line, JLabel.LEADING));
        }

        container.add(labelBox);
    }

    /**
     * Given the specified label and message, break the message apart such that it will
     * <p/>
     * fit in the label
     *
     * @param label   - label to get font properties off of
     * @param message - message to break up
     * @return list of strings that with each element representing line
     */
    private ArrayList<String> separateMessage(JLabel label, String message) {
        FontMetrics metrics = label.getFontMetrics(label.getFont());

        StringTokenizer paraSt;
        StringTokenizer lineSt;

        String paragraph;
        String line;
        String word;

        ArrayList<String> list = Lists.newArrayList();

        int labelWidth;
        int messageWidth;

        // width of the label
        labelWidth = 385;

        messageWidth = metrics.stringWidth(message);

        if (labelWidth > messageWidth) {
            list.add(message);
        } else {
            // first break up message by newlines
            paraSt = new StringTokenizer(message, NEWLINE, true);

            while (paraSt.hasMoreTokens()) {
                paragraph = paraSt.nextToken();

                // now break up by word
                lineSt = new StringTokenizer(paragraph, " ");

                line = "";

                while (lineSt.hasMoreTokens()) {
                    word = lineSt.nextToken();

                    // see if the line + word < the label width
                    if ((metrics.stringWidth(line) + metrics.stringWidth(word)) < labelWidth) {
                        line += (" " + word);
                    } else {
                        // it is too long, add the buffer to the list,

                        // and create a new line
                        list.add(line.trim());
                        line = "";
                        line += word;
                    }
                } // end of inner while

                if (paraSt.hasMoreTokens()) {
                    list.add(line);
                } else if (!line.trim().equals("")) {
                    list.add(line);
                }
            } // end of outer while
        } // end of else

        return list;
    }

    /**
     * Creates the button panel
     *
     * @return contructed button panel
     */
    private JPanel getButtonPanel() {
        JPanel buttonPanel = ComponentFactory.createPanel();

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(
                LayoutConstants.DIALOG_COMMAND_ROW_TOP_SPACE, 0, 0, 0));

        JButton detailBtn = ComponentFactory.createButtonWithText("Details");

        detailBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // show or hide the stack
                if (detailShowing) {
                    stackPanel.setVisible(false);

                    pack();
                } else {
                    stackPanel.setVisible(true);

                    pack();
                }

                detailShowing = !detailShowing;
            }
        });

        detailBtn.setMnemonic(KeyEvent.VK_D);

        AbstractAction closeAction = new AbstractAction("Close") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        };
        JButton closeBtn = ComponentFactory.createButtonWithAction(closeAction);

        // add a listener for when the user presses the escape to close the dialog
        closeBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Close");
        closeBtn.setMnemonic(KeyEvent.VK_C);
        closeBtn.getActionMap().put("Close", closeAction);

        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        closeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        // add stuff to box
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(detailBtn);
        buttonPanel.add(Box.createHorizontalStrut(LayoutConstants.COMMAND_BUTTON_SPACE));
        buttonPanel.add(closeBtn);

        return buttonPanel;
    }

    /**
     * Overrides the setVisible method to make sure this dialog is shown on the
     * <p/>
     * Event Dispatch Thread, or block the current <code>Thread</code> if the
     * <p/>
     * <field>shouldBlock</field> is true
     *
     * @param visible - true if the dialog should be visible
     */
    public void setVisible(final boolean visible) {
        if (SwingUtilities.isEventDispatchThread() || shouldBlock) {
            super.setVisible(visible);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ErrorDialog.super.setVisible(visible);
                }
            });
        }
    }

    /**
     * Returns the string message
     *
     * @return messge
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets wheter this dialog should block the current thread
     *
     * @param shouldBlock - true if this dialog should block
     */
    public void setShouldBlock(boolean shouldBlock) {
        this.shouldBlock = shouldBlock;
    }

    /**
     * Returns the exception
     *
     * @return exception
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Centers the dialog on the screen
     */
    private void centerWindow() {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

        Rectangle frameDim = getBounds();

        setLocation((screenDim.width - frameDim.width) / 2,
                (screenDim.height - frameDim.height) / 2);
    }

    /**
     * Shows a modal <code>ErrorDialog</code> with the specified title over the specified parent
     * with the specified message, and exception.
     * This method is thread safe, although most Swing methods are not.
     *
     * @param title         - title of dialog
     * @param parent        - parent to display dialog over
     * @param detailMessage - detail string which will be shown in text area
     * @param message       - message to display to user
     */
    public static void showErrorDialog(String title, Component parent,
                                       String detailMessage, String message) {
        showErrorDialog(title, parent, detailMessage, message, false);
    }

    /**
     * Shows a modal <code>ErrorDialog</code> with the specified title over the specified parent
     * with the specified message, and exception.
     * This method is thread safe, although most Swing methods are not.
     *
     * @param title         - title of dialog
     * @param parent        - parent to display dialog over
     * @param detailMessage - detail string which will be shown in text area
     * @param message       - message to display to user
     * @param shouldBlock   - block the current thread. Note this is redundant if called from the
     *                      <p/>
     *                      Event Dispatch thread
     */
    public static void showErrorDialog(String title, Component parent,
                                       String detailMessage, String message, boolean shouldBlock) {
        ErrorDialog dialog;

        Window window = getWindowForComponent(parent);

        if (window instanceof Dialog) {
            dialog = new ErrorDialog(title, (Dialog) window, detailMessage,
                    message);
        } else if (window instanceof Frame) {
            dialog = new ErrorDialog(title, (Frame) window, detailMessage,
                    message);
        } else {
            dialog = new ErrorDialog(detailMessage, message);
        }

        dialog.setShouldBlock(shouldBlock);

        dialog.setVisible(true);
    }

    /**
     * Shows a modal <code>ErrorDialog</code> with the specified title over the specified parent
     * with the specified message, and exception.
     * This method is thread safe, although most Swing methods are not.
     *
     * @param title     - title of dialog
     * @param parent    - parent to display dialog over
     * @param exception - exception to show stack of
     * @param message   - message to display to user
     */
    public static void showErrorDialog(String title, Component parent,
                                       Throwable exception, String message) {
        showErrorDialog(title, parent, exception, message, false);
    }

    /**
     * Shows a modal <code>ErrorDialog</code> with the specified title over the specified parent
     * with the specified message, exception and wheter to block the current <code>Thread</code>
     * This method is thread safe, although most Swing methods are not.
     *
     * @param title       - title of dialog
     * @param parent      - parent to display dialog over
     * @param exception   - exception to show stack of
     * @param message     - message to display to user
     * @param shouldBlock - block the current thread. Note this is redundant if called from the
     *                    <p/>
     *                    Event Dispatch thread
     */
    public static void showErrorDialog(String title, Component parent,
                                       Throwable exception, String message, boolean shouldBlock) {
        ErrorDialog dialog;

        // log exception
        if (exception != null) {
            LOG.error(exception.getMessage(), exception);
        }

        Window window = getWindowForComponent(parent);

        if (window instanceof Dialog) {
            dialog = new ErrorDialog(title, (Dialog) window, exception, message);
        } else if (window instanceof Frame) {
            dialog = new ErrorDialog(title, (Frame) window, exception, message);
        } else {
            dialog = new ErrorDialog(title, exception, message);
        }

        dialog.setShouldBlock(shouldBlock);

        dialog.setVisible(true);
    }

    /**
     * Shows a modal <code>ErrorDialog</code> with the Application Error title over the specified parent
     * with the specified message, and exception.
     * This method is thread safe, although most Swing methods are not.
     *
     * @param parent    - parent to display dialog over
     * @param exception - exception to show stack of
     * @param message   - message to display to user
     */
    public static void showErrorDialog(Component parent, Throwable exception,
                                       String message) {
        showErrorDialog(parent, exception, message, false);
    }

    /**
     * Shows a modal <code>ErrorDialog</code> with the Application Error title over the specified parent
     * with the specified message, and exception.
     * This method is thread safe, although most Swing methods are not.
     *
     * @param parent      - parent to display dialog over
     * @param exception   - exception to show stack of
     * @param message     - message to display to user
     * @param shouldBlock - block the current thread. Note this is redundant if called from the
     */
    public static void showErrorDialog(Component parent, Throwable exception,
                                       String message, boolean shouldBlock) {
        ErrorDialog dialog;

        // log exception
        if (exception != null) {
            LOG.error(exception.getMessage(), exception);
        }

        Window window = getWindowForComponent(parent);

        if (window instanceof Dialog) {
            dialog = new ErrorDialog((Dialog) window, exception, message);
        } else if (window instanceof Frame) {
            dialog = new ErrorDialog((Frame) window, exception, message);
        } else {
            dialog = new ErrorDialog(exception, message);
        }

        dialog.setShouldBlock(shouldBlock);

        dialog.setVisible(true);
    }

    /**
     * Returns the <code>Window</code> for the specified component or null if one is not found
     *
     * @param component - get window for this component
     * @return window of component
     */
    private static Window getWindowForComponent(Component component) {
        Window window = null;

        if (component instanceof Dialog || component instanceof Frame) {
            window = (Window) component;
        } else if (component != null) {
            window = SwingUtilities.windowForComponent(component);
        }

        return window;
    }
}
