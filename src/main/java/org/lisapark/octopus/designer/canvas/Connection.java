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

import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.source.Source;

/**
 * A connection is used in the {@link org.lisapark.octopus.designer.canvas.ProcessingScene} to connect
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Connection {

    private Input destination;

    private Connection(Source source, Input destination) {
        this.destination = destination;
        destination.connectSource(source);
    }

    public void clearSource() {
        destination.clearSource();
    }

    public static Connection connectSourceToSinkInput(Source source, Input input) {
        return new Connection(source, input);
    }
}
