package com.ifountain.rcmdb.domain.constraints

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 22, 2008
* Time: 5:13:38 PM
* To change this template use File | Settings | File Templates.
*/
class KeyConstraintTest extends RapidCmdbTestCase{

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        KeyConstraintDomainObjectForTest.existingInstance = null;
        KeyConstraintDomainObjectForTest.searchParams = null;
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testGetName()
    {
        KeyConstraint constraint = new KeyConstraint();
        assertEquals (KeyConstraint.KEY_CONSTRAINT, constraint.getName());
    }

    public void testSupport()
    {
        KeyConstraint constraint = new KeyConstraint();
        assertTrue(constraint.supports (String));
    }

    public void testSetParameter()
    {
        KeyConstraint constraint = new KeyConstraint();
        constraint.setPropertyName ("key1")
        def compositeKeys = ["key2","key3"]
        constraint.setParameter (compositeKeys);
        def keys = constraint.getKeys();
        assertEquals (3, keys.size());
        assertTrue (keys.containsAll(compositeKeys));
        assertTrue (keys.contains(constraint.getPropertyName()));
    }

    public void testSetParameterThrowsExceptionIfKeysParameterIsNotList()
    {
        KeyConstraint constraint = new KeyConstraint();
        constraint.setPropertyName ("key1");
        try
        {
            constraint.setParameter ("INVALID PARAM VALUE");
            fail("Should throw exception");
        }
        catch(java.lang.IllegalArgumentException e)
        {

        }
    }
    public void testProcessValidateReturnsErrorIfObjectExists()
    {
        KeyConstraint constraint = new KeyConstraint();
        constraint.setPropertyName ("key1");
        def compositeKeys = ["key2","key3"]
        constraint.setParameter (compositeKeys);
        constraint.setOwningClass (KeyConstraintDomainObjectForTest.class);



        KeyConstraintDomainObjectForTest obj1 = new KeyConstraintDomainObjectForTest(key1:"key1val", key2:"key2val", key3:"key3val");
        Errors errors = new BeanPropertyBindingResult(obj1, obj1.getClass().getName());
        constraint.validate (obj1, obj1.key1, errors);
        assertFalse (errors.hasErrors());
        
        KeyConstraintDomainObjectForTest.existingInstance = new KeyConstraintDomainObjectForTest();
        constraint.validate (obj1, obj1.key1, errors);
        assertTrue (errors.hasErrors());
        assertEquals(ConstrainedProperty.DEFAULT_MESSAGES.get(KeyConstraint.DEFAULT_NOT_UNIQUE_MESSAGE_CODE), errors.getFieldError().getDefaultMessage())
        assertEquals (3, KeyConstraintDomainObjectForTest.searchParams.size());
        assertEquals ("key1val", KeyConstraintDomainObjectForTest.searchParams["key1"]);
        assertEquals ("key2val", KeyConstraintDomainObjectForTest.searchParams["key2"]);
        assertEquals ("key3val", KeyConstraintDomainObjectForTest.searchParams["key3"]);
    }

    public void testProcessValidateDoesnotReturnsErrorIfObjectExistsAndItHasAnIdField()
    {
        KeyConstraint constraint = new KeyConstraint();
        constraint.setPropertyName ("key1");
        def compositeKeys = ["key2","key3"]
        constraint.setParameter (compositeKeys);
        constraint.setOwningClass (KeyConstraintDomainObjectForTest.class);

        KeyConstraintDomainObjectForTest obj1 = new KeyConstraintDomainObjectForTest(id:1, key1:"key1val", key2:"key2val", key3:"key3val");
        Errors errors = new BeanPropertyBindingResult(obj1, obj1.getClass().getName());
         
        KeyConstraintDomainObjectForTest.existingInstance = new KeyConstraintDomainObjectForTest();
        constraint.validate (obj1, obj1.key1, errors);
        assertFalse (errors.hasErrors());

    }
}

class KeyConstraintDomainObjectForTest
{
    static Object existingInstance;
    static Map searchParams;
    public static searchable = {};
    Long id;
    String key1;
    String key2;
    String key3;
    public static Object get(Map params)
    {
        searchParams = params;
        return existingInstance;
    }

}

class DomainObjectForTest
{
}