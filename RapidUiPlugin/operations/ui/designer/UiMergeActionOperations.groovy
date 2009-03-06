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
                help:"MergeAction.html",
                designerType: "MergeAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/connect_established.png",
                imageCollapsed: "images/rapidjs/designer/connect_established.png",
                propertyConfiguration: [
                        removeAttribute: [descr: "The attribute in response data which indicates that related components should remove the specified data"]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiRequestActionOperations.metaData();
        def childConfiguration = [];
        childConfiguration.addAll(parentMetaData.childrenConfiguration)
        childConfiguration.addAll(metaData.childrenConfiguration)
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration = childConfiguration;
        return metaData;

    }

    def static addUiElement(xmlNode, parentElement)
    {
        return UiRequestActionOperations._addUiElement(UiMergeAction, xmlNode, parentElement);
    }
}