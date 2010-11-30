package com.ifountain.es.test.util;

import com.ifountain.comp.config.IConfigurationProvider;
import com.ifountain.es.mapping.MappingException;
import com.ifountain.es.mapping.TypeMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 26, 2010
 * Time: 9:40:01 AM
 */
public class MockMappingProvider implements IConfigurationProvider {
    private Map<String, TypeMapping> mappings = new HashMap<String, TypeMapping>();
    private Map<String, TypeMapping> reloadMappings = new HashMap<String, TypeMapping>();
    private MappingException exceptionToBeThrown;
    public void setMappings(Map<String, TypeMapping> mappings) {
        this.mappings = mappings;
    }

    public void setReloadMappings(Map<String, TypeMapping> mappings) {
        this.reloadMappings = mappings;
    }

    public void setExceptionToBeThrown(MappingException exceptionToBeThrown) {
        this.exceptionToBeThrown = exceptionToBeThrown;
    }

    public Map<String, TypeMapping> load() throws MappingException{
        if(exceptionToBeThrown != null)
        throw exceptionToBeThrown;
        return mappings;
    }

    public Map<String, TypeMapping> reload() throws MappingException{
        if(exceptionToBeThrown != null)
        throw exceptionToBeThrown;
        return reloadMappings;
    }
}
