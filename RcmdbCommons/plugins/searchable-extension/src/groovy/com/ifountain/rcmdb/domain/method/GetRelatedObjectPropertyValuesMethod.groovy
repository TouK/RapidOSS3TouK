package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.property.RelationUtils
import com.ifountain.rcmdb.domain.util.RelationMetaData

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 2, 2008
 * Time: 6:20:13 PM
 * To change this template use File | Settings | File Templates.
 */
class GetRelatedObjectPropertyValuesMethod extends AbstractRapidDomainMethod{
    Map relations;
    public GetRelatedObjectPropertyValuesMethod(MetaClass mc, Map relations) {
        super(mc);
        this.relations = relations;
    }

    public boolean isWriteOperation() {
        return false;
    }

    protected Object _invoke(Object domainObject, Object[] arguments) {
        String relName = arguments[0]
        Collection propList = arguments[1]
        RelationMetaData relationMetaData = relations[relName];
        if(relationMetaData != null)
        {
            Map relatedObjectIds = RelationUtils.getRelatedObjectsIds(domainObject, relationMetaData.name, relationMetaData.otherSideName);
            if(relatedObjectIds.size() > 0)
            {
                StringBuffer query = new StringBuffer();
                relatedObjectIds.each{id, value->
                    query.append("id:").append(id).append(" OR ")
                }
                String completeQuery = query.substring(0, query.length()-3);
                return relationMetaData.otherSideCls.'getPropertyValues'(completeQuery, propList);
            }
        }
        return [];
    }

}