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

import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.MockIdGeneratorStrategy
import com.ifountain.rcmdb.converter.DateConverter
import com.ifountain.rcmdb.converter.DoubleConverter
import com.ifountain.rcmdb.converter.LongConverter
import com.ifountain.rcmdb.converter.RapidConvertUtils
import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import java.text.SimpleDateFormat
import com.ifountain.rcmdb.converter.BooleanConverter
import com.ifountain.rcmdb.converter.DateConverter
import com.ifountain.rcmdb.converter.DoubleConverter
import com.ifountain.rcmdb.converter.RapidConvertUtils

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 15, 2008
* Time: 4:01:27 PM
* To change this template use File | Settings | File Templates.
*/
class UpdateMethodTest extends RapidCmdbTestCase{
     protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        IdGenerator.initialize (new MockIdGeneratorStrategy());
        AddMethodDomainObject1.searchResult =  [total:0, results:[]];
        AddMethodDomainObject1.query = null;
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
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testUpdateMethod()
    {
        AddMethodDomainObject1 objectBeforeAdd = new AddMethodDomainObject1(prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop3:"object1Prop3Value");
        AddMethodDomainObject1 relatedObject = new AddMethodDomainObject1(id:100, prop1:"object2Prop1Value");

        def relations = ["rel1":new RelationMetaData("rel1", "revRel1", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.ONE_TO_ONE)];
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObject1.allFields, relations, ["prop1"]);

        def props = [prop1:objectBeforeAdd.prop1, prop2:objectBeforeAdd.prop2, prop3:objectBeforeAdd.prop3];

        def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals (objectBeforeAdd, addedObject);
        addedObject.numberOfFlushCalls = 0;
        addedObject.isFlushedByProperty = [];
        objectBeforeAdd.id = addedObject.id;

        AddMethodDomainObject1.indexList.clear();
        props = [prop1:objectBeforeAdd.prop1, prop2:"newProp2Value", rel1:relatedObject, id:5000];
        UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, relations);
        assertTrue (update instanceof AbstractRapidDomainWriteMethod);
        AddMethodDomainObject1 updatedObject = update.invoke (addedObject, [props] as Object[]);

        //id property will be ignored
        assertEquals (objectBeforeAdd.id, updatedObject.id);
        assertEquals ("newProp2Value", updatedObject.prop2);
        assertEquals (objectBeforeAdd.prop3, updatedObject.prop3);
        assertEquals(2, addedObject.numberOfFlushCalls);
        assertFalse(addedObject.isFlushedByProperty[0]);
        assertFalse(addedObject.isFlushedByProperty[1]);
        assertEquals(relatedObject, updatedObject.relationsShouldBeAdded.get("rel1"));
        assertEquals (1, AddMethodDomainObject1.indexList.size());
        assertSame (updatedObject, AddMethodDomainObject1.indexList[0]);

        def propName = "id"
        props = ["$propName":5000];
        updatedObject = update.invoke (addedObject, [props] as Object[]);
        //id property will be ignored
        assertEquals (objectBeforeAdd.id, updatedObject.id);
    }

    


    public void testUpdateMethodWithEvents()
    {
        def relations = ["rel1":new RelationMetaData("rel1", "revRel1", AddMethodDomainObjectWithEvents.class, AddMethodDomainObjectWithEvents.class, RelationMetaData.ONE_TO_ONE)];
        AddMethodDomainObjectWithEvents objectBeforeAdd = new AddMethodDomainObjectWithEvents(prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop3:"object1Prop3Value");

        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, relations, ["prop1"]);

        def props = [prop1:objectBeforeAdd.prop1, prop2:objectBeforeAdd.prop2, prop3:objectBeforeAdd.prop3, rel1:new AddMethodDomainObjectWithEvents()];

        def addedObject = add.invoke (AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertEquals (objectBeforeAdd, addedObject);

        AddMethodDomainObject1.indexList.clear();
        addedObject.isBeforeInsertCalled = false;
        addedObject.isAfterInsertCalled = false;
        addedObject.isOnLoadCalled = false;
        props = [prop1:objectBeforeAdd.prop1, prop2:"newProp2Value", rel1:new AddMethodDomainObjectWithEvents()];

        def beforeUpdateParams = [];
        def afterUpdateParams = [];
        addedObject.closureToBeInvokedBeforeUpdate = {params->
            beforeUpdateParams.add(params);
        }

        addedObject.closureToBeInvokedAfterUpdate = {params->
            afterUpdateParams.add(params);
        }

        UpdateMethod update = new UpdateMethod(AddMethodDomainObjectWithEvents.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, relations);
        def updatedObject = update.invoke (addedObject, [props] as Object[]);
        assertEquals (addedObject.id, updatedObject.id);
        assertEquals ("newProp2Value", updatedObject.prop2);
        assertEquals (objectBeforeAdd.prop3, updatedObject.prop3);
        assertSame (updatedObject, AddMethodDomainObject1.indexList[0]);

        assertTrue (updatedObject.isOnLoadCalled);
        assertFalse (updatedObject.isBeforeInsertCalled);
        assertFalse (updatedObject.isAfterInsertCalled);
        assertTrue (updatedObject.isBeforeUpdateCalled);
        assertTrue (updatedObject.isAfterUpdateCalled);
        assertFalse (updatedObject.isBeforeDeleteCalled);
        assertFalse (updatedObject.isAfterDeleteCalled);
        assertEquals(1, beforeUpdateParams.size());
        Map params = beforeUpdateParams[0];
        assertEquals(objectBeforeAdd.prop1, params[UpdateMethod.UPDATED_PROPERTIES].prop1);
        assertEquals(objectBeforeAdd.prop2, params[UpdateMethod.UPDATED_PROPERTIES].prop2);
        assertSame(objectBeforeAdd.rel1, params[UpdateMethod.UPDATED_PROPERTIES].rel1);

        assertEquals(1, afterUpdateParams.size());
        params = afterUpdateParams[0];
        assertEquals(objectBeforeAdd.prop1, params[UpdateMethod.UPDATED_PROPERTIES].prop1);
        assertEquals(objectBeforeAdd.prop2, params[UpdateMethod.UPDATED_PROPERTIES].prop2);
        assertSame(objectBeforeAdd.rel1, params[UpdateMethod.UPDATED_PROPERTIES].rel1);
    }

    public void testBeforeUpdateWillBeCalledBeforeValidation()
    {
         AddMethodDomainObjectWithEvents objectBeforeAdd = new AddMethodDomainObjectWithEvents(prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop3:"object1Prop3Value");

        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObjectWithEvents.allFields, [:], ["prop1"]);

        def props = [prop1:objectBeforeAdd.prop1, prop2:objectBeforeAdd.prop2, prop3:objectBeforeAdd.prop3];

        AddMethodDomainObjectWithEvents addedObject = add.invoke (AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertEquals (objectBeforeAdd, addedObject);

        AddMethodDomainObject1.indexList.clear();
        addedObject.isBeforeInsertCalled = false;
        addedObject.isAfterInsertCalled = false;
        addedObject.isOnLoadCalled = false;
        
        def propvalueToBeUpdatedInBeforeUpdate = "updatedValueInBeforeUpdate";
        addedObject.closureToBeInvokedBeforeUpdate = {params->
            addedObject.setProperty("prop2", "updatedValueInBeforeUpdate", false);
        }

        props = [prop1:objectBeforeAdd.prop1, prop2:"newProp2Value"];
        MockValidator validator = new MockValidator();
        UpdateMethod update = new UpdateMethod(AddMethodDomainObjectWithEvents.metaClass, validator, AddMethodDomainObject1.allFields, [:]);
        def updatedObject = update.invoke (addedObject, [props] as Object[]);

        assertEquals (propvalueToBeUpdatedInBeforeUpdate, validator.validatedObject.prop2);


    }


    public void testUpdateMethodWithSettingRelationToNull()
    {
        AddMethodDomainObject1 objectBeforeAdd = new AddMethodDomainObject1(prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop3:"object1Prop3Value");
        AddMethodDomainObject1 relatedObject1 = new AddMethodDomainObject1(id:100);
        AddMethodDomainObject1 relatedObject2 = new AddMethodDomainObject1(id:100);
        AddMethodDomainObject1 relatedObject3 = new AddMethodDomainObject1(id:100);
        AddMethodDomainObject1 relatedObject4 = new AddMethodDomainObject1(id:100);

        def relations = ["rel1":new RelationMetaData("rel1", "revRel1", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.ONE_TO_ONE),
        "rel2":new RelationMetaData("rel2", "revRel2", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.ONE_TO_MANY)];
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObject1.allFields, relations, ["prop1"]);

        def props = [prop1:objectBeforeAdd.prop1, prop2:objectBeforeAdd.prop2, prop3:objectBeforeAdd.prop3];

        def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals (objectBeforeAdd, addedObject);
        addedObject.rel1 = relatedObject1;
        addedObject.rel2 = [];
        addedObject.rel2 += relatedObject2;
        addedObject.rel2 += relatedObject3;
        AddMethodDomainObject1.indexList.clear();

        props = [prop1:objectBeforeAdd.prop1, prop2:"newProp2Value", rel1:null, rel2:relatedObject4];
        def validator = new MockValidator();
        UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, validator, AddMethodDomainObject1.allFields, relations);
        AddMethodDomainObject1 updatedObject = update.invoke (addedObject, [props] as Object[]);
        assertEquals (addedObject.id, updatedObject.id);
        assertEquals ("newProp2Value", updatedObject.prop2);
        assertEquals (objectBeforeAdd.prop3, updatedObject.prop3);

        assertEquals(1, updatedObject.relationsShouldBeAdded.size());
        assertEquals(2, updatedObject.relationsShouldBeRemoved.size());
        assertEquals(relatedObject4, updatedObject.relationsShouldBeAdded.get("rel2"));
        assertEquals(relatedObject1, updatedObject.relationsShouldBeRemoved.get("rel1"));
        assertEquals(2, updatedObject.relationsShouldBeRemoved.get("rel2").size());
        assertEquals(relatedObject2, updatedObject.relationsShouldBeRemoved.get("rel2")[0]);
        assertEquals(relatedObject3, updatedObject.relationsShouldBeRemoved.get("rel2")[1]);
        assertEquals (1, AddMethodDomainObject1.indexList.size())
        assertSame (updatedObject, AddMethodDomainObject1.indexList[0]);
        assertNull(validator.validatedObject.rel1);
        assertEquals(1, validator.validatedObject.rel2.size());
        assertTrue(validator.validatedObject.rel2.contains(relatedObject4));

        updatedObject.relationsShouldBeAdded.clear()
        updatedObject.relationsShouldBeRemoved.clear()

        addedObject.rel1 = null;

        props = [prop1:objectBeforeAdd.prop1, prop2:"newProp2Value", rel1:null];
        update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, relations);
        updatedObject = update.invoke (addedObject, [props] as Object[]);

        assertEquals(0, updatedObject.relationsShouldBeAdded.size());
        assertEquals(0, updatedObject.relationsShouldBeRemoved.size());

        addedObject.rel1 = [];

        update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, relations);
        updatedObject = update.invoke (addedObject, [props] as Object[]);

        assertEquals(0, updatedObject.relationsShouldBeAdded.size());
        assertEquals(0, updatedObject.relationsShouldBeRemoved.size());
    }

    public void testUpdateWithChildClass()
    {
        ChildAddMethodDomainObject objectBeforeAdd = new ChildAddMethodDomainObject(prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop6:"object1Prop6Value");

        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1, new MockValidator(), AddMethodDomainObject1.allFields, [:], ["prop1"]);

        def props = [prop1:objectBeforeAdd.prop1, prop2:objectBeforeAdd.prop2, prop6:objectBeforeAdd.prop6];

        def addedObject = add.invoke (ChildAddMethodDomainObject.class, [props] as Object[]);
        assertEquals (objectBeforeAdd, addedObject);

        props = [prop1:objectBeforeAdd.prop1, prop2:"newProp2Value", prop6:"newProp6Value"];
        UpdateMethod update = new UpdateMethod(ChildAddMethodDomainObject.metaClass, new MockValidator(), ChildAddMethodDomainObject.allFields, [:]);
        def updatedObject = update.invoke (addedObject, [props] as Object[]);
        assertEquals (addedObject.id, updatedObject.id);
        assertEquals ("newProp2Value", updatedObject.prop2);
        assertEquals ("newProp6Value", updatedObject.prop6);
        assertEquals (objectBeforeAdd.prop3, updatedObject.prop3);
    }

    public void testUpdateMethodWithStringProperties()
    {
        def prevDateConf = RapidConvertUtils.getInstance().lookup (Date);
        def prevLongConf = RapidConvertUtils.getInstance().lookup (Long);
        def prevDoubleConf = RapidConvertUtils.getInstance().lookup (Double);
        try
        {
            String dateFormatString = "yyyy-dd-MM";
            RapidConvertUtils.getInstance().register (new DateConverter(dateFormatString), Date.class)
            RapidConvertUtils.getInstance().register (new LongConverter(), Long.class)
            RapidConvertUtils.getInstance().register (new DoubleConverter(), Double.class)
            RapidConvertUtils.getInstance().register (new BooleanConverter(), Boolean.class)

            AddMethodDomainObject1 object = new AddMethodDomainObject1(id:100, prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop3:"object1Prop3Value");
            AddMethodDomainObject1.indexList.clear();
            def props = [prop1:object.prop1, prop2:"newProp2Value",  prop4:"100", prop5:"2000-01-01", doubleProp:"5.0", booleanProp:"FAlse"];
            UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, [:]);
            def updatedObject = update.invoke (object, [props] as Object[]);
            assertEquals (100, updatedObject.id);
            assertEquals ("newProp2Value", updatedObject.prop2);
            assertEquals ("object1Prop3Value", updatedObject.prop3);
            assertEquals (100, updatedObject.prop4);
            assertEquals (new Double(5.0), updatedObject.doubleProp);
            assertEquals (new Boolean(false), updatedObject.booleanProp);
            SimpleDateFormat formater = new SimpleDateFormat(dateFormatString)  ;
            assertEquals (formater.parse("2000-01-01"), updatedObject.prop5);
            assertSame (updatedObject, AddMethodDomainObject1.indexList[0]);

            AddMethodDomainObject1.indexList.clear();
            props = [prop1:object.prop1, prop2:"",  prop4:"", prop5:"", doubleProp:"", booleanProp:""];
            update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, [:]);
            updatedObject = update.invoke (object, [props] as Object[]);

            assertEquals ("", updatedObject.prop2);
            assertEquals ("object1Prop3Value", updatedObject.prop3);
            assertEquals (null, updatedObject.prop4);
            assertEquals (null, updatedObject.doubleProp);
            assertEquals (null, updatedObject.prop5);
            assertEquals (null, updatedObject.booleanProp);
            println updatedObject.booleanProp;
            assertSame (updatedObject, AddMethodDomainObject1.indexList[0]);

            AddMethodDomainObject1.indexList.clear();
            props = [prop1:object.prop1, prop2:null,  prop4:null, prop5:null, doubleProp:null, booleanProp:null];
            update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, [:]);
            updatedObject = update.invoke (object, [props] as Object[]);

            assertEquals (null, updatedObject.prop2);
            assertEquals ("object1Prop3Value", updatedObject.prop3);
            assertEquals (null, updatedObject.prop4);
            assertEquals (null, updatedObject.doubleProp);
            assertEquals (null, updatedObject.prop5);
            assertEquals (null, updatedObject.booleanProp);
            assertSame (updatedObject, AddMethodDomainObject1.indexList[0]);
            

        }
        finally
        {
            RapidConvertUtils.getInstance().register (prevDateConf, Date.class)
            RapidConvertUtils.getInstance().register (prevLongConf, Long.class)
            RapidConvertUtils.getInstance().register (prevDoubleConf, Double.class)
        }
    }


    public void testIfInvalidPropertyPassedReturnsError()
    {

        def prevDateConf = RapidConvertUtils.getInstance().lookup (Date);
        def prevLongConf = RapidConvertUtils.getInstance().lookup (Long);
        def prevDoubleConf = RapidConvertUtils.getInstance().lookup (Double);
        try
        {
            String dateFormatString = "yyyy-dd-MM";
            RapidConvertUtils.getInstance().register (new DateConverter(dateFormatString), Date.class)
            RapidConvertUtils.getInstance().register (new LongConverter(), Long.class)
            RapidConvertUtils.getInstance().register (new DoubleConverter(), Double.class)

            AddMethodDomainObject1 object = new AddMethodDomainObject1(id:100, prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop3:"object1Prop3Value");

            def props = [prop1:object.prop1,  prop4:"invalidData", prop5:"invalidData", doubleProp:"invalidData"];
            UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), AddMethodDomainObject1.allFields, [:]);
            def updatedObject = update.invoke (object, [props] as Object[]);
            assertEquals (null, updatedObject.prop4);
            assertEquals (null, updatedObject.doubleProp);
            assertEquals (null, updatedObject.prop5);
            assertTrue (updatedObject.hasErrors());
            assertTrue(updatedObject.errors.toString().indexOf("Field error in object") >= 0);
            assertTrue(updatedObject.errors.toString().indexOf("prop4") >= 0);
            assertTrue(updatedObject.errors.toString().indexOf("prop5") >= 0);
            assertTrue(updatedObject.errors.toString().indexOf("doubleProp") >= 0);
            assertTrue (AddMethodDomainObject1.indexList.isEmpty());


        }
        finally
        {
            RapidConvertUtils.getInstance().register (prevDateConf, Date.class)
            RapidConvertUtils.getInstance().register (prevLongConf, Long.class)
            RapidConvertUtils.getInstance().register (prevDoubleConf, Double.class)
        }
    }

}
