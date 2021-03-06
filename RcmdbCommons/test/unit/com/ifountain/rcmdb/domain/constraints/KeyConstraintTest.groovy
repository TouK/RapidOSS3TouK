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
package com.ifountain.rcmdb.domain.constraints

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import com.ifountain.rcmdb.domain.cache.IdCacheEntry

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 22, 2008
* Time: 5:13:38 PM
* To change this template use File | Settings | File Templates.
*/
public class KeyConstraintTest extends RapidCmdbTestCase{

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        KeyConstraintDomainObjectForTest.cacheEntry = new IdCacheEntry();
        KeyConstraintDomainObjectForTest.keyParams = null;
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



        KeyConstraintDomainObjectForTest obj1 = new KeyConstraintDomainObjectForTest(id:1, key1:"key1val", key2:"key2val", key3:"key3val");
        Errors errors = new BeanPropertyBindingResult(obj1, obj1.getClass().getName());
        constraint.validate (obj1, obj1.key1, errors);
        assertFalse (errors.hasErrors());
        
        KeyConstraintDomainObjectForTest.cacheEntry = new IdCacheEntry();
        KeyConstraintDomainObjectForTest.cacheEntry.setProperties (KeyConstraintDomainObjectForTest, obj1.id+1);
        constraint.validate (obj1, obj1.key1, errors);
        assertTrue (errors.hasErrors());
        assertEquals(ConstrainedProperty.DEFAULT_MESSAGES.get(KeyConstraint.DEFAULT_NOT_UNIQUE_MESSAGE_CODE), errors.getFieldError().getDefaultMessage())
        assertEquals(KeyConstraint.DEFAULT_NOT_UNIQUE_MESSAGE_CODE, errors.getFieldError().code)
        assertEquals (3, KeyConstraintDomainObjectForTest.keyParams.size());
        assertEquals ("key1val", KeyConstraintDomainObjectForTest.keyParams["key1"]);
        assertEquals ("key2val", KeyConstraintDomainObjectForTest.keyParams["key2"]);
        assertEquals ("key3val", KeyConstraintDomainObjectForTest.keyParams["key3"]);
    }

    public void testProcessValidateDoesnotReturnsErrorIfObjectExistsAndItHasAnIdFieldDifferentThanCurrentId()
    {
        KeyConstraint constraint = new KeyConstraint();
        constraint.setPropertyName ("key1");
        def compositeKeys = ["key2","key3"]
        constraint.setParameter (compositeKeys);
        constraint.setOwningClass (KeyConstraintDomainObjectForTest.class);

        KeyConstraintDomainObjectForTest obj1 = new KeyConstraintDomainObjectForTest(id:1, key1:"key1val", key2:"key2val", key3:"key3val");
        Errors errors = new BeanPropertyBindingResult(obj1, obj1.getClass().getName());
         
        KeyConstraintDomainObjectForTest.cacheEntry = new IdCacheEntry();
        KeyConstraintDomainObjectForTest.cacheEntry.setProperties (KeyConstraintDomainObjectForTest, obj1.id);
        constraint.validate (obj1, obj1.key1, errors);
        assertFalse (errors.hasErrors());


        obj1 = new KeyConstraintDomainObjectForTest(id:2, key1:"key1val", key2:"key2val", key3:"key3val");
        errors = new BeanPropertyBindingResult(obj1, obj1.getClass().getName());
        constraint.validate (obj1, obj1.key1, errors);
        assertTrue (errors.hasErrors());
    }

    public void testProcessValidateReturnsErrorIfOneOfKeysIsNull(){
        KeyConstraint constraint = new KeyConstraint();
        constraint.setPropertyName ("key1");
        def compositeKeys = ["key2","key3"]
        constraint.setParameter (compositeKeys);
        constraint.setOwningClass (KeyConstraintDomainObjectForTest.class);

        KeyConstraintDomainObjectForTest obj1 = new KeyConstraintDomainObjectForTest(id:1, key1:"key1val", key2:"key2val");
        Errors errors = new BeanPropertyBindingResult(obj1, obj1.getClass().getName());
        constraint.validate (obj1, obj1.key1, errors);
        assertTrue (errors.hasErrors());
        assertEquals(ConstrainedProperty.DEFAULT_MESSAGES.get(ConstrainedProperty.DEFAULT_NULL_MESSAGE_CODE), errors.getFieldError().getDefaultMessage())
        assertEquals(ConstrainedProperty.DEFAULT_NULL_MESSAGE_CODE, errors.getFieldError().code)
    }

    public void testProcessValidateReturnsErrorIfAStringKeyIsBlank(){
        KeyConstraint constraint = new KeyConstraint();
        constraint.setPropertyName ("key1");
        def compositeKeys = ["key2","key3"]
        constraint.setParameter (compositeKeys);
        constraint.setOwningClass (KeyConstraintDomainObjectForTest.class);

        KeyConstraintDomainObjectForTest obj1 = new KeyConstraintDomainObjectForTest(id:1, key1:"key1val", key2:"key2val", key3:" ");
        Errors errors = new BeanPropertyBindingResult(obj1, obj1.getClass().getName());
        constraint.validate (obj1, obj1.key1, errors);
        assertTrue (errors.hasErrors());
        assertEquals(ConstrainedProperty.DEFAULT_MESSAGES.get(ConstrainedProperty.DEFAULT_BLANK_MESSAGE_CODE), errors.getFieldError().getDefaultMessage())
        assertEquals(ConstrainedProperty.DEFAULT_BLANK_MESSAGE_CODE, errors.getFieldError().code)
    }
}

class KeyConstraintDomainObjectForTest
{
    static IdCacheEntry cacheEntry;
    static Map keyParams;
    public static searchable = {};
    Long id;
    String key1;
    String key2;
    String key3;
    public static IdCacheEntry getCacheEntry(Map params)
    {
        keyParams = params;
        return cacheEntry;
    }

}

class DomainObjectForTest
{
}