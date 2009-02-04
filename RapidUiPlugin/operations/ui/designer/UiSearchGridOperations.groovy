package ui.designer

import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 3, 2009
* Time: 10:30:44 AM
* To change this template use File | Settings | File Templates.
*/
class UiSearchGridOperations extends UiComponentOperations {
    public static Map metaData()
    {
        Map metaData = [
                designerType: "SearchGrid",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data."],
                        rootTag: [descr: "The root node name of AJAX response which SearchGrid takes as starting point to get its data."],
                        contentPath: [descr: "The node names of AJAX response which will be used as row data."],
                        keyAttribute: [descr: "The attribute name of the row node which uniquely identifies the node."],
                        fieldsUrl: [descr: "The url used for the request to the server to retrieve available properties used in view builder."],
                        queryParameter: [descr: "The url parameter to send the query to the server."],
                        totalCountAttribute: [descr: "The attribute in the root node of the AJAX response which shows the total number of hits which matches the query."],
                        offsetAttribute: [descr: "The attribute in the root node of the AJAX response which shows where the results starts from according to the search query."],
                        sortOrderAttribute: [descr: "The attribute of the row which displays the sort position of the row according to the search query."],
                        title: [descr: "SearchGrid title."],
                        pollingInterval: [descr: "Time delay between two server requests."],
                        queryEnabled: [descr: "Parameter to determine whether the quick filtering is enabled or not."],
                        maxRowsDisplayed: [descr: "The maximum row count requested from server at every poll."],
                        defaultQuery: [descr: "The query appended to the all queries sent by SearchGrid."],
                ],
                childrenConfiguration:
                [
                        [
                                designerType: "SearchGridImages",
                                isMultiple: false,
                                metaData: [
                                        designerType: "SearchGridImages",
                                        display: "Images",
                                        imageExpanded: 'images/rapidjs/designer/tab.png',
                                        imageCollapsed: 'images/rapidjs/designer/tab.png',
                                        childrenConfiguration: [
                                                [designerType: "Image", propertyName: "images", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchGridColumns",
                                isMultiple: false,
                                metaData: [
                                        designerType: "SearchGridColumns",
                                        display: "Columns",
                                        imageExpanded: 'images/rapidjs/designer/tab.png',
                                        imageCollapsed: 'images/rapidjs/designer/tab.png',
                                        childrenConfiguration: [
                                                [designerType: "Column", propertyName: "columns", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchGridMenuItems",
                                isMultiple: false,
                                metaData: [
                                        designerType: "SearchGridMenuItems",
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
        def searchGrid = DesignerUtils.addUiObject(UiSearchGrid, attributes, xmlNode);
        def columnsNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchGridColumns"};
        def imagesNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchGridImages"};
        def menuItemsNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchGridMenuItems"};
        columnsNode.UiElement.each{
            UiColumn.addUiElement(it, searchGrid);
        }
        imagesNode.UiElement.each{
            UiImage.addUiElement(it, searchGrid);
        }
        menuItemsNode.UiElement.each{
            UiMenuItem.addUiElement(it, searchGrid);
        }
        return searchGrid;
    }
}