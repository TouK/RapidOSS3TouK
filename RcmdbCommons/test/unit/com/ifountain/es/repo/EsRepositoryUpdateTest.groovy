package com.ifountain.es.repo

import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import com.ifountain.es.mapping.TypeProperty
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.es.ClosureActionListener
import com.ifountain.elasticsearch.util.EsUtils
import com.ifountain.es.test.util.ElasticSearchTestUtils

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 30, 2010
 * Time: 11:13:11 AM
 */
class EsRepositoryUpdateTest extends EsRepositoryTestCase {
  public void testUpdateWithTypeWhichDoesNotHaveKeys() throws Exception {
    createIndices([indexWithAllProperties])
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithAllProperties, [:], [:]);
    adapter.refreshIndices(indexWithAllProperties);

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    def entry = getResponse.sourceAsMap();
    assertEquals("keyword_default_value", entry.keywordProp)
    assertEquals("whitespace_default_value", entry.whitespaceProp)
    assertEquals(-1, entry.intProp);
    def oldRsInsertedAt = entry[TypeProperty.RS_INSERTED_AT]
    def oldRsUpdatedAt = entry[TypeProperty.RS_UPDATED_AT]
    assertTrue(oldRsInsertedAt > 0);
    assertTrue(oldRsUpdatedAt > 0);
    assertEquals(oldRsInsertedAt, oldRsUpdatedAt)

    def props = [id: indexResponse.id(), keywordProp: "updatedKeywordProp", intProp: 3, undefinedProp: "undefinedPropValue"];
    indexResponse = EsRepository.getInstance().update(typeWithAllProperties, props, [:]);
    adapter.refreshIndices(indexWithAllProperties);

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    entry = getResponse.sourceAsMap();
    assertEquals("updatedKeywordProp", entry.keywordProp)
    assertEquals("whitespace_default_value", entry.whitespaceProp)
    assertEquals(3, entry.intProp);
    assertEquals(oldRsInsertedAt, entry[TypeProperty.RS_INSERTED_AT])
    assertTrue(entry[TypeProperty.RS_UPDATED_AT] > oldRsUpdatedAt)
    assertFalse(entry.containsKey("undefinedProp"))
  }

  public void testUpdateWithTypeWithKeys() throws Exception {
    createIndices([indexWithMultipleKeys])
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithMultipleKeys, [keyProp1: "key1", keyProp2: 2, keyProp3: true, prop1: "prop1Value", prop2: 4], [:]);
    adapter.refreshIndices(indexWithMultipleKeys);

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    def entry = getResponse.sourceAsMap();
    assertEquals("key1", entry.keyProp1)
    assertEquals(2, entry.keyProp2)
    assertTrue(entry.keyProp3)
    assertEquals("prop1Value", entry.prop1)
    assertEquals(4, entry.prop2)
    def oldRsInsertedAt = entry[TypeProperty.RS_INSERTED_AT]
    def oldRsUpdatedAt = entry[TypeProperty.RS_UPDATED_AT]
    assertTrue(oldRsInsertedAt > 0);
    assertTrue(oldRsUpdatedAt > 0);
    assertEquals(oldRsInsertedAt, oldRsUpdatedAt)

    def props = [id: indexResponse.id(), prop1: "updatedProp1Value", undefinedProp: "undefinedPropValue"];
    indexResponse = EsRepository.getInstance().update(typeWithMultipleKeys, props, [:]);
    adapter.refreshIndices(indexWithMultipleKeys);

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    entry = getResponse.sourceAsMap();
    assertEquals("key1", entry.keyProp1)
    assertEquals(2, entry.keyProp2)
    assertTrue(entry.keyProp3)
    assertEquals("updatedProp1Value", entry.prop1)
    assertEquals(4, entry.prop2)
    assertEquals(oldRsInsertedAt, entry[TypeProperty.RS_INSERTED_AT])
    assertTrue(entry[TypeProperty.RS_UPDATED_AT] > oldRsUpdatedAt)
    assertFalse(entry.containsKey("undefinedProp"))

    props = [keyProp1: "key1", keyProp2: 2, keyProp3: true, prop2: 3];
    indexResponse = EsRepository.getInstance().update(typeWithMultipleKeys, props, [:]);
    adapter.refreshIndices(indexWithMultipleKeys);

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    entry = getResponse.sourceAsMap();
    assertEquals("key1", entry.keyProp1)
    assertEquals(2, entry.keyProp2)
    assertTrue(entry.keyProp3)
    assertEquals("updatedProp1Value", entry.prop1)
    assertEquals(3, entry.prop2)
  }

  public void testUpdateWithExtraProperties() throws Exception {
    createIndices([indexWithAllProperties])
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithAllProperties, [:], [:]);
    adapter.refreshIndices(indexWithAllProperties);

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    def props = [id: indexResponse.id(), keywordProp: "updatedKeywordProp", intProp: 3, undefinedProp: "undefinedPropValue"];
    indexResponse = EsRepository.getInstance().update(typeWithAllProperties, props, [indexAll: true]);
    adapter.refreshIndices(indexWithAllProperties);

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    def entry = getResponse.sourceAsMap();
    assertEquals("updatedKeywordProp", entry.keywordProp)
    assertEquals("whitespace_default_value", entry.whitespaceProp)
    assertEquals(3, entry.intProp);
    assertEquals("undefinedPropValue", entry.undefinedProp);
  }

  public void testUpdateWithNullAndEmptyStrings() throws Exception {
    createIndices([indexWithAllProperties])
    def props = [keywordProp: null, whitespaceProp: "", intProp: 3, longProp: 5L,
            undefinedProp1: "undefinedValue", undefinedProp2: "", undefinedProp3: null]
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithAllProperties, props, [indexAll: true])
    adapter.refreshIndices(indexWithAllProperties);

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    def entry = getResponse.sourceAsMap();
    assertEquals("keyword_default_value", entry.keywordProp)
    assertEquals("", entry.whitespaceProp)
    assertEquals("undefinedValue", entry.undefinedProp1)
    assertEquals("", entry.undefinedProp2)
    assertFalse(entry.containsKey("undefinedProp3"));

    assertEquals(1, adapter.count(getResponse.index(), getResponse.type(), "whitespaceProp:\"\"").count())
    assertEquals(1, adapter.count(getResponse.index(), getResponse.type(), "undefinedProp2:\"\"").count())

    props = [id: indexResponse.id(), keywordProp: "", undefinedProp1: null, undefinedProp3: "undefinedProp3Value", undefinedProp4: "", undefinedProp5: null]

    indexResponse = EsRepository.getInstance().update(typeWithAllProperties, props, [indexAll: true])
    adapter.refreshIndices(indexWithAllProperties);

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    entry = getResponse.sourceAsMap();
    assertEquals("", entry.keywordProp)
    assertEquals("", entry.whitespaceProp)
    assertEquals("undefinedValue", entry.undefinedProp1)
    assertEquals("", entry.undefinedProp2)
    assertEquals("undefinedProp3Value", entry.undefinedProp3)
    assertEquals("", entry.undefinedProp4)
    assertFalse(entry.containsKey("undefinedProp5"));

    assertEquals(1, adapter.count(getResponse.index(), getResponse.type(), "keywordProp:\"\"").count())
    assertEquals(1, adapter.count(getResponse.index(), getResponse.type(), "whitespaceProp:\"\"").count())
    assertEquals(1, adapter.count(getResponse.index(), getResponse.type(), "undefinedProp2:\"\"").count())
    assertEquals(1, adapter.count(getResponse.index(), getResponse.type(), "undefinedProp4:\"\"").count())
  }

  public void testUpdateDoesNotUpdateKeys() throws Exception {
    createIndices([indexWithOneKey]);
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithOneKey, [keyProp: "key1", prop1: "prop1Value"], [:])
    adapter.refreshIndices(indexWithOneKey);

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    def entry = getResponse.sourceAsMap();
    assertEquals("key1", entry.keyProp)

    EsRepository.getInstance().update(typeWithOneKey, [id: indexResponse.id(), keyProp: "updatedKey1", prop1: "updatedProp1Value"], [:])
    adapter.refreshIndices(indexWithOneKey)

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    entry = getResponse.sourceAsMap();
    assertEquals("key1", entry.keyProp)
    assertEquals("updatedProp1Value", entry.prop1)
  }

  public void testUpdatingWithTheProvidedIndex() throws Exception {
    createIndices([indexWithAllProperties]);
    String anotherIndex = "anotherindex";
    EsUtils.createIndex(adapter, anotherIndex, new HashMap());
    ElasticSearchTestUtils.clearIndex(adapter, anotherIndex);
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithAllProperties, [:], [index: anotherIndex]);
    adapter.refreshIndices(anotherIndex);
    assertEquals(anotherIndex, indexResponse.index());

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    indexResponse = EsRepository.getInstance().update(typeWithAllProperties, [id: indexResponse.id(), keywordProp: "updatedKeywordProp"], [index: anotherIndex]);
    adapter.refreshIndices(anotherIndex);
    assertEquals(anotherIndex, indexResponse.index());

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    assertEquals("updatedKeywordProp", getResponse.sourceAsMap().keywordProp);
  }

  public void testAsynchUpdate() throws Exception {
    createIndices([indexWithAllProperties]);
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithAllProperties, [:], [:])
    adapter.refreshIndices(indexWithAllProperties);

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    def entry = getResponse.sourceAsMap();
    def oldRsInsertedAt = entry[TypeProperty.RS_INSERTED_AT]
    def oldRsUpdatedAt = entry[TypeProperty.RS_UPDATED_AT]

    assertEquals(oldRsInsertedAt, oldRsUpdatedAt);

    def props = [id: indexResponse.id(), keywordProp: "updatedKeyword", whitespaceProp: "updated whitespace", intProp: 3, longProp: 5L,
            floatProp: new Float(7.0), doubleProp: new Double(8.2), booleanProp: true]

    def responses = []
    EsRepository.getInstance().update(typeWithAllProperties, props, [:], new ClosureActionListener({resp, failure ->
      if (resp) responses.add(resp);
    }));

    CommonTestUtils.waitFor(new ClosureWaitAction({
      assertEquals(1, responses.size());
    }))
    adapter.refreshIndices(indexWithAllProperties);
    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    entry = getResponse.sourceAsMap();
    assertEquals("updatedKeyword", entry.keywordProp);
    assertEquals("updated whitespace", entry.whitespaceProp);
    assertEquals(3, entry.intProp);
    assertEquals(5, (Long) entry.longProp);
    assertEquals(new Float(7.0), entry.floatProp);
    assertEquals(new Double(8.2), entry.doubleProp);
    assertTrue(entry.booleanProp);

    assertEquals(oldRsInsertedAt, entry[TypeProperty.RS_INSERTED_AT])
    assertTrue(oldRsUpdatedAt < entry[TypeProperty.RS_UPDATED_AT])
  }

  public void testUpdateThrowsExceptionIfTypeDoesNotExist() throws Exception {
    String nonExistantType = "nonExistantType"
    try {
      EsRepository.getInstance().update(nonExistantType, [:], [:])
      fail("should throw exception")
    }
    catch (e) {
      assertEquals("Type <" + nonExistantType + "> does not exist.", e.getMessage());
    }
  }

  public void testUpdateMethodThrowsExceptionIfIdOrKeysAreNotProvided() throws Exception {
    createIndices([indexWithAllProperties, indexWithMultipleKeys])
    try {
      EsRepository.getInstance().update(typeWithAllProperties, [:], [:]);
      fail("Should throw exception")
    }
    catch (e) {
      assertEquals("Property <" + TypeProperty.ID + "> should be provided for type <" + typeWithAllProperties + ">", e.getMessage());
    }

    try {
      EsRepository.getInstance().update(typeWithMultipleKeys, [:], [:]);
      fail("Should throw exception")
    }
    catch (e) {
      assertEquals("Key property <keyProp1> for type <" + typeWithMultipleKeys + "> should be provided.", e.getMessage());
    }
  }

  public void testUpdateMethodThrowsExceptionIfEntryDoesNotExist() throws Exception {
    createIndices([indexWithOneKey])
    try {
      EsRepository.getInstance().update(typeWithOneKey, [keyProp: "0", prop1: "prop1Value"], [:]);
      fail("Should throw exception")
    }
    catch (e) {
      assertEquals("Entry with " + TypeProperty.ID + " <0> does not exist for type <" + typeWithOneKey + ">", e.getMessage());
    }
  }

  public void testAsynchUpdateReturnsErrorIfEntryDoesNotExist() throws Exception {
    createIndices([indexWithOneKey]);

    def failures = []
    EsRepository.getInstance().update(typeWithOneKey, [keyProp: "key1", prop1: "prop1Value"], [:], new ClosureActionListener({resp, failure ->
      if (failure) failures.add(failure);
    }));

    CommonTestUtils.waitFor(new ClosureWaitAction({
      assertEquals(1, failures.size());
    }))
    assertEquals("Entry with " + TypeProperty.ID + " <key1> does not exist for type <" + typeWithOneKey + ">", failures[0].getMessage())
  }
}
