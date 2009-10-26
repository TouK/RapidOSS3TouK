package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.util.DesignerTemplateUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 20, 2009
* Time: 6:21:38 PM
*/
class UiTab extends UiElmnt {
    String name = "";
    String title = "";
    String contentFile = "";
    String webPageId = "";

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
                        webPageId: [isVisible: false, validators: [key: true]],
                        name: [descr: 'Name of the tab', validators: [key: true]],
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
                                                [designerType: "SearchGrid", isMultiple: true],
                                                [designerType: "SearchList", isMultiple: true],
                                                [designerType: "TreeGrid", isMultiple: true],
                                                [designerType: "Html", isMultiple: true],
                                                [designerType: "FlexPieChart", isMultiple: true],
                                                [designerType: "FlexLineChart", isMultiple: true],
                                                [designerType: "PieChart", isMultiple: true],
                                                [designerType: "ObjectMap", isMultiple: true],
                                                [designerType: "Autocomplete", isMultiple: true],
                                                [designerType: "AudioPlayer", isMultiple: true],
                                                [designerType: "GMap", isMultiple: true],
                                                [designerType: "Timeline", isMultiple: true]
                                        ]
                                ]
                        ],
                        [designerType: "Layout"],
                        [designerType: "Dialogs",
                                metaData: [
                                        help: "Dialogs.html",
                                        designerType: "Dialogs",
                                        display: "Dialogs",
                                        imageExpanded: 'images/rapidjs/component/tools/folder_open.gif',
                                        imageCollapsed: 'images/rapidjs/component/tools/folder.gif',
                                        childrenConfiguration: [
                                                [designerType: "Dialog", isMultiple: true]
                                        ]
                                ]
                        ],
                        [designerType: "Actions",
                                metaData: [
                                        help: "Actions.html",
                                        designerType: "Actions",
                                        display: "Actions",
                                        imageExpanded: 'images/rapidjs/component/tools/folder_open.gif',
                                        imageCollapsed: 'images/rapidjs/component/tools/folder.gif',
                                        childrenConfiguration: [
                                                [designerType: "FunctionAction", isMultiple: true],
                                                [designerType: "MergeAction", isMultiple: true],
                                                [designerType: "RequestAction", isMultiple: true],
                                                [designerType: "LinkAction", isMultiple: true]
                                        ]
                                ]
                        ],
                ]
        ];
        return metaData;
    }

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement) {
        def props = [webPageId: parentElement._designerKey];
        props.putAll(xmlNode.attributes());
        UiTab tab = DesignerSpace.getInstance().addUiElement(UiTab, props);
        UiLayout layout = DesignerSpace.getInstance().addUiElement(UiLayout, [tabId: tab._designerKey]);

        def layoutNode = xmlNode.UiElement.find {it.@designerType == 'Layout'}
        def componentsNode = xmlNode.UiElement.find {it.@designerType == 'Components'}
        def dialogsNode = xmlNode.UiElement.find {it.@designerType == 'Dialogs'}
        def actionsNode = xmlNode.UiElement.find {it.@designerType == 'Actions'}

        componentsNode.UiElement.each {componentNode ->
            def designerType = componentNode.@designerType.text()
            def uiElementClass = DesignerSpace.getInstance().getUiClass("${DesignerSpace.PACKAGE_NAME}.Ui${designerType}");
            if (uiElementClass) {
                uiElementClass.addUiElement(componentNode, tab)
            }
        }
        def actionAddOrder = [];
        def actionsMap = [:]
        actionsNode.UiElement.each {actionNode ->
            actionsMap.put(actionNode.@name.toString(), actionNode);
        }
        def iterationCount = 0
        if (!actionsMap.isEmpty())
        {
            while (iterationCount < 10000) {
                iterationCount++;
                actionsMap.each {actionName, actionNode ->
                    if (!actionAddOrder.contains(actionName)) {
                        def triggerNodes = actionNode.UiElement.UiElement.findAll {it.@designerType == "ActionTrigger" && it.@type == UiActionTrigger.ACTION_TYPE}
                        if (triggerNodes.size() > 0) {
                            def willAddAction = true;
                            def triggeringActions = triggerNodes.@triggeringAction;
                            for (int i = 0; i < triggeringActions.size(); i++)
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
        if (actionAddOrder.size() < actionsMap.keySet().size()) {
            throw new Exception("Cannot save successfully because action configuration has circular references.")
        }
        actionAddOrder.each {
            def actionNode = actionsMap[it];
            def designerType = actionNode.@designerType.text()
            def uiElementClass = DesignerSpace.getInstance().getUiClass("${DesignerSpace.PACKAGE_NAME}.Ui${designerType}");
            if (uiElementClass) {
                uiElementClass.addUiElement(actionNode, tab)
            }
        }
        dialogsNode.UiElement.each {dialogNode ->
            UiDialog.addUiElement(dialogNode, tab);
        }
        if (layoutNode.size() != 0)
        {
            def layoutUnitsNode = layoutNode.UiElement;
            layoutUnitsNode.each {layoutUnitNode ->
                def layoutUnit = UiLayoutUnit.addUiElement(layoutUnitNode, layout);
            }
        }

        return tab;
    }

    public UiLayout getLayout() {
        def layouts = DesignerSpace.getInstance().getUiElements(UiLayout).values().findAll {it.tabId == _designerKey};
        if (layouts.size() > 0) {
            return layouts[0]
        }
        return null;
    }

    public UiWebPage getPage() {
        def pages = DesignerSpace.getInstance().getUiElements(UiWebPage).values().findAll {it._designerKey == webPageId};
        if (pages.size() > 0) {
            return pages[0]
        }
        return null;
    }

    def getGlobalActionTrigers()
    {
        def actionTriggers = [:];
        def triggers = DesignerSpace.getInstance().getUiElements(UiActionTrigger).values().findAll {it.type == UiActionTrigger.GLOBAL_TYPE};
        triggers.each {UiActionTrigger actionTrigger ->
            if (actionTrigger.getAction().getTab().name == name)
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

    public List getComponents() {
        return DesignerSpace.getInstance().getUiElements(UiComponent).values().findAll {it.tabId == _designerKey};
    }
    public List getActions() {
        return DesignerSpace.getInstance().getUiElements(UiAction).values().findAll {it.tabId == _designerKey};
    }
    public List getDialogs() {
        return DesignerSpace.getInstance().getUiElements(UiDialog).values().findAll {it.tabId == _designerKey};
    }
}