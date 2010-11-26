package com.ifountain.es.mapping

import groovy.util.slurpersupport.GPathResult
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.SuffixFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 25, 2010
 * Time: 2:20:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class EsXmlMappingProvider implements EsMappingProvider {
  public static Map<String, String> VALID_TYPE_ATTRIBUTES = [Name:"", Index:"", AllEnabled:""];
  public static Map<String, String> VALID_TYPE_PROPERTY_ATTRIBUTES = [Name:"", IsKey:"", Type:"", DefaultValue:"", Store:"", IncludeInAll:"", Analyzer:""];
  File baseDir;

  public EsXmlMappingProvider(String configurationBaseDirPath) {
    baseDir = new File(configurationBaseDirPath);
  }


  void reload() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  private Collection<File> getConfigFileList() {
    return FileUtils.listFiles(baseDir, new SuffixFileFilter(["EsTypeConfiguration.xml"] as String[]), new TrueFileFilter());
  }

  public Map<String, TypeMapping> constructMappings() {
    Collection<File> confFileList = getConfigFileList();
    Map<String, TypeMapping> allMappings = new HashMap<String, TypeMapping>();    
    Map<String, List> typeFileLocations = new HashMap<String, List>();
    confFileList.each {File confFile ->
      String typeText = confFile.getText();
      List<TypeMapping> mappings = convertXmlToTypeMappings(typeText, confFile.path);
      mappings.each{TypeMapping mapping->
        if(!typeFileLocations.containsKey(mapping.getName())){
          typeFileLocations[mapping.name] = [];  
        }
        allMappings.put(mapping.getName(), mapping);
        typeFileLocations[mapping.name].add(confFile.getPath());
      }
    }
    typeFileLocations.each{String type, List paths->
        if(paths.size() > 1){
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
    String typeName = MappingUtils.getAttributeAs(xmlNode, "Name", String);
    String indexName = MappingUtils.getAttributeAs(xmlNode, "Index", String);
    boolean allEnabled = MappingUtils.getAttributeAs(xmlNode, "AllEnabled", Boolean);

    TypeMapping mapping = new TypeMapping(typeName, indexName);
    try{
      mapping.validate();
    }
    catch(MappingException e){
      throw MappingProviderException.invalidTypePropetiesException(typeName, filePath, e)  
    }


    List invalidAttributeNames = getInvalidAttributes(xmlNode, VALID_TYPE_ATTRIBUTES);
    if(!invalidAttributeNames.isEmpty()){
      throw MappingProviderException.invalidAttributeInTypeDefinitionException(typeName, invalidAttributeNames, filePath);
    }

    xmlNode.Properties.Property.each{propNode->
      TypeProperty prop = convertXmlNodeToTypeProperty(typeName, propNode, filePath);
      mapping.addProperty (prop);
    }
    mapping.setAllEnabled (allEnabled);
    return mapping;
  }

  private TypeProperty convertXmlNodeToTypeProperty(String typeName, xmlNode, filePath) {
    String propName = MappingUtils.getAttributeAs(xmlNode, "Name", String);
    String propType = MappingUtils.getAttributeAs(xmlNode, "Type", String);
    String defaultValueString = MappingUtils.getAttributeAs(xmlNode, "DefaultValue", String);
    String analyzer = MappingUtils.getAttributeAs(xmlNode, "Analyzer", String);
    boolean includedInAll = MappingUtils.getAttributeAs(xmlNode, "IncludeInAll", Boolean);
    boolean isKey = MappingUtils.getAttributeAs(xmlNode, "IsKey", Boolean);
    boolean store = MappingUtils.getAttributeAs(xmlNode, "Store", Boolean);

    TypeProperty prop = new TypeProperty(propName, propType);
    prop.setAnalyzer (analyzer);
    prop.setIncludeInAll (includedInAll);
    prop.setKey (isKey);
    prop.setStore (store);
    try{
      prop.validate();
    }
    catch(MappingException e){
      throw MappingProviderException.invalidTypePropetiesException(typeName, filePath, e)
    }

    List invalidAttributeNames = getInvalidAttributes(xmlNode, VALID_TYPE_PROPERTY_ATTRIBUTES);
    if(!invalidAttributeNames.isEmpty()){
      throw MappingProviderException.invalidAttributeInTypePropertyDefinitionException(typeName, propName, invalidAttributeNames, filePath);
    }
    try{
      Object defaultValue = MappingUtils.createDefaultValue(prop.type, defaultValueString);
      prop.setDefaultValue (defaultValue);
    }
    catch(MappingException e){
      throw MappingProviderException.invalidDefaultValueException(typeName, propName, filePath, e);
    }



    

    return prop;
  }

  private List getInvalidAttributes(GPathResult xmlNode, Map<String,String> validAttributeNames){
    List invalidAttributeList = new ArrayList();
    xmlNode.attributes().each{name, value->
      if(!validAttributeNames.containsKey(name)){
        invalidAttributeList.add(name);
      }
    }
    return invalidAttributeList.sort();
  }

}
