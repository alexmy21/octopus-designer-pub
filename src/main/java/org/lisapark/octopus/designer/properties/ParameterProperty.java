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

import com.jidesoft.grid.AbstractJideCellEditor;
import com.jidesoft.grid.CellEditorManager;
import com.jidesoft.grid.CellRendererManager;
import com.jidesoft.grid.Property;
import com.jidesoft.validation.ValidationObject;
import com.jidesoft.validation.ValidationResult;
import com.jidesoft.validation.Validator;
import org.lisapark.octopus.ProgrammerException;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.parameter.Parameter;
import org.lisapark.octopus.swing.JideCellEditorDecorator;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * This class is an implementation of a Jide {@link Property} that is used for displaying and editing an Octopus
 * {@link Parameter} in a {@link com.jidesoft.grid.PropertyTable}. This property's name will be set to
 * {@link Parameter#getName()}, description to {@link Parameter#getDescription()}, and type to
 * {@link Parameter#getType()}. If the parameter has a {@link javax.swing.table.TableCellEditor} or
 * {@link javax.swing.table.TableCellRenderer} they will also be set on the property; otherwise the type will used to
 * determine the editor and renderer.
 * <p/>
 * The {@link ParameterProperty} will also set a {@link Validator} on the {@link AbstractJideCellEditor} if the
 * {@link Parameter#isConstrained()} or {@link Parameter#isRequired()}. This will prevent the user from choosing
 * invalid values for said parameter.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 * @see org.lisapark.octopus.core.parameter.Parameter
 * @see com.jidesoft.grid.Property
 * @see com.jidesoft.grid.PropertyTable
 */
class ParameterProperty extends ComponentProperty<Parameter> {

    ParameterProperty(final Parameter parameter) {
        super(parameter);
        setCategory("Parameters");
        // ensure we set the type of property because this is how Jide will determine a default editor/renderer
        setType(parameter.getType());

        // setup the editor and renderer
        TableCellEditor cellEditor = getCellEditorForParameter(parameter);
        setCellEditor(cellEditor);

        TableCellRenderer cellRenderer = getCellRendererForParameter(parameter);
        setTableCellRenderer(cellRenderer);
    }

    private TableCellEditor getCellEditorForParameter(Parameter<?> parameter) {
        Class<TableCellEditor> cellEditorClass = parameter.getCellEditorClass();
        TableCellEditor cellEditor = null;

        // first try and create an editor based on the class from the parameter if it is there
        if (cellEditorClass != null) {
            try {
                cellEditor = cellEditorClass.newInstance();
            } catch (Exception e) {
                // this should NEVER happen
                throw new ProgrammerException("Could not create a cell editor from a class", e);
            }
        }

        if (cellEditor == null) {
            // loop up default
            cellEditor = (TableCellEditor) CellEditorManager.getEditor(parameter.getType());
        }

        // now set up the validation if the parameter requires it
        if (parameter.isRequired() || parameter.isConstrained()) {
            AbstractJideCellEditor jideCellEditor;

            if (cellEditor instanceof AbstractJideCellEditor) {
                jideCellEditor = (AbstractJideCellEditor) cellEditor;
            } else {

                // decorate the "real" editor inside a Jide one. We do this to make use of the Jide framework validation
                jideCellEditor = new JideCellEditorDecorator(cellEditor);
            }

            jideCellEditor.addValidationListener(new ParameterValidator(parameter));
            cellEditor = (TableCellEditor) jideCellEditor;
        }

        return cellEditor;
    }

    private TableCellRenderer getCellRendererForParameter(Parameter<?> parameter) {
        Class<TableCellRenderer> cellRendererClass = parameter.getCellRendererClass();
        TableCellRenderer cellRenderer = null;

        // first try and create an renderer based on the class from the parameter if it is there
        if (cellRendererClass != null) {
            try {
                cellRenderer = cellRendererClass.newInstance();
            } catch (Exception e) {
                // this should NEVER happen
                throw new ProgrammerException("Could not create a cell renderer from a class", e);
            }
        }

        if (cellRenderer == null) {
            // loop up default
            cellRenderer = CellRendererManager.getRenderer(parameter.getType());
        }

        return cellRenderer;
    }

    @Override
    public Object getValue() {
        return getComponent().getValue();
    }

    @SuppressWarnings("unchecked,raw")
    @Override
    public void setValue(Object value) {
        try {
            getComponent().setValue(value);
        } catch (ValidationException ex) {
            // this should never happen because we are constraining the value in the editor and validator
            throw new ProgrammerException(ex);
        }
    }

    @Override
    public String getToolTipText() {
        return getComponent().getValueForDisplay();
    }

    /**
     * An implementation of a Jide {@link Validator} that will ensure the {@link Parameter} is valid according to
     * it's {@link Parameter#validateValue(Object)}  method.
     */
    private static class ParameterValidator implements Validator {

        private final Parameter parameter;

        private ParameterValidator(Parameter<?> parameter) {
            this.parameter = parameter;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ValidationResult validating(ValidationObject validationObject) {
            // start off initially as valid, that is what true is here
            ValidationResult result = new ValidationResult(true);
            // if it isn't valid we want to have the cell editor stay right where it is
            result.setFailBehavior(ValidationResult.FAIL_BEHAVIOR_PERSIST);

            Object value = validationObject.getNewValue();

            try {
                parameter.validateValue(value);
            } catch (ValidationException e) {
                result.setValid(false);
                result.setMessage(e.getLocalizedMessage());
            }

            return result;
        }
    }
}
