package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 6, 2009
* Time: 8:40:32 AM
* To change this template use File | Settings | File Templates.
*/
class UiPieChartOperations extends UiComponentOperations
{
    public static Map metaData()
    {
        Map metaData = [
                designerType: "PieChart",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/chart_pie.png",
                imageCollapsed: "images/rapidjs/designer/chart_pie.png",
                propertyConfiguration: [
                        url:[descr:"The default URL to be used for requests to the server to retrieve the data."],
                        contentPath:[descr:"The node names of AJAX response which will be used as row data."],
                        categoryField:[descr:"The attribute in the data to define x series."],
                        dataField:[descr:"The attribute in the data to define y series."],
                        legend:[descr:"The position of the legend. Possible values are top, right, bottom, left and none."],
                        pollingInterval:[descr: "Time delay between two server requests.", required:true],
                        colors:[descr:"The color definition list of the slices. The order of the data is taken into consideration while matching the color to a category."]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiComponentOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = [:];
        attributes.putAll (xmlNode.attributes());
        attributes.tab = parentElement;
        return DesignerUtils.addUiObject(UiPieChart, attributes, xmlNode);
    }

}