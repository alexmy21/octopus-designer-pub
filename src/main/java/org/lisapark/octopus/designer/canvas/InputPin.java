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
import org.lisapark.octopus.core.sink.Sink;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class InputPin extends Pin {
    private final Sink sink;
    private final Input input;

    public InputPin(Sink sink, Input input) {

        this.sink = sink;
        this.input = input;
    }

    public boolean isConnected() {
        return input.isConnected();
    }

    public Input getInput() {
        return input;
    }

    public Sink getSink() {
        return sink;
    }

    @Override
    public String getName() {
        return input.getName();
    }
}
