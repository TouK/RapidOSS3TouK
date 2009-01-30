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
                        component:[descr:"RapidInsight component that will be displayed in the unit"],
                        gutter:[descr:"The gutter applied to the unit's wrapper, before the content."],
                        scroll:[descr:"Boolean indicating whether the unit's body should have scroll bars if the body content is larger than the display area."]
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
        return DesignerUtils.addUiObject(domainClass, attributes, xmlNode);
    }
}