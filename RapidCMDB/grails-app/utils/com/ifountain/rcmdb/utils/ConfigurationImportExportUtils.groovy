package com.ifountain.rcmdb.utils

import connection.Connection
import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import org.apache.log4j.Logger
import datasource.BaseDatasource
import script.CmdbScript
import model.Model
import groovy.util.slurpersupport.GPathResult
import org.apache.commons.io.FileUtils
import model.ModelDatasource
import model.ModelDatasourceKeyMapping
import model.ModelProperty
import model.ModelRelation
import groovy.util.slurpersupport.NodeChild
import groovy.util.slurpersupport.NodeChildren;
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: May 8, 2008
 * Time: 3:58:08 PM
 * To change this template use File | Settings | File Templates.
 */
class ConfigurationImportExportUtils { 
    String basedir;
    SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
    Map templates = [:];
    def logger;
    public ConfigurationImportExportUtils(String baseDir) {
        this(baseDir, Logger.getLogger(ConfigurationImportExportUtils.class));
    }
    public ConfigurationImportExportUtils(String baseDir,  logger) {
        this.basedir = baseDir;
        this.logger = logger;
    }

    private Template getTemplate(String templatePath)
    {
        Template foundTemplate = templates.get(templatePath);
        if(!foundTemplate)
        {
            File templateFile = new File("${basedir}/${templatePath}")
            if(templateFile.exists())
            {
                def inputStr = templateFile.newInputStream();
                foundTemplate = templateEngine.createTemplate(new InputStreamReader(inputStr))
                templates[templatePath] = foundTemplate;
                inputStr.close();
            }
            else
            {
                logger.info ("Could not find template ${templateFile.path}");
            }
            
        }
        return foundTemplate;

    }
    public void export(String exportDir, List configurationObjects)
    {
        new File(exportDir).mkdirs();
        new File(exportDir+"/models").mkdirs();
        def connectionXmls = [];
        def datasourceXmls = [];
        def scriptXmls = [];
        Template connectionsTemplate = getTemplate("connection/Connections.xml")
        Template datasourcesTemplate = getTemplate("datasource/Datasources.xml")
        Template scriptsTemplate = getTemplate("script/Scripts.xml")
        Template modelTemplate = getTemplate("model/Model.xml")
        configurationObjects.each {configurationObject->
            def templatePath = "${configurationObject.class.name.replaceAll ("\\.", "/")}.xml"
            Template template = getTemplate(templatePath);
            if(template)
            {
                if(configurationObject instanceof Connection)
                {
                    connectionXmls += template.make ([object:configurationObject]).toString();
                }
                else if(configurationObject instanceof BaseDatasource)
                {
                    datasourceXmls += template.make ([object:configurationObject]).toString();
                }
                else if(configurationObject instanceof CmdbScript)
                {
                    scriptXmls += template.make ([object:configurationObject]).toString();
                }
                else if(configurationObject instanceof Model)
                {
                    File modelFile = new File("${exportDir}/models/${configurationObject.name}.xml");
                    modelFile.setText(modelTemplate.make([model:configurationObject]).toString());
                }
            }
        }

        File connectionsFile = new File("${exportDir}/connections.xml");
        File datasourcesFile = new File("${exportDir}/datasources.xml");
        File scriptsFile = new File("${exportDir}/scripts.xml");


        connectionsFile.setText(connectionsTemplate.make([connectionXmls:connectionXmls]).toString());
        datasourcesFile.setText(datasourcesTemplate.make([datasourceXmls:datasourceXmls]).toString());
        scriptsFile.setText(scriptsTemplate.make([scriptXmls:scriptXmls]).toString());
    }

    public void importConfiguration(String importDir)
    {
        ModelProperty.list().each{ModelProperty property->
            property.propertyDatasource = null;
            property.propertySpecifyingDatasource = null;
            property.save(flush:true);
        }
        Model.list().each
        {
            it.parentModel = null;
            it.save(flush:true);
            it.refresh();
        }
        Model.list()*.delete(flush:true);
        CmdbScript.list()*.delete(flush:true);
        BaseDatasource.list()*.delete(flush:true);
        Connection.list()*.delete(flush:true);
        importConnections(importDir);
        importDatasources(importDir);
        importScripts(importDir);
        def datasources = [:]
        BaseDatasource.list().each
        {
            datasources[it.name] = it;
        }
        importModels(importDir, datasources);
    }

    private void importConnections(String importDir)
    {
        def xml = getXmlContent("${importDir}/connections.xml");
        if(xml)
        {
            xml.childNodes().each{connection->
                def connectionClass = this.class.classLoader.loadClass ("connection.${connection.name()}");
                def object = connectionClass.newInstance ();
                connection.attributes().each{attributeName, attributeValue->
                    object.setProperty(attributeName, attributeValue);
                }
                object.save(flush:true);
            }
        }
    }
    private void importDatasources(String importDir)
    {
        def xml = getXmlContent("${importDir}/datasources.xml");
        if(xml)
        {
            xml.childNodes().each{datasource->
                def datasourceClass = this.class.classLoader.loadClass ("datasource.${datasource.name()}");
                def object = datasourceClass.newInstance ();
                object.name = datasource.attributes()["name"];

                def connectionName = datasource.attributes()["connection"];
                if(connectionName)
                {
                    def connection = Connection.findByName(connectionName);
                    object.connection = connection;
                }
                object.save(flush:true);
            }
        }
    }
    private void importScripts(String importDir)
    {
        def xml = getXmlContent("${importDir}/scripts.xml");
        if(xml)
        {
            xml.childNodes().each{script->
                def object = new CmdbScript();
                script.attributes().each{attributeName, attributeValue->
                    object.setProperty(attributeName, attributeValue);
                }
                object.save(flush:true);
            }
        }
    }

    private void importModels(String importDir, datasources)
    {
        File modelDir = new File("${importDir}/models");
        def listofModelFiles = FileUtils.listFiles (modelDir, ["xml"] as String[], true)
        def models = [:]
        listofModelFiles.each{modelFile->
            def xml = getXmlContent(modelFile.path);
            if(xml)
            {
                models[xml.attributes() ["name"]] = xml;
            }
        }
        def generatedModels = [:]
        models.each{modelName, modelXml->
            importModel(modelXml, models, generatedModels, datasources);
        }
        models.each{modelName, modelXml->
            importModelRelations(modelXml, generatedModels);
        }
    }

    private void importModelRelations(modelXml, generatedModels)
    {
        def modelName = modelXml.attributes()["name"];
        def model = generatedModels[modelName].model;
        modelXml.Relations.childNodes().each{relationXml->

            def toModel = generatedModels[relationXml.attributes()["toModel"]].model;
            def name = relationXml.attributes()["name"];
            def reverseName = relationXml.attributes()["reverseName"];
            def cardinality = relationXml.attributes()["cardinality"];
            def reverseCardinality = relationXml.attributes()["reverseCardinality"];
            def relation = new ModelRelation(firstName:name, secondName:reverseName, firstCardinality:cardinality, secondCardinality:reverseCardinality, firstModel:model, secondModel:toModel).save(flush:true);
        }
    }

    private void importModel(modelXml, modelXmls, generatedModels, datasources)
    {
        def modelName = modelXml.attributes()["name"];
        if(generatedModels.containsKey(modelName))
        {
            return;
        }
        def generatedModelMetaData = new GeneratedModelData();
        
        def modelDatasources = modelXml.Datasources.childNodes()
        def modelProperties = modelXml.Properties.childNodes()


        def parentModelName = modelXml.attributes()["parentModel"];
        def parentModel = null;
        if(parentModelName && parentModelName.trim() != "")
        {
            if(!generatedModels.containsKey(parentModelName))
            {
                importModel (modelXmls[parentModelName], modelXmls, generatedModels, datasources);
            }
            generatedModelMetaData.parentModelData = generatedModels[parentModelName];
            parentModel =  generatedModelMetaData.parentModelData.model;
        }

        generatedModelMetaData.model = new Model(name:modelName, parentModel:parentModel).save(flush:true);
        modelDatasources.each{datasourceXml->
            def name = datasourceXml.attributes()["name"]
            def isMaster = datasourceXml.attributes()["master"].toBoolean();

            def realDatasource = datasources[name]; 
            def modelDatasourceInstance = new ModelDatasource(datasource:realDatasource, master:isMaster, model:generatedModelMetaData.model).save(flush:true);
            generatedModelMetaData.modelDatasources[name] = modelDatasourceInstance;
        }
        modelProperties.each{propertyXml->
            def propertyDatasource = null;
            def propertyDatasourceName = propertyXml.attributes()["propertyDatasource"];
            if(propertyDatasourceName && propertyDatasourceName != "")
            {
                propertyDatasource = generatedModelMetaData.getModelDatasource(propertyDatasourceName);
            }
            def isBlank = propertyXml.attributes()["blank"].toBoolean();
            def isLazy = propertyXml.attributes()["lazy"].toBoolean();
            def defaultValue = propertyXml.attributes()["defaultValue"]; 
            def name = propertyXml.attributes()["name"]; 
            def type = propertyXml.attributes()["type"];
            def nameInDatasource = propertyXml.attributes()["nameInDatasource"];
            generatedModelMetaData.modelProperties[name] = new ModelProperty(name:name, type:type, lazy:isLazy, blank:isBlank,
                    defaultValue:defaultValue, propertyDatasource:propertyDatasource, nameInDatasource:nameInDatasource, model:generatedModelMetaData.model).save(flush:true);
        }
        modelProperties = modelXml.Properties.childNodes()
        modelProperties.each{propertyXml->
            def propertySpecifyingDatasource =  propertyXml.attributes()["propertySpecifyingDatasource"];
            def name = propertyXml.attributes()["name"];
            if(propertySpecifyingDatasource && propertySpecifyingDatasource != "")
            {
                def modelProperty = generatedModelMetaData.modelProperties[name];
                def modelPropertySpecifyingDatasource = generatedModelMetaData.getModelProperty(propertySpecifyingDatasource);
                modelProperty.propertySpecifyingDatasource = modelPropertySpecifyingDatasource;
                modelProperty = modelProperty.save(flush:true);
            }
        }
        modelDatasources = modelXml.Datasources.childNodes()
        modelDatasources.each{datasourceXml->
            datasourceXml.childNodes().next().childNodes().each{keyXml->
                def keyPropName = keyXml.attributes()["name"];
                def datasourceName = datasourceXml.attributes()["name"];
                def nameInDatasource = keyXml.attributes()["nameInDatasource"];
                def property = generatedModelMetaData.getModelProperty(keyPropName);
                def datasource = generatedModelMetaData.getModelDatasource(datasourceName);
                def key = new ModelDatasourceKeyMapping(property:property, datasource:datasource, nameInDatasource:nameInDatasource).save(flush:true);
            }
        }

        generatedModels[modelName]  = generatedModelMetaData;
    }

    private GPathResult getXmlContent(String path)
    {
        File file = new File(path);
        if(file.exists())
        {
            return new XmlSlurper().parseText(file.getText());
        }
        return null;
    }
}

class GeneratedModelData
{
    Model model;
    GeneratedModelData parentModelData;
    def modelProperties=[:]
    def modelDatasources=[:]

    def getModelProperty(propertyName)
    {
        def modelProperty = modelProperties[propertyName];
        if(!modelProperty && parentModelData)
        {
            return parentModelData.getModelProperty(propertyName)
        }
        return modelProperty;
    }

    def getModelDatasource(datasourceName)
    {
        def modelDatasource = modelDatasources[datasourceName];
        if(!modelDatasource && parentModelData)
        {
            return parentModelData.getModelDatasource(datasourceName)
        }
        return modelDatasource;
    }
}