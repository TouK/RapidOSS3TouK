package com.ifountain.es.repo;

import com.ifountain.elasticsearch.util.EsUtils;
import com.ifountain.elasticsearch.util.MappingConstants;
import com.ifountain.es.mapping.EsMappingManager;
import com.ifountain.es.mapping.TypeMapping;
import com.ifountain.es.mapping.TypeProperty;

import java.util.*;

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 25, 2010
 * Time: 4:59:12 PM
 */
public class EsIndexManager {

    public static void createAllIndices() throws Exception {
        Map<String, TypeMapping> typeMappings = EsMappingManager.getInstance().getTypeMappings();
        Map<String, Map<String, String>> indices = new HashMap<String, Map<String, String>>();
        for (String type : typeMappings.keySet()) {
            TypeMapping typeMapping = typeMappings.get(type);
            String index = typeMapping.getIndex();
            Map<String, String> mappings = indices.get(index);
            if (mappings == null) {
                mappings = new HashMap<String, String>();
                indices.put(index, mappings);
            }
            mappings.put(type, createMappingSource(typeMapping));
        }
        
        for (String index : indices.keySet()) {
            EsUtils.createIndex(EsRepository.getInstance().getAdapter(), index, EsUtils.getDefaultIndexConfiguration(), indices.get(index));
        }
    }

    private static String createMappingSource(TypeMapping typeMapping) {
        String type = typeMapping.getName();
        Map<String, Map<String, Object>> mapping = new HashMap<String, Map<String, Object>>();
        Map<String, TypeProperty> typeProperties = typeMapping.getTypeProperties();
        for (String pName : typeProperties.keySet()) {
            TypeProperty typeProperty = typeProperties.get(pName);
            Map<String, Object> propMapping = new HashMap<String, Object>();
            String propType = typeProperty.getType();
            if (propType.equals(TypeProperty.STRING_TYPE)) {
                String analyzer = typeProperty.getAnalyzer();
                propMapping.put("index", "analyzed");
                if (analyzer.equals(TypeProperty.KEYWORD_ANALYZER)) {
                    propMapping.put("analyzer", MappingConstants.ROSS_KEYWORD);
                } else {
                    propMapping.put("analyzer", MappingConstants.ROSS_WHITESPACE);
                }
            }
            propMapping.put("type", propType);
            propMapping.put("include_in_all", typeProperty.isIncludeInAll());
            Object defaultValue = typeProperty.getDefaultValue();
            if (defaultValue != null) {
                propMapping.put("null_value", defaultValue);
            }
            String store = typeProperty.isStore() ? "yes" : "no";
            propMapping.put("store", store);
            mapping.put(pName, propMapping);
        }
        return EsUtils.getMapping(type, mapping, typeMapping.isAllEnabled());
    }
}
