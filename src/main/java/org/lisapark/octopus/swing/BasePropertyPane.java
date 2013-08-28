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

import com.jidesoft.action.CommandBar;
import com.jidesoft.grid.PropertyPane;
import com.jidesoft.grid.PropertyTable;
import com.jidesoft.swing.JideButton;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import java.awt.event.MouseEvent;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class BasePropertyPane extends PropertyPane {

    public BasePropertyPane(PropertyTable propertyTable) {
        super(propertyTable);
    }

    @Override
    protected JComponent createToolBarComponent() {
        CommandBar toolBar = ComponentFactory.createMenuBar();

        toolBar.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        toolBar.setFloatable(false);
        toolBar.setChevronAlwaysVisible(false);
        return toolBar;
    }

    @Override
    protected JideButton createButton(Action action) {
        JideButton button = new BaseJideButton(action);
        button.setRequestFocusEnabled(false);
        return button;
    }

    private static class BaseJideButton extends JideButton {

        private BaseJideButton(Action action) {
            super(action);
        }

        @Override
        public void setBorder(Border border) {
            if ((border instanceof UIResource)) {
                super.setBorder(border);
            }
        }

        /**
         * Processes mouse events occurring on this component by dispatching them to any registered
         * <code>MouseListener</code> objects, refer to {@link java.awt.Component#processMouseEvent(java.awt.event.MouseEvent)}
         * for a complete description of this method.
         * <p/>
         * This method is overridden to fix the problem with focus and selection for JTrees
         *
         * @param e the mouse event
         * @see java.awt.Component#processMouseEvent
         */
        protected void processMouseEvent(MouseEvent e) {
            if (isFocusable() && getVerifyInputWhenFocusTarget() && isMouseClickEvent(e)) {
                // only process the event if we have focus or if we can get focus
                if (hasFocus() || requestFocusInWindow()) {
                    super.processMouseEvent(e);
                }
            } else {
                super.processMouseEvent(e);
            }

        }

        private boolean isMouseClickEvent(MouseEvent e) {
            int id = e.getID();

            return id == MouseEvent.MOUSE_CLICKED || id == MouseEvent.MOUSE_PRESSED || id == MouseEvent.MOUSE_RELEASED;
        }
    }
}
