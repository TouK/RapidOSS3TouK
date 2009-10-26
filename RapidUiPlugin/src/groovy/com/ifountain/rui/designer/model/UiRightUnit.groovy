package com.ifountain.rui.designer.model
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Oct 22, 2009
 * Time: 2:52:44 PM
 */
class UiRightUnit extends UiLayoutUnit {
    Long width = 200;
    Boolean resize = false;
    Long minWidth = 0;
    Long maxWidth = 0;

    public static Map metaData()
    {
        Map metaData = [
                help: "Layout Panes - Center, Top, Bottom, Left, Right.html",
                designerType: "RightUnit",
                canBeDeleted: true,
                display: "Right",
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