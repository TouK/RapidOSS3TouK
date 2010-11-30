package com.ifountain.es.repo

import com.ifountain.core.test.util.DatasourceTestUtils
import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.es.datasource.RossEsAdapter
import com.ifountain.es.mapping.EsMappingManager
import com.ifountain.es.mapping.TypeMapping
import com.ifountain.es.mapping.TypeProperty
import com.ifountain.es.test.util.ElasticSearchTestUtils
import com.ifountain.es.test.util.MockMappingProvider

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 25, 2010
 * Time: 2:21:23 PM
 */
class EsRepositoryTestCase extends RapidCoreTestCase {
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

  protected void createIndices(def indices) {
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

  protected TypeMapping getMappingsForIndexWithAllProperties() {
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

  protected TypeMapping getMappingsForIndexWithOneKey() {
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

  protected TypeMapping getMappingsForIndexWithMultipleKeys() {
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
