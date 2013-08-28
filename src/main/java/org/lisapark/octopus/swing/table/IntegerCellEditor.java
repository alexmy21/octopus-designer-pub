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

import com.jidesoft.validation.ValidationResult;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class IntegerCellEditor extends FormattedCellEditor {
    /**
     * Default Constructor
     */
    public IntegerCellEditor() {
        super(Integer.class);
    }

    @Override
    protected void customizeFormattedField(JFormattedTextField field) {
        field.setHorizontalAlignment(JTextField.TRAILING);

        //Set up the editor for the integers.
        NumberFormatter intFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
        field.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
    }

    public Object getCellEditorValue() {
        // convert from Long to Integer
        Object longValue = super.getCellEditorValue();
        return longValue == null ? null : new Integer(longValue.toString());
    }

    @Override
    protected Object coerceValueToCommit(Object longValue) {
        return longValue == null ? null : new Integer(longValue.toString());
    }

    protected ValidationResult validateValueWithinRange(Object longValue) {
        ValidationResult result = ValidationResult.OK;
        if (longValue != null) {

            Long value = (Long) longValue;
            if (value > Integer.MAX_VALUE) {
                result = new ValidationResult(false);
                result.setMessage(String.format("%s is above the maximum value for an Integer. Please choose a smaller number.", value));

            } else if (value < Integer.MIN_VALUE) {
                result = new ValidationResult(false);
                result.setMessage(String.format("%s is below the minimum value for an Integer. Please choose a larger number.", value));
            }
        }

        return result;
    }
}
