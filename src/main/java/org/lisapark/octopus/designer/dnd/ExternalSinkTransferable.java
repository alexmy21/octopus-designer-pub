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

import org.lisapark.octopus.core.sink.external.ExternalSink;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This {@link Transferable} is used to move {@link ExternalSink}s between components for drag and drop
 * operations.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 * @see java.awt.datatransfer.Transferable
 */
public class ExternalSinkTransferable implements Transferable {
    public static final DataFlavor FLAVOR = new DataFlavor(ExternalSink.class, "External Sink");

    /**
     * Sink we are transferring
     */
    private final ExternalSink externalSink;

    public ExternalSinkTransferable(ExternalSink externalSink) {
        checkArgument(externalSink != null, "externalSink cannot be null");
        this.externalSink = externalSink;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        Object data = null;

        if (flavor.equals(FLAVOR)) {
            data = externalSink.newInstance();
        }
        return data;
    }
}
