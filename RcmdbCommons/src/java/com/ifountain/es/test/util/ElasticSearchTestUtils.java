package com.ifountain.es.test.util;

import com.ifountain.comp.test.util.CommonTestUtils;
import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.elasticsearch.connection.ElasticSearchTransportConnectionImpl;
import com.ifountain.elasticsearch.datasource.ElasticSearchAdapter;
import junit.framework.Assert;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.transport.RemoteTransportException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 26, 2010
 * Time: 9:31:56 AM
 */
public class ElasticSearchTestUtils {
    public static final String ES_TEST_CONNECTION = "ElasticSearchConnection";
    public static final String ES_HOST = "EsHost";
    public static final String ES_PORT = "EsPort";
    public static final String ES_CLUSTER = "EsCluster";

    public static ConnectionParam getESConnectionParam() {
        Map<String, Object> otherParams = new HashMap<String, Object>();
        otherParams.put(ElasticSearchTransportConnectionImpl.HOST, CommonTestUtils.getTestProperty(ES_HOST));
        otherParams.put(ElasticSearchTransportConnectionImpl.PORT, Long.parseLong(CommonTestUtils.getTestProperty(ES_PORT)));
        otherParams.put(ElasticSearchTransportConnectionImpl.CLUSTER, CommonTestUtils.getTestProperty(ES_CLUSTER));
        ConnectionParam param = new ConnectionParam(ES_TEST_CONNECTION, ElasticSearchTransportConnectionImpl.class.getName(), otherParams);
        param.setMinTimeout(30000);
        param.setMaxTimeout(30000);
        return param;
    }

    public static void deleteIndex(ElasticSearchAdapter adapter, String indexName) throws Exception {
        try {
            Assert.assertTrue(adapter.deleteIndex(indexName));
        }
        catch (RemoteTransportException e) {
            if (!(e.getCause() instanceof IndexMissingException)) {
                throw e;
            }
        }
    }

    public static void clearIndex(ElasticSearchAdapter adapter, String indexName) throws Exception {
        adapter.deleteByQuery(new String[]{indexName}, new String[0], "*:*");
        adapter.refreshIndices(indexName);
    }
}
