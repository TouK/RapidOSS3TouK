package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 3, 2009
* Time: 10:40:44 AM
* To change this template use File | Settings | File Templates.
*/
class UiSearchListOperations extends UiComponentOperations{
    public static Map metaData()
    {
        Map metaData = [
                designerType: "SearchList",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/properties.gif",
                imageCollapsed: "images/rapidjs/designer/properties.gif",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data."],
                        rootTag: [descr: "The root node name of AJAX response which SearchGrid takes as starting point to get its data."],
                        contentPath: [descr: "The node names of AJAX response which will be used as row data."],
                        keyAttribute: [descr: "The attribute name of the row node which uniquely identifies the node."],
                        queryParameter: [descr: "The url parameter to send the query to the server."],
                        totalCountAttribute: [descr: "The attribute in the root node of the AJAX response which shows the total number of hits which matches the query."],
                        offsetAttribute: [descr: "The attribute in the root node of the AJAX response which shows where the results starts from according to the search query."],
                        sortOrderAttribute: [descr: "The attribute of the row which displays the sort position of the row according to the search query."],
                        defaultFields: [descr: "Properties list that will be shown when no field configuration is found for the row. Optional if showMax property is provided."],
                        showMax: [descr: "Maximum number of properties that will be displayed from the data. It overrides defaultFields and fields declarations."],
                        pollingInterval: [descr: "Time delay between two server requests.", required:true],
                        maxRowsDisplayed: [descr: "The maximum row count requested from server at every poll."],
                        lineSize: [descr: "How many lines a row consists of."],
                ],
                childrenConfiguration:
                [
                        [
                                designerType: "SearchListFields",
                                isMultiple: false,
                                metaData: [
                                        designerType: "SearchListFields",
                                        display: "Fields",
                                        imageExpanded: 'images/rapidjs/designer/tab.png',
                                        imageCollapsed: 'images/rapidjs/designer/tab.png',
                                        childrenConfiguration: [
                                                [designerType: "SearchListField", propertyName: "fields", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchListImages",
                                isMultiple: false,
                                metaData: [
                                        designerType: "SearchListImages",
                                        display: "Images",
                                        imageExpanded: 'images/rapidjs/designer/images.png',
                                        imageCollapsed: 'images/rapidjs/designer/images.png',
                                        childrenConfiguration: [
                                                [designerType: "Image", propertyName: "images", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchListMenuItems",
                                isMultiple: false,
                                metaData: [
                                        designerType: "SearchListMenuItems",
                                        display: "MenuItems",
                                        imageExpanded: 'images/rapidjs/designer/tab.png',
                                        imageCollapsed: 'images/rapidjs/designer/tab.png',
                                        childrenConfiguration: [
                                                [designerType: "MenuItem", propertyName: "menuItems", isMultiple: true, isVisible:{component-> return component.type == "component"}]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchListPropertyMenuItems",
                                isMultiple: false,
                                metaData: [
                                        designerType: "SearchListPropertyMenuItems",
                                        display: "PropertyMenuItems",
                                        imageExpanded: 'images/rapidjs/designer/tab.png',
                                        imageCollapsed: 'images/rapidjs/designer/tab.png',
                                        childrenConfiguration: [
                                                [designerType: "MenuItem", propertyName: "propertyMenuItems", isMultiple: true, isVisible:{component-> return component.type == "property"}]
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
        def searchList = DesignerUtils.addUiObject(UiSearchList, attributes, xmlNode);
        
        def columnsNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchListFields"};
        def imagesNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchListImages"};
        def menuItemsNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchListMenuItems"};
        def propertyMenuItemsNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchListPropertyMenuItems"};
        columnsNode.UiElement.each{
            UiSearchListField.addUiElement(it, searchList);
        }
        imagesNode.UiElement.each{
            UiImage.addUiElement(it, searchList);
        }
        menuItemsNode.UiElement.each{
            UiMenuItem.addUiElement(it, searchList);
        }
        def propMenuItems = [];
        propertyMenuItemsNode.UiElement.each{
            def propMenuItem = UiMenuItem.addUiElement(it, searchList);
            propMenuItem.type = "property";
            propMenuItems.add(propMenuItem);
        }
        searchList.addRelation(propertyMenuItems:propMenuItems);
        return searchList;
    }
}