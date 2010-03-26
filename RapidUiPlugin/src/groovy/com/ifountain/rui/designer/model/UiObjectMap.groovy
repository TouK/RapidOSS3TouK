package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 5:10:45 PM
*/
class UiObjectMap extends UiComponent {
    String expandURL = "script/run/expandMap"
    String dataURL = "script/run/getMapData"
    String nodePropertyList = "name,rsClassName";
    String mapPropertyList = "mapType";

    Long pollingInterval = 0;
    Long timeout = 30;
    Long nodeSize = 60;
    String edgeColors = "'5':0xffde2c26,'4':0xfff79229,'3':0xfffae500,'2':0xff20b4e0,'1':0xffac6bac,'0':0xff62b446,'default':0xff62b446"
    String edgeColorDataKey = "state";

    public static Map metaData()
    {
        Map metaData = [
                help: "ObjectMap Component.html",
                designerType: "ObjectMap",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/chart_line.png",
                imageCollapsed: "images/rapidjs/designer/chart_line.png",
                propertyConfiguration: [
                        expandURL: [descr: "The default URL to be used for requests to the server to retrieve map topology (nodes, edges, locations, and etc.)", validators: [blank: false, nullable: false]],
                        dataURL: [descr: "The default URL to be used for requests to the server to retrieve the data of each node and edges (state, cpu utilization, and etc.)", validators: [blank: false, nullable: false]],
                        nodePropertyList: [descr: "Comma seperated list of properties to identify a node.", validators: [blank: false, nullable: false]],
                        mapPropertyList: [descr: "Comma seperated list of properties to identify a map.", validators: [blank: false, nullable: false]],
                        nodeSize: [descr: "Height of the node (width is 1.5 times height)"],
                        pollingInterval: [descr: "Time delay between two server requests.", required: true],
                        edgeColorDataKey: [descr: "The attribute name of the edge node which uniquely identifies the edge color."],
                        edgeColors: [descr: "The edge color mapping"],
                        timeout: [descr: "The time interval in seconds to wait the server request completes successfully before aborting."]
                ],
                childrenConfiguration:
                [
                        [
                                designerType: "ObjectMapImageNodeContent",
                                isMultiple: false,
                                metaData: [
                                        help: "ObjectMap Images.html",
                                        designerType: "ObjectMapImageNodeContent",
                                        display: "Images",
                                        imageExpanded: 'images/rapidjs/designer/images.png',
                                        imageCollapsed: 'images/rapidjs/designer/images.png',
                                        childrenConfiguration: [
                                                [designerType: "ImageObjectMapContent", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "ObjectMapTextNodeContent",
                                isMultiple: false,
                                metaData: [
                                        help: "ObjectMap Texts.html",
                                        designerType: "ObjectMapTextNodeContent",
                                        display: "Texts",
                                        imageExpanded: 'images/rapidjs/designer/text_smallcaps.png',
                                        imageCollapsed: 'images/rapidjs/designer/text_smallcaps.png',
                                        childrenConfiguration: [
                                                [designerType: "ObjectMapContent", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "ObjectMapGaugeNodeContent",
                                isMultiple: false,
                                metaData: [
                                        help: "ObjectMap Gauges.html",
                                        designerType: "ObjectMapGaugeNodeContent",
                                        display: "Gauges",
                                        imageExpanded: 'images/rapidjs/designer/excellent.png',
                                        imageCollapsed: 'images/rapidjs/designer/excellent.png',
                                        childrenConfiguration: [
                                                [designerType: "ObjectMapContent", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "ObjectMapToolbarMenus",
                                isMultiple: false,
                                metaData: [
                                        help: "ObjectMap ToolBarMenus.html",
                                        designerType: "ObjectMapToolbarMenus",
                                        display: "ToolbarMenus",
                                        imageExpanded: 'images/rapidjs/designer/application_view_icons.png',
                                        imageCollapsed: 'images/rapidjs/designer/application_view_icons.png',
                                        childrenConfiguration: [
                                                [designerType: "ToolbarMenu", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "ObjectMapMenuItems",
                                isMultiple: false,
                                metaData: [
                                        help: "ObjectMap MenuItems.html",
                                        designerType: "ObjectMapMenuItems",
                                        display: "MenuItems",
                                        imageExpanded: 'images/rapidjs/designer/table_row_insert.png',
                                        imageCollapsed: 'images/rapidjs/designer/table_row_insert.png',
                                        childrenConfiguration: [
                                                [designerType: "MenuItem", isMultiple: true]
                                        ]
                                ]
                        ],
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
        node.children().each{
            it."${UIELEMENT_TAG}".each{child ->
               create(child, this) 
            }
        }
        node.children().each{
            removeUnneccessaryAttributes(it)
        }
    }


    def getNodeMenuItems() {
        def toolbarMenuItems = [];
        getToolbarMenus().each {UiToolbarMenu toolbarMenu ->
            toolbarMenuItems.addAll(toolbarMenu.menuItems.name);
        }
        def nodeMenuItems = [];
        getMenuItems().each {UiMenuItem menuItem ->
            if (!toolbarMenuItems.contains(menuItem.name)) {
                nodeMenuItems.add(menuItem)
            }
        }
        return nodeMenuItems;
    }
    public List getNodeContents() {
       return DesignerSpace.getInstance().getUiElements(UiObjectMapContent).values().findAll {it.componentId == _designerKey};
    }
    public List getToolbarMenus() {
         return DesignerSpace.getInstance().getUiElements(UiToolbarMenu).values().findAll {it.componentId == _designerKey};
    }
}