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

import com.jidesoft.grid.AbstractJideCellEditor;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;

/**
 * This class is an implementation of the <a href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>
 * to wrap any TableCellEditor inside an {@link AbstractJideCellEditor}. We do this in order to make use of the
 * validation features of the Jide cell editor.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 * @see com.jidesoft.grid.AbstractJideCellEditor#validate(Object, Object)
 */
public class JideCellEditorDecorator extends AbstractJideCellEditor implements TableCellEditor {

    private final TableCellEditor realEditor;

    public JideCellEditorDecorator(TableCellEditor realEditor) {
        this.realEditor = realEditor;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return realEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public Object getCellEditorValue() {
        return realEditor.getCellEditorValue();
    }

    /**
     * Returns true.
     *
     * @param e an event object
     * @return true
     */
    public boolean isCellEditable(EventObject e) {
        return realEditor.isCellEditable(e);
    }

    /**
     * Returns true.
     *
     * @param anEvent an event object
     * @return true
     */
    public boolean shouldSelectCell(EventObject anEvent) {
        return realEditor.shouldSelectCell(anEvent);
    }

    /**
     * Calls <code>fireEditingStopped</code> and returns true.
     *
     * @return true
     */
    public boolean stopCellEditing() {
        return realEditor.stopCellEditing();
    }

    /**
     * Calls <code>fireEditingCanceled</code>.
     */
    public void cancelCellEditing() {
        if (realEditor instanceof AbstractCellEditor) {
            AbstractCellEditor abstractCellEditor = (AbstractCellEditor) realEditor;

            // Guaranteed to return a non-null array
            Object[] listeners = abstractCellEditor.getCellEditorListeners();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == CellEditorListener.class) {
                    // Lazily create the event:
                    if (changeEvent == null)
                        changeEvent = new ChangeEvent(realEditor);
                    ((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
                }
            }
        }
    }

    /**
     * Adds a <code>CellEditorListener</code> to the listener list.
     *
     * @param l the new listener to be added
     */
    public void addCellEditorListener(CellEditorListener l) {
        realEditor.addCellEditorListener(l);
    }

    /**
     * Removes a <code>CellEditorListener</code> from the listener list.
     *
     * @param l the listener to be removed
     */
    public void removeCellEditorListener(CellEditorListener l) {
        realEditor.removeCellEditorListener(l);
    }

    /**
     * Returns an array of all the <code>CellEditorListener</code>s added
     * to this AbstractCellEditor with addCellEditorListener().
     *
     * @return all of the <code>CellEditorListener</code>s added or an empty
     *         array if no listeners have been added
     * @since 1.4
     */
    public CellEditorListener[] getCellEditorListeners() {
        CellEditorListener[] listeners;
        if (realEditor instanceof AbstractCellEditor) {
            AbstractCellEditor abstractCellEditor = (AbstractCellEditor) realEditor;

            listeners = abstractCellEditor.getCellEditorListeners();
        } else {
            listeners = new CellEditorListener[]{};
        }

        return listeners;
    }
}
