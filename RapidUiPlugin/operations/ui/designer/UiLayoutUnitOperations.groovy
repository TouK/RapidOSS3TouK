package ui.designer;
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rui.util.DesignerUtils

public class UiLayoutUnitOperations extends AbstractDomainOperation
{
    public static Map metaData()
    {
        Map metaData = [
                propertyConfiguration: [
                        contentFile:[descr:"Content file where the layout content will be got"],
                        component:[descr:"RapidInsight component that will be displayed in the unit", required:true, formater:{object-> return object.name}],
                        gutter:[descr:"The gutter applied to the unit's wrapper, before the content."],
                        scroll:[descr:"Boolean indicating whether the unit's body should have scroll bars if the body content is larger than the display area."],
                        useShim:[descr:"This setting will be passed to the DragDrop instances on the resize handles and for the draggable property. This property should be used if you want the resize handles to work over iframe and other elements"]
                ],
                childrenConfiguration:[
                    [designerType:"Layout", propertyName:"childLayout", isMultiple: false]
                ]
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.parentLayout = parentElement;
        def designerType = attributes.designerType;
        def domainClass = ApplicationHolder.application.getDomainClass("ui.designer.Ui"+designerType).clazz;
        if(attributes.component != null && attributes.component != "")
        {
            attributes.component=UiComponent.get(name:attributes.component, tab:parentElement.tab, isActive:true);
        }
        return DesignerUtils.addUiObject(domainClass, attributes, xmlNode);
    }
}