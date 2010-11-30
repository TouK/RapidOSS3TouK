package com.ifountain.es.mapping

import groovy.util.slurpersupport.GPathResult
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FalseFileFilter
import org.apache.commons.io.filefilter.SuffixFileFilter
import com.ifountain.comp.config.ConfigurationProvider
import com.ifountain.rcmdb.config.XmlAttributeConversionException
import com.ifountain.rcmdb.config.XmlProcessingUtils

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 25, 2010
 * Time: 2:20:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class EsXmlMappingProvider extends ConfigurationProvider<TypeMapping> {
  public final static String WORKING_DIR_TEMP = "temp"
  public final static String MAPPING_FILE_EXTENSION = "EsTypeConfiguration.xml"
  public final static List FORBIDDEN_PROPERTY_NAMES = ["id", "_id", "all", "_all", TypeProperty.RS_INSERTED_AT, TypeProperty.RS_UPDATED_AT];
  public final static Map<String, Boolean> VALID_TYPE_ATTRIBUTES = [Name: true, Index: true, AllEnabled: false];
  public final static Map<String, Boolean> VALID_TYPE_PROPERTY_ATTRIBUTES = [Name: true, IsKey: false, Type: true, DefaultValue: false, Store: false, IncludeInAll: false, Analyzer: false];
  Map<String, Boolean> forbiddenPropertyMap = new HashMap<String, Boolean>();

  public EsXmlMappingProvider(String configurationBaseDirPath, String workingMappingFileDirPath) {
    super(configurationBaseDirPath,  workingMappingFileDirPath, WORKING_DIR_TEMP);
    FORBIDDEN_PROPERTY_NAMES.each{String propName->
      forbiddenPropertyMap.put(propName, true);      
    }
  }

  public void addInvalidPropName(String invalidPropName){
    forbiddenPropertyMap.put(invalidPropName, true);    
  }




  public Collection<File> getConfigurationFileInDir(File dir) {
    return FileUtils.listFiles(dir, new SuffixFileFilter([MAPPING_FILE_EXTENSION] as String[]), new FalseFileFilter()).sort(){it.name};
  }
  
  public Map<String, TypeMapping> constructBeans(Collection<File> confFileList) {

    Map<String, TypeMapping> allMappings = new HashMap<String, TypeMapping>();
    Map<String, List> typeFileLocations = new HashMap<String, List>();
    confFileList.each {File confFile ->
      String typeText = confFile.getText();
      List<TypeMapping> mappings = convertXmlToTypeMappings(typeText, confFile.path);
      mappings.each {TypeMapping mapping ->
        if (!typeFileLocations.containsKey(mapping.getName())) {
          typeFileLocations[mapping.name] = [];
        }
        allMappings.put(mapping.getName(), mapping);
        typeFileLocations[mapping.name].add(confFile.getPath());
      }
    }
    typeFileLocations.each {String type, List paths ->
      if (paths.size() > 1) {
        throw MappingProviderException.duplicateTypeException(type, paths.unique());
      }
    }
    return allMappings;
  }

  private List<TypeMapping> convertXmlToTypeMappings(String typesText, String filePath) {
    GPathResult res = new XmlSlurper().parseText(typesText);
    List<TypeMapping> typeMappings = new ArrayList<TypeMapping>();
    def allTypeNodes = res.Type;
    allTypeNodes.each {type ->
      TypeMapping mapping = convertXmlNodeToTypeMapping(type, filePath);
      typeMappings.add(mapping);
    }
    return typeMappings;
  }

  private TypeMapping convertXmlNodeToTypeMapping(xmlNode, filePath) {
    List missingMandatoryProps = XmlProcessingUtils.getMissingMandatoryAttributes(xmlNode, VALID_TYPE_ATTRIBUTES);
    if (!missingMandatoryProps.isEmpty()) {
      throw MappingProviderException.missingMandatoryXmlProperty(missingMandatoryProps, filePath);
    }
    try {
      String typeName = XmlProcessingUtils.getAttributeAs(xmlNode, "Name", String);
      String indexName = XmlProcessingUtils.getAttributeAs(xmlNode, "Index", String);
      boolean allEnabled = XmlProcessingUtils.getAttributeAs(xmlNode, "AllEnabled", Boolean);

      TypeMapping mapping = new TypeMapping(typeName, indexName);
      try {
        mapping.validate();
      }
      catch (MappingException e) {
        throw MappingProviderException.invalidTypePropetiesException(typeName, filePath, e)
      }


      List invalidAttributeNames = XmlProcessingUtils.getInvalidAttributes(xmlNode, VALID_TYPE_ATTRIBUTES);
      if (!invalidAttributeNames.isEmpty()) {
        throw MappingProviderException.invalidAttributeInTypeDefinitionException(typeName, invalidAttributeNames, filePath);
      }

      xmlNode.Properties.Property.each {propNode ->
        TypeProperty prop = convertXmlNodeToTypeProperty(typeName, propNode, filePath);
        mapping.addProperty(prop);
      }
      if (allEnabled != null)
        mapping.setAllEnabled(allEnabled);
      return mapping;
    } catch (XmlAttributeConversionException e) {
      throw MappingProviderException.invalidXmlAttribute(e.attributeName, e.attributeValue, filePath)
    }
  }

  private TypeProperty convertXmlNodeToTypeProperty(String typeName, xmlNode, filePath) {
    List missingMandatoryProps = XmlProcessingUtils.getMissingMandatoryAttributes(xmlNode, VALID_TYPE_PROPERTY_ATTRIBUTES);
    if (!missingMandatoryProps.isEmpty()) {
      throw MappingProviderException.missingMandatoryXmlProperty(missingMandatoryProps, filePath);
    }
    try {
      String propName = XmlProcessingUtils.getAttributeAs(xmlNode, "Name", String);
      if(forbiddenPropertyMap.containsKey(propName)){
        throw MappingProviderException.forbiddenPropertyNameIsUsed(typeName, propName, filePath);
      }
      String propType = XmlProcessingUtils.getAttributeAs(xmlNode, "Type", String);
      String defaultValueString = XmlProcessingUtils.getAttributeAs(xmlNode, "DefaultValue", String);
      String analyzer = XmlProcessingUtils.getAttributeAs(xmlNode, "Analyzer", String);
      boolean includedInAll = XmlProcessingUtils.getAttributeAs(xmlNode, "IncludeInAll", Boolean);
      boolean isKey = XmlProcessingUtils.getAttributeAs(xmlNode, "IsKey", Boolean);
      boolean store = XmlProcessingUtils.getAttributeAs(xmlNode, "Store", Boolean);

      TypeProperty prop = new TypeProperty(propName, propType);
      if (analyzer != null)
        prop.setAnalyzer(analyzer);
      if (includedInAll != null)
        prop.setIncludeInAll(includedInAll);
      if (isKey != null)
        prop.setKey(isKey);
      if (store != null)
        prop.setStore(store);
      try {
        prop.validate();
      }
      catch (MappingException e) {
        throw MappingProviderException.invalidTypePropetiesException(typeName, filePath, e)
      }

      List invalidAttributeNames = XmlProcessingUtils.getInvalidAttributes(xmlNode, VALID_TYPE_PROPERTY_ATTRIBUTES);
      if (!invalidAttributeNames.isEmpty()) {
        throw MappingProviderException.invalidAttributeInTypePropertyDefinitionException(typeName, propName, invalidAttributeNames, filePath);
      }
      if (defaultValueString != null) {
        try {
          Object defaultValue = MappingUtils.createDefaultValue(prop.type, defaultValueString);
          prop.setDefaultValue(defaultValue);
        }
        catch (MappingException e) {
          throw MappingProviderException.invalidDefaultValueException(typeName, propName, filePath, e);
        }
      }

      return prop;
    } catch (XmlAttributeConversionException e) {
      throw MappingProviderException.invalidXmlAttribute(e.attributeName, e.attributeValue, filePath)
    }
  }
}
