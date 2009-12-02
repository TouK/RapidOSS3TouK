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
package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import org.springframework.validation.BeanPropertyBindingResult
import relation.Relation
import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.domain.ObjectProcessor
import com.ifountain.rcmdb.domain.MockObjectProcessorObserver
import com.ifountain.rcmdb.domain.cache.IdCacheEntry
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rcmdb.domain.statistics.GlobalOperationStatisticResult
import com.ifountain.rcmdb.domain.statistics.OperationStatistics

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 15, 2008
* Time: 2:27:48 PM
* To change this template use File | Settings | File Templates.
*/
class RemoveMethodTest extends RapidCmdbWithCompassTestCase{

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        RemoveMethodDomainObject.unIndexList = [];
        RemoveMethodDomainObject.eventCallList = [];
        RemoveMethodDomainObject.updateCacheCallParams.clear();
        RemoveMethodDomainObject.cacheEntryParams.clear();
        RemoveMethodDomainObject.idCache.clear();
        RemoveMethodDomainObject.relatedInstancesShouldBeReturnedFromRemoveRelationMethod = [:]
        RemoveMethodDomainObject.metaClass.'static'.keySet = {
            return []
        }
        RemoveMethodDomainObjectWithEvents.metaClass.'static'.keySet = {
            return []
        }
    }

    public void tearDown() {
        ObjectProcessor.getInstance().deleteObservers();
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testRemoveObject()
    {
        RemoveMethodDomainObject objectToBeRemoved = new RemoveMethodDomainObject(id:1, prop1:"prop1Value1");
        RemoveMethodDomainObject.idCache[objectToBeRemoved.prop1] =  new IdCacheEntry();
        RemoveMethodDomainObject.idCache[objectToBeRemoved.prop1].setProperties(RemoveMethodDomainObject.class, objectToBeRemoved.id);
        RemoveMethod removeMethod = new RemoveMethod(objectToBeRemoved.metaClass, [:]);
        assertTrue (removeMethod  instanceof AbstractRapidDomainWriteMethod);
        removeMethod.invoke (objectToBeRemoved, null);
        assertSame(objectToBeRemoved, RemoveMethodDomainObject.unIndexList[0][0]);
        assertNull (objectToBeRemoved.relationsToBeRemoved);
        assertFalse (objectToBeRemoved.hasErrors());
        assertSame (objectToBeRemoved, RemoveMethodDomainObject.cacheEntryParams[0]);
        assertFalse (RemoveMethodDomainObject.idCache[objectToBeRemoved.prop1].exist())
        assertSame(objectToBeRemoved, RemoveMethodDomainObject.updateCacheCallParams[0][0])
        assertSame(false, RemoveMethodDomainObject.updateCacheCallParams[0][1])
    }

    public void testRemoveObjectWithOperationStatistics(){
        OperationStatistics.getInstance().reset();
        RemoveMethodDomainObject objectToBeRemoved = new RemoveMethodDomainObject(id:1, prop1:"prop1Value1");
        RemoveMethodDomainObject.idCache[objectToBeRemoved.prop1] =  new IdCacheEntry();
        RemoveMethodDomainObject.idCache[objectToBeRemoved.prop1].setProperties(RemoveMethodDomainObject.class, objectToBeRemoved.id);
        RemoveMethod removeMethod = new RemoveMethod(objectToBeRemoved.metaClass, [:]);
        removeMethod.invoke (objectToBeRemoved, null);

        GlobalOperationStatisticResult result = OperationStatistics.getInstance().getModelStatistic(OperationStatistics.REMOVE_OPERATION_NAME, RemoveMethodDomainObject.name);
        assertNotNull(result);
        def resultReport = result.getGeneralReport()
        assertEquals(1, resultReport.NumberOfOperations)
        assertTrue(resultReport.TotalDuration > 0)

        result = OperationStatistics.getInstance().getModelStatistic(OperationStatistics.BEFORE_DELETE_OPERATION_NAME, RemoveMethodDomainObject.name);
        assertNotNull(result);
        resultReport = result.getGeneralReport()
        assertEquals(1, resultReport.NumberOfOperations)
        assertTrue(resultReport.TotalDuration > 0)

        result = OperationStatistics.getInstance().getModelStatistic(OperationStatistics.AFTER_DELETE_OPERATION_NAME, RemoveMethodDomainObject.name);
        assertNotNull(result);
        resultReport = result.getGeneralReport()
        assertEquals(1, resultReport.NumberOfOperations)
        assertTrue(resultReport.TotalDuration > 0)
    }

    public void testRemoveObjectWithEvents()
    {
        initialize([relation.Relation], []);
        MockObjectProcessorObserver observer = new MockObjectProcessorObserver();
        ObjectProcessor.getInstance().addObserver(observer);

        def rel1Object=new Object();
        RemoveMethodDomainObjectWithEvents objectToBeRemoved = new RemoveMethodDomainObjectWithEvents(id:1, prop1:"prop1Value1",rel1:rel1Object);
        RemoveMethodDomainObjectWithEvents objectDoesNotExist = new RemoveMethodDomainObjectWithEvents(id:2, prop1:"prop1Value2");
        RemoveMethodDomainObject.idCache[objectToBeRemoved.prop1] =  new IdCacheEntry();
        RemoveMethodDomainObject.idCache[objectToBeRemoved.prop1].setProperties(RemoveMethodDomainObject.class, objectToBeRemoved.id);
        def relations=[rel1:new RelationMetaData("rel1","otherrel1",RemoveMethodDomainObject,RemoveMethodDomainObject,RelationMetaData.ONE_TO_ONE)];


        RemoveMethod removeMethod = new RemoveMethod(objectDoesNotExist.metaClass, relations);
        removeMethod.invoke (objectDoesNotExist, null);

        assertEquals(0, RemoveMethodDomainObjectWithEvents.unIndexList.size());
        assertNull(objectDoesNotExist.relationsToBeRemoved)
        assertEquals(0, RemoveMethodDomainObject.eventCallList.size())
        assertEquals(0, observer.repositoryChanges.size());

        removeMethod = new RemoveMethod(objectToBeRemoved.metaClass, relations);
        removeMethod.invoke (objectToBeRemoved, null);
        
        assertSame(objectToBeRemoved, RemoveMethodDomainObjectWithEvents.unIndexList[0][0]);
        assertEquals (rel1Object,objectToBeRemoved.relationsToBeRemoved.rel1[0]);

        def eventCallList=["beforeDelete","removeRelation","unindex","afterDelete"];
        assertEquals(eventCallList,RemoveMethodDomainObject.eventCallList);

        assertEquals(1, observer.repositoryChanges.size());
        Map repositoryChange = observer.repositoryChanges[0];
        assertEquals(2, repositoryChange.size());
        assertEquals(EventTriggeringUtils.AFTER_DELETE_EVENT, repositoryChange[ObjectProcessor.EVENT_NAME]);
        assertEquals(objectToBeRemoved.prop1, repositoryChange[ObjectProcessor.DOMAIN_OBJECT].prop1)

    }

    public void testRemoveObjectReturnsErrorIfObjectDoesnotExist()
    {
        RemoveMethodDomainObject objectToBeRemoved = new RemoveMethodDomainObject(id:1, prop1:"prop1Value1");
        RemoveMethod removeMethod = new RemoveMethod(objectToBeRemoved.metaClass, [:]);
        removeMethod.invoke (objectToBeRemoved, null);
        assertTrue (objectToBeRemoved.hasErrors());
        assertEquals(0, RemoveMethodDomainObject.updateCacheCallParams.size())
    }

    public void testRemoveObjectWithRelations()
    {

        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2], []);
        RelationMethodDomainObject1 objectToBeRemoved = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 relatedObj1 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 relatedObj2 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 relatedObj3 = RelationMethodDomainObject2.add([:]);

        objectToBeRemoved.addRelation(rel1:relatedObj1, rel2:relatedObj2, rel3:relatedObj3);
        assertEquals (objectToBeRemoved, relatedObj1.revRel1);
        assertEquals (objectToBeRemoved, relatedObj2.revRel2);
        assertTrue (relatedObj3.revRel3.contains(objectToBeRemoved));
        objectToBeRemoved.remove();
        
        assertNull (RelationMethodDomainObject1.get(id:objectToBeRemoved.id))
        assertNull (RelationMethodDomainObject2.get(id:relatedObj2.id))
        assertEquals (relatedObj1, RelationMethodDomainObject2.get(id:relatedObj1.id))
        assertNull (relatedObj1.revRel1);
        assertNull (relatedObj2.revRel2);
        assertFalse (relatedObj3.revRel3.contains(objectToBeRemoved));
        assertNull (objectToBeRemoved.rel1);
        assertTrue (objectToBeRemoved.rel2.isEmpty());
        assertEquals(0, Relation.search("objectId:${objectToBeRemoved.id} OR reverseObjectId:${objectToBeRemoved.id}").total);
    }
    
}

class RemoveMethodDomainObject
{
    def __operation_class__ = null;
    def static relatedInstancesShouldBeReturnedFromRemoveRelationMethod = [:];
    Long id;
    String prop1;
    def rel1;
    def rel3;
    List rel2 = [];
    def static unIndexList = [];
    def static eventCallList=[];
    def static cacheEntryParams = [];
    def static idCache = [:];
    def static updateCacheCallParams = [];
    
    def errors =  new BeanPropertyBindingResult(this, this.class.getName());
    def relationsToBeRemoved;

    def static updateCacheEntry(object, boolean exist)
    {
        updateCacheCallParams.add([object, exist]);
        if(!exist)
        {
            idCache[object.prop1] = new IdCacheEntry();
        }
        else
        {
            idCache[object.prop1] = new IdCacheEntry();
            idCache[object.prop1].setProperties(object.class, object.id);
        }
    }
    def static getCacheEntry(params)
    {
        cacheEntryParams.add(params);
        IdCacheEntry entry = idCache[params.prop1];
        if(entry == null)
        {
            entry = new IdCacheEntry();
            idCache[params.prop1] = entry;
        }
        return entry;
    }
    

    def static unindex(objectList)
    {
        unIndexList.add(objectList);
        eventCallList.add("unindex");
    }

    public boolean hasErrors()
    {
        return errors.hasErrors();        
    }
    def cloneObject(){
       return this; 
    }

    def removeRelation(Map relations)
    {
        eventCallList.add("removeRelation");
        relationsToBeRemoved = relations;
        return relatedInstancesShouldBeReturnedFromRemoveRelationMethod;
    }
}

class RemoveMethodDomainObjectWithEvents extends RemoveMethodDomainObject
{
    def onLoadWrapper(){
        eventCallList.add("onLoad");
    }

    def beforeInsertWrapper(){
        eventCallList.add("beforeInsert");
    }
    def beforeUpdateWrapper(params){
        eventCallList.add("beforeUpdate");
    }
    def beforeDeleteWrapper(){
        eventCallList.add("beforeDelete");
    }
    def afterInsertWrapper(){
        eventCallList.add("afterInsert");
    }
    def afterUpdateWrapper(params){
        eventCallList.add("afterUpdate");
    }
    def afterDeleteWrapper(){
        eventCallList.add("afterDelete");
    }
}

