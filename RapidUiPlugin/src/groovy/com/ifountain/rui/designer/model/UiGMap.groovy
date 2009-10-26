package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 3:05:55 PM
*/
class UiGMap extends UiComponent{
    String url
    String googleKey = "ABQIAAAA7ipkp7ZXyXVH2UHyOgqZhxT2yXp_ZAY8_ufC3CFXhHIE1NvwkxRnNbZP5arP3T53Mzg-yLZcEMRBew"
    Long pollingInterval= 0;
    Long timeout=60;
    String locationTagName = "Location";
    String lineTagName = "Line";
    String iconTagName = "Icon";
    Long lineSize = 5;
    Long defaultIconWidth = 32;
    Long defaultIconHeight = 32;

    public static Map metaData()
    {
        Map metaData = [
                help:"GMap Component.html",
                designerType: "GMap",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/map.png",
                imageCollapsed: "images/rapidjs/designer/map.png",
                propertyConfiguration: [
                        url:[descr:"The default URL to be used for requests to the server to retrieve the data. ", validators:[blank:false, nullable:false]],
                        googleKey:[descr:"Google Maps API key for your domain.See Google Maps API documentation for further information.", validators:[blank:false, nullable:false]],
                        pollingInterval:[descr:"Time delay between two server requests.", required:true],
                        locationTagName:[descr:"The node names of AJAX response which will be used as location data. "],
                        lineTagName:[descr:"The node names of AJAX response which will be used as line data. "],
                        iconTagName:[descr:"The node names of AJAX response which will be used as icon data. "],
                        lineSize:[descr:"Pixel width of the lines."],
                        defaultIconWidth:[descr:"Default width of icons in pixels if not specified in AJAX response. Default is 32."],
                        defaultIconHeight:[descr:"Default height of icons in pixels if not specified in AJAX response. Default is 32."],
                        timeout:[descr:"The time interval in seconds to wait the server request completes successfully before aborting."]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiComponent.metaData();
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = [:];
        attributes.putAll(xmlNode.attributes());
        attributes.tabId = parentElement._designerKey;
        return DesignerSpace.getInstance().addUiElement(UiGMap, attributes);
    }
}