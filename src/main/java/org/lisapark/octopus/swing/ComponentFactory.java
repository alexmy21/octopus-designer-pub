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
package org.lisapark.octopus.swing;

import com.jidesoft.action.CommandBar;
import com.jidesoft.action.CommandMenuBar;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.grid.ContextSensitiveTableModel;
import com.jidesoft.grid.PropertyPane;
import com.jidesoft.grid.PropertyTable;
import com.jidesoft.status.LabelStatusBarItem;
import com.jidesoft.status.MemoryStatusBarItem;
import com.jidesoft.status.StatusBar;
import com.jidesoft.status.TimeStatusBarItem;
import com.jidesoft.swing.JideMenu;
import com.jidesoft.swing.JideScrollPane;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.swing.MultilineLabel;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Factory object that can vend common ui widgets
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class ComponentFactory {

    /**
     * Highlight listener for text components. This can be shared across text fields
     * since swing is not multi-thread safe
     */
    private static HightlightFocusListener hlfl = new HightlightFocusListener();

    public static JPanel createPanel() {
        return new JPanel();
    }

    public static JPanel createPanelWithLayout(LayoutManager layoutManager) {
        return new JPanel(layoutManager);
    }

    /**
     * Returns a <code>JFormattedTextField</code>
     *
     * @return JFormattedTextField
     */
    public static BaseFormattedTextField createFormattedTextField() {

        BaseFormattedTextField text = new BaseFormattedTextField();
        // add highlight listener
        text.addFocusListener(hlfl);

        return text;
    }

    public static JTextField createTextField() {
        JTextField text = new JTextField();
        // add highlight listener
        text.addFocusListener(hlfl);
        return text;
    }

    public static BaseButton createButton() {
        return new BaseButton();
    }

    public static BaseButton createButtonWithText(String text) {
        return new BaseButton(text);
    }

    public static BaseButton createButtonWithAction(Action action) {
        return new BaseButton(action);
    }

    public static BaseStyledButton createStyledButton() {
        return new BaseStyledButton();
    }

    public static BaseStyledButton createStyledButtonWithAction(Action action) {
        return new BaseStyledButton(action);
    }

    public static BaseStyledButton createToolbarButtonWithAction(Action action) {
        BaseStyledButton btn = new BaseStyledButton(action);
        btn.setFocusable(false);
        return btn;
    }

    public static MultilineLabel createMultilineLabelWithText(String text) {
        return new MultilineLabel(text);
    }

    public static JLabel createLabelWithTextAndMnemonic(String text, int mnemonic) {
        JLabel label = new JLabel(text);
        label.setDisplayedMnemonic(mnemonic);

        return label;
    }

    public static JLabel createLabelWithIcon(Icon icon) {
        return new JLabel(icon);
    }

    public static JList createListWithModel(ListModel model) {
        return new JList(model);
    }

    public static JideScrollPane createScrollPane() {
        return new JideScrollPane();
    }

    public static JideScrollPane createScrollPaneWithComponent(Component component) {
        return new JideScrollPane(component);
    }

    public static JideTabbedPane createTabbedPaneWithTabPlacement(int tabPlacement) {
        return new JideTabbedPane(tabPlacement);
    }

    public static BaseTable createTableWithModel(ContextSensitiveTableModel model) {
        return new BaseTable(model);
    }

    public static StatusBar createStatusBar() {
        return new StatusBar();
    }

    public static TimeStatusBarItem createTimeStatusBarItem() {
        return new TimeStatusBarItem();
    }

    public static MemoryStatusBarItem createMemoryStatusBarItem() {
        return new MemoryStatusBarItem();
    }

    public static LabelStatusBarItem createLabelStatusBarItem() {
        return new LabelStatusBarItem();
    }

    public static PropertyPane createPropertyPaneWithTable(PropertyTable table) {
        return new BasePropertyPane(table);
    }

    /**
     * Creates and returns a new {@link DockableFrame} with all relevant names set to the specified name.
     *
     * @param name of title
     * @return new frame
     */
    public static DockableFrame createDockableFrameWithName(String name) {
        DockableFrame dockableFrame = new DockableFrame(name);
        dockableFrame.setTitle(name);
        dockableFrame.setSideTitle(name);
        dockableFrame.setTabTitle(name);
        return dockableFrame;
    }

    public static CommandBar createToolBarWithName(String name) {
        CommandBar toolBar = new CommandBar(name);
        toolBar.setStretch(true);
        toolBar.setPaintBackground(false);
        return toolBar;
    }

    public static CommandMenuBar createMenuBar() {
        CommandMenuBar menuBar = new CommandMenuBar();
        menuBar.setStretch(true);
        menuBar.setPaintBackground(false);

        return menuBar;
    }

    public static CommandMenuBar createMenuBarWithName(String name) {
        CommandMenuBar menuBar = new CommandMenuBar(name);
        menuBar.setStretch(true);
        menuBar.setPaintBackground(false);

        return menuBar;
    }

    public static JMenu createMenuWithNameAndMnemonic(String name, int mnemonic) {
        JideMenu menu = new JideMenu(name);
        menu.setMnemonic(mnemonic);
        return menu;
    }

    public static JMenuItem createMenuItemWithAction(Action action) {
        return new JMenuItem(action);
    }

    public static JMenuItem createMenuItemWithNameAndMnemonic(String name, int mnemonic) {

        return new JMenuItem(name, mnemonic);
    }

    public static JMenuItem createMenuItemWithNameMnemonicAndAccelerator(String name, int mnemonic, KeyStroke accelerator) {
        JMenuItem menuItem = new JMenuItem(name, mnemonic);
        menuItem.setAccelerator(accelerator);

        return menuItem;
    }

    /**
     * Focus Listener for selecting the value of the given text field
     * when it gains focus.
     */
    private static class HightlightFocusListener implements FocusListener {
        /**
         * Constructor
         */
        HightlightFocusListener() {

        }

        /**
         * Invoked when a component gains the keyboard focus.
         */
        public void focusGained(FocusEvent e) {
            Component source = (Component) e.getSource();

            if (source instanceof JFormattedTextField) {
                final JTextComponent textComponent = (JTextComponent) source;

                // dcoumented bug in 1.5 that can be gotten around by placing event on queue
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String text = textComponent.getText();

                        // select the text
                        textComponent.select(0, text.length());
                    }
                });

            } else if (source instanceof JTextComponent) {
                JTextComponent textComponent = (JTextComponent) source;

                String text = textComponent.getText();

                // select the text
                textComponent.select(0, text.length());
            }
        }

        /**
         * Invoked when a component loses the keyboard focus.
         */
        public void focusLost(FocusEvent e) {
            // noop
        }
    }
}
