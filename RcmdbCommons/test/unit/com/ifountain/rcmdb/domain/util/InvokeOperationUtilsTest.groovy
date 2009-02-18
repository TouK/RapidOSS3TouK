package com.ifountain.rcmdb.domain.util

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.operation.DomainOperationManager
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.util.DataStore
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 18, 2009
* Time: 10:58:34 AM
* To change this template use File | Settings | File Templates.
*/
class InvokeOperationUtilsTest extends RapidCmdbTestCase
{

    GroovyClassLoader gcl;
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        gcl = new GroovyClassLoader();
        DataStore.clear();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }
    
    public void testInvokeOperation()
    {
        def domainClass = gcl.parseClass ("""
            class Domain1{
                def ${RapidCMDBConstants.OPERATION_PROPERTY_NAME};
            }
        """);

         def domainOperationClass = gcl.parseClass ("""
            class Domain1Operations extends ${AbstractDomainOperation.class.name}{
                def method1(arg1, arg2)
                {
                    ${DataStore.class.name}.put("method1", [arg1, arg2]);
                    return 5;
                }

                def static method2(arg1, arg2)
                {
                    ${DataStore.class.name}.put("method2", [arg1, arg2]);
                    return 5;
                }
            }
        """);
        def methods = ["method1":domainOperationClass.metaClass.method1, "method2":domainOperationClass.metaClass.static.method2];
        def args = ["arg1", "arg2"];
        def res = InvokeOperationUtils.invokeMethod(domainClass.newInstance(), "method1", args as Object[], domainOperationClass, methods);
        assertEquals (args, DataStore.get("method1"));
        assertEquals (5, res);

        //test invoking static method
        def staticRes = InvokeOperationUtils.invokeStaticMethod(domainClass, "method2", args as Object[], domainOperationClass, methods);
        assertEquals (args, DataStore.get("method2"));
        assertEquals (5, staticRes);
    }

    public void testInvokeOperationThrowsExceptionIfOperationClassIsNotSpecified()
    {
        def domainClass = gcl.parseClass ("""
            class Domain1{
                def ${RapidCMDBConstants.OPERATION_PROPERTY_NAME};
            }
        """);
        def domainInstance = domainClass.newInstance();
        def args = ["arg1", "arg2"];
        try
        {
            InvokeOperationUtils.invokeMethod(domainInstance, "method1", args as Object[], null, [:]);
            fail("Should throw exception since operation class does not exist");
        }catch(groovy.lang.MissingMethodException ex)
        {
            assertEquals (domainClass, ex.getType());
            assertEquals ("method1", ex.getMethod());
        }
        assertNull (domainInstance[RapidCMDBConstants.OPERATION_PROPERTY_NAME]);
        
        //test calling static method
        try
        {
            InvokeOperationUtils.invokeStaticMethod(domainClass, "method1", args as Object[], null, [:]);
            fail("Should throw exception since operation class does not exist");
        }catch(groovy.lang.MissingMethodException ex)
        {
            assertEquals (domainClass, ex.getType());
            assertEquals ("method1", ex.getMethod());
        }

    }

    public void testInvokeOperationThrowsExceptionIfMethodDoesnotExist()
    {
        def domainClass = gcl.parseClass ("""
            class Domain1{
                def ${RapidCMDBConstants.OPERATION_PROPERTY_NAME};
            }
        """);
        def domainOperationClass = gcl.parseClass ("""
            class Domain1Operations extends ${AbstractDomainOperation.class.name}{
            }
        """);
        def domainInstance = domainClass.newInstance();
        def args = ["arg1", "arg2"];
        try
        {
            InvokeOperationUtils.invokeMethod(domainInstance, "method1", args as Object[], domainOperationClass, [:]);
            fail("Should throw exception since method does not exist");
        }catch(groovy.lang.MissingMethodException ex)
        {
            assertEquals (domainClass, ex.getType());
            assertEquals ("method1", ex.getMethod());
        }
        assertNull (domainInstance[RapidCMDBConstants.OPERATION_PROPERTY_NAME]);

        //test calling static method
        try
        {
            InvokeOperationUtils.invokeStaticMethod(domainClass, "method1", args as Object[], domainOperationClass, [:]);
            fail("Should throw exception since method does not exist");
        }catch(groovy.lang.MissingMethodException ex)
        {
            assertEquals (domainClass, ex.getType());
            assertEquals ("method1", ex.getMethod());
        }
    }

    public void testInvokeOperationThrowsMissingMethodExceptionComingFromAnotherMethod()
    {
        def domainClass = gcl.parseClass ("""
            class Domain1{
                def ${RapidCMDBConstants.OPERATION_PROPERTY_NAME};
            }
        """);
        def domainOperationClass = gcl.parseClass ("""
            class Domain1Operations extends ${AbstractDomainOperation.class.name}{
                def method1(){
                    method2();
                }
                def static method3(){
                    method4();
                }
            }
        """);
        def methods = ["method1":domainOperationClass.metaClass.method1, "method3":domainOperationClass.metaClass.static.method3];
        def domainInstance = domainClass.newInstance();
        try
        {
            InvokeOperationUtils.invokeMethod(domainInstance, "method1", [] as Object[], domainOperationClass, methods);
            fail("Should throw exception since method2 does not exist");
        }catch(groovy.lang.MissingMethodException ex)
        {
            assertEquals (domainOperationClass, ex.getType());
            assertEquals ("method2", ex.getMethod());
        }

        //Calling missing static method
        try
        {
            InvokeOperationUtils.invokeStaticMethod(domainClass, "method3", [] as Object[], domainOperationClass, methods);
            fail("Should throw exception since method4 does not exist");
        }catch(groovy.lang.MissingMethodException ex)
        {
            assertEquals (domainOperationClass, ex.getType());
            assertEquals ("method4", ex.getMethod());
        }
    }

    public void testInvokeOperationThrowsMissingMethodExceptionComingFromOtherClasses()
    {
        def anotherClass = gcl.parseClass ("""
            class AnotherClass{
            }
        """);
        def domainClass = gcl.parseClass ("""
            class Domain1{
                def ${RapidCMDBConstants.OPERATION_PROPERTY_NAME};
            }
        """);
        def domainOperationClass = gcl.parseClass ("""
            class Domain1Operations extends ${AbstractDomainOperation.class.name}{
                def method1(){
                    (new ${anotherClass.name}()).method1();
                }
                def static method2(){
                    (new ${anotherClass.name}()).method1();
                }
            }
        """);
        def methods = ["method1":domainOperationClass.metaClass.method1, "method2":domainOperationClass.metaClass.static.method2];
        def domainInstance = domainClass.newInstance();
        try
        {
            InvokeOperationUtils.invokeMethod(domainInstance, "method1", [] as Object[], domainOperationClass, methods);
            fail("Should throw exception since method1 of ${anotherClass.name}  does not exist");
        }catch(groovy.lang.MissingMethodException ex)
        {
            assertEquals (anotherClass, ex.getType());
            assertEquals ("method1", ex.getMethod());
        }

        //test invoke static
        try
        {
            InvokeOperationUtils.invokeStaticMethod(domainClass, "method2", [] as Object[], domainOperationClass, methods);
            fail("Should throw exception since method1 of ${anotherClass.name}  does not exist");
        }catch(groovy.lang.MissingMethodException ex)
        {
            assertEquals (anotherClass, ex.getType());
            assertEquals ("method1", ex.getMethod());
        }
    }
}