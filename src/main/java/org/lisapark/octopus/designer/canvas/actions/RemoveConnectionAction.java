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
package org.lisapark.octopus.designer.canvas.actions;

import org.lisapark.octopus.designer.canvas.Connection;
import org.lisapark.octopus.designer.canvas.ProcessingScene;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

import java.awt.event.KeyEvent;

/**
 * This action responds to the user pressing the {@link KeyEvent#VK_DELETE} while a connection, i.e. edge, is selected
 * in the scene. It will verify that the object for the widget is a {@link Connection} then call the
 * {@link ProcessingScene#removeConnection(org.lisapark.octopus.designer.canvas.Connection)} method.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class RemoveConnectionAction extends WidgetAction.Adapter {
    private final ProcessingScene processingScene;

    public RemoveConnectionAction(ProcessingScene processingScene) {

        this.processingScene = processingScene;
    }

    public State keyTyped(Widget widget, WidgetKeyEvent event) {
        if (event.getKeyChar() == KeyEvent.VK_DELETE) {
            Object object = processingScene.findObject(widget);

            if (object != null && object instanceof Connection) {
                Connection connection = (Connection) object;

                processingScene.removeConnection(connection);
                return State.CONSUMED;
            }
        }
        return State.REJECTED;
    }
}
