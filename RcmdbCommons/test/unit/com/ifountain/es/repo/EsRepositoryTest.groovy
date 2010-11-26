package com.ifountain.es.repo

import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.es.test.util.MockMappingProvider
import com.ifountain.es.mapping.TypeMapping
import com.ifountain.es.mapping.TypeProperty
import com.ifountain.es.mapping.EsMappingManager
import com.ifountain.es.test.util.ElasticSearchTestUtils
import com.ifountain.es.datasource.RossEsAdapter
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.get.GetResponse
import com.ifountain.core.test.util.DatasourceTestUtils

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
    assertEquals(7, entry.size());
    assertEquals("keyword_default_value", entry.keywordProp);
    assertEquals("whitespace_default_value", entry.whitespaceProp);
    assertEquals(-1, entry.intProp);
    assertEquals(1, (Long) entry.longProp);
    assertEquals(new Double(2.3), entry.doubleProp);
    assertEquals(new Float(5.0), entry.floatProp);
    assertTrue(entry.booleanProp);

    def props = [keywordProp: "keyword", whitespaceProp: "white space", intProp: 3, longProp: 5L,
            doubleProp: new Double(3.5), floatProp: new Float(2.7), booleanProp: false, undefinedProp: "undefinedValue"]
    indexResponse = EsRepository.getInstance().index(typeWithAllProperties, props, [:])

    adapter.refreshIndices(indexWithAllProperties);

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    entry = getResponse.sourceAsMap();
    assertEquals(7, entry.size());
    assertEquals("keyword", entry.keywordProp);
    assertEquals("white space", entry.whitespaceProp);
    assertEquals(3, entry.intProp);
    assertEquals(5, (Long) entry.longProp);
    assertEquals(new Double(3.5), entry.doubleProp);
    assertEquals(new Float(2.7), entry.floatProp);
    assertFalse(entry.booleanProp);
    assertNull(entry.undefinedProp);

    indexResponse = EsRepository.getInstance().index(typeWithAllProperties, props, [indexAll: true])

    adapter.refreshIndices(indexWithAllProperties);

    getResponse = adapter.get(indexResponse.index(), indexResponse.type(), indexResponse.id());
    assertTrue(getResponse.exists());

    entry = getResponse.sourceAsMap();
    assertEquals(8, entry.size());
    assertEquals("keyword", entry.keywordProp);
    assertEquals("white space", entry.whitespaceProp);
    assertEquals(3, entry.intProp);
    assertEquals(5, (Long) entry.longProp);
    assertEquals(new Double(3.5), entry.doubleProp);
    assertEquals(new Float(2.7), entry.floatProp);
    assertFalse(entry.booleanProp);
    assertEquals("undefinedValue", entry.undefinedProp);

  }

  private void createIndices(def indices) {
    def mappingConfig = [:];
    indices.each {index ->
      ElasticSearchTestUtils.deleteIndex(adapter, index);
      if (index == indexWithAllProperties) {
        mappingConfig[typeWithAllProperties] = getMappingsForIndexWithAllProperties();
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

}
