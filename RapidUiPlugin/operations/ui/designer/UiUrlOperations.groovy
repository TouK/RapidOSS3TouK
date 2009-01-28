package ui.designer;
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

public class UiUrlOperations extends AbstractDomainOperation
{
    public static Map metaData = [
            canBeDeleted: true,
            display: "Url",
            properties: [
                    url: [descr: 'Url of the page set']
            ],
            children: [Tabs: [isMultiple: false]]
    ];

    public static Map metaData()
    {
        return metaData;
    }

}