package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.util.Relation
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.MockIdGeneratorStrategy
import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.converter.LongConverter
import java.text.SimpleDateFormat
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import org.springframework.validation.Errors
import com.ifountain.rcmdb.domain.converter.DoubleConverter
import org.springframework.validation.Validator
import org.codehaus.groovy.grails.validation.GrailsDomainClassValidator
import org.springframework.validation.FieldError
import com.ifountain.rcmdb.domain.util.ValidationUtils
import org.springframework.validation.BindingResult

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
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, validator, [:], []);
        def props = [prop1:expectedDomainObject1.prop1];
        def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals (expectedDomainObject1, addedObject);
        assertTrue (AddMethodDomainObject1.indexList[0].contains(addedObject));
        assertNull(addedObject.relationsShouldBeAdded)
        assertEquals("", AddMethodDomainObject1.query);
        def prevId = addedObject.id;

        AddMethodDomainObject1.indexList.clear();
        AddMethodDomainObject1 expectedDomainObject2 = new AddMethodDomainObject1(prop1:"object2Prop1Value");
        props = [prop1:expectedDomainObject2.prop1];
        addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals (expectedDomainObject2, addedObject);
        assertTrue (AddMethodDomainObject1.indexList[0].contains(addedObject));
        assertEquals (prevId+1, addedObject.id);
    }

    public void testAddMethodForAChildClass()
    {
        ChildAddMethodDomainObject expectedDomainObject1 = new ChildAddMethodDomainObject(prop1:"object1Prop1Value", prop6:"object1Prop6Value");
        AddMethod add = new AddMethod(ChildAddMethodDomainObject.metaClass, validator, [:], []);
        def props = [prop1:expectedDomainObject1.prop1, prop6:expectedDomainObject1.prop6];
        def addedObject = add.invoke (ChildAddMethodDomainObject.class, [props] as Object[]);
        assertEquals (expectedDomainObject1, addedObject);
        assertEquals (expectedDomainObject1.prop6, addedObject.prop6);
        assertTrue (ChildAddMethodDomainObject.indexList[0].contains(addedObject));
        assertNull(addedObject.relationsShouldBeAdded)
        assertEquals("", ChildAddMethodDomainObject.query);

    }

    public void testAddMethodWithEvents()
    {
        AddMethodDomainObjectWithEvents expectedDomainObject1 = new AddMethodDomainObjectWithEvents(prop1:"object1Prop1Value");
        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, validator, [:], ["prop1"]);
        def props = [prop1:expectedDomainObject1.prop1];
        def addedObject = add.invoke (AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertTrue (addedObject.isOnLoadCalled);
        assertTrue (addedObject.isBeforeInsertCalled);
        assertFalse (addedObject.isBeforeUpdateCalled);
        assertFalse (addedObject.isBeforeDeleteCalled);
        assertEquals (expectedDomainObject1, addedObject);
        assertTrue (AddMethodDomainObjectWithEvents.indexList[0].contains(addedObject));
        assertNull(addedObject.relationsShouldBeAdded)
        assertEquals("prop1:\"${expectedDomainObject1.prop1}\"", AddMethodDomainObjectWithEvents.query);
        
        AddMethodDomainObjectWithEvents.searchResult = [total:1, results:[expectedDomainObject1]];

        addedObject = add.invoke (AddMethodDomainObjectWithEvents.class, [props] as Object[]);

        assertTrue (addedObject.isOnLoadCalled);
        assertFalse (addedObject.isBeforeInsertCalled);
        assertTrue (addedObject.isBeforeUpdateCalled);
        assertFalse (addedObject.isBeforeDeleteCalled);
        assertEquals (expectedDomainObject1, addedObject);
        assertTrue (AddMethodDomainObjectWithEvents.indexList[0].contains(addedObject));
    }


    public void testIfKeyIsNullReturnsError()
    {
        fail("Implement later");
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, validator, [:], ["prop1"]);
        def props = [:];
        def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertTrue (addedObject.hasErrors());

        props = [prop1:"   "];
        addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertTrue (addedObject.hasErrors());
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
            AddMethodDomainObject1 expectedDomainObject1 = new AddMethodDomainObject1(prop1:"object1Prop1Value");
            AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, validator, [:], []);
            def props = [prop1:expectedDomainObject1.prop1,  prop4:"100", prop5:"2000-01-01", doubleProp:"5.0"];
            def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
            assertEquals (100, addedObject.prop4);
            assertEquals (new Double(5.0), addedObject.doubleProp);
            SimpleDateFormat formater = new SimpleDateFormat(dateFormatString)  ;
            assertEquals (formater.parse("2000-01-01"), addedObject.prop5);

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
            AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, validator, [:], []);
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
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, validator, [:], []);
        def props = [prop1:expectedDomainObject1.prop1];
        def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertTrue (addedObject.hasErrors());
        assertSame (validator.error, addedObject.errors.getAllErrors()[0])
        assertTrue (AddMethodDomainObject1.indexList.isEmpty());
    }

    public void testAddMethodWithUndefinedProperties()
    {
        AddMethodDomainObject1 expectedDomainObject1 = new AddMethodDomainObject1(prop1:"object1Prop1Value");
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, validator, [:], []);
        def props = [prop1:expectedDomainObject1.prop1, undefinedProperty:"undefinedProp"];
        def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals (expectedDomainObject1, addedObject);

    }

    public void testAddMethodWithRelationProperties()
    {
        AddMethodDomainObject1 expectedDomainObject1 = new AddMethodDomainObject1(prop1:"object1Prop1Value");
        AddMethodDomainObject1 expectedDomainObject2 = new AddMethodDomainObject1(id:100);
        AddMethodDomainObject1 expectedDomainObject3 = new AddMethodDomainObject1(id:101);
        AddMethodDomainObject1 expectedDomainObject4 = new AddMethodDomainObject1(id:102);

        def relations = ["rel1":new Relation("rel1", "revRel1", AddMethodDomainObject1.class, AddMethodDomainObject1.class, Relation.ONE_TO_ONE),
        "rel2":new Relation("rel2", "revRel2", AddMethodDomainObject1.class, AddMethodDomainObject1.class, Relation.ONE_TO_ONE)];

        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, validator, relations, []);
        def props = [prop1:expectedDomainObject1.prop1, rel1:[expectedDomainObject2, expectedDomainObject3], rel2:expectedDomainObject4];
        def addedObject1 = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);

        assertEquals(expectedDomainObject1.prop1, addedObject1.prop1);
        assertTrue(addedObject1.relationsShouldBeAdded.get("rel1").contains(expectedDomainObject2));
        assertTrue(addedObject1.relationsShouldBeAdded.get("rel1").contains(expectedDomainObject3));
        assertEquals(expectedDomainObject4, addedObject1.relationsShouldBeAdded.get("rel2"));
        assertEquals (1, AddMethodDomainObject1.indexList.size());
        assertTrue (AddMethodDomainObject1.indexList[0].contains(addedObject1));
    }


    public void testIfObjectAlreadyExistsUpdatesObjects()
    {
        AddMethodDomainObject1 objectBeforeAdd = new AddMethodDomainObject1(prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop3:"object1Prop3Value");
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, validator, [:], ["prop1"]);
        def props = [prop1:objectBeforeAdd.prop1, prop2:objectBeforeAdd.prop2, prop3:objectBeforeAdd.prop3];
        def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        def objectId = addedObject.id;
        assertEquals (objectBeforeAdd, addedObject);
        assertEquals("prop1:\"object1Prop1Value\"".toString(), AddMethodDomainObject1.query);

        AddMethodDomainObject1.searchResult = [total:1, results:[addedObject]];

        props = [prop1:objectBeforeAdd.prop1, prop2:"newProp2Value"];
        def addedObjectAfterAdd = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals (objectId, addedObjectAfterAdd.id);
        assertEquals ("newProp2Value", addedObjectAfterAdd.prop2);
        assertEquals (objectBeforeAdd.prop3, addedObjectAfterAdd.prop3);

        props = [prop1:objectBeforeAdd.prop1, prop2:null];
        addedObjectAfterAdd = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals (objectId, addedObjectAfterAdd.id);
        assertEquals (null, addedObjectAfterAdd.prop2);

        props = [prop1:objectBeforeAdd.prop1, prop2:""];
        addedObjectAfterAdd = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals (objectId, addedObjectAfterAdd.id);
        assertEquals ("", addedObjectAfterAdd.prop2);
    }
}

class AddMethodDomainObject1
{
    def static searchResult = [total:0, results:[]];
    def static query;
    def static indexList = [];
    def relationsShouldBeAdded;
    def relationsShouldBeRemoved;
    def rel1;
    Errors errors;
    String prop1;
    String prop2;
    String prop3;
    Long prop4;
    Date prop5;
    Double doubleProp;
    long id;
    def static search(queryClosure)
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

    def addRelation(Map relations)
    {
        relationsShouldBeAdded = relations;
    }

    def removeRelation(Map relations)
    {
        relationsShouldBeRemoved = relations;
    }

    public boolean equals(Object obj) {
        if(obj instanceof AddMethodDomainObject1)
        {
            return obj.prop1 == prop1;
        }
        return false;
    }
}


class ChildAddMethodDomainObject extends AddMethodDomainObject1
{
    String prop6;
}

class AddMethodDomainObjectWithEvents extends AddMethodDomainObject1
{
    boolean isOnLoadCalled = false;
    boolean isBeforeInsertCalled = false;
    boolean isBeforeUpdateCalled = false;
    boolean isBeforeDeleteCalled = false;

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
}

class MockValidator implements Validator{
    boolean supports = false;
    FieldError error;
    public boolean supports(Class aClass) {
        return supports; //To change body of implemented methods use File | Settings | File Templates.
    }

    public void validate(Object o, Errors errors) {
        if(error)
        {
            (( BindingResult ) errors).addError( error );
        }
    }

}
