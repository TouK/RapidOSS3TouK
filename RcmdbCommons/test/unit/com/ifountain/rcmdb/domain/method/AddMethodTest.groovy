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
import org.springframework.validation.BindingResult
import org.springframework.validation.Errors
import org.springframework.validation.FieldError
import org.springframework.validation.Validator
import com.ifountain.rcmdb.converter.BooleanConverter
import com.ifountain.rcmdb.converter.BooleanConverter
import com.ifountain.rcmdb.converter.DateConverter
import com.ifountain.rcmdb.converter.LongConverter
import com.ifountain.rcmdb.converter.RapidConvertUtils

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 13, 2008
* Time: 4:04:02 PM
* To change this template use File | Settings | File Templates.
*/
class AddMethodTest extends RapidCmdbTestCase{
    MockValidator validator;
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        IdGenerator.initialize (new MockIdGeneratorStrategy());
        AddMethodDomainObject1.searchResult =  [total:0, results:[]];
        AddMethodDomainObject1.query = null;
        AddMethodDomainObject1.indexList = [];
        validator = new MockValidator();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testAddMethod()
    {
        AddMethodDomainObject1 expectedDomainObject1 = new AddMethodDomainObject1(prop1:"object1Prop1Value");
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1.class, validator, AddMethodDomainObject1.allFields, [:], ["prop1"]);
        assertTrue (add.isWriteOperation());

        def props = [prop1:expectedDomainObject1.prop1, id:-5000];
        AddMethodDomainObject1 addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals (expectedDomainObject1, addedObject);
        assertTrue (AddMethodDomainObject1.indexList[0].contains(addedObject));
        assertNull(addedObject.relationsShouldBeAdded)
        assertEquals("prop1:\"object1Prop1Value\"", AddMethodDomainObject1.query);
        assertEquals (2, addedObject.numberOfFlushCalls);
        assertFalse (addedObject.isFlushedByProperty[0]);
        assertFalse (addedObject.isFlushedByProperty[1]);
        def prevId = addedObject.id;

        AddMethodDomainObject1.indexList.clear();
        AddMethodDomainObject1 expectedDomainObject2 = new AddMethodDomainObject1(prop1:"object2Prop1Value");
        props = [prop1:expectedDomainObject2.prop1];
        addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals (expectedDomainObject2, addedObject);
        assertTrue (AddMethodDomainObject1.indexList[0].contains(addedObject));
        assertEquals (prevId+1, addedObject.id);
    }

    public void testGetLockName()
    {
        AddMethodDomainObject1 expectedDomainObject1 = new AddMethodDomainObject1(prop1:"object1Prop1Value");
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1.class, validator, AddMethodDomainObject1.allFields, [:], ["prop1"]);
        String lockName = add.getLockName ([[prop1:"prop1Value", prop2:"prop2Value"]] as Object[]);
        assertEquals(AddMethodDomainObject1.name+"prop1Value", lockName);
        add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1.class, validator, AddMethodDomainObject1.allFields, [:], ["prop1", "prop2"]);
        lockName = add.getLockName ([[prop1:"prop1Value", prop2:"prop2Value"]] as Object[]);
        assertEquals(AddMethodDomainObject1.name+"prop1Value"+"prop2Value", lockName);

        add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1.class, validator, AddMethodDomainObject1.allFields, [:], []);
        lockName = add.getLockName ([[prop1:"prop1Value", prop2:"prop2Value"]] as Object[]);
        assertEquals(null, lockName);
    }

    public void testAddMethodForAChildClass()
    {
        ChildAddMethodDomainObject expectedDomainObject1 = new ChildAddMethodDomainObject(prop1:"object1Prop1Value", prop6:"object1Prop6Value");
        AddMethod add = new AddMethod(ChildAddMethodDomainObject.metaClass, AddMethodDomainObject1.class, validator, ChildAddMethodDomainObject.allFields, [:], ["prop1"]);
        def props = [prop1:expectedDomainObject1.prop1, prop6:expectedDomainObject1.prop6];
        def addedObject = add.invoke (ChildAddMethodDomainObject.class, [props] as Object[]);
        assertEquals (expectedDomainObject1, addedObject);
        assertEquals (expectedDomainObject1.prop6, addedObject.prop6);
        assertTrue (ChildAddMethodDomainObject.indexList[0].contains(addedObject));
        assertNull(addedObject.relationsShouldBeAdded)
        assertEquals("prop1:\"object1Prop1Value\"", AddMethodDomainObject1.query);

    }

    public void testAddMethodForAChildGeneratesErrorIfKeysExistsForAnotherChildOfParentClass()
    {
        ChildAddMethodDomainObject expectedDomainObject1 = new ChildAddMethodDomainObject(prop1:"object1Prop1Value", prop6:"object1Prop6Value");
        AddMethod add = new AddMethod(ChildAddMethodDomainObject.metaClass, AddMethodDomainObject1.class, validator, ChildAddMethodDomainObject.allFields, [:], ["prop1"]);
        AddMethod add2 = new AddMethod(ChildAddMethodDomainObject2.metaClass, AddMethodDomainObject1.class, validator, ChildAddMethodDomainObject2.allFields, [:], ["prop1"]);


        
        def props = [prop1:expectedDomainObject1.prop1];
        def addedObject = add.invoke (ChildAddMethodDomainObject.class, [props] as Object[]);
        AddMethodDomainObjectWithEvents.searchResult = [total:1, results:[expectedDomainObject1]];

        def addedObject2 = add2.invoke (ChildAddMethodDomainObject2.class, [props] as Object[]);        
        assertTrue(addedObject2.hasErrors())

        /*
        assertEquals (expectedDomainObject1, addedObject);
        assertEquals (expectedDomainObject1.prop6, addedObject.prop6);
        assertTrue (ChildAddMethodDomainObject.indexList[0].contains(addedObject));
        assertNull(addedObject.relationsShouldBeAdded)
        assertEquals("prop1:\"object1Prop1Value\"", AddMethodDomainObject1.query);
        */
    }
    

    public void testAddMethodWithEvents()
    {
        AddMethodDomainObjectWithEvents expectedDomainObject1 = new AddMethodDomainObjectWithEvents(prop1:"object1Prop1Value");
        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, AddMethodDomainObject1.class, validator, AddMethodDomainObjectWithEvents.allFields, [:], ["prop1"]);
        def props = [prop1:expectedDomainObject1.prop1];
        def addedObject = add.invoke (AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertTrue (addedObject.isOnLoadCalled);
        assertTrue (addedObject.isBeforeInsertCalled);
        assertTrue (addedObject.isAfterInsertCalled);
        assertFalse (addedObject.isBeforeUpdateCalled);
        assertFalse (addedObject.isAfterUpdateCalled);
        assertFalse (addedObject.isBeforeDeleteCalled);
        assertFalse (addedObject.isAfterDeleteCalled);
        assertEquals (expectedDomainObject1, addedObject);
        assertTrue (AddMethodDomainObjectWithEvents.indexList[0].contains(addedObject));
        assertNull(addedObject.relationsShouldBeAdded)
        assertEquals("prop1:\"${expectedDomainObject1.prop1}\"", AddMethodDomainObjectWithEvents.query);
        
        AddMethodDomainObjectWithEvents.searchResult = [total:1, results:[expectedDomainObject1]];

        addedObject = add.invoke (AddMethodDomainObjectWithEvents.class, [props] as Object[]);

        assertFalse (addedObject.isOnLoadCalled);
        assertFalse (addedObject.isBeforeInsertCalled);
        assertFalse (addedObject.isAfterInsertCalled);
        assertFalse (addedObject.isBeforeDeleteCalled);
        assertFalse (addedObject.isAfterDeleteCalled);
    }


    public void testAddMethodWithStringProperties()
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
            AddMethodDomainObject1 expectedDomainObject1 = new AddMethodDomainObject1(prop1:"object1Prop1Value");
            AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1.class, validator, AddMethodDomainObject1.allFields, [:], ["prop1"]);
            def props = [prop1:expectedDomainObject1.prop1,  prop4:"100", prop5:"2000-01-01", doubleProp:"5.0", booleanProp:"TrUe"];
            def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
            assertEquals (100, addedObject.prop4);
            assertEquals (new Double(5.0), addedObject.doubleProp);
            SimpleDateFormat formater = new SimpleDateFormat(dateFormatString)  ;
            assertEquals (formater.parse("2000-01-01"), addedObject.prop5);
            assertEquals (new Boolean(true), addedObject.booleanProp);

            props = [prop1:expectedDomainObject1.prop1,  prop4:"", prop5:"", doubleProp:""];
            addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);

            assertEquals (null, addedObject.prop4);
            assertEquals (null, addedObject.doubleProp);
            assertEquals (null, addedObject.prop5);


            props = [prop1:expectedDomainObject1.prop1,  prop4:null, prop5:null, doubleProp:null];
            addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);

            assertEquals (null, addedObject.prop4);
            assertEquals (null, addedObject.doubleProp);
            assertEquals (null, addedObject.prop5);
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
            AddMethodDomainObject1 expectedDomainObject1 = new AddMethodDomainObject1(prop1:"object1Prop1Value");
            AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1.class, validator, AddMethodDomainObject1.allFields, [:], ["prop1"]);
            def props = [prop1:expectedDomainObject1.prop1,  prop4:"invalidData", prop5:"invalidData", doubleProp:"invalidData"];
            def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
            assertEquals (null, addedObject.prop4);
            assertEquals (null, addedObject.doubleProp);
            assertEquals (null, addedObject.prop5);
            assertTrue (addedObject.hasErrors());
            assertTrue(addedObject.errors.toString().indexOf("Field error in object") >= 0);
            assertTrue(addedObject.errors.toString().indexOf("prop4") >= 0);
            assertTrue(addedObject.errors.toString().indexOf("prop5") >= 0);
            assertTrue(addedObject.errors.toString().indexOf("doubleProp") >= 0);
            assertTrue (AddMethodDomainObject1.indexList.isEmpty());
        }
        finally
        {
            RapidConvertUtils.getInstance().register (prevDateConf, Date.class)
            RapidConvertUtils.getInstance().register (prevLongConf, Long.class)
            RapidConvertUtils.getInstance().register (prevDoubleConf, Double.class)
        }
    }


    public void testAddMethodWithInvalidData()
    {
        validator.supports = true;
        validator.error = new FieldError( AddMethodDomainObject1.class.name, "prop1","value1",false,[] as String[], [] as Object[], "");
        AddMethodDomainObject1 expectedDomainObject1 = new AddMethodDomainObject1(prop1:"object1Prop1Value");
        
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1.class, validator, AddMethodDomainObject1.allFields, [:], ["prop1"]);
        def props = [prop1:expectedDomainObject1.prop1];
        def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertTrue (addedObject.hasErrors());
        assertSame (validator.error, addedObject.errors.getAllErrors()[0])
        assertTrue (AddMethodDomainObject1.indexList.isEmpty());
    }

    public void testAddMethodWithUndefinedProperties()
    {
        AddMethodDomainObject1 expectedDomainObject1 = new AddMethodDomainObject1(prop1:"object1Prop1Value");
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1.class, validator, AddMethodDomainObject1.allFields, [:], ["prop1"]);
        def props = [prop1:expectedDomainObject1.prop1, undefinedProperty:"undefinedProp"];
        def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals (expectedDomainObject1, addedObject);

    }

    public void testAddMethodWithRelationProperties()
    {
        AddMethodDomainObject1 expectedDomainObject1 = new AddMethodDomainObject1(prop1:"object1Prop1Value");
        AddMethodDomainObject1 expectedDomainObject2 = new AddMethodDomainObject1(id:100, prop1:"object2Prop1Value");
        AddMethodDomainObject1 expectedDomainObject3 = new AddMethodDomainObject1(id:101, prop1:"object3Prop1Value");
        AddMethodDomainObject1 expectedDomainObject4 = new AddMethodDomainObject1(id:102, prop1:"object4Prop1Value");
        AddMethodDomainObject1 expectedDomainObject5 = new AddMethodDomainObject1(id:103, prop1:"object4Prop1Value");
        AddMethodDomainObject1 expectedDomainObject6 = new AddMethodDomainObject1(id:104, prop1:"object4Prop1Value");

        def relations = ["rel1":new RelationMetaData("rel1", "revRel1", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.ONE_TO_ONE),
        "rel2":new RelationMetaData("rel2", "revRel2", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.ONE_TO_MANY),
        "rel3":new RelationMetaData("rel3", "revRel3", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.MANY_TO_ONE),
        "rel4":new RelationMetaData("rel4", "revRel4", AddMethodDomainObject1.class, AddMethodDomainObject1.class, RelationMetaData.MANY_TO_MANY)];

        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1.class, validator, AddMethodDomainObject1.allFields, relations, ["prop1"]);
        def props = [prop1:expectedDomainObject1.prop1, rel1:[expectedDomainObject2, expectedDomainObject3], rel2:expectedDomainObject4, rel3:expectedDomainObject5, rel4:[expectedDomainObject6]];

        AddMethodDomainObject1 addedObject1 = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);


        assertEquals(expectedDomainObject1.prop1, addedObject1.prop1);
        assertTrue(addedObject1.relationsShouldBeAdded.get("rel1").contains(expectedDomainObject2));
        assertTrue(addedObject1.relationsShouldBeAdded.get("rel1").contains(expectedDomainObject3));
        assertEquals(expectedDomainObject4, addedObject1.relationsShouldBeAdded.get("rel2"));
        assertEquals(expectedDomainObject5, addedObject1.relationsShouldBeAdded.get("rel3"));
        assertTrue(addedObject1.relationsShouldBeAdded.get("rel4").contains(expectedDomainObject6));

        assertEquals (1, AddMethodDomainObject1.indexList.size());
        assertTrue (AddMethodDomainObject1.indexList[0].contains(addedObject1));

        assertEquals (expectedDomainObject2, validator.validatedObject.rel1);
        assertEquals (1, validator.validatedObject.rel2.size());
        assertTrue (validator.validatedObject.rel2.contains(expectedDomainObject4));
        assertEquals (expectedDomainObject5, validator.validatedObject.rel3);
        assertTrue (validator.validatedObject.rel4.contains(expectedDomainObject6));


    }


    public void testIfObjectAlreadyExistsUpdatesObjects()
    {
        AddMethodDomainObject1 objectBeforeAdd = new AddMethodDomainObject1(prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop3:"object1Prop3Value");
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, AddMethodDomainObject1.class, validator, AddMethodDomainObject1.allFields, [:], ["prop1"]);
        def props = [prop1:objectBeforeAdd.prop1, prop2:objectBeforeAdd.prop2, prop3:objectBeforeAdd.prop3];
        def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        def objectId = addedObject.id;
        assertEquals (objectBeforeAdd, addedObject);
        assertEquals("prop1:\"object1Prop1Value\"".toString(), AddMethodDomainObject1.query);

        AddMethodDomainObject1.searchResult = [total:1, results:[addedObject]];

        props = [prop1:objectBeforeAdd.prop1, prop2:"newProp2Value"];
        AddMethodDomainObject1 addedObjectAfterAdd = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        
        assertEquals (props, addedObjectAfterAdd.propertiesToBeUpdated);
    }
}

class AddMethodDomainObject1  extends GroovyObjectSupport
{
    def static allFields = ["rel1":[type:Object], "rel2":[type:Object], "rel3":[type:Object], "rel4":[type:Object], "prop1":[type:String], "prop2":[type:String], "prop3":[type:String], "prop4":[type:Long], "prop5":[type:Date], "doubleProp":[type:Double], "booleanProp":[type:Boolean], "id":[type:Long]];
    def static searchResult = [total:0, results:[]];
    def static query;
    def static indexList = [];
    def relationsShouldBeAdded;
    def relationsShouldBeRemoved;
    def propertiesToBeUpdated;
    def rel1;
    def rel2;
    def rel3;
    def rel4;
    int numberOfFlushCalls = 0;
    List isFlushedByProperty = [];
    Errors errors;
    String prop1;
    String prop2;
    String prop3;
    Long prop4;
    Date prop5;
    Double doubleProp;
    Boolean booleanProp;
    long id;
    def static searchWithoutTriggering(queryClosure)
    {
        AddMethodDomainObject1.query = queryClosure;
        return searchResult
    }

    def static index(objectList)
    {
        indexList.add(objectList);
    }


    public boolean hasErrors()
    {
        return errors && errors.hasErrors();
    }
    def update(Map props)
    {
        propertiesToBeUpdated = props;
        return this;
    }
    def addRelation(Map relations)
    {
        relationsShouldBeAdded = relations;
        return this;
    }

    def removeRelation(Map relations)
    {
        relationsShouldBeRemoved = relations;
        return this;
    }

    public boolean equals(Object obj) {
        if(obj instanceof AddMethodDomainObject1)
        {
            return obj.prop1 == prop1;
        }
        return false;
    }

    public void setProperty(String propName, Object propValue)
    {
        setProperty (propName, propValue, true);        
    }

    public void setProperty(String propName, Object propValue, boolean flush)
    {
        super.setProperty (propName, propValue);
        if(propName == "prop1" || propName == "prop2" || propName == "prop3"
                || propName == "prop4" || propName == "prop5" || propName == "prop6"
                || propName == "rel1" || propName == "doubleProp" || propName == "id")
        {
            numberOfFlushCalls++;
            this.isFlushedByProperty += flush;
        }
    }
}


class ChildAddMethodDomainObject extends AddMethodDomainObject1
{
    def static searchResult = [total:0, results:[]];
    def static query;
    def static allFields = ["rel1":[type:Object], "rel2":[type:Object], "prop1":[type:String], "prop2":[type:String], "prop3":[type:String], "prop4":[type:Long], "prop5":[type:Date], "prop6":[type:String], "doubleProp":[type:Double], "booleanProp":[type:Boolean], "id":[type:Long]];
    def static searchWithoutTriggering(queryClosure)
    {
        ChildAddMethodDomainObject.query = queryClosure;
        return searchResult
    }
    String prop6;
}

class ChildAddMethodDomainObject2 extends AddMethodDomainObject1
{
    def static searchResult = [total:0, results:[]];
    def static query;
    def static allFields = ["rel1":[type:Object], "rel2":[type:Object], "prop1":[type:String], "prop2":[type:String], "prop3":[type:String], "prop4":[type:Long], "prop5":[type:Date], "prop6":[type:String], "doubleProp":[type:Double], "booleanProp":[type:Boolean], "id":[type:Long]];
    def static searchWithoutTriggering(queryClosure)
    {
        ChildAddMethodDomainObject.query = queryClosure;
        return searchResult
    }
    String prop6;
}

class AddMethodDomainObjectWithEvents extends AddMethodDomainObject1
{
    boolean isOnLoadCalled = false;
    boolean isBeforeInsertCalled = false;
    boolean isAfterInsertCalled = false;
    boolean isBeforeUpdateCalled = false;
    boolean isAfterUpdateCalled = false;
    boolean isBeforeDeleteCalled = false;
    boolean isAfterDeleteCalled = false;

    def onLoad = {
        isOnLoadCalled = true;
    }

    def beforeInsert = {
        isBeforeInsertCalled = true;
    }
    def beforeUpdate = {
        isBeforeUpdateCalled = true;
    }
    def beforeDelete = {
        isBeforeDeleteCalled = true;
    }

    def afterInsert = {
        isAfterInsertCalled = true;
    }
    def afterUpdate = {
        isAfterUpdateCalled = true;
    }
    def afterDelete = {
        isAfterDeleteCalled = true;
    }
}

class MockValidator implements Validator{
    boolean supports = false;
    FieldError error;
    def validatedObject;
    public boolean supports(Class aClass) {
        return supports; //To change body of implemented methods use File | Settings | File Templates.
    }

    public void validate(Object o, Errors errors) {
        validatedObject = o;
        if(error)
        {
            (( BindingResult ) errors).addError( error );
        }
    }

}
