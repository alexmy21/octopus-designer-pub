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

import com.jidesoft.validation.ValidationObject;
import com.jidesoft.validation.ValidationResult;
import com.jidesoft.validation.Validator;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.util.Naming;

/**
 * Implementation of a Jide {@link com.jidesoft.validation.Validator} to see if the attribute name is valid.
 */
class AttributeNameValidator implements Validator {

    @Override
    public ValidationResult validating(ValidationObject validationObject) {
        // start off initially as valid, that is what true is here
        ValidationResult result = new ValidationResult(true);
        // if it isn't valid we want to have the cell editor stay right where it is
        result.setFailBehavior(ValidationResult.FAIL_BEHAVIOR_PERSIST);

        Object value = validationObject.getNewValue();

        try {
            validateAttributeName((String) value);
        } catch (ValidationException e) {
            result.setValid(false);
            result.setMessage(e.getLocalizedMessage());
        }

        return result;
    }

    void validateAttributeName(String name) throws ValidationException {
        Naming.checkValidity(name, "Attribute name");
    }
}
