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

import com.jidesoft.combobox.ExComboBox;
import com.jidesoft.grid.ListComboBoxCellEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ComboBoxCellEditor extends ListComboBoxCellEditor implements FocusListener {

    /**
     * This flag will be true when the <method>cancelCellEditing</method> is called
     */
    private boolean canceling;

    private boolean stopped;

    /**
     * Component that would get the next focus
     */
    private JComponent oppositeComponent;

    public ComboBoxCellEditor() {
        init();
    }

    public ComboBoxCellEditor(ComboBoxModel comboBoxModel) {
        super(comboBoxModel);
        init();
    }

    public ComboBoxCellEditor(Object[] objects) {
        super(objects);
        init();
    }

    private void init() {
        setAutoStopCellEditing(false);
        _comboBox.setPopupCancelBehavior(ExComboBox.REVERT);
        _comboBox.setInputVerifier(new EditorInputVerifier());
        _comboBox.addFocusListener(this);
    }

    @Override
    public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
        stopped = false;
        return super.getTableCellEditorComponent(jTable, o, b, i, i1);
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

    @Override
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

        // if we have already stopped, don't try and do it again
        if (!stopped) {
            stopped = isEditorDoneEditing();

            if (stopped) {
                stopped = super.stopCellEditing();
            }
        }

        return stopped;
    }

    @Override
    public final void actionPerformed(ActionEvent actionEvent) {
        stopCellEditing();
    }

    /**
     * Returns true if the editor is ready to give up focus.
     *
     * @return true if focus can be removed from editor
     */
    protected boolean isEditorDoneEditing() {
        return true;
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
            // make sure we are not canceling or already stopped
            boolean yieldFocus;

            if (canceling || stopped) {
                yieldFocus = true;
            } else {
                stopped = isEditorDoneEditing();

                if (stopped) {
                    // we call this to properly fire the events
                    ComboBoxCellEditor.super.stopCellEditing();
                }

                yieldFocus = stopped;
            }

            return yieldFocus;
        }
    }
}
