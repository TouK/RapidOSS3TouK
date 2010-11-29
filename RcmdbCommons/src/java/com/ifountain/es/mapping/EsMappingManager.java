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
    public static void destroy() {
        singletonInstance = null;
    }

    private Map<String, TypeMapping> typeMappings;

    private EsMappingManager() {
    }

    public TypeMapping getMapping(String type) {
        return typeMappings.get(type);
    }

    public void load() throws MappingException{
        Map<String, TypeMapping> mappings = getMappingProvider().constructMappings();
        setTypeMappings(mappings);
    }

    public void reload() throws MappingException{
        Map<String, TypeMapping> mappings = getMappingProvider().reload();
        setTypeMappings(mappings);
    }

    private void setTypeMappings(Map<String, TypeMapping> newMappings) throws MappingException {
        typeMappings = Collections.unmodifiableMap(newMappings);
        addDefaultProperties();
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

    public EsMappingProvider getMappingProvider()throws MappingException{
        if(this.mappingProvider == null){
            throw MappingException.noProviderSpecified();
        }
        return this.mappingProvider;    
    }

    private void addDefaultProperties() throws MappingException {

        for(Map.Entry<String, TypeMapping> entry: typeMappings.entrySet()){
            TypeMapping mapping = entry.getValue();
            TypeProperty[] props = createDefaultProperties();
            for(TypeProperty prop:props){
                mapping.addProperty(prop);                
            }
        }
    }

    private TypeProperty[] createDefaultProperties(){
        return new TypeProperty[]{new TypeProperty(TypeProperty.RS_INSERTED_AT, TypeProperty.LONG_TYPE),
        new TypeProperty(TypeProperty.RS_UPDATED_AT, TypeProperty.LONG_TYPE)
        };
    }
    

    public void addListener(EsMappingListener listener){
        this.listeners.add(listener);
    }

}
