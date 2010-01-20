package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 5:29:26 PM
*/
class UiImageObjectMapContent extends UiObjectMapContent {
    String mapping = "";

    public static Map metaData()
    {
        Map metaData = [
                help: "ObjectMap Image.html",
                designerType: "ImageObjectMapContent",
                canBeDeleted: true,
                display: "Image",
                imageExpanded: "images/rapidjs/designer/report.png",
                imageCollapsed: "images/rapidjs/designer/report.png",
                propertyConfiguration: [
                        mapping: [descr: "Map which defines the image mapping according to the possible values of dataKey attribute", validators: [blank: false, nullable: false]]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiObjectMapContent.metaData();
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        attributesAsString["type"] = "image"
    }

}