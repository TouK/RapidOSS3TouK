package com.ifountain.rcmdb.domain.generation

import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import groovy.text.SimpleTemplateEngine
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import groovy.util.slurpersupport.GPathResult
import org.springframework.validation.Errors
import groovy.text.Template
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import com.ifountain.compass.CompositeDirectoryWrapperProvider

class ModelGenerator 
{
    public static final String VALID_DIR_TYPES = [CompositeDirectoryWrapperProvider.FILE_DIR_TYPE, CompositeDirectoryWrapperProvider.RAM_DIR_TYPE, CompositeDirectoryWrapperProvider.MIRRORED_DIR_TYPE]
    public static final List VALID_PROPERTY_TYPE_CLASSES = [String, Double, Date, Boolean, Long]
    private static final String validModelNameExpression = "[A-Z][a-z_][A-Za-z_0-9]*"
    private static final String validPropertyNameExpression = "[a-z_][a-z_][A-Za-z_0-9]*"
    public static final String NUMBER_TYPE = "number"
    public static final String FLOAT_TYPE = "float"
    public static final String STRING_TYPE = "string"
    public static final String DATE_TYPE = "date"
    public static final String BOOLEAN_TYPE = "boolean"
    public static final String RELATION_TYPE_ONE = "One"
    public static final String RELATION_TYPE_MANY = "Many"
    public static final String MODEL_FILE_DIR = "grails-app/domain";
    public static final String TEMPLATES_FILE_DIR = "grails-app/templates/groovy";
    public static final List DEFAULT_IMPORTS = ["com.ifountain.core.domain.annotations.*"];
    private static ModelGenerator generator;
    File wholeClasstemplateFile
    File autoGeneratedCodeTemplateFile;

    List invalidNames = [];

    def engine;
    Template wholeClassTemplate;
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

    def getModelText(String modelXml)
    {
        def modelMetaData = new ModelMetaData(modelXml);
        return getModelText(modelMetaData);
    }

    def validateModels(modelMetaDatas)
    {
        modelMetaDatas.each{String modelName,ModelMetaData modelMetaData->
            if(invalidNames.contains(modelName) || !modelName.matches(validModelNameExpression))
            {
                throw ModelGenerationException.invalidModelName(modelName);
            }

            if(modelMetaData.storageType != null && !VALID_DIR_TYPES.contains(modelMetaData.storageType) )
            {
                throw ModelGenerationException.invalidStorageType(modelName, modelMetaData.storageType);
            }
            if(!modelMetaData.masterDatasource && !modelMetaData.parentModelName)
            {
                throw ModelGenerationException.masterDatasourceDoesnotExists(modelName);
            }

            if(modelMetaData.parentModelName != null && modelMetaDatas[modelMetaData.parentModelName] == null)
            {
                throw ModelGenerationException.undefinedParentModel(modelName, modelMetaData.parentModelName);
            }

            modelMetaData.relations.each{relname, relationConf->
                if(modelMetaDatas[relationConf.type] == null)
                {
                    throw ModelGenerationException.undefinedRelatedModel(modelName, relname, relationConf.type);
                }
                if(invalidNames.contains(relname) || !relname.matches(validPropertyNameExpression))
                {
                    throw ModelGenerationException.invalidModelRelationName(modelName, relname);
                }
                def parentModelName = modelMetaData.parentModelName;
                while(parentModelName != null && parentModelName != "")
                {
                    def parentModelMetaData = modelMetaDatas[parentModelName];
                    if(parentModelMetaData.relations[relname] != null)
                    {
                        throw ModelGenerationException.duplicateRelation(modelName, relname)
                    }
                    parentModelName = parentModelMetaData.parentModelName;
                }
            }

            modelMetaData.propertyList.each{propConf->
                if(invalidNames.contains(propConf.name) || !propConf.name.matches(validPropertyNameExpression))
                {
                    throw ModelGenerationException.invalidModelPropertyName(modelName, propConf.name);
                }
                def parentModelName = modelMetaData.parentModelName;
                while(parentModelName != null && parentModelName != "")
                {
                    def parentModelMetaData = modelMetaDatas[parentModelName];
                    if(parentModelMetaData.propertyMap.containsKey(propConf.name))
                    {
                        throw ModelGenerationException.duplicateProperty(modelName, propConf.name)
                    }
                    parentModelName = parentModelMetaData.parentModelName;
                }
            }

            modelMetaData.propertyConfigurations.each{propName, config->
                def ds = config.datasource;
                if(ds != null)
                {
                    if(!checkDatasourceExist(modelMetaData, modelMetaDatas, ds))
                    {
                        throw ModelGenerationException.datasourceDoesnotExists(modelName, ds, propName);
                    }
                }
                else
                {
                    if(!checkPropertyExist(modelMetaData, modelMetaDatas, config.datasourceProperty))
                    {
                        throw ModelGenerationException.datasourcePropertyDoesnotExists(modelName, config.datasourceProperty, propName);
                    }
                }
            }

            modelMetaData.datasourceConfiguration.each{dsName,dsConf->
                if(dsConf.keys.size() == 0)
                {
                    throw ModelGenerationException.noKeySpecifiedForDatasource(dsName, modelName);
                }
                if(modelMetaData.parentModelName && checkDatasourceExist(modelMetaDatas[modelMetaData.parentModelName], modelMetaDatas, dsName))
                {
                    throw ModelGenerationException.duplicateParentDatasource(dsName, modelName);
                }
            }
        }
    }

    def checkDatasourceExist(ModelMetaData modelMetaData, modelMetaDatas, datasourceName)
    {
        if(modelMetaData == null) return false;
        if(modelMetaData.datasourceConfiguration.containsKey(datasourceName))
        {
            return true;
        }
        if(modelMetaData.parentModelName)
        {
            ModelMetaData parentModel = modelMetaDatas[modelMetaData.parentModelName];
            return checkDatasourceExist(parentModel, modelMetaDatas, datasourceName);
        }
        return false;
    }

    def checkPropertyExist(ModelMetaData modelMetaData, modelMetaDatas, propertyName)
    {
        if(modelMetaData == null) return false;
        boolean propExist = false;
        modelMetaData.propertyList.each{
            if(it.name == propertyName)
            {
                propExist = true;
                return;
            }
        }
        if(propExist)
        {
            return true;
        }
        if(modelMetaData.parentModelName)
        {
            ModelMetaData parentModel = modelMetaDatas[modelMetaData.parentModelName];
            return checkPropertyExist(parentModel, modelMetaDatas, propExist);
        }
        return false;
    }

    def createModelFiles(modelMetaDatas)
    {
        workingModelDir.mkdirs();
        tempModelDir.mkdirs();
        modelMetaDatas.each {modelName,ModelMetaData modelMetaData->
            def modelFileToBeGenerated =  getGeneratedModelFile(modelMetaData.modelName);
            def modelText =  getModelText(modelMetaData);
            modelFileToBeGenerated.setText(modelText);
        }
    }

    def getModelText(ModelMetaData modelMetaData)
    {
        def bindings = ["model":modelMetaData];
        def autoGeneratedCode = autoGeneratedCodeTemplate.make(bindings).toString();


        def currentModelFile =  new File(workingModelDir.path + "/" + modelMetaData.modelName + ".groovy");
        if(!currentModelFile.exists())
        {
            bindings["DEFAULT_IMPORTS"] = DEFAULT_IMPORTS;
            bindings["AUTO_GENERATED_CODE"] = autoGeneratedCode;
            return wholeClassTemplate.make(bindings).toString();

        }
        else
        {
            return ModelGenerationUtils.generateClassText(currentModelFile, modelMetaData.modelName, modelMetaData.parentModelName, autoGeneratedCode, DEFAULT_IMPORTS);
        }
    }




}

class ModelMetaData
{
    def indexName;
    def storageType;
    def modelName;
    def parentModelName;
    def datasourceConfiguration = [:];
    def masterDatasource = null;
    def relations = [:];
    def propertyConfigurations = [:];
    def transientProps = [];
    def constraints = [:];
    def propertyList = [];
    def propertyMap = [:];
    def numberOfDatasources
    def ModelMetaData(String modelXml)
    {
        def xmlModel = new XmlSlurper().parseText(modelXml);
        modelName = xmlModel.@name.text()
        parentModelName = xmlModel.@parentModel == ""?null:xmlModel.@parentModel.text()
        indexName = xmlModel.@indexName.text() == ""?null:xmlModel.@indexName.text()
        storageType = xmlModel.@storageType.text() == ""?null:xmlModel.@storageType.text()
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
            if(datasourceConfiguration.containsKey(dsName))
            {
                throw ModelGenerationException.duplicateDatasource(dsName, modelName);
            }
            datasourceConfiguration[dsName] = dsConf;
        }
    }

    def processProperties(GPathResult model)
    {
        def processedProperties = [:]
        def masterKeyPropName = null;
        model.Properties.Property.each{GPathResult property->
            def generalPropConfig = [:];
            def propertyName = property.@name.text();
            if(propertyMap.containsKey(propertyName)){
                throw ModelGenerationException.duplicateProperty(modelName, propertyName);
            }
            generalPropConfig["type"] =getRealType(property.@type.text());
            generalPropConfig["name"] = propertyName;
            propertyMap[propertyName] = generalPropConfig;
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
                if(generalPropConfig["type"] == String.simpleName)
                {
                    constraints[propertyName][ConstrainedProperty.BLANK_CONSTRAINT] = true;
                }
                constraints[propertyName][ConstrainedProperty.NULLABLE_CONSTRAINT] = true;
            }
            else
            {
                if(!masterDatasource || !masterDatasource.keys.containsKey(propertyName))
                {
                    if(generalPropConfig["type"] == String.simpleName)
                    {
                        constraints[propertyName][ConstrainedProperty.BLANK_CONSTRAINT] = true;
                    }
                    constraints[propertyName][ConstrainedProperty.NULLABLE_CONSTRAINT] = true;
                }
                else
                {
                    if(generalPropConfig["type"] == String.simpleName)
                    {
                        constraints[propertyName][ConstrainedProperty.BLANK_CONSTRAINT] = false;
                    }
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

        if(parentModelName == null || parentModelName == "")
        {
            propertyList += [type:Long.simpleName, name:GrailsDomainClassProperty.IDENTITY];
            propertyList += [type:Long.simpleName, name:GrailsDomainClassProperty.VERSION];
            propertyList += [type:Errors.name, name:com.ifountain.rcmdb.util.RapidCMDBConstants.ERRORS_PROPERTY_NAME];
            propertyList += [type:Object.simpleName, name:com.ifountain.rcmdb.util.RapidCMDBConstants.OPERATION_PROPERTY_NAME];
            propertyList += [type:Object.simpleName, name:com.ifountain.rcmdb.util.RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED];
            constraints[com.ifountain.rcmdb.util.RapidCMDBConstants.OPERATION_PROPERTY_NAME] = ["${ConstrainedProperty.NULLABLE_CONSTRAINT}":true];
            constraints[com.ifountain.rcmdb.util.RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED] = ["${ConstrainedProperty.NULLABLE_CONSTRAINT}":true];
            constraints[com.ifountain.rcmdb.util.RapidCMDBConstants.ERRORS_PROPERTY_NAME] = ["${ConstrainedProperty.NULLABLE_CONSTRAINT}":true];
            transientProps += com.ifountain.rcmdb.util.RapidCMDBConstants.ERRORS_PROPERTY_NAME;
            transientProps += com.ifountain.rcmdb.util.RapidCMDBConstants.OPERATION_PROPERTY_NAME;
            transientProps += com.ifountain.rcmdb.util.RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED;
        }
    }

    private def processRelation(cardinality, oppositeCardinality, name, oppositeName, oppositeType, isOwner)
    {
        transientProps += name;
        def relationConfig = [:]
        if(cardinality == ModelGenerator.RELATION_TYPE_ONE && oppositeCardinality == ModelGenerator.RELATION_TYPE_MANY)
        {
            relationConfig["isMany"] = true;
            def generalPropConfig = [:];
            generalPropConfig["type"] = List.simpleName;
            generalPropConfig["name"] = name;
            generalPropConfig["defaultValue"] = [];
            propertyList += generalPropConfig;

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
            relationConfig["isMany"] = true;
            def generalPropConfig = [:];
            generalPropConfig["type"] = List.simpleName;
            generalPropConfig["name"] = name;
            generalPropConfig["defaultValue"] = [];
            propertyList += generalPropConfig;
        }
        relationConfig["reverseName"] = oppositeName;
        relationConfig["type"] = oppositeType;
        if(relations[name]  == null)
        {
            relations[name] = relationConfig;
        }
        else
        {
            throw ModelGenerationException.duplicateRelation(modelName, name);
        }
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
            return String.simpleName
        }
        else if(type== ModelGenerator.NUMBER_TYPE)
        {
            return Long.simpleName;
        }
        else if(type== ModelGenerator.FLOAT_TYPE)
        {
            return Double.simpleName;
        }
        else if(type == ModelGenerator.DATE_TYPE)
        {
            return Date.simpleName;
        }
        else if(type == ModelGenerator.BOOLEAN_TYPE)
        {
            return Boolean.simpleName;
        }
        else
        {
            return Object.simpleName;
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
        else if(type == "boolean")
        {
            return defaultValue?"${new Boolean(defaultValue).booleanValue()}":"false";
        }
        else
        {
            return "\"$defaultValue\"";
        }
    }
}