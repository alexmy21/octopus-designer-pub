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

import com.jidesoft.combobox.ListExComboBox;
import com.jidesoft.grid.ListComboBoxCellEditor;
import org.lisapark.octopus.ProgrammerException;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.event.Attribute;
import org.lisapark.octopus.core.processor.ProcessorInput;
import org.lisapark.octopus.core.source.Source;
import org.lisapark.octopus.designer.properties.support.AttributeListCellRenderer;
import org.lisapark.octopus.designer.properties.support.AttributeTableCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * This class is an implementation of a Jide {@link com.jidesoft.grid.Property} that is used for displaying and editing
 * an Octopus {@link ProcessorInput} in a {@link com.jidesoft.grid.PropertyTable}. This property's name will be set to
 * {@link ProcessorInput#getName()}, description to {@link ProcessorInput#getDescription()}, and type to
 * {@link ProcessorInput#getType()}.
 * <p/>
 * The editor used for this property type is a subclass of a {@link ListComboBoxCellEditor}. The user will be presented
 * with {@link Attribute}s from the {@link org.lisapark.octopus.core.processor.ProcessorInput#getSource()} if it is
 * set. Only <b>compatible</b> attributes will be shown in the list. Also note that if the source it not set, this
 * property will <b>not</b> be editable.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 * @see org.lisapark.octopus.core.parameter.Parameter
 * @see com.jidesoft.grid.Property
 * @see com.jidesoft.grid.PropertyTable
 */
class ProcessorInputProperty extends ComponentProperty<ProcessorInput> {

    ProcessorInputProperty(ProcessorInput input) {
        super(input);
        setType(Attribute.class);

        setCellEditor(new InputAttributeCellEditor(input));
        setTableCellRenderer(new AttributeTableCellRenderer());

        // todo localize string
        setCategory("Input data");
    }

    @Override
    public boolean hasValue() {
        return getComponent().getSource() != null;
    }

    /**
     * Returns true of the {@link org.lisapark.octopus.core.processor.ProcessorInput#getSource()} has been set. Otherwise
     * there are no {@link Attribute}s to choose from.
     *
     * @return true if the source has been set for the input
     */
    @Override
    public boolean isEditable() {
        return hasValue();
    }

    @Override
    public Object getValue() {
        return getComponent().getSourceAttribute();
    }

    @SuppressWarnings("unchecked,raw")
    @Override
    public void setValue(Object value) {
        if (value == null) {
            getComponent().clearSourceAttribute();
        } else {
            try {
                getComponent().setSourceAttribute((Attribute) value);
            } catch (ValidationException ex) {
                // this should never happen because we are constraining the value in the editor
                throw new ProgrammerException(ex);
            }
        }
    }

    /**
     * This class extends {@link ListComboBoxCellEditor} to provides a filtered {@link DefaultComboBoxModel} of the
     * available attributes from a source.
     */
    static class InputAttributeCellEditor extends ListComboBoxCellEditor {
        private final ProcessorInput input;

        private InputAttributeCellEditor(ProcessorInput input) {
            this.input = input;
        }

        @Override
        protected ListExComboBox createListComboBox(ComboBoxModel comboBoxModel, Class<?> aClass) {
            ListExComboBox listExComboBox = super.createListComboBox(comboBoxModel, aClass);
            listExComboBox.setRenderer(new AttributeListCellRenderer());

            return listExComboBox;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            Source source = input.getSource();
            // Jide's editor requires a new model every time, otherwise there are issues
            DefaultComboBoxModel newModel = new DefaultComboBoxModel();

            ListExComboBox listExComboBox = (ListExComboBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);

            if (source != null) {
                // todo - do we move this into output??
                List<Attribute> attributes = source.getOutput().getAttributes();

                for (Attribute attribute : attributes) {
                    // NOTE - we only add attribute that are compatible with the input's type
                    if (attribute.isCompatibleWith(input.getType())) {
                        newModel.addElement(attribute);
                    }
                }
                newModel.setSelectedItem(input.getSourceAttribute());
            } else {

                newModel.setSelectedItem(null);
            }

            listExComboBox.setModel(newModel);
            listExComboBox.setPopupVolatile(true);

            return listExComboBox;
        }
    }
}
