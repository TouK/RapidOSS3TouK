import groovy.xml.MarkupBuilder
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import org.apache.log4j.Logger
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: mustafa seker
* Date: Jul 6, 2008
* Time: 11:44:46 AM
* To change this template use File | Settings | File Templates.
*/
logger = Logger.getLogger(getClass().name);
logger.info ("Model creator started");
def baseDir = System.getProperty ("base.dir");
smartsModelConfigurationFile = new File("$baseDir/grails-app/conf/ModelConfiguration.xml");
if(!smartsModelConfigurationFile.exists())
{
    logger.info ("Configuration file ${smartsModelConfigurationFile.absolutePath} doesnot exist.");
    throw new Exception("Configuration file doesnot exist.");
}
def modelXmls = getModelXmls();
String tempDirectory = web.grailsApplication.config.toProperties()["rapidCMDB.temp.dir"];
if(tempDirectory != null)
{
    def modelDir = new File(tempDirectory);
    if(modelDir.exists() && modelDir.isDirectory())
    {
        FileUtils.deleteDirectory (modelDir);
    }
}
ModelGenerator.getInstance().generateModels (modelXmls);
web.flash?.message = "Models generated successfully."
def getModelXmls()
{
    def slurper = new XmlSlurper()
    def res = slurper.parseText(smartsModelConfigurationFile.getText());
    def modelsXml = res.Models.Model;
    def relations = res.Relations.Relation;

    def relationConfiguration = [:]
    relations.each{relation->
        def fromClass = relation.@From.text()
        def toClass = relation.@To.text()
        def name = relation.@Name.text()
        def reverseName = relation.@ReverseName.text()
        String type = relation.@Type.text()
        def cardinalities = type.toLowerCase().split("to", -1)
        def fromCardinality = getCardinality(cardinalities[0])
        def toCardinality = getCardinality(cardinalities[1])
        def tmpRelConfigs = []
        tmpRelConfigs += [fromClass:fromClass, name:name, reverseName:reverseName, toModel:toClass,cardinality:fromCardinality, reverseCardinality:toCardinality, isOwner:true]
        tmpRelConfigs +=  [fromClass:toClass, name:reverseName, reverseName:name, toModel:fromClass,cardinality:toCardinality, reverseCardinality:fromCardinality, isOwner:false]
        tmpRelConfigs.each{Map relConfig->
            def relConfigs = relationConfiguration[relConfig.fromClass];
            if(relConfigs == null)
            {
                relConfigs = [:]
                relationConfiguration[relConfig.fromClass] = relConfigs;
            }
            relConfig.remove("fromClass")
            relConfigs[relConfig.name] = relConfig;
        }
    }


    def modelXmlStrings = [];
    logger.info ("Will create ${modelsXml.size()} number of models.");
    modelsXml.each{modelXml->
        def modelString = new StringWriter();
        def modelBuilder = new MarkupBuilder(modelString);
        def modelProperties = modelXml.Properties.Property;
        def modelDatasources = modelXml.Datasources.Datasource;
        def modelName = modelXml.@Name.text();
        def parentName = modelXml.@Parent.text();
        def indexName = modelXml.@IndexName.text();
        indexName = indexName == null?"":indexName;
        def modelMetaProps = [name:modelName, indexName:indexName];
        logger.info ("Creating model ${modelName} with ${modelProperties.size()} number of properties");
        if(parentName != null && parentName != "")
        {
            logger.info ("Model ${modelName} is a child of ${parentName}");
            modelMetaProps["parentModel"] = parentName;
        }
        modelBuilder.Model(modelMetaProps)
        {
            def keys = [];
            modelBuilder.Properties()
            {
                modelProperties.each{field->

                    def localName = field.@Name.text();
                    def datasource = field.@Datasource.text();
                    def nameInDatasource = field.@NameInDatasource.text();
                    def datasourceProperty = field.@DatasourceProperty.text();
                    def type = field.@Type.text();
                    def defaultValue = field.@Default.text();
                    def isKey = field.@IsKey.text();
                    isKey = isKey == "true";

                    def modelPropertyConfig = [name:localName, type:type, defaultValue:defaultValue, lazy:false]
                    if(datasourceProperty != null && datasourceProperty != "")
                    {
                        modelPropertyConfig["datasourceProperty"] = datasourceProperty;
                    }
                    else if(datasource != null && datasource != "")
                    {
                        modelPropertyConfig["datasource"] = datasource;
                    }
                    else
                    {
                        modelPropertyConfig["datasource"] = "RCMDB";
                    }

                    if(nameInDatasource != null && nameInDatasource != "")
                    {
                        modelPropertyConfig["nameInDatasource"] = nameInDatasource;
                    }
                    else
                    {
                        modelPropertyConfig["nameInDatasource"] = localName;
                    }
                    logger.info ("Creating property ${localName}");
                    if(type == "number")
                    {
                        modelPropertyConfig["defaultvalue"] = "0";
                    }
                    else
                    {
                        modelPropertyConfig["defaultvalue"] = "";
                    }
                    
                    modelBuilder.Property(modelPropertyConfig);
                    if(isKey)
                    {
                        keys += localName;
                    }

                }
            }
            modelBuilder.Datasources()
            {
                if(parentName == null  || parentName == "")
                {
                    modelBuilder.Datasource(name:"RCMDB")
                    {
                        keys.each{
                            modelBuilder.Key(propertyName:it, nameInDatasource:it);
                        }
                    }
                }
                modelDatasources.each{modelDatasource->
                    def dsName = modelDatasource.@Name.text();
                    def datasourceKeys = modelDatasource.Keys.Key;
                    modelBuilder.Datasource(name:dsName)
                    {
                        datasourceKeys.each{modelDatasourceKey->
                            def keyPropName = modelDatasourceKey.@Name.text();
                            def keyPropNameInDatasource = modelDatasourceKey.@NameinDatasource.text();
                            if(keyPropNameInDatasource == null || keyPropNameInDatasource == "")
                            {
                                keyPropNameInDatasource = keyPropName;
                            }
                            modelBuilder.Key(propertyName:keyPropName, nameInDatasource:keyPropNameInDatasource);
                        }
                    }    
                }
                
            }

            modelBuilder.Relations()
            {
                relationConfiguration[modelName].each{String relName, Map relationconfig->
                    modelBuilder.Relation(relationconfig);
                }
            }
        }
        modelXmlStrings += modelString.toString();
    }
    return modelXmlStrings;
}
def getCardinality(cardinalityString)
{
    if(cardinalityString == ModelGenerator.RELATION_TYPE_ONE.toLowerCase())
    {
        return  ModelGenerator.RELATION_TYPE_ONE;
    }
    else
    {
        return ModelGenerator.RELATION_TYPE_MANY;
    }
}
def getParentObjects(allSmartsModelObjects, parents, obj)
{
    if(obj.parentName != null)
    {
        def parent = allSmartsModelObjects[obj.parentName];
        parents.add(parent);
        getParentObjects(allSmartsModelObjects, parents, parent);
    }
}


