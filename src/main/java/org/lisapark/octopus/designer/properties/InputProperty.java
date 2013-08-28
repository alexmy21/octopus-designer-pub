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

import org.lisapark.octopus.core.Input;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class InputProperty extends ComponentProperty<Input> {
    InputProperty(Input input) {
        super(input);
        setType(String.class);
        setCategory("Source");
        setEditable(false);
    }

    @Override
    public Object getValue() {
        Input input = getComponent();
        return input.getSource() == null ? "" : input.getSource().getOutput().getName();
    }

    public void setValue(Object value) {
        // noop
    }
}
