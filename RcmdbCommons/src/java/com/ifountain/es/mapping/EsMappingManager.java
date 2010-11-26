package com.ifountain.es.mapping;

import java.util.*;

public class EsMappingManager {
    EsMappingProvider mappingProvider;
    private static EsMappingManager singletonInstance;
    private List<EsMappingListener> listeners = new ArrayList<EsMappingListener>();

    public static EsMappingManager getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new EsMappingManager();
        }
        return singletonInstance;
    }

    Map<String, TypeMapping> typeMappings = new HashMap<String, TypeMapping>();

    private EsMappingManager() {
    }

    public TypeMapping getMapping(String type) {
        return typeMappings.get(type);
    }

    public void load() {
        typeMappings.clear();
        Map<String, TypeMapping> mappings = mappingProvider.constructMappings();
        typeMappings.putAll(mappings);
        for (EsMappingListener listener : listeners) {
            listener.mappingChanged();
        }
    }

    public void setMappingProvider(EsMappingProvider mappingProvider) {
        this.mappingProvider = mappingProvider;
    }

    public Map<String, TypeMapping> getTypeMappings() {
        return typeMappings;
    }

    public void addListener(EsMappingListener listener){
        this.listeners.add(listener);
    }

}
