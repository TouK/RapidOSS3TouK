package com.ifountain.rui.designer

import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 20, 2009
* Time: 11:34:11 AM
*/
public abstract class UiElmnt {
    public static final String DESIGNER_TYPE = "designerType"
    public static final String UIELEMENT_TAG = "UiElement"
    protected String _designerKey;
    public Map attributesAsString;

    public static UiElmnt create(GPathResult node, UiElmnt parent) {
        def designerType = node.@"${DESIGNER_TYPE}".toString()
        Class uiElementClass = DesignerSpace.getInstance().getUiClass(DesignerSpace.PACKAGE_NAME + ".Ui" + designerType);
        if (uiElementClass) {
            UiElmnt instance = (UiElmnt) uiElementClass.newInstance();
            instance.populateStringAttributes(node, parent);
            instance = DesignerSpace.getInstance().addUiElement(instance);
            instance.addChildElements(node, parent);
            instance.removeUnneccessaryAttributesOfInstanceNode(node)
            return instance;
        }
        else {
            throw new Exception("No class found with designer type: ${designerType}".toString())
        }
    }
    public static void removeUnneccessaryAttributes(GPathResult node) {
        def props = [:]
        def atts = node.attributes();
        props.putAll(atts);
        props.each {key, value ->
            if (key != "designerType") {
                atts.remove(key);
            }
        }
    }

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        attributesAsString = [:]
        attributesAsString.putAll(node.attributes());
    }

    protected void removeUnneccessaryAttributesOfInstanceNode(GPathResult node) {
        def propsNeeded = [];
        this.class.metaClass.properties.each {
            if (it.setter != null) {
                propsNeeded.add(it.name)
            }
        }
        def props = [:]
        def atts = node.attributes();
        props.putAll(atts);
        def extraProps = getExtraPropsNeededInXml();
        propsNeeded.addAll(extraProps);
        props.each {key, value ->
            if (!propsNeeded.contains(key)) {
                atts.remove(key);
            }
        }
    }
    protected List getExtraPropsNeededInXml() {
        return [DESIGNER_TYPE];
    }

//    private static Class getUiClassFromDesignerType(String designerType){
//        switch(designerType){
//            case "ObjectMapTextNodeContent":
//            case "ObjectMapGaugeNodeContent":
//                return com.ifountain.rui.designer.model.UiObjectMapContent
//            default:
//                DesignerSpace.getUiClass(DesignerSpace.PACKAGE_NAME + ".Ui" + designerType);
//        }
//    }

    protected abstract void addChildElements(GPathResult node, UiElmnt parent);

}