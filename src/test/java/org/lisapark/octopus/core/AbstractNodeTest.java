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
package org.lisapark.octopus.core;

import java.awt.*;
import java.util.UUID;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class AbstractNodeTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullName() throws Exception {
        AbstractNodeImpl abstractNodeImpl = new AbstractNodeImpl(null, "description");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullDescription() throws Exception {
        AbstractNodeImpl abstractNodeImpl = new AbstractNodeImpl("name", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetName_Null() throws Exception {
        Node node = new AbstractNodeImpl();
        node.setName(null);
    }

    @Test
    public void testSetName() throws Exception {
        Node node = new AbstractNodeImpl();
        node = node.setName("test");

        assertThat(node.getName(), is("test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDescription_Null() throws Exception {
        Node node = new AbstractNodeImpl();
        node.setDescription(null);
    }

    @Test
    public void testSetDescription() throws Exception {
        Node node = new AbstractNodeImpl();
        node = node.setDescription("test");

        assertThat(node.getDescription(), is("test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetLocation_Null() throws Exception {
        Node node = new AbstractNodeImpl();
        node.setLocation(null);
    }

    @Test
    public void testSetLocation() throws Exception {
        Node node = new AbstractNodeImpl();
        Point location = new Point(1, 2);
        node = node.setLocation(location);

        assertThat(node.getLocation(), is(location));
    }

    private static class AbstractNodeImpl extends AbstractNode {

        private AbstractNodeImpl(String name, String description) {
            super(UUID.randomUUID(), name, description);
        }

        private AbstractNodeImpl() {
            super(UUID.randomUUID());
        }

        @Override
        public Reproducible newInstance() {
            return new AbstractNodeImpl(this.getName(), this.getDescription());
        }

        @Override
        public Copyable copyOf() {
            return null;
        }
    }
}
