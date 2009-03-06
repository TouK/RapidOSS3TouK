package ui.designer;
public class UiBottomUnitOperations extends UiLayoutUnitOperations
{

    public static Map metaData()
    {
        Map metaData = [
                help:"Layout Panes - Center, Top, Bottom, Left, Right.html",
                designerType:"BottomUnit",
                canBeDeleted: true,
                display: "Bottom",
                imageExpanded:"images/rapidjs/designer/layout.png",
                imageCollapsed:"images/rapidjs/designer/layout.png",
                propertyConfiguration: [
                        height:[descr:"The height (in pixels) that the unit will take up in the layout."],
                        minHeight:[descr:"The min height of unit."],
                        maxHeight:[descr:"The max height of unit."],
                        resize:[descr:"Boolean indicating whether this unit is resizeable.", required:true],
                ],
                childrenConfiguration:[]
        ];
        def parentMetaData = UiLayoutUnitOperations.metaData();
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

}