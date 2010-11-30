package com.ifountain.es.repo

import org.elasticsearch.index.query.xcontent.QueryBuilders
import com.ifountain.elasticsearch.util.EsUtils
import com.ifountain.es.test.util.ElasticSearchTestUtils
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.get.GetResponse

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 30, 2010
 * Time: 5:25:20 PM
 */
class EsRepositoryCountTest extends EsRepositoryTestCase {
  public void testCountWithStringQuery() throws Exception {
    createIndices([indexWithAllProperties])
    EsRepository.getInstance().index(typeWithAllProperties, [:], [:])
    EsRepository.getInstance().index(typeWithAllProperties, [:], [:])
    EsRepository.getInstance().index(typeWithAllProperties, [keywordProp: "anotherKeywordProp"], [:])
    adapter.refreshIndices(indexWithAllProperties);

    assertEquals(3, EsRepository.getInstance().count([typeWithAllProperties], "*:*", [:]))
  }

  public void testCountWithBuilder() throws Exception {
    createIndices([indexWithAllProperties])
    EsRepository.getInstance().index(typeWithAllProperties, [:], [:])
    EsRepository.getInstance().index(typeWithAllProperties, [:], [:])
    EsRepository.getInstance().index(typeWithAllProperties, [keywordProp: "anotherKeywordProp"], [:])
    adapter.refreshIndices(indexWithAllProperties);

    assertEquals(3, EsRepository.getInstance().count([typeWithAllProperties], QueryBuilders.matchAllQuery(), [:]))
  }

  public void testCountThrowsExceptionIfTypeDoesNotExist() throws Exception {
    String nonExistantType = "nonExistantType"
    try {
      EsRepository.getInstance().count([nonExistantType], "*:*", [:])
      fail("should throw exception")
    }
    catch (e) {
      assertEquals("Type <" + nonExistantType + "> does not exist.", e.getMessage());
    }
  }

  public void testCountWithTheProvidedIndices() throws Exception {
    createIndices([indexWithAllProperties]);
    String anotherIndex = "anotherindex";
    EsUtils.createIndex(adapter, anotherIndex, new HashMap());
    ElasticSearchTestUtils.clearIndex(adapter, anotherIndex);
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithAllProperties, [:], [index: anotherIndex]);
    adapter.refreshIndices(anotherIndex);
    assertEquals(anotherIndex, indexResponse.index());

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    assertEquals(1, EsRepository.getInstance().count([typeWithAllProperties], QueryBuilders.matchAllQuery(), [indices: [anotherIndex]]))
  }
}
