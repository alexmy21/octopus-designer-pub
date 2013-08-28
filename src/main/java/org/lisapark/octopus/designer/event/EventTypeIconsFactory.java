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
package org.lisapark.octopus.designer.event;

import com.jidesoft.icons.IconsFactory;

import javax.swing.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class EventTypeIconsFactory {

    public static final String ADD = "icons/add.png";
    public static final String DELETE = "icons/delete.png";

    static ImageIcon getImageIcon(String name) {
        if (name != null) {
            return IconsFactory.getImageIcon(EventTypeIconsFactory.class, name);
        } else {
            return null;
        }
    }

    public static void main(String[] argv) {
        IconsFactory.generateHTML(EventTypeIconsFactory.class);
    }
}
