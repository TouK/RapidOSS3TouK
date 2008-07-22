package com.ifountain.rcmdb.domain.generation

import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import groovy.text.SimpleTemplateEngine
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import groovy.util.slurpersupport.GPathResult

class ModelGenerator 
{
    public static final String NUMBER_TYPE = "number"
    public static final String FLOAT_TYPE = "float"
    public static final String STRING_TYPE = "string"
    public static final String DATE_TYPE = "date"
    public static final String RELATION_TYPE_ONE = "One"
    public static final String RELATION_TYPE_MANY = "Many"
    public static final String MODEL_FILE_DIR = "grails-app/domain";
    public static final String TEMPLATES_FILE_DIR = "grails-app/templates/groovy";
    public static final List DEFAULT_IMPORTS = ["com.ifountain.core.domain.annotations.*"];
    private static ModelGenerator generator;
    File wholeClasstemplateFile
    File autoGeneratedCodeTemplateFile;

    def engine;
    def wholeClassTemplate;
    def autoGeneratedCodeTemplate;
    def tempBaseDir;
    def workingBaseDir;
    File workingModelDir;
    File tempModelDir;
    private ModelGenerator()
    {
    }

    public static ModelGenerator getInstance()
    {
        if(generator == null)
        {
            generator = new ModelGenerator();
        }
        return generator;
    }

    def initialize(String workingBaseDir, String tempBaseDir, String templateDir)
    {
        wholeClasstemplateFile = new File("$templateDir/${TEMPLATES_FILE_DIR}/DomainClassTemplate.txt".toString());
        autoGeneratedCodeTemplateFile = new File("$templateDir/${TEMPLATES_FILE_DIR}/AutoGeneratedCodeTemplate.txt".toString());
        engine = new SimpleTemplateEngine();
        wholeClassTemplate = engine.createTemplate(wholeClasstemplateFile);
        autoGeneratedCodeTemplate = engine.createTemplate(autoGeneratedCodeTemplateFile);
        this.tempBaseDir = tempBaseDir;
        this.workingBaseDir = workingBaseDir;
        workingModelDir = new File(workingBaseDir+"/"+ MODEL_FILE_DIR);
        tempModelDir = new File(tempBaseDir+"/"+ MODEL_FILE_DIR);
    }

    def generateModels(Collection modelXmls)
    {
        def modelMetaDatas = [:];
        modelXmls.each{
            def metaData = new ModelMetaData(it);
            modelMetaDatas[metaData.modelName] = metaData;
        }
        validateModels(modelMetaDatas);
        createModelFiles(modelMetaDatas);
    }

    public File getGeneratedModelFile(String name)
    {
        return new File(tempModelDir.path + "/" + name + ".groovy");
    }
    public File getCurrentModelFile(String name)
    {
        return new File(workingModelDir.path + "/" + name + ".groovy");
    }

    def generateSingleModelFileWithoutValidation(String modelXml)
    {
        def modelMetaDatas = [:];
        def modelMetaData = new ModelMetaData(modelXml);
        modelMetaDatas[modelMetaData.modelName] = modelMetaData;
        createModelFiles(modelMetaDatas);
    }

    def validateModels(modelMetaDatas)
    {
        modelMetaDatas.each{String modelName,ModelMetaData modelMetaData->
            if(!modelMetaData.masterDatasource && !modelMetaData.parentModelName)
            {
                throw ModelGenerationException.masterDatasourceDoesnotExists(modelName);
            }

            modelMetaData.datasourceConfiguration.each{dsName,dsConf->
                if(dsConf.keys.size() == 0)
                {
                    throw ModelGenerationException.noKeySpecifiedForDatasource(dsName, modelName);
                }
            }
        }
    }

    def createModelFiles(modelMetaDatas)
    {
        workingModelDir.mkdirs();
        tempModelDir.mkdirs();

        
        modelMetaDatas.each {modelName,ModelMetaData modelMetaData->
            def bindings = ["model":modelMetaData];
            def autoGeneratedCode = autoGeneratedCodeTemplate.make(bindings).toString();
            def modelFileToBeGenerated =  getGeneratedModelFile(modelMetaData.modelName);

            def currentModelFile =  new File(workingModelDir.path + "/" + modelMetaData.modelName + ".groovy");
            if(!currentModelFile.exists())
            {
                bindings["DEFAULT_IMPORTS"] = DEFAULT_IMPORTS;
                bindings["AUTO_GENERATED_CODE"] = autoGeneratedCode;
               modelFileToBeGenerated.withWriter { w ->
                    def x = wholeClassTemplate.make(bindings);
                    x.writeTo(w);
                }

            }
            else
            {
                def modelText = ModelGenerationUtils.generateClassText(currentModelFile, modelMetaData.modelName, modelMetaData.parentModelName, autoGeneratedCode, DEFAULT_IMPORTS);
                modelFileToBeGenerated.setText(modelText);
            }
        }
    }


}

class ModelMetaData
{
    def modelName;
    def parentModelName;
    def datasourceConfiguration = [:];
    def masterDatasource = null;
    def hasMany = [:];
    def belongsTo = [];
    def mappedBy = [:];
    def propertyConfigurations = [:];
    def transientProps = [];
    def constraints = [:];
    def propertyList = [];
    def numberOfDatasources
    def ModelMetaData(String modelXml)
    {
        def xmlModel = new XmlSlurper().parseText(modelXml);
        modelName = xmlModel.@name.text()
        parentModelName = xmlModel.@parentModel == ""?null:xmlModel.@parentModel.text()
        createDatasourceConfiguration (xmlModel);
        processProperties(xmlModel);
        processRelations(xmlModel);
    }

    def createDatasourceConfiguration(GPathResult model)
    {
        model.Datasources.Datasource.each{GPathResult datasource->
            def dsConf = [:];
            def dsName = datasource.@name.text();
            def keys = [:];
            datasource.Key.each{GPathResult keyMapping->
                keys[keyMapping.@propertyName.text()] = ["nameInDs":keyMapping.@nameInDatasource != ""?keyMapping.@nameInDatasource.text():keyMapping.@propertyName.text()];
            }
            dsConf["keys"] =  keys;
            if(dsName == "RCMDB")
            {
                masterDatasource = dsConf;
                if(keys.isEmpty())
                {
                    keys["id"] = ["nameInDs":"id"];
                }
            }
            datasourceConfiguration[dsName] = dsConf;
        }
    }

    def processProperties(GPathResult model)
    {
        def masterKeyPropName = null;
        model.Properties.Property.each{GPathResult property->
            def generalPropConfig = [:];
            def propertyName = property.@name.text();
            generalPropConfig["type"] =getRealType(property.@type.text());
            generalPropConfig["name"] = propertyName;
            propertyList += generalPropConfig;
            constraints[propertyName] = [:];
            generalPropConfig["defaultValue"] = getDefaultValue(property);
            if(property.@datasource != "RCMDB" && property.@datasource != "" || property.@datasourceProperty?.text() != "")
            {
                transientProps += propertyName;
                def federatedPropertyConfiguration = [:];
                federatedPropertyConfiguration["nameInDs"] =  property.@nameInDatasource != ""?property.@nameInDatasource.text():propertyName;
                if(property.@datasource != "")
                {
                    federatedPropertyConfiguration["datasource"] =  property.@datasource.text();
                }
                else
                {
                    federatedPropertyConfiguration["datasourceProperty"] =  property.@datasourceProperty.text();
                }
                federatedPropertyConfiguration["lazy"] = new Boolean(property.@lazy.text()).booleanValue();
                propertyConfigurations[propertyName] = federatedPropertyConfiguration;
                constraints[propertyName][ConstrainedProperty.BLANK_CONSTRAINT] = true;
                constraints[propertyName][ConstrainedProperty.NULLABLE_CONSTRAINT] = true;
            }
            else
            {
                if(!masterDatasource || !masterDatasource.keys.containsKey(propertyName))
                {
                    constraints[propertyName][ConstrainedProperty.BLANK_CONSTRAINT] = true;
                    constraints[propertyName][ConstrainedProperty.NULLABLE_CONSTRAINT] = true;
                }
                else
                {
                    constraints[propertyName][ConstrainedProperty.BLANK_CONSTRAINT] = false;
                    constraints[propertyName][ConstrainedProperty.NULLABLE_CONSTRAINT] = false;
                    masterKeyPropName = propertyName;
                }
            }
        }
        if(masterKeyPropName)
        {
            def uniqueKeys = [];
            masterDatasource.keys.each{key,value->
                if(key != masterKeyPropName)
                {
                    uniqueKeys += key;
                }
            }
            constraints[masterKeyPropName][KeyConstraint.KEY_CONSTRAINT] = uniqueKeys;
        }
    }

    private def processRelation(cardinality, oppositeCardinality, name, oppositeName, oppositeType, isOwner)
    {
        if(cardinality == ModelGenerator.RELATION_TYPE_ONE && oppositeCardinality == ModelGenerator.RELATION_TYPE_MANY)
        {
            hasMany[name] = oppositeType;
        }
        else if(cardinality == ModelGenerator.RELATION_TYPE_MANY && oppositeCardinality == ModelGenerator.RELATION_TYPE_ONE || cardinality == ModelGenerator.RELATION_TYPE_ONE && oppositeCardinality == ModelGenerator.RELATION_TYPE_ONE)
        {
            constraints[name] = [nullable:true];
            def generalPropConfig = [:];
            generalPropConfig["type"] = oppositeType;
            generalPropConfig["name"] = name;
            generalPropConfig["defaultValue"] = null;
            propertyList += generalPropConfig;
        }
        else if(cardinality == ModelGenerator.RELATION_TYPE_MANY && oppositeCardinality == ModelGenerator.RELATION_TYPE_MANY)
        {
            if(!isOwner)
            {
                if(!belongsTo.contains(oppositeType))
                {
                    belongsTo += oppositeType;
                }
            }
            hasMany[name] = oppositeType;
        }
        mappedBy[name] = oppositeName;
    }
    
    def processRelations(xmlModel)
    {
        xmlModel.Relations.Relation.each{GPathResult relation->
            processRelation(relation.@cardinality.text(),relation.@reverseCardinality.text(),relation.@name.text(),relation.@reverseName.text(), relation.@toModel.text(), relation.@isOwner.text() == "true");
        }
    }

    def getRealType(String type)
    {
        if(type == ModelGenerator.STRING_TYPE)
        {
            return String.name
        }
        else if(type== ModelGenerator.NUMBER_TYPE)
        {
            return Long.name;
        }
        else if(type== ModelGenerator.FLOAT_TYPE)
        {
            return Double.name;
        }
        else if(type == ModelGenerator.DATE_TYPE)
        {
            return Date.name;
        }
        else
        {
            return Object.name;
        }
    }

    def getDefaultValue(GPathResult property)
    {
        def defaultValue = property.@defaultValue.text() == ""?null:property.@defaultValue.text();
        def type = property.@type.text();
        if(type == "string")
        {
            return defaultValue?"\"${defaultValue}\"":"\"\"";
        }
        else if(type== "number" || type == "float")
        {
            return defaultValue?"$defaultValue":"0";
        }
        else if(type == "date")
        {
            DateConverter converter = RapidConvertUtils.getInstance().lookup (Date.class);
            Date date = defaultValue?converter.formater.parse (defaultValue):new Date(0);
            return "new Date(${date.getTime()})";
        }
        else
        {
            return "\"$defaultValue\"";
        }
    }
}