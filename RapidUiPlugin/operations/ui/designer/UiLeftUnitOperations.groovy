package ui.designer;
public class UiLeftUnitOperations extends UiLayoutUnitOperations
{

    public static Map metaData()
    {
        Map metaData = [
                designerType:"LeftUnit",
                canBeDeleted: true,
                display: "Left",
                imageExpanded="images/rapidjs/designer/layout_content.png",
                imageCollapsed="images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        width:[descr:"The width (in pixels) that the unit will take up in the layout."],
                        resize:[descr:"Boolean indicating whether this unit is resizeable."],
                ],
                childrenConfiguration:[]
        ];
        def parentMetaData = UiLayoutUnitOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.putAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

}