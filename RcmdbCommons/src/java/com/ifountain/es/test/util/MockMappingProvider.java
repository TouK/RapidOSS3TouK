package com.ifountain.es.test.util;

import com.ifountain.es.mapping.EsMappingProvider;
import com.ifountain.es.mapping.TypeMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 26, 2010
 * Time: 9:40:01 AM
 */
public class MockMappingProvider implements EsMappingProvider {
    private Map<String, TypeMapping> mappings = new HashMap<String, TypeMapping>();

    public void setMappings(Map<String, TypeMapping> mappings) {
        this.mappings = mappings;
    }

    public Map<String, TypeMapping> constructMappings() {
        return mappings;
    }

    public Map<String, TypeMapping> reload() {
        return null;
    }
}
