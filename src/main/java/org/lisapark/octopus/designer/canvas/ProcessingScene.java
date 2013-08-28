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

import com.google.common.collect.Lists;
import org.lisapark.octopus.core.Input;
import org.lisapark.octopus.core.Node;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.sink.Sink;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.Source;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.designer.canvas.actions.RemoveConnectionAction;
import org.lisapark.octopus.designer.canvas.actions.RemoveExternalSinkAction;
import org.lisapark.octopus.designer.canvas.actions.RemoveExternalSourceAction;
import org.lisapark.octopus.designer.canvas.actions.RemoveProcessorAction;
import org.lisapark.octopus.designer.canvas.providers.NodeMoveProvider;
import org.lisapark.octopus.designer.canvas.providers.ProcessingSceneConnectProvider;
import org.lisapark.octopus.designer.dnd.NodeAcceptProvider;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.graph.GraphPinScene;
import org.netbeans.api.visual.graph.layout.GridGraphLayout;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.vmd.VMDConnectionWidget;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.EventProcessingType;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ProcessingScene extends GraphPinScene<Node, Connection, Pin> {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingScene.class);

    private final LayerWidget mainLayer = new LayerWidget(this);
    private final LayerWidget connectionLayer = new LayerWidget(this);
    private final LayerWidget interactionLayer = new LayerWidget(this);

    private Router router;

    private WidgetAction connectAction = ActionFactory.createExtendedConnectAction(interactionLayer, new ProcessingSceneConnectProvider(this));
    //private WidgetAction reconnectAction = ActionFactory.createReconnectAction(new ProcessingSceneReconnectProvider(this));
    private WidgetAction moveControlPointAction = ActionFactory.createOrthogonalMoveControlPointAction();
    private WidgetAction moveAction = ActionFactory.createMoveAction(ActionFactory.createFreeMoveStrategy(), new NodeMoveProvider(this));
    private WidgetAction acceptAction = ActionFactory.createAcceptAction(new NodeAcceptProvider(this));

    private WidgetAction removeConnectionAction = new RemoveConnectionAction(this);
    private WidgetAction removeExternalSinkAction = new RemoveExternalSinkAction(this);
    private WidgetAction removeExternalSourceAction = new RemoveExternalSourceAction(this);
    private WidgetAction removeProcessorAction = new RemoveProcessorAction(this);

    private ProcessingModel model;

    public ProcessingScene(ProcessingModel model) {
        this.model = model;
        setKeyEventProcessingType(EventProcessingType.FOCUSED_WIDGET_AND_ITS_PARENTS);

        LayerWidget backgroundLayer = new LayerWidget(this);
        addChild(backgroundLayer);
        addChild(mainLayer);
        addChild(connectionLayer);
        addChild(interactionLayer);

        router = RouterFactory.createOrthogonalSearchRouter(mainLayer, connectionLayer);

        getActions().addAction(ActionFactory.createZoomAction());
        getActions().addAction(ActionFactory.createPanAction());
        getActions().addAction(ActionFactory.createRectangularSelectAction(this, backgroundLayer));
        getActions().addAction(acceptAction);

        LayoutFactory.createSceneGraphLayout(this, new GridGraphLayout<Node, Connection>().setChecker(true));

        initializeFromModel(model);

    }

    private void initializeFromModel(ProcessingModel model) {
        Set<ExternalSource> externalSources = model.getExternalSources();
        for (ExternalSource externalSource : externalSources) {
            addExternalSource(externalSource);
        }

        Set<Processor> processors = model.getProcessors();
        for (Processor processor : processors) {
            addProcessor(processor);
        }

        Set<ExternalSink> externalSinks = model.getExternalSinks();
        for (ExternalSink externalSink : externalSinks) {
            addExternalSink(externalSink);
        }

        for (ExternalSink sink : externalSinks) {
            List<? extends Input> inputs = sink.getInputs();

            for (Input input : inputs) {
                Source sinkSource = input.getSource();

                if (sinkSource != null) {
                    Connection edge = Connection.connectSourceToSinkInput(sinkSource, input);
                    addEdge(edge);

                    OutputPin sourcePin = findOutputPinForSource(sinkSource);
                    setEdgeSource(edge, sourcePin);

                    InputPin destinationPin = findInputPinForSinkAndInput(sink, input);
                    setEdgeTarget(edge, destinationPin);
                }
            }
        }

        // now connect everything - we do this by way of examining the inputs for a Processor
        for (Processor processor : processors) {
            Collection<Input> inputs = processor.getInputs();

            for (Input input : inputs) {
                Source processorSource = input.getSource();

                if (processorSource != null) {
                    Connection edge = Connection.connectSourceToSinkInput(processorSource, input);
                    addEdge(edge);

                    OutputPin sourcePin = findOutputPinForSource(processorSource);
                    setEdgeSource(edge, sourcePin);

                    InputPin destinationPin = findInputPinForSinkAndInput(processor, input);
                    setEdgeTarget(edge, destinationPin);
                }
            }
        }
    }

    private InputPin findInputPinForSinkAndInput(Sink sink, Input input) {
        Collection<Pin> processorPins = getNodePins(sink);
        InputPin inputPin = null;

        for (Pin pin : processorPins) {
            if (pin instanceof InputPin) {
                InputPin candidatePin = (InputPin) pin;

                if (candidatePin.getInput().equals(input)) {
                    inputPin = candidatePin;
                    break;
                }
            }
        }

        if (inputPin == null) {
            throw new IllegalStateException(
                    String.format("Could not find inputPin for sink [%s] and input [%s]", sink, input));
        }

        return inputPin;
    }

    private OutputPin findOutputPinForSource(Source source) {
        Collection<Pin> processorPins = getNodePins(source);
        OutputPin outputPin = null;

        for (Pin pin : processorPins) {
            if (pin instanceof OutputPin) {
                outputPin = (OutputPin) pin;
                break;
            }
        }

        if (outputPin == null) {
            throw new IllegalStateException(String.format("Could not find outputPin for source [%s]", source));
        }

        return outputPin;
    }

    public void addExternalSource(ExternalSource source) {
        // make sure we have never seen this source before
        if (findStoredObject(source) == null) {
            model.addExternalEventSource(source);

            VMDNodeWidget nodeWidget = (VMDNodeWidget) addNode(source);

            nodeWidget.setPreferredLocation(source.getLocation());
            nodeWidget.setNodeName(source.getName());
            nodeWidget.setToolTipText(source.getDescription());

            VMDPinWidget pinWidget = (VMDPinWidget) addPin(source, new OutputPin(source));
            pinWidget.setPinName(source.getOutput().getName());
            pinWidget.setToolTipText(source.getOutput().getDescription());
        }
    }

    public void removeExternalSource(ExternalSource externalSource) {
        // make sure it is a valid source
        if (findStoredObject(externalSource) != null) {
            model.removeExternalEventSource(externalSource);

            removeNodeWithEdges(externalSource);
        }
    }

    public void addExternalSink(ExternalSink sink) {
        // make sure we have never seen this sink before
        if (findStoredObject(sink) == null) {
            model.addExternalSink(sink);

            VMDNodeWidget nodeWidget = (VMDNodeWidget) addNode(sink);

            nodeWidget.setPreferredLocation(sink.getLocation());
            nodeWidget.setNodeName(sink.getName());
            nodeWidget.setToolTipText(sink.getDescription());

            // add the input pins
            for (Input input : sink.getInputs()) {
                VMDPinWidget pinWidget = (VMDPinWidget) addPin(sink, new InputPin(sink, input));
                pinWidget.setPinName(input.getName());
                pinWidget.setToolTipText(input.getDescription());
            }
        }
    }

    public void removeExternalSink(ExternalSink externalSink) {
        // make sure it is a valid sink
        if (findStoredObject(externalSink) != null) {
            model.removeExternalEventSink(externalSink);

            removeNodeWithEdges(externalSink);
        }
    }

    public void addProcessor(Processor<?> processor) {
        // make sure we have never seen this processor before
        if (findStoredObject(processor) == null) {
            model.addProcessor(processor);

            VMDNodeWidget nodeWidget = (VMDNodeWidget) addNode(processor);

            nodeWidget.setPreferredLocation(processor.getLocation());
            nodeWidget.setNodeName(processor.getName());
            nodeWidget.setToolTipText(processor.getDescription());

            // add the input pins
            for (Input input : processor.getInputs()) {
                VMDPinWidget pinWidget = (VMDPinWidget) addPin(processor, new InputPin(processor, input));
                pinWidget.setPinName(input.getName());
                pinWidget.setToolTipText(input.getDescription());
            }

            // add the output pin if this processor generates an output

            VMDPinWidget pinWidget = (VMDPinWidget) addPin(processor, new OutputPin(processor));

            pinWidget.setPinName(processor.getOutput().getName());
            pinWidget.setToolTipText(processor.getOutput().getDescription());
        }
    }

    public void removeProcessor(Processor<?> processor) {
        // make sure it is a valid processor
        if (findStoredObject(processor) != null) {
            model.removeProcessor(processor);

            removeNodeWithEdges(processor);
        }
    }

    public void connectOutputPinToInputPin(OutputPin sourcePin, InputPin destinationPin) {
        // todo verify the pin
        Connection connection = Connection.connectSourceToSinkInput(sourcePin.getSource(), destinationPin.getInput());

        addEdge(connection);

        setEdgeSource(connection, sourcePin);
        setEdgeTarget(connection, destinationPin);
    }


    public void removeConnection(Connection connection) {
        // make sure it is a valid connection
        if (findStoredObject(connection) != null) {
            removeEdge(connection);

            connection.clearSource();
        }
    }

    @Override
    protected Widget attachNodeWidget(Node node) {
        VMDNodeWidget widget = new VMDNodeWidget(this);
        mainLayer.addChild(widget);

        widget.getHeader().getActions().addAction(createObjectHoverAction());
        widget.getActions().addAction(createSelectAction());
        widget.getActions().addAction(moveAction);
        widget.getActions().addAction(getRemoveActionForNode(node));

        return widget;
    }

    private WidgetAction getRemoveActionForNode(Node node) {
        if (node instanceof Processor) {
            return removeProcessorAction;

        } else if (node instanceof ExternalSink) {
            return removeExternalSinkAction;

        } else {
            return removeExternalSourceAction;
        }
    }

    @Override
    protected Widget attachEdgeWidget(Connection edge) {
        VMDConnectionWidget connectionWidget = new VMDConnectionWidget(this, router);
        connectionLayer.addChild(connectionWidget);

        connectionWidget.getActions().addAction(createObjectHoverAction());
        connectionWidget.getActions().addAction(createSelectAction());
        connectionWidget.getActions().addAction(moveControlPointAction);
        connectionWidget.getActions().addAction(removeConnectionAction);

        connectionLayer.addChild(connectionWidget);

        return connectionWidget;
    }

    @Override
    protected Widget attachPinWidget(Node node, Pin pin) {
        VMDPinWidget widget = new VMDPinWidget(this);
        Image image = Utilities.loadImage("methodPublic.gif");
        widget.setGlyphs(Lists.newArrayList(image));
        ((VMDNodeWidget) findWidget(node)).attachPinWidget(widget);
        widget.getActions().addAction(createObjectHoverAction());
        widget.getActions().addAction(createSelectAction());
        widget.getActions().addAction(connectAction);

        return widget;
    }

    @Override
    protected void attachEdgeSourceAnchor(Connection edge, Pin oldSourcePin, Pin sourcePin) {
        LOG.debug(String.format("attachEdgeSourceAnchor Edge %s, oldSourcePin %s, sourcePin %s", edge, oldSourcePin, sourcePin));

        if (sourcePin != null) {
            ((ConnectionWidget) findWidget(edge)).setSourceAnchor(getPinAnchor(sourcePin));
        } else {
            ((ConnectionWidget) findWidget(edge)).setSourceAnchor(null);
        }
    }

    @Override
    protected void attachEdgeTargetAnchor(Connection edge, Pin oldTargetPin, Pin targetPin) {
        LOG.debug(String.format("attachEdgeTargetAnchor Edge %s, oldTargetPin %s, targetPin %s", edge, oldTargetPin, targetPin));

        if (targetPin != null) {
            ((ConnectionWidget) findWidget(edge)).setTargetAnchor(getPinAnchor(targetPin));
        } else {
            ((ConnectionWidget) findWidget(edge)).setTargetAnchor(null);
        }
    }

    private Anchor getPinAnchor(Pin pin) {
        VMDNodeWidget nodeWidget = (VMDNodeWidget) findWidget(getPinNode(pin));
        Widget pinMainWidget = findWidget(pin);
        Anchor anchor;
        if (pinMainWidget != null) {
            anchor = AnchorFactory.createDirectionalAnchor(pinMainWidget, AnchorFactory.DirectionalAnchorKind.HORIZONTAL, 8);
            anchor = nodeWidget.createAnchorPin(anchor);
        } else
            anchor = nodeWidget.getNodeAnchor();
        return anchor;
    }
}
