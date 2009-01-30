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
                                    childrenConfiguration: [
                                    ]
                            ],
                            propertyName:"layout"
                    ],
                    [designerType:"Dialogs",
                            metaData:[
                                    designerType:"Dialogs",
                                    display:"Dialogs",
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
                                    childrenConfiguration: [
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
        def components = [];
        def dialogs = [];
        componentsNode.UiElement.each{componentNode->
            def designerType = componentNode.@designerType.text()
            def domainClass = ApplicationHolder.application.getDomainClass("ui.designer.Ui"+designerType);
            def component = domainClass.clazz.addUiElement(componentNode, uiTab);
            components.add(component);
        }

        dialogsNode.UiElement.each{dialogNode->
            def dialog = UiDialog.addUiElement(dialogNode, uiTab);
            dialogs.add(dialog);
        }
        if(layoutNode.size() != 0)
        {
            def layoutUnitsNode = layoutNode.UiElement;
            layoutUnitsNode.each{layoutUnitNode->
                def layoutUnit = UiLayoutUnit.addUiElement(layoutUnitNode, uiLayout);
            }
        }
        uiTab.addRelation(dialogs:dialogs, components:components);
        return uiTab;
    }

}