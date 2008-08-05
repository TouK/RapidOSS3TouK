package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Aug 5, 2008
 * Time: 12:15:59 PM
 * To change this template use File | Settings | File Templates.
 */
class RemoveAllMethodTest extends RapidCmdbTestCase{
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        RemoveAllMethodDomainObject.unIndexList = [];
        RemoveAllMethodDomainObject.existingInstanceCount = 0;
        RemoveAllMethodDomainObject.countQuery = null;
        RemoveAllMethodDomainObject.relatedInstancesShouldBeReturnedFromRemoveRelationMethod = [:]
        RemoveAllMethodDomainObject.searchResults = [];
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testRemoveAll()
    {
        def obj1 = new RemoveAllMethodDomainObject(id:1);
        def obj2 = new RemoveAllMethodDomainObject(id:2);
        def obj3 = new RemoveAllMethodDomainObject(id:3);
        RemoveAllMethodDomainObject.searchResults +=  [total:0, results:[obj1]];
        RemoveAllMethodDomainObject.searchResults +=  [total:0, results:[obj2]];
        RemoveAllMethodDomainObject.searchResults +=  [total:0, results:[obj3]];
        RemoveAllMethod method = new RemoveAllMethod(RemoveAllMethodDomainObject.metaClass);
        method.invoke(RemoveAllMethodDomainObject, null);
        assertEquals (4, RemoveAllMethodDomainObject.queries.size());
        RemoveAllMethodDomainObject.queries.each{
            assertEquals ("id:[0 TO *]", it);    
        }
        assertSame (obj1, RemoveAllMethodDomainObject.unIndexList[0]);
        assertSame (obj2, RemoveAllMethodDomainObject.unIndexList[1]);
        assertSame (obj3, RemoveAllMethodDomainObject.unIndexList[2]);

    }
}

class RemoveAllMethodDomainObject extends RemoveMethodDomainObject
{
    def static searchResults = [];
    def static queries = [];
    def static searchWithoutTriggering(queryClosure)
    {
        RemoveAllMethodDomainObject.queries += queryClosure;
        if(searchResults.isEmpty())
        {
            return [total:0, results:[]];        
        }
        else
        {
            return searchResults.remove(0)
        }
    }

    def remove()
    {
        RemoveAllMethodDomainObject.unIndexList += this;
    }
}