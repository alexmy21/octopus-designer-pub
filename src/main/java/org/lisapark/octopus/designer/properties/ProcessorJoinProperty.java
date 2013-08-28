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
import org.lisapark.octopus.core.processor.ProcessorJoin;
import org.lisapark.octopus.core.source.Source;
import org.lisapark.octopus.designer.properties.support.AttributeListCellRenderer;
import org.lisapark.octopus.designer.properties.support.AttributeTableCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * This class is an implementation of a Jide {@link com.jidesoft.grid.Property} that is used for displaying and editing
 * an Octopus {@link org.lisapark.octopus.core.processor.ProcessorInput} in a {@link com.jidesoft.grid.PropertyTable}. 
 * This property's name will be set to
 * {@link org.lisapark.octopus.core.processor.ProcessorInput#getName()}, description to {@link org.lisapark.octopus.core.processor.
 * ProcessorInput#getDescription()}, and type to
 * {@link org.lisapark.octopus.core.processor.ProcessorInput#getType()}.
 * <p/>
 * The editor used for this property type is a subclass of a {@link com.jidesoft.grid.ListComboBoxCellEditor}. The user will be presented
 * with {@link org.lisapark.octopus.core.event.Attribute}s from the {@link org.lisapark.octopus.core.processor.ProcessorInput#getSource()} if it is
 * set. Only <b>compatible</b> attributes will be shown in the list. Also note that if the source it not set, this
 * property will <b>not</b> be editable.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 * @see org.lisapark.octopus.core.parameter.Parameter
 * @see com.jidesoft.grid.Property
 * @see com.jidesoft.grid.PropertyTable
 */
class ProcessorJoinProperty extends ComponentProperty<ProcessorInput> {
    private final ProcessorJoin join;

    ProcessorJoinProperty(ProcessorJoin join, ProcessorInput sourceInput, ProcessorInput targetInput) {
        super(sourceInput);
        this.join = join;
        setType(Attribute.class);

        setCellEditor(new JoinAttributeCellEditor(join, sourceInput, targetInput));
        setTableCellRenderer(new AttributeTableCellRenderer());

        // todo localize string
        setCategory("Join");
    }

    @Override
    public boolean hasValue() {
        return getComponent().getSource() != null;
    }

    /**
     * Returns true of the {@link org.lisapark.octopus.core.processor.ProcessorInput#getSource()} has been set. Otherwise
     * there are no {@link org.lisapark.octopus.core.event.Attribute}s to choose from.
     * todo update comment
     *
     * @return true if the source has been set for the sourceInput
     */
    @Override
    public boolean isEditable() {
        return hasValue() && join.isRequired();
    }

    @Override
    public Object getValue() {
        return join.getJoinAttributeForInput(getComponent());
    }

    @SuppressWarnings("unchecked,raw")
    @Override
    public void setValue(Object value) {
        if (value == null) {
            join.clearJoinAttributeForInput(getComponent());
        } else {
            try {
                join.setJoinAttributeForInput(getComponent(), (Attribute) value);
            } catch (ValidationException ex) {
                // this should never happen because we are constraining the value in the editor
                throw new ProgrammerException(ex);
            }
        }
    }

    /**
     * This class extends {@link com.jidesoft.grid.ListComboBoxCellEditor} to provides a filtered {@link javax.swing.DefaultComboBoxModel} of the
     * available attributes from a source.
     */
    static class JoinAttributeCellEditor extends ListComboBoxCellEditor {
        private final ProcessorJoin join;
        private final ProcessorInput sourceInput;
        private final ProcessorInput targetInput;

        private JoinAttributeCellEditor(ProcessorJoin join, ProcessorInput sourceInput, ProcessorInput targetInput) {
            this.join = join;
            this.sourceInput = sourceInput;
            this.targetInput = targetInput;
        }

        @Override
        protected ListExComboBox createListComboBox(ComboBoxModel comboBoxModel, Class<?> aClass) {
            ListExComboBox listExComboBox = super.createListComboBox(comboBoxModel, aClass);
            listExComboBox.setRenderer(new AttributeListCellRenderer());

            return listExComboBox;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            Source source = sourceInput.getSource();
            // Jide's editor requires a new model every time, otherwise there are issues
            DefaultComboBoxModel newModel = new DefaultComboBoxModel();

            ListExComboBox listExComboBox = (ListExComboBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);

            if (source != null) {
                List<Attribute> attributes = source.getOutput().getAttributes();

                // if the target source is not set, or the target join attribute is not set, we can add them add
                Attribute targetAttribute = join.getJoinAttributeForInput(targetInput);
                if (targetAttribute == null) {
                    for (Attribute attribute : attributes) {
                        newModel.addElement(attribute);
                    }
                } else {

                    for (Attribute attribute : attributes) {
                        // NOTE - we only add attribute that are compatible with the targetAttribute's type
                        if (attribute.isCompatibleWith(targetAttribute.getType())) {
                            newModel.addElement(attribute);
                        }
                    }
                }
                newModel.setSelectedItem(join.getJoinAttributeForInput(sourceInput));
            } else {

                newModel.setSelectedItem(null);
            }

            listExComboBox.setModel(newModel);
            listExComboBox.setPopupVolatile(true);

            return listExComboBox;
        }
    }
}
