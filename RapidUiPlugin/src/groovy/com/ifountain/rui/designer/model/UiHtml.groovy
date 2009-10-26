package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.UiElmnt
import com.ifountain.rui.designer.UiElmnt

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 21, 2009
* Time: 5:05:34 PM
*/
class UiHtml extends UiComponent {
    
    Long pollingInterval = 0;
    Long timeout = 30;
    String url = "";
    Boolean iframe = false;

    public static Map metaData()
    {
        Map metaData = [
                help:"HTML Component.html",
                designerType: "Html",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/html.png",
                imageCollapsed: "images/rapidjs/designer/html.png",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data."],
                        pollingInterval:[descr:"Time delay between two server requests."],
                        iframe:[descr:"Parameter to display the content in an iframe or embed it to Html component's body."],
                        timeout:[descr:"The time interval in seconds to wait the server request completes successfully before aborting."]
                ],
                childrenConfiguration: []
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
        def attributes = [:];
        attributes.putAll (xmlNode.attributes());
        attributes.tabId = parentElement._designerKey;
        return DesignerSpace.getInstance().addUiElement(UiHtml, attributes);
    }
}