package com.ifountain.es.mapping;

import com.ifountain.comp.config.ConfigurationBean;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 25, 2010
 * Time: 10:51:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class TypeProperty  extends ConfigurationBean {
    public static String STRING_TYPE = "string";
    public static String INTEGER_TYPE = "integer";
    public static String BOOLEAN_TYPE = "boolean";
    public static String LONG_TYPE = "long";
    public static String DOUBLE_TYPE = "double";
    public static String FLOAT_TYPE = "float";
    public static String DATE_TYPE = "date";

    public static String EMPTY_STRING = "_e";

    public static String RS_INSERTED_AT = "rsInsertedAt";
    public static String RS_UPDATED_AT = "rsUpdatedAt";
    public static String ID = "id";

    public static String[] VALID_PROPERTY_TYPES = {STRING_TYPE, INTEGER_TYPE,
            BOOLEAN_TYPE, LONG_TYPE, DOUBLE_TYPE, FLOAT_TYPE, DATE_TYPE};

    public static String KEYWORD_ANALYZER = "keyword";
    public static String WHITSPACE_ANALYZER = "whitespace";
    public static String[] VALID_ANALYZER_TYPES = {KEYWORD_ANALYZER, WHITSPACE_ANALYZER};
    private String name;
    private String type;
    private Object defaultValue;
    private String analyzer = KEYWORD_ANALYZER;
    private boolean includeInAll = true;
    private boolean isKey = false;
    private boolean store = false;

    public TypeProperty(String name, String type) {
        this.name = name;
        this.type = type.toLowerCase();
        defaultValue = getDefaultValue(type);
    }

    public void validate() throws MappingException {
        if (!isNameValid(name)) {
            throw MappingException.invalidPropertyNameException(name);
        }
        if (!isTypeValid(type)) {
            throw MappingException.invalidPropertyTypeException(type);
        }
        if (!isAnalyzerValid(analyzer)) {
            throw MappingException.invalidAnalyzerTypeException(analyzer);
        }
        if (analyzer.equals(WHITSPACE_ANALYZER) && isKey == true) {
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

    public static boolean isNameValid(String propName) {
        if (propName.matches(".*\\s.*")) {
            return false;
        }
        return true;
    }

    private static Object getDefaultValue(String type) {
        if (type.equals(TypeProperty.STRING_TYPE)) {
            return EMPTY_STRING;
        } else if (type.equals(TypeProperty.INTEGER_TYPE) || type.equals(TypeProperty.LONG_TYPE)) {
            return 0;
        }
        else if(type.equals(TypeProperty.DOUBLE_TYPE)){
            return 0.0d;
        }
        else if(type.equals(TypeProperty.FLOAT_TYPE)){
            return 0.0f;
        }
        else if (type.equals(TypeProperty.DATE_TYPE)) {
            return new Date(0);
        } else if (type.equals(TypeProperty.BOOLEAN_TYPE)) {
            return false;
        }
        return null;
    }

    public static boolean isTypeValid(String type) {
        for (String validType : VALID_PROPERTY_TYPES) {
            if (validType.equals(type))
                return true;
        }
        return false;
    }

    public static boolean isAnalyzerValid(String analyzerType) {
        for (String validType : VALID_ANALYZER_TYPES) {
            if (validType.equals(analyzerType))
                return true;
        }
        return false;
    }
}
