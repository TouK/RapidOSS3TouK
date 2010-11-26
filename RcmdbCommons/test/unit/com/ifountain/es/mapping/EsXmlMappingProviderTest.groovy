package com.ifountain.es.mapping

import com.ifountain.core.test.util.RapidCoreTestCase
import org.apache.commons.io.FileUtils

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 25, 2010
 * Time: 3:04:57 PM
 * To change this template use File | Settings | File Templates.
 */
class EsXmlMappingProviderTest extends RapidCoreTestCase {
  String testOutputDir = "../testoutput";
  public void setUp() {
    super.setUp();
    File testOutputDirectory = new File(testOutputDir);
    FileUtils.deleteDirectory (testOutputDirectory);
    testOutputDirectory.mkdirs();
  }

  public void tearDown() {
    super.tearDown();
  }

  public void testConstructMappings() {
    String xmlContent = """
      <Types>
          <Type Name="type1" Index="index1" AllEnabled="true">
              <Properties>
                  <Property Name="prop1" Type="String" DefaultValue="abc" Analyzer="${TypeProperty.KEYWORD_ANALYZER}"  IsKey="true" IncludeInAll="false" Store="true"></Property>
                  <Property Name="prop2" Type="int" DefaultValue="12" Analyzer="${TypeProperty.KEYWORD_ANALYZER}"  IsKey="false" IncludeInAll="true" Store="false"></Property>
              </Properties>
          </Type>

          <Type Name="type2" Index="index2" AllEnabled="false">
              <Properties>
                  <Property Name="prop1" Type="Double" DefaultValue="15" Analyzer="${TypeProperty.WHITSPACE_ANALYZER}"  IsKey="false" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>
      </Types>
    """
    File typeMappingConfigFile = new File("${testOutputDir}/EsTypeConfiguration.xml");
    typeMappingConfigFile.setText (xmlContent);
    
    EsXmlMappingProvider provider = new EsXmlMappingProvider(testOutputDir);
    Map<String, TypeMapping> mappings = provider.constructMappings();
    assertEquals (2, mappings.size());
    //check type1
    TypeMapping mapping = mappings["type1"];
    assertEquals ("index1", mapping.getIndex())
    assertTrue (mapping.isAllEnabled())
    Map<String, TypeProperty> typeProps = mapping.getTypeProperties();
    assertEquals (2, typeProps.size());
    
    TypeProperty prop = mapping.getTypeProperty("prop1");
    assertEquals (TypeProperty.STRING_TYPE, prop.getType());
    assertEquals (TypeProperty.KEYWORD_ANALYZER, prop.getAnalyzer());
    assertTrue (prop.isKey());
    assertFalse (prop.isIncludeInAll());
    assertTrue (prop.isStore());
    assertEquals("abc", prop.getDefaultValue());

    prop = mapping.getTypeProperty("prop2");
    assertEquals (TypeProperty.INTEGER_TYPE, prop.getType());
    assertEquals (TypeProperty.KEYWORD_ANALYZER, prop.getAnalyzer());
    assertFalse (prop.isKey());
    assertTrue (prop.isIncludeInAll());
    assertFalse (prop.isStore());
    assertEquals(Integer.name, prop.getDefaultValue().class.getName());
    assertEquals(12, prop.getDefaultValue());

    //check type2
    mapping = mappings["type2"];
    assertEquals ("index2", mapping.getIndex())
    assertFalse (mapping.isAllEnabled())
    typeProps = mapping.getTypeProperties();
    assertEquals (1, typeProps.size());

    prop = mapping.getTypeProperty("prop1");
    assertEquals (TypeProperty.DOUBLE_TYPE, prop.getType());
    assertEquals (TypeProperty.WHITSPACE_ANALYZER, prop.getAnalyzer());
    assertFalse (prop.isKey());
    assertFalse (prop.isIncludeInAll());
    assertTrue (prop.isStore());
    assertEquals(Double.name, prop.getDefaultValue().class.getName());
    assertEquals((int)15.0, (int)prop.getDefaultValue());
  }

  public void testConstructMappingsWithMultipleConfigFiles(){
    String xmlContent1 = """
      <Types>
          <Type Name="type1" Index="index1" AllEnabled="true">
              <Properties>
                  <Property Name="prop1" Type="String" DefaultValue="abc" Analyzer="${TypeProperty.KEYWORD_ANALYZER}"  IsKey="true" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>
      </Types>
    """

    String xmlContent2 = """
      <Types>
          <Type Name="type2" Index="index2" AllEnabled="false">
              <Properties>
                  <Property Name="prop1" Type="Double" DefaultValue="15" Analyzer="${TypeProperty.WHITSPACE_ANALYZER}"  IsKey="false" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>
      </Types>
    """

    String xmlContent3 = """
      <Types>
          <Type Name="type3" Index="index1" AllEnabled="true">
              <Properties>
                  <Property Name="prop1" Type="String" DefaultValue="abc" Analyzer="${TypeProperty.KEYWORD_ANALYZER}"  IsKey="true" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>
      </Types>
    """
    File typeMappingConfigFile1 = new File("${testOutputDir}/Sample1EsTypeConfiguration.xml");
    File typeMappingConfigFile2 = new File("${testOutputDir}/Sample2EsTypeConfiguration.xml");
    File typeMappingConfigFile3 = new File("${testOutputDir}/Sample3.xml");
    typeMappingConfigFile1.setText (xmlContent1);
    typeMappingConfigFile2.setText (xmlContent2);
    typeMappingConfigFile3.setText (xmlContent3);

    EsXmlMappingProvider provider = new EsXmlMappingProvider(testOutputDir);
    Map<String, TypeMapping> mappings = provider.constructMappings();
    assertEquals (2, mappings.size());
    //check type1
    TypeMapping mapping = mappings["type1"];
    assertEquals ("index1", mapping.getIndex())
    assertTrue (mapping.isAllEnabled())
    Map<String, TypeProperty> typeProps = mapping.getTypeProperties();
    assertEquals (1, typeProps.size());

    TypeProperty prop = mapping.getTypeProperty("prop1");
    assertEquals (TypeProperty.STRING_TYPE, prop.getType());
    assertEquals (TypeProperty.KEYWORD_ANALYZER, prop.getAnalyzer());
    assertTrue (prop.isKey());
    assertFalse (prop.isIncludeInAll());
    assertTrue (prop.isStore());
    assertEquals("abc", prop.getDefaultValue());
    //check type2
    mapping = mappings["type2"];
    assertEquals ("index2", mapping.getIndex())
    assertFalse (mapping.isAllEnabled())
    typeProps = mapping.getTypeProperties();
    assertEquals (1, typeProps.size());

    prop = mapping.getTypeProperty("prop1");
    assertEquals (TypeProperty.DOUBLE_TYPE, prop.getType());
    assertEquals (TypeProperty.WHITSPACE_ANALYZER, prop.getAnalyzer());
    assertFalse (prop.isKey());
    assertFalse (prop.isIncludeInAll());
    assertTrue (prop.isStore());
    assertEquals(Double.name, prop.getDefaultValue().class.getName());
    assertEquals((int)15.0, (int)prop.getDefaultValue());
  }

  public void testConstructMappingsThrowsExceptionIfDuplicateTypeExist(){
    String xmlContent = """
      <Types>
          <Type Name="type1" Index="index1" AllEnabled="true">
              <Properties>
                  <Property Name="prop1" Type="String" DefaultValue="abc" Analyzer="${TypeProperty.KEYWORD_ANALYZER}"  IsKey="true" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>

          <Type Name="type1" Index="index2" AllEnabled="false">
              <Properties>
                  <Property Name="prop1" Type="Double" DefaultValue="15" Analyzer="${TypeProperty.WHITSPACE_ANALYZER}"  IsKey="false" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>
      </Types>
    """
    File typeMappingConfigFile = new File("${testOutputDir}/EsTypeConfiguration.xml");
    typeMappingConfigFile.setText (xmlContent);

    EsXmlMappingProvider provider = new EsXmlMappingProvider(testOutputDir);
    try {
      provider.constructMappings()
      fail("Should throw exception multiple types exist");
    } catch (MappingException e) {
      MappingException expectedEx = MappingProviderException.duplicateTypeException("type1", [typeMappingConfigFile.path]);
      assertEquals (expectedEx.toString(), e.toString());
    }
  }

  public void testConstructMappingsThrowsExceptionIfDuplicateTypeExistInMultipleFiles(){
    String xmlContent = """
      <Types>
          <Type Name="type1" Index="index1" AllEnabled="true">
              <Properties>
                  <Property Name="prop1" Type="String" DefaultValue="abc" Analyzer="${TypeProperty.KEYWORD_ANALYZER}"  IsKey="true" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>
      </Types>
    """
    File typeMappingConfigFile1 = new File("${testOutputDir}/Sample1EsTypeConfiguration.xml");
    File typeMappingConfigFile2 = new File("${testOutputDir}/Sample2EsTypeConfiguration.xml");
    typeMappingConfigFile1.setText (xmlContent);
    typeMappingConfigFile2.setText (xmlContent);

    EsXmlMappingProvider provider = new EsXmlMappingProvider(testOutputDir);
    try {
      provider.constructMappings()
      fail("Should throw exception multiple types exist");
    } catch (MappingException e) {
      MappingException expectedEx = MappingProviderException.duplicateTypeException("type1", [typeMappingConfigFile1.path, typeMappingConfigFile2.path]);
      assertEquals (expectedEx.toString(), e.toString());
    }
  }

  public void testConstructMappingsThrowsExceptionIfUnexpectedAttributesExistInTypeDefinition(){
    String xmlContent = """
      <Types>
          <Type Name="type1" Index="index1" AllEnabled="true" InvalidAttribute1="abdcd" InvalidAttribute2="abdcd">
              <Properties>
                  <Property Name="prop1" Type="String" DefaultValue="abc" Analyzer="tokenized"  IsKey="true" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>
      </Types>
    """
    File typeMappingConfigFile1 = new File("${testOutputDir}/Sample1EsTypeConfiguration.xml");
    typeMappingConfigFile1.setText (xmlContent);

    EsXmlMappingProvider provider = new EsXmlMappingProvider(testOutputDir);
    try {
      provider.constructMappings()
      fail("Should throw exception since invalid attributes exist");
    } catch (MappingException e) {
      MappingException expectedEx = MappingProviderException.invalidAttributeInTypeDefinitionException("type1", ["InvalidAttribute1","InvalidAttribute2"].sort(), typeMappingConfigFile1.path);
      assertEquals (expectedEx.toString(), e.toString());
    }
  }

  public void testConstructMappingsThrowsExceptionIfUnexpectedAttributesExistInTypePropertyDefinition(){
    String xmlContent = """
      <Types>
          <Type Name="type1" Index="index1" AllEnabled="true">
              <Properties>
                  <Property Name="prop1" Type="String" DefaultValue="abc" Analyzer="${TypeProperty.KEYWORD_ANALYZER}"  IsKey="true" IncludeInAll="false" Store="true"  InvalidAttribute1="abdcd" InvalidAttribute2="abdcd"></Property>
              </Properties>
          </Type>
      </Types>
    """
    File typeMappingConfigFile1 = new File("${testOutputDir}/Sample1EsTypeConfiguration.xml");
    typeMappingConfigFile1.setText (xmlContent);

    EsXmlMappingProvider provider = new EsXmlMappingProvider(testOutputDir);
    try {
      provider.constructMappings()
      fail("Should throw exception since invalid attributes exist in type property");
    } catch (MappingException e) {
      MappingException expectedEx = MappingProviderException.invalidAttributeInTypePropertyDefinitionException("type1", "prop1", ["InvalidAttribute1","InvalidAttribute2"].sort(), typeMappingConfigFile1.path);
      assertEquals (expectedEx.toString(), e.toString());
    }
  }

  public void testConstructMappingsThrowsExceptionIfInvalidTypeNameExist(){
    String invalidTypeName = "Invalid type name type1"
    String xmlContent = """
      <Types>
          <Type Name="${invalidTypeName}" Index="index1" AllEnabled="true">
              <Properties>
                  <Property Name="prop1" Type="String" DefaultValue="abc" Analyzer="tokenized"  IsKey="true" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>
      </Types>
    """
    File typeMappingConfigFile1 = new File("${testOutputDir}/Sample1EsTypeConfiguration.xml");
    typeMappingConfigFile1.setText (xmlContent);

    EsXmlMappingProvider provider = new EsXmlMappingProvider(testOutputDir);
    try {
      provider.constructMappings()
      fail("Should throw exception since name of type is invalid");
    } catch (MappingException e) {
      MappingException nestedException = MappingException.invalidTypeNameException(invalidTypeName);
      MappingException expectedEx = MappingProviderException.invalidTypePropetiesException(invalidTypeName, typeMappingConfigFile1.path, nestedException);
      assertEquals (expectedEx.toString(), e.toString());
    }
  }

  public void testConstructMappingsThrowsExceptionIfInvalidIndexNameExist(){
    String invalidIndexName = "Invalid index name"
    String xmlContent = """
      <Types>
          <Type Name="type1" Index="${invalidIndexName}" AllEnabled="true">
              <Properties>
                  <Property Name="prop1" Type="String" DefaultValue="abc" Analyzer="tokenized"  IsKey="true" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>
      </Types>
    """
    File typeMappingConfigFile1 = new File("${testOutputDir}/Sample1EsTypeConfiguration.xml");
    typeMappingConfigFile1.setText (xmlContent);

    EsXmlMappingProvider provider = new EsXmlMappingProvider(testOutputDir);
    try {
      provider.constructMappings()
      fail("Should throw exception since index name of type is invalid");
    } catch (MappingException e) {
      MappingException nestedException = MappingException.invalidIndexNameException(invalidIndexName);
      MappingException expectedEx = MappingProviderException.invalidTypePropetiesException("type1", typeMappingConfigFile1.path, nestedException);
      assertEquals (expectedEx.toString(), e.toString());
    }
  }
  public void testConstructMappingsThrowsExceptionIfInvalidPropertyNameExist(){
    String invalidPropertyName = "Invalid property name"
    String xmlContent = """
      <Types>
          <Type Name="type1" Index="index1" AllEnabled="true">
              <Properties>
                  <Property Name="${invalidPropertyName}" Type="String" DefaultValue="abc" Analyzer="tokenized"  IsKey="true" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>
      </Types>
    """
    File typeMappingConfigFile1 = new File("${testOutputDir}/Sample1EsTypeConfiguration.xml");
    typeMappingConfigFile1.setText (xmlContent);

    EsXmlMappingProvider provider = new EsXmlMappingProvider(testOutputDir);
    try {
      provider.constructMappings()
      fail("Should throw exception since type property name is invalid");
    } catch (MappingException e) {
      MappingException nestedException = MappingException.invalidPropertyNameException(invalidPropertyName);
      MappingException expectedEx = MappingProviderException.invalidTypePropetiesException("type1", typeMappingConfigFile1.path, nestedException);
      assertEquals (expectedEx.toString(), e.toString());
    }
  }
  
  public void testConstructMappingsThrowsExceptionIfInvalidPropertyTypeExist(){
    String invalidPropertyType = "InvalidType"
    String xmlContent = """
      <Types>
          <Type Name="type1" Index="index1" AllEnabled="true">
              <Properties>
                  <Property Name="prop1" Type="${invalidPropertyType}" DefaultValue="abc" Analyzer="tokenized"  IsKey="true" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>
      </Types>
    """
    File typeMappingConfigFile1 = new File("${testOutputDir}/Sample1EsTypeConfiguration.xml");
    typeMappingConfigFile1.setText (xmlContent);

    EsXmlMappingProvider provider = new EsXmlMappingProvider(testOutputDir);
    try {
      provider.constructMappings()
      fail("Should throw exception since type property type is invalid");
    } catch (MappingException e) {
      MappingException nestedException = MappingException.invalidPropertyTypeException(invalidPropertyType.toLowerCase());
      MappingException expectedEx = MappingProviderException.invalidTypePropetiesException("type1", typeMappingConfigFile1.path, nestedException);
      assertEquals (expectedEx.getMessage(), e.getMessage());
    }
  }
  
  public void testConstructMappingsThrowsExceptionIfInvalidDefaultValueExist(){
    String invalidPropertyType = "InvalidType"
    String xmlContent = """
      <Types>
          <Type Name="type1" Index="index1" AllEnabled="true">
              <Properties>
                  <Property Name="prop1" Type="int" DefaultValue="abc" Analyzer="${TypeProperty.KEYWORD_ANALYZER}"  IsKey="true" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>
      </Types>
    """
    File typeMappingConfigFile1 = new File("${testOutputDir}/Sample1EsTypeConfiguration.xml");
    typeMappingConfigFile1.setText (xmlContent);

    EsXmlMappingProvider provider = new EsXmlMappingProvider(testOutputDir);
    try {
      provider.constructMappings()
      fail("Should throw exception since type property type is invalid");
    } catch (MappingException e) {
      MappingException nestedException = MappingProviderException.defaultValueException(TypeProperty.INTEGER_TYPE, "abc", null);
      MappingException expectedEx = MappingProviderException.invalidDefaultValueException("type1", "prop1", typeMappingConfigFile1.path, nestedException);
      assertEquals (expectedEx.getMessage(), e.getMessage());
    }
  }

  

  public void testAnalyzerValidation()
  {
    String invalidPropertyAnalyzer = "InvalidAnalyzer"
    String xmlContent = """
      <Types>
          <Type Name="type1" Index="index1" AllEnabled="true">
              <Properties>
                  <Property Name="prop1" Type="String" DefaultValue="abc" Analyzer="${invalidPropertyAnalyzer}"  IsKey="true" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>
      </Types>
    """
    File typeMappingConfigFile1 = new File("${testOutputDir}/Sample1EsTypeConfiguration.xml");
    typeMappingConfigFile1.setText (xmlContent);

    EsXmlMappingProvider provider = new EsXmlMappingProvider(testOutputDir);
    try {
      provider.constructMappings()
      fail("Should throw exception since type analyzer type is invalid");
    } catch (MappingException e) {
      MappingException nestedException = MappingException.invalidAnalyzerTypeException(invalidPropertyAnalyzer);
      MappingException expectedEx = MappingProviderException.invalidTypePropetiesException("type1", typeMappingConfigFile1.path, nestedException);
      assertEquals (expectedEx.getMessage(), e.getMessage());
    }
  }
  
  public void testAnalyzerValidation1()
  {
    String invalidKeyPropertyAnalyzer = TypeProperty.WHITSPACE_ANALYZER;
    String xmlContent = """
      <Types>
          <Type Name="type1" Index="index1" AllEnabled="true">
              <Properties>
                  <Property Name="prop1" Type="String" DefaultValue="abc" Analyzer="${invalidKeyPropertyAnalyzer}"  IsKey="true" IncludeInAll="false" Store="true"></Property>
              </Properties>
          </Type>
      </Types>
    """
    File typeMappingConfigFile1 = new File("${testOutputDir}/Sample1EsTypeConfiguration.xml");
    typeMappingConfigFile1.setText (xmlContent);

    EsXmlMappingProvider provider = new EsXmlMappingProvider(testOutputDir);
    try {
      provider.constructMappings()
      fail("Should throw exception since key property analyzer type is invalid");
    } catch (MappingException e) {
      MappingException nestedException = MappingException.invalidAnalyzerForKeyProp(invalidKeyPropertyAnalyzer);
      MappingException expectedEx = MappingProviderException.invalidTypePropetiesException("type1", typeMappingConfigFile1.path, nestedException);
      assertEquals (expectedEx.getMessage(), e.getMessage());
    }
  }

}
