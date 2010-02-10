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

import com.ifountain.rcmdb.converter.*
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.MockIdGeneratorStrategy
import com.ifountain.rcmdb.domain.MockObjectProcessorObserver
import com.ifountain.rcmdb.domain.ObjectProcessor
import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import java.text.SimpleDateFormat
import com.ifountain.rcmdb.domain.cache.IdCacheEntry
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.domain.statistics.GlobalOperationStatisticResult
import com.ifountain.rcmdb.domain.statistics.OperationStatistics
import com.ifountain.rcmdb.domain.DomainLockManager
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.rcmdb.domain.cache.IdCache

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 15, 2008
* Time: 4:01:27 PM
* To change this template use File | Settings | File Templates.
*/
class UpdateMethodTest extends RapidCmdbTestCase {
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        DomainLockManager.getInstance().initialize (TestLogUtils.log);
        LockStrategyImpl.setMaxNumberOfRetries(1);
        IdCache.initialize (10000);

        IdGenerator.initialize(new MockIdGeneratorStrategy());
        AddMethodDomainObject1.idCache = [:];
        AddMethodDomainObject1.cacheEntryParams = null;
        AddMethodDomainObjectWithEvents.eventCalls = [];
        AddMethodDomainObject1.searchResult = [total: 0, results: []];
        AddMethodDomainObject1.query = null;
        AddMethodDomainObject1.cacheEntryParams = [];
        AddMethodDomainObject1.indexList = [];
        AddMethodDomainObject1.metaClass.'static'.keySet = {
            return []
        }

        AddMethodDomainObjectWithEvents.metaClass.'static'.keySet = {
            return []
        }

        ChildAddMethodDomainObject.metaClass.'static'.keySet = {
            return []
        }

        MockValidator.closureToBeInvokedInValidate = null;
    }

    protected void tearDown() {
        DomainLockManager.destroy();
        IdCache.clearCache();
        AddMethodDomainObjectWithEvents.eventCalls = [];
        ObjectProcessor.getInstance().deleteObservers();
        MockValidator.closureToBeInvokedInValidate = null;
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.

    }

    public void testUpdateMethod()
    {
        AddMethodDomainObject1 objectBeforeAdd = new AddMethodDomainObject1(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");
        AddMethodDomainObject1 relatedObject = new AddMethodDomainObject1(id: 100, prop1: "object2Prop1Value");

        def relations = ["rel1": new RelationMetaData("rel1", "revRel1", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.ONE_TO_ONE)];
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObject1.allFields, relations, ["prop1"]);

        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3];

        def addedObject = add.invoke(AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);
        addedObject.numberOfFlushCalls = 0;
        addedObject.isFlushedByProperty = [];
        objectBeforeAdd.id = addedObject.id;
        def insertedAt = addedObject.rsInsertedAt;
        //make sure that time passed after add to test rsInsertedAt modification
        Thread.sleep(200);

        AddMethodDomainObject1.indexList.clear();
        props = [prop1: objectBeforeAdd.prop1, prop2: "newProp2Value", rel1: relatedObject, id: 5000];
        UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, relations);
        assertTrue(update instanceof AbstractRapidDomainWriteMethod);
        AddMethodDomainObject1 updatedObject = update.invoke(addedObject, [props] as Object[]);
        assertFalse(updatedObject.hasErrors());

        //id property will be ignored
        assertEquals(updatedObject.rsInsertedAt, insertedAt);
        def updatedAt = addedObject.rsUpdatedAt;
        assertTrue(updatedAt > 0);
        assertTrue(updatedAt <= System.currentTimeMillis() && updatedAt >= System.currentTimeMillis() - 3000);
        assertEquals(objectBeforeAdd.id, updatedObject.id);
        assertEquals("newProp2Value", updatedObject.prop2);
        assertEquals(objectBeforeAdd.prop3, updatedObject.prop3);
        assertEquals(3, addedObject.numberOfFlushCalls);
        assertFalse(addedObject.isFlushedByProperty[0]);
        assertFalse(addedObject.isFlushedByProperty[1]);
        assertFalse(addedObject.isFlushedByProperty[2]);
        assertEquals(relatedObject, updatedObject.relationsShouldBeAdded.get("rel1"));
        assertEquals(1, AddMethodDomainObject1.indexList.size());
        assertSame(updatedObject, AddMethodDomainObject1.indexList[0]);

        def propName = "id"
        props = ["$propName": 5000];
        updatedObject = update.invoke(addedObject, [props] as Object[]);
        //id property will be ignored
        assertEquals(objectBeforeAdd.id, updatedObject.id);
    }

    public void testUpdateMethodWithOperationStatistics() {
        OperationStatistics.getInstance().reset();
        
        AddMethodDomainObject1 objectBeforeAdd = new AddMethodDomainObject1(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");

        def relations = ["rel1": new RelationMetaData("rel1", "revRel1", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.ONE_TO_ONE)];
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObject1.allFields, relations, ["prop1"]);

        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3];

        def addedObject = add.invoke(AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);
       

        props = [prop1: objectBeforeAdd.prop1, prop2: "newProp2Value"];
        UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, relations);
        AddMethodDomainObject1 updatedObject = update.invoke(addedObject, [props] as Object[]);
        assertFalse(updatedObject.hasErrors());

        GlobalOperationStatisticResult result = OperationStatistics.getInstance().getModelStatistic(OperationStatistics.UPDATE_OPERATION_NAME, AddMethodDomainObject1.class.name);
        assertNotNull(result);
        def resultReport = result.getGeneralReport()
        assertEquals(1, resultReport.NumberOfOperations)
        assertTrue(resultReport.TotalDuration > 0)

        result = OperationStatistics.getInstance().getModelStatistic(OperationStatistics.BEFORE_UPDATE_OPERATION_NAME, AddMethodDomainObject1.class.name);
        assertNotNull(result);
        resultReport = result.getGeneralReport()
        assertEquals(1, resultReport.NumberOfOperations)
        assertTrue(resultReport.TotalDuration > 0)

        result = OperationStatistics.getInstance().getModelStatistic(OperationStatistics.AFTER_UPDATE_OPERATION_NAME, AddMethodDomainObject1.class.name);
        assertNotNull(result);
        resultReport = result.getGeneralReport()
        assertEquals(1, resultReport.NumberOfOperations)
        assertTrue(resultReport.TotalDuration > 0)
    }


    public void testUpdateMethodWithNullPropValue()
    {
        AddMethodDomainObject1 objectBeforeAdd = new AddMethodDomainObject1(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value", nullableProp: "notnullvalue");
        AddMethodDomainObject1 relatedObject = new AddMethodDomainObject1(id: 100, prop1: "object2Prop1Value");

        def relations = ["rel1": new RelationMetaData("rel1", "revRel1", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.ONE_TO_ONE)];
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObject1.allFields, relations, ["prop1"]);

        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3, nullableProp: objectBeforeAdd.nullableProp];

        def addedObject = add.invoke(AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals("notnullvalue", addedObject.nullableProp);

        props = [prop1: objectBeforeAdd.prop1, nullableProp: null];
        UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, relations);
        AddMethodDomainObject1 updatedObject = update.invoke(addedObject, [props] as Object[]);
        assertEquals("defaultValue", updatedObject.nullableProp);
    }


    public void testUpdateMethodWithNullPropetyValueChangedToNotNullInBeforeUpdateEvent()
    {

        AddMethodDomainObjectWithEvents objectBeforeAdd = new AddMethodDomainObjectWithEvents(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");

        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, [:], ["prop1"]);

        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3];

        AddMethodDomainObjectWithEvents addedObject = add.invoke(AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);

        AddMethodDomainObject1.indexList.clear();

        def propvalueToBeUpdatedInBeforeUpdate = "updatedValueInBeforeUpdate";
        addedObject.closureToBeInvokedBeforeUpdate = {params ->
            addedObject.setProperty("nullableProp", propvalueToBeUpdatedInBeforeUpdate, false);
        }

        props = [prop1: objectBeforeAdd.prop1, nullableProp: null];
        MockValidator validator = new MockValidator();
        UpdateMethod update = new UpdateMethod(AddMethodDomainObjectWithEvents.metaClass, validator, AddMethodDomainObject1.allFields, [:]);
        def updatedObject = update.invoke(addedObject, [props] as Object[]);

        assertEquals(propvalueToBeUpdatedInBeforeUpdate, updatedObject.nullableProp);
        assertEquals(propvalueToBeUpdatedInBeforeUpdate, validator.validatedObject.nullableProp);

    }

    public void testUpdateMethodWithNullPropertyValueSetInBeforeInsert()
    {
        AddMethodDomainObjectWithEvents objectBeforeAdd = new AddMethodDomainObjectWithEvents(prop1: "object1Prop1Value", prop2: "object1Prop2Value", nullableProp: "notnulll");

        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, [:], ["prop1"]);

        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, nullableProp: objectBeforeAdd.nullableProp];

        AddMethodDomainObjectWithEvents addedObject = add.invoke(AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);

        AddMethodDomainObject1.indexList.clear();

        def propvalueToBeUpdatedInBeforeUpdate = "updatedValueInBeforeUpdate";
        addedObject.closureToBeInvokedBeforeUpdate = {params ->
            addedObject.setProperty("nullableProp", null, false);
            //only map key is used for changed props value should be ignored
            addedObject.updatedPropsMap = [nullableProp: "notNullValue"];
        }
        def nullablePropValueInValidate = "notnulllll";
        MockValidator.closureToBeInvokedInValidate = {_validator, _wrapper, _object, _errors ->
            nullablePropValueInValidate = _object.nullableProp;
        }
        props = [prop1: objectBeforeAdd.prop1, nullableProp: "notnull222"];
        MockValidator validator = new MockValidator();
        UpdateMethod update = new UpdateMethod(AddMethodDomainObjectWithEvents.metaClass, validator, AddMethodDomainObject1.allFields, [:]);
        def updatedObject = update.invoke(addedObject, [props] as Object[]);

        assertEquals("defaultValue", updatedObject.nullableProp);
        assertEquals("defaultValue", validator.validatedObject.nullableProp);
        assertEquals(null, nullablePropValueInValidate);

    }

    public void testUpdateMethodWithKeyProperties()
    {
        AddMethodDomainObject1 objectBeforeAdd = new AddMethodDomainObject1(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");
        AddMethodDomainObject1 relatedObject = new AddMethodDomainObject1(id: 100, prop1: "object2Prop1Value");

        def relations = ["rel1": new RelationMetaData("rel1", "revRel1", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.ONE_TO_ONE)];
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObject1.allFields, relations, ["prop1"]);

        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3];

        def addedObject = add.invoke(AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);
        addedObject.numberOfFlushCalls = 0;
        addedObject.isFlushedByProperty = [];
        objectBeforeAdd.id = addedObject.id;

        AddMethodDomainObject1.indexList.clear();
        props = [prop1: "newKeyValue"];
        UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, relations);
        assertTrue(update instanceof AbstractRapidDomainWriteMethod);
        AddMethodDomainObject1 updatedObject = update.invoke(addedObject, [props] as Object[]);
        assertFalse(updatedObject.hasErrors());

        assertEquals(objectBeforeAdd.id, updatedObject.id);
        assertEquals(props.prop1, updatedObject.prop1);
        assertEquals(2, AddMethodDomainObject1.idCache.size());
        assertFalse(AddMethodDomainObject1.idCache[objectBeforeAdd.prop1].exist());
        assertTrue(AddMethodDomainObject1.idCache[updatedObject.prop1].exist());
        assertEquals(updatedObject.id, AddMethodDomainObject1.idCache[updatedObject.prop1].id);
        assertEquals(updatedObject.class, AddMethodDomainObject1.idCache[updatedObject.prop1].alias);
        assertSame(updatedObject, AddMethodDomainObject1.cacheEntryParams[1]);
    }

    public void testUpdateMethodReturnsErrorIfObjectDoesnotExist()
    {
        AddMethodDomainObject1 object = new AddMethodDomainObject1(id: 100, prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");
        def props = [prop1: object.prop1, prop2: "newProp2Value"];
        UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, [:]);
        assertTrue(update instanceof AbstractRapidDomainWriteMethod);
        AddMethodDomainObject1 updatedObject = update.invoke(object, [[prop2: "newprop2value"]] as Object[]);
        assertTrue(updatedObject.hasErrors());
        assertSame(object, AddMethodDomainObject1.cacheEntryParams[0]);
        assertEquals("default.not.exist.message", updatedObject.errors.allErrors[0].code);
        assertEquals(0, updatedObject[RapidCMDBConstants.UPDATED_AT_PROPERTY_NAME]);
    }




    public void testUpdateMethodWillNotCallIndexIfNoPropertyHasChanged()
    {
        AddMethodDomainObjectWithEvents objectBeforeAdd = new AddMethodDomainObjectWithEvents(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");
        def relations = [:];
        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, AddMethodDomainObjectWithEvents, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations, ["prop1"]);
        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3];
        def addedObject = add.invoke(AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);
        addedObject.numberOfFlushCalls = 0;
        addedObject.isFlushedByProperty = [];
        objectBeforeAdd.id = addedObject.id;

        AddMethodDomainObjectWithEvents.eventCalls.clear();
        AddMethodDomainObjectWithEvents.indexList.clear();
        IdCacheEntry entry = AddMethodDomainObjectWithEvents.idCache[addedObject.prop1];
        def numberOfUpdateCacheCall = AddMethodDomainObject1.updateCacheCallParams.size();
        props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2];
        //for rsUpdatedAt,
        Thread.sleep(100);
        UpdateMethod update = new UpdateMethod(AddMethodDomainObjectWithEvents.metaClass, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations);
        AddMethodDomainObjectWithEvents updatedObject = update.invoke(addedObject, [props] as Object[]);


        //id property will be ignored
        assertEquals(updatedObject[RapidCMDBConstants.INSERTED_AT_PROPERTY_NAME], updatedObject[RapidCMDBConstants.UPDATED_AT_PROPERTY_NAME]);

        assertEquals(objectBeforeAdd.id, updatedObject.id);
        assertEquals(objectBeforeAdd.prop2, updatedObject.prop2);
        assertEquals(objectBeforeAdd.prop3, updatedObject.prop3);
        assertEquals(0, AddMethodDomainObjectWithEvents.indexList.size());
        assertSame(updatedObject, addedObject);
        assertEquals(0, AddMethodDomainObjectWithEvents.eventCalls.size());
        IdCacheEntry entryAfterUpdate = AddMethodDomainObjectWithEvents.idCache[updatedObject.prop1];
        assertSame(entry, entryAfterUpdate)
        assertTrue(entryAfterUpdate.exist());
        assertEquals(AddMethodDomainObjectWithEvents.class, entryAfterUpdate.alias);
        assertEquals(updatedObject.id, entryAfterUpdate.id);
        assertEquals("cache should not be updated since no property changed", numberOfUpdateCacheCall, AddMethodDomainObject1.updateCacheCallParams.size());
    }

    public void testUpdateMethodWillNotCallIndexIfNoPropertyHasChangedWithNullProps()
    {

        AddMethodDomainObject1 objectBeforeAdd = new AddMethodDomainObject1(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");

        def relations = [:];
        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, AddMethodDomainObjectWithEvents, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations, ["prop1"]);
        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3, nullableProp: null];
        def addedObject = add.invoke(AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);
        addedObject.numberOfFlushCalls = 0;
        addedObject.isFlushedByProperty = [];
        objectBeforeAdd.id = addedObject.id;

        AddMethodDomainObjectWithEvents.eventCalls.clear();
        AddMethodDomainObjectWithEvents.indexList.clear();
        IdCacheEntry entry = AddMethodDomainObjectWithEvents.idCache[addedObject.prop1];
        def numberOfUpdateCacheCall = AddMethodDomainObject1.updateCacheCallParams.size();
        props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, nullableProp: null];
        UpdateMethod update = new UpdateMethod(AddMethodDomainObjectWithEvents.metaClass, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations);
        AddMethodDomainObjectWithEvents updatedObject = update.invoke(addedObject, [props] as Object[]);


        //id property will be ignored
        assertEquals(updatedObject[RapidCMDBConstants.INSERTED_AT_PROPERTY_NAME], updatedObject[RapidCMDBConstants.UPDATED_AT_PROPERTY_NAME]);
        assertEquals(objectBeforeAdd.id, updatedObject.id);
        assertEquals(objectBeforeAdd.prop2, updatedObject.prop2);
        assertEquals(objectBeforeAdd.prop3, updatedObject.prop3);
        assertEquals(objectBeforeAdd.nullableProp, updatedObject.nullableProp);
        assertEquals(0, AddMethodDomainObjectWithEvents.indexList.size());
        assertSame(updatedObject, addedObject);
        println AddMethodDomainObjectWithEvents.eventCalls

        assertEquals(1, AddMethodDomainObjectWithEvents.eventCalls.size());
        assertEquals("beforeUpdate", AddMethodDomainObjectWithEvents.eventCalls[0]);
        IdCacheEntry entryAfterUpdate = AddMethodDomainObjectWithEvents.idCache[updatedObject.prop1];
        assertSame(entry, entryAfterUpdate)
        assertTrue(entryAfterUpdate.exist());
        assertEquals(AddMethodDomainObjectWithEvents.class, entryAfterUpdate.alias);
        assertEquals(updatedObject.id, entryAfterUpdate.id);
        assertEquals("cache should not be updated since no property changed", numberOfUpdateCacheCall, AddMethodDomainObject1.updateCacheCallParams.size());
    }
    public void testUpdateMethodWillNotCallIndexIfNoPropertyHasChangedWithNullValueAssignedInBeforeInsert()
    {

        AddMethodDomainObject1 objectBeforeAdd = new AddMethodDomainObjectWithEvents(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");

        def relations = [:];
        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, AddMethodDomainObjectWithEvents, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations, ["prop1"]);
        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3, nullableProp: null];
        def addedObject = add.invoke(AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);
        addedObject.numberOfFlushCalls = 0;
        addedObject.isFlushedByProperty = [];
        objectBeforeAdd.id = addedObject.id;

        AddMethodDomainObjectWithEvents.eventCalls.clear();
        AddMethodDomainObjectWithEvents.indexList.clear();
        IdCacheEntry entry = AddMethodDomainObjectWithEvents.idCache[addedObject.prop1];
        def numberOfUpdateCacheCall = AddMethodDomainObject1.updateCacheCallParams.size();
        props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, nullableProp: "changedValue"];

        //change the property back to normal here , null will convert to defaultValue , put it into updatedPropsMap
        addedObject.closureToBeInvokedBeforeUpdate = {params ->
            addedObject.setProperty("nullableProp", null, false);
            addedObject.updatedPropsMap = [nullableProp: null];
        }



        UpdateMethod update = new UpdateMethod(AddMethodDomainObjectWithEvents.metaClass, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations);
        AddMethodDomainObjectWithEvents updatedObject = update.invoke(addedObject, [props] as Object[]);



        //id property will be ignored
        assertEquals(updatedObject[RapidCMDBConstants.INSERTED_AT_PROPERTY_NAME], updatedObject[RapidCMDBConstants.UPDATED_AT_PROPERTY_NAME]);
        assertEquals(objectBeforeAdd.id, updatedObject.id);
        assertEquals(objectBeforeAdd.prop2, updatedObject.prop2);
        assertEquals(objectBeforeAdd.prop3, updatedObject.prop3);
        assertEquals(objectBeforeAdd.nullableProp, updatedObject.nullableProp);
        assertEquals(0, AddMethodDomainObjectWithEvents.indexList.size());
        assertSame(updatedObject, addedObject);
        println AddMethodDomainObjectWithEvents.eventCalls

        assertEquals(1, AddMethodDomainObjectWithEvents.eventCalls.size());
        assertEquals("beforeUpdate", AddMethodDomainObjectWithEvents.eventCalls[0]);
        IdCacheEntry entryAfterUpdate = AddMethodDomainObjectWithEvents.idCache[updatedObject.prop1];
        assertSame(entry, entryAfterUpdate)
        assertTrue(entryAfterUpdate.exist());
        assertEquals(AddMethodDomainObjectWithEvents.class, entryAfterUpdate.alias);
        assertEquals(updatedObject.id, entryAfterUpdate.id);
        assertEquals("cache should not be updated since no property changed", numberOfUpdateCacheCall, AddMethodDomainObject1.updateCacheCallParams.size());
    }

    public void testUpdateMethodWillNotCallIndexIfNoPropertyHasChangedAfterBeforeUpdateRevertsTheChangeInProps()
    {

        AddMethodDomainObject1 objectBeforeAdd = new AddMethodDomainObjectWithEvents(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");

        def relations = [:];
        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, AddMethodDomainObjectWithEvents, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations, ["prop1"]);
        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3];
        def addedObject = add.invoke(AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);
        addedObject.numberOfFlushCalls = 0;
        addedObject.isFlushedByProperty = [];
        objectBeforeAdd.id = addedObject.id;

        AddMethodDomainObjectWithEvents.eventCalls.clear();
        AddMethodDomainObjectWithEvents.indexList.clear();
        IdCacheEntry entry = AddMethodDomainObjectWithEvents.idCache[addedObject.prop1];
        def numberOfUpdateCacheCall = AddMethodDomainObject1.updateCacheCallParams.size();

        //change the property back to normal here , put it into updatedPropsMap
        addedObject.closureToBeInvokedBeforeUpdate = {params ->
            addedObject.setProperty("prop2", objectBeforeAdd.prop2, false);
            addedObject.updatedPropsMap = [prop2: objectBeforeAdd.prop2 + "2"];
        }

        props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2 + "2"];
        UpdateMethod update = new UpdateMethod(AddMethodDomainObjectWithEvents.metaClass, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations);
        AddMethodDomainObjectWithEvents updatedObject = update.invoke(addedObject, [props] as Object[]);


        //id property will be ignored
        assertEquals(updatedObject[RapidCMDBConstants.INSERTED_AT_PROPERTY_NAME], updatedObject[RapidCMDBConstants.UPDATED_AT_PROPERTY_NAME]);
        assertEquals(objectBeforeAdd.id, updatedObject.id);
        assertEquals(objectBeforeAdd.prop2, updatedObject.prop2);
        assertEquals(objectBeforeAdd.prop3, updatedObject.prop3);
        assertEquals(objectBeforeAdd.nullableProp, updatedObject.nullableProp);
        assertEquals(0, AddMethodDomainObjectWithEvents.indexList.size());
        assertSame(updatedObject, addedObject);
        println AddMethodDomainObjectWithEvents.eventCalls

        assertEquals(1, AddMethodDomainObjectWithEvents.eventCalls.size());
        assertEquals("beforeUpdate", AddMethodDomainObjectWithEvents.eventCalls[0]);
        IdCacheEntry entryAfterUpdate = AddMethodDomainObjectWithEvents.idCache[updatedObject.prop1];
        assertSame(entry, entryAfterUpdate)
        assertTrue(entryAfterUpdate.exist());
        assertEquals(AddMethodDomainObjectWithEvents.class, entryAfterUpdate.alias);
        assertEquals(updatedObject.id, entryAfterUpdate.id);
        assertEquals("cache should not be updated since no property changed", numberOfUpdateCacheCall, AddMethodDomainObject1.updateCacheCallParams.size());
    }

    public void testUpdateMethodWillAddUpdatedPropsMapReturnedFromBeforeUpdate()
    {
        AddMethodDomainObjectWithEvents objectBeforeAdd = new AddMethodDomainObjectWithEvents(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value", prop4: 5);
        assertEquals("defaultValue", objectBeforeAdd.nullableProp);

        def relations = [:];
        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, AddMethodDomainObjectWithEvents, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations, ["prop1"]);
        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3];
        def addedObject = add.invoke(AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);
        addedObject.numberOfFlushCalls = 0;
        addedObject.isFlushedByProperty = [];
        objectBeforeAdd.id = addedObject.id;

        AddMethodDomainObjectWithEvents.eventCalls.clear();
        AddMethodDomainObjectWithEvents.indexList.clear();
        def afterUpdateCalled = false;
        // nullableProp:null change will be ignored because it will be converted to defaultValue in update Method
        addedObject.closureToBeInvokedAfterUpdate = {params ->
            afterUpdateCalled = true;
            assertEquals(3, params.updatedProps.size());
            assertEquals(objectBeforeAdd.prop2, params.updatedProps["prop2"]);
            assertEquals("updatedProp3Value", params.updatedProps["prop3"]);
            assertEquals(6, params.updatedProps["prop4"]);
        }


        MockObjectProcessorObserver observer = new MockObjectProcessorObserver();
        ObjectProcessor.getInstance().addObserver(observer);

        addedObject.updatedPropsMap = [prop3: "updatedProp3Value", prop4: 6]

        IdCacheEntry entry = AddMethodDomainObjectWithEvents.idCache[addedObject.prop1];
        def numberOfUpdateCacheCall = AddMethodDomainObject1.updateCacheCallParams.size();
        // nullableProp:null change will be ignored because it will be converted to defaultValue in update Method
        props = [prop1: objectBeforeAdd.prop1, prop2: "updatedProp2", nullableProp: null, prop4: 6];
        UpdateMethod update = new UpdateMethod(AddMethodDomainObjectWithEvents.metaClass, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations);
        AddMethodDomainObjectWithEvents updatedObject = update.invoke(addedObject, [props] as Object[]);

        assertTrue(afterUpdateCalled);

        assertEquals(1, observer.repositoryChanges.size());
        Map repositoryChange = observer.repositoryChanges[0];
        assertEquals(3, repositoryChange.size());
        assertEquals(EventTriggeringUtils.AFTER_UPDATE_EVENT, repositoryChange[ObjectProcessor.EVENT_NAME])
        assertEquals(updatedObject, repositoryChange[ObjectProcessor.DOMAIN_OBJECT])
        assertNotSame(updatedObject, repositoryChange[ObjectProcessor.DOMAIN_OBJECT])
        Map updatedProps = repositoryChange[ObjectProcessor.UPDATED_PROPERTIES]
        assertEquals(3, updatedProps.size())
        assertEquals(objectBeforeAdd.prop2, updatedProps.prop2);
        assertSame(addedObject.updatedPropsMap.prop3, updatedProps.prop3);
        assertSame(addedObject.updatedPropsMap.prop4, updatedProps.prop4);

    }

    public void testUpdateMethodWillReturnErrorsIfErrorsOccurredWhileConvertingStringProperties()
    {
        def dateFormat = "yyyy-dd-MM HH:mm:ss";
        RapidConvertUtils.getInstance().register(new DateConverter(dateFormat), Date.class)
        AddMethodDomainObjectWithEvents objectBeforeAdd = new AddMethodDomainObjectWithEvents(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value", prop5: new Date());
        def relations = [:];
        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, AddMethodDomainObjectWithEvents, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations, ["prop1"]);
        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3, prop5: objectBeforeAdd.prop5];
        def addedObject = add.invoke(AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);
        addedObject.numberOfFlushCalls = 0;
        addedObject.isFlushedByProperty = [];
        objectBeforeAdd.id = addedObject.id;

        AddMethodDomainObjectWithEvents.eventCalls.clear();
        AddMethodDomainObjectWithEvents.indexList.clear();
        props = [prop1: objectBeforeAdd.prop1, prop5: "invalid date prop"];
        UpdateMethod update = new UpdateMethod(AddMethodDomainObjectWithEvents.metaClass, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations);
        AddMethodDomainObjectWithEvents updatedObject = update.invoke(addedObject, [props] as Object[]);

        //id property will be ignored
        assertEquals(objectBeforeAdd.id, updatedObject.id);
        assertEquals(updatedObject[RapidCMDBConstants.INSERTED_AT_PROPERTY_NAME], updatedObject[RapidCMDBConstants.UPDATED_AT_PROPERTY_NAME]);
        assertEquals(objectBeforeAdd.prop5, updatedObject.prop5);
        assertEquals(0, AddMethodDomainObjectWithEvents.indexList.size());
        assertTrue(updatedObject.hasErrors());
        assertSame(updatedObject, addedObject);
        assertEquals(0, AddMethodDomainObjectWithEvents.eventCalls.size());
    }

    public void testUpdateMethodWillNotCallIndexButWillCallAddRemoveRelationsIfNoPropertyHasChangedButRelationsIsSpecified()
    {
        AddMethodDomainObjectWithEvents objectBeforeAdd = new AddMethodDomainObjectWithEvents(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");
        AddMethodDomainObjectWithEvents relatedObject = new AddMethodDomainObjectWithEvents(id: 100, prop1: "object2Prop1Value");

        def relations = ["rel1": new RelationMetaData("rel1", "revRel1", AddMethodDomainObjectWithEvents.class, AddMethodDomainObjectWithEvents.class, RelationMetaData.ONE_TO_ONE)];
        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, AddMethodDomainObjectWithEvents, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations, ["prop1"]);
        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3];
        def addedObject = add.invoke(AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);
        addedObject.numberOfFlushCalls = 0;
        addedObject.isFlushedByProperty = [];
        objectBeforeAdd.id = addedObject.id;

        AddMethodDomainObjectWithEvents.eventCalls.clear();
        AddMethodDomainObjectWithEvents.indexList.clear();
        def relsToBeRemoved = [new AddMethodDomainObjectWithEvents()];
        addedObject.rel1 = relsToBeRemoved;
        props["rel1"] = new AddMethodDomainObjectWithEvents();
        UpdateMethod update = new UpdateMethod(AddMethodDomainObjectWithEvents.metaClass, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations);
        AddMethodDomainObject1 updatedObject = update.invoke(addedObject, [props] as Object[]);

        //id property will be ignored
        assertEquals(objectBeforeAdd.id, updatedObject.id);
        assertEquals(updatedObject[RapidCMDBConstants.INSERTED_AT_PROPERTY_NAME], updatedObject[RapidCMDBConstants.UPDATED_AT_PROPERTY_NAME]);
        assertEquals(objectBeforeAdd.prop1, updatedObject.prop1);
        assertEquals(objectBeforeAdd.prop2, updatedObject.prop2);
        assertEquals(objectBeforeAdd.prop3, updatedObject.prop3);
        assertEquals(1, updatedObject.relationsShouldBeAdded.size());
        assertEquals(1, updatedObject.relationsShouldBeRemoved.size());
        assertSame(relsToBeRemoved[0], updatedObject.relationsShouldBeRemoved.rel1[0]);
        assertSame(props.rel1, updatedObject.relationsShouldBeAdded.rel1);
        assertEquals(0, AddMethodDomainObject1.indexList.size());
        assertSame(updatedObject, addedObject);

        assertEquals("beforeUpdate", AddMethodDomainObjectWithEvents.eventCalls[0])
        assertEquals("removeRelation", AddMethodDomainObjectWithEvents.eventCalls[1])
        assertEquals("addRelation", AddMethodDomainObjectWithEvents.eventCalls[2])
        assertEquals("afterUpdate", AddMethodDomainObjectWithEvents.eventCalls[3])
    }





    public void testUpdateMethodWithEvents()
    {
        def relations = ["rel1": new RelationMetaData("rel1", "revRel1", AddMethodDomainObjectWithEvents.class, AddMethodDomainObjectWithEvents.class, RelationMetaData.ONE_TO_ONE)];
        AddMethodDomainObjectWithEvents objectBeforeAdd = new AddMethodDomainObjectWithEvents(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");

        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations, ["prop1"]);

        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3, rel1: new AddMethodDomainObjectWithEvents()];

        AddMethodDomainObjectWithEvents addedObject = add.invoke(AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);

        AddMethodDomainObject1.indexList.clear();
        AddMethodDomainObjectWithEvents.eventCalls = [];
        props = [prop1: objectBeforeAdd.prop1, prop2: "newProp2Value", rel1: new AddMethodDomainObjectWithEvents()];

        def beforeUpdateParams = [];
        def afterUpdateParams = [];
        addedObject.closureToBeInvokedBeforeUpdate = {params ->
            beforeUpdateParams.add(params);
        }

        addedObject.closureToBeInvokedAfterUpdate = {params ->
            afterUpdateParams.add(params);
        }
        MockObjectProcessorObserver observer = new MockObjectProcessorObserver();
        ObjectProcessor.getInstance().addObserver(observer);
        UpdateMethod update = new UpdateMethod(AddMethodDomainObjectWithEvents.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, relations);
        def updatedObject = update.invoke(addedObject, [props] as Object[]);
        assertEquals(addedObject.id, updatedObject.id);
        assertEquals("newProp2Value", updatedObject.prop2);
        assertEquals(objectBeforeAdd.prop3, updatedObject.prop3);
        assertSame(updatedObject, AddMethodDomainObject1.indexList[0]);
        assertEquals(["beforeUpdate", "index", "removeRelation", "addRelation", "afterUpdate", "onLoad"], AddMethodDomainObjectWithEvents.eventCalls);


        assertEquals(1, beforeUpdateParams.size());
        Map params = beforeUpdateParams[0];
        assertEquals(2, params[UpdateMethod.UPDATED_PROPERTIES].size())
        assertEquals(objectBeforeAdd.prop2, params[UpdateMethod.UPDATED_PROPERTIES].prop2);
        assertSame(objectBeforeAdd.rel1, params[UpdateMethod.UPDATED_PROPERTIES].rel1);

        assertEquals(1, afterUpdateParams.size());
        params = afterUpdateParams[0];
        assertEquals(2, params[UpdateMethod.UPDATED_PROPERTIES].size())
        assertEquals(objectBeforeAdd.prop2, params[UpdateMethod.UPDATED_PROPERTIES].prop2);
        assertSame(objectBeforeAdd.rel1, params[UpdateMethod.UPDATED_PROPERTIES].rel1);

        assertEquals(1, observer.repositoryChanges.size());
        Map repositoryChange = observer.repositoryChanges[0];
        assertEquals(3, repositoryChange.size());
        assertEquals(EventTriggeringUtils.AFTER_UPDATE_EVENT, repositoryChange[ObjectProcessor.EVENT_NAME])
        assertEquals(updatedObject, repositoryChange[ObjectProcessor.DOMAIN_OBJECT])
        assertNotSame(updatedObject, repositoryChange[ObjectProcessor.DOMAIN_OBJECT])
        Map updatedProps = repositoryChange[ObjectProcessor.UPDATED_PROPERTIES]
        assertEquals(2, updatedProps.size())
        assertEquals(objectBeforeAdd.prop2, updatedProps.prop2);
        assertSame(objectBeforeAdd.rel1, updatedProps.rel1);
    }

    public void testBeforeUpdateWillBeCalledBeforeValidation()
    {
        AddMethodDomainObjectWithEvents objectBeforeAdd = new AddMethodDomainObjectWithEvents(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");

        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, [:], ["prop1"]);

        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3];

        AddMethodDomainObjectWithEvents addedObject = add.invoke(AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);

        AddMethodDomainObject1.indexList.clear();

        def propvalueToBeUpdatedInBeforeUpdate = "updatedValueInBeforeUpdate";
        addedObject.closureToBeInvokedBeforeUpdate = {params ->
            addedObject.setProperty("prop2", "updatedValueInBeforeUpdate", false);
        }

        props = [prop1: objectBeforeAdd.prop1, prop2: "newProp2Value"];
        MockValidator validator = new MockValidator();
        UpdateMethod update = new UpdateMethod(AddMethodDomainObjectWithEvents.metaClass, validator, AddMethodDomainObject1.allFields, [:]);
        def updatedObject = update.invoke(addedObject, [props] as Object[]);

        assertEquals(propvalueToBeUpdatedInBeforeUpdate, validator.validatedObject.prop2);

    }


    public void testUpdateMethodWithSettingRelationToNull()
    {
        AddMethodDomainObject1 objectBeforeAdd = new AddMethodDomainObject1(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");
        AddMethodDomainObject1 relatedObject1 = new AddMethodDomainObject1(id: 100);
        AddMethodDomainObject1 relatedObject2 = new AddMethodDomainObject1(id: 100);
        AddMethodDomainObject1 relatedObject3 = new AddMethodDomainObject1(id: 100);
        AddMethodDomainObject1 relatedObject4 = new AddMethodDomainObject1(id: 100);

        def relations = ["rel1": new RelationMetaData("rel1", "revRel1", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.ONE_TO_ONE),
                "rel2": new RelationMetaData("rel2", "revRel2", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.ONE_TO_MANY),
                "rel3": new RelationMetaData("rel3", "revRel3", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.ONE_TO_ONE),
                "rel4": new RelationMetaData("rel4", "revRel4", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.ONE_TO_MANY),
                "rel5": new RelationMetaData("rel5", "revRel5", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.ONE_TO_MANY),
                "rel6": new RelationMetaData("rel6", "revRel6", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.MANY_TO_ONE),
        ];
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObject1.allFields, relations, ["prop1"]);

        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop3: objectBeforeAdd.prop3];

        def addedObject = add.invoke(AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);
        addedObject.rel1 = relatedObject1;
        addedObject.rel2 = [];
        addedObject.rel2 += relatedObject2;
        addedObject.rel2 += relatedObject3;
        AddMethodDomainObject1.indexList.clear();

        props = [prop1: objectBeforeAdd.prop1, prop2: "newProp2Value", rel1: null, rel2: relatedObject4, rel3: [], rel4: [relatedObject4], rel5: null, rel6: null];
        def validator = new MockValidator();
        UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, validator, AddMethodDomainObject1.allFields, relations);
        AddMethodDomainObject1 updatedObject = update.invoke(addedObject, [props] as Object[]);
        assertEquals(addedObject.id, updatedObject.id);
        assertEquals("newProp2Value", updatedObject.prop2);
        assertEquals(objectBeforeAdd.prop3, updatedObject.prop3);

        assertEquals(2, updatedObject.relationsShouldBeAdded.size());
        assertEquals(2, updatedObject.relationsShouldBeRemoved.size());
        assertEquals(relatedObject4, updatedObject.relationsShouldBeAdded.get("rel2"));
        assertEquals(relatedObject4, updatedObject.relationsShouldBeAdded.get("rel4")[0]);
        assertEquals(relatedObject1, updatedObject.relationsShouldBeRemoved.get("rel1"));

        assertEquals(2, updatedObject.relationsShouldBeRemoved.get("rel2").size());
        assertEquals(relatedObject2, updatedObject.relationsShouldBeRemoved.get("rel2")[0]);
        assertEquals(relatedObject3, updatedObject.relationsShouldBeRemoved.get("rel2")[1]);
        assertEquals(1, AddMethodDomainObject1.indexList.size())
        assertSame(updatedObject, AddMethodDomainObject1.indexList[0]);
        assertNull(validator.validatedObject.rel1);
        assertNull(validator.validatedObject.rel6);
        assertNull(validator.validatedObject.rel3);
        assertEquals(1, validator.validatedObject.rel2.size());
        assertEquals(1, validator.validatedObject.rel4.size());
        assertEquals(0, validator.validatedObject.rel5.size());
        assertTrue(validator.validatedObject.rel2.contains(relatedObject4));
        assertTrue(validator.validatedObject.rel4.contains(relatedObject4));

        updatedObject.relationsShouldBeAdded.clear()
        updatedObject.relationsShouldBeRemoved.clear()

        addedObject.rel1 = null;

        props = [prop1: objectBeforeAdd.prop1, prop2: "newProp2Value", rel1: null];
        update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, relations);
        updatedObject = update.invoke(addedObject, [props] as Object[]);

        assertEquals(0, updatedObject.relationsShouldBeAdded.size());
        assertEquals(0, updatedObject.relationsShouldBeRemoved.size());

        addedObject.rel1 = [];

        update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, relations);
        updatedObject = update.invoke(addedObject, [props] as Object[]);

        assertEquals(0, updatedObject.relationsShouldBeAdded.size());
        assertEquals(0, updatedObject.relationsShouldBeRemoved.size());
    }

    public void testUpdateWithChildClass()
    {
        ChildAddMethodDomainObject objectBeforeAdd = new ChildAddMethodDomainObject(prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop6: "object1Prop6Value");

        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObject1.allFields, [:], ["prop1"]);

        def props = [prop1: objectBeforeAdd.prop1, prop2: objectBeforeAdd.prop2, prop6: objectBeforeAdd.prop6];

        def addedObject = add.invoke(ChildAddMethodDomainObject.class, [props] as Object[]);
        assertEquals(objectBeforeAdd, addedObject);

        props = [prop1: objectBeforeAdd.prop1, prop2: "newProp2Value", prop6: "newProp6Value"];
        UpdateMethod update = new UpdateMethod(ChildAddMethodDomainObject.metaClass, new MockValidator(), ChildAddMethodDomainObject.allFields, [:]);
        def updatedObject = update.invoke(addedObject, [props] as Object[]);
        assertEquals(addedObject.id, updatedObject.id);
        assertEquals("newProp2Value", updatedObject.prop2);
        assertEquals("newProp6Value", updatedObject.prop6);
        assertEquals(objectBeforeAdd.prop3, updatedObject.prop3);
    }

    public void testUpdateMethodWithStringProperties()
    {
        def prevDateConf = RapidConvertUtils.getInstance().lookup(Date);
        def prevLongConf = RapidConvertUtils.getInstance().lookup(Long);
        def prevDoubleConf = RapidConvertUtils.getInstance().lookup(Double);
        try
        {
            String dateFormatString = "yyyy-dd-MM";
            RapidConvertUtils.getInstance().register(new DateConverter(dateFormatString), Date.class)
            RapidConvertUtils.getInstance().register(new LongConverter(), Long.class)
            RapidConvertUtils.getInstance().register(new DoubleConverter(), Double.class)
            RapidConvertUtils.getInstance().register(new BooleanConverter(), Boolean.class)

            AddMethodDomainObject1 object = new AddMethodDomainObject1(id: 100, prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");
            AddMethodDomainObject1.indexList.clear();
            AddMethodDomainObject1.getCacheEntry(object).setProperties(AddMethodDomainObject1.class, object.id);
            def props = [prop1: object.prop1, prop2: "newProp2Value", prop4: "100", prop5: "2000-01-01", doubleProp: "5.0", booleanProp: "FAlse"];
            UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, [:]);
            def updatedObject = update.invoke(object, [props] as Object[]);
            assertEquals(100, updatedObject.id);
            assertEquals("newProp2Value", updatedObject.prop2);
            assertEquals("object1Prop3Value", updatedObject.prop3);
            assertEquals(100, updatedObject.prop4);
            assertEquals(new Double(5.0), updatedObject.doubleProp);
            assertEquals(new Boolean(false), updatedObject.booleanProp);
            SimpleDateFormat formater = new SimpleDateFormat(dateFormatString);
            assertEquals(formater.parse("2000-01-01"), updatedObject.prop5);
            assertSame(updatedObject, AddMethodDomainObject1.indexList[0]);

            AddMethodDomainObject1.indexList.clear();
            AddMethodDomainObject1.getCacheEntry(object).setProperties(AddMethodDomainObject1.class, object.id);
            props = [prop1: object.prop1, prop2: "", prop4: null, prop5: null, doubleProp: null, booleanProp: null];
            update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, [:]);
            updatedObject = update.invoke(object, [props] as Object[]);

            assertEquals("", updatedObject.prop2);
            assertEquals("object1Prop3Value", updatedObject.prop3);
            assertEquals(null, updatedObject.prop4);
            assertEquals(null, updatedObject.doubleProp);
            assertEquals(null, updatedObject.prop5);
            assertEquals(null, updatedObject.booleanProp);
            println updatedObject.booleanProp;
            assertSame(updatedObject, AddMethodDomainObject1.indexList[0]);

            AddMethodDomainObject1.indexList.clear();
            AddMethodDomainObject1.getCacheEntry(object).setProperties(AddMethodDomainObject1.class, object.id);
            props = [prop1: object.prop1, prop2: null, prop4: null, prop5: null, doubleProp: null, booleanProp: null];
            update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, [:]);
            updatedObject = update.invoke(object, [props] as Object[]);

            assertEquals(null, updatedObject.prop2);
            assertEquals("object1Prop3Value", updatedObject.prop3);
            assertEquals(null, updatedObject.prop4);
            assertEquals(null, updatedObject.doubleProp);
            assertEquals(null, updatedObject.prop5);
            assertEquals(null, updatedObject.booleanProp);
            assertSame(updatedObject, AddMethodDomainObject1.indexList[0]);

        }
        finally
        {
            RapidConvertUtils.getInstance().register(prevDateConf, Date.class)
            RapidConvertUtils.getInstance().register(prevLongConf, Long.class)
            RapidConvertUtils.getInstance().register(prevDoubleConf, Double.class)
        }
    }


    public void testIfInvalidPropertyPassedReturnsError()
    {

        def prevDateConf = RapidConvertUtils.getInstance().lookup(Date);
        def prevLongConf = RapidConvertUtils.getInstance().lookup(Long);
        def prevDoubleConf = RapidConvertUtils.getInstance().lookup(Double);
        try
        {
            String dateFormatString = "yyyy-dd-MM";
            RapidConvertUtils.getInstance().register(new DateConverter(dateFormatString), Date.class)
            RapidConvertUtils.getInstance().register(new LongConverter(), Long.class)
            RapidConvertUtils.getInstance().register(new DoubleConverter(), Double.class)

            AddMethodDomainObject1 object = new AddMethodDomainObject1(id: 100, prop1: "object1Prop1Value", prop2: "object1Prop2Value", prop3: "object1Prop3Value");
            AddMethodDomainObject1.getCacheEntry(object).setProperties(AddMethodDomainObject1.class, object.id);
            def props = [prop1: object.prop1, prop4: "invalidData", prop5: "invalidData", doubleProp: "invalidData"];
            UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, [:]);
            def updatedObject = update.invoke(object, [props] as Object[]);
            assertEquals(null, updatedObject.prop4);
            assertEquals(null, updatedObject.doubleProp);
            assertEquals(null, updatedObject.prop5);
            assertEquals(0, updatedObject[RapidCMDBConstants.UPDATED_AT_PROPERTY_NAME]);
            assertTrue(updatedObject.hasErrors());
            assertTrue(updatedObject.errors.toString().indexOf("Field error in object") >= 0);
            assertTrue(updatedObject.errors.toString().indexOf("prop4") >= 0);
            assertTrue(updatedObject.errors.toString().indexOf("prop5") >= 0);
            assertTrue(updatedObject.errors.toString().indexOf("doubleProp") >= 0);
            assertTrue(AddMethodDomainObject1.indexList.isEmpty());

        }
        finally
        {
            RapidConvertUtils.getInstance().register(prevDateConf, Date.class)
            RapidConvertUtils.getInstance().register(prevLongConf, Long.class)
            RapidConvertUtils.getInstance().register(prevDoubleConf, Double.class)
        }
    }

}
