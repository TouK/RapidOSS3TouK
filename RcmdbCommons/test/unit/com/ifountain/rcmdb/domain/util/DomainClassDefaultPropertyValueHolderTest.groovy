package com.ifountain.rcmdb.domain.util

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.compass.CompassConstants

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 27, 2009
* Time: 11:58:06 AM
* To change this template use File | Settings | File Templates.
*/
class DomainClassDefaultPropertyValueHolderTest extends RapidCmdbTestCase
{

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        DomainClassDefaultPropertyValueHolder.destroy();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        DomainClassDefaultPropertyValueHolder.destroy();
    }

    public void testGetDefaultValue()
    {
        String class1Name = "Class1"
        String class2Name = "Class2"
        String class2Package = "com.trial"
        String prop1Value = "default";
        Long prop2Value = 5;
        Long prop3Value = 5;
        GroovyClassLoader gcl = new GroovyClassLoader();
        def class1 = gcl.parseClass ("""
        class ${class1Name}{
            String prop1 = "${prop1Value}";
            Long prop2 = ${prop2Value};
        }
        """)

        def class2 = gcl.parseClass ("""
        package ${class2Package};
        class ${class2Name}{
            String prop1 = "default";
            Long prop3 = 5;
        }
        """)
        def domainClasses = [class1, class2];
        DomainClassDefaultPropertyValueHolder.initialize(domainClasses);
        assertEquals (prop1Value, DomainClassDefaultPropertyValueHolder.getDefaultPropery(class1.name, "prop1"));
        assertEquals (prop1Value, DomainClassDefaultPropertyValueHolder.getDefaultPropery(class1.name, CompassConstants.UN_TOKENIZED_FIELD_PREFIX+"prop1"));
        assertEquals (prop2Value, DomainClassDefaultPropertyValueHolder.getDefaultPropery(class1.name, "prop2"));
        assertEquals (prop2Value, DomainClassDefaultPropertyValueHolder.getDefaultPropery(class1.name, CompassConstants.UN_TOKENIZED_FIELD_PREFIX+"prop2"));
        assertEquals (prop1Value, DomainClassDefaultPropertyValueHolder.getDefaultPropery(class2.name, "prop1"));
        assertEquals (prop1Value, DomainClassDefaultPropertyValueHolder.getDefaultPropery(class2.name, CompassConstants.UN_TOKENIZED_FIELD_PREFIX+"prop1"));
        assertEquals (prop3Value, DomainClassDefaultPropertyValueHolder.getDefaultPropery(class2.name, "prop3"));
        assertEquals (prop3Value, DomainClassDefaultPropertyValueHolder.getDefaultPropery(class2.name, CompassConstants.UN_TOKENIZED_FIELD_PREFIX+"prop3"));


        //test get value with simple name
        assertEquals (prop1Value, DomainClassDefaultPropertyValueHolder.getDefaultProperyWithSimpleName(class1.simpleName, "prop1"));
        assertEquals (prop1Value, DomainClassDefaultPropertyValueHolder.getDefaultProperyWithSimpleName(class1.simpleName, CompassConstants.UN_TOKENIZED_FIELD_PREFIX+"prop1"));
        assertEquals (prop2Value, DomainClassDefaultPropertyValueHolder.getDefaultProperyWithSimpleName(class1.simpleName, "prop2"));
        assertEquals (prop2Value, DomainClassDefaultPropertyValueHolder.getDefaultProperyWithSimpleName(class1.simpleName, CompassConstants.UN_TOKENIZED_FIELD_PREFIX+"prop2"));
        assertEquals (prop1Value, DomainClassDefaultPropertyValueHolder.getDefaultProperyWithSimpleName(class2.simpleName, "prop1"));
        assertEquals (prop1Value, DomainClassDefaultPropertyValueHolder.getDefaultProperyWithSimpleName(class2.simpleName, CompassConstants.UN_TOKENIZED_FIELD_PREFIX+"prop1"));
        assertEquals (prop3Value, DomainClassDefaultPropertyValueHolder.getDefaultProperyWithSimpleName(class2.simpleName, "prop3"));
        assertEquals (prop3Value, DomainClassDefaultPropertyValueHolder.getDefaultProperyWithSimpleName(class2.simpleName, CompassConstants.UN_TOKENIZED_FIELD_PREFIX+"prop3"));

        try
        {
            DomainClassDefaultPropertyValueHolder.getDefaultPropery(class2.name, "prop4")
            fail("Should throw exception since prop4 is not defined");
        }
        catch(groovy.lang.MissingPropertyException p)
        {
            assertEquals ("prop4", p.getProperty());
            assertEquals (class2.name, p.getType().name);
        }



        DomainClassDefaultPropertyValueHolder.destroy();
        try
        {
            DomainClassDefaultPropertyValueHolder.getDefaultPropery(class1.name, "prop1")
            fail("Should throw exception since instance destroyed and no class exist");
        }
        catch(Exception e)
        {
            assertEquals ("Domain class is not defined", e.getMessage());
        }

        try
        {
            DomainClassDefaultPropertyValueHolder.getDefaultProperyWithSimpleName(class1.simpleName, "prop1")
            fail("Should throw exception since instance destroyed and no class exist");
        }
        catch(Exception e)
        {
            assertEquals ("Domain class is not defined", e.getMessage());
        }
    }
}