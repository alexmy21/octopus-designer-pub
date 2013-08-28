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
package org.lisapark.octopus.designer.palette;

import com.google.common.collect.Lists;
import org.lisapark.octopus.core.Node;

import javax.swing.*;
import java.util.List;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
class NodeListModel<T extends Node> extends AbstractListModel {

    private List<T> data = Lists.newArrayList();

    protected List<T> getData() {
        return data;
    }

    public void setData(List<T> newData) {
        this.data.clear();
        this.data.addAll(newData);
        fireContentsChanged(this, -1, -1);
    }

    @Override
    public int getSize() {
        List<T> nodes = getData();
        return nodes == null ? 0 : nodes.size();
    }

    @Override
    public Object getElementAt(int index) {
        List<T> nodes = getData();
        return nodes == null ? null : nodes.get(index);
    }

    static <T extends Node> NodeListModel<T> newNodeListModel() {
        return new NodeListModel<T>();
    }
}
