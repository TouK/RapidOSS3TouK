package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.validator.IRapidValidator

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 2, 2009
* Time: 10:14:11 AM
* To change this template use File | Settings | File Templates.
*/
class BulkAddMethod extends AbstractRapidDomainBulkWriteMethod{

    AddMethod addMethod;
    public BulkAddMethod(MetaClass mcp, Class rootDomainClass, IRapidValidator validator, Map allFields, Map relations, List keys) {
        super(mcp); //To change body of overridden methods use File | Settings | File Templates.
        addMethod = new AddMethod(mcp, rootDomainClass, validator, allFields, relations, keys);
    }

    protected Object _invoke(Object domainClass, Object[] arguments) {
        def domainObjectProps = arguments[0];
        def addedObjects = [];
        domainObjectProps.each{props->
            addedObjects.add(addMethod.invoke (domainClass, [props] as Object[]));
        }
        return addedObjects;
    }
}