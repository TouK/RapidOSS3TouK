package com.ifountain.es.mapping;

import com.ifountain.core.test.util.RapidCoreTestCase;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 25, 2010
 * Time: 4:32:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class TypeMappingAndTypePropertyTest extends RapidCoreTestCase{
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

    public void addPropertyThrowsExceptionIfPropertyAlreadyExist() throws MappingException {
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

    public void testIsNameValid(){
        assertTrue(TypeMapping.isNameValid("test1"));
        assertTrue(TypeMapping.isNameValid("typeTest1"));
        assertTrue(TypeMapping.isNameValid("TypeTest2"));
        assertFalse(TypeMapping.isNameValid("Type Test2"));
        assertFalse(TypeMapping.isNameValid("A Type Test2"));
    }

    public void testTypeMappingThrowsExceptionIfNameIsInvalid(){
        String invalidTypeName = "A type";
        TypeMapping mapping = new TypeMapping(invalidTypeName, "index1");
        try {
            mapping.validate();
            fail("Should throw exception if name is invalid");
        } catch (MappingException e) {
            MappingException expectedException = MappingException.invalidTypeNameException(invalidTypeName);
            assertEquals(expectedException.toString(), e.toString());
        }
    }

    public void testTypeMappingThrowsExceptionIfIndexNameIsInvalid(){
        String invalidIndexName = "An index";
        TypeMapping mapping = new TypeMapping("type1", invalidIndexName);
        try {
            mapping.validate();
            fail("Should throw exception if name is invalid");
        } catch (MappingException e) {
            MappingException expectedException = MappingException.invalidIndexNameException(invalidIndexName);
            assertEquals(expectedException.toString(), e.toString());
        }
    }

    public void testTypePropertyValidationThrowsExceptionIfPropertyNameIsInvalid(){
        String invalidPropName = "Invalid prop name";
        TypeProperty property = new TypeProperty(invalidPropName, TypeProperty.STRING_TYPE);

        try {
            property.validate();
            fail("Should throw exception since prop name is invalid");
        } catch (MappingException e) {
            MappingException expectedEx = MappingException.invalidPropertyNameException(invalidPropName);
            assertEquals(expectedEx.toString(), e.toString());
        }
    }

    public void testTypePropertyValidationThrowsExceptionIfPropertyTypeIsInvalid(){
        String invalidPropType = "Invalid";
        TypeProperty property = new TypeProperty("prop1", invalidPropType);
        assertEquals(invalidPropType.toLowerCase(), property.getType());
        try {
            property.validate();
            fail("Should throw exception since prop type is invalid");
        } catch (MappingException e) {
            MappingException expectedEx = MappingException.invalidPropertyTypeException(invalidPropType.toLowerCase());
            assertEquals(expectedEx.toString(), e.toString());
        }
    }

    public void testTypePropertyValidationThrowsExceptionIfPropertyAnalyzerTypeIsInvalid(){
        String invalidAnalyzerType = "InvalidAnalyzer";
        TypeProperty property = new TypeProperty("prop1", TypeProperty.STRING_TYPE);
        property.setAnalyzer(invalidAnalyzerType);
        try {
            property.validate();
            fail("Should throw exception since analyzer type is invalid");
        } catch (MappingException e) {
            MappingException expectedEx = MappingException.invalidAnalyzerTypeException(invalidAnalyzerType);
            assertEquals(expectedEx.toString(), e.toString());
        }
    }

    public void testTypePropertyValidationThrowsExceptionIfKeyPropertyHasInvalidAnalyzerType(){
        String invalidAnalyzerTypeForKey = TypeProperty.WHITSPACE_ANALYZER;
        TypeProperty property = new TypeProperty("prop1", TypeProperty.STRING_TYPE);
        property.setKey(true);
        property.setAnalyzer(invalidAnalyzerTypeForKey);
        try {
            property.validate();
            fail("Should throw exception since key property analyzer type is invalid");
        } catch (MappingException e) {
            MappingException expectedEx = MappingException.invalidAnalyzerForKeyProp(invalidAnalyzerTypeForKey);
            assertEquals(expectedEx.toString(), e.toString());
        }
    }

    public void testIsPropertyNameValid(){
        assertTrue(TypeProperty.isNameValid("test1"));
        assertTrue(TypeProperty.isNameValid("typeTest1"));
        assertTrue(TypeProperty.isNameValid("TypeTest2"));
        assertFalse(TypeProperty.isNameValid("Type Test2"));
        assertFalse(TypeProperty.isNameValid("A Type Test2"));
    }
}
