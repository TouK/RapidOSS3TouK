package com.ifountain.rui.designer.model

import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.UiElmnt
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 3:27:50 PM
*/
class UiSearchGrid extends UiComponent {
    String url = "search";
    String rootTag = "Objects";
    String contentPath = "Object";
    String keyAttribute = "id";
    String defaultView = "default";
    String fieldsUrl = "script/run/getViewFields?format=xml";
    String queryParameter = "query";
    String totalCountAttribute = "total";
    String offsetAttribute = "offset";
    String sortOrderAttribute = "sortOrder";
    String defaultSearchClass = "RsEvent"
    String searchClassesUrl = "script/run/getClassesForSearch?rootClass=RsEvent&format=xml"
    String extraPropertiesToRequest = ""
    Long pollingInterval = 0;
    Long timeout = 30;
    Boolean searchInEnabled = true;
    Boolean queryEnabled = true;
    Boolean bringAllProperties = true;
    Long maxRowsDisplayed = 100;
    String defaultQuery = "";


    public static Map metaData()
    {
        Map metaData = [
                help: "SearchGrid Component.html",
                designerType: "SearchGrid",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/table.png",
                imageCollapsed: "images/rapidjs/designer/table.png",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data.", validators: [blank: false, nullable: false]],
                        rootTag: [descr: "The root node name of AJAX response which SearchGrid takes as starting point to get its data.", validators: [blank: false, nullable: false]],
                        contentPath: [descr: "The node names of AJAX response which will be used as row data.", validators: [blank: false, nullable: false]],
                        keyAttribute: [descr: "The attribute name of the row node which uniquely identifies the node.", validators: [blank: false, nullable: false]],
                        defaultSearchClass: [descr: "Default class that the search will be applied.", validators: [blank: false, nullable: false]],
                        fieldsUrl: [descr: "The url used for the request to the server to retrieve available properties used in view builder.", validators: [blank: false, nullable: false]],
                        totalCountAttribute: [descr: "The attribute in the root node of the AJAX response which shows the total number of hits which matches the query.", validators: [blank: false, nullable: false]],
                        offsetAttribute: [descr: "The attribute in the root node of the AJAX response which shows where the results starts from according to the search query.", validators: [blank: false, nullable: false]],
                        sortOrderAttribute: [descr: "The attribute of the row which displays the sort position of the row according to the search query.", validators: [blank: false, nullable: false]],
                        pollingInterval: [descr: "Time delay between two server requests.", required: true],
                        defaultView: [descr: "The view which will be shown when the search grid is shown."],
                        queryParameter: [descr: "The url parameter to send the query to the server.", validators: [blank: false, nullable: false]],
                        searchInEnabled: [descr: "Determines if the query should be applied on only defaultSearchClass or should be selected among classes which are brought by searchClassesUrl.", validators: [blank: false, nullable: false]],
                        queryEnabled: [descr: "Parameter to determine whether the quick filtering is enabled or not."],
                        bringAllProperties: [descr: "When set to false SearchGrid requests its data with a parameter (\"propertyList\") to indicate that it needs only a set of properties, to decrease the size of the data coming from server."],
                        extraPropertiesToRequest: [descr: "Comma separated property names which will be added \"propertyList\" URL parameter. Active when bringAllProperties is set to false."],
                        searchClassesUrl: [descr: "The url used for the request to the server to retrieve available search classes."],
                        maxRowsDisplayed: [descr: "The maximum row count requested from server at every poll."],
                        defaultQuery: [descr: "The query appended to the all queries sent by SearchGrid."],
                        timeout: [descr: "The time interval in seconds to wait the server request completes successfully before aborting."]
                ],
                childrenConfiguration:
                [
                        [designerType: "SearchListTimeRangeSelector", isMultiple: false],
                        [
                                designerType: "SearchGridImages",
                                isMultiple: false,
                                metaData: [
                                        help: "SearchGrid Images.html",
                                        designerType: "SearchGridImages",
                                        display: "Images",
                                        imageExpanded: 'images/rapidjs/designer/images.png',
                                        imageCollapsed: 'images/rapidjs/designer/images.png',
                                        childrenConfiguration: [
                                                [designerType: "Image", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchGridColumns",
                                isMultiple: false,
                                metaData: [
                                        help: "SearchGrid Columns.html",
                                        designerType: "SearchGridColumns",
                                        display: "Columns",
                                        imageExpanded: 'images/rapidjs/designer/text_columns.png',
                                        imageCollapsed: 'images/rapidjs/designer/text_columns.png',
                                        childrenConfiguration: [
                                                [designerType: "SearchGridColumn", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchGridRowColors",
                                isMultiple: false,
                                metaData: [
                                        help: "SearchGrid RowColors.html",
                                        designerType: "SearchGridRowColors",
                                        display: "RowColors",
                                        imageExpanded: 'images/rapidjs/designer/color_swatch.png',
                                        imageCollapsed: 'images/rapidjs/designer/color_swatch.png',
                                        childrenConfiguration: [
                                                [designerType: "RowColor", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchGridMenuItems",
                                isMultiple: false,
                                metaData: [
                                        help: "SearchGrid MenuItems.html",
                                        designerType: "SearchGridMenuItems",
                                        display: "MenuItems",
                                        imageExpanded: 'images/rapidjs/designer/table_row_insert.png',
                                        imageCollapsed: 'images/rapidjs/designer/table_row_insert.png',
                                        childrenConfiguration: [
                                                [designerType: "MenuItem", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchGridMultiSelectionMenuItems",
                                isMultiple: false,
                                metaData: [
                                        help: "SearchGrid MenuItems.html",
                                        designerType: "SearchGridMultiSelectionMenuItems",
                                        display: "MultiSelectionMenuItems",
                                        imageExpanded: 'images/rapidjs/designer/table_row_insert.png',
                                        imageCollapsed: 'images/rapidjs/designer/table_row_insert.png',
                                        childrenConfiguration: [
                                                [designerType: "MenuItem", isMultiple: true]
                                        ]
                                ]
                        ]
                ]
        ];
        def parentMetaData = UiComponent.metaData();
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.tabId = parentElement._designerKey;
        if(attributes.searchInEnabled == "true" && attributes.searchClassesUrl == ""){
            throw new Exception("Property searchClassesUrl should be provided if searchInEnabled is true for SearchGrid ${attributes.name}")
        }
        def searchGrid = DesignerSpace.getInstance().addUiElement(UiSearchGrid, attributes);
        def timeRangeSelector = xmlNode.UiElement.find {it.@designerType.text() == "SearchListTimeRangeSelector"};
        def columnsNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchGridColumns"};
        def imagesNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchGridImages"};
        def menuItemsNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchGridMenuItems"};
        def multiSelectionMenuItemsNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchGridMultiSelectionMenuItems"};
        def rowColorsNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchGridRowColors"};
        if (timeRangeSelector && timeRangeSelector.size() > 0)
        {
            UiSearchListTimeRangeSelector.addUiElement(timeRangeSelector, searchGrid)
        }
        columnsNode.UiElement.each {
            UiSearchGridColumn.addUiElement(it, searchGrid);
        }
        imagesNode.UiElement.each {
            UiImage.addUiElement(it, searchGrid);
        }
        menuItemsNode.UiElement.each {
            UiMenuItem.addUiElement(it, searchGrid);
        }
        multiSelectionMenuItemsNode.UiElement.each {
            def menuItem = UiMenuItem.addUiElement(it, searchGrid);
            menuItem.type = "multiple"
        }
        rowColorsNode.UiElement.each {
            UiRowColor.addUiElement(it, searchGrid);
        }
        return searchGrid;
    }

    public List getSubComponents() {
        return DesignerSpace.getInstance().getUiElements(UiSearchListTimeRangeSelector).values().findAll {it.componentId == _designerKey};
    }

    public List getImages() {
        return DesignerSpace.getInstance().getUiElements(UiImage).values().findAll {it.componentId == _designerKey};
    }

    public List getRowColors() {
        return DesignerSpace.getInstance().getUiElements(UiRowColor).values().findAll {it.gridId == _designerKey};
    }

    public List getColumns() {
        return DesignerSpace.getInstance().getUiElements(UiSearchGridColumn).values().findAll {it.componentId == _designerKey};
    }
    public List getRowMenuItems() {
        return DesignerSpace.getInstance().getUiElements(UiMenuItem).values().findAll {it.componentId == _designerKey && it.type == "component"};
    }
     public List getMultiSelectionMenuItems() {
        return DesignerSpace.getInstance().getUiElements(UiMenuItem).values().findAll {it.componentId == _designerKey && it.type == "multiple"};
    }
}