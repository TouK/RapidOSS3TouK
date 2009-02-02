package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 2:30:38 PM
* To change this template use File | Settings | File Templates.
*/
class UiRequestParameterOperations extends AbstractDomainOperation{
     public static Map metaData()
    {
        Map metaData = [
                designerType:"RequestParameter",
                canBeDeleted: true,
                display: "RequestParameter",
                propertyConfiguration: [
                        key:[descr:"The URL parameter name", isRequired:true],
                        value:[descr:"The JavaScript expression that will be evaluated to determine the value of the URL parameter", isRequired:true]
                ],
                childrenConfiguration:[]
        ];
        return metaData;
    }
}