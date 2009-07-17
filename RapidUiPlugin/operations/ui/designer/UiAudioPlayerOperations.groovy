package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 16, 2009
* Time: 5:49:45 PM
* To change this template use File | Settings | File Templates.
*/
class UiAudioPlayerOperations extends UiComponentOperations{
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
                        soundFile: [descr: "The URL of the sound file to be played."],
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data. "],
                        playCondition: [descr: "The javascript expression evaluated on received data to determine whether the sound file will be played or not.",type: "Expression"],
                        volume: [descr: "The percentage of the sound volume."],
                        pollingInterval: [descr: "Time delay between two server requests."],
                        timeout:[descr:"The time interval in seconds to wait the server request completes successfully before aborting."]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiComponentOperations.metaData();
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.remove("title")
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = [:];
        attributes.putAll(xmlNode.attributes());
        attributes.tab = parentElement;
        attributes.tabId = parentElement.id;
        return DesignerUtils.addUiObject(UiAudioPlayer, attributes, xmlNode);
    }
}