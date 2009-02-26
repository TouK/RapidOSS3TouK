package ui.designer;
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

public class UiWebPageOperations extends AbstractDomainOperation
{
    public static Map metaData()
    {
        Map metaData = [
                help:"Web Page.html",
                designerType: "WebPage",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: 'images/rapidjs/designer/page_world.png',
                imageCollapsed: 'images/rapidjs/designer/page_world.png',
                propertyConfiguration: [
                        name: [descr: 'Url of the page set']
                ],
                childrenConfiguration: [
                        [
                                help:"Tabs.html",
                                designerType: "Tabs",
                                isMultiple: false,
                                metaData: [
                                        designerType: "Tabs",
                                        display: "Tabs",
                                        imageExpanded: 'images/rapidjs/component/tools/folder_open.gif',
                                        imageCollapsed: 'images/rapidjs/component/tools/folder.gif',
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
        def name = xmlNode.attributes().name
        def uiUrl = DesignerUtils.addUiObject(UiWebPage, [name:name], xmlNode);
        def addedTabs = [];
        def tabNodes = xmlNode.UiElement.UiElement;
        tabNodes.each{tabNode->
            addedTabs.add(UiTab.addUiElement(tabNode, uiUrl));
        }
        uiUrl.addRelation(tabs:addedTabs);
        return uiUrl; 
    }

}