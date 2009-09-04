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
                help:"SearchGrid Component.html",
                designerType: "SearchGrid",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/table.png",
                imageCollapsed: "images/rapidjs/designer/table.png",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data."],
                        rootTag: [descr: "The root node name of AJAX response which SearchGrid takes as starting point to get its data."],
                        contentPath: [descr: "The node names of AJAX response which will be used as row data."],
                        keyAttribute: [descr: "The attribute name of the row node which uniquely identifies the node."],
                        defaultSearchClass: [descr: "Default class that the search will be applied."],
                        fieldsUrl: [descr: "The url used for the request to the server to retrieve available properties used in view builder."],
                        totalCountAttribute: [descr: "The attribute in the root node of the AJAX response which shows the total number of hits which matches the query."],
                        offsetAttribute: [descr: "The attribute in the root node of the AJAX response which shows where the results starts from according to the search query."],
                        sortOrderAttribute: [descr: "The attribute of the row which displays the sort position of the row according to the search query."],
                        pollingInterval: [descr: "Time delay between two server requests.", required:true],
                        queryParameter: [descr: "The url parameter to send the query to the server."],
                        queryEnabled: [descr: "Parameter to determine whether the quick filtering is enabled or not."],
                        searchClassesUrl: [descr: "The url used for the request to the server to retrieve available search classes."],
                        maxRowsDisplayed: [descr: "The maximum row count requested from server at every poll."],
                        defaultQuery: [descr: "The query appended to the all queries sent by SearchGrid."],
                        timeout:[descr:"The time interval in seconds to wait the server request completes successfully before aborting."]
                ],
                childrenConfiguration:
                [
                        [designerType: "SearchListTimeRangeSelector", isMultiple: false, propertyName: "subComponents"],
                        [
                                designerType: "SearchGridImages",
                                isMultiple: false,
                                metaData: [
                                        help:"SearchGrid Images.html",
                                        designerType: "SearchGridImages",
                                        display: "Images",
                                        imageExpanded: 'images/rapidjs/designer/images.png',
                                        imageCollapsed: 'images/rapidjs/designer/images.png',
                                        childrenConfiguration: [
                                                [designerType: "Image", propertyName: "images", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchGridColumns",
                                isMultiple: false,
                                metaData: [
                                        help:"SearchGrid Columns.html",
                                        designerType: "SearchGridColumns",
                                        display: "Columns",
                                        imageExpanded: 'images/rapidjs/designer/text_columns.png',
                                        imageCollapsed: 'images/rapidjs/designer/text_columns.png',
                                        childrenConfiguration: [
                                                [designerType: "SearchGridColumn", propertyName: "columns", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchGridRowColors",
                                isMultiple: false,
                                metaData: [
                                        help:"SearchGrid RowColors.html",
                                        designerType: "SearchGridRowColors",
                                        display: "RowColors",
                                        imageExpanded: 'images/rapidjs/designer/color_swatch.png',
                                        imageCollapsed: 'images/rapidjs/designer/color_swatch.png',
                                        childrenConfiguration: [
                                                [designerType: "RowColor", propertyName: "rowColors", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchGridMenuItems",
                                isMultiple: false,
                                metaData: [
                                        help:"SearchGrid MenuItems.html",
                                        designerType: "SearchGridMenuItems",
                                        display: "MenuItems",
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
        def searchGrid = DesignerUtils.addUiObject(UiSearchGrid, attributes, xmlNode);
        def timeRangeSelector = xmlNode.UiElement.find {it.@designerType.text() == "SearchListTimeRangeSelector"};
        def columnsNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchGridColumns"};
        def imagesNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchGridImages"};
        def menuItemsNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchGridMenuItems"};
        def rowColorsNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchGridRowColors"};
        if(timeRangeSelector && timeRangeSelector.size() > 0 )
        {
            UiSearchListTimeRangeSelector.addUiElement(timeRangeSelector, searchGrid)
        }
        columnsNode.UiElement.each{
            UiSearchGridColumn.addUiElement(it, searchGrid);
        }
        imagesNode.UiElement.each{
            UiImage.addUiElement(it, searchGrid);
        }
        menuItemsNode.UiElement.each{
            UiMenuItem.addUiElement(it, searchGrid);
        }
        rowColorsNode.UiElement.each{
            UiRowColor.addUiElement(it, searchGrid);
        }
        return searchGrid;
    }
}