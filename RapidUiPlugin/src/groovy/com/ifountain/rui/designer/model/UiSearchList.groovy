package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.UiElmnt

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 4:50:12 PM
*/
class UiSearchList extends UiComponent {
    String url = "search";
    String rootTag = "Objects";
    String contentPath = "Object";
    String keyAttribute = "id";
    String queryParameter = "query";
    String defaultFields = "";
    Boolean searchInEnabled = true;
    String extraPropertiesToRequest = ""
    Boolean bringAllProperties = true;
    Boolean multipleFieldSorting = true;
    Long showMax = 0;
    Long lineSize = 3;
    Long pollingInterval = 0;
    Long timeout = 30;
    Long maxRowsDisplayed = 100;
    String defaultQuery = "";
    String defaultSearchClass = "RsEvent"
    String searchClassesUrl = "script/run/getClassesForSearch?rootClass=RsEvent&format=xml"

    public static Map metaData()
    {
        Map metaData = [
                help: "SearchList Component.html",
                designerType: "SearchList",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/properties.gif",
                imageCollapsed: "images/rapidjs/designer/properties.gif",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data.", validators: [blank: false, nullable: false]],
                        rootTag: [descr: "The root node name of AJAX response which SearchGrid takes as starting point to get its data.", validators: [blank: false, nullable: false]],
                        contentPath: [descr: "The node names of AJAX response which will be used as row data.", validators: [blank: false, nullable: false]],
                        keyAttribute: [descr: "The attribute name of the row node which uniquely identifies the node.", validators: [blank: false, nullable: false]],
                        defaultSearchClass: [descr: "Default class that the search will be applied.", validators: [blank: false, nullable: false]],
                        pollingInterval: [descr: "Time delay between two server requests.", required: true],
                        queryParameter: [descr: "The url parameter to send the query to the server.", validators: [blank: false, nullable: false]],
                        searchInEnabled: [descr: "Determines if the query should be applied on only defaultSearchClass or should be selected among classes which are brought by searchClassesUrl.", validators: [blank: false, nullable: false]],
                        bringAllProperties: [descr: "When set to false SearchList requests its data with a parameter (\"propertyList\") to indicate that it needs only a set of properties, to decrease the size of the data coming from server."],
                        extraPropertiesToRequest: [descr: "Comma separated property names which will be added \"propertyList\" URL parameter. Active when bringAllProperties is set to false."],
                        multipleFieldSorting: [descr: "When set true component supports multiple field sorting"],
                        searchClassesUrl: [descr: "The url used for the request to the server to retrieve available search classes."],
                        defaultFields: [descr: "Properties list that will be shown when no field configuration is found for the row. Optional if showMax property is provided."],
                        showMax: [descr: "Maximum number of properties that will be displayed from the data. It overrides defaultFields and fields declarations."],
                        maxRowsDisplayed: [descr: "The maximum row count requested from server at every poll."],
                        defaultQuery: [descr: "The query appended to the all queries sent by SearchGrid."],
                        lineSize: [descr: "How many lines a row consists of."],
                        timeout: [descr: "The time interval in seconds to wait the server request completes successfully before aborting."]
                ],
                childrenConfiguration:
                [
                        [designerType: "SearchListTimeRangeSelector", isMultiple: false],
                        [
                                designerType: "SearchListFields",
                                isMultiple: false,
                                metaData: [
                                        help: "SearchList Fields.html",
                                        designerType: "SearchListFields",
                                        display: "Fields",
                                        imageExpanded: 'images/rapidjs/designer/textfield_rename.png',
                                        imageCollapsed: 'images/rapidjs/designer/textfield_rename.png',
                                        childrenConfiguration: [
                                                [designerType: "SearchListField", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchListImages",
                                isMultiple: false,
                                metaData: [
                                        help: "SearchList Images.html",
                                        designerType: "SearchListImages",
                                        display: "Images",
                                        imageExpanded: 'images/rapidjs/designer/images.png',
                                        imageCollapsed: 'images/rapidjs/designer/images.png',
                                        childrenConfiguration: [
                                                [designerType: "Image", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchListMenuItems",
                                isMultiple: false,
                                metaData: [
                                        help: "SearchList MenuItems.html",
                                        designerType: "SearchListMenuItems",
                                        display: "MenuItems",
                                        imageExpanded: 'images/rapidjs/designer/table_row_insert.png',
                                        imageCollapsed: 'images/rapidjs/designer/table_row_insert.png',
                                        childrenConfiguration: [
                                                [designerType: "MenuItem", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchListMultiSelectionMenuItems",
                                isMultiple: false,
                                metaData: [
                                        help: "SearchGrid MultiSelectionMenuItems.html",
                                        designerType: "SearchListMultiSelectionMenuItems",
                                        display: "MultiSelectionMenuItems",
                                        imageExpanded: 'images/rapidjs/designer/table_row_insert.png',
                                        imageCollapsed: 'images/rapidjs/designer/table_row_insert.png',
                                        childrenConfiguration: [
                                                [designerType: "MenuItem", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "SearchListPropertyMenuItems",
                                isMultiple: false,
                                metaData: [
                                        help: "SearchList PropertyMenuItems.html",
                                        designerType: "SearchListPropertyMenuItems",
                                        display: "PropertyMenuItems",
                                        imageExpanded: 'images/rapidjs/designer/text_indent.png',
                                        imageCollapsed: 'images/rapidjs/designer/text_indent.png',
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
            throw new Exception("Property searchClassesUrl should be provided if searchInEnabled is true for SearchList ${attributes.name}")
        }
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {
        node.children().each {
            if (it.@designerType.text() != "SearchListTimeRangeSelector") {
                it."${UIELEMENT_TAG}".each {child ->
                    create(child, this)
                }
                removeUnneccessaryAttributes(it);
            }
            else {
                create(it, this)
            }
        }
    }

    public List getFields() {
        return DesignerSpace.getInstance().getUiElements(UiSearchListField).values().findAll {it.componentId == _designerKey};
    }
    public List getImages() {
        return DesignerSpace.getInstance().getUiElements(UiImage).values().findAll {it.componentId == _designerKey};
    }
    public List getPropertyMenuItems() {
        return DesignerSpace.getInstance().getUiElements(UiMenuItem).values().findAll {it.componentId == _designerKey && it.type == "property"};
    }
    public List getMultiSelectionMenuItems() {
        return DesignerSpace.getInstance().getUiElements(UiMenuItem).values().findAll {it.componentId == _designerKey && it.type == "multiple"};
    }
    public List getSubComponents() {
        return DesignerSpace.getInstance().getUiElements(UiSearchListTimeRangeSelector).values().findAll {it.componentId == _designerKey};
    }

    public List getRowMenuItems() {
        return DesignerSpace.getInstance().getUiElements(UiMenuItem).values().findAll {it.componentId == _designerKey && it.type == "component"};
    }
}