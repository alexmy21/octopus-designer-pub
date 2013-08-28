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
package org.lisapark.octopus.designer.canvas;

import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.model.StateModel;
import org.netbeans.api.visual.widget.ImageWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Utilities;

import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class NodeWidget extends Widget implements StateModel.Listener {

    private static final Image IMAGE_EXPAND = Utilities.loadImage("org/netbeans/modules/visual/resources/vmd-expand.png"); // NOI18N
    private static final Image IMAGE_COLLAPSE = Utilities.loadImage("org/netbeans/modules/visual/resources/vmd-collapse.png"); // NOI18N

    private static final Color BORDER_CATEGORY_BACKGROUND = new Color(0xCDDDF8);
    private static final Border BORDER_MINIMIZE = BorderFactory.createRoundedBorder(2, 2, null, new Color(0xBACDF0));
    static final Color COLOR_SELECTED = new Color(0x748CC0);
    static final Border BORDER = BorderFactory.createOpaqueBorder(2, 8, 2, 8);
    static final Border BORDER_HOVERED = BorderFactory.createLineBorder(2, 8, 2, 8, Color.BLACK);

    private Widget header;
    private ImageWidget minimizeWidget;
    private ImageWidget imageWidget;
    private LabelWidget nameWidget;

    /**
     * Creates a new widget which will be used in a specified scene.
     *
     * @param scene the scene where the widget is going to be used
     */
    public NodeWidget(Scene scene) {
        super(scene);
    }

    @Override
    public void stateChanged() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
