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
    public static final String INDEX = "index";
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
        IndexMethodPropertyProcessor processor = new IndexMethodPropertyProcessor(type, props, indexOptions);
        processor.process();
        String id = processor.getId();
        if (id != null) {
            return getAdapter().index(processor.getIndex(), type, new XsonSource(processor.getProperties()), id, false);
        } else {
            return getAdapter().index(processor.getIndex(), type, new XsonSource(processor.getProperties()), true);
        }
    }

    public void index(String type, Map<String, Object> props, Map<String, Object> indexOptions, ActionListener<IndexResponse> listener) throws Exception {
        IndexMethodPropertyProcessor processor = new IndexMethodPropertyProcessor(type, props, indexOptions);
        processor.process();
        String id = processor.getId();
        if (id != null) {
            getAdapter().index(processor.getIndex(), type, new XsonSource(processor.getProperties()), id, false, listener);
        } else {
            getAdapter().index(processor.getIndex(), type, new XsonSource(processor.getProperties()), true, listener);
        }
    }

    public IndexResponse update(String type, Map<String, Object> props, Map<String, Object> updateOptions) throws Exception {
        UpdateMethodPropertyProcessor processor = new UpdateMethodPropertyProcessor(type, props, updateOptions);
        GetResponse getResponse = adapter.get(processor.getIndex(), type, processor.getId());
        if (!getResponse.exists()) {
            throw new Exception("Entry with " + TypeProperty.ID + " <" + processor.getId() + "> does not exist for type <" + type + ">");
        }
        processor.setOldProperties(getResponse.sourceAsMap());
        processor.process();
        return adapter.index(processor.getIndex(), type, new XsonSource(processor.getProperties()), processor.getId());
    }

    public void update(final String type, final Map<String, Object> props, final Map<String, Object> updateOptions, final ActionListener<IndexResponse> listener) throws Exception {
        final UpdateMethodPropertyProcessor processor = new UpdateMethodPropertyProcessor(type, props, updateOptions);
        final String id = processor.getId();
        final String index = processor.getIndex();
        adapter.get(index, type, id, new ActionListener<GetResponse>() {
            public void onResponse(GetResponse getResponse) {
                if (getResponse.exists()) {
                    processor.setOldProperties(getResponse.sourceAsMap());
                    processor.process();
                    try {
                        adapter.index(index, type, new XsonSource(processor.getProperties()), id, listener);
                    } catch (Exception e) {
                        listener.onFailure(e);
                    }
                } else {
                    listener.onFailure(new Exception("Entry with " + TypeProperty.ID + " <" + id + "> does not exist for type <" + type + ">"));
                }
            }

            public void onFailure(Throwable throwable) {
                listener.onFailure(throwable);
            }
        });
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

    private abstract class AbstractPropertyProcessor {
        protected String type;
        protected String index;
        protected String id;
        protected Map<String, Object> propertiesToProcess;
        protected Map<String, Object> properties = new HashMap<String, Object>();
        protected Map<String, Object> options;
        protected boolean indexAll;
        protected TypeMapping typeMapping;

        protected AbstractPropertyProcessor(String type, Map<String, Object> propertiesToProcess, Map<String, Object> options) throws Exception {
            this.type = type;
            this.propertiesToProcess = propertiesToProcess;
            this.options = options;
            typeMapping = EsMappingManager.getInstance().getMapping(type);
            if (typeMapping == null) {
                throw new Exception("Type <" + type + "> does not exist.");
            }
            index = options.containsKey(INDEX) ? (String) options.get(INDEX) : typeMapping.getIndex();
            id = calculateId(typeMapping.getKeys());
            indexAll = options.containsKey(INDEX_ALL) && (Boolean) options.get(INDEX_ALL);
        }

        protected abstract void process();

        public String getIndex() {
            return index;
        }

        public String getId() {
            return id;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        protected String calculateId(Map<String, TypeProperty> keys) throws Exception {
            StringBuffer idBuf = new StringBuffer("");
            for (Map.Entry<String, TypeProperty> key : keys.entrySet()) {
                String pName = key.getKey();
                if (!propertiesToProcess.containsKey(pName)) {
                    throw new Exception("Key property <" + pName + "> for type <" + type + "> should be provided.");
                }
                idBuf.append(String.valueOf(propertiesToProcess.get(pName))).append("_");
            }
            String id = idBuf.toString();
            if (id.length() > 0) {
                return id.substring(0, id.length() - 1);
            }
            return null;
        }

        protected Object processPropValue(Object propValue) {
            if (propValue instanceof String && ((String) propValue).trim().equals("")) {
                propValue = TypeProperty.EMPTY_STRING;
            }
            return propValue;
        }

        protected long calculateCurrentTime() {
            long now = System.currentTimeMillis();
            long nanoTime = System.nanoTime();
            long digitsToAdd = (nanoTime % 100000000L) / 10000L;
            return now * 10000L + digitsToAdd;
        }
    }

    private class IndexMethodPropertyProcessor extends AbstractPropertyProcessor {
        protected IndexMethodPropertyProcessor(String type, Map<String, Object> propertiesToProcess, Map<String, Object> options) throws Exception {
            super(type, propertiesToProcess, options);
        }

        @Override
        protected void process() {
            Map<String, Object> typeDefaultValues = defaultValues.get(type);
            properties.putAll(typeDefaultValues);
            for (Map.Entry<String, Object> p : propertiesToProcess.entrySet()) {
                Object propValue = p.getValue();
                if (propValue != null && (indexAll || typeDefaultValues.containsKey(p.getKey()))) {
                    properties.put(p.getKey(), processPropValue(propValue));
                }
            }
            long now = calculateCurrentTime();
            properties.put(TypeProperty.RS_INSERTED_AT, now);
            properties.put(TypeProperty.RS_UPDATED_AT, now);
        }
    }

    private class UpdateMethodPropertyProcessor extends AbstractPropertyProcessor {
        private Map<String, Object> oldProperties;

        protected UpdateMethodPropertyProcessor(String type, Map<String, Object> propertiesToProcess, Map<String, Object> options) throws Exception {
            super(type, propertiesToProcess, options);
        }

        @Override
        protected void process() {
            Map<String, TypeProperty> keys = typeMapping.getKeys();
            Map<String, Object> typeDefaultValues = defaultValues.get(type);
            properties.putAll(typeDefaultValues);
            for (Map.Entry<String, Object> oldP : oldProperties.entrySet()) {
                properties.put(oldP.getKey(), processPropValue(oldP.getValue()));
            }
            for (Map.Entry<String, Object> p : propertiesToProcess.entrySet()) {
                Object propValue = p.getValue();
                String pName = p.getKey();
                if (propValue != null && !keys.containsKey(pName) && (indexAll || typeDefaultValues.containsKey(p.getKey()))) {
                    properties.put(pName, processPropValue(propValue));
                }
            }
            properties.put(TypeProperty.RS_UPDATED_AT, calculateCurrentTime());
        }

        @Override
        protected String calculateId(Map<String, TypeProperty> keys) throws Exception {
            String id = (String) propertiesToProcess.remove(TypeProperty.ID);
            if (keys.size() == 0 && id == null) {
                throw new Exception("Property <" + TypeProperty.ID + "> should be provided for update method for type <" + type + ">");
            } else if (keys.size() > 0 && id == null) {
                id = super.calculateId(keys);
            }
            return id;
        }

        public void setOldProperties(Map<String, Object> oldProperties) {
            this.oldProperties = oldProperties;
        }
    }

}
