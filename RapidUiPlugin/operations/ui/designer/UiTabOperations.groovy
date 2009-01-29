package ui.designer;
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

public class UiTabOperations extends AbstractDomainOperation
{
    public static Map metaData()
    {
        Map metaData = [
                designerType:"Tab",
                canBeDeleted: true,
                displayFromProperty: "name",
                propertyConfiguration: [
                        name: [descr: 'Name of the tab']
                ],
                childrenConfiguration:[]
        ];
        return metaData;
    }

}