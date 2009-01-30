package ui.designer;
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

public class UiUrlOperations extends AbstractDomainOperation
{
    public static Map metaData()
    {
        Map metaData = [
                designerType: "Url",
                canBeDeleted: true,
                displayFromProperty: "url",
                imageExpanded: 'images/rapidjs/designer/gsp_logo.png',
                imageCollapsed: 'images/rapidjs/designer/gsp_logo.png',
                propertyConfiguration: [
                        url: [descr: 'Url of the page set']
                ],
                childrenConfiguration: [
                        [
                                designerType: "Tabs",
                                isMultiple: false,
                                metaData: [
                                        designerType: "Tabs",
                                        display: "Tabs",
                                        imageExpanded: 'images/rapidjs/designer/tab.png',
                                        imageCollapsed: 'images/rapidjs/designer/tab.png',
                                        childrenConfiguration: [
                                                [designerType: "Tab", propertyName: "tabs", isMultiple: true]
                                        ]
                                ]
                        ]
                ]
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def url = xmlNode.attributes().url
        def uiUrl = DesignerUtils.addUiObject(UiUrl, [url:url], xmlNode);
        def addedTabs = [];
        def tabNodes = xmlNode.UiElement.UiElement;
        tabNodes.each{tabNode->
            addedTabs.add(UiTab.addUiElement(tabNode, uiUrl));
        }
        uiUrl.addRelation(tabs:addedTabs);
        return uiUrl; 
    }

}