package com.ifountain.rui.designer.model
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jan 29, 2010
 * Time: 5:52:35 PM
 */
class UiExecuteJavascriptAction extends UiAction {
    String javascript = "";

    public static Map metaData()
    {
        Map metaData = [
                help: "ExecuteJavascriptAction.html",
                designerType: "ExecuteJavascriptAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/javascript.gif",
                imageCollapsed: "images/rapidjs/designer/javascript.gif",
                propertyConfiguration: [
                        javascript: [descr: "Javascript code to execute", validators: [blank: false, nullable: false], type: "Expression"]
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

}