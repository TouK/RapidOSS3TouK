package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 2, 2009
 * Time: 2:09:19 PM
 * To change this template use File | Settings | File Templates.
 */
class UiCombinedActionOperations extends UiActionOperations {
    public static Map metaData()
    {
        Map metaData = [
                designerType: "CombinedAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        actions: [descr: "The list of action names that will be executed in order"]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiActionOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;

    }
}