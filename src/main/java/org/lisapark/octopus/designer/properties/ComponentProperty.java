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
package org.lisapark.octopus.designer.properties;

import org.lisapark.octopus.core.AbstractComponent;
import org.lisapark.octopus.swing.table.BaseProperty;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
abstract class ComponentProperty<T extends AbstractComponent> extends BaseProperty {

    private T component;

    protected ComponentProperty(T component) {
        this.component = component;
        setName(component.getName());
        setDescription(component.getDescription());
    }

    protected T getComponent() {
        return component;
    }

    @Override
    public String getToolTipText() {
        return component.getDescription();
    }
}
