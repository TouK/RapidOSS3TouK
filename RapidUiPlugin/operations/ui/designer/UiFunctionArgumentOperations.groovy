package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 2:26:54 PM
* To change this template use File | Settings | File Templates.
*/
class UiFunctionArgumentOperations extends AbstractDomainOperation{
     public static Map metaData()
    {
        Map metaData = [
                designerType:"FunctionArgument",
                canBeDeleted: true,
                display: "FunctionArgument",
                propertyConfiguration: [
                        value:[descr:"JavaScript expression evaluated and passed to the function as argument", isRequired:true]
                ],
                childrenConfiguration:[]
        ];
        return metaData;
    }
}