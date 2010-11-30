package com.ifountain.es.repo

import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.core.test.util.DatasourceTestUtils
import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.es.ClosureActionListener
import com.ifountain.es.datasource.RossEsAdapter
import com.ifountain.es.mapping.EsMappingManager
import com.ifountain.es.mapping.TypeMapping
import com.ifountain.es.mapping.TypeProperty
import com.ifountain.es.test.util.ElasticSearchTestUtils
import com.ifountain.es.test.util.MockMappingProvider
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import com.ifountain.elasticsearch.util.EsUtils
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.bulk.BulkItemResponse

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 25, 2010
 * Time: 2:21:23 PM
 */
class EsRepositoryTest extends RapidCoreTestCase {
  def indexWithAllProperties = "testindex1";
  def typeWithAllProperties = "type1";
  def indexWithOneKey = "testindex2"
  def typeWithOneKey = "type2"
  def indexWithMultipleKeys = "testindex3"
  def typeWithMultipleKeys = "type3"
  RossEsAdapter adapter;
  MockMappingProvider mappingProvider;

  protected void setUp() {
    super.setUp();
    def param = ElasticSearchTestUtils.getESConnectionParam()
    param.setConnectionName(EsRepository.CONNECTION_NAME);
    DatasourceTestUtils.getParamSupplier().setParam(param)
    mappingProvider = new MockMappingProvider();
    EsMappingManager.getInstance().setMappingProvider(mappingProvider)
    adapter = EsRepository.getInstance().getAdapter();
  }

  protected void tearDown() {
    super.tearDown();
  }

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

  public void testDeleteMethodWithId() throws Exception {
    createIndices([indexWithMultipleKeys])

    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithMultipleKeys, [keyProp1: "keyProp1Value", keyProp2: 2, keyProp3: true], [:])
    adapter.refreshIndices(indexWithMultipleKeys);

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    EsRepository.getInstance().delete(typeWithMultipleKeys, [id: indexResponse.id()], [:]);
    adapter.refreshIndices(indexWithMultipleKeys);

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertFalse(getResponse.exists());
  }

  public void testDeleteMethodWithKeys() throws Exception {
    createIndices([indexWithMultipleKeys])

    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithMultipleKeys, [keyProp1: "keyProp1Value", keyProp2: 2, keyProp3: true], [:])
    adapter.refreshIndices(indexWithMultipleKeys);

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    EsRepository.getInstance().delete(typeWithMultipleKeys, [keyProp1: "keyProp1Value", keyProp2: 2, keyProp3: true], [:]);
    adapter.refreshIndices(indexWithMultipleKeys);

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertFalse(getResponse.exists());
  }

  public void testDeleteMethodThrowsExceptionIfIdOrKeysAreNotProvided() throws Exception {
    createIndices([indexWithAllProperties, indexWithMultipleKeys])
    try {
      EsRepository.getInstance().delete(typeWithAllProperties, [:], [:]);
      fail("should throw exception")
    }
    catch (e) {
      assertEquals("Property <" + TypeProperty.ID + "> should be provided for type <" + typeWithAllProperties + ">", e.getMessage());
    }
    try {
      EsRepository.getInstance().delete(typeWithMultipleKeys, [keyProp1: "keyProp1Value", keyProp2: 2], [:]);
      fail("should throw exception")
    }
    catch (e) {
      assertEquals("Key property <keyProp3> for type <" + typeWithMultipleKeys + "> should be provided.", e.getMessage())
    }
  }

  public void testDeletingWithTheProvidedIndex() throws Exception {
    createIndices([indexWithAllProperties]);
    String anotherIndex = "anotherindex";
    EsUtils.createIndex(adapter, anotherIndex, new HashMap());
    ElasticSearchTestUtils.clearIndex(adapter, anotherIndex);
    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithAllProperties, [:], [index: anotherIndex]);
    adapter.refreshIndices(anotherIndex);
    assertEquals(anotherIndex, indexResponse.index());

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    EsRepository.getInstance().delete(typeWithAllProperties, [id: indexResponse.id()], [index: anotherIndex]);
    adapter.refreshIndices(anotherIndex);

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertFalse(getResponse.exists());
  }

  public void testDeleteMethodThrowsExceptionIfTypeDoesNotExist() throws Exception {
    String nonExistantType = "nonExistantType"
    try {
      EsRepository.getInstance().delete(nonExistantType, [:], [:])
      fail("should throw exception")
    }
    catch (e) {
      assertEquals("Type <" + nonExistantType + "> does not exist.", e.getMessage());
    }
  }

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

  private void createIndices(def indices) {
    def mappingConfig = [:];
    indices.each {index ->
      ElasticSearchTestUtils.deleteIndex(adapter, index);
      if (index == indexWithAllProperties) {
        mappingConfig[typeWithAllProperties] = getMappingsForIndexWithAllProperties();
      }
      else if (index == indexWithOneKey) {
        mappingConfig[typeWithOneKey] = getMappingsForIndexWithOneKey();
      }
      else if (index == indexWithMultipleKeys) {
        mappingConfig[typeWithMultipleKeys] = getMappingsForIndexWithMultipleKeys();
      }
    }
    mappingProvider.setMappings(mappingConfig);
    EsMappingManager.getInstance().load();
    EsIndexManager.createAllIndices();
  }

  private TypeMapping getMappingsForIndexWithAllProperties() {
    TypeMapping typeMapping = new TypeMapping(typeWithAllProperties, indexWithAllProperties)
    TypeProperty keywordProp = new TypeProperty("keywordProp", TypeProperty.STRING_TYPE)
    keywordProp.setAnalyzer(TypeProperty.KEYWORD_ANALYZER);
    keywordProp.setDefaultValue("keyword_default_value");

    TypeProperty whitespaceProp = new TypeProperty("whitespaceProp", TypeProperty.STRING_TYPE)
    whitespaceProp.setAnalyzer(TypeProperty.WHITSPACE_ANALYZER);
    whitespaceProp.setDefaultValue("whitespace_default_value");

    TypeProperty intProp = new TypeProperty("intProp", TypeProperty.INTEGER_TYPE)
    intProp.setDefaultValue(new Integer(-1));

    TypeProperty longProp = new TypeProperty("longProp", TypeProperty.LONG_TYPE)
    longProp.setDefaultValue(new Long(1));

    TypeProperty doubleProp = new TypeProperty("doubleProp", TypeProperty.DOUBLE_TYPE)
    doubleProp.setDefaultValue(new Double(2.3));

    TypeProperty floatProp = new TypeProperty("floatProp", TypeProperty.FLOAT_TYPE)
    floatProp.setDefaultValue(new Float(5.0));

    TypeProperty booleanProp = new TypeProperty("booleanProp", TypeProperty.BOOLEAN_TYPE)
    booleanProp.setDefaultValue(new Boolean(true));

    typeMapping.addProperty(keywordProp)
    typeMapping.addProperty(whitespaceProp)
    typeMapping.addProperty(intProp)
    typeMapping.addProperty(longProp)
    typeMapping.addProperty(doubleProp)
    typeMapping.addProperty(floatProp)
    typeMapping.addProperty(booleanProp)

    return typeMapping;
  }

  private TypeMapping getMappingsForIndexWithOneKey() {
    TypeMapping typeMapping = new TypeMapping(typeWithOneKey, indexWithOneKey)
    TypeProperty keyProp = new TypeProperty("keyProp", TypeProperty.STRING_TYPE)
    keyProp.setKey(true);
    keyProp.setAnalyzer(TypeProperty.KEYWORD_ANALYZER);

    TypeProperty prop1 = new TypeProperty("prop1", TypeProperty.STRING_TYPE)
    prop1.setAnalyzer(TypeProperty.WHITSPACE_ANALYZER);
    prop1.setDefaultValue("prop1DefaultValue")

    TypeProperty prop2 = new TypeProperty("prop2", TypeProperty.INTEGER_TYPE)
    prop2.setDefaultValue(0);

    typeMapping.addProperty(keyProp)
    typeMapping.addProperty(prop1)
    typeMapping.addProperty(prop2)
    return typeMapping;
  }

  private TypeMapping getMappingsForIndexWithMultipleKeys() {
    TypeMapping typeMapping = new TypeMapping(typeWithMultipleKeys, indexWithMultipleKeys)
    TypeProperty keyProp1 = new TypeProperty("keyProp1", TypeProperty.STRING_TYPE)
    keyProp1.setKey(true);
    keyProp1.setAnalyzer(TypeProperty.KEYWORD_ANALYZER);

    TypeProperty keyProp2 = new TypeProperty("keyProp2", TypeProperty.INTEGER_TYPE)
    keyProp2.setKey(true)
    TypeProperty keyProp3 = new TypeProperty("keyProp3", TypeProperty.BOOLEAN_TYPE)
    keyProp3.setKey(true);

    TypeProperty prop1 = new TypeProperty("prop1", TypeProperty.STRING_TYPE)
    prop1.setDefaultValue("prop1DefaultValue")
    prop1.setAnalyzer(TypeProperty.WHITSPACE_ANALYZER);

    TypeProperty prop2 = new TypeProperty("prop2", TypeProperty.INTEGER_TYPE)
    prop2.setDefaultValue(0);

    typeMapping.addProperty(keyProp1)
    typeMapping.addProperty(keyProp2)
    typeMapping.addProperty(keyProp3)
    typeMapping.addProperty(prop1)
    typeMapping.addProperty(prop2)
    return typeMapping;

  }

}
