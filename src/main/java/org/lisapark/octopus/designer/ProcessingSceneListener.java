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

import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.designer.properties.PropertiesPanel;
import org.netbeans.api.visual.model.ObjectSceneEvent;
import org.netbeans.api.visual.model.ObjectSceneListener;
import org.netbeans.api.visual.model.ObjectState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ProcessingSceneListener implements ObjectSceneListener {

    private final PropertiesPanel propertiesPanel;

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingSceneListener.class);

    public ProcessingSceneListener(PropertiesPanel propertiesPanel) {
        this.propertiesPanel = propertiesPanel;
    }

    @Override
    public void objectAdded(ObjectSceneEvent event, Object addedObject) {
        LOG.debug("objectAdded to scene {}", addedObject);
    }

    @Override
    public void objectRemoved(ObjectSceneEvent event, Object removedObject) {
        LOG.debug("objectRemoved from scene {}", removedObject);
    }

    @Override
    public void objectStateChanged(ObjectSceneEvent event, Object changedObject, ObjectState previousState, ObjectState newState) {
        LOG.debug(String.format("objectStateChanged on scene %s %s -> %s", changedObject, previousState, newState));
    }

    @Override
    public void selectionChanged(ObjectSceneEvent event, Set<Object> previousSelection, Set<Object> newSelection) {
        LOG.debug("selectionChanged on scene {}", newSelection);

        if (newSelection != null && newSelection.size() == 1) {
            Object selectedObject = newSelection.iterator().next();

            if (selectedObject instanceof Processor) {
                propertiesPanel.setSelectedProcessor((Processor) selectedObject);

            } else if (selectedObject instanceof ExternalSource) {
                propertiesPanel.setSelectedExternalSource((ExternalSource) selectedObject);

            } else if (selectedObject instanceof ExternalSink) {
                propertiesPanel.setSelectedExternalSink((ExternalSink) selectedObject);
            }
        } else {
            propertiesPanel.clearSelection();
        }
    }

    @Override
    public void highlightingChanged(ObjectSceneEvent event, Set<Object> previousHighlighting, Set<Object> newHighlighting) {
        LOG.debug("highlightingChanged on scene {}", newHighlighting);
    }

    @Override
    public void hoverChanged(ObjectSceneEvent event, Object previousHoveredObject, Object newHoveredObject) {
        LOG.debug("hoverChanged on scene {}", newHoveredObject);
    }

    @Override
    public void focusChanged(ObjectSceneEvent event, Object previousFocusedObject, Object newFocusedObject) {
        LOG.debug("focusChanged on scene {}", newFocusedObject);
    }
}
