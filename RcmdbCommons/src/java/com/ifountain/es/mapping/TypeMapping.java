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
    boolean isAllEnabled;


    public void addProperty(TypeProperty prop){
        
    }

    public List<TypeProperty> getTypeProperties(){
        return null;
    }

    public TypeProperty getTypeProperty(String propName){
        return null;
    }
}
