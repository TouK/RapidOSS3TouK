package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.util.Relation
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.MockIdGeneratorStrategy
import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.converter.LongConverter
import org.apache.commons.beanutils.ConvertUtils
import java.text.SimpleDateFormat
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import com.ifountain.rcmdb.domain.converter.DoubleConverter

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
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testUpdateMethod()
    {
        AddMethodDomainObject1 objectBeforeAdd = new AddMethodDomainObject1(prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop3:"object1Prop3Value");
        AddMethodDomainObject1 relatedObject = new AddMethodDomainObject1(id:100);

        def relations = ["rel1":new Relation("rel1", "revRel1", AddMethodDomainObject1.class, AddMethodDomainObject1.class, Relation.ONE_TO_ONE)];
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, new MockValidator(), relations, ["prop1"]);

        def props = [prop1:objectBeforeAdd.prop1, prop2:objectBeforeAdd.prop2, prop3:objectBeforeAdd.prop3];

        def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals (objectBeforeAdd, addedObject);
        addedObject.numberOfFlushCalls = 0;
        addedObject.isFlushedByProperty = false;
        props = [prop1:objectBeforeAdd.prop1, prop2:"newProp2Value", rel1:relatedObject];
        UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), relations);
        def updatedObject = update.invoke (addedObject, [props] as Object[]);
        assertEquals (addedObject.id, updatedObject.id);
        assertEquals ("newProp2Value", updatedObject.prop2);
        assertEquals (objectBeforeAdd.prop3, updatedObject.prop3);
        assertEquals(2, addedObject.numberOfFlushCalls);
        assertFalse(addedObject.isFlushedByProperty);
        assertEquals(relatedObject, updatedObject.relationsShouldBeAdded.get("rel1"));
    }
    public void testUpdateMethodWithEvents()
    {
        AddMethodDomainObjectWithEvents objectBeforeAdd = new AddMethodDomainObjectWithEvents(prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop3:"object1Prop3Value");

        AddMethod add = new AddMethod(AddMethodDomainObjectWithEvents.metaClass, new MockValidator(), [:], ["prop1"]);

        def props = [prop1:objectBeforeAdd.prop1, prop2:objectBeforeAdd.prop2, prop3:objectBeforeAdd.prop3];

        def addedObject = add.invoke (AddMethodDomainObjectWithEvents.class, [props] as Object[]);
        assertEquals (objectBeforeAdd, addedObject);
        addedObject.isBeforeInsertCalled = false;
        addedObject.isOnLoadCalled = false;
        props = [prop1:objectBeforeAdd.prop1, prop2:"newProp2Value"];
        UpdateMethod update = new UpdateMethod(AddMethodDomainObjectWithEvents.metaClass, new MockValidator(), [:]);
        def updatedObject = update.invoke (addedObject, [props] as Object[]);
        assertEquals (addedObject.id, updatedObject.id);
        assertEquals ("newProp2Value", updatedObject.prop2);
        assertEquals (objectBeforeAdd.prop3, updatedObject.prop3);

        assertTrue (updatedObject.isOnLoadCalled);
        assertFalse (updatedObject.isBeforeInsertCalled);
        assertTrue (updatedObject.isBeforeUpdateCalled);
        assertFalse (updatedObject.isBeforeDeleteCalled);
    }


    public void testUpdateMethodWithSettingRelationToNull()
    {
        AddMethodDomainObject1 objectBeforeAdd = new AddMethodDomainObject1(prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop3:"object1Prop3Value");
        AddMethodDomainObject1 relatedObject = new AddMethodDomainObject1(id:100);

        def relations = ["rel1":new Relation("rel1", "revRel1", AddMethodDomainObject1.class, AddMethodDomainObject1.class, Relation.ONE_TO_ONE)];
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, new MockValidator(), relations, ["prop1"]);

        def props = [prop1:objectBeforeAdd.prop1, prop2:objectBeforeAdd.prop2, prop3:objectBeforeAdd.prop3];

        def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals (objectBeforeAdd, addedObject);
        addedObject.rel1 = relatedObject;

        props = [prop1:objectBeforeAdd.prop1, prop2:"newProp2Value", rel1:null];
        UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), relations);
        def updatedObject = update.invoke (addedObject, [props] as Object[]);
        assertEquals (addedObject.id, updatedObject.id);
        assertEquals ("newProp2Value", updatedObject.prop2);
        assertEquals (objectBeforeAdd.prop3, updatedObject.prop3);

        assertEquals(0, updatedObject.relationsShouldBeAdded.size());
        assertEquals(relatedObject, updatedObject.relationsShouldBeRemoved.get("rel1"));

        addedObject.rel1 = null;

        update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), relations);
        updatedObject = update.invoke (addedObject, [props] as Object[]);

        assertEquals(0, updatedObject.relationsShouldBeAdded.size());
        assertEquals(0, updatedObject.relationsShouldBeRemoved.size());

        addedObject.rel1 = [];

        update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), relations);
        updatedObject = update.invoke (addedObject, [props] as Object[]);

        assertEquals(0, updatedObject.relationsShouldBeAdded.size());
        assertEquals(0, updatedObject.relationsShouldBeRemoved.size());
    }

    public void testUpdateWithChildClass()
    {
        ChildAddMethodDomainObject objectBeforeAdd = new ChildAddMethodDomainObject(prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop6:"object1Prop6Value");

        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, new MockValidator(), [:], ["prop1"]);

        def props = [prop1:objectBeforeAdd.prop1, prop2:objectBeforeAdd.prop2, prop6:objectBeforeAdd.prop6];

        def addedObject = add.invoke (ChildAddMethodDomainObject.class, [props] as Object[]);
        assertEquals (objectBeforeAdd, addedObject);

        props = [prop1:objectBeforeAdd.prop1, prop2:"newProp2Value", prop6:"newProp6Value"];
        UpdateMethod update = new UpdateMethod(ChildAddMethodDomainObject.metaClass, new MockValidator(), [:]);
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

            AddMethodDomainObject1 object = new AddMethodDomainObject1(id:100, prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop3:"object1Prop3Value");

            def props = [prop1:object.prop1, prop2:"newProp2Value",  prop4:"100", prop5:"2000-01-01", doubleProp:"5.0"];
            UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), [:]);
            def updatedObject = update.invoke (object, [props] as Object[]);
            assertEquals (100, updatedObject.id);
            assertEquals ("newProp2Value", updatedObject.prop2);
            assertEquals ("object1Prop3Value", updatedObject.prop3);
            assertEquals (100, updatedObject.prop4);
            assertEquals (new Double(5.0), updatedObject.doubleProp);
            SimpleDateFormat formater = new SimpleDateFormat(dateFormatString)  ;
            assertEquals (formater.parse("2000-01-01"), updatedObject.prop5);

            props = [prop1:object.prop1, prop2:"",  prop4:"", prop5:"", doubleProp:""];
            update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), [:]);
            updatedObject = update.invoke (object, [props] as Object[]);

            assertEquals ("", updatedObject.prop2);
            assertEquals ("object1Prop3Value", updatedObject.prop3);
            assertEquals (null, updatedObject.prop4);
            assertEquals (null, updatedObject.doubleProp);
            assertEquals (null, updatedObject.prop5);

            props = [prop1:object.prop1, prop2:null,  prop4:null, prop5:null, doubleProp:null];
            update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), [:]);
            updatedObject = update.invoke (object, [props] as Object[]);

            assertEquals (null, updatedObject.prop2);
            assertEquals ("object1Prop3Value", updatedObject.prop3);
            assertEquals (null, updatedObject.prop4);
            assertEquals (null, updatedObject.doubleProp);
            assertEquals (null, updatedObject.prop5);
            

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
            UpdateMethod update = new UpdateMethod(AddMethodDomainObject1.metaClass, new MockValidator(), [:]);
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