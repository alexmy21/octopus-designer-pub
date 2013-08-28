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
package org.lisapark.octopus;

/**
 * This is a unique exception in Octopus, it is thrown only when the exception is the cause of a programmer error. Any
 * time this happens means there is a whole in the programmer's logic and needs to be corrected.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ProgrammerException extends RuntimeException {

    public ProgrammerException(Throwable cause) {
        super(cause);
    }

    public ProgrammerException(String message) {
        super(message);
    }

    public ProgrammerException(String message, Throwable cause) {
        super(message, cause);
    }
}
