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
import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class LayoutHelper {

    /**
     * Creates a vertical box containing the given label and component. The label and component
     * will have their Alignment set to LEFT_ALIGNMENT.
     *
     * @param label     for the component
     * @param component to add to box
     * @return a box containing the specified label and component
     */
    public static Box createVerticalBox(JLabel label, JComponent component) {
        return createVerticalBox(label, component, true);
    }

    /**
     * Creates a vertical box containing the given label and component. The label and component
     * will have their Alignment set to LEFT_ALIGNMENT and will have their max width set
     * if the lockWidth is true
     *
     * @param label     for the component
     * @param component to add to box
     * @param lockWidth true if max width should be set
     * @return a box containing the specified label and component
     */
    public static Box createVerticalBox(JLabel label, JComponent component, boolean lockWidth) {
        Box box = new Box(BoxLayout.PAGE_AXIS);
        addLabelAndComponentVertically(box, label, component, lockWidth, Component.LEFT_ALIGNMENT);

        return box;
    }

    /**
     * Adds the label and component to the {@code Box} specified.  Max width is set according to the
     * lockWidth parameter.  Additionally, vertical alignment of the label and component is set according
     * to {@code verticalAlignment}.
     *
     * @param box               {@code Box} where to add both label and component
     * @param label             label to add
     * @param component         component to add
     * @param lockWidth         whether to set maximum width or not
     * @param verticalAlignment The vertical alignment to use for label and box
     */
    private static void addLabelAndComponentVertically(Box box, JLabel label, JComponent component,
                                                       boolean lockWidth, float verticalAlignment) {
        box.setAlignmentX(verticalAlignment);
        label.setMaximumSize(label.getPreferredSize());
        label.setAlignmentX(verticalAlignment);
        component.setAlignmentX(verticalAlignment);
        box.add(label);
        box.add(Box.createVerticalStrut(LayoutConstants.LABEL_COMPONENT_SPACE));
        box.add(component);
        box.add(Box.createVerticalStrut(LayoutConstants.COMPONENT_SPACE));

        if (lockWidth) {
            box.setMaximumSize(new Dimension(box.getPreferredSize().width, Short.MAX_VALUE));
        }
    }


}
