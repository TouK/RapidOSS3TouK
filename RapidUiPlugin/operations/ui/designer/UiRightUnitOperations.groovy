package ui.designer;
public class UiRightUnitOperations extends UiLayoutUnitOperations
{

    public static Map metaData()
    {
        Map metaData = [
                designerType:"RightUnit",
                canBeDeleted: true,
                display: "Right",
                imageExpanded:"images/rapidjs/designer/layout_content.png",
                imageCollapsed:"images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        width:[descr:"The width (in pixels) that the unit will take up in the layout."],
                        resize:[descr:"Boolean indicating whether this unit is resizeable."],
                        minWidth:[descr:"The min width of unit."],
                        maxWidth:[descr:"The max width of unit."]
                ],
                childrenConfiguration:[]
        ];
        def parentMetaData = UiLayoutUnitOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

}