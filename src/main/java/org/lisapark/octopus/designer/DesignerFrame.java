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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jidesoft.action.CommandBar;
import com.jidesoft.action.DefaultDockableBarDockableHolder;
import com.jidesoft.action.DockableBarManager;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.DockingManager;
import com.jidesoft.status.LabelStatusBarItem;
import com.jidesoft.status.MemoryStatusBarItem;
import com.jidesoft.status.StatusBar;
import com.jidesoft.status.TimeStatusBarItem;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JidePopupMenu;
import com.jidesoft.swing.JideScrollPane;
import org.lisapark.octopus.core.Node;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.core.ValidationException;
import org.lisapark.octopus.core.compiler.esper.EsperCompiler;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.runtime.ProcessingRuntime;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.designer.canvas.CanvasPanel;
import org.lisapark.octopus.designer.canvas.NodeSelectionListener;
import org.lisapark.octopus.designer.palette.PalettePanel;
import org.lisapark.octopus.designer.properties.PropertiesPanel;
import org.lisapark.octopus.repository.OctopusRepository;
import org.lisapark.octopus.repository.RepositoryException;
import org.lisapark.octopus.swing.BaseStyledButton;
import org.lisapark.octopus.swing.ComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.util.List;

/**
 * This is the main {@link JFrame} for the Octopus Designer application.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DesignerFrame extends DefaultDockableBarDockableHolder {

    private static final Logger LOG = LoggerFactory.getLogger(DesignerFrame.class);
    /**
     * This is the profile used for Jide's {@link DockingManager}. It allows it
     * to store layout information that will be persisted from one session of
     * the application to the next
     */
    private static final String PROFILE_KEY = "octopus";
    private static final String MENU_BAR_KEY = "Menu";
    private static final String TOOL_BAR_KEY = "Tools";
    /**
     * These are the keys associated with the different {@link DockableFrame}s.
     * They are used by the {@link DockingManager} to show a frame
     */
    private static final String PROPERTIES_KEY = "Properties";
    private static final String OUTPUT_KEY = "Output";
    private static final String PALETTE_KEY = "Palette";
    /**
     * This is the current {@link ProcessingModel} we are working on
     */
    private ProcessingModel currentProcessingModel;
    /**
     * The repository is used for retrieving and saving different Octopus
     * artifacts
     */
    private final OctopusRepository repository;
    /**
     * These are the main views for the designer application
     */
    private CanvasPanel canvasPanel;
    private PropertiesPanel propertiesPanel;
    private PalettePanel palettePanel;
    /**
     * These are the menu and tool bar actions
     */
    private OpenAction openAction = new OpenAction(
            "Open...", DesignerIconsFactory.getImageIcon(DesignerIconsFactory.OPEN), "Open existing model", KeyEvent.VK_O);
    private NewAction newAction = new NewAction(
            "Create New", DesignerIconsFactory.getImageIcon(DesignerIconsFactory.NEW), "Create new model", KeyEvent.VK_N);
    private SaveAction saveAction = new SaveAction(
            "Save", DesignerIconsFactory.getImageIcon(DesignerIconsFactory.SAVE), "Save model", KeyEvent.VK_S);
    private ExitAction exitAction = new ExitAction(
            "Exit", "Exit from designer", KeyEvent.VK_X);
    private CompileAction compileAction = new CompileAction(
            "Compile", DesignerIconsFactory.getImageIcon(DesignerIconsFactory.COMPILE), "Compile model");
    private RunAction runAction = new RunAction(
            "Run", DesignerIconsFactory.getImageIcon(DesignerIconsFactory.RUN), "Run model");
    private ClearOutputAction clearOutputAction = new ClearOutputAction("Clear");
    private CopyAllAction copyAllAction = new CopyAllAction("Copy All");
    /**
     * This status bar label will contain the name of the
     * {@link #currentProcessingModel}
     */
    private LabelStatusBarItem modelNameStatusItem;
    private JTextArea outputTxt;

    public DesignerFrame(OctopusRepository repository) {
        super("Octopus");
        this.repository = repository;

        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initializeDockableBarManager();
        initializeDockingManager();
        initializeStatusBar();

        addWindowListener(new WindowClosingListener());
    }

    /**
     * Initializes the {@link DockableBarManager} by creating and adding the
     * {@link #createMenuBar()}
     */
    private void initializeDockableBarManager() {
        DockableBarManager dockableBarManager = getDockableBarManager();
        dockableBarManager.setShowInitial(false);
        // loadLayoutData() will make the main JFrame visible, so we disable this feature with the next call
        dockableBarManager.addDockableBar(createMenuBar());
        dockableBarManager.addDockableBar(createToolBar());
        dockableBarManager.loadLayoutData();
    }

    /**
     * Initializes and adds all of the {@link DockableFrame}s to the
     * {@link DockingManager}
     */
    private void initializeDockingManager() {
        DockingManager dockingManager = getDockingManager();
        // loadLayoutData() will make the main JFrame visible, so we disable this feature with the next call
        dockingManager.setShowInitial(false);
        dockingManager.setProfileKey(PROFILE_KEY);

        dockingManager.getWorkspace().add(createCanvas());
        dockingManager.beginLoadLayoutData();
        dockingManager.setInitSplitPriority(DockingManager.SPLIT_EAST_WEST_SOUTH_NORTH);

        dockingManager.addFrame(createProperties());
        dockingManager.addFrame(createOutput());
        dockingManager.addFrame(createPalette());

        // load layout information from previous session. This indicates the end of beginLoadLayoutData() method above.
        dockingManager.loadLayoutData();
    }

    /**
     * Initializes and adds the {@link StatusBar}
     */
    private void initializeStatusBar() {
        StatusBar statusBar = ComponentFactory.createStatusBar();

        TimeStatusBarItem time = ComponentFactory.createTimeStatusBarItem();
        MemoryStatusBarItem gc = ComponentFactory.createMemoryStatusBarItem();
        modelNameStatusItem = ComponentFactory.createLabelStatusBarItem();

        statusBar.add(time, JideBoxLayout.FLEXIBLE);
        statusBar.add(modelNameStatusItem, JideBoxLayout.VARY);
        statusBar.add(gc, JideBoxLayout.FIX);

        getContentPane().add(statusBar, BorderLayout.AFTER_LAST_LINE);
    }

    private JComponent createCanvas() {
        this.canvasPanel = new CanvasPanel();
        return createScrollPaneForComponent(canvasPanel);
    }

    private DockableFrame createPalette() {
        this.palettePanel = new PalettePanel();

        DockableFrame propertiesFrame = ComponentFactory.createDockableFrameWithName(PALETTE_KEY);
        propertiesFrame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
        propertiesFrame.getContext().setInitSide(DockContext.DOCK_SIDE_WEST);
        propertiesFrame.getContext().setInitIndex(1);
        propertiesFrame.add(createScrollPaneForComponent(palettePanel));

        return propertiesFrame;
    }

    private DockableFrame createProperties() {
        this.propertiesPanel = new PropertiesPanel();

        NodeSelectionListener nodeSelectionListener = new NodeSelectionListener() {
            @Override
            public void selectedChanged(Node node) {
                if (node instanceof Processor) {
                    propertiesPanel.setSelectedProcessor((Processor) node);

                } else if (node instanceof ExternalSource) {
                    propertiesPanel.setSelectedExternalSource((ExternalSource) node);

                } else if (node instanceof ExternalSink) {
                    propertiesPanel.setSelectedExternalSink((ExternalSink) node);
                } else {
                    propertiesPanel.clearSelection();
                }
            }
        };
        canvasPanel.setNodeSelectionListener(nodeSelectionListener);

        DockableFrame propertiesFrame = ComponentFactory.createDockableFrameWithName(PROPERTIES_KEY);
        propertiesFrame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
        propertiesFrame.getContext().setInitSide(DockContext.DOCK_SIDE_WEST);
        propertiesFrame.getContext().setInitIndex(0);
        propertiesFrame.add(createScrollPaneForComponent(propertiesPanel));

        return propertiesFrame;
    }

    private DockableFrame createOutput() {
        // todo real output and can we hook it up with some meaningful messages?
        DockableFrame logFrame = ComponentFactory.createDockableFrameWithName(OUTPUT_KEY);
        logFrame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
        logFrame.getContext().setInitSide(DockContext.DOCK_SIDE_SOUTH);

        outputTxt = new JTextArea();
        outputTxt.setEditable(false);

        final JidePopupMenu popupMenu = new JidePopupMenu();
        popupMenu.add(clearOutputAction);
        popupMenu.add(copyAllAction);

        outputTxt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(outputTxt, e.getX(), e.getY());
                }
            }
        });

        logFrame.add(createScrollPaneForComponent(outputTxt));

        return logFrame;
    }

    private JScrollPane createScrollPaneForComponent(JComponent component) {
        JScrollPane pane = ComponentFactory.createScrollPaneWithComponent(component);
        pane.setVerticalScrollBarPolicy(JideScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        return pane;
    }

    private CommandBar createMenuBar() {
        CommandBar menuBar = ComponentFactory.createMenuBarWithName(MENU_BAR_KEY);
        menuBar.setInitIndex(0);

        JMenu fileMenu = createFileMenu();
        JMenu viewMenu = createViewMenu();

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        // todo 
//        menuBar.add(windowMenu);
//        menuBar.add(helpMenu);

        return menuBar;
    }

    private CommandBar createToolBar() {
        CommandBar toolBar = ComponentFactory.createToolBarWithName(TOOL_BAR_KEY);
        toolBar.setInitIndex(1);

        BaseStyledButton newBtn = ComponentFactory.createToolbarButtonWithAction(newAction);
        // need to null out the text since they are gotten from the action
        newBtn.setText(null);
        toolBar.add(newBtn);

        BaseStyledButton openBtn = ComponentFactory.createToolbarButtonWithAction(openAction);
        openBtn.setText(null);
        toolBar.add(openBtn);

        BaseStyledButton saveBtn = ComponentFactory.createToolbarButtonWithAction(saveAction);
        saveBtn.setText(null);
        toolBar.add(saveBtn);

        toolBar.addSeparator();
        BaseStyledButton compileBtn = ComponentFactory.createToolbarButtonWithAction(compileAction);
        compileBtn.setText(null);
        toolBar.add(compileBtn);

        BaseStyledButton runBtn = ComponentFactory.createToolbarButtonWithAction(runAction);
        runBtn.setText(null);
        toolBar.add(runBtn);

        return toolBar;
    }

    private JMenu createFileMenu() {
        JMenuItem newMi = ComponentFactory.createMenuItemWithAction(newAction);
        newMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));

        JMenuItem openMi = ComponentFactory.createMenuItemWithAction(openAction);
        openMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));

        JMenuItem saveMi = ComponentFactory.createMenuItemWithAction(saveAction);
        saveMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));

        JMenuItem saveAsMi = ComponentFactory.createMenuItemWithNameMnemonicAndAccelerator("Save As", KeyEvent.VK_A, KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));

        JMenuItem exitMi = ComponentFactory.createMenuItemWithAction(exitAction);

        JMenu fileMnu = ComponentFactory.createMenuWithNameAndMnemonic("File", KeyEvent.VK_F);
        fileMnu.add(newMi);
        fileMnu.add(openMi);
        fileMnu.addSeparator();
        fileMnu.add(saveMi);

        // todo save as
        //fileMnu.add(saveAsMi);
        fileMnu.addSeparator();
        fileMnu.add(exitMi);

        return fileMnu;
    }

    private JMenu createViewMenu() {
        JMenuItem nextViewMi = ComponentFactory.createMenuItemWithNameMnemonicAndAccelerator("Slect next window", KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
        nextViewMi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String frameKey = getDockingManager().getNextFrame(getDockingManager().getActiveFrameKey());
                if (frameKey != null) {
                    getDockingManager().showFrame(frameKey);
                }
            }
        });

        JMenuItem previousMi = ComponentFactory.createMenuItemWithNameMnemonicAndAccelerator("Slect previous window", KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_F6, InputEvent.SHIFT_MASK));
        previousMi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String frameKey = getDockingManager().getPreviousFrame(getDockingManager().getActiveFrameKey());
                if (frameKey != null) {
                    getDockingManager().showFrame(frameKey);
                }
            }
        });

        JMenuItem paletteMi = ComponentFactory.createMenuItemWithNameMnemonicAndAccelerator(PALETTE_KEY, KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
        paletteMi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getDockingManager().showFrame(PALETTE_KEY);
            }
        });

        JMenuItem propertiesMi = ComponentFactory.createMenuItemWithNameMnemonicAndAccelerator(PROPERTIES_KEY, KeyEvent.VK_W, KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
        propertiesMi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getDockingManager().showFrame(PROPERTIES_KEY);
            }
        });

        JMenuItem outputMi = ComponentFactory.createMenuItemWithNameMnemonicAndAccelerator(OUTPUT_KEY, KeyEvent.VK_U, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
        outputMi.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getDockingManager().showFrame(OUTPUT_KEY);
            }
        });

        JMenu viewMenu = ComponentFactory.createMenuWithNameAndMnemonic("View", KeyEvent.VK_V);
        viewMenu.add(nextViewMi);
        viewMenu.add(previousMi);
        viewMenu.addSeparator();
        viewMenu.add(paletteMi);
        viewMenu.add(propertiesMi);
        viewMenu.addSeparator();
        viewMenu.add(outputMi);

        return viewMenu;
    }

    /**
     * This method will load all the initial data from the {@link #repository}.
     * This includes all the template {@link Processor}s,
     * {@link ExternalSource}s and {@link ExternalSink}s. The method will then
     * give this data to the appropriate views.
     */
    void loadInitialDataFromRepository() throws RepositoryException {
        List<ExternalSink> sinkTemplates = repository.getAllExternalSinkTemplates();
        palettePanel.setExternalSinks(sinkTemplates);

        List<ExternalSource> sourceTemplates = repository.getAllExternalSourceTemplates();
        palettePanel.setExternalSources(sourceTemplates);

        List<Processor> processorTemplates = repository.getAllProcessorTemplates();
        palettePanel.setProcessors(processorTemplates);
    }

    private void setCurrentProcessingModel(ProcessingModel currentProcessingModel) {
        this.currentProcessingModel = currentProcessingModel;
        modelNameStatusItem.setText(currentProcessingModel.getModelName());

        canvasPanel.setProcessingModel(currentProcessingModel);
        propertiesPanel.setProcessingModel(currentProcessingModel);
    }

    /**
     * Shuts down the application by saving the layout state and dispose the
     * frame
     */
    private void shutdown() {
        DockingManager dockingManager = getDockingManager();

        if (dockingManager != null) {
            LOG.debug("Saving docking layout");
            dockingManager.saveLayoutData();
        }

        DockableBarManager dockableBarManager = getDockableBarManager();

        if (dockableBarManager != null) {
            LOG.debug("Saving docking bar layout");
            dockableBarManager.saveLayoutData();
        }

        setVisible(false);
        dispose();
    }

    private class OpenAction extends AbstractAction {

        private OpenAction(String text, ImageIcon icon, String description, int mnemonic) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, description);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ProcessingModel modelToOpen = OpenModelDialog.openProcessingModel(DesignerFrame.this, repository);

            if (modelToOpen != null) {
                LOG.debug("Opening processing model '{}'", modelToOpen.getModelName());

                setCurrentProcessingModel(modelToOpen);
            }
        }
    }

    private class NewAction extends AbstractAction {

        private NewAction(String text, ImageIcon icon, String description, int mnemonic) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, description);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // todo better way to come up with initial name
            setCurrentProcessingModel(new ProcessingModel("model"));
        }
    }

    private class SaveAction extends AbstractAction {

        private SaveAction(String text, ImageIcon icon, String description, int mnemonic) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, description);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentProcessingModel != null) {
                SaveModelDialog.saveProcessingModel(DesignerFrame.this, currentProcessingModel, repository);

                // update the status bar with the updated name
                modelNameStatusItem.setText(currentProcessingModel.getModelName());
            }
        }
    }

    private class ExitAction extends AbstractAction {

        private ExitAction(String text, String description, int mnemonic) {
            super(text);
            putValue(SHORT_DESCRIPTION, description);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            shutdown();
        }
    }

    private class CompileAction extends AbstractAction {

        public CompileAction(String text, ImageIcon icon, String description) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, description);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentProcessingModel != null) {
                org.lisapark.octopus.core.compiler.Compiler compiler = new EsperCompiler();
                try {
                    compiler.compile(currentProcessingModel);

                    outputTxt.append(currentProcessingModel.getModelName() + " compiled successfully.\n");
                    
                    String modelJson        = currentProcessingModel.toJson();                    
                    JsonParser parser       = new JsonParser();
                    JsonElement jsonElement = parser.parse(modelJson);

                    Gson gson   = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(jsonElement);
                    
                    outputTxt.append("Model JSON:\n" + json);

                } catch (ValidationException ex) {
                    outputTxt.append(ex.getLocalizedMessage() + "\n");
                }
            }
        }
    }

    private class RunAction extends AbstractAction {

        private RunAction(String text, ImageIcon icon, String description) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, description);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentProcessingModel != null) {
                TextAreaOutStreamAdaptor writer = new TextAreaOutStreamAdaptor(outputTxt);

                org.lisapark.octopus.core.compiler.Compiler compiler = new EsperCompiler();
                PrintStream stream = new PrintStream(writer);
                compiler.setStandardOut(stream);
                compiler.setStandardError(stream);
                try {
                    outputTxt.append("Running model '" + currentProcessingModel.getModelName() + "'. Please wait...\n");
                    ProcessingRuntime runtime = compiler.compile(currentProcessingModel);
                    // todo do we need a stop button and lock out ui?
                    runtime.start();
                    runtime.shutdown();

                    outputTxt.append("Model '" + currentProcessingModel.getModelName() + "' completed running.\n");
                } catch (ValidationException e1) {
                    outputTxt.append(e1.getLocalizedMessage() + "\n");
                }
            }
        }
    }

    private class ClearOutputAction extends AbstractAction {

        private ClearOutputAction(String text) {
            super(text, null);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            outputTxt.setText(null);
        }
    }

    private class CopyAllAction extends AbstractAction {

        private CopyAllAction(String text) {
            super(text, null);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Document document = outputTxt.getDocument();
            try {
                String srcData = document.getText(0, document.getLength());
                StringSelection contents = new StringSelection(srcData);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(contents, null);
            } catch (BadLocationException ex) {
                // should never happen
            }
        }
    }

    /**
     * This listener will respond to the user closing the frame. It will save
     * the layout data for the
     * {@link com.jidesoft.docking.DockingManager}, {@link DockableBarManager}
     * and dispose of the frame.
     */
    private class WindowClosingListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            shutdown();
        }

        @Override
        public void windowOpened(WindowEvent e) {
            DockingManager dockingManager = getDockingManager();

            if (dockingManager != null) {
                dockingManager.showInitial();
            }

            DockableBarManager dockableBarManager = getDockableBarManager();

            if (dockableBarManager != null) {
                dockableBarManager.showInitial();
            }
        }
    }
}
