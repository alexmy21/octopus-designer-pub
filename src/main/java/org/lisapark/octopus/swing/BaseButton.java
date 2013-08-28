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

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class BaseButton extends JButton {
    public BaseButton() {
    }

    public BaseButton(Action action) {
        super(action);
    }

    public BaseButton(Icon icon) {
        super(icon);
    }

    public BaseButton(String s) {
        super(s);
    }

    public BaseButton(String s, Icon icon) {
        super(s, icon);
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
