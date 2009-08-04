package ui.designer;
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rui.util.DesignerUtils
import com.ifountain.rui.util.DesignerTemplateUtils

public class UiTabOperations extends AbstractDomainOperation
{
    public static Map metaData()
    {
        Map metaData = [
                help: "Tab.html",
                designerType: "Tab",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: 'images/rapidjs/designer/tab.png',
                imageCollapsed: 'images/rapidjs/designer/tab.png',
                propertyConfiguration: [
                        name: [descr: 'Name of the tab'],
                        title: [descr: "Title of tab", required: true],
                        contentFile: [descr: 'The file path relative to web-app that will be embedded to tab, where you can write free form HTML, JavaScript and CSS']
                ],
                childrenConfiguration:
                [
                        [designerType: "Components",
                                metaData: [
                                        help: "Components.html",
                                        designerType: "Components",
                                        display: "Components",
                                        imageExpanded: 'images/rapidjs/component/tools/folder_open.gif',
                                        imageCollapsed: 'images/rapidjs/component/tools/folder.gif',
                                        childrenConfiguration: [
                                                [designerType: "SearchGrid", propertyName: "components", isMultiple: true, isVisible: {component -> return component.class.simpleName == "UiSearchGrid"}],
                                                [designerType: "SearchList", propertyName: "components", isMultiple: true, isVisible: {component -> return component.class.simpleName == "UiSearchList"}],
                                                [designerType: "TreeGrid", propertyName: "components", isMultiple: true, isVisible: {component -> return component.class.simpleName == "UiTreeGrid"}],
                                                [designerType: "Html", propertyName: "components", isMultiple: true, isVisible: {component -> return component.class.simpleName == "UiHtml"}],
                                                [designerType: "FlexPieChart", propertyName: "components", isMultiple: true, isVisible: {component -> return component.class.simpleName == "UiFlexPieChart"}],
                                                [designerType: "FlexLineChart", propertyName: "components", isMultiple: true, isVisible: {component -> return component.class.simpleName == "UiFlexLineChart"}],
                                                [designerType: "PieChart", propertyName: "components", isMultiple: true, isVisible: {component -> return component.class.simpleName == "UiPieChart"}],
                                                [designerType: "ObjectMap", propertyName: "components", isMultiple: true, isVisible: {component -> return component.class.simpleName == "UiObjectMap"}],
                                                [designerType: "Autocomplete", propertyName: "components", isMultiple: true, isVisible: {component -> return component.class.simpleName == "UiAutocomplete"}],
                                                [designerType: "AudioPlayer", propertyName: "components", isMultiple: true, isVisible: {component -> return component.class.simpleName == "UiAudioPlayer"}],
                                                [designerType: "GMap", propertyName: "components", isMultiple: true, isVisible: {component -> return component.class.simpleName == "UiGMap"}],
                                                [designerType: "Timeline", propertyName: "components", isMultiple: true, isVisible: {component -> return component.class.simpleName == "UiTimeline"}]
                                        ]
                                ],
                                propertyName: "layout"
                        ],
                        [designerType: "Layout", propertyName: "layout"],
                        [designerType: "Dialogs",
                                metaData: [
                                        help: "Dialogs.html",
                                        designerType: "Dialogs",
                                        display: "Dialogs",
                                        imageExpanded: 'images/rapidjs/component/tools/folder_open.gif',
                                        imageCollapsed: 'images/rapidjs/component/tools/folder.gif',
                                        childrenConfiguration: [
                                                [designerType: "Dialog", isMultiple: true, propertyName: "dialogs"]
                                        ]
                                ],
                                propertyName: "layout"
                        ],
                        [designerType: "Actions",
                                metaData: [
                                        help: "Actions.html",
                                        designerType: "Actions",
                                        display: "Actions",
                                        imageExpanded: 'images/rapidjs/component/tools/folder_open.gif',
                                        imageCollapsed: 'images/rapidjs/component/tools/folder.gif',
                                        childrenConfiguration: [
                                                [designerType: "FunctionAction", isMultiple: true, propertyName: "actions", isVisible: {component -> return component.class.simpleName == "UiFunctionAction"}],
                                                [designerType: "MergeAction", isMultiple: true, propertyName: "actions", isVisible: {component -> return component.class.simpleName == "UiMergeAction"}],
                                                [designerType: "RequestAction", isMultiple: true, propertyName: "actions", isVisible: {component -> return component.class.simpleName == "UiRequestAction"}],
                                                [designerType: "LinkAction", isMultiple: true, propertyName: "actions", isVisible: {component -> return component.class.simpleName == "UiLinkAction"}]
                                        ]
                                ],
                                propertyName: "layout"
                        ],
                ]
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = [:]
        attributes.putAll(xmlNode.attributes());
        attributes.webPage = parentElement;
        attributes.webPageId = parentElement.id;
        def uiLayout = UiLayout.add([:]);
        attributes.layout = uiLayout;

        def uiTab = DesignerUtils.addUiObject(UiTab, attributes, xmlNode);
        def layoutNode = xmlNode.UiElement.find {it.@designerType == 'Layout'}
        def componentsNode = xmlNode.UiElement.find {it.@designerType == 'Components'}
        def dialogsNode = xmlNode.UiElement.find {it.@designerType == 'Dialogs'}
        def actionsNode = xmlNode.UiElement.find {it.@designerType == 'Actions'}
        componentsNode.UiElement.each {componentNode ->
            def designerType = componentNode.@designerType.text()
            def domainClass = ApplicationHolder.application.getDomainClass("ui.designer.Ui" + designerType);
            def component = domainClass.clazz.addUiElement(componentNode, uiTab);
        }
        def actionAddOrder = [];
        def actionsMap = [:]
        actionsNode.UiElement.each {actionNode ->
            actionsMap.put(actionNode.@name.toString(), actionNode);
        }
        def iterationCount = 0
        if(!actionsMap.isEmpty())
        {
            while (iterationCount < 10000) {
                iterationCount++;
                actionsMap.each {actionName, actionNode ->
                    if (!actionAddOrder.contains(actionName)) {
                        def triggerNodes = actionNode.UiElement.UiElement.findAll {it.@designerType == "ActionTrigger" && it.@type == UiActionTrigger.ACTION_TYPE}
                        if (triggerNodes.size() > 0) {
                            def willAddAction = true;
                            def triggeringActions = triggerNodes.@triggeringAction;
                            for(int i=0; i < triggeringActions.size(); i++)
                            {
                                def triggeringActionName = triggeringActions[i].toString()
                                if (actionsMap[triggeringActionName] != null) {
                                    if (!actionAddOrder.contains(triggeringActionName)) {
                                        willAddAction = false;
                                        break;
                                    }
                                }
                                else {
                                    throw new Exception("Could not add ${actionName}, because triggeringAction ${triggeringActionName} does not exist");
                                }
                            }
                            if (willAddAction) {
                                actionAddOrder.add(actionName);
                            }
                        }
                        else {
                            actionAddOrder.add(actionName);
                        }
                    }
                }
            }
        }
        if(actionAddOrder.size() < actionsMap.keySet().size()){
            throw new Exception("Cannot save successfully because action configuration has circular references.")
        }
        actionAddOrder.each {
            def actionNode = actionsMap[it];
            def designerType = actionNode.@designerType.text()
            def domainClass = ApplicationHolder.application.getDomainClass("ui.designer.Ui"+ designerType);
            domainClass.clazz.'addUiElement'(actionNode, uiTab);
        }

        dialogsNode.UiElement.each {dialogNode ->
            UiDialog.addUiElement(dialogNode, uiTab);
        }
        if (layoutNode.size() != 0)
        {
            def layoutUnitsNode = layoutNode.UiElement;
            layoutUnitsNode.each {layoutUnitNode ->
                def layoutUnit = UiLayoutUnit.addUiElement(layoutUnitNode, uiLayout, uiTab);
            }
        }
        return uiTab;
    }

    def getGlobalActionTrigers()
    {
        def actionTriggers = [:];
        def triggers = UiActionTrigger.searchEvery("type:${UiActionTrigger.GLOBAL_TYPE.exactQuery()}");
        triggers.each {UiActionTrigger actionTrigger ->
            if (actionTrigger.action.tab.name == name)
            {
                def triggerArray = actionTriggers.get(actionTrigger.event);
                if (triggerArray == null) {
                    triggerArray = [];
                    actionTriggers.put(actionTrigger.event, triggerArray)
                }
                triggerArray.add(actionTrigger)
            }
        }
        return actionTriggers;
    }

    def getActionsString(actionTriggers) {
        return DesignerTemplateUtils.getActionsString(actionTriggers);
    }


    def getTabFilePath()
    {
        def page = webPage;
        return "${page.getUrlDirectory()}/${name}.gsp".toString()
    }

    def deleteTabFile(baseDir)
    {
        def tabFile = new File("${baseDir}/${getTabFilePath()}");
        tabFile.delete();
    }

}