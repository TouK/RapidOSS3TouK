package com.ifountain.es.repo

import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import com.ifountain.es.test.util.ElasticSearchTestUtils
import com.ifountain.elasticsearch.util.EsUtils
import com.ifountain.es.mapping.TypeProperty
import com.ifountain.es.ClosureActionListener
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 30, 2010
 * Time: 11:15:10 AM
 */
class EsRepositoryDeleteTest extends EsRepositoryTestCase {

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

  public void testAsynchDelete() throws Exception {
    createIndices([indexWithMultipleKeys])

    IndexResponse indexResponse = EsRepository.getInstance().index(typeWithMultipleKeys, [keyProp1: "keyProp1Value", keyProp2: 2, keyProp3: true], [:])
    adapter.refreshIndices(indexWithMultipleKeys);

    GetResponse getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    def responses = []
    EsRepository.getInstance().delete(typeWithMultipleKeys, [id: indexResponse.id()], [:], new ClosureActionListener({resp, f ->
      if (resp) responses.add(resp);
    }))

    CommonTestUtils.waitFor(new ClosureWaitAction({
      assertEquals(1, responses.size())
    }))
    adapter.refreshIndices(indexWithMultipleKeys);
    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertFalse(getResponse.exists());
  }
}
