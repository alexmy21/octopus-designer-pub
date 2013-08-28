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

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class LayoutConstants {

    public static final int COMPONENT_HORIZONTAL_GAP = 12;
    public static final int COMPONENT_VERTICAL_GAP = 12;

    /**
     * 12-pixel margins between the border of the
     * dialog box and its components.
     * 12->|                           |
     * +-|---------------------------|-+
     * 12->-|-|---------------------------|-|-
     * | |                           | |
     * | |                           | |
     * | |                           | |
     * -|-|---------------------------|-|-
     * +-|---------------------------|-+
     * |                           |
     */
    public static final int DIALOG_MARGIN_TOP = 10;
    public static final int DIALOG_MARGIN_LEFT = 5;

    // 10, 5, 5, 5

    /**
     * 11-pixel margins between the border of the
     * dialog box and its components.
     * |                           |
     * +-|---------------------------|-+
     * -|-|---------------------------|-|-
     * | |                           | |
     * | |                           | |
     * | |                           | |
     * -|-|---------------------------|-|- <-11
     * +-|---------------------------|-+
     * |                           | <-11
     */
    public static final int DIALOG_MARGIN_BOTTOM = 5;
    public static final int DIALOG_MARGIN_RIGHT = 5;

    /**
     * 17 pixels of vertical space between the command
     * button row and the other components
     * <p/>
     * +--------------------------------+
     * |                                |
     * |                                |
     * -|--------------------------------|-
     * |                                | 17
     * -|------------+-------+-+-------+-|-
     * |            | Find  | | Close | |
     * |            +-------+ +-------+ |
     * +--------------------------------+
     */
    public static final int DIALOG_COMMAND_ROW_TOP_SPACE = 17;

    /**
     * Insert 5 pixels between the trailing edge of a
     * label and any associated components. Insert 5
     * pixels between the trailing edge of a label and
     * the component it describes when labels are
     * right-aligned. When labels are left-aligned,
     * insert 5 pixels between the trailing edge of
     * the longest label and its associated component
     */
    public static final int LABEL_COMPONENT_SPACE = 5;

    /**
     * multiples of 6 pixels for perceived spacing between
     * components. If the measurement involves a component
     * edge with a white border, subtract 1 pixel to arrive
     * at the actual measurement between components (because
     * the white border on available components is less visually
     * significant than the dark border). In these cases, you
     * should specify the actual measurement as 1 pixel less--that
     * is, 5 pixels between components within a group and 11
     * pixels between groups of components.
     */
    public static final int COMPONENT_SPACE = 6;
    public static final int COMPONENT_GROUP_SPACE = 11;

    /**
     * Space individual toolbar buttons 2 pixels apart. Space
     * groups of toolbar buttons 11 pixels apart;
     */
    public static final int TOOLBAR_BUTTON_SPACE = 2;
    public static final int TOOLBAR_BUTTON_GROUP_SPACE = 11;

    /**
     * Include 3 pixels of space above and below toolbar buttons.
     * This actually means 2 pixels of space below the toolbar
     * because of the white border on the buttons.
     */
    public static final int TOOLBAR_BUTTON_TOP_SPACE = 3;
    public static final int TOOLBAR_BUTTON_BOTTOM_SPACE = 3;

    /**
     * When toggle buttons are independent (like checkboxes) and
     * used outside a toolbar, separate them with 5 pixels. Within
     * a toolbar, separate independent toggle buttons by 2 pixels
     */
    public static final int TOGGLE_BUTTON_SPACE = 5;

    /**
     * Space Command buttons in a group 5 pixels apart.
     */
    public static final int COMMAND_BUTTON_SPACE = 5;

    /**
     * Space between status bar components
     */
    public static final int STATUS_BAR_SPACE = 1;

    /**
     * Determine which button has the widest button text and insert
     * 12 pixels of padding on either side of the text. Make all
     * the remaining buttons in the group the same size as the button
     * with the longest text.
     */
    public static final int COMMAND_BUTTON_RIGHT_PADDING = 12;
    public static final int COMMAND_BUTTON_LEFT_PADDING = 12;

    /**
     * Checkbox and Radio Button Layout and Spacing
     * Align the leading of edge of checkboxes with
     * that of other components.
     * Use a layout manager to achieve consistent
     * spacing in checkbox button groups
     * <p/>
     * Space checkboxes in a group 5 pixels apart.
     * Space radio buttons in a group 5 pixels apart.
     */
    public static final int CHECK_BOX_SPACE = 5;
    public static final int RADIO_BUTTON_SPACE = 5;

    /**
     * Titled Borders for Panels
     * Use a titled border in a panel to group two or more sets of
     * related components, but do not draw titled borders around a
     * single set of checkboxes or radio buttons. Use labels instead.
     * <p/>
     * Insert 9 pixels between the edges of the panel and the titled border.
     * Insert 8 pixels between the top of the title and the component
     * above the titled border. Insert 9 pixels between the bottom of
     * the title and the top of the first label in the panel. Insert 8
     * pixels between component groups and between the bottom of the
     * last component and the lower border.
     */
    public static final int TITLE_PANEL_OUTSIDE_MARGIN = 9;
    public static final int TITLE_PANEL_INSIDE_MARGIN = 8;

    /**
     * This should be used for a titled border's bottom margin if within the border there are
     * components that have a COMPONENT_SPACE strut under them
     */
    public static final int TITLE_PANEL_BOTTOM_MARGIN_WITH_COMPONENT = TITLE_PANEL_INSIDE_MARGIN - COMPONENT_SPACE;

    /**
     * Minimum width for a button.
     */
    public static final int MINIMUM_BUTTON_WIDTH = 81;

    /**
     * Minimum height for a button.
     */
    public static final int MINIMUM_BUTTON_HEIGHT = 25;

}
