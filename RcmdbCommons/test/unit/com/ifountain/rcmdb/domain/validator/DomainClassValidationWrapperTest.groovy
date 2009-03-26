package com.ifountain.rcmdb.domain.validator

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.util.DataStore

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 25, 2009
* Time: 11:55:13 PM
* To change this template use File | Settings | File Templates.
*/
class DomainClassValidationWrapperTest extends RapidCmdbTestCase{

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        DataStore.clear();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        DataStore.clear();
    }

    public void testInvokeMethod()
    {
        String className = "Class1";
        GroovyClassLoader gcl = new GroovyClassLoader();
        def valueShouldBeReturned = "returnedValue"
        Class domainClass = gcl.parseClass ("""
            class ${className}{
                String prop1;
                Long prop2;
                def method1(String param1, String param2)
                {
                    ${DataStore.class.name}.put("method1", param1+param2);
                    return "${valueShouldBeReturned}"
                }
                def static method2(String param1, String param2)
                {
                    ${DataStore.class.name}.put("method2", param1+param2);
                    return "${valueShouldBeReturned}"
                }
            }
        """)
        def domainObject = domainClass.newInstance();
        def updatedProps = [:];
        DomainClassValidationWrapper wrapper = new DomainClassValidationWrapper(domainObject, updatedProps);
        def param1Val = "param1"
        def param2Val = "param2"
        assertEquals (valueShouldBeReturned, wrapper.method1(param1Val, param2Val));
        assertEquals (valueShouldBeReturned, wrapper.method2(param1Val, param2Val));
        assertEquals (param1Val+param2Val, DataStore.get("method1"));
        assertEquals (param1Val+param2Val, DataStore.get("method2"));
    }
    public void testGetProperty()
    {
        String className = "Class1";
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class domainClass = gcl.parseClass ("""
            class ${className}{
                String prop1;
                Long prop2;
            }
        """)
        def domainObject = domainClass.newInstance();
        domainObject.prop1  = "prop1Value"
        domainObject.prop2 = 5;
        def updatedProps = [prop2:"updatedProp2Value"];
        DomainClassValidationWrapper wrapper = new DomainClassValidationWrapper(domainObject, updatedProps);
        assertEquals (domainObject.prop1, wrapper.prop1);
        assertEquals (updatedProps.prop2, wrapper.prop2);
        
    }

    public void testSetProperty()
    {
        String className = "Class1";
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class domainClass = gcl.parseClass ("""
            class ${className}{
                String prop1;
                Long prop2;
                public void setProperty(String propName, Object propValue, boolean flush)
                {
                    ${DataStore.class.name}.put("setPropWithoutUpdate", propValue);
                    
                }

                public void setProperty(String propName, Object propValue)
                {
                    ${DataStore.class.name}.put("setProp", propValue);

                }
            }
        """)
        def domainObject = domainClass.newInstance();
        domainObject.prop1  = "prop1Value"
        domainObject.prop2 = 5;
        DataStore.clear();
        def updatedProps = [:];
        DomainClassValidationWrapper wrapper = new DomainClassValidationWrapper(domainObject, updatedProps);
        try
        {
            wrapper.prop1 = "willThrowException";
        }
        catch(RapidValidationException e)
        {
            assertEquals (RapidValidationException.propertySetException().getMessage(), e.getMessage());
        }
        assertNull (DataStore.get("setProp"));
        assertNull (DataStore.get("setPropWithoutUpdate"));
        def errorObject = new Object();
        wrapper.errors = errorObject;

        assertSame (errorObject, DataStore.get("setPropWithoutUpdate"));
        assertSame (null, DataStore.get("setProp"));
    }
}