package com.ifountain.es.repo;

import com.ifountain.es.datasource.RossEsAdapter;
import org.apache.log4j.Logger;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;
import org.elasticsearch.search.SearchHits;

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
public class EsRepository {
    public static final String CONNECTION_NAME = "localEs";
    private static EsRepository instance;

    private RossEsAdapter adapter;
    private Logger logger;

    public static EsRepository getInstance() {
        if (instance == null) {
            instance = new EsRepository();
        }
        return instance;
    }

    private EsRepository() {
        logger = Logger.getRootLogger();
        adapter = new RossEsAdapter(CONNECTION_NAME, logger);
    }

    public IndexResponse index(String type, Map<String, Object> props, Map<String, Object> indexOptions) {
        return null;
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

}
