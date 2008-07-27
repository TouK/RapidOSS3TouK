package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.util.Relation

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 15, 2008
* Time: 5:12:58 PM
* To change this template use File | Settings | File Templates.
*/
class GetMethodTest extends RapidCmdbTestCase{

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        GetMethodDomainObject.query = null;
        GetMethodDomainObject.searchResult = [total:0, results:[]];
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testGetMethodWithMap()
    {
        def keys = ["prop1",  "prop2"]
        GetMethod get = new GetMethod(GetMethodDomainObject.metaClass, keys, [:]);
        assertFalse (get.isWriteOperation());
        def result = get.invoke (GetMethodDomainObject, [[prop1:"prop1Value", prop3:"prop3Value"]] as Object[]);
        assertNull (result);
        assertEquals ("prop1:\"prop1Value\" AND prop2:\"null\"", GetMethodDomainObject.query);

        GetMethodDomainObject objectWillBeReturned = new GetMethodDomainObject(prop1:"prop1Value", prop2:"prop2Value")
        GetMethodDomainObject.searchResult = [total:1, results:[objectWillBeReturned]];
        result = get.invoke (GetMethodDomainObject, [[prop1:"prop1Value", prop2:"prop2Value"]] as Object[]);
        assertEquals (objectWillBeReturned, result);
        assertEquals ("prop1:\"prop1Value\" AND prop2:\"prop2Value\"", GetMethodDomainObject.query);
    }


    public void testGetMethodWithMapContainingId()
    {
        def keys = ["prop1",  "prop2"]
        GetMethod get = new GetMethod(GetMethodDomainObject.metaClass, keys, [:]);
        def result = get.invoke (GetMethodDomainObject, [[prop1:"prop1Value", prop3:"prop3Value", id:1000]] as Object[]);
        assertNull (result);
        assertEquals ("id:\"1000\"", GetMethodDomainObject.query);
    }

    public void testGetMethodWithNumber()
    {
        def keys = ["prop1",  "prop2"]
        GetMethod get = new GetMethod(GetMethodDomainObject.metaClass, keys, [:]);
        def result = get.invoke (GetMethodDomainObject, [1000] as Object[]);
        assertNull (result);
        assertNotNull (GetMethodDomainObject.query);

        GetMethodDomainObject objectWillBeReturned = new GetMethodDomainObject(prop1:"prop1Value", prop2:"prop2Value")
        GetMethodDomainObject.searchResult = [total:1, results:[objectWillBeReturned]];
        result = get.invoke (GetMethodDomainObject, [1000] as Object[]);
        assertEquals (objectWillBeReturned, result);
        assertNotNull (GetMethodDomainObject.query);
    }

    public void testGetMethodWithRelationAndAPropertyAsKey()
    {
        def keys = ["prop1",  "rel1"]
        def relations = ["rel1":new Relation("rel1", "revRel1", GetMethodDomainObject, RelationMethodDomainObject2, Relation.ONE_TO_ONE)]
        RelationMethodDomainObject2 relatedObject1 = new RelationMethodDomainObject2(id:1);
        RelationMethodDomainObject2 relatedObject2 = new RelationMethodDomainObject2(id:2);
        GetMethodDomainObject objectWillBeReturned1 = new GetMethodDomainObject(id:3, prop1:"prop1Value", prop2:"prop2Value", rel1:relatedObject1)
        GetMethodDomainObject objectWillBeReturned2 = new GetMethodDomainObject(id:4, prop1:"prop1Value", prop2:"prop2Value", rel1:relatedObject2)
        GetMethodDomainObject.searchResult = [total:2, results:[objectWillBeReturned2, objectWillBeReturned1]];
        def params = [prop1:objectWillBeReturned1.prop1, rel1:new RelationMethodDomainObject2(id:1)]
        GetMethod get = new GetMethod(GetMethodDomainObject.metaClass, keys, relations);
        def result = get.invoke (GetMethodDomainObject, [params] as Object[]);
        assertEquals (objectWillBeReturned1.id, result.id);
        assertEquals ("prop1:\"${objectWillBeReturned1.prop1}\"", GetMethodDomainObject.query);

        objectWillBeReturned1.rel1 = null;
        GetMethodDomainObject.searchResult = [total:2, results:[objectWillBeReturned2, objectWillBeReturned1]];
        params = [prop1:objectWillBeReturned1.prop1, rel1:new RelationMethodDomainObject2(id:1)]
        result = get.invoke (GetMethodDomainObject, [params] as Object[]);
        assertNull (result);
        assertEquals ("prop1:\"${objectWillBeReturned1.prop1}\"", GetMethodDomainObject.query);
    }

    public void testGetMethodWithTwoRelationsAsKey()
    {
        def keys = ["rel2",  "rel1"]
        def relations = ["rel1":new Relation("rel1", "revRel1", GetMethodDomainObject, RelationMethodDomainObject2, Relation.ONE_TO_ONE),
        "rel2":new Relation("rel2", "revRel2", GetMethodDomainObject, RelationMethodDomainObject2, Relation.ONE_TO_ONE)]
        RelationMethodDomainObject2 relatedObject1 = new RelationMethodDomainObject2(id:1);
        RelationMethodDomainObject2 relatedObject2 = new RelationMethodDomainObject2(id:2);
        RelationMethodDomainObject2 relatedObject3 = new RelationMethodDomainObject2(id:5);
        RelationMethodDomainObject2 relatedObject4 = new RelationMethodDomainObject2(id:6);
        GetMethodDomainObject objectWillBeReturned1 = new GetMethodDomainObject(id:3, prop1:"prop1Value", prop2:"prop2Value", rel1:relatedObject1, rel2:relatedObject3)
        GetMethodDomainObject objectWillBeReturned2 = new GetMethodDomainObject(id:4, prop1:"prop1Value", prop2:"prop2Value", rel1:relatedObject2, rel2:relatedObject4)
        GetMethodDomainObject.searchResult = [total:2, results:[objectWillBeReturned2, objectWillBeReturned1]];
        def params = [rel2:new RelationMethodDomainObject2(id:5), rel1:new RelationMethodDomainObject2(id:1)]
        GetMethod get = new GetMethod(GetMethodDomainObject.metaClass, keys, relations);
        def result = get.invoke (GetMethodDomainObject, [params] as Object[]);
        assertEquals (objectWillBeReturned1.id, result.id);
        assertEquals ("id:[0 TO *]", GetMethodDomainObject.query);

        objectWillBeReturned1.rel1 = null;
        GetMethodDomainObject.searchResult = [total:2, results:[objectWillBeReturned2, objectWillBeReturned1]];
        params = [prop1:objectWillBeReturned1.prop1, rel1:new RelationMethodDomainObject2(id:1)]
        result = get.invoke (GetMethodDomainObject, [params] as Object[]);
        assertNull (result);
        assertEquals ("id:[0 TO *]", GetMethodDomainObject.query);
    }


    public void testGetMethodWithString()
    {
        def keys = ["prop1",  "prop2"]
        GetMethod get = new GetMethod(GetMethodDomainObject.metaClass, keys, [:]);
        String query = "string query";
        def result = get.invoke (GetMethodDomainObject, [query] as Object[]);
        assertNull (result.results[0]);
        assertEquals (query, GetMethodDomainObject.query);

        GetMethodDomainObject objectWillBeReturned = new GetMethodDomainObject(prop1:"prop1Value", prop2:"prop2Value")
        GetMethodDomainObject.searchResult = [total:1, results:[objectWillBeReturned]];
        result = get.invoke (GetMethodDomainObject, [query] as Object[]);
        assertEquals (objectWillBeReturned, result.results[0]);
        assertEquals (query, GetMethodDomainObject.query);
    }

}

class GetMethodDomainObject
{
    def static searchResult = [total:0, results:[]];
    def static query;
    String prop1;
    String prop2;
    String prop3;
    RelationMethodDomainObject2 rel1;
    RelationMethodDomainObject2 rel2;
    long id;
    def static search(Closure queryClosure)
    {
        GetMethodDomainObject.query = queryClosure;
        return searchResult;
    }
    def static search(String query)
    {
        GetMethodDomainObject.query = query;
        return searchResult;
    }
    public boolean equals(Object obj) {
        if(obj instanceof GetMethodDomainObject)
        {
            return obj.prop1 == prop1;
        }
        return false;
    }

}