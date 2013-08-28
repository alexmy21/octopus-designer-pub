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
package org.lisapark.octopus.swing;

import org.apache.commons.lang.reflect.FieldUtils;
import org.lisapark.octopus.ProgrammerException;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.text.Format;
import java.text.ParseException;

/**
 * This is a base class for all JFormattedTextFields. It is needed to ensure that the
 * <code>InputVerifier</code>'s logic is called before the editor will actually try and
 * commit the value.
 */
public class BaseFormattedTextField extends JFormattedTextField {

    private static final Action commitOnEnterAction = new CommitOnEnterAction();

    /**
     * Creates a <code>JFormattedTextField</code> with no
     * <code>AbstractFormatterFactory</code>. Use <code>setMask</code> or
     * <code>setFormatterFactory</code> to configure the
     * <code>JFormattedTextField</code> to edit a particular type of
     * value.
     */
    public BaseFormattedTextField() {
    }

    /**
     * Creates a JFormattedTextField with the specified value. This will
     * create an <code>AbstractFormatterFactory</code> based on the
     * type of <code>value</code>.
     *
     * @param value Initial value for the JFormattedTextField
     */
    public BaseFormattedTextField(Object value) {
        super(value);
    }

    /**
     * Creates a <code>JFormattedTextField</code>. <code>format</code> is
     * wrapped in an appropriate <code>AbstractFormatter</code> which is
     * then wrapped in an <code>AbstractFormatterFactory</code>.
     *
     * @param format Format used to look up an AbstractFormatter
     */
    public BaseFormattedTextField(Format format) {
        super(format);
    }

    /**
     * Creates a <code>JFormattedTextField</code> with the specified
     * <code>AbstractFormatter</code>. The <code>AbstractFormatter</code>
     * is placed in an <code>AbstractFormatterFactory</code>.
     *
     * @param formatter AbstractFormatter to use for formatting.
     */
    public BaseFormattedTextField(AbstractFormatter formatter) {
        super(formatter);
    }

    /**
     * Creates a <code>JFormattedTextField</code> with the specified
     * <code>AbstractFormatterFactory</code>.
     *
     * @param factory AbstractFormatterFactory used for formatting.
     */
    public BaseFormattedTextField(AbstractFormatterFactory factory) {
        super(factory);
    }

    /**
     * Creates a <code>JFormattedTextField</code> with the specified
     * <code>AbstractFormatterFactory</code> and initial value.
     *
     * @param factory      <code>AbstractFormatterFactory</code> used for
     *                     formatting.
     * @param currentValue Initial value to use
     */
    public BaseFormattedTextField(AbstractFormatterFactory factory, Object currentValue) {
        super(factory, currentValue);
    }

    /**
     * Invoked to process the key bindings for <code>ks</code> as the result
     * of the <code>KeyEvent</code> <code>e</code>. We override this method to make
     * sure that the text field has the proper action for when the user presses
     * the enter key
     *
     * @param ks        the <code>KeyStroke</code> queried
     * @param e         the <code>KeyEvent</code>
     * @param condition one of the following values:
     *                  <ul>
     *                  <li>JComponent.WHEN_FOCUSED
     *                  <li>JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
     *                  <li>JComponent.WHEN_IN_FOCUSED_WINDOW
     *                  </ul>
     * @param pressed   true if the key is pressed
     * @return true if there was a binding to an action, and the action
     *         was enabled
     */
    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
        // we need to override this method to allow for handling of the delete key properly
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            InputMap map = getInputMap(condition);
            ActionMap am = getActionMap();

            if (map != null && am != null && isEnabled()) {
                Object binding = map.get(ks);
                Action action;

                // replace the nofify action if necessary
                if (binding != null && binding.equals(JTextField.notifyAction)) {
                    action = am.get(binding);

                    if (action != commitOnEnterAction) {
                        am.put(JTextField.notifyAction, commitOnEnterAction);
                    }
                }
            }
        }

        return super.processKeyBinding(ks, e, condition, pressed);
    }

    /**
     * Returns the real value, not string representation, of the text in the
     * text field. If subclasses need to implement different logic the the return
     * value of the object, this method should be subclassed
     *
     * @return object value
     * @throws ParseException if the <code>AbstractFormatter</code> is not able
     *                        to format the current value
     */
    public Object getValueToCommit() throws ParseException {
        AbstractFormatter format = getFormatter();
        Object value;

        if (format != null) {
            value = format.stringToValue(getText());
        } else {
            value = getText();
        }

        return value;
    }

    /**
     * This method is called when the user presses the enter key to commit a value
     * to the text field
     *
     * @throws ParseException if the <code>AbstractFormatter</code> is not able
     *                        to format the current value
     */
    protected final void commitOnEnter() throws ParseException {
        boolean canCommit = canCommitEdit();

        if (canCommit) {
            setValue(getValueToCommit());
        }
    }

    /**
     * Returns true if we should try and commit the text for the formatted text field.
     * This method will see if there is an <code>InputVerifier</code> to ensure that
     * the input is verified before trying to commit. This is needed because what is allowed
     * by the formatted field may not be always allowed by the input verifier
     *
     * @return true if we can try and commit
     */
    protected boolean canCommitEdit() {
        boolean canCommit;

        InputVerifier inputVerifier = getInputVerifier();

        // if there is no verifier, or the verify says it is valid
        canCommit = (inputVerifier == null) || (inputVerifier.verify(this));

        return canCommit;
    }

    /**
     * Forces the current value to be taken from the * <code>AbstractFormatter</code> and
     * set as the current value. Note that internally this method will call <method>getValueToCommit</method>
     * to the value to commit
     *
     * @throws ParseException if the <code>AbstractFormatter</code> is not able
     *                        to format the current value
     */
    public final void commitEdit() throws ParseException {
        AbstractFormatter format = getFormatter();

        if (format != null) {
            setValue(getValueToCommit());
        } else {

            setValue(getValueToCommit());
        }
    }

    /**
     * Returns the non-committed but formatted version of the value in the field.
     * Note that if there is a problem converting the value using the formatter,
     * null will be returned;
     *
     * @return formatted value
     */
    public Object getNonCommittedValue() {
        AbstractFormatter format = getFormatter();
        Object nonCommittedValue = null;

        if (format != null) {
            try {
                nonCommittedValue = format.stringToValue(getText());
            } catch (ParseException e) {
                // just return null if it is invalid
            }
        }

        return nonCommittedValue;
    }

    /**
     * This class is very much like the CommitAction from JFormattedTextField, but instead
     * of calling commitEdit, it calls the commitOnEnter method. It does this to run the
     * InputVerifier logic when the user presses the enter key. We don't want to do this
     * logic in the normal commitEdit method because JFormattedTextField's focus lost
     * behavior will commit an edit when the component loses focus, but which happens after
     * the InputVerifier.
     * <p/>
     * Note that this code is almost an exact duplicate of code from JFormattedTextField and
     * JTextField
     */
    static class CommitOnEnterAction extends TextAction {

        private Field editedField;

        CommitOnEnterAction() {
            super(JTextField.notifyAction);
        }

        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getFocusedComponent();

            if (target instanceof BaseFormattedTextField) {
                // Attempt to commit the value
                try {
                    ((BaseFormattedTextField) target).commitOnEnter();

                    /**
                     * This code is taken from BasicRootPaneUI in order for the default button logic to work
                     * as expected. Normally, when enter is pressed the default button's action performed
                     * is called, but for formatted text field they consume this event. We want to emulate
                     * the default behavior
                     */
                    JRootPane root = (JRootPane) SwingUtilities.getAncestorOfClass(JRootPane.class, target);

                    JButton owner = root.getDefaultButton();
                    if (owner != null) {
                        owner.doClick(20);
                    }
                } catch (ParseException pe) {
                    UIManager.getLookAndFeel().provideErrorFeedback(target);
                    // value not commited, don't notify ActionListeners
                    return;
                }
            }
            // Super behavior.
            superActionPerformed();
        }

        private void superActionPerformed() {
            JTextComponent target = getFocusedComponent();

            if (target instanceof JTextField) {
                JTextField field = (JTextField) target;
                field.postActionEvent();
            }
        }

        public boolean isEnabled() {
            JTextComponent target = getFocusedComponent();
            if (target instanceof JFormattedTextField) {
                JFormattedTextField ftf = (JFormattedTextField) target;

                if (editedField == null) {
                    editedField = FieldUtils.getField(JFormattedTextField.class, "edited", true);
                }

                boolean editedValue;

                try {
                    editedValue = (Boolean) editedField.get(ftf);
                } catch (IllegalAccessException e) {
                    // this should never happen
                    throw new ProgrammerException(e);
                }

                return editedValue;
            }
            return superIsEnabled();
        }

        private boolean superIsEnabled() {
            JTextComponent target = getFocusedComponent();

            return target instanceof JTextField && hasActionListener(target);
        }

        /**
         * Returns true if the receiver has an <code>ActionListener</code>
         * installed.
         *
         * @param textField text field to check
         * @return true if the specified component has ActionListeners
         */
        private static boolean hasActionListener(JTextComponent textField) {
            // Guaranteed to return a non-null array
            ActionListener[] listeners = textField.getListeners(ActionListener.class);

            return listeners != null && listeners.length > 0;
        }
    }
}
