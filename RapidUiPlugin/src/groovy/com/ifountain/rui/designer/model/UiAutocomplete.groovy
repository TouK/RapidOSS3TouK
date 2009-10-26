package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 2:57:57 PM
*/
class UiAutocomplete extends UiComponent{
    String contentPath = "";
    String url = "";
    String suggestionAttribute = "";
    String submitButtonLabel = "Search";
    Long cacheSize = 0;
    Long timeout = 0;
    Boolean animated = false;

    public static Map metaData()
    {
        Map metaData = [
                help: "Autocomplete Component.html",
                designerType: "Autocomplete",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/autocomplete.png",
                imageCollapsed: "images/rapidjs/designer/autocomplete.png",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data. ", validators:[blank:false, nullable:false]],
                        contentPath: [descr: "The node names of AJAX response which will be used as suggestion data.", validators:[blank:false, nullable:false]],
                        suggestionAttribute: [descr: "The attribute of suggestion data which will be displayed in suggestion box.", validators:[blank:false, nullable:false]],
                        submitButtonLabel: [descr: "Submit button text. Default is \"Search\"."],
                        cacheSize: [descr: "Max number of data stored in client side cache."],
                        animated: [descr: "Parameter to display suggestion box whether animated or not."],
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
        attributes.putAll(xmlNode.attributes());
        attributes.tabId = parentElement._designerKey;
        return DesignerSpace.getInstance().addUiElement(UiAutocomplete, attributes);
    }
}