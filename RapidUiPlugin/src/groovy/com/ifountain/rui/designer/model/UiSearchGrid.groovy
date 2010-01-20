package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

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
    String defaultSearchClass = "RsEvent"
    String searchClassesUrl = "script/run/getClassesForSearch?rootClass=RsEvent&format=xml"
    String extraPropertiesToRequest = ""
    Long pollingInterval = 0;
    Long timeout = 30;
    Boolean searchInEnabled = true;
    Boolean queryEnabled = true;
    Boolean bringAllProperties = true;
    Boolean multipleFieldSorting = true;
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
                        pollingInterval: [descr: "Time delay between two server requests.", required: true],
                        defaultView: [descr: "The view which will be shown when the search grid is shown."],
                        queryParameter: [descr: "The url parameter to send the query to the server.", validators: [blank: false, nullable: false]],
                        searchInEnabled: [descr: "Determines if the query should be applied on only defaultSearchClass or should be selected among classes which are brought by searchClassesUrl.", validators: [blank: false, nullable: false]],
                        queryEnabled: [descr: "Parameter to determine whether the quick filtering is enabled or not."],
                        bringAllProperties: [descr: "When set to false SearchGrid requests its data with a parameter (\"propertyList\") to indicate that it needs only a set of properties, to decrease the size of the data coming from server."],
                        extraPropertiesToRequest: [descr: "Comma separated property names which will be added \"propertyList\" URL parameter. Active when bringAllProperties is set to false."],
                        multipleFieldSorting: [descr: "When set true component supports multiple field sorting"],
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
                                        help: "SearchGrid MultiSelectionMenuItems.html",
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

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        def attributes = node.attributes();
        if (attributes.searchInEnabled == "true" && attributes.searchClassesUrl == "") {
            throw new Exception("Property searchClassesUrl should be provided if searchInEnabled is true for SearchGrid ${attributes.name}")
        }
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {
        node.children().each {
            if (it.@designerType.text() != "SearchListTimeRangeSelector") {
                it."${UIELEMENT_TAG}".each{child ->
                    create(child, this)
                }
                removeUnneccessaryAttributes(it);
            }
            else{
                create(it, this)
            }
        }
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