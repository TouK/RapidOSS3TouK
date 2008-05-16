package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.util.Relation
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.MockIdGeneratorStrategy

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
        UpdateMethodDomainObjecty objectBeforeAdd = new UpdateMethodDomainObjecty(prop1:"object1Prop1Value", prop2:"object1Prop2Value", prop3:"object1Prop3Value");
        UpdateMethodDomainObjecty relatedObject = new UpdateMethodDomainObjecty(id:100);

        def relations = ["rel1":new Relation("rel1", "revRel1", UpdateMethodDomainObjecty.class, UpdateMethodDomainObjecty.class, Relation.ONE_TO_ONE)];
        AddMethod add = new AddMethod(UpdateMethodDomainObjecty.metaClass, relations, ["prop1"]);
        
        def props = [prop1:objectBeforeAdd.prop1, prop2:objectBeforeAdd.prop2, prop3:objectBeforeAdd.prop3];

        def addedObject = add.invoke (UpdateMethodDomainObjecty.class, [props] as Object[]);
        assertEquals (objectBeforeAdd, addedObject);

        props = [prop1:objectBeforeAdd.prop1, prop2:"newProp2Value", rel1:relatedObject];
        UpdateMethod update = new UpdateMethod(UpdateMethodDomainObjecty.metaClass, relations, ["prop1"]);
        def updatedObject = update.invoke (addedObject, [props] as Object[]);
        assertEquals (addedObject.id, updatedObject.id);
        assertEquals ("newProp2Value", updatedObject.prop2);
        assertEquals (objectBeforeAdd.prop3, updatedObject.prop3);

        assertEquals(relatedObject, updatedObject.relationsToBeRemoved.get("rel1"));
    }
}

class UpdateMethodDomainObjecty extends AddMethodDomainObject1
{
    def relationsToBeRemoved;
    def removeRelation(Map relationsToBeRemoved)
    {
        this.relationsToBeRemoved = relationsToBeRemoved;
    }
}