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
public class FloatCellEditor extends FormattedCellEditor {
    /**
     * Default Constructor
     */
    public FloatCellEditor() {
        super(Float.class);
    }

    @Override
    protected void customizeFormattedField(JFormattedTextField field) {
        field.setHorizontalAlignment(JTextField.LEADING);

        //Set up the editor for the floats.
        NumberFormatter floatFormatter = new NumberFormatter(NumberFormat.getNumberInstance());
        field.setFormatterFactory(new DefaultFormatterFactory(floatFormatter));
    }

    public Object getCellEditorValue() {
        // convert from Double to Float
        Object doubleValue = super.getCellEditorValue();
        return doubleValue == null ? null : new Float(doubleValue.toString());
    }

    @Override
    protected Object coerceValueToCommit(Object doubleValue) {
        return doubleValue == null ? null : new Float(doubleValue.toString());
    }

    protected ValidationResult validateValueWithinRange(Object doubleValue) {
        ValidationResult result = ValidationResult.OK;
        if (doubleValue != null) {

            Double value = (Double) doubleValue;
            if (value > Float.MAX_VALUE) {
                result = new ValidationResult(false);
                result.setMessage(String.format("%s is above the maximum value for a Float. Please choose a smaller number.", value));

            } else if (value < (-Float.MAX_VALUE)) {
                result = new ValidationResult(false);
                result.setMessage(String.format("%s is below the minimum value for a Float. Please choose a larger number.", value));
            }
        }

        return result;
    }
}
