package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 2:04:38 PM
* To change this template use File | Settings | File Templates.
*/
class UiActionTriggerOperations extends AbstractDomainOperation{
    public static Map metaData()
    {
        Map metaData = [
                designerType:"ActionTrigger",
                canBeDeleted: true,
                display: "ActionTrigger",
                propertyConfiguration: [
                        name:[descr:""],
                        component:[descr:""],
                        isMenuItem:[descr:""]
                ],
                childrenConfiguration:[]
        ];
        return metaData;
    }
}