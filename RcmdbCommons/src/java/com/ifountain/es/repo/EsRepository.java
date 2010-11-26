package com.ifountain.es.repo;

import com.ifountain.elasticsearch.datasource.actions.XsonSource;
import com.ifountain.es.datasource.RossEsAdapter;
import com.ifountain.es.mapping.EsMappingListener;
import com.ifountain.es.mapping.EsMappingManager;
import com.ifountain.es.mapping.TypeMapping;
import com.ifountain.es.mapping.TypeProperty;
import org.apache.log4j.Logger;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;
import org.elasticsearch.search.SearchHits;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 25, 2010
 * Time: 11:10:24 AM
 */

/**
 * Type and default value support will be implemented here
 */
public class EsRepository implements EsMappingListener {
    public static final String CONNECTION_NAME = "localEs";
    public static final String INDEX_ALL = "indexAll";
    private static EsRepository instance;

    private RossEsAdapter adapter;
    private Logger logger;
    private Map<String, Map<String, Object>> defaultValues = new HashMap<String, Map<String, Object>>();

    public static EsRepository getInstance() {
        if (instance == null) {
            instance = new EsRepository();
        }
        return instance;
    }

    private EsRepository() {
        logger = Logger.getRootLogger();
        adapter = new RossEsAdapter(CONNECTION_NAME, logger);
        EsMappingManager.getInstance().addListener(this);
        constructDefaultValues();
    }

    public IndexResponse index(String type, Map<String, Object> props, Map<String, Object> indexOptions) throws Exception {
        TypeMapping typeMapping = EsMappingManager.getInstance().getMapping(type);
        String index = typeMapping.getIndex();
        Map<String, Object> properties = new HashMap<String, Object>();
        Map<String, Object> typeDefaultValues = defaultValues.get(type);
        properties.putAll(typeDefaultValues);
        if (indexOptions.containsKey(INDEX_ALL) && (Boolean) indexOptions.get(INDEX_ALL)) {
            properties.putAll(props);
        } else {
            for (Map.Entry<String, Object> p : props.entrySet()) {
                if (typeDefaultValues.containsKey(p.getKey())) {
                    properties.put(p.getKey(), p.getValue());
                }
            }
        }
        return getAdapter().index(index, type, new XsonSource(properties), true);
    }

    public void index(String type, Map<String, Object> props, Map<String, Object> indexOptions, ActionListener<IndexResponse> listener) {
    }

    public IndexResponse update(String type, Map<String, Object> props, Map<String, Object> indexOptions) {
        return null;
    }

    public void update(String type, Map<String, Object> props, Map<String, Object> indexOptions, ActionListener<IndexResponse> listener) {
    }

    public void delete(String type, Map<String, Object> keys, ActionListener<DeleteResponse> listener) {
    }

    public DeleteResponse delete(String type, Map<String, Object> keys) {
        return null;
    }

    public void deleteByQuery(String type, String query, ActionListener<DeleteByQueryResponse> listener) {
    }

    public DeleteByQueryResponse deleteByQuery(String type, String query) {
        return null;
    }

    public void deleteByQuery(String type, XContentQueryBuilder query, ActionListener<DeleteByQueryResponse> listener) {
    }

    public DeleteByQueryResponse deleteByQuery(String type, XContentQueryBuilder query) {
        return null;
    }

    public BulkResponse bulk(List<Map<String, Object>> actions, Map<String, Object> bulkOptions) {
        return null;
    }

    public void bulk(List<Map<String, Object>> actions, Map<String, Object> bulkOptions, ActionListener<BulkResponse> listener) {
    }

    public SearchHits search(List<String> types, String query, Map<String, Object> queryOptions) {
        return null;
    }

    public SearchHits searchEvery(List<String> types, String query, Map<String, Object> queryOptions) {
        return null;
    }

    public SearchHits search(List<String> types, XContentQueryBuilder queryBuilder, Map<String, Object> queryOptions) {
        return null;
    }

    public SearchHits searchEvery(List<String> types, XContentQueryBuilder queryBuilder, Map<String, Object> queryOptions) {
        return null;
    }

    public GetResponse get(String type, Map<String, Object> keys, Map<String, Object> options) {
        return null;
    }

    public GetResponse get(String type, Map<String, Object> keys) {
        return null;
    }

    public int count(List<String> types, XContentQueryBuilder queryBuilder) {
        return 0;
    }

    public int count(List<String> types, String query) {
        return 0;
    }

    public RossEsAdapter getAdapter() {
        return adapter;
    }

    public void mappingChanged() {
        constructDefaultValues();
    }

    private void constructDefaultValues() {
        defaultValues.clear();
        Map<String, TypeMapping> mappings = EsMappingManager.getInstance().getTypeMappings();
        for (String type : mappings.keySet()) {
            TypeMapping typeMapping = mappings.get(type);
            Map<String, Object> typeDefaultValues = new HashMap<String, Object>();
            Map<String, TypeProperty> properties = typeMapping.getTypeProperties();
            for (String pName : properties.keySet()) {
                typeDefaultValues.put(pName, properties.get(pName).getDefaultValue());
            }
            defaultValues.put(type, typeDefaultValues);
        }
    }
}
