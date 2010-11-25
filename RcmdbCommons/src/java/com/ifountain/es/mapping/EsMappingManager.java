package com.ifountain.es.mapping;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 25, 2010
 * Time: 10:49:54 AM
 * To change this template use File | Settings | File Templates.
 */
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

    private EsMappingManager(){
    }

    public TypeMapping getMapping(String type){
        return mappingProvider.getMapping(type);
    }

    public void setMappingProvider(EsMappingProvider mappingProvider){
        this.mappingProvider = mappingProvider;
    }

    
}
