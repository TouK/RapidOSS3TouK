package com.ifountain.es.repo

import com.ifountain.comp.config.ConfigurationProvider
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.SuffixFileFilter
import org.apache.commons.io.filefilter.FalseFileFilter
import groovy.util.slurpersupport.GPathResult
import com.ifountain.es.mapping.MappingUtils
import com.ifountain.rcmdb.config.XmlProcessingUtils
import com.ifountain.rcmdb.config.XmlAttributeConversionException

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 30, 2010
 * Time: 3:54:45 PM
 * To change this template use File | Settings | File Templates.
 */
class EsXmlIndexConfigurationProvider extends ConfigurationProvider<IndexConfiguration> {
  public final static String WORKING_DIR_TEMP = "temp"
  public final static String CONFIG_FILE_SUFFIX = "EsIndexConfiguration.xml"
  public final static Map<String, Boolean> VALID_XML_ATTRIBUTES = [Name: true, ShardCount: true, ReplicaCount: true];

  public EsXmlIndexConfigurationProvider(baseDirPath, workDirPath) {
    super(baseDirPath, workDirPath, WORKING_DIR_TEMP);
  }

  public Collection<File> getConfigurationFileInDir(File dir) {
    return FileUtils.listFiles(dir, new SuffixFileFilter([CONFIG_FILE_SUFFIX] as String[]), FalseFileFilter.FALSE).sort() {it.name};
  }

  public Map<String, IndexConfiguration> constructBeans(Collection<File> configurationFileList) {
    Map<String, IndexConfiguration> indexConfs = new HashMap<String, IndexConfiguration>();
    Map<String, List> indexConfigurationFiles = new HashMap<String, List>()
    configurationFileList.each {File confFile ->
      GPathResult indexNodes = new XmlSlurper().parseText(confFile.getText()).Index;
      indexNodes.each {GPathResult indexNode ->
        IndexConfiguration conf = createConfigurationFromXmlNode(indexNode, confFile.getPath());
        if (indexConfigurationFiles[conf.name] == null) {
          indexConfigurationFiles[conf.name] = [];
        }
        indexConfigurationFiles[conf.name].add(confFile);
        indexConfs[conf.name] = conf;
      }
    }
    indexConfigurationFiles.each {String indexName, List confFiles ->
      if (confFiles.size() > 1) {
        throw IndexConfigurationProviderException.multipleIndexExistInFiles(indexName, confFiles.path.unique());
      }

    }
    return indexConfs;  //To change body of implemented methods use File | Settings | File Templates.
  }

  private IndexConfiguration createConfigurationFromXmlNode(GPathResult xmlNode, String filePath) {
    List<String> missingMandatoryProps = XmlProcessingUtils.getMissingMandatoryAttributes(xmlNode, VALID_XML_ATTRIBUTES);
    if (!missingMandatoryProps.isEmpty()) {
      throw IndexConfigurationProviderException.missingMandatoryProps(missingMandatoryProps, filePath);
    }
    String name = XmlProcessingUtils.getAttributeAs(xmlNode, "Name", String.class)
    List<String> invalidXmlAttributes = XmlProcessingUtils.getInvalidAttributes(xmlNode, VALID_XML_ATTRIBUTES)
    if (!invalidXmlAttributes.isEmpty()) {
      throw IndexConfigurationProviderException.unexpectedXmlAttributes(name, invalidXmlAttributes, filePath);
    }
    try {
      int shardCount = XmlProcessingUtils.getAttributeAs(xmlNode, "ShardCount", Integer.class)
      int replicaCount = XmlProcessingUtils.getAttributeAs(xmlNode, "ReplicaCount", Integer.class)
      IndexConfiguration conf = new IndexConfiguration(name)
      conf.setReplicaCount(replicaCount)
      conf.setShardCount(shardCount)
      return conf
    } catch (XmlAttributeConversionException ex) {
      throw IndexConfigurationProviderException.invalidXmlAttribute(name, ex.getAttributeName(), ex.getAttributeValue(), filePath, ex);
    }

  }
}
