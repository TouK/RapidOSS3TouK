package com.ifountain.es.repo

import com.ifountain.comp.config.ConfigurationProviderException

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 30, 2010
 * Time: 4:53:54 PM
 * To change this template use File | Settings | File Templates.
 */
class IndexConfigurationProviderException extends ConfigurationProviderException{

  public IndexConfigurationProviderException() {
  }

  public IndexConfigurationProviderException(String message) {
    super(message);
  }

  public IndexConfigurationProviderException(String message, Throwable cause) {
    super(message, cause);
  }

  public static  IndexConfigurationProviderException invalidXmlAttribute(String indexName, String attributeName, String attributeValue, String filePath, Exception e){
    return new IndexConfigurationProviderException("Invalid value <${attributeValue}> for XML attribute <${attributeName}> of index <${indexName}> in file <${filePath}>. Reason: ${e.toString()}", e);
  }
  public static  IndexConfigurationProviderException unexpectedXmlAttributes(String indexName, List<String> attributeNames, String filePath){
    return new IndexConfigurationProviderException("Unexpected XML attributes <${attributeNames.join(", ")}> exist in  index <${indexName}> in file <${filePath}>.");
  }
  public static  IndexConfigurationProviderException missingMandatoryProps(List<String> attributeNames, String filePath){
    return new IndexConfigurationProviderException("Missing mandatory XML attributes <${attributeNames.join(", ")}> in file <${filePath}>.");
  }
  public static  IndexConfigurationProviderException multipleIndexExistInFiles(String indexName, List<String> filePaths){
    return new IndexConfigurationProviderException("Duplicate index <${indexName}> exist in files <${filePaths.join(", ")}>");
  }
}
