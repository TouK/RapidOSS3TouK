package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.domain.property.RelationUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 6, 2009
* Time: 2:16:08 PM
* To change this template use File | Settings | File Templates.
*/
class GetRelatedObjectsMethod extends AbstractRapidDomainMethod {
    Map relations;
    public GetRelatedObjectsMethod(MetaClass mcp, Map relations) {
        super(mcp)
        this.relations = relations;
    }
    public Object invoke(Object domainObject, Object[] arguments) {
        String relName = arguments[0]
        String source = arguments[1]
        RelationMetaData relationMetaData = relations[relName];
        if (relationMetaData != null) {
            return RelationUtils.getRelatedObjects(domainObject, relationMetaData, source);
        }
        else {
            throw new MissingPropertyException("No such relation: ${relName} for class: ${domainObject.class.name}")
        }
    }

}