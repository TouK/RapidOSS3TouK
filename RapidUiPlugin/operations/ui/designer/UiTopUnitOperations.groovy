package ui.designer;
public class UiTopUnitOperations extends UiLayoutUnitOperations
{

    public static Map metaData()
    {
        Map metaData = [
                designerType:"TopUnit",
                canBeDeleted: true,
                display: "Top",
                imageExpanded="images/rapidjs/designer/layout_content.png",
                imageCollapsed="images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        height:[descr:"The height (in pixels) that the unit will take up in the layout."],
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