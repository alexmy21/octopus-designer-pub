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

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DoubleCellEditor extends FormattedCellEditor {
    /**
     * Default Constructor
     */
    public DoubleCellEditor() {
        super(Double.class);
    }

    @Override
    protected void customizeFormattedField(JFormattedTextField field) {
        field.setHorizontalAlignment(JTextField.LEADING);

        //Set up the editor for the doubles.
        NumberFormatter doubleFormatter = new NumberFormatter(NumberFormat.getNumberInstance());
        field.setFormatterFactory(new DefaultFormatterFactory(doubleFormatter));
    }
}
