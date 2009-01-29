package ui.designer;
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

public class UiUrlOperations extends AbstractDomainOperation
{
    public static Map metaData()
    {
        Map metaData = [
                designerType: "Url",
                canBeDeleted: true,
                displayFromProperty: "url",
                imageExpanded: 'images/rapidjs/designer/gsp_logo.png',
                imageCollapsed: 'images/rapidjs/designer/gsp_logo.png',
                propertyConfiguration: [
                        url: [descr: 'Url of the page set']
                ],
                childrenConfiguration: [
                        [
                                designerType: "Tabs",
                                metaData: [
                                        designerType: "Tabs",
                                        display: "Tabs",
                                        imageExpanded: 'images/rapidjs/designer/tab.png',
                                        imageCollapsed: 'images/rapidjs/designer/tab.png',
                                        childrenConfiguration: [
                                                [designerType: "Tab", propertyName: "tabs", isMultiple: true]
                                        ]
                                ]
                        ]
                ]
        ];
        return metaData;
    }

}