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
package org.lisapark.octopus.designer;

import com.jidesoft.dialog.BannerPanel;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.StandardDialog;
import com.jidesoft.plaf.UIDefaultsLookup;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.repository.OctopusRepository;
import org.lisapark.octopus.repository.RepositoryException;
import org.lisapark.octopus.swing.ComponentFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class SaveModelDialog extends StandardDialog {

    private final OctopusRepository repository;
    private ProcessingModel modelToSave;
    private JButton okButton;
    private JTextField modelNameTxt;

    private SaveModelDialog(JFrame frame, OctopusRepository repository, ProcessingModel modelToSave) {
        super(frame, "Octopus");
        setResizable(false);
        this.repository = repository;
        this.modelToSave = modelToSave;
    }

    @Override
    public JComponent createBannerPanel() {
        BannerPanel bannerPanel = new BannerPanel("Save model",
                "Enter model name.",
                DesignerIconsFactory.getImageIcon(DesignerIconsFactory.OCTOPUS_LARGE));
        bannerPanel.setBackground(Color.WHITE);
        bannerPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        return bannerPanel;
    }

    @Override
    public JComponent createContentPanel() {
        JLabel modelNameLbl = ComponentFactory.createLabelWithTextAndMnemonic("Model name: ", KeyEvent.VK_N);
        modelNameLbl.setHorizontalAlignment(SwingConstants.CENTER);

        modelNameTxt = ComponentFactory.createTextField();
        modelNameTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                okButton.setEnabled(modelNameTxt.getText().trim().length() > 0);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                okButton.setEnabled(modelNameTxt.getText().trim().length() > 0);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                okButton.setEnabled(modelNameTxt.getText().trim().length() > 0);
            }
        });
        modelNameTxt.setText(modelToSave.getModelName());

        modelNameLbl.setLabelFor(modelNameTxt);

        // note that this padding numbers are Jide recommendations
        JPanel topPanel = ComponentFactory.createPanelWithLayout(new BorderLayout(6, 6));
        topPanel.add(modelNameLbl, BorderLayout.BEFORE_LINE_BEGINS);
        topPanel.add(modelNameTxt, BorderLayout.CENTER);

        // note that this padding numbers are Jide recommendations
        JPanel contentPanel = ComponentFactory.createPanelWithLayout(new BorderLayout(10, 10));
        // note that this padding numbers are Jide recommendations
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        contentPanel.add(topPanel, BorderLayout.BEFORE_FIRST_LINE);
        contentPanel.setPreferredSize(new Dimension(400, 100));

        // we want the model name text field to have focus first when the dialog opens
        setInitFocusedComponent(modelNameTxt);

        return contentPanel;
    }

    @Override
    public ButtonPanel createButtonPanel() {
        ButtonPanel buttonPanel = new ButtonPanel();
        // note that these padding numbers coincide with what Jide recommends for StandardDialog button panels
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        okButton = ComponentFactory.createButton();
        okButton.setName(OK);
        okButton.setAction(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });
        // we need to disable the button AFTER setting the action
        okButton.setEnabled(false);

        JButton cancelButton = ComponentFactory.createButton();
        cancelButton.setName(CANCEL);
        cancelButton.setAction(new AbstractAction(UIDefaultsLookup.getString("OptionPane.cancelButtonText")) {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        buttonPanel.addButton(okButton, ButtonPanel.AFFIRMATIVE_BUTTON);
        buttonPanel.addButton(cancelButton, ButtonPanel.CANCEL_BUTTON);

        setDefaultCancelAction(cancelButton.getAction());
        setDefaultAction(okButton.getAction());
        getRootPane().setDefaultButton(okButton);

        return buttonPanel;
    }

    /**
     * This methods is called when the user presses the save button. It will try and store the model to the
     * {@link #repository}
     */
    private void onSave() {
        modelToSave.setModelName(modelNameTxt.getText());

        try {
            repository.saveProcessingModel(modelToSave);
            setDialogResult(RESULT_AFFIRMED);
            setVisible(false);
            dispose();
        } catch (RepositoryException e) {
            ErrorDialog.showErrorDialog(this, e, "Problem saving model");
        }
    }

    /**
     * This method is called when the user presses the cancel button. This will dismiss the dialog
     */
    private void onCancel() {
        setDialogResult(RESULT_CANCELLED);
        setVisible(false);
        dispose();
    }

    /**
     * This method will create and display a new {@link org.lisapark.octopus.designer.SaveModelDialog} for saving
     * a {@link org.lisapark.octopus.core.ProcessingModel}.
     *
     * @param parent      to center dialog over
     * @param modelToSave that user is saving
     * @param repository  used for saving models
     */
    public static void saveProcessingModel(Component parent, ProcessingModel modelToSave, OctopusRepository repository) {
        JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, parent);
        SaveModelDialog dialog = new SaveModelDialog(frame, repository, modelToSave);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
}

