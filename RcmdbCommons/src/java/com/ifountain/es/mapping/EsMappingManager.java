package com.ifountain.es.mapping;

import com.ifountain.comp.config.ConfigurationProvider;
import com.ifountain.comp.config.ConfigurationProviderException;
import com.ifountain.comp.config.IConfigurationProvider;

import java.util.*;

public class EsMappingManager {
    IConfigurationProvider<TypeMapping> mappingProvider;
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

    private Map<String, TypeMapping> typeMappings = Collections.unmodifiableMap(new HashMap());

    private EsMappingManager() {
    }

    public TypeMapping getMapping(String type) {
        return typeMappings.get(type);
    }

    public void load() throws ConfigurationProviderException {
        Map<String, TypeMapping> mappings = getMappingProvider().load();
        setTypeMappings(mappings);
    }

    public void reload() throws ConfigurationProviderException{
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

    public void setMappingProvider(IConfigurationProvider<TypeMapping> mappingProvider) {
        this.mappingProvider = mappingProvider;
    }

    public Map<String, TypeMapping> getTypeMappings() {
        return typeMappings;
    }

    public IConfigurationProvider<TypeMapping> getMappingProvider()throws MappingException{
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

    public static TypeProperty[] createDefaultProperties(){
        return new TypeProperty[]{new TypeProperty(TypeProperty.RS_INSERTED_AT, TypeProperty.LONG_TYPE),
        new TypeProperty(TypeProperty.RS_UPDATED_AT, TypeProperty.LONG_TYPE)
        };
    }
    

    public void addListener(EsMappingListener listener){
        this.listeners.add(listener);
    }

}
