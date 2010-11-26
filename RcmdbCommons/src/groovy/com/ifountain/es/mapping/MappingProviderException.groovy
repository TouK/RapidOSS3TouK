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
}
