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

import com.jidesoft.grid.CellEditorFactory;
import com.jidesoft.grid.CellEditorManager;
import com.jidesoft.grid.ContextSensitiveTable;
import com.jidesoft.grid.EditingNotStoppedException;
import com.jidesoft.plaf.basic.BasicJideTableUI;
import com.jidesoft.validation.ValidationResult;
import org.lisapark.octopus.swing.table.DoubleCellEditor;
import org.lisapark.octopus.swing.table.FloatCellEditor;
import org.lisapark.octopus.swing.table.IntegerCellEditor;
import org.lisapark.octopus.swing.table.LongCellEditor;
import org.lisapark.octopus.swing.table.ShortCellEditor;
import org.lisapark.octopus.swing.table.StringCellEditor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class BaseTable extends ContextSensitiveTable {
    static {
        UIManager.getDefaults().put("BaseTableUI", "org.lisapark.octopus.swing.BaseTable$BaseTableUI");
    }

    private ValidationFailedListener validationFailedListener;

    public BaseTable() {
        init();
    }

    public BaseTable(TableModel tableModel) {
        super(tableModel);
        init();
    }

    private void init() {
        CellEditorManager.registerEditor(Short.class, new CellEditorFactory() {
            @Override
            public CellEditor create() {
                return new ShortCellEditor();
            }
        });
        CellEditorManager.registerEditor(Integer.class, new CellEditorFactory() {
            @Override
            public CellEditor create() {
                return new IntegerCellEditor();
            }
        });

        CellEditorManager.registerEditor(Long.class, new CellEditorFactory() {
            @Override
            public CellEditor create() {
                return new LongCellEditor();
            }
        });
        CellEditorManager.registerEditor(Float.class, new CellEditorFactory() {
            @Override
            public CellEditor create() {
                return new FloatCellEditor();
            }
        });
        CellEditorManager.registerEditor(Double.class, new CellEditorFactory() {
            @Override
            public CellEditor create() {
                return new DoubleCellEditor();
            }
        });
        CellEditorManager.registerEditor(String.class, new CellEditorFactory() {
            @Override
            public CellEditor create() {
                return new StringCellEditor();
            }
        });
    }

    public ValidationFailedListener getValidationFailedListener() {
        return validationFailedListener;
    }

    public void setValidationFailedListener(ValidationFailedListener validationFailedListener) {
        this.validationFailedListener = validationFailedListener;
    }

    @Override
    public void editingStopped(ChangeEvent e) {
        try {
            super.editingStopped(e);

        } catch (EditingNotStoppedException ex) {
            ValidationResult result = ex.getValidationResult();

            if (validationFailedListener != null) {
                validationFailedListener.validationFailed(result);
            }
        }
    }

    @Override
    public String getActualUIClassID() {
        return "BaseTableUI";
    }

    public String getUIClassID() {
        return "BaseTableUI";
    }

    /**
     * We need to subclass the Jide's UI Delegate for the table because there is
     * a bug when typing into a cell. If the table has focus, and the user types a non-
     * navigational character, the letter will be put into the cell, but the cell is never
     * given focus correctly; therefore resulting in focus management problems. One example
     * of such a problem is there is no caret for a text field. Another example is if there
     * is an InputVefifier on an editor, it won't be give a chance to act.
     * <p/>
     * Note that most of the code comes from BasicTableUI's Handler class
     *
     * @see javax.swing.plaf.basic.BasicTableUI.Handler#keyTyped
     */
    public static class BaseTableUI extends BasicJideTableUI {

        public static ComponentUI createUI(JComponent c) {
            return new BaseTableUI();
        }

        /**
         * Creates the key listener for handling keyboard navigation in the JTable.
         */
        protected KeyListener createKeyListener() {
            return new KeyHandler();
        }

        private class KeyHandler implements KeyListener {
            /**
             * Invoked when a key has been typed.
             * See the class description for {@link java.awt.event.KeyEvent} for a definition of
             * a key typed event.
             */
            public void keyTyped(final KeyEvent e) {
                KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getKeyChar(), e.getModifiers());

                // We register all actions using ANCESTOR_OF_FOCUSED_COMPONENT
                // which means that we might perform the appropriate action
                // in the table and then forward it to the editor if the editor
                // had focus. Make sure this doesn't happen by checking our
                // InputMaps.
                InputMap map = table.getInputMap(JComponent.WHEN_FOCUSED);

                if (map != null && map.get(keyStroke) != null) {
                    return;
                }

                map = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

                if (map != null && map.get(keyStroke) != null) {
                    return;
                }

                keyStroke = KeyStroke.getKeyStrokeForEvent(e);

                // The AWT seems to generate an unconsumed \r event when
                // ENTER (\n) is pressed.
                if (e.getKeyChar() == '\r') {
                    return;
                }

                int leadRow = getAdjustedLead(table, true);
                int leadColumn = getAdjustedLead(table, false);

                if (leadRow != -1 && leadColumn != -1 && !table.isEditing()) {
                    if (!table.editCellAt(leadRow, leadColumn)) {
                        return;
                    }
                }

                Component editorComp = table.getEditorComponent();

                if (table.isEditing() && editorComp != null) {
                    if (editorComp instanceof JComponent) {

                        final JComponent component = (JComponent) editorComp;

                        if (!component.hasFocus()) {
                            // give the component focus if it doesn't have it
                            component.requestFocusInWindow();
                        }

                        map = component.getInputMap(JComponent.WHEN_FOCUSED);
                        Object binding = (map != null) ? map.get(keyStroke) : null;

                        if (binding == null) {
                            map = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                            binding = (map != null) ? map.get(keyStroke) : null;
                        }

                        if (binding != null) {
                            ActionMap am = component.getActionMap();
                            final Action action = (am != null) ? am.get(binding) : null;

                            final KeyStroke keyStroke1 = keyStroke;

                            // since we called component.requestFocusInWindow, we need to key event
                            // to be handled after the component receieves focus. Per the JComponent.requestFocusInWindow
                            // this cannot be guranuteed that the component gets focus after this call returns,
                            // but only after it received the FOCUS_GAINED event. InvokeLater ensure this occurs
                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {
                                    if (action != null && component instanceof JFormattedTextField) {
                                        // JFormatted Text Fields has a known bug where when focus is gained, calling select
                                        // actually won't work. So since HightlightFocusListener.focusGained is on every text
                                        // component in the system, we need to make sure we do another invoke later because
                                        // we need the text to be selected first before send the key event
                                        SwingUtilities.invokeLater(new Runnable() {
                                            public void run() {
                                                SwingUtilities.notifyAction(action, keyStroke1, e, component, e.getModifiers());
                                            }
                                        });
                                    } else if (action != null) {

                                        SwingUtilities.notifyAction(action, keyStroke1, e, component, e.getModifiers());
                                    }
                                }
                            });

                            e.consume();
                        }
                    }
                }
            }

            /**
             * Invoked when a key has been pressed.
             * See the class description for {@link java.awt.event.KeyEvent} for a definition of
             * a key pressed event.
             */
            public void keyPressed(KeyEvent e) {
                // noop
            }

            /**
             * Invoked when a key has been released.
             * See the class description for {@link java.awt.event.KeyEvent} for a definition of
             * a key released event.
             */
            public void keyReleased(KeyEvent e) {
                // noop
            }

            private int getAdjustedLead(JTable table, boolean row) {
                return row ? getAdjustedLead(table, row, table.getSelectionModel())
                        : getAdjustedLead(table, row, table.getColumnModel().getSelectionModel());
            }

            private int getAdjustedLead(JTable table,
                                        boolean row,
                                        ListSelectionModel model) {

                int index = model.getLeadSelectionIndex();
                int compare = row ? table.getRowCount() : table.getColumnCount();

                return index < compare ? index : -1;
            }
        }
    }

}
