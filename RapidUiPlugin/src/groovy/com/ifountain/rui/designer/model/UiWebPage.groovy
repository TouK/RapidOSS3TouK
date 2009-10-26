package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt

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

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement) {
        def webPage = DesignerSpace.getInstance().addUiElement(UiWebPage, xmlNode.attributes());
        def addedTabs = [];
        def tabNodes = xmlNode.UiElement.UiElement;
        tabNodes.each {tabNode ->
            UiTab.addUiElement(tabNode, webPage);
        }
        return webPage;
    }

    public Collection getTabs(){
        return DesignerSpace.getInstance().getUiElements(UiTab).values().findAll{it.webPageId == _designerKey};
    }
}