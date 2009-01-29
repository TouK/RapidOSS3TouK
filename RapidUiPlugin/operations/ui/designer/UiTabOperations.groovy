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
                        name: [descr: 'Name of the tab'],
                        javascriptFile: [descr: 'The file path relative to web-app that will be embedded to tab, where you can write free form JavaScript']
                ],
                childrenConfiguration:[]
        ];
        return metaData;
    }

}