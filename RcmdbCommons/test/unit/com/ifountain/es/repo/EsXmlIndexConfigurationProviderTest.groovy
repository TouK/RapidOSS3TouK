package com.ifountain.es.repo

import com.ifountain.comp.test.util.RCompTestCase
import org.apache.commons.io.FileUtils
import com.ifountain.comp.config.ConfigurationProviderException
import com.ifountain.rcmdb.config.XmlAttributeConversionException

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 30, 2010
 * Time: 4:15:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class EsXmlIndexConfigurationProviderTest extends RCompTestCase {
  String testOutputDir = "../testoutput";

  public void setUp() {
    super.setUp();
    File testOutputDirectory = new File(testOutputDir);
    FileUtils.deleteDirectory(testOutputDirectory);
    testOutputDirectory.mkdirs();
  }

  public void testGetConfigFile() {
    File typeMappingConfigFile1 = new File("${testOutputDir}/Sample1EsIndexConfiguration.xml");
    typeMappingConfigFile1.setText("");
    File typeMappingConfigFile2 = new File("${testOutputDir}/Sample2EsIndexConfiguration.xml");
    typeMappingConfigFile2.setText("");
    File typeMappingConfigFile3 = new File("${testOutputDir}/sample.xml");
    typeMappingConfigFile3.setText("");
    File typeMappingConfigFile4 = new File("${testOutputDir}/xyz/Sample2EsIndexConfiguration.xml");
    typeMappingConfigFile4.parentFile.mkdirs();
    typeMappingConfigFile4.setText("");


    EsXmlIndexConfigurationProvider provider = new EsXmlIndexConfigurationProvider("", testOutputDir);
    Collection<File> files = provider.getConfigurationFileInDir(typeMappingConfigFile2.getParentFile());
    assertEquals(2, files.size());
    assertEquals(typeMappingConfigFile1, files[0]);
    assertEquals(typeMappingConfigFile2, files[1]);
  }

  public void testLoad() {
    String xmlContent = """
      <Indexes>
          <Index Name="index1" ShardCount="2" ReplicaCount="2">
          </Index>
          <Index Name="index2" ShardCount="3" ReplicaCount="1">
          </Index>
      </Indexes>
    """
    File typeMappingConfigFile = new File("${testOutputDir}/${EsXmlIndexConfigurationProvider.CONFIG_FILE_SUFFIX}");
    typeMappingConfigFile.setText(xmlContent);

    EsXmlIndexConfigurationProvider provider = new EsXmlIndexConfigurationProvider("", testOutputDir);
    Map<String, IndexConfiguration> mappings = provider.load();

    assertEquals(2, mappings.size());
    IndexConfiguration conf = mappings.index1;
    assertEquals(2, conf.getShardCount());
    assertEquals(2, conf.getReplicaCount());

    conf = mappings.index2;
    assertEquals(3, conf.getShardCount());
    assertEquals(1, conf.getReplicaCount());
  }

  public void testLoadThrowsExceptionIfShardCountIsNotAnInteger() {
    String xmlContent = """
      <Indexes>
          <Index Name="index1" ShardCount="abc" ReplicaCount="2">
          </Index>
      </Indexes>
    """
    File typeMappingConfigFile = new File("${testOutputDir}/${EsXmlIndexConfigurationProvider.CONFIG_FILE_SUFFIX}");
    typeMappingConfigFile.setText(xmlContent);

    EsXmlIndexConfigurationProvider provider = new EsXmlIndexConfigurationProvider("", testOutputDir);
    try{
      provider.load();
      fail("Should throw exception since shardCount is not a number");
    }
    catch(ConfigurationProviderException e){
      XmlAttributeConversionException nestedEx = new XmlAttributeConversionException("ShardCount", "abc", Integer.class, null);
      IndexConfigurationProviderException expectedException = IndexConfigurationProviderException.invalidXmlAttribute("index1", "ShardCount", "abc", typeMappingConfigFile.path, nestedEx)
      assertEquals (expectedException.toString(), e.getCause().toString());
    }

  }
  public void testLoadThrowsExceptionIfReplicaCountIsNotAnInteger() {
    String xmlContent = """
      <Indexes>
          <Index Name="index1" ShardCount="2" ReplicaCount="abc">
          </Index>
      </Indexes>
    """
    File typeMappingConfigFile = new File("${testOutputDir}/${EsXmlIndexConfigurationProvider.CONFIG_FILE_SUFFIX}");
    typeMappingConfigFile.setText(xmlContent);

    EsXmlIndexConfigurationProvider provider = new EsXmlIndexConfigurationProvider("", testOutputDir);
    try{
      provider.load();
      fail("Should throw exception since ReplicaCount is not a number");
    }
    catch(ConfigurationProviderException e){
      XmlAttributeConversionException nestedEx = new XmlAttributeConversionException("ReplicaCount", "abc", Integer.class, null);
      IndexConfigurationProviderException expectedException = IndexConfigurationProviderException.invalidXmlAttribute("index1", "ReplicaCount", "abc", typeMappingConfigFile.path, nestedEx)
      assertEquals (expectedException.toString(), e.getCause().toString());
    }
  }

  public void testLoadThrowsExceptionIfExtraXmlAttributesExist() {
    String xmlContent = """
      <Indexes>
          <Index Name="index1" ShardCount="2" ReplicaCount="2" ExtraProp1="pr1" ExtraProp2="pr2">
          </Index>
      </Indexes>
    """
    File typeMappingConfigFile = new File("${testOutputDir}/${EsXmlIndexConfigurationProvider.CONFIG_FILE_SUFFIX}");
    typeMappingConfigFile.setText(xmlContent);

    EsXmlIndexConfigurationProvider provider = new EsXmlIndexConfigurationProvider("", testOutputDir);
    try{
      provider.load();
      fail("Should throw exception since unexpected attributes exist");
    }
    catch(ConfigurationProviderException e){
      IndexConfigurationProviderException expectedException = IndexConfigurationProviderException.unexpectedXmlAttributes("index1", ["ExtraProp1", "ExtraProp2"].sort(), typeMappingConfigFile.path)
      assertEquals (expectedException.toString(), e.getCause().toString());
    }
  }

  public void testLoadThrowsExceptionIfMandatoryPropsAreMissing() {
    String xmlContent = """
      <Indexes>
          <Index>
          </Index>
      </Indexes>
    """
    File typeMappingConfigFile = new File("${testOutputDir}/${EsXmlIndexConfigurationProvider.CONFIG_FILE_SUFFIX}");
    typeMappingConfigFile.setText(xmlContent);

    EsXmlIndexConfigurationProvider provider = new EsXmlIndexConfigurationProvider("", testOutputDir);
    try{
      provider.load();
      fail("Should throw exception since mandatory props are missing");
    }
    catch(ConfigurationProviderException e){
      IndexConfigurationProviderException expectedException = IndexConfigurationProviderException.missingMandatoryProps(["Name", "ShardCount", "ReplicaCount"].sort(), typeMappingConfigFile.path)
      assertEquals (expectedException.toString(), e.getCause().toString());
    }
  }

  public void testLoadThrowsExceptionIfSameIndexNamesExistInMultipleIndexEntry() {
    String xmlContent = """
      <Indexes>
          <Index Name="index1" ShardCount="3" ReplicaCount="4">
          </Index>
          <Index Name="index1" ShardCount="3" ReplicaCount="4">
          </Index>
      </Indexes>
    """
    File typeMappingConfigFile = new File("${testOutputDir}/${EsXmlIndexConfigurationProvider.CONFIG_FILE_SUFFIX}");
    typeMappingConfigFile.setText(xmlContent);

    EsXmlIndexConfigurationProvider provider = new EsXmlIndexConfigurationProvider("", testOutputDir);
    try{
      provider.load();
      fail("Should throw exception since duplicate index exist");
    }
    catch(ConfigurationProviderException e){
      IndexConfigurationProviderException expectedException = IndexConfigurationProviderException.multipleIndexExistInFiles("index1", [typeMappingConfigFile.path])
      assertEquals (expectedException.toString(), e.getCause().toString());
    }
  }

  public void testLoadThrowsExceptionIfSameIndexNamesExistInMultipleIndexEntryInDifferentConfFiles() {
    String xmlContent = """
      <Indexes>
          <Index Name="index1" ShardCount="3" ReplicaCount="4">
          </Index>
      </Indexes>
    """
    File typeMappingConfigFile1 = new File("${testOutputDir}/Sample1${EsXmlIndexConfigurationProvider.CONFIG_FILE_SUFFIX}");
    typeMappingConfigFile1.setText(xmlContent);
    File typeMappingConfigFile2 = new File("${testOutputDir}/Sample2${EsXmlIndexConfigurationProvider.CONFIG_FILE_SUFFIX}");
    typeMappingConfigFile2.setText(xmlContent);

    EsXmlIndexConfigurationProvider provider = new EsXmlIndexConfigurationProvider("", testOutputDir);
    try{
      provider.load();
      fail("Should throw exception since duplicate index exist in multiple conf files");
    }
    catch(ConfigurationProviderException e){
      IndexConfigurationProviderException expectedException = IndexConfigurationProviderException.multipleIndexExistInFiles("index1", [typeMappingConfigFile1.path, typeMappingConfigFile2.path])
      assertEquals (expectedException.toString(), e.getCause().toString());
    }
  }
}
