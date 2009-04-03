package com.ifountain.rcmdb.domain.method
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 3, 2009
 * Time: 4:35:57 PM
 * To change this template use File | Settings | File Templates.
 */
class BulkAddRelationMethod extends AbstractRapidDomainBulkWriteMethod{
    AddRelationMethod method;
    public BulkAddRelationMethod(MetaClass mcp, Map relations) {
        super(mcp); //To change body of overridden methods use File | Settings | File Templates.
        method = new AddRelationMethod(mcp, relations);
    }

    protected Object _invoke(Object domainClass, Object[] arguments) {
        def relationList = arguments[0];
        def returnedObjects = [];
        relationList.each{relationConfig->
            def domainObject = relationConfig.object;
            def source = relationConfig.source;
            def objectRelations = relationConfig.relations;
            returnedObjects.add(method.invoke (domainObject, [objectRelations, source, true] as Object[]));
        }

        return returnedObjects;
    }
}