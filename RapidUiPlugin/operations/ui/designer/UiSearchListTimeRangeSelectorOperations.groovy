package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Sep 4, 2009
* Time: 2:59:50 PM
* To change this template use File | Settings | File Templates.
*/
class UiSearchListTimeRangeSelectorOperations extends AbstractDomainOperation{
    public static Map metaData()
    {
        Map metaData = [
                help:"TimeRangeSelector Component.html",
                designerType: "SearchListTimeRangeSelector",
                canBeDeleted: true,
                display: "TimeRangeSelector",
                imageExpanded: "images/rapidjs/designer/chart_pie.png",
                imageCollapsed: "images/rapidjs/designer/chart_pie.png",
                propertyConfiguration: [
                        url:[descr:"The URL to be used for requests to the server to retrieve the data."],
                        buttonConfigurationUrl:[descr:"The URL to be used for requests to the server to retrieve the button configuration."],
                        fromTimeProperty:[descr:"name of the property which specify start of timestamp interval of data"],
                        toTimeProperty:[descr:"name of the property which specify end of timestamp interval of data"],
                        stringFromTimeProperty:[descr:"name of the property which string representation of start of timestamp interval"],
                        stringToTimeProperty:[descr:"name of the property which string representation of end of timestamp interval"],
                        timeAxisLabelProperty:[descr:"Name of the property which will be used by horizontal axis to categorize data."],
                        valueProperties:[descr:"Comma seperated list of property names which will be shown as bar chart."],
                        tooltipProperty:[descr:"Tooltip property"]
                ],
                childrenConfiguration: []
        ];

        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = [:];
        attributes.url = xmlNode.@url.text();
        attributes.buttonConfigurationUrl = xmlNode.@buttonConfigurationUrl.text();
        attributes.valueProperties = xmlNode.@valueProperties.text();
        attributes.fromTimeProperty = xmlNode.@fromTimeProperty.text();
        attributes.tooltipProperty = xmlNode.@tooltipProperty.text();
        attributes.toTimeProperty = xmlNode.@toTimeProperty.text();
        attributes.stringFromTimeProperty = xmlNode.@stringFromTimeProperty.text();
        attributes.stringToTimeProperty = xmlNode.@stringToTimeProperty.text();
        attributes.timeAxisLabelProperty = xmlNode.@timeAxisLabelProperty.text();
        if(parentElement instanceof UiSearchGrid)
        {
            attributes.searchGrid = parentElement
        }
        else
        {
            attributes.searchList = parentElement            
        }
        return DesignerUtils.addUiObject(UiSearchListTimeRangeSelector, attributes, xmlNode);
    }
}