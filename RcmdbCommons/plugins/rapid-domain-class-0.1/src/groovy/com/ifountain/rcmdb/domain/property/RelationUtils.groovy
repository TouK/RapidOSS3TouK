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
import org.apache.lucene.search.BooleanQuery
import com.ifountain.rcmdb.util.CollectionUtils
import com.ifountain.rcmdb.domain.statistics.OperationStatisticResult
import com.ifountain.rcmdb.domain.statistics.OperationStatistics

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
    public static final String DEFAULT_SOURCE_NAME = "¿_u_u_¿"
    private static final int MAX_NUMBER_OF_OBJECT_TO_BE_PROCESSED_IN_REMOVE = 200;
    private static final int MAX_NUMBER_OF_OBJECT_TO_BE_PROCESSED_IN_GETRELATIONS = 200;

    public static void addRelatedObjects(object, RelationMetaData relation, Collection relatedObjects, String source)
    {
        OperationStatisticResult statistics = new OperationStatisticResult(model:"relation.Relation");
        statistics.start();

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

        if(relationObjects.size()>0)
        {
            statistics.stop();
            statistics.numberOfOperations += relationObjects.size()-1;
            OperationStatistics.getInstance().addStatisticResult (OperationStatistics.ADD_OPERATION_NAME, statistics);
        }
    }
    public static String getSourceString(String source)
    {
        if(source != null)
        {
            return "¿${source}¿";
        }
        else
        {
            return DEFAULT_SOURCE_NAME;
        }
    }
    public static void updateSource(Collection relationObjectIdAndSource, String source)
    {
        OperationStatisticResult statistics = new OperationStatisticResult(model:"relation.Relation");
        statistics.start();

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
        {
            Relation.index(relsToBeIndex);

            statistics.stop();
            statistics.numberOfOperations += relsToBeIndex.size()-1;
            OperationStatistics.getInstance().addStatisticResult (OperationStatistics.UPDATE_OPERATION_NAME, statistics);
        }
    }
    public static void removeRelations(object, RelationMetaData relation, Collection relatedObjects, String source)
    {
        if(relatedObjects.isEmpty()) return;

        OperationStatisticResult statistics = new OperationStatisticResult(model:"relation.Relation");
        statistics.start();
        def deleteCount=0;

        def otherSideName = relation.otherSideName == null?NULL_RELATION_NAME:relation.otherSideName;
        def relationName = relation.name == null?NULL_RELATION_NAME:relation.name;
        CollectionUtils.executeForEachBatch(new ArrayList(relatedObjects), MAX_NUMBER_OF_OBJECT_TO_BE_PROCESSED_IN_REMOVE){List relatedObjectToBeProcessed->
            StringBuffer bf = new StringBuffer("(objectId:").append(object.id).append(" AND ").append("name:\"").append(relationName).append("\" AND ");
            bf.append("reverseName:\"").append(otherSideName).append("\"");
            bf.append(" AND ").append("(reverseObjectId:")
            bf.append(relatedObjectToBeProcessed.id.join(" OR reverseObjectId:"));
            bf.append(")) OR (")
            bf.append("reverseObjectId:").append(object.id).append(" AND ").append("reverseName:\"").append(relationName).append("\" AND ");
            bf.append("name:\"").append(otherSideName).append("\"");
            bf.append(" AND ").append("(objectId:")
            bf.append(relatedObjectToBeProcessed.id.join(" OR objectId:"))
            bf.append("))");
            if(source == null)
            {
                Relation.searchEvery(bf.toString(), [raw:{hits, CompassSession session->
                    hits.iterator().each{CompassHit hit->
                        session.delete (hit.getResource());
                    }
                    deleteCount+=hits.length();
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
                    deleteCount+=relsToBeDeleted.size();
                }
                if(!relsToBeUpdated.isEmpty())
                {
                    Relation.index(relsToBeUpdated);
                    deleteCount+=relsToBeUpdated.size();
                }
            }
        }

        if(deleteCount>0)
        {
            statistics.stop();
            statistics.numberOfOperations += deleteCount-1;
            OperationStatistics.getInstance().addStatisticResult (OperationStatistics.REMOVE_OPERATION_NAME, statistics);
        }
    }
    public static void removeExistingRelations(object, String relationName, String otherSideName)
    {
        removeExistingRelationsById(object.id, relationName, otherSideName);
    }
    public static void removeExistingRelationsById(objectId)
    {
        OperationStatisticResult statistics = new OperationStatisticResult(model:"relation.Relation");
        statistics.start();
        def deleteCount=0;

        def query = "objectId:${objectId} OR reverseObjectId:${objectId}";  
        Relation.searchEvery(query, [raw:{hits, CompassSession session->
            hits.iterator().each{CompassHit hit->
                session.delete (hit.getResource());
            }
            deleteCount=hits.length();
        }]);

        if(deleteCount>0)
        {
            statistics.stop();
            statistics.numberOfOperations += deleteCount-1;
            OperationStatistics.getInstance().addStatisticResult (OperationStatistics.REMOVE_OPERATION_NAME, statistics);
        }
    }
    public static void removeExistingRelationsById(objectId, String relationName, String otherSideName)
    {
        OperationStatisticResult statistics = new OperationStatisticResult(model:"relation.Relation");
        statistics.start();
        def deleteCount=0;

        otherSideName = otherSideName == null?NULL_RELATION_NAME:otherSideName;
        relationName = relationName == null?NULL_RELATION_NAME:relationName;
        def allRelatedObjectIds = [:];

        def query = "(objectId:${objectId} AND name:\"${relationName}\" AND reverseName:\"${otherSideName}\") OR (reverseObjectId:${objectId} AND reverseName:\"${relationName}\" AND name:\"${otherSideName}\")";
        Relation.searchEvery(query, [raw:{hits, CompassSession session->
            hits.iterator().each{CompassHit hit->
                session.delete (hit.getResource());
            }
            deleteCount=hits.length();
        }]);

        if(deleteCount>0)
        {
            statistics.stop();
            statistics.numberOfOperations += deleteCount-1;
            OperationStatistics.getInstance().addStatisticResult (OperationStatistics.REMOVE_OPERATION_NAME, statistics);
        }
    }
    public static Map getRelatedObjectsIdsByObjectId(objectId, String relationName, String otherSideName){
        return getRelatedObjectsIdsByObjectId(objectId, relationName, otherSideName, null)
    }
    public static Map getRelatedObjectsIdsByObjectId(objectId, String relationName, String otherSideName, String source)
    {
        otherSideName = otherSideName == null?NULL_RELATION_NAME:otherSideName;
        relationName = relationName == null?NULL_RELATION_NAME:relationName;
        def allRelatedObjectIds = [:];
        def query = "objectId:${objectId} AND name:\"${relationName}\" AND reverseName:\"${otherSideName}\"";
        Relation.getPropertyValues(query, ["reverseObjectId", "source"]).each{
            if((source == null || source == "") || it.source.indexOf(getSourceString(source)) > -1){
                allRelatedObjectIds.put(it.reverseObjectId, it);    
            }
        }
        query = "reverseObjectId:${objectId} AND reverseName:\"${relationName}\" AND name:\"${otherSideName}\"";
        Relation.getPropertyValues(query, ["objectId", "source"]).each{
            if((source == null || source == "") || it.source.indexOf(getSourceString(source)) > -1){
                allRelatedObjectIds.put(it.objectId, it);    
            }
        }

        return allRelatedObjectIds;
    }
    public static Map getRelatedObjectsIds(object, String relationName, String otherSideName)
    {
        return getRelatedObjectsIds(object, relationName, otherSideName, null);
    }
    public static Map getRelatedObjectsIds(object, String relationName, String otherSideName, String source)
    {
        return getRelatedObjectsIdsByObjectId(object.id, relationName, otherSideName, source);
    }

    public static Object getRelatedObjects(object, com.ifountain.rcmdb.domain.util.RelationMetaData relationMetaData){
         return getRelatedObjects(object, relationMetaData, null)
    }
    public static Object getRelatedObjectsByObjectId(objectId, com.ifountain.rcmdb.domain.util.RelationMetaData relationMetaData){
         return getRelatedObjectsByObjectId(objectId, relationMetaData, null)
    }
    public static Object getRelatedObjects(object, com.ifountain.rcmdb.domain.util.RelationMetaData relationMetaData, String source)
    {
        return getRelatedObjectsByObjectId(object.id,relationMetaData,source);
    }
    public static Object getRelatedObjectsByObjectId(objectId, com.ifountain.rcmdb.domain.util.RelationMetaData relationMetaData, String source)
    {
        OperationStatisticResult statistics = new OperationStatisticResult(model:relationMetaData.getCls().name);
        statistics.start();
        def results = [];
        def foundObjectCount=0;

        def allRealtedObjectIds = getRelatedObjectsIdsByObjectId(objectId, relationMetaData.name, relationMetaData.otherSideName, source);
        if(relationMetaData.isOneToOne() || relationMetaData.isManyToOne())
        {
            if(!allRealtedObjectIds.isEmpty())
            {
                results=CompassMethodInvoker.search(relationMetaData.otherSideCls.metaClass, "id:${allRealtedObjectIds.keySet().iterator().next()}").results[0];
                if(results!=null)
                {
                    foundObjectCount=1;
                }
            }
            else
            {
                results=null;
            }
        }
        else
        {
            if(allRealtedObjectIds.isEmpty())
            {
                results=[];
            }
            else
            {

                CollectionUtils.executeForEachBatch(new ArrayList(allRealtedObjectIds.keySet()), MAX_NUMBER_OF_OBJECT_TO_BE_PROCESSED_IN_GETRELATIONS){List objectToBeProcessed->
                    StringBuffer query = new StringBuffer("id:");
                    query.append(objectToBeProcessed.join (" OR id:"))
                    results.addAll(CompassMethodInvoker.searchEvery(relationMetaData.otherSideCls.metaClass, query.toString()));
                }
                foundObjectCount=results.size();
            }
        }


        statistics.stop();
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.GET_RELATED_OBJECTS_OPERATION_NAME, statistics);
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.GET_RELATED_OBJECTS_OPERATION_NAME, statistics.getSubStatisticsWithObjectCount(foundObjectCount));        
        return results;
    }

    
}