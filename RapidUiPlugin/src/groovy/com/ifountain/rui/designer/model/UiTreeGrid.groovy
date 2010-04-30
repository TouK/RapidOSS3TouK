package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 4:24:18 PM
*/
class UiTreeGrid extends UiComponent {
    String url = "";
    String rootTag = "";
    String contentPath = "";
    String keyAttribute = "";
    String expandAttribute = "expanded";
    Long pollingInterval = 0;
    Long timeout = 30;
    Boolean expanded = false;
    Boolean tooltip = false;

    public static Map metaData()
    {
        Map metaData = [
                help: "TreeGrid Component.html",
                designerType: "TreeGrid",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/chart_organisation.png",
                imageCollapsed: "images/rapidjs/designer/chart_organisation.png",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data.", validators: [blank: false, nullable: false]],
                        rootTag: [descr: "The root node name of AJAX response which SearchGrid takes as starting point to get its data.", validators: [blank: false, nullable: false]],
                        contentPath: [descr: "The node names of AJAX response which will be used as row data.", validators: [blank: false, nullable: false]],
                        keyAttribute: [descr: "The attribute name of the row node which uniquely identifies the node.", validators: [blank: false, nullable: false]],
                        expandAttribute: [descr: "The attribute name of the row node which shows current row as expanded."],
                        pollingInterval: [descr: "Time delay between two server requests.", required: true],
                        expanded: [descr: "Parameter to display TreeGrid branches either expanded or collapsed"],
                        tooltip: [descr: "Parameter to display a tooltip over rows."],
                        timeout: [descr: "The time interval in seconds to wait the server request completes successfully before aborting."]
                ],
                childrenConfiguration:
                [
                        [
                                designerType: "TreeGridColumns",
                                isMultiple: false,
                                metaData: [
                                        designerType: "TreeGridColumns",
                                        display: "Columns",
                                        help: "TreeGrid Columns.html",
                                        imageExpanded: 'images/rapidjs/designer/text_columns.png',
                                        imageCollapsed: 'images/rapidjs/designer/text_columns.png',
                                        childrenConfiguration: [
                                                [designerType: "TreeGridColumn", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "TreeGridRootImages",
                                isMultiple: false,
                                metaData: [
                                        designerType: "TreeGridRootImages",
                                        help: "TreeGrid RootImages.html",
                                        display: "RootImages",
                                        imageExpanded: 'images/rapidjs/designer/images.png',
                                        imageCollapsed: 'images/rapidjs/designer/images.png',
                                        childrenConfiguration: [
                                                [designerType: "RootImage", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "TreeGridMenuItems",
                                isMultiple: false,
                                metaData: [
                                        designerType: "TreeGridMenuItems",
                                        display: "MenuItems",
                                        help: "TreeGrid MenuItems.html",
                                        imageExpanded: 'images/rapidjs/designer/table_row_insert.png',
                                        imageCollapsed: 'images/rapidjs/designer/table_row_insert.png',
                                        childrenConfiguration: [
                                                [designerType: "MenuItem", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "TreeGridMultiSelectionMenuItems",
                                isMultiple: false,
                                metaData: [
                                        help: "SearchGrid MultiSelectionMenuItems.html",
                                        designerType: "TreeGridMultiSelectionMenuItems",
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

    protected void addChildElements(GPathResult node, UiElmnt parent) {
        node."${UIELEMENT_TAG}".each{
            it."${UIELEMENT_TAG}".each{uiEl ->
                create(uiEl, this)    
            }
            removeUnneccessaryAttributes(it)
        }
    }
    public List getColumns() {
        return DesignerSpace.getInstance().getUiElements(UiTreeGridColumn).values().findAll {it.componentId == _designerKey};
    }
    public List getRootImages() {
        return DesignerSpace.getInstance().getUiElements(UiRootImage).values().findAll {it.componentId == _designerKey};
    }
    public List getRowMenuItems() {
        return DesignerSpace.getInstance().getUiElements(UiMenuItem).values().findAll {it.componentId == _designerKey && it.type == "component"};
    }
    public List getMultiSelectionMenuItems() {
        return DesignerSpace.getInstance().getUiElements(UiMenuItem).values().findAll {it.componentId == _designerKey && it.type == "multiple"};
    }

}