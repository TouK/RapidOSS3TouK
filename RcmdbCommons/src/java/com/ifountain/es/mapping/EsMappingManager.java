package com.ifountain.es.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
public class EsMappingManager {
    EsMappingProvider mappingProvider;
    private static EsMappingManager singletonInstance;
    public static EsMappingManager getInstance(){
        if(singletonInstance == null)
        {
            singletonInstance = new EsMappingManager();
        }
        return singletonInstance;
    }

    Map<String, TypeMapping> typeMappings = new HashMap<String, TypeMapping>();
    private EsMappingManager(){
    }

    public TypeMapping getMapping(String type){
        return typeMappings.get(type);
    }

    public void load(){
        typeMappings.clear();
        Map<String, TypeMapping> mappings = mappingProvider.constructMappings();
        typeMappings.putAll(mappings);
    }

    public void setMappingProvider(EsMappingProvider mappingProvider){
        this.mappingProvider = mappingProvider;
    }

    
}
