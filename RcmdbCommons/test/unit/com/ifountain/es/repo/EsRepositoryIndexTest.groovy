package com.ifountain.es.repo

import com.ifountain.es.mapping.TypeProperty
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import com.ifountain.es.test.util.ElasticSearchTestUtils
import com.ifountain.elasticsearch.util.EsUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.es.ClosureActionListener

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 30, 2010
 * Time: 11:09:13 AM
 */
class EsRepositoryIndexTest extends EsRepositoryTestCase {
  public void testEsRepositoryIsSingleton() throws Exception {
    EsRepository repo1 = EsRepository.getInstance();
    EsRepository repo2 = EsRepository.getInstance();
    assertSame(repo1, repo2);
  }

  public void testIndexMethodIndexesEntryWithDefaultValues() throws Exception {
    createIndices([indexWithAllProperties])
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithAllProperties, [:], [:]);
    adapter.refreshIndices(indexWithAllProperties);

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    def entry = getResponse.sourceAsMap();
    assertEquals(9, entry.size());
    assertEquals("keyword_default_value", entry.keywordProp);
    assertEquals("whitespace_default_value", entry.whitespaceProp);
    assertEquals(-1, entry.intProp);
    assertEquals(1, (Long) entry.longProp);
    assertEquals(new Double(2.3), entry.doubleProp);
    assertEquals(new Float(5.0), entry.floatProp);
    assertTrue(entry.booleanProp);
    assertTrue(entry[TypeProperty.RS_INSERTED_AT] > 0);
    assertTrue(entry[TypeProperty.RS_UPDATED_AT] > 0);
    assertEquals(entry[TypeProperty.RS_INSERTED_AT], entry[TypeProperty.RS_UPDATED_AT])

    def props = [keywordProp: "keyword", whitespaceProp: "white space", intProp: 3, longProp: 5L,
            doubleProp: new Double(3.5), floatProp: new Float(2.7), booleanProp: false, undefinedProp: "undefinedValue"]
    indexResponse = EsRepository.getInstance().index(typeWithAllProperties, props, [:])

    adapter.refreshIndices(indexWithAllProperties);

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    entry = getResponse.sourceAsMap();
    assertEquals(9, entry.size());
    assertEquals("keyword", entry.keywordProp);
    assertEquals("white space", entry.whitespaceProp);
    assertEquals(3, entry.intProp);
    assertEquals(5, (Long) entry.longProp);
    assertEquals(new Double(3.5), entry.doubleProp);
    assertEquals(new Float(2.7), entry.floatProp);
    assertFalse(entry.booleanProp);
    assertNull(entry.undefinedProp);
    assertTrue(entry[TypeProperty.RS_INSERTED_AT] > 0);
    assertTrue(entry[TypeProperty.RS_UPDATED_AT] > 0);
    assertEquals(entry[TypeProperty.RS_INSERTED_AT], entry[TypeProperty.RS_UPDATED_AT])

    indexResponse = EsRepository.getInstance().index(typeWithAllProperties, props, [indexAll: true])

    adapter.refreshIndices(indexWithAllProperties);

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    entry = getResponse.sourceAsMap();
    assertEquals(10, entry.size());
    assertEquals("keyword", entry.keywordProp);
    assertEquals("white space", entry.whitespaceProp);
    assertEquals(3, entry.intProp);
    assertEquals(5, (Long) entry.longProp);
    assertEquals(new Double(3.5), entry.doubleProp);
    assertEquals(new Float(2.7), entry.floatProp);
    assertFalse(entry.booleanProp);
    assertEquals("undefinedValue", entry.undefinedProp);
    assertTrue(entry[TypeProperty.RS_INSERTED_AT] > 0);
    assertTrue(entry[TypeProperty.RS_UPDATED_AT] > 0);
    assertEquals(entry[TypeProperty.RS_INSERTED_AT], entry[TypeProperty.RS_UPDATED_AT])
  }

  public void testIndexingWithKeyProperties() throws Exception {
    createIndices([indexWithOneKey, indexWithMultipleKeys])
    try {
      EsRepository.getInstance().index(typeWithOneKey, [:], [:])
      fail("should throw exception");
    }
    catch (Exception e) {
      assertEquals("Key property <keyProp> for type <" + typeWithOneKey + "> should be provided.", e.getMessage());
    }
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithOneKey, [keyProp: "keyPropValue", prop1: "prop1Value", prop2: 1], [:])
    adapter.refreshIndices(indexWithOneKey);

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());
    assertEquals("keyPropValue", getResponse.id());

    def entry = getResponse.sourceAsMap();
    assertEquals("keyPropValue", entry.keyProp)
    assertEquals("prop1Value", entry.prop1)
    assertEquals(1, entry.prop2)

    try {
      EsRepository.getInstance().index(typeWithMultipleKeys, [keyProp1: "keyProp1Value", keyProp2: 2], [:])
      fail("should throw exception");
    }
    catch (Exception e) {
      assertEquals("Key property <keyProp3> for type <" + typeWithMultipleKeys + "> should be provided.", e.getMessage());
    }

    indexResponse = EsRepository.getInstance().index(typeWithMultipleKeys, [keyProp1: "keyProp1Value", keyProp2: 1, keyProp3: true, prop1: "prop1Value", prop2: 1], [:])
    adapter.refreshIndices(indexWithMultipleKeys);

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());
    assertEquals("keyProp1Value_1_true", getResponse.id());

    entry = getResponse.sourceAsMap();
    assertEquals("keyProp1Value", entry.keyProp1)
    assertEquals(1, entry.keyProp2)
    assertTrue(entry.keyProp3)
    assertEquals("prop1Value", entry.prop1)
    assertEquals(1, entry.prop2)
  }

  public void testIndexOverridesTheOldEntryWithTheSameKey() throws Exception {
    createIndices([indexWithMultipleKeys])
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithMultipleKeys, [keyProp1: "keyProp1Value", keyProp2: 1, keyProp3: true, prop1: "prop1Value", prop2: 1], [:])
    adapter.refreshIndices(indexWithMultipleKeys);

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());
    assertEquals("keyProp1Value_1_true", getResponse.id());

    def entry = getResponse.sourceAsMap();
    assertEquals("prop1Value", entry.prop1)
    assertEquals(1, entry.prop2)
    assertTrue(entry[TypeProperty.RS_INSERTED_AT] > 0);
    assertTrue(entry[TypeProperty.RS_UPDATED_AT] > 0);
    assertEquals(entry[TypeProperty.RS_INSERTED_AT], entry[TypeProperty.RS_UPDATED_AT])

    indexResponse = EsRepository.getInstance().index(typeWithMultipleKeys, [keyProp1: "keyProp1Value", keyProp2: 1, keyProp3: true, prop1: "prop1UpdatedValue"], [:])
    adapter.refreshIndices(indexWithMultipleKeys);

    assertEquals(1, adapter.count(indexResponse.index(), indexResponse.type(), "*:*").count())
    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());
    assertEquals("keyProp1Value_1_true", getResponse.id());

    entry = getResponse.sourceAsMap();
    assertEquals("prop1UpdatedValue", entry.prop1)
    assertEquals(0, entry.prop2)
    assertTrue(entry[TypeProperty.RS_INSERTED_AT] > 0);
    assertTrue(entry[TypeProperty.RS_UPDATED_AT] > 0);
    assertEquals(entry[TypeProperty.RS_INSERTED_AT], entry[TypeProperty.RS_UPDATED_AT])
  }

  public void testIndexWithNullAndEmptyStrings() throws Exception {
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
  }

  public void testAsynchIndexing() throws Exception {
    createIndices([indexWithMultipleKeys])

    def props = [keyProp1: "keyProp1Value", keyProp2: 1, keyProp3: true, prop1: "prop1Value", prop2: 1];
    def responses = [];
    EsRepository.getInstance().index(typeWithMultipleKeys, props, [:], new ClosureActionListener({resp, failure ->
      if (resp) responses.add(resp);
    }))
    CommonTestUtils.waitFor(new ClosureWaitAction({
      assertEquals(1, responses.size());
    }))
    IndexResponse indexResponse = responses[0];
    adapter.refreshIndices(indexWithMultipleKeys);

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());
    assertEquals("keyProp1Value_1_true", getResponse.id());

    def entry = getResponse.sourceAsMap();
    assertEquals("keyProp1Value", entry.keyProp1)
    assertEquals(1, entry.keyProp2)
    assertTrue(entry.keyProp3)
    assertEquals("prop1Value", entry.prop1)
    assertEquals(1, entry.prop2)
  }

  public void testIndexMethodThrowsExceptionIfTypeDoesNotExist() throws Exception {
    String nonExistantType = "nonExistantType"
    try {
      EsRepository.getInstance().index(nonExistantType, [:], [:])
      fail("should throw exception")
    }
    catch (e) {
      assertEquals("Type <" + nonExistantType + "> does not exist.", e.getMessage());
    }
  }

  public void testIndexMethodWritingToTheProvidedIndex() throws Exception {
    createIndices([indexWithAllProperties]);
    String anotherIndex = "anotherindex";
    EsUtils.createIndex(adapter, anotherIndex, new HashMap());
    ElasticSearchTestUtils.clearIndex(adapter, anotherIndex);
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithAllProperties, [:], [index: anotherIndex]);
    adapter.refreshIndices(anotherIndex);
    assertEquals(anotherIndex, indexResponse.index());

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());
    def entry = getResponse.sourceAsMap();
    assertEquals(9, entry.size());
    assertEquals("keyword_default_value", entry.keywordProp);
    assertEquals("whitespace_default_value", entry.whitespaceProp);
    assertEquals(-1, entry.intProp);
    assertEquals(1, (Long) entry.longProp);
    assertEquals(new Double(2.3), entry.doubleProp);
    assertEquals(new Float(5.0), entry.floatProp);
    assertTrue(entry.booleanProp);
    assertTrue(entry[TypeProperty.RS_INSERTED_AT] > 0);
    assertTrue(entry[TypeProperty.RS_UPDATED_AT] > 0);
    assertEquals(entry[TypeProperty.RS_INSERTED_AT], entry[TypeProperty.RS_UPDATED_AT])
  }
}
