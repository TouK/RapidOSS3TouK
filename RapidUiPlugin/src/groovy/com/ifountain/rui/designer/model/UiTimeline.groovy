package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 3:13:52 PM
*/
class UiTimeline extends UiComponent{
    String url = "";
    Long pollingInterval= 0;
    Long timeout= 30;

    public static Map metaData()
    {
        Map metaData = [
                help:"Timeline Component.html",
                designerType: "Timeline",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/date.png",
                imageCollapsed: "images/rapidjs/designer/date.png",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data.", validators:[blank:false, nullable:false]],
                        pollingInterval: [descr: "Time delay between two server requests.", required:true],
                        timeout:[descr:"The time interval in seconds to wait the server request completes successfully before aborting."]

                ],
                childrenConfiguration:[[designerType: "TimelineBand", isMultiple:"true"]]
        ];
        def parentMetaData = UiComponent.metaData();
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {
        node."${UIELEMENT_TAG}".each{
            create(it, this);
        }
    }

    public List getBands(){
        DesignerSpace.getInstance().getUiElements(UiTimelineBand).values().findAll {it.timelineId == _designerKey};
    }
}