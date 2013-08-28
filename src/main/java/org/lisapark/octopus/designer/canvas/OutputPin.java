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

import org.lisapark.octopus.core.Output;
import org.lisapark.octopus.core.source.Source;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class OutputPin extends Pin {
    private final Source source;

    public OutputPin(Source source) {

        this.source = source;
    }

    public Source getSource() {
        return source;
    }

    public Output getOutput() {

        return source.getOutput();
    }

    @Override
    public String getName() {
        return "output???";
    }
}
