package ui.designer;
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

public class UiLayoutOperations extends AbstractDomainOperation
{
    public static Map metaData()
    {
        Map metaData = [
                designerType:"Layout",
                canBeDeleted: true,
                display: "Layout",
                propertyConfiguration: [],
                childrenConfiguration:[
                    [designerType:"LayoutUnit", propertyName:"units", isMultiple: true]
                ]
        ];
        return metaData;
    }

}