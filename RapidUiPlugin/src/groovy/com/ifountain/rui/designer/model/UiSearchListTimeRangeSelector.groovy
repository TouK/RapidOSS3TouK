package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 3:44:54 PM
*/
class UiSearchListTimeRangeSelector extends UiElmnt {
    String url = "script/run/getTimeRangeData";
    String buttonConfigurationUrl = "script/run/getTimeRangeButtonConfiguration";
    String fromTimeProperty = "fromTime";
    String toTimeProperty = "toTime";
    String timeAxisLabelProperty = "timeAxisLabel";
    String stringFromTimeProperty = "stringFromTime";
    String stringToTimeProperty = "stringToTime";
    String valueProperties = "value";
    String tooltipProperty = "tooltip";
    String componentId = "";

    public static Map metaData()
    {
        Map metaData = [
                help: "TimeRangeSelector Component.html",
                designerType: "SearchListTimeRangeSelector",
                canBeDeleted: true,
                display: "TimeRangeSelector",
                imageExpanded: "images/rapidjs/designer/chart_pie.png",
                imageCollapsed: "images/rapidjs/designer/chart_pie.png",
                propertyConfiguration: [
                        componentId: [isVisible: false, validators: [blank: false, nullable: false]],
                        url: [descr: "The URL to be used for requests to the server to retrieve the data.", validators: [blank: false, nullable: false]],
                        buttonConfigurationUrl: [descr: "The URL to be used for requests to the server to retrieve the button configuration.", validators: [blank: false, nullable: false]],
                        fromTimeProperty: [descr: "name of the property which specify start of timestamp interval of data", validators: [blank: false, nullable: false]],
                        toTimeProperty: [descr: "name of the property which specify end of timestamp interval of data", validators: [blank: false, nullable: false]],
                        stringFromTimeProperty: [descr: "name of the property which string representation of start of timestamp interval", validators: [blank: false, nullable: false]],
                        stringToTimeProperty: [descr: "name of the property which string representation of end of timestamp interval", validators: [blank: false, nullable: false]],
                        timeAxisLabelProperty: [descr: "Name of the property which will be used by horizontal axis to categorize data.", validators: [blank: false, nullable: false]],
                        valueProperties: [descr: "Comma seperated list of property names which will be shown as bar chart.", validators: [blank: false, nullable: false]],
                        tooltipProperty: [descr: "Tooltip property", validators: [blank: false, nullable: false]]
                ],
                childrenConfiguration: []
        ];

        return metaData;
    }

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = [:];
        attributes.putAll(xmlNode.attributes())
        attributes.componentId = parentElement._designerKey;
        return DesignerSpace.getInstance().addUiElement(UiSearchListTimeRangeSelector, attributes);
    }
}