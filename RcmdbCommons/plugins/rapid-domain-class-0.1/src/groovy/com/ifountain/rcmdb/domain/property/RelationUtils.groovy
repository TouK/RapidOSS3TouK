package com.ifountain.rcmdb.domain.property

import com.ifountain.rcmdb.domain.method.CompassMethodInvoker
import com.ifountain.rcmdb.domain.util.RelationMetaData
import org.compass.core.CompassHit
import org.compass.core.CompassSession
import relation.Relation

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 1, 2008
 * Time: 3:26:13 PM
 * To change this template use File | Settings | File Templates.
 */
class RelationUtils
{
    public static final String NULL_RELATION_NAME = "-"
    public static void addRelatedObjects(object, RelationMetaData relation, Collection relatedObjects)
    {
        def otherSideName = relation.otherSideName == null?NULL_RELATION_NAME:relation.otherSideName;
        def relationName = relation.name == null?NULL_RELATION_NAME:relation.name;
        relatedObjects.each{
            Relation.add(objectId:object.id, reverseObjectId:it.id, name:relationName, reverseName:otherSideName);
        }
    }
    public static void removeRelations(object, RelationMetaData relation, Collection relatedObjects)
    {
        if(relatedObjects.isEmpty()) return;
        def otherSideName = relation.otherSideName == null?NULL_RELATION_NAME:relation.otherSideName;
        def relationName = relation.name == null?NULL_RELATION_NAME:relation.name;
        StringBuffer bf = new StringBuffer("(objectId:").append(object.id).append(" AND ").append("name:\"").append(relationName).append("\" AND ");
        bf.append("reverseName:\"").append(otherSideName).append("\"");
        bf.append(" AND ").append("(")
        relatedObjects.each{
            bf.append("reverseObjectId:").append(it.id).append(" OR ")
        }
        bf.delete(bf.length()-3, bf.length());
        bf.append(")) OR (")
        bf.append("reverseObjectId:").append(object.id).append(" AND ").append("reverseName:\"").append(relationName).append("\" AND ");
        bf.append("name:\"").append(otherSideName).append("\"");
        bf.append(" AND ").append("(")
        relatedObjects.each{
            bf.append("objectId:").append(it.id).append(" OR ")
        }
        bf.delete(bf.length()-3, bf.length());
        bf.append("))");
        Relation.searchEvery(bf.toString(), [raw:{hits, CompassSession session->
            hits.each{CompassHit hit->
                session.delete (hit.getResource());
            }
        }]);


    }
    public static void removeExistingRelations(object, String relationName, String otherSideName)
    {
        removeExistingRelationsById(object.id, relationName, otherSideName);
    }
    public static void removeExistingRelationsById(objectId)
    {
        def query = "objectId:${objectId} OR reverseObjectId:${objectId}";  
        Relation.searchEvery(query, [raw:{hits, CompassSession session->
            hits.each{CompassHit hit->
                session.delete (hit.getResource());
            }
        }]);
    }
    public static void removeExistingRelationsById(objectId, String relationName, String otherSideName)
    {
        otherSideName = otherSideName == null?NULL_RELATION_NAME:otherSideName;
        relationName = relationName == null?NULL_RELATION_NAME:relationName;
        def allRelatedObjectIds = [:];

        def query = "(objectId:${objectId} AND name:\"${relationName}\" AND reverseName:\"${otherSideName}\") OR (reverseObjectId:${objectId} AND reverseName:\"${relationName}\" AND name:\"${otherSideName}\")";
        Relation.searchEvery(query, [raw:{hits, CompassSession session->
            hits.each{CompassHit hit->
                session.delete (hit.getResource());                
            }
        }]);
    }
    public static Map getAllRelatedObjectIds(objectId)
    {
        def query = "objectId:${objectId}";
        def allRelatedObjectIds = [:];
        allRelatedObjectIds.putAll(Relation.propertySummary(query, "reverseObjectId").reverseObjectId);
        query = "reverseObjectId:${objectId}";
        allRelatedObjectIds.putAll(Relation.propertySummary(query, "objectId").objectId);
        return allRelatedObjectIds;
    }
    public static Map getRelatedObjectsIdsByObjectId(objectId, String relationName, String otherSideName)
    {
        otherSideName = otherSideName == null?NULL_RELATION_NAME:otherSideName;
        relationName = relationName == null?NULL_RELATION_NAME:relationName;
        def allRelatedObjectIds = [:];
        def query = "objectId:${objectId} AND name:\"${relationName}\" AND reverseName:\"${otherSideName}\"";
        allRelatedObjectIds.putAll(Relation.propertySummary(query, "reverseObjectId").reverseObjectId);
        query = "reverseObjectId:${objectId} AND reverseName:\"${relationName}\" AND name:\"${otherSideName}\"";
        allRelatedObjectIds.putAll(Relation.propertySummary(query, "objectId").objectId);
        return allRelatedObjectIds;
    }
    public static Map getRelatedObjectsIds(object, String relationName, String otherSideName)
    {
        return getRelatedObjectsIdsByObjectId(object.id, relationName, otherSideName);
    }
    public static Object getRelatedObjects(object, com.ifountain.rcmdb.domain.util.RelationMetaData relationMetaData)
    {
        def allRealtedObjectIds = getRelatedObjectsIds(object, relationMetaData.name, relationMetaData.otherSideName);
        if(relationMetaData.isOneToOne() || relationMetaData.isManyToOne())
        {
            if(!allRealtedObjectIds.isEmpty())
            {
                    return CompassMethodInvoker.search(relationMetaData.otherSideCls.metaClass, "id:${allRealtedObjectIds.keySet().iterator().next()}").results[0];
            }
            else
            {
                return null;
            }
        }
        else
        {
            if(allRealtedObjectIds.isEmpty())
            {
                return [];
            }
            else
            {
                StringBuffer query = new StringBuffer();
                allRealtedObjectIds.each{id, numberOfIds->
                    query.append("id:").append(id).append(" OR ")
                }
                return CompassMethodInvoker.searchEvery(relationMetaData.otherSideCls.metaClass, query.substring(0, query.length()-4));
            }
        }
    }
}