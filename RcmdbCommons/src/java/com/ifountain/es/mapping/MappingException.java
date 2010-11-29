package com.ifountain.es.mapping;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 25, 2010
 * Time: 4:39:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class MappingException extends Exception {
    public MappingException() {
    }

    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingException(Throwable cause) {
        super(cause);
    }
    public static MappingException invalidPropertyTypeException(String type) {
        return new MappingException("Invalid property type <"+type+">");
    }
    public static MappingException invalidAnalyzerTypeException(String analyzerType) {
        return new MappingException("Invalid analyzer type <"+analyzerType+">");
    }
    public static MappingException invalidTypeNameException(String type) {
        return new MappingException("Invalid type name <"+type+">");
    }
    public static MappingException invalidIndexNameException(String index) {
        return new MappingException("Invalid index name <"+index+">");
    }
    public static MappingException invalidPropertyNameException(String propertyName) {
        return new MappingException("Invalid property name <"+propertyName+">");
    }

    public static MappingException invalidAnalyzerForKeyProp(String analyzerName) {
        return new MappingException("Key property cannot have <"+analyzerName+"> analyzer");
    }
    public static MappingException noProviderSpecified() {
        return new MappingException("No provider specified in mapping manager");
    }
}
