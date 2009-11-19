package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.util.DesignerTemplateUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 20, 2009
* Time: 6:29:27 PM
*/
class UiLayoutUnit extends UiElmnt {
    String contentFile = "";
    String gutter = "0px";
    Boolean scroll = false;
    Boolean useShim = false;
    String parentLayoutId;
    String componentId;

    public static Map metaData()
    {
        Map metaData = [
                propertyConfiguration: [
                        component: [descr: "RapidInsight component that will be displayed in the unit", required: true],
                        contentFile: [descr: "Content file where the layout content will be retrieved."],
                        gutter: [descr: "The gutter applied to the unit's wrapper, before the content."],
                        scroll: [descr: "Boolean indicating whether the unit's body should have scroll bars if the body content is larger than the display area."],
                        useShim: [descr: "This setting will be passed to the DragDrop instances on the resize handles and for the draggable property. This property should be used if you want the resize handles to work over iframe and other elements"]
                ],
                childrenConfiguration: [
                        [designerType: "Layout", isMultiple: false]
                ]
        ];
        return metaData;
    }

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.parentLayoutId = parentElement._designerKey;
        def designerType = attributes.designerType;
        if (attributes.component != null && attributes.component != "")
        {
            UiComponent component = DesignerSpace.getInstance().getUiElement(UiComponent, "${parentElement.tabId}_${attributes.component}")
            if (!component) {
                throw new Exception("Could not find component ${attributes.component} for ${designerType}")
            }
            attributes.componentId = component._designerKey;
        }
        def layoutClass = DesignerSpace.getInstance().getUiClass("${DesignerSpace.PACKAGE_NAME}.Ui${designerType}")
        UiLayoutUnit layoutUnit = DesignerSpace.getInstance().addUiElement(layoutClass, attributes);
        def innerLayouts = xmlNode.UiElement;
        innerLayouts.each {innerLayoutNode ->
            def addedLayout = DesignerSpace.getInstance().addUiElement(UiLayout, [parentUnitId: layoutUnit._designerKey, tabId:parentElement.tabId])
            def innerLayoutUnitsNode = innerLayoutNode.UiElement;
            innerLayoutUnitsNode.each {innerLayoutUnitNode ->
                UiLayoutUnit.addUiElement(innerLayoutUnitNode, addedLayout);
            }
        }
        return layoutUnit;
    }

    public UiLayout getChildLayout() {
        def layouts = DesignerSpace.getInstance().getUiElements(UiLayout).values().findAll {it.parentUnitId == _designerKey};
        if (layouts.size() > 0) {
            return layouts[0]
        }
        return null;
    }

    public UiComponent getComponent() {
        def comps = DesignerSpace.getInstance().getUiElements(UiComponent).values().findAll {it._designerKey == componentId};
        if (comps.size() > 0) {
            return comps[0]
        }
        return null;
    }

    def getContentFileDivId()
    {
        return "loyutUnit${_designerKey}${DesignerTemplateUtils.getContentDivId(contentFile)}"
    }

    public static List getLayoutUnitHavingContentFile(UiLayout layout)
    {
        def layoutUnitList = [];
        if(layout)
        {
            _getLayoutUnitHavingContentFile(layout, layoutUnitList);
        }
        return layoutUnitList;
    }
    private static void _getLayoutUnitHavingContentFile(UiLayout layout, List layoutUnitList)
    {
        layout.units.each{UiLayoutUnit unit->
            def childLayout;
            if(unit.contentFile != null && unit.contentFile != "")
            {
                layoutUnitList.add(unit);
            }
            else if((childLayout = unit.childLayout) != null)
            {
                _getLayoutUnitHavingContentFile (childLayout, layoutUnitList);
            }
        }
    }

}