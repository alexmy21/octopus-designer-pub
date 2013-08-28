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
package org.lisapark.octopus.designer;

import com.jidesoft.icons.IconsFactory;

import javax.swing.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DesignerIconsFactory {

    public static final String OCTOPUS_LARGE = "icons/octopus-icon.png";
    public static final String NEW = "icons/new.gif";
    public static final String SAVE = "icons/save.gif";
    public static final String OPEN = "icons/open.gif";
    public static final String COMPILE = "icons/compile.gif";
    public static final String RUN = "icons/run.png";

    public static ImageIcon getImageIcon(String name) {
        if (name != null) {
            return IconsFactory.getImageIcon(DesignerIconsFactory.class, name);
        } else {
            return null;
        }
    }

    public static void main(String[] argv) {
        IconsFactory.generateHTML(DesignerIconsFactory.class);
    }
}
