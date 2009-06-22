/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.annotations.HideProperty
import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.domain.property.FederatedPropertyManager

/**
* Created by IntelliJ IDEA.
* User: mustafa sener
* Date: Sep 12, 2008
* Time: 1:27:43 PM
* To change this template use File | Settings | File Templates.
*/
class GetPropertiesMethodTest extends RapidCmdbTestCase {

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        ExpandoMetaClass.enableGlobally()
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }


    public void testGetProperties()
    {
        FederatedPropertyManagerImpl manager = new FederatedPropertyManagerImpl();
        ConstrainedProperty.registerNewConstraint(KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
        GrailsDomainClass cls = new DefaultGrailsDomainClass(GetPropertiesMethodDomainObject);
        manager.federatedProps[cls.clazz] = ["prop4"]

        GetPropertiesMethod method = new GetPropertiesMethod(cls, manager);
        method.operationClass = GetPropertiesMethodDomainObjectOperations;

        def allProperties = method.getDomainObjectProperties();
        assertEquals(13, allProperties.size())


        RapidDomainClassProperty prop = allProperties[0] //propsMap["declaredProp1"];
        assertEquals("declaredProp1", prop.name);
        assertFalse(prop.isRelation);
        assertEquals(Object, prop.type);
        assertTrue(prop.isOperationProperty);

        prop = allProperties[1] //propsMap["declaredProp2"];
        assertEquals("declaredProp2", prop.name);
        assertFalse(prop.isRelation);
        assertFalse(prop.isKey);
        assertEquals(Object, prop.type);
        assertTrue(prop.isOperationProperty);

        prop = allProperties[2];
        assertEquals("id", prop.name);
        assertFalse(prop.isRelation);
        assertEquals(Long, prop.type);
        assertFalse(prop.isKey);
        assertFalse(prop.isOperationProperty);

        prop = allProperties[3] //propsMap["oprProp2"];
        assertEquals("oprProp2", prop.name);
        assertFalse(prop.isKey);
        assertFalse(prop.isRelation);
        assertEquals(Object, prop.type);
        assertTrue(prop.isOperationProperty);

        prop = allProperties[4] //propsMap["oprProp3"];
        assertEquals("oprProp3", prop.name);
        assertFalse(prop.isKey);
        assertFalse(prop.isRelation);
        assertEquals(Object, prop.type);
        assertTrue(prop.isOperationProperty);

        prop = allProperties[5] //propsMap["oprProp3"];
        assertEquals("oprProp4", prop.name);
        assertFalse(prop.isKey);
        assertFalse(prop.isRelation);
        assertEquals(Object, prop.type);
        assertTrue(prop.isOperationProperty);

        prop = allProperties[6] //propsMap["oprProp3"];
        assertEquals("oprProp5", prop.name);
        assertFalse(prop.isKey);
        assertFalse(prop.isRelation);
        assertEquals(Object, prop.type);
        assertTrue(prop.isOperationProperty);

        prop = allProperties[7] //propsMap["prop1"];
        assertEquals("prop1", prop.name);
        assertTrue(prop.isKey);
        assertFalse(prop.isRelation);
        assertEquals(String, prop.type);
        assertFalse(prop.isOperationProperty);
        assertFalse(prop.isFederated);

        prop = allProperties[8] //propsMap["prop1"];
        assertEquals("prop2", prop.name);
        assertTrue(prop.isKey);
        assertFalse(prop.isRelation);
        assertEquals(String, prop.type);
        assertFalse(prop.isOperationProperty);
        assertFalse(prop.isFederated);

        prop = allProperties[9] //propsMap["prop1"];
        assertEquals("prop3", prop.name);
        assertFalse(prop.isKey);
        assertFalse(prop.isRelation);
        assertEquals(String, prop.type);
        assertFalse(prop.isOperationProperty);
        assertFalse(prop.isFederated);

        prop = allProperties[10] //propsMap["prop1"];
        assertEquals("prop4", prop.name);
        assertFalse(prop.isKey);
        assertFalse(prop.isRelation);
        assertEquals(String, prop.type);
        assertFalse(prop.isOperationProperty);
        assertTrue(prop.isFederated);

        prop = allProperties[11] //propsMap["rel1"];
        assertTrue(prop instanceof RapidDomainClassRelation);
        assertEquals("rel1", prop.name);
        assertTrue(prop.isRelation);
        assertTrue(prop.isKey);
        assertFalse(prop.isOperationProperty);
        assertEquals(RelationMetaData.ONE_TO_ONE, prop.type);
        assertEquals(RelationMethodDomainObject2, prop.relatedModel);
        assertEquals("revRel1", prop.reverseName);

        prop = allProperties[12] //propsMap["rel1"];
        assertTrue(prop instanceof RapidDomainClassRelation);
        assertEquals("rel2", prop.name);
        assertTrue(prop.isRelation);
        assertFalse(prop.isKey);
        assertFalse(prop.isOperationProperty);
        assertEquals(RelationMetaData.ONE_TO_MANY, prop.type);
        assertEquals(RelationMethodDomainObject2, prop.relatedModel);
        assertEquals("revRel2", prop.reverseName);


        try
        {
            allProperties.remove(0)
            fail("Should throw exception beacuse this list cannot be modified");
        }
        catch (UnsupportedOperationException e)
        {
        }
        method.setOperationClass(null);
        allProperties = method.getDomainObjectProperties();
        assertEquals(7, allProperties.size())
    }


    public void testGetPropertyIgnoresPropertyWithNotPrimitivePropertiesAndInvalidTypesAccordingToModelGeneration()
    {
        FederatedPropertyManagerImpl manager = new FederatedPropertyManagerImpl();
        ConstrainedProperty.registerNewConstraint(KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
        GrailsDomainClass cls = new DefaultGrailsDomainClass(GetPropertiesMethodDomainObjectWithInvalidProperties);
        GetPropertiesMethod method = new GetPropertiesMethod(cls, manager);
        def allProperties = method.getDomainObjectProperties();
        assertEquals(9, allProperties.size())
        RapidDomainClassProperty prop = allProperties[0]
        assertEquals("id", prop.name);

        prop = allProperties[1]
        assertEquals("notValidPropertyAccordingToModelGeneation1", prop.name);

        prop = allProperties[2]
        assertEquals("notValidPropertyAccordingToModelGeneation2", prop.name);

        prop = allProperties[3]
        assertEquals("prop1", prop.name);

        prop = allProperties[4]
        assertEquals("prop2", prop.name);

        prop = allProperties[5]
        assertEquals("prop3", prop.name);

        prop = allProperties[6]
        assertEquals("prop4", prop.name);

        prop = allProperties[7]
        assertEquals("rel1", prop.name);

        prop = allProperties[8]
        assertEquals("rel2", prop.name);

    }

    public void testGetPropertiesIgnoresIfPropertyHaveGetterInOperations()
    {
        FederatedPropertyManagerImpl manager = new FederatedPropertyManagerImpl();
        ConstrainedProperty.registerNewConstraint(KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
        GrailsDomainClass cls = new DefaultGrailsDomainClass(GetPropertiesMethodIgnorePropertyDomainObject);
        GetPropertiesMethod method = new GetPropertiesMethod(cls, manager);
        method.operationClass = GetPropertiesMethodIgnorePropertyDomainObjectOperations;

        def allProperties = method.getDomainObjectProperties();
        allProperties.each {
            println it.name + " : " + it.type + " : " + it.isOperationProperty
        }

        assertEquals(6, allProperties.size());

        RapidDomainClassProperty prop = allProperties[0];
        assertEquals("id", prop.name);
        assertFalse(prop.isRelation);
        assertEquals(Long, prop.type);
        assertFalse(prop.isKey);
        assertFalse(prop.isOperationProperty);


        prop = allProperties[1];
        assertEquals("prop1", prop.name);
        assertFalse(prop.isRelation);
        assertFalse(prop.isKey);
        assertEquals(String, prop.type);
        assertFalse(prop.isOperationProperty);

        prop = allProperties[2];
        assertEquals("prop2", prop.name);
        assertFalse(prop.isRelation);
        assertFalse(prop.isKey);
        assertEquals(String, prop.type);
        assertFalse(prop.isOperationProperty);

        prop = allProperties[3];
        assertEquals("prop3", prop.name);
        assertFalse(prop.isRelation);
        assertFalse(prop.isKey);
        assertEquals(String, prop.type);
        assertFalse(prop.isOperationProperty);

        prop = allProperties[4] //propsMap["rel1"];
        assertTrue(prop instanceof RapidDomainClassRelation);
        assertEquals("rel1", prop.name);
        assertTrue(prop.isRelation);
        assertFalse(prop.isKey);
        assertFalse(prop.isOperationProperty);
        assertEquals(RelationMetaData.ONE_TO_ONE, prop.type);
        assertEquals(RelationMethodDomainObject2, prop.relatedModel);
        assertEquals("revRel1", prop.reverseName);

        prop = allProperties[5] //propsMap["rel1"];
        assertTrue(prop instanceof RapidDomainClassRelation);
        assertEquals("rel2", prop.name);
        assertTrue(prop.isRelation);
        assertFalse(prop.isKey);
        assertFalse(prop.isOperationProperty);
        assertEquals(RelationMetaData.ONE_TO_MANY, prop.type);
        assertEquals(RelationMethodDomainObject2, prop.relatedModel);
        assertEquals("revRel2", prop.reverseName);

    }
    public void testGetPropertiesIgnoresStaticPropertyInOperations()
    {
        FederatedPropertyManagerImpl manager = new FederatedPropertyManagerImpl();
        ConstrainedProperty.registerNewConstraint(KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
        GrailsDomainClass cls = new DefaultGrailsDomainClass(GetPropertiesMethodIgnoreStaticPropertyDomainObject);
        GetPropertiesMethod method = new GetPropertiesMethod(cls, manager);
        method.operationClass = GetPropertiesMethodIgnoreStaticPropertyDomainObjectOperations;

        def allProperties = method.getDomainObjectProperties();
        allProperties.each {
            println it.name + " : " + it.type + " : " + it.isOperationProperty
        }

        assertEquals(4, allProperties.size());

        RapidDomainClassProperty prop = allProperties[0];
        assertEquals("id", prop.name);
        assertFalse(prop.isRelation);
        assertEquals(Long, prop.type);
        assertFalse(prop.isKey);
        assertFalse(prop.isOperationProperty);


        prop = allProperties[1];
        assertEquals("prop1", prop.name);
        assertFalse(prop.isRelation);
        assertFalse(prop.isKey);
        assertEquals(String, prop.type);
        assertFalse(prop.isOperationProperty);

        prop = allProperties[2];
        assertEquals("prop2", prop.name);
        assertFalse(prop.isRelation);
        assertFalse(prop.isKey);
        assertEquals(String, prop.type);
        assertFalse(prop.isOperationProperty);

        prop = allProperties[3];
        assertEquals("prop3", prop.name);
        assertFalse(prop.isRelation);
        assertFalse(prop.isKey);
        assertEquals(String, prop.type);
        assertFalse(prop.isOperationProperty);

    }
}

class GetPropertiesMethodDomainObject
{
    static searchable = {
        except = ["rel1", "rel2", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static cascaded = ["rel2": true]
    static datasources = [:]
    Long id;
    Long version;
    String prop1;
    String prop2;
    String prop3;
    String prop4;
    RelationMethodDomainObject2 rel1;
    List rel2 = [];

    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __is_federated_properties_loaded__;
    static constraints = {
        __operation_class__(nullable: true)
        __is_federated_properties_loaded__(nullable: true)
        prop1(key: ["prop2", "rel1"]);
        errors(nullable: true)
        rel1(nullable: true)
        rel2(nullable: true)
    }
    static relations = [
            rel1: [type: RelationMethodDomainObject2, reverseName: "revRel1", isMany: false],
            rel2: [isMany: true, reverseName: "revRel2", type: RelationMethodDomainObject2],
    ]
    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    //AUTO_GENERATED_CODE
}


class GetPropertiesMethodDomainObjectWithInvalidProperties extends GetPropertiesMethodDomainObject
{
    Object invalidProp1;
    Observer invalidProp2;
    int notValidPropertyAccordingToModelGeneation1;
    boolean notValidPropertyAccordingToModelGeneation2;
}

class GetPropertiesMethodParentDomainObjectOperations extends AbstractDomainOperation
{
    private String privateProperty;
    def declaredProp1;
    def getOprProp2()
    {
    }
}
class GetPropertiesMethodDomainObjectOperations extends GetPropertiesMethodParentDomainObjectOperations
{
    private String privateProperty2;


    //    @HideProperty
    //    String privateProperty4;
    def declaredProp2;

    //test with only get
    def getOprProp3()
    {
    }
    //test with only set
    def setOprProp4(newValue)
    {
    }
    //test with both get and set
    def getOprProp5()
    {
    }
    def setOprProp5(newValue)
    {
    }


    def getOprProp6(thisIsNotAProp)
    {
    }
    //    @HideProperty
    //    private String getPrivateProperty3()
    //    {
    //        return "";
    //    }
}


class GetPropertiesMethodIgnorePropertyDomainObject
{
    static searchable = {
        except = ["rel1", "rel2"];
    };
    static cascaded = [:]
    static datasources = [:]
    Long id;
    Long version;
    String prop1;
    String prop2;
    String prop3;
    RelationMethodDomainObject2 rel1;
    List rel2 = [];

    static constraints = {
        rel1(nullable: true)
        rel2(nullable: true)
    }
    static relations = [
            rel1: [type: RelationMethodDomainObject2, reverseName: "revRel1", isMany: false],
            rel2: [isMany: true, reverseName: "revRel2", type: RelationMethodDomainObject2],
    ]
    static propertyConfiguration = [:]
    static transients = [];
    //AUTO_GENERATED_CODE
}

class GetPropertiesMethodIgnorePropertyDomainObjectOperations
{
    def getProp1()
    {
    }
    def setProp2(Object x)
    {
    }
    def getProp3()
    {
    }
    def setProp3(Object x)
    {
    }

    def getRel1() {

    }
    def getRel2() {

    }

}



class GetPropertiesMethodIgnoreStaticPropertyDomainObject
{
    static searchable = {
        except = ["rel1", "rel2"];
    };
    static cascaded = [:]
    static datasources = [:]
    Long id;
    Long version;
    String prop1;
    String prop2;
    String prop3;

    static constraints = {}
    static relations = [:]
    static propertyConfiguration = [:]
    static transients = [];
    //AUTO_GENERATED_CODE
}

class GetPropertiesMethodIgnoreStaticPropertyDomainObjectOperations
{
    static int static1;
    static String static2;
    static def getStatic3()
    {

    }
    static int getStatic4()
    {

    }
    static boolean getStatic5()
    {

    }
    static void setStatic5(boolean newValue)
    {

    }
    static void setStatic6(String newValue)
    {

    }
}

class FederatedPropertyManagerImpl implements FederatedPropertyManager
{
    def federatedProps = [:];
    def lazyProps = [:];
    public boolean isFederated(Class domainClass, String propName) {
        return federatedProps[domainClass] != null && federatedProps[domainClass].contains(propName); //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isLazy(Class domainClass, String propName) {
        return lazyProps[domainClass] != null && lazyProps[domainClass].contains(propName); //To change body of implemented methods use File | Settings | File Templates.
    }

}