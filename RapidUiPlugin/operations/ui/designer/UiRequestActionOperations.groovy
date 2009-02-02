package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 2, 2009
 * Time: 2:09:19 PM
 * To change this template use File | Settings | File Templates.
 */
class UiRequestActionOperations extends UiActionOperations {
    public static Map metaData()
    {
        Map metaData = [
                designerType: "RequestAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        url: [descr: "The URL to be used for requests to the server"],
                        timeout:[descr: "The time interval in seconds to wait the server request completes successfully before aborting."],
                        components:[descr:"The list of component names which are related with this action (For example which components' error dialog should show if an error occurred during request)"]
                ],
                childrenConfiguration: [
                        [designerType: "RequestParameter", isMultiple: true, propertyName: "parameters"]
                ]
        ];
        def parentMetaData = UiActionOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;

    }
}