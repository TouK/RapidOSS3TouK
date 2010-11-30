package com.ifountain.es.repo

import com.ifountain.es.mapping.TypeProperty
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.bulk.BulkItemResponse
import org.elasticsearch.action.bulk.BulkResponse
import com.ifountain.es.test.util.ElasticSearchTestUtils
import com.ifountain.elasticsearch.util.EsUtils
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.es.ClosureActionListener
import com.ifountain.rcmdb.test.util.ClosureWaitAction

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 30, 2010
 * Time: 11:19:18 AM
 */
class EsRepositoryBulkTest extends EsRepositoryTestCase {
  public void testBulkMethodIndexesEntryWithDefaultValues() throws Exception {
    createIndices([indexWithAllProperties])
    def bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_INDEX_ACTION, type: typeWithAllProperties, properties: [:]]);
    BulkResponse bulkResponse = EsRepository.getInstance().bulk(bulkActions);
    BulkItemResponse[] items = bulkResponse.items();
    assertEquals(1, items.length)
    adapter.refreshIndices(indexWithAllProperties);

    GetResponse getResponse = adapter.get(indexWithAllProperties, typeWithAllProperties, items[0].id());
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
    bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_INDEX_ACTION, type: typeWithAllProperties, properties: props]);
    bulkResponse = EsRepository.getInstance().bulk(bulkActions);
    items = bulkResponse.items();
    assertEquals(1, items.length)
    adapter.refreshIndices(indexWithAllProperties);

    getResponse = adapter.get(indexWithAllProperties, typeWithAllProperties, items[0].id());
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

    bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_INDEX_ACTION, type: typeWithAllProperties, properties: props, indexAll: true]);
    bulkResponse = EsRepository.getInstance().bulk(bulkActions);
    items = bulkResponse.items();
    assertEquals(1, items.length)
    adapter.refreshIndices(indexWithAllProperties);

    getResponse = adapter.get(indexWithAllProperties, typeWithAllProperties, items[0].id());
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

  public void testBulkWithKeyProperties() throws Exception {
    createIndices([indexWithOneKey, indexWithMultipleKeys])
    def bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_INDEX_ACTION, type: typeWithOneKey, properties: [:]]);
    try {
      EsRepository.getInstance().bulk(bulkActions)
      fail("should throw exception");
    }
    catch (Exception e) {
      assertEquals("Key property <keyProp> for type <" + typeWithOneKey + "> should be provided.", e.getMessage());
    }

    bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_DELETE_ACTION, type: typeWithOneKey, properties: [:]]);
    try {
      EsRepository.getInstance().bulk(bulkActions)
      fail("should throw exception");
    }
    catch (Exception e) {
      assertEquals("Key property <keyProp> for type <" + typeWithOneKey + "> should be provided.", e.getMessage());
    }

    bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_INDEX_ACTION, type: typeWithOneKey, properties: [keyProp: "keyPropValue", prop1: "prop1Value", prop2: 1]]);
    BulkResponse bulkResponse = EsRepository.getInstance().bulk(bulkActions)
    BulkItemResponse[] items = bulkResponse.items();
    assertEquals(1, items.length)
    adapter.refreshIndices(indexWithOneKey);

    GetResponse getResponse = adapter.get(items[0].index(), items[0].type(), items[0].id());
    assertTrue(getResponse.exists());
    assertEquals("keyPropValue", getResponse.id());

    def entry = getResponse.sourceAsMap();
    assertEquals("keyPropValue", entry.keyProp)
    assertEquals("prop1Value", entry.prop1)
    assertEquals(1, entry.prop2)

    bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_DELETE_ACTION, type: typeWithOneKey, properties: [keyProp: "keyPropValue", prop1: "prop1Value", prop2: 1]]);
    bulkResponse = EsRepository.getInstance().bulk(bulkActions)
    items = bulkResponse.items();
    assertEquals(1, items.length)
    adapter.refreshIndices(indexWithOneKey);

    getResponse = adapter.get(items[0].index(), items[0].type(), items[0].id());
    assertFalse(getResponse.exists());

    bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_INDEX_ACTION, type: typeWithMultipleKeys, properties: [keyProp1: "keyProp1Value", keyProp2: 2, prop2: 1]]);
    try {
      EsRepository.getInstance().bulk(bulkActions)
      fail("should throw exception");
    }
    catch (Exception e) {
      assertEquals("Key property <keyProp3> for type <" + typeWithMultipleKeys + "> should be provided.", e.getMessage());
    }

    bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_DELETE_ACTION, type: typeWithMultipleKeys, properties: [keyProp1: "keyProp1Value", keyProp2: 2]]);
    try {
      EsRepository.getInstance().bulk(bulkActions)
      fail("should throw exception");
    }
    catch (Exception e) {
      assertEquals("Key property <keyProp3> for type <" + typeWithMultipleKeys + "> should be provided.", e.getMessage());
    }

    bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_INDEX_ACTION, type: typeWithMultipleKeys, properties: [keyProp1: "keyProp1Value", keyProp2: 1, keyProp3: true, prop1: "prop1Value", prop2: 1]]);
    bulkResponse = EsRepository.getInstance().bulk(bulkActions);
    items = bulkResponse.items();
    assertEquals(1, items.length)
    adapter.refreshIndices(indexWithMultipleKeys);

    getResponse = adapter.get(items[0].index(), items[0].type(), items[0].id());
    assertTrue(getResponse.exists());
    assertEquals("keyProp1Value_1_true", getResponse.id());

    entry = getResponse.sourceAsMap();
    assertEquals("keyProp1Value", entry.keyProp1)
    assertEquals(1, entry.keyProp2)
    assertTrue(entry.keyProp3)
    assertEquals("prop1Value", entry.prop1)
    assertEquals(1, entry.prop2)

    bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_DELETE_ACTION, type: typeWithMultipleKeys, properties: [keyProp1: "keyProp1Value", keyProp2: 1, keyProp3: true, prop1: "prop1Value"]]);
    bulkResponse = EsRepository.getInstance().bulk(bulkActions);
    items = bulkResponse.items();
    assertEquals(1, items.length)
    adapter.refreshIndices(indexWithMultipleKeys);

    getResponse = adapter.get(items[0].index(), items[0].type(), items[0].id());
    assertFalse(getResponse.exists());
  }

  public void testBulkOverridesTheOldEntryWithTheSameKey() throws Exception {
    createIndices([indexWithMultipleKeys])
    def bulkActions = [];
    def props = [keyProp1: "keyProp1Value", keyProp2: 1, keyProp3: true, prop1: "prop1Value", prop2: 1];
    bulkActions.add([action: EsRepository.BULK_INDEX_ACTION, type: typeWithMultipleKeys, properties: props]);
    BulkResponse bulkResponse = EsRepository.getInstance().bulk(bulkActions)
    BulkItemResponse[] items = bulkResponse.items();
    assertEquals(1, items.length)
    adapter.refreshIndices(indexWithMultipleKeys);

    GetResponse getResponse = adapter.get(items[0].index(), items[0].type(), items[0].id());
    assertTrue(getResponse.exists());
    assertEquals("keyProp1Value_1_true", getResponse.id());

    def entry = getResponse.sourceAsMap();
    assertEquals("prop1Value", entry.prop1)
    assertEquals(1, entry.prop2)
    assertTrue(entry[TypeProperty.RS_INSERTED_AT] > 0);
    assertTrue(entry[TypeProperty.RS_UPDATED_AT] > 0);
    assertEquals(entry[TypeProperty.RS_INSERTED_AT], entry[TypeProperty.RS_UPDATED_AT])

    bulkActions = [];
    props = [keyProp1: "keyProp1Value", keyProp2: 1, keyProp3: true, prop1: "prop1UpdatedValue"];
    bulkActions.add([action: EsRepository.BULK_INDEX_ACTION, type: typeWithMultipleKeys, properties: props]);
    bulkResponse = EsRepository.getInstance().bulk(bulkActions)
    items = bulkResponse.items();
    assertEquals(1, items.length)
    adapter.refreshIndices(indexWithMultipleKeys);

    assertEquals(1, adapter.count(items[0].index(), items[0].type(), "*:*").count())
    getResponse = adapter.get(items[0].index(), items[0].type(), items[0].id());
    assertTrue(getResponse.exists());
    assertEquals("keyProp1Value_1_true", getResponse.id());

    entry = getResponse.sourceAsMap();
    assertEquals("prop1UpdatedValue", entry.prop1)
    assertEquals(0, entry.prop2)
    assertTrue(entry[TypeProperty.RS_INSERTED_AT] > 0);
    assertTrue(entry[TypeProperty.RS_UPDATED_AT] > 0);
    assertEquals(entry[TypeProperty.RS_INSERTED_AT], entry[TypeProperty.RS_UPDATED_AT])
  }

  public void testBulkWithNullAndEmptyStrings() throws Exception {
    createIndices([indexWithAllProperties])
    def bulkActions = [];
    def props = [keywordProp: null, whitespaceProp: "", intProp: 3, longProp: 5L,
            undefinedProp1: "undefinedValue", undefinedProp2: "", undefinedProp3: null]
    bulkActions.add([action: EsRepository.BULK_INDEX_ACTION, type: typeWithAllProperties, properties: props, indexAll: true]);
    BulkResponse bulkResponse = EsRepository.getInstance().bulk(bulkActions)
    BulkItemResponse[] items = bulkResponse.items();
    assertEquals(1, items.length)
    adapter.refreshIndices(indexWithAllProperties);

    GetResponse getResponse = adapter.get(items[0].index(), items[0].type(), items[0].id());
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

  public void testAsynchBulk() throws Exception {
    createIndices([indexWithMultipleKeys])
    def bulkActions = [];
    def props = [keyProp1: "keyProp1Value", keyProp2: 1, keyProp3: true, prop1: "prop1Value", prop2: 1];
    bulkActions.add([action: EsRepository.BULK_INDEX_ACTION, type: typeWithMultipleKeys, properties: props, indexAll: true]);
    def responses = [];
    EsRepository.getInstance().bulk(bulkActions, new ClosureActionListener({resp, failure ->
      if (resp) responses.add(resp);
    }))
    CommonTestUtils.waitFor(new ClosureWaitAction({
      assertEquals(1, responses.size());
    }))
    BulkResponse bulkResponse = responses[0];
    BulkItemResponse[] items = bulkResponse.items();
    assertEquals(1, items.length)
    adapter.refreshIndices(indexWithMultipleKeys);

    GetResponse getResponse = adapter.get(items[0].index(), items[0].type(), items[0].id());
    assertTrue(getResponse.exists());
    assertEquals("keyProp1Value_1_true", getResponse.id());

    def entry = getResponse.sourceAsMap();
    assertEquals("keyProp1Value", entry.keyProp1)
    assertEquals(1, entry.keyProp2)
    assertTrue(entry.keyProp3)
    assertEquals("prop1Value", entry.prop1)
    assertEquals(1, entry.prop2)
  }

  public void testBulkMethodThrowsExceptionIfTypeDoesNotExist() throws Exception {
    String nonExistantType = "nonExistantType"
    def bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_INDEX_ACTION, type: nonExistantType, properties: [:]]);
    try {
      EsRepository.getInstance().bulk(bulkActions)
      fail("should throw exception")
    }
    catch (e) {
      assertEquals("Type <" + nonExistantType + "> does not exist.", e.getMessage());
    }

    bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_DELETE_ACTION, type: nonExistantType, properties: [:]]);
    try {
      EsRepository.getInstance().bulk(bulkActions)
      fail("should throw exception")
    }
    catch (e) {
      assertEquals("Type <" + nonExistantType + "> does not exist.", e.getMessage());
    }
  }

  public void testBulkMethodWritingToTheProvidedIndex() throws Exception {
    createIndices([indexWithAllProperties]);
    String anotherIndex = "anotherindex";
    EsUtils.createIndex(adapter, anotherIndex, new HashMap());
    ElasticSearchTestUtils.clearIndex(adapter, anotherIndex);

    def bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_INDEX_ACTION, type: typeWithAllProperties, properties: [:], index: anotherIndex]);
    BulkResponse bulkResponse = EsRepository.getInstance().bulk(bulkActions);
    BulkItemResponse[] items = bulkResponse.items();
    assertEquals(1, items.length)
    adapter.refreshIndices(anotherIndex);
    assertEquals(anotherIndex, items[0].index());

    GetResponse getResponse = adapter.get(items[0].index(), items[0].type(), items[0].id());
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

    bulkActions = [];
    bulkActions.add([action: EsRepository.BULK_DELETE_ACTION, type: typeWithAllProperties, properties: [id: getResponse.id()], index: anotherIndex]);
    bulkResponse = EsRepository.getInstance().bulk(bulkActions);
    items = bulkResponse.items();
    assertEquals(1, items.length)
    adapter.refreshIndices(anotherIndex);
    assertEquals(anotherIndex, items[0].index());

    getResponse = adapter.get(items[0].index(), items[0].type(), items[0].id());
    assertFalse(getResponse.exists());
  }

  public void testBulkWithMultipleActions() throws Exception {
    createIndices([indexWithAllProperties, indexWithMultipleKeys, indexWithOneKey])

    def bulkActions = []
    bulkActions.add(type: typeWithAllProperties, action: EsRepository.BULK_INDEX_ACTION, properties: [keywordProp: "keywordPropValue"])
    bulkActions.add(type: typeWithMultipleKeys, action: EsRepository.BULK_INDEX_ACTION, properties: [keyProp1: "key1", keyProp2: 1, keyProp3: true, prop1: "prop1Value"])
    bulkActions.add(type: typeWithOneKey, action: EsRepository.BULK_INDEX_ACTION, properties: [keyProp: "key2", prop1: "prop1Value"])

    BulkResponse bulkResponse = EsRepository.getInstance().bulk(bulkActions)
    BulkItemResponse[] items = bulkResponse.items();
    assertEquals(3, items.length);
    adapter.refreshIndices(indexWithAllProperties, indexWithMultipleKeys, indexWithOneKey);

    GetResponse getResponse1 = adapter.get(items[0].index(), items[0].type(), items[0].id());
    assertTrue(getResponse1.exists());

    def entry1 = getResponse1.sourceAsMap();
    assertEquals("keywordPropValue", entry1.keywordProp);

    GetResponse getResponse2 = adapter.get(items[1].index(), items[1].type(), items[1].id());
    assertTrue(getResponse2.exists());

    def entry2 = getResponse2.sourceAsMap();
    assertEquals("key1", entry2.keyProp1);
    assertEquals("prop1Value", entry2.prop1);
    assertEquals(1, entry2.keyProp2);
    assertTrue(entry2.keyProp3);

    GetResponse getResponse3 = adapter.get(items[2].index(), items[2].type(), items[2].id());
    assertTrue(getResponse3.exists());

    def entry3 = getResponse3.sourceAsMap();
    assertEquals("key2", entry3.keyProp);
    assertEquals("prop1Value", entry2.prop1);


    bulkActions = [];
    bulkActions.add(type: typeWithAllProperties, action: EsRepository.BULK_DELETE_ACTION, properties: [id: getResponse1.id()])
    bulkActions.add(type: typeWithMultipleKeys, action: EsRepository.BULK_DELETE_ACTION, properties: [id: getResponse2.id()])
    bulkActions.add(type: typeWithOneKey, action: EsRepository.BULK_DELETE_ACTION, properties: [id: getResponse3.id()])
    bulkActions.add(type: typeWithAllProperties, action: EsRepository.BULK_INDEX_ACTION, properties: [keywordProp: "anotherKeywordPropValue"])

    bulkResponse = EsRepository.getInstance().bulk(bulkActions)
    items = bulkResponse.items();
    assertEquals(4, items.length);
    adapter.refreshIndices(indexWithAllProperties, indexWithMultipleKeys, indexWithOneKey);

    assertFalse(adapter.get(items[0].index(), items[0].type(), items[0].id()).exists())
    assertFalse(adapter.get(items[1].index(), items[1].type(), items[1].id()).exists())
    assertFalse(adapter.get(items[2].index(), items[2].type(), items[2].id()).exists())

    GetResponse getResponse4 = adapter.get(items[3].index(), items[3].type(), items[3].id());
    assertTrue(getResponse4.exists());

    def entry4 = getResponse4.sourceAsMap();
    assertEquals("anotherKeywordPropValue", entry4.keywordProp);
  }
}
