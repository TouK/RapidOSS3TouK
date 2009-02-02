package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 2:04:38 PM
* To change this template use File | Settings | File Templates.
*/
class UiEventOperations extends AbstractDomainOperation{
    public static Map metaData()
    {
        Map metaData = [
                designerType:"Event",
                canBeDeleted: true,
                display: "Event",
                propertyConfiguration: [
                        eventName:[descr:""],
                        component:[descr:""]
                ],
                childrenConfiguration:[]
        ];
        return metaData;
    }
}