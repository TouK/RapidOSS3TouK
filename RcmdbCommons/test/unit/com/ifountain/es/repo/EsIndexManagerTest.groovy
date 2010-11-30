package com.ifountain.es.repo

import com.ifountain.core.test.util.DatasourceTestUtils
import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.elasticsearch.datasource.actions.GetMappingResponse
import com.ifountain.elasticsearch.util.MappingConstants
import com.ifountain.es.datasource.RossEsAdapter
import com.ifountain.es.mapping.EsMappingManager
import com.ifountain.es.mapping.TypeMapping
import com.ifountain.es.mapping.TypeProperty
import com.ifountain.es.test.util.ElasticSearchTestUtils
import com.ifountain.es.test.util.MockMappingProvider

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 26, 2010
 * Time: 9:19:31 AM
 */
class EsIndexManagerTest extends RapidCoreTestCase {
  protected void setUp() {
    super.setUp();
    def param = ElasticSearchTestUtils.getESConnectionParam()
    param.setConnectionName(EsRepository.CONNECTION_NAME);
    DatasourceTestUtils.getParamSupplier().setParam(param)
  }

  public void testCreateAllIndices() throws Exception {
    RossEsAdapter adapter = EsRepository.getInstance().getAdapter();
    String index1 = "testindex1"
    String index2 = "testindex2"

    String type1 = "type1"
    String type2 = "type2"
    String type3 = "type3"

    ElasticSearchTestUtils.deleteIndex(adapter, index1)
    ElasticSearchTestUtils.deleteIndex(adapter, index2)

    TypeProperty prop1 = new TypeProperty("prop1", TypeProperty.STRING_TYPE)
    prop1.setDefaultValue("default");
    prop1.setAnalyzer(TypeProperty.WHITSPACE_ANALYZER);
    prop1.setIncludeInAll(false)
    prop1.setStore(true);

    TypeProperty prop2 = new TypeProperty("prop2", TypeProperty.STRING_TYPE)
    prop2.setAnalyzer(TypeProperty.KEYWORD_ANALYZER);

    TypeProperty prop3 = new TypeProperty("prop3", TypeProperty.INTEGER_TYPE)
    prop3.setDefaultValue(2)

    TypeProperty prop4 = new TypeProperty("prop4", TypeProperty.LONG_TYPE)
    TypeProperty prop5 = new TypeProperty("prop5", TypeProperty.FLOAT_TYPE)
    TypeProperty prop6 = new TypeProperty("prop6", TypeProperty.DOUBLE_TYPE)
    TypeProperty prop7 = new TypeProperty("prop7", TypeProperty.DATE_TYPE)
    TypeProperty prop8 = new TypeProperty("prop8", TypeProperty.BOOLEAN_TYPE)
    prop8.setIncludeInAll(false)

    TypeMapping typeMapping1 = new TypeMapping(type1, index1);
    typeMapping1.setAllEnabled(false)
    typeMapping1.addProperty(prop1)
    typeMapping1.addProperty(prop5)
    typeMapping1.addProperty(prop4)

    TypeMapping typeMapping2 = new TypeMapping(type2, index1);
    typeMapping2.addProperty(prop2)
    typeMapping2.addProperty(prop3)
    typeMapping2.addProperty(prop6)

    TypeMapping typeMapping3 = new TypeMapping(type3, index2);
    typeMapping3.addProperty(prop7)
    typeMapping3.addProperty(prop8)

    def mappings = [:]
    mappings[typeMapping1.getName()] = typeMapping1
    mappings[typeMapping2.getName()] = typeMapping2
    mappings[typeMapping3.getName()] = typeMapping3

    def provider = new MockMappingProvider()
    provider.setMappings(mappings)
    EsMappingManager.getInstance().setMappingProvider(provider);
    EsMappingManager.getInstance().load();
    EsIndexManager.createAllIndices();

    Thread.sleep(500);

    GetMappingResponse mappingResponse = adapter.getMapping([index1, index2] as String[], [] as String[]);
    def receivedMappings = mappingResponse.getMappings();
    assertEquals(2, receivedMappings.size());
    def indexMapping1 = receivedMappings.get(index1);
    assertNotNull(indexMapping1);
    assertEquals(2, indexMapping1.size());

    def tMapping1 = indexMapping1.get(type1);
    assertNotNull(tMapping1);
    assertFalse(tMapping1._all.enabled)
    assertEquals(MappingConstants.ROSS_WHITESPACE, tMapping1._all.analyzer)
    def properties = tMapping1.properties;
    assertEquals(3+EsMappingManager.createDefaultProperties().size(), properties.size());

    def prop1Mapping = properties.prop1;
    assertNotNull(prop1Mapping);

    assertEquals("multi_field", prop1Mapping.type)

    def prop1Fields = prop1Mapping.fields;
    assertEquals(2, prop1Fields.size());
    def prop1WhitspaceMapping = prop1Fields.prop1
    def prop1ExactMapping = prop1Fields.exact
    assertNotNull(prop1WhitspaceMapping)
    assertNotNull(prop1ExactMapping)

    assertEquals(TypeProperty.STRING_TYPE, prop1WhitspaceMapping.type)
    assertEquals(MappingConstants.ROSS_WHITESPACE, prop1WhitspaceMapping.analyzer)
    assertEquals("default", prop1WhitspaceMapping.null_value)
    assertEquals("yes", prop1WhitspaceMapping.store)
    assertFalse(prop1WhitspaceMapping.include_in_all)

    assertEquals(TypeProperty.STRING_TYPE, prop1ExactMapping.type)
    assertEquals(MappingConstants.ROSS_KEYWORD, prop1ExactMapping.analyzer)
    assertEquals("default", prop1ExactMapping.null_value)
    assertEquals("yes", prop1ExactMapping.store)
    assertFalse(prop1ExactMapping.include_in_all)

    def prop4Mapping = properties.prop4;
    assertNotNull(prop4Mapping);

    assertEquals(TypeProperty.LONG_TYPE, prop4Mapping.type)
    assertNull(prop4Mapping.store)
    assertFalse(prop4Mapping.include_in_all)

    def prop5Mapping = properties.prop5;
    assertNotNull(prop5Mapping);

    assertEquals(TypeProperty.FLOAT_TYPE, prop5Mapping.type)
    assertNull(prop5Mapping.store)
    assertFalse(prop5Mapping.include_in_all)



    def tMapping2 = indexMapping1.get(type2);
    assertNotNull(tMapping2);
    assertNull(tMapping2._all.enabled)
    assertEquals(MappingConstants.ROSS_WHITESPACE, tMapping1._all.analyzer)
    properties = tMapping2.properties;
    assertEquals(3+EsMappingManager.createDefaultProperties().size(), properties.size());

    def prop2Mapping = properties.prop2;
    assertNotNull(prop2Mapping);

    assertEquals(TypeProperty.STRING_TYPE, prop2Mapping.type)
    assertEquals(MappingConstants.ROSS_KEYWORD, prop2Mapping.analyzer)
    assertNull(prop2Mapping.store)
    assertTrue(prop2Mapping.include_in_all)

    def prop3Mapping = properties.prop3;
    assertNotNull(prop3Mapping);

    assertEquals(TypeProperty.INTEGER_TYPE, prop3Mapping.type)
    assertNull(prop3Mapping.store)
    assertTrue(prop3Mapping.include_in_all)

    def prop6Mapping = properties.prop6;
    assertNotNull(prop6Mapping);

    assertEquals(TypeProperty.DOUBLE_TYPE, prop6Mapping.type)
    assertNull(prop6Mapping.store)
    assertTrue(prop6Mapping.include_in_all)


    def indexMapping2 = receivedMappings.get(index2);
    assertNotNull(indexMapping2);
    assertEquals(1, indexMapping2.size());

    def tMapping3 = indexMapping2.get(type3);
    assertNotNull(tMapping3);
    assertNull(tMapping3._all.enabled)
    assertEquals(MappingConstants.ROSS_WHITESPACE, tMapping3._all.analyzer)
    properties = tMapping3.properties;
    assertEquals(2+EsMappingManager.createDefaultProperties().size(), properties.size());

    def prop7Mapping = properties.prop7;
    assertNotNull(prop7Mapping);

    assertEquals(TypeProperty.DATE_TYPE, prop7Mapping.type)
    assertNull(prop7Mapping.store)
    assertTrue(prop7Mapping.include_in_all)

    def prop8Mapping = properties.prop8;
    assertNotNull(prop8Mapping);

    assertEquals(TypeProperty.BOOLEAN_TYPE, prop8Mapping.type)
    assertNull(prop8Mapping.store)
    assertFalse(prop8Mapping.include_in_all)


  }

}
