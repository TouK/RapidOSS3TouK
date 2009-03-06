package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 6, 2009
* Time: 10:09:31 AM
* To change this template use File | Settings | File Templates.
*/
class UiImageObjectMapContentOperations extends UiObjectMapContentOperations{
    public static Map metaData()
    {
        Map metaData = [
                help:"ObjectMap Image.html",
                designerType: "ImageObjectMapContent",
                canBeDeleted: true,
                display: "Image",
                imageExpanded: "images/rapidjs/designer/report.png",
                imageCollapsed: "images/rapidjs/designer/report.png",
                propertyConfiguration: [
                        mapping: [descr: "Map which defines the image mapping according to the possible values of dataKey attribute", required:true]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiObjectMapContentOperations.metaData();
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.objectMap = parentElement
        attributes.objectMapId = parentElement.id
        attributes.type = "image"
        return DesignerUtils.addUiObject(UiImageObjectMapContent, attributes, xmlNode);
    }
}