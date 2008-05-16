package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 15, 2008
* Time: 2:27:48 PM
* To change this template use File | Settings | File Templates.
*/
class RemoveMethodTest extends RapidCmdbTestCase{

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testRemoveObject()
    {
        RemoveMethodDomainObject objectToBeRemoved = new RemoveMethodDomainObject();
        RemoveMethod removeMethod = new RemoveMethod(objectToBeRemoved.metaClass);
        removeMethod.invoke (objectToBeRemoved, null);
        assertSame(objectToBeRemoved, RemoveMethodDomainObject.unIndexList[0][0]);
    }
}

class RemoveMethodDomainObject
{
    def static unIndexList = [];

    def static unindex(objectList)
    {
        unIndexList.add(objectList);
    }
}

