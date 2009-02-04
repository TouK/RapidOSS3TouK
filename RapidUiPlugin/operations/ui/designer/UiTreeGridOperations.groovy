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
                designerType: "TreeGrid",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        id: [descr: "The unique name of the component which is stored in the global JavaScript object YAHOO.rapidjs.Components."],
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data."],
                        rootTag: [descr: "The root node name of AJAX response which SearchGrid takes as starting point to get its data."],
                        contentPath: [descr: "The node names of AJAX response which will be used as row data."],
                        keyAttribute: [descr: "The attribute name of the row node which uniquely identifies the node."],
                        title: [descr: "SearchGrid title."],
                        pollingInterval: [descr: "Time delay between two server requests."],
                        expanded: [descr: "Parameter to display TreeGrid branches either expanded or collapsed"],
                        tooltip: [descr: "Parameter to display a tooltip over rows."],
                ],
                childrenConfiguration:
                [
                        [
                                designerType: "TreeGridColumns",
                                isMultiple: false,
                                metaData: [
                                        designerType: "TreeGridColumns",
                                        display: "Columns",
                                        imageExpanded: 'images/rapidjs/designer/tab.png',
                                        imageCollapsed: 'images/rapidjs/designer/tab.png',
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
                                        display: "RootImages",
                                        imageExpanded: 'images/rapidjs/designer/tab.png',
                                        imageCollapsed: 'images/rapidjs/designer/tab.png',
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
                                        imageExpanded: 'images/rapidjs/designer/tab.png',
                                        imageCollapsed: 'images/rapidjs/designer/tab.png',
                                        childrenConfiguration: [
                                                [designerType: "MenuItem", propertyName: "menuItems", isMultiple: true]
                                        ]
                                ]
                        ]
                ]
        ];
        def parentMetaData = UiComponentOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.tab = parentElement;
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