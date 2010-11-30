package com.ifountain.es.repo;

import com.ifountain.elasticsearch.datasource.actions.BulkDeleteItem;
import com.ifountain.elasticsearch.datasource.actions.BulkIndexItem;
import com.ifountain.elasticsearch.datasource.actions.BulkItem;
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

import java.util.*;

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
    public static final String BULK_INDEX_ACTION = "index";
    public static final String BULK_DELETE_ACTION = "delete";
    private static EsRepository instance;

    private RossEsAdapter adapter;
    private Map<String, Map<String, Object>> defaultValues = new HashMap<String, Map<String, Object>>();

    public static EsRepository getInstance() {
        if (instance == null) {
            instance = new EsRepository();
        }
        return instance;
    }

    private EsRepository() {
        Logger logger = Logger.getRootLogger();
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

    public BulkResponse bulk(List<Map<String, Object>> actions) throws Exception {
        return adapter.bulk(createBulkItems(actions));
    }

    public void bulk(List<Map<String, Object>> actions, ActionListener<BulkResponse> listener) throws Exception {
        adapter.bulk(createBulkItems(actions), listener);
    }

    private List<BulkItem> createBulkItems(List<Map<String, Object>> bulkActions) throws Exception {
        List<BulkItem> bulkItems = new ArrayList<BulkItem>();
        for (Map<String, Object> action : bulkActions) {
            String actionType = (String) getRequiredBulkActionProperty("action", action);
            String type = (String) getRequiredBulkActionProperty("type", action);
            Map<String, Object> properties = (Map<String, Object>) getRequiredBulkActionProperty("properties", action);
            if (actionType.equals(BULK_INDEX_ACTION)) {
                IndexMethodPropertyProcessor processor = new IndexMethodPropertyProcessor(type, properties, action);
                processor.process();
                BulkIndexItem bulkItem = new BulkIndexItem(processor.getIndex(), type, new XsonSource(processor.getProperties()));
                String id = processor.getId();
                if (id != null) {
                    bulkItem.setId(id);
                    bulkItem.setIsCreate(false);
                } else {
                    bulkItem.setIsCreate(true);
                }
                bulkItems.add(bulkItem);
            } else if (actionType.equals(BULK_DELETE_ACTION)) {
                MethodProcessor processor = new MethodProcessor(type, properties, action);
                BulkDeleteItem bulkItem = new BulkDeleteItem(processor.getIndex(), type, processor.getId());
                bulkItems.add(bulkItem);
            } else {
                throw new Exception("Invalid bulk action type <" + actionType + ">");
            }
        }
        return bulkItems;
    }

    private Object getRequiredBulkActionProperty(String propertyName, Map<String, Object> action) throws Exception {
        Object pValue = action.remove(propertyName);
        if (pValue == null) {
            throw new Exception("Bulk action property <" + propertyName + "> does not exist");
        }
        return pValue;
    }

    public void delete(String type, Map<String, Object> keys, Map<String, Object> options, ActionListener<DeleteResponse> listener) throws Exception {
        MethodProcessor processor = new MethodProcessor(type, keys, options);
        adapter.delete(processor.getIndex(), type, processor.getId(), listener);

    }

    public DeleteResponse delete(String type, Map<String, Object> keys, Map<String, Object> options) throws Exception {
        MethodProcessor processor = new MethodProcessor(type, keys, options);
        return adapter.delete(processor.getIndex(), type, processor.getId());
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

    public GetResponse get(String type, Map<String, Object> keys, Map<String, Object> options) throws Exception {
        MethodProcessor processor = new MethodProcessor(type, keys, options);
        return adapter.get(processor.getIndex(), type, processor.getId());
    }

    public GetResponse get(String type, Map<String, Object> keys) throws Exception {
        return get(type, keys, new HashMap<String, Object>());
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

    private TypeMapping checkType(String type) throws Exception {
        TypeMapping typeMapping = EsMappingManager.getInstance().getMapping(type);
        if (typeMapping == null) {
            throw new Exception("Type <" + type + "> does not exist.");
        }
        return typeMapping;
    }

    private class MethodProcessor {
        protected String type;
        protected String id;
        protected TypeMapping typeMapping;
        protected Map<String, Object> propertiesToProcess;
        protected String index;
        protected Map<String, Object> options;

        protected MethodProcessor(String type, Map<String, Object> propertiesToProcess, Map<String, Object> options) throws Exception {
            this.type = type;
            this.propertiesToProcess = propertiesToProcess;
            this.options = options;
            typeMapping = checkType(type);
            id = calculateId(typeMapping.getKeys());
            index = options.containsKey(INDEX) ? (String) options.get(INDEX) : typeMapping.getIndex();
        }

        protected String calculateId(Map<String, TypeProperty> keys) throws Exception {
            if (isIdRequired()) {
                String id = (String) propertiesToProcess.remove(TypeProperty.ID);
                if (keys.size() == 0 && id == null) {
                    throw new Exception("Property <" + TypeProperty.ID + "> should be provided for type <" + type + ">");
                } else if (keys.size() > 0 && id == null) {
                    return _calculateId(keys);
                }
                return id;
            } else {
                return _calculateId(keys);
            }
        }

        protected boolean isIdRequired() {
            return true;
        }

        protected String _calculateId(Map<String, TypeProperty> keys) throws Exception {
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

        public String getIndex() {
            return index;
        }

        public String getId() {
            return id;
        }
    }

    private abstract class AbstractMethodPropertyProcessor extends MethodProcessor {
        protected Map<String, Object> properties = new HashMap<String, Object>();
        protected boolean indexAll;


        protected AbstractMethodPropertyProcessor(String type, Map<String, Object> propertiesToProcess, Map<String, Object> options) throws Exception {
            super(type, propertiesToProcess, options);
            indexAll = options.containsKey(INDEX_ALL) && (Boolean) options.get(INDEX_ALL);
        }

        protected abstract void process();

        public Map<String, Object> getProperties() {
            return properties;
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

    private class IndexMethodPropertyProcessor extends AbstractMethodPropertyProcessor {
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

        @Override
        protected boolean isIdRequired() {
            return false;
        }
    }

    private class UpdateMethodPropertyProcessor extends AbstractMethodPropertyProcessor {
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

        public void setOldProperties(Map<String, Object> oldProperties) {
            this.oldProperties = oldProperties;
        }
    }

}
