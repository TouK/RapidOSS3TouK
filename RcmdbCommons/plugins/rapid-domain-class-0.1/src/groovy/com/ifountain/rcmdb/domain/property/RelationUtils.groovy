/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package com.ifountain.rcmdb.domain.property

import com.ifountain.rcmdb.domain.method.CompassMethodInvoker
import com.ifountain.rcmdb.domain.util.RelationMetaData
import org.compass.core.CompassHit
import org.compass.core.CompassSession
import relation.Relation
import com.ifountain.rcmdb.domain.IdGenerator
import org.compass.core.CompassHits

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
    public static final String DEFAULT_SOURCE_NAME = "�_u_u_�"

    public static void addRelatedObjects(object, RelationMetaData relation, Collection relatedObjects, String source)
    {
        def otherSideName = relation.otherSideName == null?NULL_RELATION_NAME:relation.otherSideName;
        def relationName = relation.name == null?NULL_RELATION_NAME:relation.name;
        def relationObjects = [];
        def objId = object.id;
        relatedObjects.each{
            def relObjectId = IdGenerator.getInstance().getNextId();
            def relationObject = new Relation();
            relationObject.setProperty ("id", relObjectId, false);
            relationObject.setProperty ("objectId", objId, false);
            relationObject.setProperty ("source", getSourceString(source), false);
            relationObject.setProperty ("reverseObjectId", it.id, false);
            relationObject.setProperty ("name", relationName, false);
            relationObject.setProperty ("reverseName", otherSideName, false);
            relationObjects.add(relationObject);
        }
        Relation.index(relationObjects);
    }
    public static String getSourceString(String source)
    {
        if(source != null)
        {
            return "�${source}�";
        }
        else
        {
            return DEFAULT_SOURCE_NAME;
        }
    }
    public static void updateSource(Collection relationObjectIdAndSource, String source)
    {
        if(relationObjectIdAndSource.isEmpty()) return;
        StringBuffer bf = new StringBuffer();
        def sourceExpression = getSourceString(source)
        def relsToBeIndex = [];
        relationObjectIdAndSource.each{Map relationProps->
            def prevSources = relationProps.source;
            if(prevSources.indexOf(sourceExpression) <0 )
            {
                def sources = "${prevSources}${sourceExpression}"
                Relation rel = Relation.searchEvery("id:${relationProps.id}")[0];
                relsToBeIndex.add(rel);
                rel.setProperty ("source", sources, false);
            }
        }
        if(!relsToBeIndex.isEmpty())
        Relation.index(relsToBeIndex);
    }
    public static void removeRelations(object, RelationMetaData relation, Collection relatedObjects, String source)
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
        if(source == null)
        {
            Relation.searchEvery(bf.toString(), [raw:{hits, CompassSession session->
                hits.iterator().each{CompassHit hit->
                    session.delete (hit.getResource());
                }
            }]);
        }
        else
        {
            def rels = Relation.searchEvery(bf.toString());
            def relsToBeDeleted = [];
            def relsToBeUpdated = [];
            def sourceExpression = getSourceString(source)
            rels.each{Relation rel->
                def relSource = rel.source;
                def containsSource = relSource.indexOf(sourceExpression) >= 0;
                if(containsSource)
                {
                    relSource = relSource.replaceAll (sourceExpression, "");
                    if(relSource == "" || relSource == DEFAULT_SOURCE_NAME)
                    {
                        relsToBeDeleted.add(rel);
                    }
                    else
                    {
                        rel.setProperty ("source", relSource, false);
                        relsToBeUpdated.add(rel);
                    }
                }
            }
            if(!relsToBeDeleted.isEmpty())
            {
                Relation.unindex(relsToBeDeleted);
            }
            if(!relsToBeUpdated.isEmpty())
            {
                Relation.index(relsToBeUpdated);
            }
        }


    }
    public static void removeExistingRelations(object, String relationName, String otherSideName)
    {
        removeExistingRelationsById(object.id, relationName, otherSideName);
    }
    public static void removeExistingRelationsById(objectId)
    {
        def query = "objectId:${objectId} OR reverseObjectId:${objectId}";  
        Relation.searchEvery(query, [raw:{hits, CompassSession session->
            hits.iterator().each{CompassHit hit->
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
            hits.iterator().each{CompassHit hit->
                session.delete (hit.getResource());                
            }
        }]);
    }
    public static Map getRelatedObjectsIdsByObjectId(objectId, String relationName, String otherSideName)
    {
        otherSideName = otherSideName == null?NULL_RELATION_NAME:otherSideName;
        relationName = relationName == null?NULL_RELATION_NAME:relationName;
        def allRelatedObjectIds = [:];
        def query = "objectId:${objectId} AND name:\"${relationName}\" AND reverseName:\"${otherSideName}\"";
        Relation.getPropertyValues(query, ["reverseObjectId", "source"]).each{
            allRelatedObjectIds.put(it.reverseObjectId, it);            
        }
        query = "reverseObjectId:${objectId} AND reverseName:\"${relationName}\" AND name:\"${otherSideName}\"";
        Relation.getPropertyValues(query, ["objectId", "source"]).each{
            allRelatedObjectIds.put(it.objectId, it);
        }

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