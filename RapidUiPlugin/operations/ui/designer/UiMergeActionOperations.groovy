package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 2, 2009
 * Time: 2:09:19 PM
 * To change this template use File | Settings | File Templates.
 */
class UiMergeActionOperations extends UiRequestActionOperations {
    public static Map metaData()
    {
        Map metaData = [
                designerType: "MergeAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        removeAttribute: [descr: "The attribute in response data which indicates that related components should remove the specified data"]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiRequestActionOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;

    }

    def static addUiElement(xmlNode, parentElement)
    {
        return UiRequestActionOperations._addUiElement(UiMergeAction, xmlNode, parentElement);
    }
}