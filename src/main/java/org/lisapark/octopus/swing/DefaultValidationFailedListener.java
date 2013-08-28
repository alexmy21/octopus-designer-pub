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
package org.lisapark.octopus.swing;

import com.jidesoft.validation.ValidationResult;

import javax.swing.*;
import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DefaultValidationFailedListener implements ValidationFailedListener {
    private final Component parentComponent;

    public DefaultValidationFailedListener(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    @Override
    public void validationFailed(ValidationResult result) {
        JOptionPane.showMessageDialog(parentComponent, result.getMessage(), "Octopus", JOptionPane.ERROR_MESSAGE);
    }
}
