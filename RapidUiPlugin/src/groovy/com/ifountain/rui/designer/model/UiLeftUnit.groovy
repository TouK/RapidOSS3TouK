package com.ifountain.rui.designer.model
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Oct 22, 2009
 * Time: 2:50:40 PM
 */
class UiLeftUnit extends UiLayoutUnit {
    Long width = 200;
    Long minWidth = 0;
    Long maxWidth = 0;
    Boolean resize = false;

    public static Map metaData()
    {
        Map metaData = [
                help: "Layout Panes - Center, Top, Bottom, Left, Right.html",
                designerType: "LeftUnit",
                canBeDeleted: true,
                display: "Left",
                imageExpanded: "images/rapidjs/designer/layout.png",
                imageCollapsed: "images/rapidjs/designer/layout.png",
                propertyConfiguration: [
                        width: [descr: "The width (in pixels) that the unit will take up in the layout.", validators:[nullable:false]],
                        resize: [descr: "Boolean indicating whether this unit is resizeable.", required: true],
                        minWidth: [descr: "The min width of unit."],
                        maxWidth: [descr: "The max width of unit."]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiLayoutUnit.metaData();
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }
}