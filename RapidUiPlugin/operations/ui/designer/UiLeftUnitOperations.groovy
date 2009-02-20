package ui.designer;
public class UiLeftUnitOperations extends UiLayoutUnitOperations
{

    public static Map metaData()
    {
        Map metaData = [
                help:"Layout Panes - Center, Top, Bottom, Left, Right.html",
                designerType:"LeftUnit",
                canBeDeleted: true,
                display: "Left",
                imageExpanded:"images/rapidjs/designer/layout_content.png",
                imageCollapsed:"images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        width:[descr:"The width (in pixels) that the unit will take up in the layout."],
                        resize:[descr:"Boolean indicating whether this unit is resizeable.", required:true],
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