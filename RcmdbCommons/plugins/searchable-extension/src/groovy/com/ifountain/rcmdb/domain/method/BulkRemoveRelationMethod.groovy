package com.ifountain.rcmdb.domain.method
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 3, 2009
 * Time: 5:11:21 PM
 * To change this template use File | Settings | File Templates.
 */
class BulkRemoveRelationMethod extends AbstractRapidDomainBulkWriteMethod{
    RemoveRelationMethod method;
    public BulkRemoveRelationMethod(MetaClass mcp, Map relations) {
        super(mcp);
        method = new RemoveRelationMethod(mcp, relations);
    }
    protected Object _invoke(Object domainClass, Object[] arguments) {
        def relationList = arguments[0];
        def returnedObjects = [];
        relationList.each{relationConfig->
            def domainObject = relationConfig.object;
            def source = relationConfig.source;
            def objectRelations = relationConfig.relations;
            def returnedObject = method.invoke (domainObject, [objectRelations, source, true] as Object[]);
            returnedObjects.add(returnedObject);
        }

        return returnedObjects;
    }
}