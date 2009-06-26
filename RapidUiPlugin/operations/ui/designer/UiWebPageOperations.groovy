package ui.designer;
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils
import org.apache.commons.io.FileUtils

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
                                designerType: "Tabs",
                                isMultiple: false,
                                metaData: [
                                        help:"Tabs.html",
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

    def getUrlLayoutFilePath()
    {
        return "grails-app/views/layouts/${name}Layout.gsp".toString()
    }

    def getHelpFilePath()
    {
        return "web-app/help/${name}Help.gsp".toString()
    }

    def getUrlFilePath()
    {
        return "web-app/${name}.gsp".toString()
    }
    def getUrlDirectory()
    {
        return "web-app/${name}".toString()
    }

    def deleteUrlFiles(baseDir)
    {
        def urlLayoutFile = new File("${baseDir}/${getUrlLayoutFilePath ()}");
        def urlRedirectFile = new File("${baseDir}/${getUrlFilePath()}");
        def tabsDir = new File("${baseDir}/${getUrlDirectory()}");
        urlRedirectFile.delete();
        urlLayoutFile.delete();
        if (tabsDir.exists())
        {
            FileUtils.deleteDirectory(tabsDir)
        }
    }

}