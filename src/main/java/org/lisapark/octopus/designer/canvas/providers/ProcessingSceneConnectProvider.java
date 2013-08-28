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
package org.lisapark.octopus.designer.canvas.providers;

import org.lisapark.octopus.designer.canvas.InputPin;
import org.lisapark.octopus.designer.canvas.OutputPin;
import org.lisapark.octopus.designer.canvas.ProcessingScene;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ProcessingSceneConnectProvider implements ConnectProvider {

    private ProcessingScene scene;

    public ProcessingSceneConnectProvider(ProcessingScene scene) {
        this.scene = scene;
    }

    private OutputPin getOutPinForFromWidget(Widget widget) {
        Object object = scene.findObject(widget);

        OutputPin sourcePin = null;
        if (object instanceof OutputPin) {
            sourcePin = (OutputPin) object;
        }

        return sourcePin;
    }

    private InputPin getInputPinFromWidget(Widget widget) {
        Object object = scene.findObject(widget);

        InputPin inputPin = null;
        if (object instanceof InputPin) {
            inputPin = (InputPin) object;
        }

        return inputPin;
    }

    /**
     * Called for checking whether a specified source widget is a possible source of a connection. Only widgets
     * that have an {@link OutputPin} are possible sources.
     *
     * @param sourceWidget the source widget
     * @return if true, then it is possible to create a connection for the source widget; if false, then is not allowed
     */
    @Override
    public boolean isSourceWidget(Widget sourceWidget) {

        return getOutPinForFromWidget(sourceWidget) != null;
    }

    /**
     * Called for checking whether a connection could be created between a specified source and target widget.
     * Called only when a hasCustomTargetWidgetResolver returns false.
     * <p/>
     * We only allows connections when the targetWidget is an {@link InputPin} and it is <b>not</b> already connected
     *
     * @param sourceWidget the source widget
     * @param targetWidget the target widget
     * @return the connector state
     */
    @Override
    public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
        Object object = scene.findObject(targetWidget);

        ConnectorState connectorState = ConnectorState.REJECT;

        if (object instanceof InputPin) {
            InputPin inputPin = (InputPin) object;

            // make sure the input is NOT already in use
            if (!inputPin.isConnected()) {
                connectorState = ConnectorState.ACCEPT;
            }

        } else if (object != null) {
            connectorState = ConnectorState.REJECT_AND_STOP;
        }

        return connectorState;
    }

    /**
     * Called to check whether the provider has a custom target widget resolver.
     *
     * @param scene the scene where the resolver will be called
     * @return false, then the isTargetWidget method is called for resolving the target widget
     */
    @Override
    public boolean hasCustomTargetWidgetResolver(Scene scene) {
        return false;
    }

    /**
     * Called to find the target widget of a possible connection.
     * Called only when a hasCustomTargetWidgetResolver returns true.
     *
     * @param scene         the scene
     * @param sceneLocation the scene location
     * @return the target widget; null if no target widget found
     */
    @Override
    public Widget resolveTargetWidget(Scene scene, Point sceneLocation) {

        return null;
    }

    /**
     * Called for creating a new connection between a specified source and target widget.
     * This method is called only when the possible connection is available and an user approves its creation.
     *
     * @param sourceWidget the source widget
     * @param targetWidget the target widget
     */
    @Override
    public void createConnection(Widget sourceWidget, Widget targetWidget) {
        if (isSourceWidget(sourceWidget) && isTargetWidget(sourceWidget, targetWidget).equals(ConnectorState.ACCEPT)) {
            // we create a connection from the Source's output pin to the target's input pin
            OutputPin outputPin = getOutPinForFromWidget(sourceWidget);
            InputPin inputPin = getInputPinFromWidget(targetWidget);

            scene.connectOutputPinToInputPin(outputPin, inputPin);
        }
    }
}
