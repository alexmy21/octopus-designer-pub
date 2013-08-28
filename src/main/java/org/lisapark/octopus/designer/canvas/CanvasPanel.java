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
package org.lisapark.octopus.designer.canvas;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import org.lisapark.octopus.core.Node;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.swing.LayoutConstants;
import org.netbeans.api.visual.model.ObjectSceneEvent;
import org.netbeans.api.visual.model.ObjectSceneEventType;
import org.netbeans.api.visual.model.ObjectSceneListener;
import org.netbeans.api.visual.model.ObjectState;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class CanvasPanel extends JPanel {

    private ProcessingScene scene;
    private JComponent sceneView;
    private ObjectSceneListenerProxy listenerProxy = new ObjectSceneListenerProxy();

    public CanvasPanel() {
        super(new BorderLayout(LayoutConstants.COMPONENT_HORIZONTAL_GAP, LayoutConstants.COMPONENT_VERTICAL_GAP));
    }

    public void setNodeSelectionListener(NodeSelectionListener selectionListener) {
        listenerProxy.setNodeListener(selectionListener);

        if (scene != null) {
            scene.removeObjectSceneListener(listenerProxy, ObjectSceneEventType.values());

            scene.addObjectSceneListener(listenerProxy, ObjectSceneEventType.values());
        }

    }

    public void setProcessingModel(ProcessingModel model) {
        checkArgument(model != null, "model cannot be null");

        if (this.scene != null) {
            remove(sceneView);
            scene.removeObjectSceneListener(listenerProxy, ObjectSceneEventType.values());
        }

        scene = new ProcessingScene(model);
        sceneView = scene.createView();

        scene.addObjectSceneListener(listenerProxy, ObjectSceneEventType.values());

        add(sceneView);
    }

    private class ObjectSceneListenerProxy implements ObjectSceneListener {
        private NodeSelectionListener nodeSelectionListener;

        public void setNodeListener(NodeSelectionListener nodeSelectionListener) {
            this.nodeSelectionListener = nodeSelectionListener;
        }

        @Override
        public void objectAdded(ObjectSceneEvent event, Object addedObject) {
            // if a new node is added to the scene, we want to select it
            if (addedObject instanceof Node) {
                scene.setSelectedObjects(Sets.newHashSet(addedObject));
            }
        }

        @Override
        public void objectRemoved(ObjectSceneEvent event, Object removedObject) {

        }

        @Override
        public void objectStateChanged(ObjectSceneEvent event, Object changedObject, ObjectState previousState, ObjectState newState) {

        }

        @Override
        public void selectionChanged(ObjectSceneEvent event, Set<Object> previousSelection, Set<Object> newSelection) {
            if (nodeSelectionListener != null) {
                Node selectedNode = null;

                if (newSelection != null && newSelection.size() > 0) {
                    // if multiple objects are selected, we can only allow it when they are coming from
                    // the same node
                    for (Object selectedObject : newSelection) {
                        Node currentNode = null;
                        // we want to get the parent node of the ping
                        if (selectedObject instanceof Pin) {
                            currentNode = scene.getPinNode((Pin) selectedObject);

                        } else if (selectedObject instanceof Node) {
                            currentNode = (Node) selectedObject;
                        }

                        if (selectedNode == null) {
                            selectedNode = currentNode;

                        } else if (Objects.equal(selectedNode, currentNode)) {
                            selectedNode = currentNode;
                        } else {
                            // from multiple nodes, clear selection
                            selectedNode = null;
                            break;
                        }
                    }
                    sceneView.requestFocusInWindow();
                }

                nodeSelectionListener.selectedChanged(selectedNode);
            }
        }

        @Override
        public void highlightingChanged(ObjectSceneEvent event, Set<Object> previousHighlighting, Set<Object> newHighlighting) {

        }

        @Override
        public void hoverChanged(ObjectSceneEvent event, Object previousHoveredObject, Object newHoveredObject) {

        }

        @Override
        public void focusChanged(ObjectSceneEvent event, Object previousFocusedObject, Object newFocusedObject) {
        }
    }
}
