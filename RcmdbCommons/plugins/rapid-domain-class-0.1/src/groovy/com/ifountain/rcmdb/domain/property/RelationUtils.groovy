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

    public static Object getRelationObjects(object, com.ifountain.rcmdb.domain.util.RelationMetaData relationMetaData, listOfObjects = null)
    {
        def results = null;
        def isReverse = isReverse(object, relationMetaData);
        StringBuffer idQuery = new StringBuffer();
        listOfObjects.each{relatedObject->
            if(!isReverse)
            {
                idQuery.append("reverseObjectId:").append(relatedObject.id).append(" OR ")
            }
            else
            {
                idQuery.append("objectId:").append(relatedObject.id).append(" OR ")
            }
        }
        if(!isReverse)
        {

            results = Relation.searchEvery("objectId:${object.id} AND name:${relationMetaData.name} ${relationMetaData.otherSideName?"AND reverseName:${relationMetaData.otherSideName}":''} ${listOfObjects?'AND ('+idQuery.substring(0, idQuery.length()-4) +')':''}")
        }
        else
        {
            results = Relation.searchEvery("reverseObjectId:${object.id} AND reverseName:${relationMetaData.name} AND name:${relationMetaData.otherSideName} ${listOfObjects?'AND ('+idQuery.substring(0, idQuery.length()-4) +')':''}")
        }
        return results;
    }

    
    public static Object getRelatedObjects(object, com.ifountain.rcmdb.domain.util.RelationMetaData relationMetaData)
    {
        def isReverse = isReverse(object, relationMetaData);
        def relationObjects = getRelationObjects(object, relationMetaData)
        if(relationMetaData.isOneToOne() || relationMetaData.isManyToOne())
        {
            Relation relationObject = relationObjects[0];
            if(relationObject)
            {

                if(isReverse)
                {
                    return CompassMethodInvoker.searchEvery(relationMetaData.otherSideCls.metaClass, "id:${relationObject.objectId}")[0];
                }
                else
                {
                    return CompassMethodInvoker.searchEvery(relationMetaData.otherSideCls.metaClass, "id:${relationObject.reverseObjectId}")[0];    
                }
            }
            else
            {
                return null;
            }
        }
        else
        {
            if(relationObjects.isEmpty())
            {
                return relationObjects;
            }
            else
            {
                StringBuffer query = new StringBuffer();
                relationObjects.each{Relation relationObject->
                    if(isReverse)
                    {
                        query.append("id:").append(relationObject.objectId).append(" OR ")
                    }
                    else
                    {
                        query.append("id:").append(relationObject.reverseObjectId).append(" OR ")
                    }
                }
                return CompassMethodInvoker.searchEvery(relationMetaData.otherSideCls.metaClass, query.substring(0, query.length()-4));
            }
        }
    }

    public static boolean isReverse(domainObject, com.ifountain.rcmdb.domain.util.RelationMetaData relation)
    {
        return relation.otherSideName != null && relation.name.compareTo(relation.otherSideName) < 0;
    }
}