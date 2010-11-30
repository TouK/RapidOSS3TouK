package com.ifountain.es.repo

import org.elasticsearch.index.query.xcontent.QueryBuilders
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import com.ifountain.es.test.util.ElasticSearchTestUtils
import com.ifountain.elasticsearch.util.EsUtils
import com.ifountain.es.ClosureActionListener
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction;
/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 30, 2010
 * Time: 4:54:57 PM
 */
class EsRepositoryDeleteByQueryTest extends EsRepositoryTestCase {

  public void testDeleteByQueryWithStringQuery() throws Exception {
    createIndices([indexWithAllProperties])
    EsRepository.getInstance().index(typeWithAllProperties, [:], [:])
    EsRepository.getInstance().index(typeWithAllProperties, [:], [:])
    EsRepository.getInstance().index(typeWithAllProperties, [keywordProp: "anotherKeywordProp"], [:])
    adapter.refreshIndices(indexWithAllProperties);

    assertEquals(3, adapter.count(indexWithAllProperties, typeWithAllProperties, "*:*").count())

    EsRepository.getInstance().deleteByQuery([typeWithAllProperties], "keywordProp:keyword_default_value", [:]);
    adapter.refreshIndices(indexWithAllProperties);

    assertEquals(1, adapter.count(indexWithAllProperties, typeWithAllProperties, "*:*").count())
    assertEquals(0, adapter.count(indexWithAllProperties, typeWithAllProperties, "keywordProp:keyword_default_value").count())
    assertEquals(1, adapter.count(indexWithAllProperties, typeWithAllProperties, "keywordProp:anotherKeywordProp").count())

  }

  public void testDeleteByQueryWithBuilder() throws Exception {
    createIndices([indexWithAllProperties])
    EsRepository.getInstance().index(typeWithAllProperties, [:], [:])
    EsRepository.getInstance().index(typeWithAllProperties, [:], [:])
    EsRepository.getInstance().index(typeWithAllProperties, [keywordProp: "anotherKeywordProp"], [:])
    adapter.refreshIndices(indexWithAllProperties);

    assertEquals(3, adapter.count(indexWithAllProperties, typeWithAllProperties, "*:*").count())

    EsRepository.getInstance().deleteByQuery([typeWithAllProperties], QueryBuilders.queryString("keywordProp:keyword_default_value"), [:]);
    adapter.refreshIndices(indexWithAllProperties);

    assertEquals(1, adapter.count(indexWithAllProperties, typeWithAllProperties, "*:*").count())
    assertEquals(0, adapter.count(indexWithAllProperties, typeWithAllProperties, "keywordProp:keyword_default_value").count())
    assertEquals(1, adapter.count(indexWithAllProperties, typeWithAllProperties, "keywordProp:anotherKeywordProp").count())
  }

  public void testDeleteByQueryThrowsExceptionIfTypeDoesNotExist() throws Exception {
    String nonExistantType = "nonExistantType"
    try {
      EsRepository.getInstance().deleteByQuery([nonExistantType], "*:*", [:])
      fail("should throw exception")
    }
    catch (e) {
      assertEquals("Type <" + nonExistantType + "> does not exist.", e.getMessage());
    }
  }

  public void testDeleteByQueryWithTheProvidedIndices() throws Exception {
    createIndices([indexWithAllProperties]);
    String anotherIndex = "anotherindex";
    EsUtils.createIndex(adapter, anotherIndex, new HashMap());
    ElasticSearchTestUtils.clearIndex(adapter, anotherIndex);
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithAllProperties, [:], [index: anotherIndex]);
    adapter.refreshIndices(anotherIndex);
    assertEquals(anotherIndex, indexResponse.index());

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    EsRepository.getInstance().deleteByQuery([typeWithAllProperties], "*:*", [indices: [anotherIndex]]);
    adapter.refreshIndices(anotherIndex);

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertFalse(getResponse.exists());
  }

  public void testAsynchDeleteByQuery() throws Exception {
    createIndices([indexWithAllProperties])
    EsRepository.getInstance().index(typeWithAllProperties, [:], [:])
    EsRepository.getInstance().index(typeWithAllProperties, [:], [:])
    EsRepository.getInstance().index(typeWithAllProperties, [keywordProp: "anotherKeywordProp"], [:])
    adapter.refreshIndices(indexWithAllProperties);

    assertEquals(3, adapter.count(indexWithAllProperties, typeWithAllProperties, "*:*").count())

    def responses = [];
    EsRepository.getInstance().deleteByQuery([typeWithAllProperties], QueryBuilders.queryString("keywordProp:keyword_default_value"), [:], new ClosureActionListener({r, f ->
      if (r) responses.add(r);
    }));
    CommonTestUtils.waitFor(new ClosureWaitAction({
      assertEquals(1, responses.size())
    }))
    adapter.refreshIndices(indexWithAllProperties);

    assertEquals(1, adapter.count(indexWithAllProperties, typeWithAllProperties, "*:*").count())

    EsRepository.getInstance().deleteByQuery([typeWithAllProperties], "*:*", [:], new ClosureActionListener({r, f ->
      if (r) responses.add(r);
    }));
    CommonTestUtils.waitFor(new ClosureWaitAction({
      assertEquals(2, responses.size())
    }))
    adapter.refreshIndices(indexWithAllProperties);

    assertEquals(0, adapter.count(indexWithAllProperties, typeWithAllProperties, "*:*").count())

  }
}
