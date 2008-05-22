package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.compass.core.Compass
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import com.ifountain.rcmdb.domain.util.Relation
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.MockIdGeneratorStrategy
import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.converter.LongConverter
import org.apache.commons.beanutils.ConvertUtils
import java.text.SimpleDateFormat
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 13, 2008
* Time: 4:04:02 PM
* To change this template use File | Settings | File Templates.
*/
class AddMethodTest extends RapidCmdbTestCase{
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

    public void testAddMethod()
    {
        AddMethodDomainObject1 expectedDomainObject1 = new AddMethodDomainObject1(prop1:"object1Prop1Value");
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, [:], []);
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

    public void testAddMethodWithStringProperties()
    {
        def prevDateConf = RapidConvertUtils.getInstance().lookup (Date);
        def prevLongConf = RapidConvertUtils.getInstance().lookup (Long);
        try
        {
            String dateFormatString = "yyyy-dd-MM";
            RapidConvertUtils.getInstance().register (new DateConverter(dateFormatString), Date.class)
            RapidConvertUtils.getInstance().register (new LongConverter(), Long.class)
            AddMethodDomainObject1 expectedDomainObject1 = new AddMethodDomainObject1(prop1:"object1Prop1Value");
            AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, [:], []);
            def props = [prop1:expectedDomainObject1.prop1,  prop4:"100", prop5:"2000-01-01"];
            def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
            assertEquals (100, addedObject.prop4);
            SimpleDateFormat formater = new SimpleDateFormat(dateFormatString)  ;
            assertEquals (formater.parse("2000-01-01"), addedObject.prop5);
        }
        finally
        {
            RapidConvertUtils.getInstance().register (prevDateConf, Date.class)
            RapidConvertUtils.getInstance().register (prevLongConf, Long.class)
        }
    }

    public void testAddMethodWithUndefinedProperties()
    {
        AddMethodDomainObject1 expectedDomainObject1 = new AddMethodDomainObject1(prop1:"object1Prop1Value");
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, [:], []);
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

        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, relations, []);
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
        AddMethod add = new AddMethod(AddMethodDomainObject1.metaClass, [:], ["prop1"]);
        def props = [prop1:objectBeforeAdd.prop1, prop2:objectBeforeAdd.prop2, prop3:objectBeforeAdd.prop3];
        def addedObject = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        def objectId = addedObject.id;
        assertEquals (objectBeforeAdd, addedObject);
        assertEquals("prop1:object1Prop1Value".toString(), AddMethodDomainObject1.query);

        AddMethodDomainObject1.searchResult = [total:1, results:[addedObject]];

        props = [prop1:objectBeforeAdd.prop1, prop2:"newProp2Value"];
        def addedObjectAfterAdd = add.invoke (AddMethodDomainObject1.class, [props] as Object[]);
        assertEquals (objectId, addedObjectAfterAdd.id);
        assertEquals ("newProp2Value", addedObjectAfterAdd.prop2);
        assertEquals (objectBeforeAdd.prop3, addedObjectAfterAdd.prop3);
    }
}

class AddMethodDomainObject1
{
    def static searchResult = [total:0, results:[]];
    def static query;
    def static indexList = [];
    def relationsShouldBeAdded;
    String prop1;
    String prop2;
    String prop3;
    Long prop4;
    Date prop5;
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

    def addRelation(Map relations)
    {
        relationsShouldBeAdded = relations;
    }

    public boolean equals(Object obj) {
        if(obj instanceof AddMethodDomainObject1)
        {
            return obj.prop1 == prop1;
        }
        return false;
    }
}

