package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.util.Relation
import org.codehaus.groovy.grails.orm.hibernate.metaclass.AbstractSavePersistentMethod
import org.springframework.validation.BeanPropertyBindingResult
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.MockIdGeneratorStrategy

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 15, 2008
* Time: 8:56:03 AM
* To change this template use File | Settings | File Templates.
*/
class AddRelationMethodTest extends RapidCmdbTestCase{

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        IdGenerator.initialize (new MockIdGeneratorStrategy());
        RelationMethodDomainObject1.indexList = [];
        RelationMethodDomainObject2.indexList = [];
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testAddMethodWithOneToOneRelations()
    {
        def fromRelation = "rel1"
        def toRelation = "revRel1"
        def fromRelationClass = RelationMethodDomainObject1.class;
        def toRelationClass = RelationMethodDomainObject2.class;
        RelationMethodDomainObject1 expectedDomainObject1 = new RelationMethodDomainObject1(id:1);
        RelationMethodDomainObject2 expectedDomainObject2 = new RelationMethodDomainObject2(id:2);
        RelationMethodDomainObject2 expectedDomainObject3 = new RelationMethodDomainObject2(id:3);

        def relationsForObject1 = ["rel1":new Relation(fromRelation, toRelation, fromRelationClass, toRelationClass, Relation.ONE_TO_ONE)]

        AddRelationMethod add = new AddRelationMethod(RelationMethodDomainObject1.metaClass, relationsForObject1);
        def props = [rel1:expectedDomainObject2];
        add.invoke (expectedDomainObject1, [props] as Object[]);

        assertEquals(expectedDomainObject2, expectedDomainObject1.rel1);
        assertEquals(expectedDomainObject1, expectedDomainObject2.revRel1);
        assertEquals (1, RelationMethodDomainObject1.indexList.size());
        assertTrue (RelationMethodDomainObject1.indexList[0].contains(expectedDomainObject1));
        assertEquals (1, RelationMethodDomainObject2.indexList.size());
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(expectedDomainObject2));

        RelationMethodDomainObject2.indexList = []
        RelationMethodDomainObject1.indexList = []

        props = [rel1:expectedDomainObject3];
        add.invoke (expectedDomainObject1, [props] as Object[]);
        assertEquals(expectedDomainObject3, expectedDomainObject1.rel1);
        assertEquals(expectedDomainObject1, expectedDomainObject3.revRel1);
        assertNull(expectedDomainObject2.revRel1);

        assertEquals (1, RelationMethodDomainObject1.indexList.size());
        assertTrue (RelationMethodDomainObject1.indexList[0].contains(expectedDomainObject1));
        assertEquals (1, RelationMethodDomainObject2.indexList.size());
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(expectedDomainObject3));
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(expectedDomainObject2));
    }

    public void testAddRelationMethodWithOneToManyRelations()
    {
        def fromRelation = "rel2"
        def toRelation = "revRel2"
        def fromRelationClass = RelationMethodDomainObject1.class;
        def toRelationClass = RelationMethodDomainObject2.class;
        RelationMethodDomainObject1 relatedDomainObject1 = new RelationMethodDomainObject1(id:1);
        RelationMethodDomainObject2 relatedDomainObject2 = new RelationMethodDomainObject2(id:2);
        RelationMethodDomainObject2 relatedDomainObject3 = new RelationMethodDomainObject2(id:3);
        RelationMethodDomainObject1 relatedDomainObject4 = new RelationMethodDomainObject1(id:4);

        def relations = ["rel2":new Relation(fromRelation, toRelation, fromRelationClass, toRelationClass, Relation.ONE_TO_MANY)]
        AddRelationMethod add = new AddRelationMethod(RelationMethodDomainObject1.metaClass, relations);
        def props = [rel2:[relatedDomainObject2, relatedDomainObject3]];
        add.invoke (relatedDomainObject1, [props] as Object[]);

        assertEquals(relatedDomainObject1, relatedDomainObject2.revRel2);
        assertEquals(relatedDomainObject1, relatedDomainObject3.revRel2);
        assertTrue(relatedDomainObject1.rel2.contains(relatedDomainObject2));
        assertTrue(relatedDomainObject1.rel2.contains(relatedDomainObject3));
        assertEquals (1, RelationMethodDomainObject2.indexList.size());
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(relatedDomainObject2));
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(relatedDomainObject3));
        assertEquals (1, RelationMethodDomainObject1.indexList.size());
        assertTrue (RelationMethodDomainObject1.indexList[0].contains(relatedDomainObject1));

        RelationMethodDomainObject2.indexList = []
        RelationMethodDomainObject1.indexList = []
        props = [rel2:[relatedDomainObject2]];
        add.invoke (relatedDomainObject4, [props] as Object[]);

        assertEquals(relatedDomainObject4, relatedDomainObject2.revRel2);
        assertTrue(relatedDomainObject4.rel2.contains(relatedDomainObject2));
        assertEquals(relatedDomainObject1, relatedDomainObject2.relationsToBeRemoved.get("revRel2"))
        assertEquals (1, RelationMethodDomainObject1.indexList.size());
        assertTrue (RelationMethodDomainObject1.indexList[0].contains(relatedDomainObject4));
        assertEquals (1, RelationMethodDomainObject2.indexList.size());
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(relatedDomainObject2));
    }


    public void testAddRelationMethodWithManyToOneRelations()
    {
        def fromRelation = "rel3"
        def toRelation = "revRel3"
        def fromRelationClass = RelationMethodDomainObject1.class;
        def toRelationClass = RelationMethodDomainObject2.class;
        RelationMethodDomainObject1 relatedDomainObject1 = new RelationMethodDomainObject1(id:1);
        RelationMethodDomainObject1 relatedDomainObject2 = new RelationMethodDomainObject1(id:2);
        RelationMethodDomainObject2 relatedDomainObject3 = new RelationMethodDomainObject2(id:3);
        RelationMethodDomainObject2 relatedDomainObject4 = new RelationMethodDomainObject2(id:4);

        def relations = ["rel3":new Relation(fromRelation, toRelation, fromRelationClass, toRelationClass, Relation.MANY_TO_ONE)]
        AddRelationMethod add = new AddRelationMethod(RelationMethodDomainObject1.metaClass, relations);
        def props = [rel3:relatedDomainObject3];
        add.invoke (relatedDomainObject1, [props] as Object[]);
        add.invoke (relatedDomainObject2, [props] as Object[]);

        assertEquals(relatedDomainObject3, relatedDomainObject1.rel3);
        assertEquals(relatedDomainObject3, relatedDomainObject2.rel3);
        assertTrue(relatedDomainObject3.revRel3.contains(relatedDomainObject1));
        assertTrue(relatedDomainObject3.revRel3.contains(relatedDomainObject2));
        assertEquals (2, RelationMethodDomainObject1.indexList.size());
        assertTrue (RelationMethodDomainObject1.indexList[0].contains(relatedDomainObject1));
        assertTrue (RelationMethodDomainObject1.indexList[1].contains(relatedDomainObject2));
        assertEquals (2, RelationMethodDomainObject2.indexList.size());
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(relatedDomainObject3));


        RelationMethodDomainObject2.indexList = []
        RelationMethodDomainObject1.indexList = []
        props = [rel3:[relatedDomainObject4]];
        add.invoke (relatedDomainObject2, [props] as Object[]);

        assertEquals(relatedDomainObject4, relatedDomainObject2.rel3);
        assertTrue(relatedDomainObject4.revRel3.contains(relatedDomainObject2));
        assertEquals(relatedDomainObject3, relatedDomainObject2.relationsToBeRemoved.get("rel3"))
        assertEquals (1, RelationMethodDomainObject1.indexList.size());
        assertTrue (RelationMethodDomainObject1.indexList[0].contains(relatedDomainObject2));
        assertEquals (1, RelationMethodDomainObject2.indexList.size());
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(relatedDomainObject4));
    }

    public void testAddRelationMethodWithManyToManyRelations()
    {
        def fromRelation = "rel4"
        def toRelation = "revRel4"
        def fromRelationClass = RelationMethodDomainObject1.class;
        def toRelationClass = RelationMethodDomainObject2.class;
        RelationMethodDomainObject1 relatedDomainObject1 = new RelationMethodDomainObject1(id:1);
        RelationMethodDomainObject2 relatedDomainObject2 = new RelationMethodDomainObject2(id:2);

        def relations = ["rel4":new Relation(fromRelation,toRelation, fromRelationClass, toRelationClass, Relation.MANY_TO_MANY)]
        AddRelationMethod add = new AddRelationMethod(RelationMethodDomainObject1.metaClass, relations);
        add = new AddRelationMethod(RelationMethodDomainObject1.metaClass, relations);
        def props = [rel4:relatedDomainObject2];
        add.invoke (relatedDomainObject1, [props] as Object[]);

        assertTrue(relatedDomainObject1.rel4.contains(relatedDomainObject2));
        assertTrue(relatedDomainObject2.revRel4.contains(relatedDomainObject1));
        assertEquals (1, RelationMethodDomainObject1.indexList.size());
        assertTrue (RelationMethodDomainObject1.indexList[0].contains(relatedDomainObject1));
        assertEquals (1, RelationMethodDomainObject2.indexList.size());
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(relatedDomainObject2));
    }

    public void testWithMultipleRelations()
    {
        RelationMethodDomainObject1 relatedDomainObject1 = new RelationMethodDomainObject1(id:1);
        RelationMethodDomainObject2 relatedDomainObject2 = new RelationMethodDomainObject2(id:2);
        RelationMethodDomainObject2 relatedDomainObject3 = new RelationMethodDomainObject2(id:3);
        RelationMethodDomainObject2 relatedDomainObject4 = new RelationMethodDomainObject2(id:4);
        RelationMethodDomainObject2 relatedDomainObject5 = new RelationMethodDomainObject2(id:5);

        def relations = ["rel1":new Relation("rel1","revRel1", RelationMethodDomainObject1.class, RelationMethodDomainObject2.class, Relation.ONE_TO_ONE),
        "rel2":new Relation("rel2","revRel2", RelationMethodDomainObject1.class, RelationMethodDomainObject2.class, Relation.ONE_TO_MANY),
        "rel4":new Relation("rel4","revRel4", RelationMethodDomainObject1.class, RelationMethodDomainObject2.class, Relation.MANY_TO_MANY)];
        AddRelationMethod add = new AddRelationMethod(RelationMethodDomainObject1.metaClass, relations);
        add = new AddRelationMethod(RelationMethodDomainObject1.metaClass, relations);
        def props = [rel1:relatedDomainObject2, rel2:[relatedDomainObject3,relatedDomainObject4], rel4:relatedDomainObject5];
        add.invoke (relatedDomainObject1, [props] as Object[]);

        assertEquals(relatedDomainObject2, relatedDomainObject1.rel1);
        assertTrue(relatedDomainObject1.rel2.contains(relatedDomainObject3));
        assertTrue(relatedDomainObject1.rel2.contains(relatedDomainObject4));
        assertTrue(relatedDomainObject1.rel4.contains(relatedDomainObject5));
        assertTrue(relatedDomainObject5.revRel4.contains(relatedDomainObject1));
        assertEquals(relatedDomainObject1, relatedDomainObject2.revRel1);
        assertEquals(relatedDomainObject1, relatedDomainObject3.revRel2);
        assertEquals(relatedDomainObject1, relatedDomainObject4.revRel2);

        assertEquals (1, RelationMethodDomainObject1.indexList.size());
        assertTrue (RelationMethodDomainObject1.indexList[0].contains(relatedDomainObject1));
        assertEquals (1, RelationMethodDomainObject2.indexList.size());
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(relatedDomainObject2));
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(relatedDomainObject3));
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(relatedDomainObject4));
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(relatedDomainObject5));
    }

    public void testIfOneToOneRelationAlreadyExistsDoesnotAddTwice()
    {
        RelationMethodDomainObject1 relatedDomainObject1 = new RelationMethodDomainObject1(id:1);
        RelationMethodDomainObject2 relatedDomainObject2 = new RelationMethodDomainObject2(id:2);

        def relations = ["rel1":new Relation("rel1","revRel1", RelationMethodDomainObject1.class, RelationMethodDomainObject2.class, Relation.ONE_TO_ONE)]
        AddRelationMethod add = new AddRelationMethod(RelationMethodDomainObject1.metaClass, relations);
        add = new AddRelationMethod(RelationMethodDomainObject1.metaClass, relations);
        def props = [rel1:relatedDomainObject2];
        add.invoke (relatedDomainObject1, [props] as Object[]);
        add.invoke (relatedDomainObject1, [props] as Object[]);

        assertEquals(relatedDomainObject2, relatedDomainObject1.rel1);
        assertEquals(relatedDomainObject1, relatedDomainObject2.revRel1);
        assertEquals(1, relatedDomainObject2.indexList.size());
        assertEquals(1, relatedDomainObject1.indexList.size());
    }

    public void testIfOneToManyRelationAlreadyExistsDoesnotAddTwice()
    {
        RelationMethodDomainObject1 relatedDomainObject1 = new RelationMethodDomainObject1(id:1);
        RelationMethodDomainObject2 relatedDomainObject2 = new RelationMethodDomainObject2(id:2);

        def relations = ["rel2":new Relation("rel2","revRel2", RelationMethodDomainObject1.class, RelationMethodDomainObject2.class, Relation.ONE_TO_MANY)]
        AddRelationMethod add = new AddRelationMethod(RelationMethodDomainObject1.metaClass, relations);
        add = new AddRelationMethod(RelationMethodDomainObject1.metaClass, relations);
        def props = [rel2:relatedDomainObject2];
        add.invoke (relatedDomainObject1, [props] as Object[]);
        add.invoke (relatedDomainObject1, [props] as Object[]);

        assertTrue(relatedDomainObject1.rel2.contains(relatedDomainObject2));
        assertEquals(relatedDomainObject1, relatedDomainObject2.revRel2);
        assertEquals(1, relatedDomainObject2.indexList.size());
        assertEquals(1, relatedDomainObject1.indexList.size());
    }

}

class RelationMethodDomainObject1
{
    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(this,getClass().getName());
    def static indexList = [];
    def relationsToBeRemoved;
    RelationMethodDomainObject2 rel1;
    HashSet rel2 = [];
    RelationMethodDomainObject2 rel3;
    HashSet rel4 = [];
    long id;
    boolean hasErrors()
    {
        return errors.hasErrors();
    }
    def static index(objectList)
    {
        indexList.add(objectList);
    }

    def removeRelation(Map relations)
    {
        relationsToBeRemoved = relations;
    }

    public boolean equals(Object obj) {
        if(obj instanceof RelationMethodDomainObject1)
        {
            return obj.id == id;
        }
        return false;
    }

}

class RelationMethodDomainObject2
{
    def static indexList = [];
    def relationsToBeRemoved;
    RelationMethodDomainObject1 revRel1;
    RelationMethodDomainObject1 revRel2;
    HashSet revRel4 = [];
    HashSet revRel3 = [];
    String prop1;
    long id;


    def static index(objectList)
    {
        indexList.add(objectList);
    }

    def removeRelation(Map relations)
    {
        relationsToBeRemoved = relations;
    }
    public boolean equals(Object obj) {
        if(obj instanceof RelationMethodDomainObject2)
        {
            return obj.id == id;
        }
        return false;
    }
}