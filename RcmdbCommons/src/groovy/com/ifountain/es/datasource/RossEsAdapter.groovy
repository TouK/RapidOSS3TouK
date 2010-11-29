package com.ifountain.es.datasource;


import com.ifountain.elasticsearch.datasource.ElasticSearchAdapter
import com.ifountain.elasticsearch.datasource.actions.XsonSource
import org.apache.log4j.Logger
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.count.CountResponse
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.index.query.xcontent.FilterBuilders
import org.elasticsearch.index.query.xcontent.QueryBuilders
import org.elasticsearch.index.query.xcontent.QueryFilterBuilder
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder

/**
 * Created by Sezgin Kucukkaraaslan.
 * Date: Nov 10, 2010
 * Time: 10:13:23 AM
 */
public class RossEsAdapter extends ElasticSearchAdapter {

    public RossEsAdapter(String connConfigName, Logger logger) {
        super(connConfigName, logger);
    }

    @Override
    public SearchResponse searchWithXson(String[] indices, String[] types, XsonSource source, long timeout) throws Exception {
        throw new Exception("Searching with xson is not supported");
    }

    @Override
    public SearchResponse searchWithXson(String[] indices, String[] types, XsonSource source) throws Exception {
        throw new Exception("Searching with xson is not supported");
    }

    @Override
    public void searchWithXson(String[] indices, String[] types, XsonSource source, ActionListener<SearchResponse> listener) throws Exception {
        throw new Exception("Searching with xson is not supported");
    }

    @Override
    public SearchResponse search(String[] i, String[] t, String query, Map searchOptions) throws Exception {
        XContentQueryBuilder builder = constructBuilderWithQuery(query, searchOptions);
        builder = getFilteredQuery(i, t, builder);
        return super.searchWithXson(i, t, constructSourceWithBuilder(builder, searchOptions), calculateTimeout(searchOptions));
    }

    @Override
    public void search(String[] i, String[] t, String query, Map searchOptions, ActionListener<SearchResponse> listener) throws Exception {
        XContentQueryBuilder builder = constructBuilderWithQuery(query, searchOptions);
        builder = getFilteredQuery(i, t, builder);
        super.searchWithXson(i, t, constructSourceWithBuilder(builder, searchOptions), listener);
    }

    @Override
    public void search(String[] i, String[] t, XContentQueryBuilder builder, Map searchOptions, ActionListener<SearchResponse> listener) throws Exception {
        builder = getFilteredQuery(i, t, builder);
        super.searchWithXson(i, t, constructSourceWithBuilder(builder, searchOptions), listener);
    }

    @Override
    public SearchResponse search(String[] i, String[] t, XContentQueryBuilder builder, Map searchOptions) throws Exception {
        builder = getFilteredQuery(i, t, builder);
        return super.searchWithXson(i, t, constructSourceWithBuilder(builder, searchOptions), calculateTimeout(searchOptions));
    }

    @Override
    public CountResponse countWithXson(String[] indices, String[] types, XsonSource query, long timeout) throws Exception {
        throw new Exception("Count with xson is not supported");
    }

    @Override
    public CountResponse countWithXson(String[] indices, String[] types, XsonSource query) throws Exception {
        throw new Exception("Count with xson is not supported");
    }

    @Override
    public void countWithXson(String[] indices, String[] types, XsonSource query, ActionListener<CountResponse> listener) throws Exception {
        throw new Exception("Count with xson is not supported");
    }

    @Override
    public CountResponse count(String[] indices, String[] types, String query, long timeout) throws Exception {
        XContentQueryBuilder builder = QueryBuilders.queryString(query);
        builder = getFilteredQuery(indices, types, builder);
        return super.countWithXson(indices, types, new XsonSource(builder), timeout);
    }

    @Override
    public CountResponse count(String[] indices, String[] types, String query) throws Exception {
        XContentQueryBuilder builder = QueryBuilders.queryString(query);
        builder = getFilteredQuery(indices, types, builder);
        return super.countWithXson(indices, types, new XsonSource(builder), -1);
    }

    @Override
    public void count(String[] indices, String[] types, String query, ActionListener<CountResponse> listener) throws Exception {
        XContentQueryBuilder builder = QueryBuilders.queryString(query);
        builder = getFilteredQuery(indices, types, builder);
        super.countWithXson(indices, types, new XsonSource(builder), listener);
    }

    @Override
    public CountResponse count(String[] indices, String[] types, XContentQueryBuilder builder, long timeout) throws Exception {
        builder = getFilteredQuery(indices, types, builder);
        return super.countWithXson(indices, types, new XsonSource(builder), timeout);
    }

    @Override
    public CountResponse count(String[] indices, String[] types, XContentQueryBuilder builder) throws Exception {
        builder = getFilteredQuery(indices, types, builder);
        return super.countWithXson(indices, types, new XsonSource(builder), -1);
    }

    @Override
    public void count(String[] indices, String[] types, XContentQueryBuilder builder, ActionListener<CountResponse> listener) throws Exception {
        builder = getFilteredQuery(indices, types, builder);
        super.countWithXson(indices, types, new XsonSource(builder), listener);
    }

    @Override
    public DeleteByQueryResponse deleteByXsonQuery(String[] indices, String[] types, XsonSource query, long timeout) throws Exception {
        throw new Exception("Delete by xson query is not supported");
    }

    @Override
    public DeleteByQueryResponse deleteByXsonQuery(String[] indices, String[] types, XsonSource query) throws Exception {
        throw new Exception("Delete by xson query is not supported");
    }

    @Override
    public void deleteByXsonQuery(String[] indices, String[] types, XsonSource query, ActionListener<DeleteByQueryResponse> listener) throws Exception {
        throw new Exception("Delete by xson query is not supported");
    }

    @Override
    public void deleteByQuery(String[] indices, String[] types, String query, ActionListener<DeleteByQueryResponse> listener) throws Exception {
        XContentQueryBuilder builder = QueryBuilders.queryString(query);
        builder = getFilteredQuery(indices, types, builder);
        super.deleteByXsonQuery(indices, types, new XsonSource(builder), listener);
    }

    @Override
    public DeleteByQueryResponse deleteByQuery(String[] indices, String[] types, String query) throws Exception {
        XContentQueryBuilder builder = QueryBuilders.queryString(query);
        builder = getFilteredQuery(indices, types, builder);
        return super.deleteByXsonQuery(indices, types, new XsonSource(builder), -1);
    }

    @Override
    public DeleteByQueryResponse deleteByQuery(String[] indices, String[] types, String query, long timeout) throws Exception {
        XContentQueryBuilder builder = QueryBuilders.queryString(query);
        builder = getFilteredQuery(indices, types, builder);
        return super.deleteByXsonQuery(indices, types, new XsonSource(builder), timeout);
    }


    @Override
    public DeleteByQueryResponse deleteByQuery(String[] indices, String[] types, XContentQueryBuilder builder, long timeout) throws Exception {
        builder = getFilteredQuery(indices, types, builder);
        return super.deleteByXsonQuery(indices, types, new XsonSource(builder), timeout);
    }

    @Override
    public DeleteByQueryResponse deleteByQuery(String[] indices, String[] types, XContentQueryBuilder builder) throws Exception {
        builder = getFilteredQuery(indices, types, builder);
        return super.deleteByXsonQuery(indices, types, new XsonSource(builder), -1);
    }

    @Override
    public void deleteByQuery(String[] indices, String[] types, XContentQueryBuilder builder, ActionListener<DeleteByQueryResponse> listener) throws Exception {
        builder = getFilteredQuery(indices, types, builder);
        super.deleteByXsonQuery(indices, types, new XsonSource(builder), listener);
    }

    private XContentQueryBuilder getFilteredQuery(String[] indices, String[] types, XContentQueryBuilder builder) {
//        def esUtility = application.RapidApplication.getUtility("EsUtility")
//        def allTypes = esUtility.getTypes();
//
//        def typesToBeFiltered;
//        if(types.length == 0){
//        	typesToBeFiltered = allTypes.keySet();
//        }
//        else{
//        	typesToBeFiltered = types.toList();
//        }
//        List filters = com.ifountain.compass.search.FilterManager.getEsFilters(typesToBeFiltered as String[]);
//        filters.each{filter ->
//        	QueryFilterBuilder filterBuilder = FilterBuilders.queryFilter(QueryBuilders.queryString(filter));
//        	filterBuilder.cache(true);
//        	builder = QueryBuilders.filteredQuery(builder, filterBuilder);
//        }
        return builder;
    }
}
