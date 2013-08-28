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
package org.lisapark.octopus.designer.canvas.providers;

import org.lisapark.octopus.core.Node;
import org.lisapark.octopus.designer.canvas.ProcessingScene;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.widget.Widget;

import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class NodeMoveProvider implements MoveProvider {

    private ProcessingScene scene;

    public NodeMoveProvider(ProcessingScene scene) {
        this.scene = scene;
    }

    @Override
    public void movementStarted(Widget widget) {

    }

    @Override
    public void movementFinished(Widget widget) {

    }

    @Override
    public Point getOriginalLocation(Widget widget) {
        return widget.getPreferredLocation();
    }

    @Override
    public void setNewLocation(Widget widget, Point point) {
        widget.setPreferredLocation(point);

        Object object = scene.findObject(widget);
        if (object instanceof Node) {
            Node node = (Node) object;
            node.setLocation(point);
        }
    }
}
