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

import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.SeparatorWidget;

import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ProcessorWidget extends NodeWidget {

    private LabelWidget header;
    private SeparatorWidget pinsSeparator;

    /**
     * Creates a new widget which will be used in a specified scene.
     *
     * @param scene the scene where the widget is going to be used
     */
    public ProcessorWidget(Scene scene) {
        super(scene);
        setOpaque(false);
        setLayout(LayoutFactory.createVerticalFlowLayout());
        setMinimumSize(new Dimension(128, 8));

        header = new LabelWidget(scene);
        addChild(header);

        pinsSeparator = new SeparatorWidget(scene, SeparatorWidget.Orientation.HORIZONTAL);
        addChild(pinsSeparator);
    }
}
