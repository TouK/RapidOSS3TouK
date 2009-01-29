package ui.designer;
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

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
}