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
    public static String INTEGER_TYPE = "integer";
    public static String LONG_TYPE = "long";
    public static String DOUBLE_TYPE = "double";
    public static String FLOAT_TYPE = "float";
    public static String DATE_TYPE = "date";
    public static String BOOLEAN_TYPE = "boolean";

    public static String KEYWORD_ANALYZER = "keyword";
    public static String WHITSPACE_ANALYZER = "whitespace";
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
}
