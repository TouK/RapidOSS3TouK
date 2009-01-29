package ui.designer;
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

public class UiUrlOperations extends AbstractDomainOperation
{
    public static Map metaData()
    {
        Map metaData = [
                designerType:"Url",
                canBeDeleted: true,
                displayFromProperty: "url",
                propertyConfiguration: [
                        url: [descr: 'Url of the page set']
                ],
                childrenConfiguration:[
                        [
                            designerType:"Tabs",
                            isMultiple: false,
                            metaData: [
                                    canBeDeleted: false,
                                    designerType:"Tabs",
                                    display: "Tabs",
                                    childrenConfiguration: [
                                            [designerType:"Tab", propertyName:"tabs", isMultiple: true]
                                    ]
                            ]
                        ]
                ]
        ];
        return metaData;
    }

}