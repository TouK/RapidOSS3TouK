package com.ifountain.es.mapping;

import com.ifountain.core.test.util.RapidCoreTestCase;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 25, 2010
 * Time: 4:32:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class TypeMappingTest extends RapidCoreTestCase{
    public void testAddProperty() throws MappingException {
        TypeProperty prop1 = new TypeProperty("prop1", TypeProperty.STRING_TYPE);
        TypeMapping mapping = new TypeMapping("mapping1", "index1");
        assertEquals(0, mapping.getTypeProperties().size());
        mapping.addProperty(prop1);
        assertSame(prop1, mapping.getTypeProperty("prop1"));

        TypeProperty prop2 = new TypeProperty("prop2", TypeProperty.STRING_TYPE);
        mapping.addProperty(prop2);
        assertSame(prop2, mapping.getTypeProperty("prop2"));

    }

    public void adddPropertyThrowsExceptionIfPropertyAlreadyExist() throws MappingException {
        TypeProperty prop1 = new TypeProperty("prop1", TypeProperty.STRING_TYPE);
        TypeMapping mapping = new TypeMapping("mapping1", "index1");
        mapping.addProperty(prop1);
        assertNotNull(mapping.getTypeProperty("prop1"));

        TypeProperty prop1Clone = new TypeProperty("prop1", TypeProperty.STRING_TYPE);
        try{
            mapping.addProperty(prop1);
            fail("Should throw exception");
        }catch(MappingException e){
            assertEquals("Duplicate property " + prop1Clone.getName() + " in  " + mapping.getName(), e.getMessage());
        }

    }
}
