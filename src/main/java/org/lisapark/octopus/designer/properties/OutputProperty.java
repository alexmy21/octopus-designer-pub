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

import org.lisapark.octopus.core.Output;
import org.lisapark.octopus.core.event.EventType;
import org.lisapark.octopus.designer.properties.support.EventTypeCellEditor;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class OutputProperty extends ComponentProperty<Output> {

    OutputProperty(Output output, final EventTypeCellEditor eventTypeCellEditor) {
        super(output);

        setType(EventType.class);
        setEditable(true);
        setCategory("Output");
        setCellEditor(eventTypeCellEditor);
    }

    @Override
    public Object getValue() {
        return getComponent().getEventType();
    }

    @Override
    public void setValue(Object value) {
        getComponent().setEventType((EventType) value);
    }
}
