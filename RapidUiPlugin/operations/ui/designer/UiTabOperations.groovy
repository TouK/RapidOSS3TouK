package ui.designer;
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rui.util.DesignerUtils

public class UiTabOperations extends AbstractDomainOperation
{
    public static Map metaData()
    {
        Map metaData = [
                designerType: "Tab",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: 'images/rapidjs/designer/page.png',
                imageCollapsed: 'images/rapidjs/designer/page.png',
                propertyConfiguration: [
                        name: [descr: 'Name of the tab'],
                        javascriptFile: [descr: 'The file path relative to web-app that will be embedded to tab, where you can write free form JavaScript']
                ],
                childrenConfiguration:
                [
                    [designerType:"Layout", propertyName:"layout"],
                    [designerType:"Components",
                            metaData:[
                                    designerType:"Components",
                                    display:"Components",
                                    imageExpanded: 'images/rapidjs/component/tools/folder_open.gif',
                                    imageCollapsed: 'images/rapidjs/component/tools/folder.gif',
                                    childrenConfiguration: [
                                        [designerType:"FlexPieChart", propertyName:"components", isMultiple:true, isVisible:{component-> return component.class.simpleName == "UiFlexPieChart"}]
                                        ,[designerType:"SearchList", propertyName:"components", isMultiple:true, isVisible:{component-> return component.class.simpleName == "UiSearchList"}]
                                        ,[designerType:"SearchGrid", propertyName:"components", isMultiple:true, isVisible:{component-> return component.class.simpleName == "UiSearchGrid"}]
                                        ,[designerType:"TreeGrid", propertyName:"components", isMultiple:true, isVisible:{component-> return component.class.simpleName == "UiTreeGrid"}]
                                        ,[designerType:"Timeline", propertyName:"components", isMultiple:true, isVisible:{component-> return component.class.simpleName == "UiTimeline"}]
                                        ,[designerType:"PieChart", propertyName:"components", isMultiple:true, isVisible:{component-> return component.class.simpleName == "UiPieChart"}]
                                        ,[designerType:"GMap", propertyName:"components", isMultiple:true, isVisible:{component-> return component.class.simpleName == "UiGMap"}]
                                        ,[designerType:"Html", propertyName:"components", isMultiple:true, isVisible:{component-> return component.class.simpleName == "UiHtml"}]
                                    ]
                            ],
                            propertyName:"layout"
                    ],
                    [designerType:"Dialogs",
                            metaData:[
                                    designerType:"Dialogs",
                                    display:"Dialogs",
                                    imageExpanded: 'images/rapidjs/component/tools/folder_open.gif',
                                    imageCollapsed: 'images/rapidjs/component/tools/folder.gif',
                                    childrenConfiguration: [
                                        [designerType:"Dialog", isMultiple:true, propertyName:"dialogs"]
                                    ]
                            ],
                            propertyName:"layout"
                    ],
                    [designerType:"Actions",
                            metaData:[
                                    designerType:"Actions",
                                    display:"Actions",
                                    imageExpanded: 'images/rapidjs/component/tools/folder_open.gif',
                                    imageCollapsed: 'images/rapidjs/component/tools/folder.gif',
                                    childrenConfiguration: [
                                        [designerType:"FunctionAction", isMultiple:true, propertyName:"actions", isVisible:{component-> return component.class.simpleName == "UiFunctionAction"}],
                                        [designerType:"MergeAction", isMultiple:true, propertyName:"actions", isVisible:{component-> return component.class.simpleName == "UiMergeAction"}],
                                        [designerType:"RequestAction", isMultiple:true, propertyName:"actions", isVisible:{component-> return component.class.simpleName == "UiRequestAction"}],
                                        [designerType:"CombinedAction", isMultiple:true, propertyName:"actions", isVisible:{component-> return component.class.simpleName == "UiCombinedAction"}],
                                        [designerType:"LinkAction", isMultiple:true, propertyName:"actions", isVisible:{component-> return component.class.simpleName == "UiLinkAction"}]
                                    ]
                            ],
                            propertyName:"layout"
                    ],
                ]
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = [:]
        attributes.putAll(xmlNode.attributes());
        attributes.url = parentElement;
        def uiLayout = UiLayout.add([:]);
        attributes.layout = uiLayout;

        def uiTab = DesignerUtils.addUiObject(UiTab, attributes, xmlNode);
        def layoutNode = xmlNode.UiElement.find{ it.@designerType == 'Layout' }
        def componentsNode = xmlNode.UiElement.find{ it.@designerType == 'Components' }
        def dialogsNode = xmlNode.UiElement.find{ it.@designerType == 'Dialogs' }
        def actionsNode = xmlNode.UiElement.find{ it.@designerType == 'Actions' }
        componentsNode.UiElement.each{componentNode->
            def designerType = componentNode.@designerType.text()
            def domainClass = ApplicationHolder.application.getDomainClass("ui.designer.Ui"+designerType);
            def component = domainClass.clazz.addUiElement(componentNode, uiTab);
        }

        actionsNode.UiElement.each{actionNode->
            if(actionNode.'@designerType'.text() != "CombinedAction")
            {
                def designerType = actionNode.@designerType.text()
                def domainClass = ApplicationHolder.application.getDomainClass("ui.designer.Ui"+designerType);
                domainClass.clazz.'addUiElement'(actionNode, uiTab);
            }
        }

        actionsNode.UiElement.each{actionNode->
            if(actionNode.'@designerType'.text() == "CombinedAction")
            {
                UiCombinedAction.addUiElement(actionNode, uiTab);
            }
        }

        dialogsNode.UiElement.each{dialogNode->
            UiDialog.addUiElement(dialogNode, uiTab);
        }
        if(layoutNode.size() != 0)
        {
            def layoutUnitsNode = layoutNode.UiElement;
            layoutUnitsNode.each{layoutUnitNode->
                def layoutUnit = UiLayoutUnit.addUiElement(layoutUnitNode, uiLayout);
            }
        }
        return uiTab;
    }

}