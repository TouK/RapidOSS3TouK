package com.ifountain.rcmdb.methods

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.methods.exception.UndefinedMethodException;
/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 16, 2009
* Time: 8:54:33 AM
* To change this template use File | Settings | File Templates.
*/
class MethodFactoryTest extends RapidCmdbTestCase{
    public void testFactory()
    {
        def method = MethodFactory.createMethod (MethodFactory.WITH_SESSION_METHOD);
        def paramTypes = method.getParameterTypes();
        assertEquals (2, paramTypes.size());
        assertEquals (String.name, paramTypes[0].name);
        assertEquals (Closure.name, paramTypes[1].name);
    }

    public void testFactoryThrowsExceptionIfMethodIsNotDefined()
    {
        try
        {
            MethodFactory.createMethod ("");
            fail("Should throw exception");
        }catch(UndefinedMethodException e)
        {

        }

    }
}