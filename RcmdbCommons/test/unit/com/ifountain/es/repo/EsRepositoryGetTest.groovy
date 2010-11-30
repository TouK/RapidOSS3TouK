package com.ifountain.es.repo

import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import com.ifountain.es.mapping.TypeProperty
import com.ifountain.elasticsearch.util.EsUtils
import com.ifountain.es.test.util.ElasticSearchTestUtils

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 30, 2010
 * Time: 11:52:13 AM
 */
class EsRepositoryGetTest extends EsRepositoryTestCase {

  public void testGetMethodWithId() throws Exception {
    createIndices([indexWithMultipleKeys])

    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithMultipleKeys, [keyProp1: "keyProp1Value", keyProp2: 2, keyProp3: true, prop1: "prop1Value", prop2: 3], [:])
    adapter.refreshIndices(indexWithMultipleKeys);

    GetResponse getResponse = EsRepository.getInstance().get(typeWithMultipleKeys, [id: indexResponse.id()]);
    assertTrue(getResponse.exists());
    assertEquals(indexWithMultipleKeys, getResponse.index())
    assertEquals(typeWithMultipleKeys, getResponse.type())
    assertEquals("keyProp1Value_2_true", getResponse.id())

    def entry = getResponse.sourceAsMap();
    assertEquals("keyProp1Value", entry.keyProp1)
    assertEquals("prop1Value", entry.prop1)
    assertEquals(2, entry.keyProp2)
    assertEquals(3, entry.prop2)
    assertTrue(entry.keyProp3)
  }

  public void testGetMethodWithKeys() throws Exception {
    createIndices([indexWithMultipleKeys])

    EsRepository.getInstance().index(typeWithMultipleKeys, [keyProp1: "keyProp1Value", keyProp2: 2, keyProp3: true, prop2: 3, prop1: "prop1Value"], [:])
    adapter.refreshIndices(indexWithMultipleKeys);

    GetResponse getResponse = EsRepository.getInstance().get(typeWithMultipleKeys, [keyProp1: "keyProp1Value", keyProp2: 2, keyProp3: true]);
    assertTrue(getResponse.exists());
    assertEquals(indexWithMultipleKeys, getResponse.index())
    assertEquals(typeWithMultipleKeys, getResponse.type())
    assertEquals("keyProp1Value_2_true", getResponse.id())

    def entry = getResponse.sourceAsMap();
    assertEquals("keyProp1Value", entry.keyProp1)
    assertEquals("prop1Value", entry.prop1)
    assertEquals(2, entry.keyProp2)
    assertEquals(3, entry.prop2)
    assertTrue(entry.keyProp3)
  }

  public void testGetMethodThrowsExceptionIfIdOrKeysAreNotProvided() throws Exception {
    createIndices([indexWithAllProperties, indexWithMultipleKeys])
    try {
      EsRepository.getInstance().get(typeWithAllProperties, [:]);
      fail("should throw exception")
    }
    catch (e) {
      assertEquals("Property <" + TypeProperty.ID + "> should be provided for type <" + typeWithAllProperties + ">", e.getMessage());
    }
    try {
      EsRepository.getInstance().get(typeWithMultipleKeys, [keyProp1: "keyProp1Value", keyProp2: 2]);
      fail("should throw exception")
    }
    catch (e) {
      assertEquals("Key property <keyProp3> for type <" + typeWithMultipleKeys + "> should be provided.", e.getMessage())
    }
  }

  public void testGetWithTheProvidedIndex() throws Exception {
    createIndices([indexWithAllProperties]);
    String anotherIndex = "anotherindex";
    EsUtils.createIndex(adapter, anotherIndex, new HashMap());
    ElasticSearchTestUtils.clearIndex(adapter, anotherIndex);
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithAllProperties, [:], [index: anotherIndex]);
    adapter.refreshIndices(anotherIndex);
    assertEquals(anotherIndex, indexResponse.index());

    GetResponse getResponse = EsRepository.getInstance().get(typeWithAllProperties, [id: indexResponse.id()], [index:anotherIndex]);
    assertTrue(getResponse.exists());
    assertEquals(anotherIndex, getResponse.index())
    assertEquals("keyword_default_value", getResponse.sourceAsMap().keywordProp)
  }

  public void testGetMethodThrowsExceptionIfTypeDoesNotExist() throws Exception {
    String nonExistantType = "nonExistantType"
    try {
      EsRepository.getInstance().get(nonExistantType, [:])
      fail("should throw exception")
    }
    catch (e) {
      assertEquals("Type <" + nonExistantType + "> does not exist.", e.getMessage());
    }
  }
}
