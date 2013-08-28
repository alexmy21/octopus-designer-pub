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
package org.lisapark.octopus.designer.dnd;

import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.designer.canvas.ProcessingScene;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Implementation of an {@link AcceptProvider} that will handle the accepting of the following {@link Transferable}s:
 * <ul>
 * <li>ProcessorTransferable</li>
 * <li>ExternalSourceTransferable</li>
 * <li>ExternalSinkTransferable</li>
 * <ul>
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 * @see org.netbeans.api.visual.action.AcceptProvider
 * @see java.awt.datatransfer.Transferable
 */
public class NodeAcceptProvider implements AcceptProvider {

    private static final Logger LOG = LoggerFactory.getLogger(NodeAcceptProvider.class);

    /**
     * Scene we will be transferring the objects to
     */
    private final ProcessingScene scene;

    public NodeAcceptProvider(ProcessingScene scene) {
        checkArgument(scene != null, "scene cannot be null");
        this.scene = scene;
    }

    /**
     * Checks whether a transferable can be dropped on a widget at a specific point.
     *
     * @param widget       the widget could be dropped
     * @param point        the drop location in local coordination system of the widget
     * @param transferable the transferable
     * @return the state
     */
    @Override
    public ConnectorState isAcceptable(Widget widget, Point point, Transferable transferable) {
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        // see if we have an acceptable flavor we can handle
        DataFlavor acceptableFlavor = findAcceptableFlavor(flavors);

        return (acceptableFlavor != null) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
    }

    static DataFlavor findAcceptableFlavor(DataFlavor[] candidateFlavors) {
        DataFlavor acceptableFlavor = null;

        if (candidateFlavors != null) {
            for (DataFlavor candidateFlavor : candidateFlavors) {
                if (isAcceptableFlavor(candidateFlavor)) {
                    acceptableFlavor = candidateFlavor;
                }
            }
        }

        return acceptableFlavor;
    }

    static boolean isAcceptableFlavor(DataFlavor flavor) {
        return flavor == ProcessorTransferable.FLAVOR ||
                flavor == ExternalSinkTransferable.FLAVOR ||
                flavor == ExternalSourceTransferable.FLAVOR;
    }

    /**
     * Handles the drop of a transferable.
     *
     * @param widget       the widget where the transferable is dropped
     * @param point        the drop location in local coordination system of the widget
     * @param transferable the transferable
     */
    @Override
    public void accept(Widget widget, Point point, Transferable transferable) {
        try {
            DataFlavor[] flavors = transferable.getTransferDataFlavors();
            // see if we have an acceptable flavor we can handle
            DataFlavor acceptableFlavor = findAcceptableFlavor(flavors);

            if (acceptableFlavor != null) {
                if (acceptableFlavor == ProcessorTransferable.FLAVOR) {
                    Processor processor = (Processor) transferable.getTransferData(ProcessorTransferable.FLAVOR);
                    processor.setLocation(point);

                    LOG.debug("Dropping processor {} at location {}", processor, point);
                    scene.addProcessor(processor);

                } else if (acceptableFlavor == ExternalSourceTransferable.FLAVOR) {
                    ExternalSource externalSource = (ExternalSource) transferable.getTransferData(ExternalSourceTransferable.FLAVOR);
                    externalSource.setLocation(point);

                    LOG.debug("Dropping external source {} at location {}", externalSource, point);
                    scene.addExternalSource(externalSource);

                } else if (acceptableFlavor == ExternalSinkTransferable.FLAVOR) {
                    ExternalSink externalSink = (ExternalSink) transferable.getTransferData(ExternalSinkTransferable.FLAVOR);
                    externalSink.setLocation(point);

                    LOG.debug("Dropping external sink {} at location {}", externalSink, point);
                    scene.addExternalSink(externalSink);
                }
            } else {
                LOG.warn("Could not find acceptable flavor {}", flavors);
            }

        } catch (UnsupportedFlavorException e) {
            LOG.error("Problem transferring object", e);
        } catch (IOException e) {
            LOG.error("Problem transferring object", e);
        }
    }
}
