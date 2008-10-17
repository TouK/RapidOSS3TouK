package com.ifountain.rcmdb.domain.property

import relation.Relation
import com.ifountain.rcmdb.domain.method.CompassMethodInvoker

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 1, 2008
 * Time: 3:26:13 PM
 * To change this template use File | Settings | File Templates.
 */
class RelationUtils
{
    def static getReverseRelationObjectsById(id, otherSideName, otherSideClass, notRemovedRelations = null)
    {
        def query = new StringBuffer();
        if(notRemovedRelations)
        {
            query.append("(")
            notRemovedRelations.each{relatedObject->
                query.append("objectId:").append(relatedObject.id).append(" OR ")
            }
            query = query.substring(0, query.length()-4)+") AND "
        }
        if(otherSideClass instanceof Class)
        {
            return Relation.searchEvery("${query} name:${otherSideName} AND className:${otherSideClass.name} ${Relation.getRelKey(id)}:${id}");
        }
        else
        {
            return Relation.searchEvery("${query} name:${otherSideName} AND className:${otherSideClass} ${Relation.getRelKey(id)}:${id}");
        }
    }
    def static getReverseRelationObjects(domainObject, otherSideName, otherSideClass, notRemovedRelations = null)
    {
        return getReverseRelationObjectsById(domainObject.id, otherSideName, otherSideClass, notRemovedRelations);   
    }


    public static Object getRelatedObjects(object, com.ifountain.rcmdb.domain.util.RelationMetaData relationMetaData)
    {
        Relation domainObjectRelations = Relation.get(objectId:object.id, name:relationMetaData.name);
        def reverseRelationObjects = getReverseRelationObjects(object, relationMetaData.otherSideName, relationMetaData.otherSideCls)
        def allRelatedObjectsIds = [:];
        if(domainObjectRelations)
        {
            allRelatedObjectsIds.putAll (domainObjectRelations.relatedObjectIds);
        }
        reverseRelationObjects.each{
            allRelatedObjectsIds[Relation.getRelKey(it.objectId)] = it.objectId;            
        }
        if(relationMetaData.isOneToOne() || relationMetaData.isManyToOne())
        {
            if(!allRelatedObjectsIds.isEmpty())
            {
                    return CompassMethodInvoker.search(relationMetaData.otherSideCls.metaClass, "id:${allRelatedObjectsIds.values().iterator().next()}").results[0];
            }
            else
            {
                return null;
            }
        }
        else
        {
            if(allRelatedObjectsIds.isEmpty())
            {
                return [];
            }
            else
            {
                StringBuffer query = new StringBuffer();
                allRelatedObjectsIds.each{relKey,  id->
                    query.append("id:").append(id).append(" OR ")
                }
                return CompassMethodInvoker.searchEvery(relationMetaData.otherSideCls.metaClass, query.substring(0, query.length()-4));
            }
        }
    }
}