package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

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
        GetMethod get = new GetMethod(GetMethodDomainObject.metaClass, keys);
        def result = get.invoke (GetMethodDomainObject, [[prop1:"prop1Value", prop3:"prop3Value"]] as Object[]);
        assertNull (result);
        assertNotNull (GetMethodDomainObject.query);

        GetMethodDomainObject objectWillBeReturned = new GetMethodDomainObject(prop1:"prop1Value", prop2:"prop2Value")
        GetMethodDomainObject.searchResult = [total:1, results:[objectWillBeReturned]];
        result = get.invoke (GetMethodDomainObject, [[prop1:"prop1Value", prop2:"prop2Value"]] as Object[]);
        assertEquals (objectWillBeReturned, result);
        assertNotNull (GetMethodDomainObject.query);
    }


    public void testGetMethodWithString()
    {
        def keys = ["prop1",  "prop2"]
        GetMethod get = new GetMethod(GetMethodDomainObject.metaClass, keys);
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