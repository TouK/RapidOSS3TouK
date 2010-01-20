package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.UiElmnt

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 2:55:25 PM
*/
class UiAudioPlayer extends UiComponent {

    String url = "";
    String soundFile = "";
    String playCondition = "true";
    String suggestionAttribute = "";
    Long volume = 100;
    Long timeout = 0;
    Long pollingInterval = 0;

    public static Map metaData()
    {
        Map metaData = [
                help: "AudioPlayer Component.html",
                designerType: "AudioPlayer",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/music.png",
                imageCollapsed: "images/rapidjs/designer/music.png",
                propertyConfiguration: [
                        soundFile: [descr: "The URL of the sound file to be played.", validators:[blank:false, nullable:false]],
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data. "],
                        playCondition: [descr: "The javascript expression evaluated on received data to determine whether the sound file will be played or not.", type: "Expression"],
                        volume: [descr: "The percentage of the sound volume."],
                        pollingInterval: [descr: "Time delay between two server requests."],
                        timeout: [descr: "The time interval in seconds to wait the server request completes successfully before aborting."]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiComponent.metaData();
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.remove("title")
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {}

}