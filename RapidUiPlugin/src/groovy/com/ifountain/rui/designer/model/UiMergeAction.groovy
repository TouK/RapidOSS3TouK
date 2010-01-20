package com.ifountain.rui.designer.model

import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.UiElmnt

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 2:18:18 PM
*/
class UiMergeAction extends UiRequestAction {
    String removeAttribute = "";

    public static Map metaData()
    {
        Map metaData = [
                help: "MergeAction.html",
                designerType: "MergeAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/connect_established.png",
                imageCollapsed: "images/rapidjs/designer/connect_established.png",
                propertyConfiguration: [
                        removeAttribute: [descr: "The attribute in response data which indicates that related components should remove the specified data"]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiRequestAction.metaData();
        def childConfiguration = [];
        childConfiguration.addAll(parentMetaData.childrenConfiguration)
        childConfiguration.addAll(metaData.childrenConfiguration)
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration = childConfiguration;
        return metaData;

    }

}