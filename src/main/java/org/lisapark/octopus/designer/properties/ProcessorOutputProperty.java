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

import com.jidesoft.validation.ValidationObject;
import com.jidesoft.validation.ValidationResult;
import com.jidesoft.validation.Validator;
import org.lisapark.octopus.ProgrammerException;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.event.Attribute;
import org.lisapark.octopus.core.processor.ProcessorOutput;
import org.lisapark.octopus.swing.table.StringCellEditor;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class ProcessorOutputProperty extends ComponentProperty<ProcessorOutput> {

    ProcessorOutputProperty(ProcessorOutput output) {
        super(output);
        setType(String.class);
        setCategory("Output data");

        StringCellEditor cellEditor = new StringCellEditor();
        cellEditor.addValidationListener(new ProcessorOutputValidator());
        setCellEditor(cellEditor);
    }

    @Override
    public Object getValue() {
        return getComponent().getAttributeName();
    }

    @SuppressWarnings("unchecked,raw")
    @Override
    public void setValue(Object value) {
        // update the value on the input
        if (value instanceof String) {
            try {
                getComponent().setAttributeName((String) value);
            } catch (ValidationException ex) {
                // this should never happen because we are constraining the value in the editor and validator
                throw new ProgrammerException(ex);
            }

        } else {
            throw new IllegalArgumentException(String.format("Unknown type for input %s", getName()));
        }
    }

    /**
     * An implementation of a Jide {@link com.jidesoft.validation.Validator} that will ensure the {@link org.lisapark.octopus.core.parameter.Parameter} is valid according to
     * it's {@link org.lisapark.octopus.core.parameter.Parameter#validateValue(Object)}  method.
     */
    private static class ProcessorOutputValidator implements Validator {

        @SuppressWarnings("unchecked")
        @Override
        public ValidationResult validating(ValidationObject validationObject) {
            // start off initially as valid, that is what true is here
            ValidationResult result = new ValidationResult(true);
            // if it isn't valid we want to have the cell editor stay right where it is
            result.setFailBehavior(ValidationResult.FAIL_BEHAVIOR_PERSIST);

            Object value = validationObject.getNewValue();

            try {
                Attribute.validateAttributeName((String) value);
            } catch (ValidationException e) {
                result.setValid(false);
                result.setMessage(e.getLocalizedMessage());
            }

            return result;
        }
    }
}
