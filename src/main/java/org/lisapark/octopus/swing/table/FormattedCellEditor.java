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
package org.lisapark.octopus.swing.table;

import com.jidesoft.grid.ContextSensitiveCellEditor;
import com.jidesoft.validation.ValidationResult;
import org.lisapark.octopus.swing.BaseFormattedTextField;
import org.lisapark.octopus.swing.ComponentFactory;
import org.lisapark.octopus.swing.DefaultValidationFailedListener;
import org.lisapark.octopus.swing.ValidationFailedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.text.ParseException;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class FormattedCellEditor extends ContextSensitiveCellEditor implements FocusListener {
    private static final String CHECK_ACTION = "check";

    /**
     * This flag will be true when the <method>cancelCellEditing</method> is called
     */
    private boolean canceling;

    /**
     * value that is initally in the cell. This is needed if the user wants to revert back to
     * a valid value
     */
    protected Object originalValue;

    /**
     * Component that would get the next focus
     */
    private JComponent oppositeComponent;
    private BaseFormattedTextField field;

    private ValidationFailedListener validationFailedListener;

    /**
     * Default Constructor
     *
     * @param type the class of the value for this editor.
     * @see com.jidesoft.converter.ConverterContextSupport
     */
    public FormattedCellEditor(Class<?> type) {
        setType(type);
        field = ComponentFactory.createFormattedTextField();
        field.setBorder(ContextSensitiveCellEditor.DEFAULT_CELL_EDITOR_BORDER);

        field.setFocusLostBehavior(JFormattedTextField.REVERT);
        // React when the user presses Enter while the editor is active.
        // (Tab is handled as specified by JFormattedTextField's focusLostBehavior property.)
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), CHECK_ACTION);
        field.getActionMap().put(CHECK_ACTION, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // if there is an input verifier then we need to verify the field according to it before we call
                // stop cell editing
                if (field.getInputVerifier().verify(field)) {
                    FormattedCellEditor.super.stopCellEditing();
                }
            }
        });
        field.setInputVerifier(new EditorInputVerifier());
        field.addFocusListener(this);

        validationFailedListener = new DefaultValidationFailedListener(field);

        customizeFormattedField(field);
    }

    protected void customizeFormattedField(JFormattedTextField field) {

    }

    protected Object coerceValueToCommit(Object value) {
        return value;
    }

    protected ValidationResult validateValueWithinRange(Object value) {
        return ValidationResult.OK;
    }

    /**
     * Returns the value of the cell editor. This will be the last valid value
     * in the formatted text field
     *
     * @return cell value
     */
    @Override
    public Object getCellEditorValue() {
        return field.getValue();
    }

    /**
     * Tells the editor to cancel the editing session. We need to override this
     * so the InputVerifier doesn't come into play
     */
    public void cancelCellEditing() {
        canceling = true;
        super.cancelCellEditing();
        canceling = false;
    }

    /**
     * Returns true if the editor is ready to give up focus.
     *
     * @return true if focus can be removed from editor
     */
    protected boolean isEditorDoneEditing() {
        /**
         * Make there are no leading and trailing spaces
         */
        field.setText(field.getText().trim());
        boolean doneEditing = true;

        if (field.isEditValid()) {
            try {
                if (getValidationListeners().length > 0) {
                    ValidationResult validationResult = validateValueWithinRange(field.getValueToCommit());

                    if (validationResult.isValid()) {
                        validationResult = validate(originalValue, coerceValueToCommit(field.getValueToCommit()));

                        if (validationResult != null && !validationResult.isValid()) {
                            doneEditing = false;

                            validationFailedListener.validationFailed(validationResult);
                        }
                    } else {
                        doneEditing = false;

                        validationFailedListener.validationFailed(validationResult);
                    }
                }

                if (doneEditing) {
                    field.commitEdit();
                }
            } catch (ParseException ex) {
                // since it is valid, this will never happen
            }
        } else {
            ValidationResult validationResult = new ValidationResult(false);
            validationResult.setMessage(String.format("%s is not a valid %s", field.getText(), getType().getSimpleName()));

            validationFailedListener.validationFailed(validationResult);
            //don't let the editor go away
            doneEditing = false;
        }

        return doneEditing;
    }

    /**
     * Asks the editor to stop editing. If the currently entered value is valid
     * the editor will commit the value and return control to the table.
     * If the value is not valid, and there is an <code>InvalidValueHandler</code>,
     * control will be passed to it, otherwise, the editor will not give up focus.
     *
     * @return true if the editor stopped
     */
    public boolean stopCellEditing() {
        /**
         * Check that the component that will get the next focus wants
         * to verify or not.  If no then forget about verification and
         * stop cell editing, verify otherwise.  This is for user entering
         * invalid value and then hitting cancel/scroll button.
         */
        if (oppositeComponent != null) {
            if (oppositeComponent.getVerifyInputWhenFocusTarget() == false) {
                return true;
            }
        }

        boolean doneEditing = isEditorDoneEditing();

        if (doneEditing) {
            super.stopCellEditing();
        }

        return doneEditing;
    }

    public void focusGained(FocusEvent e) {
    }

    /**
     * Store the component that would get the next focus, this is done
     * so that we can tell the editor to stop editing and do not even
     * validate because the user canceled.
     *
     * @see #stopCellEditing
     */
    public void focusLost(FocusEvent e) {
        oppositeComponent = (e.getOppositeComponent() instanceof JComponent ?
                (JComponent) e.getOppositeComponent() : null);
    }

    /**
     * We override <method>getTableCellEditorComponent</method> to call setValue on the
     * formatted text field with the value form the table
     *
     * @param table      table we are providing the editor for
     * @param value      value we are editing
     * @param isSelected true if the cell is selected
     * @param row        row number of value
     * @param column     column number of value
     * @return component to use as the editor
     */
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value, boolean isSelected,
                                                 int row, int column) {
        // remember the last valid value
        originalValue = value;

        field.setValue(value);
        return field;
    }

    private class EditorInputVerifier extends InputVerifier {
        /**
         * Checks whether the JComponent's input is valid. This method should
         * have no side effects. It returns a boolean indicating the status
         * of the argument's input.
         *
         * @param input the JComponent to verify
         * @return <code>true</code> when valid, <code>false</code> when invalid
         * @see javax.swing.JComponent#setInputVerifier
         * @see javax.swing.JComponent#getInputVerifier
         */
        public boolean verify(JComponent input) {
            // make sure we are not canceling
            return !canceling && isEditorDoneEditing();
        }
    }
}
