package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 3, 2009
* Time: 10:46:15 AM
* To change this template use File | Settings | File Templates.
*/
class UiTreeGridOperations extends UiComponentOperations{
    public static Map metaData()
    {
        Map metaData = [
                help:"TreeGrid Component.html",
                designerType: "TreeGrid",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/chart_organisation.png",
                imageCollapsed: "images/rapidjs/designer/chart_organisation.png",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data."],
                        rootTag: [descr: "The root node name of AJAX response which SearchGrid takes as starting point to get its data."],
                        contentPath: [descr: "The node names of AJAX response which will be used as row data."],
                        keyAttribute: [descr: "The attribute name of the row node which uniquely identifies the node."],
                        expandAttribute: [descr: "The attribute name of the row node which shows current row as expanded."],
                        pollingInterval: [descr: "Time delay between two server requests.", required:true],
                        expanded: [descr: "Parameter to display TreeGrid branches either expanded or collapsed"],
                        tooltip: [descr: "Parameter to display a tooltip over rows."],
                        timeout:[descr:"The time interval in seconds to wait the server request completes successfully before aborting."]
                ],
                childrenConfiguration:
                [
                        [
                                designerType: "TreeGridColumns",
                                isMultiple: false,
                                metaData: [
                                        designerType: "TreeGridColumns",
                                        display: "Columns",
                                        help:"TreeGrid Columns.html",
                                        imageExpanded: 'images/rapidjs/designer/text_columns.png',
                                        imageCollapsed: 'images/rapidjs/designer/text_columns.png',
                                        childrenConfiguration: [
                                                [designerType: "TreeGridColumn", propertyName: "columns", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "TreeGridRootImages",
                                isMultiple: false,
                                metaData: [
                                        designerType: "TreeGridRootImages",
                                        help:"TreeGrid RootImages.html",
                                        display: "RootImages",
                                        imageExpanded: 'images/rapidjs/designer/images.png',
                                        imageCollapsed: 'images/rapidjs/designer/images.png',
                                        childrenConfiguration: [
                                                [designerType: "RootImage", propertyName: "rootImages", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "TreeGridMenuItems",
                                isMultiple: false,
                                metaData: [
                                        designerType: "TreeGridMenuItems",
                                        display: "MenuItems",
                                        help:"TreeGrid MenuItems.html",
                                        imageExpanded: 'images/rapidjs/designer/table_row_insert.png',
                                        imageCollapsed: 'images/rapidjs/designer/table_row_insert.png',
                                        childrenConfiguration: [
                                                [designerType: "MenuItem", propertyName: "menuItems", isMultiple: true, isVisible:{component-> return component.type == "component" && component.parentMenuItem == null}]
                                        ]
                                ]
                        ]
                ]
        ];
        def parentMetaData = UiComponentOperations.metaData();
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.tab = parentElement;
        attributes.tabId = parentElement.id;
        def treeGrid = DesignerUtils.addUiObject(UiTreeGrid, attributes, xmlNode);

        def columnsNode = xmlNode.UiElement.find {it.@designerType.text() == "TreeGridColumns"};
        def imagesNode = xmlNode.UiElement.find {it.@designerType.text() == "TreeGridRootImages"};
        def menuItemsNode = xmlNode.UiElement.find {it.@designerType.text() == "TreeGridMenuItems"};
        columnsNode.UiElement.each{
            UiTreeGridColumn.addUiElement(it, treeGrid);
        }
        imagesNode.UiElement.each{
            UiRootImage.addUiElement(it, treeGrid);
        }
        menuItemsNode.UiElement.each{
            UiMenuItem.addUiElement(it, treeGrid);
        }
        return treeGrid;
    }
}