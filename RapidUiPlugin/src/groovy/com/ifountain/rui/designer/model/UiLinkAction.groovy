package com.ifountain.rui.designer.model

import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.UiElmnt
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 10:51:43 AM
*/
class UiLinkAction extends UiAction {
    public static String SELF = "self"
    public static String BLANK = "blank"

    String url;
    String target = SELF;

    public static Map metaData()
    {
        Map metaData = [
                help: "LinkAction.html",
                designerType: "LinkAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/link.png",
                imageCollapsed: "images/rapidjs/designer/link.png",
                propertyConfiguration: [
                        url: [descr: "JavaScript expression that will be evaluated to determine the URL to redirect to", type: "Expression", validators: [blank: false, nullable: false]],
                        target: [descr: "Specifies where to open the linked document", validators: [blank: false, nullable: false, inList: [SELF, BLANK]]]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiAction.metaData();
        def propConfig = [:]
        propConfig.put("tabId", parentMetaData.propertyConfiguration.remove("tabId"))
        propConfig.put("name", parentMetaData.propertyConfiguration.remove("name"))
        propConfig.putAll(metaData.propertyConfiguration)
        propConfig.putAll(parentMetaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;

    }

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.tabId = parentElement._designerKey;
        def addedAction = DesignerSpace.getInstance().addUiElement(UiLinkAction, attributes);
        addTriggers(xmlNode, addedAction);
        return addedAction;
    }
}