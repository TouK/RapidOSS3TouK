package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 27, 2009
* Time: 8:53:32 AM
* To change this template use File | Settings | File Templates.
*/
class UiAutocompleteOperations extends UiComponentOperations {
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
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data. "],
                        contentPath: [descr: "The node names of AJAX response which will be used as suggestion data."],
                        suggestionAttribute: [descr: "The attribute of suggestion data which will be displayed in suggestion box."],
                        submitButtonLabel: [descr: "Submit button text. Default is \"Search\"."],
                        cacheSize: [descr: "Max number of data stored in client side cache."],
                        animated: [descr: "Parameter to display suggestion box whether animated or not."],
                        timeout:[descr:"The time interval in seconds to wait the server request completes successfully before aborting."]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiComponentOperations.metaData();
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = [:];
        attributes.putAll(xmlNode.attributes());
        attributes.tab = parentElement;
        attributes.tabId = parentElement.id;
        return DesignerUtils.addUiObject(UiAutocomplete, attributes, xmlNode);
    }
}