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

import com.jidesoft.plaf.LookAndFeelFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.*;
import org.apache.commons.io.IOUtils;
import org.lisapark.octopus.repository.OctopusRepository;
import org.lisapark.octopus.repository.RepositoryException;
import org.lisapark.octopus.repository.db4o.OctopusDb4oRepository;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DesignerApplication {

    public static void main(String[] args) throws IOException {
        // Reference to the license
        com.jidesoft.utils.Lm.verifyLicense("Lisa Park", "Octopus Designer", "zS6180HbbJpdVY1yArGP4blHYyvg6mK2");
        
        if (args.length != 1) {
            System.err.printf("Usage: DesignerApplication propertyFile\n");
            System.exit(-1);
        }

        Properties properties = parseProperties(args[0]);
        String repositoryFile = properties.getProperty("octopus.repository.file");
        if (repositoryFile == null || repositoryFile.length() == 0) {
            System.err.printf("The property file %s is missing the octopus.repository.file property", args[0]);
            System.exit(-1);
        }
        
        Integer port = Integer.parseInt(properties.getProperty("db4o.server.port"));
        String uid = properties.getProperty("db4o.server.uid");
        String psw = properties.getProperty("db4o.server.psw");

        OctopusRepository repository = new OctopusDb4oRepository(repositoryFile, port, uid, psw);
        
        LookAndFeelFactory.installJideExtension(5);
        
//        .installDefaultLookAndFeelAndExtension();
        final DesignerFrame designerFrame = new DesignerFrame(repository);
        try {
            designerFrame.loadInitialDataFromRepository();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    designerFrame.setVisible(true);
                }
            });
        } catch (RepositoryException e) {
            ErrorDialog.showErrorDialog(null, e, "Problem loading initial data from repository");
        }
    }

    private static Properties parseProperties(String propertyFileName) throws IOException {
        InputStream fin = null;
        Properties properties = null;
        try {
            fin = new FileInputStream(new File(propertyFileName));

            properties = new Properties();
            properties.load(fin);

        } finally {
            IOUtils.closeQuietly(fin);
        }

        return properties;
    }
}
