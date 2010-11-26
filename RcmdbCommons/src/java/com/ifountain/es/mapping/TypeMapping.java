package com.ifountain.es.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 25, 2010
 * Time: 10:50:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class TypeMapping {
    String index;
    String name;
    Map<String, TypeProperty> typeProperties = new HashMap<String, TypeProperty>();
    boolean isAllEnabled = true;

    public TypeMapping(String name, String index) {
        this.name = name;
        this.index = index;
    }

    public void addProperty(TypeProperty prop) throws MappingException {
        if(typeProperties.containsKey(prop.getName()))
        {
            throw new MappingException("Duplicate property " + prop.getName() + " in  " + getName());
        }
        typeProperties.put(prop.getName(), prop);    
    }

    public Map<String, TypeProperty> getTypeProperties(){
        return typeProperties;
    }

    public TypeProperty getTypeProperty(String propName){
        return typeProperties.get(propName);
    }

    public String getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public boolean isAllEnabled() {
        return isAllEnabled;
    }

    public void setAllEnabled(boolean allEnabled) {
        isAllEnabled = allEnabled;
    }
}
