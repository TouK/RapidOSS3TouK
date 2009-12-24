import com.ifountain.rcmdb.domain.generation.ModelGenerationUtils
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import groovy.xml.MarkupBuilder
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FalseFileFilter
import org.apache.commons.io.filefilter.SuffixFileFilter
import com.ifountain.rcmdb.domain.generation.DataCorrectionUtilities

/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
/**
* Created by IntelliJ IDEA.
* User: mustafa seker
* Date: Jul 6, 2008
* Time: 11:44:46 AM
* To change this template use File | Settings | File Templates.
*/

VALID_MODEL_XML_PROPERTIES = ["Name", "Parent", "IndexName", "StorageType"]
VALID_MODEL_PROPERTY_XML_PROPERTIES = ["Name", "Type", "Default", "IsKey", "Lazy", "Datasource", "DatasourceProperty", "NameInDatasource"]
VALID_MODEL_DATASOURCE_XML_PROPERTIES = ["Definition", "Name", "NameProperty"]
VALID_MODEL_DATASOURCE_KEY_XML_PROPERTIES = ["Name", "NameInDatasource"]
VALID_MODEL_RELATION_XML_PROPERTIES = ["From", "To", "Name", "ReverseName", "Type"]
logger.info("Model creator started");
def baseDir = System.getProperty("base.dir");
confDir = new File("${baseDir}/grails-app/conf");
modelConfigFiles = FileUtils.listFiles(confDir, new SuffixFileFilter("ModelConfiguration.xml"), new FalseFileFilter())
if (modelConfigFiles.size() == 0)
{
    logger.info("No model configuration file has been found.");
    throw new Exception("No model configuration file has been found.");
}
def modelXmls = getModelXmls();
String tempDirectory = web.grailsApplication.config.toProperties()["rapidCMDB.temp.dir"];
if (tempDirectory != null)
{
    def modelDir = new File(tempDirectory);
    if (modelDir.exists() && modelDir.isDirectory())
    {
        FileUtils.deleteDirectory(modelDir);
    }
}
ModelGenerator.getInstance().generateModels(modelXmls);
def message = "Models generated successfully. Please restart the server."
try{
    DataCorrectionUtilities.dataCorrectionBeforeReloadStep(baseDir, tempDirectory)
}
catch(e){
    message =  "Could not successfully generate models. Reason: ${e.toString()}"
}

web.flash?.message = message
if(params.targetURI){
    web.redirect(uri:params.targetURI)
}
else{
   return message 
}
def getModelXmls()
{
    def relationConfiguration = [:]
    modelConfigFiles.each {File modelConfigurationFile ->
        def slurper = new XmlSlurper()
        def res = slurper.parseText(modelConfigurationFile.getText());
        def relations = res.Relations.Relation;
        relations.each {relation ->
            def fromClass = relation.@From.text()
            def toClass = relation.@To.text()
            ModelGenerationUtils.validateXmlProperties (VALID_MODEL_RELATION_XML_PROPERTIES, fromClass, relation);
            def name = relation.@Name.text()
            def reverseName = relation.@ReverseName.text()
            String type = relation.@Type.text()
            def cardinalities = type.toLowerCase().split("to", -1)
            def fromCardinality = getCardinality(cardinalities[0])
            def toCardinality = getCardinality(cardinalities[1])
            def tmpRelConfigs = []
            tmpRelConfigs += [fromClass: fromClass, name: name, reverseName: reverseName, toModel: toClass, cardinality: fromCardinality, reverseCardinality: toCardinality, isOwner: true]
            tmpRelConfigs += [fromClass: toClass, name: reverseName, reverseName: name, toModel: fromClass, cardinality: toCardinality, reverseCardinality: fromCardinality, isOwner: false]
            tmpRelConfigs.each {Map relConfig ->
                def relConfigs = relationConfiguration[relConfig.fromClass];
                if (relConfigs == null)
                {
                    relConfigs = [:]
                    relationConfiguration[relConfig.fromClass] = relConfigs;
                }
                relConfig.remove("fromClass")
                relConfigs[relConfig.name] = relConfig;
            }
        }
    }
    def modelXmlStrings = [];
    modelConfigFiles.each {File modelConfigurationFile ->
        def slurper = new XmlSlurper()
        def res = slurper.parseText(modelConfigurationFile.getText());
        def modelsXml = res.Models.Model;
        logger.info("Will create ${modelsXml.size()} number of models for file ${modelConfigurationFile}.");
        modelsXml.each {modelXml ->
            def modelString = new StringWriter();
            def modelBuilder = new MarkupBuilder(modelString);
            def modelProperties = modelXml.Properties.Property;
            def modelDatasources = modelXml.Datasources.Datasource;
            def modelName = modelXml.@Name.text();
            ModelGenerationUtils.validateXmlProperties (VALID_MODEL_XML_PROPERTIES, modelName, modelXml);
            def parentName = modelXml.@Parent.text();
            def storageType = modelXml.@StorageType.text();
            def indexName = modelXml.@IndexName.text();
            indexName = indexName == null ? "" : indexName;
            def modelMetaProps = [name: modelName, indexName: indexName];
            logger.info("Creating model ${modelName} with ${modelProperties.size()} number of properties");
            if (parentName != null && parentName != "")
            {
                logger.info("Model ${modelName} is a child of ${parentName}");
                modelMetaProps["parentModel"] = parentName;
            }

            if (storageType != null && storageType != "")
            {
                logger.info("Model ${modelName} will be written to ${storageType} storage.");
                modelMetaProps["storageType"] = storageType;
            }
            modelBuilder.Model(modelMetaProps) {
                def keys = [];
                modelBuilder.Properties() {
                    modelProperties.each {field ->
                        ModelGenerationUtils.validateXmlProperties (VALID_MODEL_PROPERTY_XML_PROPERTIES, modelMetaProps.name, field);
                        def localName = field.@Name.text();
                        def datasource = field.@Datasource.text();
                        def nameInDatasource = field.@NameInDatasource.text();
                        def datasourceProperty = field.@DatasourceProperty.text();
                        def type = field.@Type.text();
                        def defaultValue = field.@Default.text();
                        def isKey = field.@IsKey.text();
                        def isLazy = field.@Lazy.text();
                        isKey = isKey == "true";
                        isLazy = isLazy == "true";

                        def modelPropertyConfig = [name: localName, type: type, defaultValue: defaultValue, lazy: isLazy]
                        if (datasourceProperty != null && datasourceProperty != "")
                        {
                            modelPropertyConfig["datasourceProperty"] = datasourceProperty;
                        }
                        else if (datasource != null && datasource != "")
                        {
                            modelPropertyConfig["datasource"] = datasource;
                        }
                        else
                        {
                            modelPropertyConfig["datasource"] = "RCMDB";
                        }

                        if (nameInDatasource != null && nameInDatasource != "")
                        {
                            modelPropertyConfig["nameInDatasource"] = nameInDatasource;
                        }
                        else
                        {
                            modelPropertyConfig["nameInDatasource"] = localName;
                        }
                        logger.info("Creating property ${localName}");
                        modelBuilder.Property(modelPropertyConfig);
                        if (isKey)
                        {
                            keys += localName;
                        }

                    }
                }
                modelBuilder.Datasources() {
                    if (parentName == null || parentName == "")
                    {
                        modelBuilder.Datasource(name: "RCMDB")
                                {
                                    keys.each {
                                        modelBuilder.Key(propertyName: it, nameInDatasource: it);
                                    }
                                }
                    }
                    modelDatasources.each {modelDatasource ->
                        ModelGenerationUtils.validateXmlProperties (VALID_MODEL_DATASOURCE_XML_PROPERTIES, modelName, modelDatasource);
                        def dsName = modelDatasource.@Definition.text();
                        def mappedName = modelDatasource.@Name.text();
                        def mappedNameProperty = modelDatasource.@NameProperty.text();
                        def dsConf = [name:dsName]
                        if(mappedName != null && mappedName != "")
                        {
                            dsConf.mappedName = mappedName;
                        }
                        else if(mappedNameProperty != null && mappedNameProperty != "")
                        {
                            dsConf.mappedNameProperty = mappedNameProperty;    
                        }
                        def datasourceKeys = modelDatasource.Keys.Key;
                        modelBuilder.Datasource(dsConf)
                                {
                                    datasourceKeys.each {modelDatasourceKey ->
                                        ModelGenerationUtils.validateXmlProperties (VALID_MODEL_DATASOURCE_KEY_XML_PROPERTIES, modelName, modelDatasourceKey);
                                        def keyPropName = modelDatasourceKey.@Name.text();
                                        def keyPropNameInDatasource = modelDatasourceKey.@NameInDatasource.text();
                                        if (keyPropNameInDatasource == null || keyPropNameInDatasource == "")
                                        {
                                            keyPropNameInDatasource = keyPropName;
                                        }
                                        modelBuilder.Key(propertyName: keyPropName, nameInDatasource: keyPropNameInDatasource);
                                    }
                                }
                    }

                }

                modelBuilder.Relations() {
                    relationConfiguration[modelName].each {String relName, Map relationconfig ->
                        modelBuilder.Relation(relationconfig);
                    }
                }
            }
            modelXmlStrings += modelString.toString();
        }
    }
    return modelXmlStrings;
}
def getCardinality(cardinalityString)
{
    if (cardinalityString == ModelGenerator.RELATION_TYPE_ONE.toLowerCase())
    {
        return ModelGenerator.RELATION_TYPE_ONE;
    }
    else
    {
        return ModelGenerator.RELATION_TYPE_MANY;
    }
}
def getParentObjects(allModelObjects, parents, obj)
{
    if (obj.parentName != null)
    {
        def parent = allModelObjects[obj.parentName];
        parents.add(parent);
        getParentObjects(allModelObjects, parents, parent);
    }
}


