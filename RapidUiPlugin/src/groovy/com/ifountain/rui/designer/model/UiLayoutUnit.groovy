package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt
import com.ifountain.rui.util.DesignerTemplateUtils
import groovy.util.slurpersupport.GPathResult

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

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        def designerType = node.@"${DESIGNER_TYPE}";
        attributesAsString["parentLayoutId"] = parent._designerKey;
        def componentAtt = node.@component;
        if (componentAtt != null && componentAtt != "")
        {
            UiComponent component = (UiComponent) DesignerSpace.getInstance().getUiElement(UiComponent, "${parent.tabId}_${componentAtt}")
            if (!component) {
                throw new Exception("Could not find component ${componentAtt} for ${designerType}".toString())
            }
            attributesAsString["componentId"] = component._designerKey;
        }
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {
        def innerLayouts = node."${UIELEMENT_TAG}";
        innerLayouts.each {innerLayoutNode ->
            create(innerLayoutNode, this)
        }
    }

    public UiLayout getChildLayout() {
        def layouts = DesignerSpace.getInstance().getUiElements(UiLayout).values().findAll {it.parentUnitId == _designerKey};
        if (layouts.size() > 0) {
            return layouts[0]
        }
        return null;
    }

    public UiLayout getParentLayout() {
        def layouts = DesignerSpace.getInstance().getUiElements(UiLayout).values().findAll {it._designerKey == parentLayoutId};
        if (layouts.size() > 0) {
            return layouts[0]
        }
        return null;
    }

    protected List getExtraPropsNeededInXml() {
        def props = super.getExtraPropsNeededInXml();
        props.addAll(["component"])
        return props;
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
        if (layout)
        {
            _getLayoutUnitHavingContentFile(layout, layoutUnitList);
        }
        return layoutUnitList;
    }
    private static void _getLayoutUnitHavingContentFile(UiLayout layout, List layoutUnitList)
    {
        layout.units.each {UiLayoutUnit unit ->
            def childLayout;
            if (unit.contentFile != null && unit.contentFile != "")
            {
                layoutUnitList.add(unit);
            }
            else if ((childLayout = unit.childLayout) != null)
            {
                _getLayoutUnitHavingContentFile(childLayout, layoutUnitList);
            }
        }
    }

}