package com.ifountain.es.mapping

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 26, 2010
 * Time: 9:25:01 AM
 * To change this template use File | Settings | File Templates.
 */
class MappingProviderException extends MappingException {

  public MappingProviderException(String message) {
    super(message);
  }

  public MappingProviderException(String message, Throwable cause) {
    super(message, cause);
  }

  public static MappingException defaultValueException(String type, String defaultValueStr, Throwable t) {
    return new MappingProviderException("Invalid default value <${defaultValueStr}>. Cannot convert to <${type}>", t);
  }

  public static MappingException invalidDefaultValueException(String type, String propName, String filePath, Throwable t) {
    return new MappingProviderException("Invalid default value for property ${propName} of type ${type} in ${filePath}. Reason:${t.toString()}", t);
  }
  public static MappingException invalidXmlAttribute(String attribute, String attributeValue, String filePath) {
    return new MappingProviderException("Invalid XML property value <${attributeValue}> of XML definition attribute <${attribute}> in <${filePath}>");
  }
  public static MappingException missingMandatoryXmlProperty(List attributes, String filePath) {
    return new MappingProviderException("Missing mandatory XML property <${attributes.join(", ")}> in <${filePath}>");
  }

  public static MappingException duplicateTypeException(String type, Collection filePath){
    return new MappingProviderException("Duplicate entry for type <${type}> exist in files <${filePath.join(", ")}>")
  }

  public static MappingException invalidAttributeInTypeDefinitionException(String type, List attributeNames, String filePath)
  {
    return new MappingProviderException("Invalid attributes <${attributeNames.join(",")}> for type <${type}> exist in file <${filePath}>")
  }
  public static MappingException invalidAttributeInTypePropertyDefinitionException(String type, String propName, List attributeNames, String filePath)
  {
    return new MappingProviderException("Invalid attributes <${attributeNames.join(",")}> for TypeProperty <${propName}> in type <${type}> exist in file <${filePath}>")
  }

  public static MappingException invalidTypePropetiesException(String type, String filePath, Exception cause){
    return new MappingProviderException("Invalid properties for type <${type}> in file <${filePath}>. Reason: ${cause.toString()}", cause) 
  }
  public static MappingException cannotDeleteExistingWorkingFile(String filePath){
    return new MappingProviderException("Cannot delete existing mapping file <${filePath}>.");
  }
  public static MappingException cannotDeleteTempDir(String filePath, Exception e){
    return new MappingProviderException("Cannot delete temp dir <${filePath}>.", e);
  }
  public static MappingException cannotRestoreExistingWorkingFile(String filePath, Exception restoreReason){
    return new MappingProviderException("Cannot delete existing mapping file <${filePath}>. Restore reason: ${restoreReason.toString()}", restoreReason);
  }
  public static MappingException cannotCreateProviderWhileTempDirExist(String filePath){
    return new MappingProviderException("Cannot create provider since temp directory <${filePath}> exist in working directory. Either restore temp dir content to working copy or delete temp dir.", );
  }
  public static MappingException forbiddenPropertyNameIsUsed(String type, String propName, String filePath){
    return new MappingProviderException("Forbidden property name<${propName}> in type <${type}> in file <${filePath}>");
  }
}
