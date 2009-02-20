package ui.designer;
public class UiCenterUnitOperations extends UiLayoutUnitOperations
{

    public static Map metaData()
    {
        Map metaData = [
                help:"Layout Panes - Center, Top, Bottom, Left, Right.html",
                designerType:"CenterUnit",
                display: "Center",
                imageExpanded:"images/rapidjs/designer/layout_content.png",
                imageCollapsed:"images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [:],
                childrenConfiguration:[]
        ];
        def parentMetaData = UiLayoutUnitOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

}