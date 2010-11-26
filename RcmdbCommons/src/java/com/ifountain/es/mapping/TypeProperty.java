package com.ifountain.es.mapping;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 25, 2010
 * Time: 10:51:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class TypeProperty {
    public static String STRING_TYPE = "string";
    public static String INTEGER_TYPE = "int";
    public static String BOOLEAN_TYPE = "boolean";
    public static String LONG_TYPE = "long";
    public static String DOUBLE_TYPE = "double";
    public static String FLOAT_TYPE = "float";
    public static String DATE_TYPE = "date";

    public static String[] VALID_PROPERTY_TYPES = {STRING_TYPE, INTEGER_TYPE,
            BOOLEAN_TYPE, LONG_TYPE, DOUBLE_TYPE, FLOAT_TYPE, DATE_TYPE};

    public static String KEYWORD_ANALYZER = "keyword";
    public static String WHITSPACE_ANALYZER = "whitespace";
    public static String[] VALID_ANALYZER_TYPES = {KEYWORD_ANALYZER, WHITSPACE_ANALYZER};
    String name;
    String type;
    Object defaultValue;
    String analyzer = KEYWORD_ANALYZER;
    boolean includeInAll = true;
    boolean isKey = false;
    boolean store = false;

    public TypeProperty(String name, String type) {
        this.name = name;
        this.type = type.toLowerCase();
    }

    public void validate() throws MappingException{
        if(!isNameValid(name)){
            throw MappingException.invalidPropertyNameException(name);
        }
        if(!isTypeValid(type)){
            throw MappingException.invalidPropertyTypeException(type);            
        }
        if(!isAnalyzerValid(analyzer)){
            throw MappingException.invalidAnalyzerTypeException(analyzer);
        }
        if(analyzer.equals(WHITSPACE_ANALYZER) && isKey == true){
            throw MappingException.invalidAnalyzerForKeyProp(analyzer);
        }
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public String getAnalyzer() {
        return analyzer;
    }

    public boolean isIncludeInAll() {
        return includeInAll;
    }

    public boolean isKey() {
        return isKey;
    }

    public boolean isStore() {
        return store;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }

    public void setIncludeInAll(boolean includeInAll) {
        this.includeInAll = includeInAll;
    }

    public void setKey(boolean key) {
        isKey = key;
    }

    public void setStore(boolean store) {
        this.store = store;
    }

    public static boolean isNameValid(String propName){
         if(propName.matches(".*\\s.*")){
            return false;
        }
        return true;
    }

    public static boolean isTypeValid(String type){
         for(String validType : VALID_PROPERTY_TYPES){
             if(validType.equals(type))
                 return true;
         }
        return false;
    }

    public static boolean isAnalyzerValid(String analyzerType){
         for(String validType : VALID_ANALYZER_TYPES){
             if(validType.equals(analyzerType))
                 return true;
         }
        return false;
    }
}
