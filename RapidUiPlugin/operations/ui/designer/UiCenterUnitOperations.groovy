package ui.designer;
public class UiCenterUnitOperations extends UiLayoutUnitOperations
{

    public static Map metaData()
    {
        Map metaData = [
                designerType:"CenterUnit",
                canBeDeleted: true,
                display: "Center",
                imageExpanded:"images/rapidjs/designer/layout_content.png",
                imageCollapsed:"images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                ],
                childrenConfiguration:[]
        ];
        def parentMetaData = UiLayoutUnitOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.putAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

}