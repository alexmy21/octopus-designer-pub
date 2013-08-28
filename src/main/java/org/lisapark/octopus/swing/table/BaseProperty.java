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
package org.lisapark.octopus.swing.table;

import com.jidesoft.grid.Property;

/**
 * This is a base class for all {@link Property}s in Octopus. This is an extension of a Jide property that will be
 * used to add new functionality.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class BaseProperty extends Property {

    /**
     * Returns the tool tip that should be used for this property
     *
     * @return tool tip text
     */
    public abstract String getToolTipText();
}
