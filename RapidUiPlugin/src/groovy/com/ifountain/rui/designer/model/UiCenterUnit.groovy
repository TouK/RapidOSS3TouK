package com.ifountain.rui.designer.model
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Oct 21, 2009
 * Time: 6:31:20 PM
 */
class UiCenterUnit extends UiLayoutUnit {
    public static Map metaData()
    {
        Map metaData = [
                help: "Layout Panes - Center, Top, Bottom, Left, Right.html",
                designerType: "CenterUnit",
                display: "Center",
                imageExpanded: "images/rapidjs/designer/layout.png",
                imageCollapsed: "images/rapidjs/designer/layout.png",
                propertyConfiguration: [:],
                childrenConfiguration: []
        ];
        def parentMetaData = UiLayoutUnit.metaData();
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        propConfig.remove("collapse");
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }
}