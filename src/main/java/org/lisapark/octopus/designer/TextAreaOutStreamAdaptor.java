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

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class TextAreaOutStreamAdaptor extends OutputStream {
    private JTextArea textArea;

    public TextAreaOutStreamAdaptor(JTextArea textArea) {
        checkArgument(textArea != null, "textArea cannot be null");
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        String str = new String(new byte[b], 0, 1);

        // append is thread safe and doesn't need to be in a invokeLater
        textArea.append(str);
        updateCaretPosition();
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        this.write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        String str = new String(bytes, off, len);

        // append is thread safe and doesn't need to be in a invokeLater
        textArea.append(str);
        updateCaretPosition();
    }

    @Override
    public void flush() throws IOException {
        // append is thread safe and doesn't need to be in a invokeLater
        textArea.append("\n");
    }

    @Override
    public void close() throws IOException {

    }

    private void updateCaretPosition() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        });
    }
}
