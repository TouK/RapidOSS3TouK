package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 20, 2009
* Time: 1:10:42 PM
*/
class UiWebPage extends UiElmnt {

    String name = "";

    public static Map metaData() {
        Map metaData = [
                help: "Web Page.html",
                designerType: "WebPage",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: 'images/rapidjs/designer/page_world.png',
                imageCollapsed: 'images/rapidjs/designer/page_world.png',
                propertyConfiguration: [
                        name: [descr: 'Url of the page set', validators: [key: true, matches: "[a-z_A-z]\\w*"]]
                ],
                childrenConfiguration: [
                        [
                                designerType: "Tabs",
                                isMultiple: false,
                                metaData: [
                                        help: "Tabs.html",
                                        designerType: "Tabs",
                                        display: "Tabs",
                                        imageExpanded: 'images/rapidjs/component/tools/folder_open.gif',
                                        imageCollapsed: 'images/rapidjs/component/tools/folder.gif',
                                        childrenConfiguration: [
                                                [designerType: "Tab", isMultiple: true]
                                        ]
                                ]
                        ]
                ]
        ];
        return metaData;
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {
        def tabsNode = node."${UIELEMENT_TAG}"[0]
        UiElmnt.removeUnneccessaryAttributes(tabsNode)
        def tabNodes = tabsNode."${UIELEMENT_TAG}";
        tabNodes.each {tabNode ->
            UiElmnt.create(tabNode, this)
        }
    }

    public Collection getTabs(){
        return DesignerSpace.getInstance().getUiElements(UiTab).values().findAll{it.webPageId == _designerKey};
    }
}