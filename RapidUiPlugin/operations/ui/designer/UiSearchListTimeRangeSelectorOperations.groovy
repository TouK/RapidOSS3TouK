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
                        timeProperty:[descr:"The property name of time values."],
                        valueProperties:[descr:"Comma seperated list of property names which will be shown as bar chart."]
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
        attributes.timeProperty = xmlNode.@timeProperty.text();
        if(parentElement instanceof UiSearchGrid)
        {
            attributes.grid = parentElement
        }
        else
        {
            attributes.list = parentElement            
        }
        return DesignerUtils.addUiObject(UiSearchListTimeRangeSelector, attributes, xmlNode);
    }
}