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
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class Borders {

    /**
     * Inside border for panels
     */
    public final static Border PADDING_BORDER = BorderFactory.createEmptyBorder(
            LayoutConstants.DIALOG_MARGIN_TOP,
            LayoutConstants.DIALOG_MARGIN_LEFT,
            LayoutConstants.DIALOG_MARGIN_BOTTOM,
            LayoutConstants.DIALOG_MARGIN_RIGHT
    );

    /**
     * Inside border for panels inside of tabbed panes
     */
    public final static Border TABBED_PANE_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), PADDING_BORDER);
}
