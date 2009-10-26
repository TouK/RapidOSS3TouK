package com.ifountain.rui.designer.model
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Oct 22, 2009
 * Time: 2:51:51 PM
 */
class UiTopUnit extends UiLayoutUnit{
    Long height = 200;
    Boolean resize = false;
    Long maxHeight = 0;
    Long minHeight = 0;

    public static Map metaData()
    {
        Map metaData = [
                help: "Layout Panes - Center, Top, Bottom, Left, Right.html",
                designerType: "TopUnit",
                canBeDeleted: true,
                display: "Top",
                imageExpanded: "images/rapidjs/designer/layout.png",
                imageCollapsed: "images/rapidjs/designer/layout.png",
                propertyConfiguration: [
                        height: [descr: "The height (in pixels) that the unit will take up in the layout.", validators:[nullable:false]],
                        resize: [descr: "Boolean indicating whether this unit is resizeable.", required: true],
                        minHeight: [descr: "The min height of unit."],
                        maxHeight: [descr: "The max height of unit."]
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