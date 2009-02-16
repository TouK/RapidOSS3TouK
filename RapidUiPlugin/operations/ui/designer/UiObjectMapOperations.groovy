package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 6, 2009
* Time: 9:53:30 AM
* To change this template use File | Settings | File Templates.
*/
class UiObjectMapOperations extends UiComponentOperations {
    public static Map metaData()
    {
        Map metaData = [
                designerType: "ObjectMap",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        expandURL: [descr: "The default URL to be used for requests to the server to retrieve map topology (nodes, edges, locations, and etc.)"],
                        dataURL: [descr: "The default URL to be used for requests to the server to retrieve the data of each node and edges (state, cpu utilization, and etc.)"],
                        nodeSize: [descr: "Height of the node (width is 1.5 times height)"],
                        dataTag: [descr: "The node name of AJAX response which will be used as node data"],
                        pollingInterval: [descr: "Time delay between two server requests.", required:true],
                        edgeColorDataKey: [descr: "The attribute name of the edge node which uniquely identifies the edge color."],
                        edgeColors: [descr: "The edge color mapping"]
                ],
                childrenConfiguration:
                [
                        [
                                designerType: "ObjectMapImageNodeContent",
                                isMultiple: false,
                                metaData: [
                                        designerType: "ObjectMapImageNodeContent",
                                        display: "Images",
                                        imageExpanded: 'images/rapidjs/designer/images.png',
                                        imageCollapsed: 'images/rapidjs/designer/images.png',
                                        childrenConfiguration: [
                                                [designerType: "ImageObjectMapContent", propertyName: "nodeContents", isMultiple: true, isVisible:{component->component.type == "image"}]
                                        ]
                                ]
                        ],
                        [
                                designerType: "ObjectMapTextNodeContent",
                                isMultiple: false,
                                metaData: [
                                        designerType: "ObjectMapTextNodeContent",
                                        display: "Texts",
                                        imageExpanded: 'images/rapidjs/designer/tab.png',
                                        imageCollapsed: 'images/rapidjs/designer/tab.png',
                                        childrenConfiguration: [
                                                [designerType: "ObjectMapContent", propertyName: "nodeContents", isMultiple: true, isVisible:{component->component.type == "text"}]
                                        ]
                                ]
                        ],
                        [
                                designerType: "ObjectMapGaugeNodeContent",
                                isMultiple: false,
                                metaData: [
                                        designerType: "ObjectMapGaugeNodeContent",
                                        display: "Gauges",
                                        imageExpanded: 'images/rapidjs/designer/tab.png',
                                        imageCollapsed: 'images/rapidjs/designer/tab.png',
                                        childrenConfiguration: [
                                                [designerType: "ObjectMapContent", propertyName: "nodeContents", isMultiple: true, isVisible:{component->component.type == "gauge"}]
                                        ]
                                ]
                        ],
                        [
                                designerType: "ObjectMapToolbarMenus",
                                isMultiple: false,
                                metaData: [
                                        designerType: "ObjectMapToolbarMenus",
                                        display: "ToolbarMenus",
                                        imageExpanded: 'images/rapidjs/designer/tab.png',
                                        imageCollapsed: 'images/rapidjs/designer/tab.png',
                                        childrenConfiguration: [
                                                [designerType: "ToolbarMenu", propertyName: "toolbarMenus", isMultiple: true]
                                        ]
                                ]
                        ],
                        [
                                designerType: "ObjectMapMenuItems",
                                isMultiple: false,
                                metaData: [
                                        designerType: "ObjectMapMenuItems",
                                        display: "MenuItems",
                                        imageExpanded: 'images/rapidjs/designer/tab.png',
                                        imageCollapsed: 'images/rapidjs/designer/tab.png',
                                        childrenConfiguration: [
                                                [designerType: "MenuItem", propertyName: "menuItems", isMultiple: true, isVisible:{component-> return component.type == "component"}]
                                        ]
                                ]
                        ],
                ]
        ];
        def parentMetaData = UiComponentOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = [:];
        attributes.putAll (xmlNode.attributes());
        attributes.tab = parentElement;
        def addedMap = DesignerUtils.addUiObject(UiObjectMap, attributes, xmlNode);
        def textsNode = xmlNode.UiElement.find {it.@designerType.text() == "ObjectMapTextNodeContent"}
        def imagesNode = xmlNode.UiElement.find {it.@designerType.text() == "ObjectMapImageNodeContent"}
        def gaugesNode = xmlNode.UiElement.find {it.@designerType.text() == "ObjectMapGaugeNodeContent"}
        def toolbarsNode = xmlNode.UiElement.find {it.@designerType.text() == "ObjectMapToolbarMenus"}
        def menuItemsNode = xmlNode.UiElement.find {it.@designerType.text() == "ObjectMapMenuItems"}

        textsNode.UiElement.each{
            it.attributes().put("type", "text")
            UiObjectMapContent.addUiElement(it, addedMap);
        }
        imagesNode.UiElement.each{
            UiImageObjectMapContent.addUiElement(it, addedMap);
        }
        gaugesNode.UiElement.each{
            it.attributes().put("type", "gauge")
            UiObjectMapContent.addUiElement(it, addedMap);
        }
        toolbarsNode.UiElement.each{
            UiToolbarMenu.addUiElement(it, addedMap);
        }
        menuItemsNode.UiElement.each{
            UiMenuItem.addUiElement(it, addedMap);
        }
        return addedMap;
    }
}