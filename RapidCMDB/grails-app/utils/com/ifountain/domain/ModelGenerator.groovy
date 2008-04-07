package com.ifountain.domain

import model.Model
import org.apache.commons.io.FileUtils
import groovy.text.SimpleTemplateEngine
import model.ModelRelation

class ModelGenerator
{
    public static final List DEFAULT_IMPORTS = ["com.ifountain.core.domain.annotations.*"];
    private static ModelGenerator generator;
    static final String BASE_DIRECTORY = System.getProperty("base.dir", ".");
    static final String classLinePattern = "\\s*(public|private|protected|)\\s*class\\s+\\S+\\s*(extends\\s+\\S+)*.*";
    static final String importLinePattern = "\\s*import\\s+\\S+";
    static final String classDefinitionReplacementPattern = "\\s*(public|private|protected|)\\s*class\\s+\\S+\\s*(extends\\s+\\S+)*[^{]";
    static final String AUTO_GENERATED_CODE_COMMENT = "//AUTO_GENERATED_CODE";
    static final File wholeClasstemplateFile = new File("$BASE_DIRECTORY/grails-app/templates/DomainClassTemplate.txt");
    static final File autoGeneratedCodeTemplateFile = new File("$BASE_DIRECTORY/grails-app/templates/AutoGeneratedCodeTemplate.txt");

    def engine;
    def wholeClassTemplate;
    def autoGeneratedCodeTemplate;
    private ModelGenerator()
    {
        engine = new SimpleTemplateEngine();
        wholeClassTemplate = engine.createTemplate(wholeClasstemplateFile);
        autoGeneratedCodeTemplate = engine.createTemplate(autoGeneratedCodeTemplateFile);
    }

    public static ModelGenerator getInstance()
    {
        if(!generator)
        {
            generator = new ModelGenerator();
        }
        return generator;
    }


    def generateModel(Model model)
    {
        def dependentModels = ModelUtils.getDependentModels(model);
        def modelMetaDatas = [:];
        dependentModels.each{key,value->
            modelMetaDatas[key] = new ModelMetaData(value);
        }
        validateModels(modelMetaDatas);
        createModelFiles(modelMetaDatas);
    }                  




    private def createEmptyDependentClasses(model)
    {
        def modelsNeedsToBeCreated = [:]
        if(model.parentModel)
        {
            modelsNeedToBeCreated[]
            generateEmptyModel (model.parentModel);
        }
        model.fromRelations
    }

    def validateModels(modelMetaDatas)
    {
        modelMetaDatas.each{modelName,modelMetaData->
            def modelBean = modelMetaData.model; 
            if(!modelMetaData.masterDatasource && !modelBean.parentModel)
            {
                throw ModelGenerationException.masterDatasourceDoesnotExists(modelBean.name);
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
        modelMetaDatas.each {modelName,modelMetaData->
            def model = modelMetaData.model;
            model.generateAll = Boolean.TRUE;
            model.save();
            def parentDir = model.getModelFile().getParentFile();
            parentDir.mkdirs();

            def bindings = ["model":modelMetaData];
            def autoGeneratedCode = autoGeneratedCodeTemplate.make(bindings).toString();

            if(!model.isGenerated())
            {
                bindings["DEFAULT_IMPORTS"] = DEFAULT_IMPORTS;
                bindings["AUTO_GENERATED_CODE"] = autoGeneratedCode;
                model.getModelFile().withWriter { w ->
                    def x = wholeClassTemplate.make(bindings);
//                    println x;
                    x.writeTo(w);
                }
            }
            else
            {
                def modelFile = model.getModelFile();
                def newClassFileBeforeClassDefinition = new StringBuffer();
                def newClassFileafterClassDefinition = new StringBuffer();
                int classParts = 0;
                modelFile.eachLine {line->

                    if(classParts == 0 && line.matches(importLinePattern))
                    {
                        DEFAULT_IMPORTS.each{defaultImport->
                            if(line.indexOf(defaultImport) < 0)
                            {
                                newClassFileBeforeClassDefinition.append(line+ "\n");
                            }
                        }
                    }
                    else if(classParts == 0 && line.matches(classLinePattern))
                    {
                        def defaultImportPart = new StringBuffer();
                        DEFAULT_IMPORTS.each{defaultImport->
                            defaultImportPart.append("import $defaultImport;\n")
                        }
                        defaultImportPart.append (newClassFileBeforeClassDefinition);
                        newClassFileBeforeClassDefinition = defaultImportPart;
                        classParts++;
                        def classDefinition = "class $model.name";
                        if(model.parentModel)
                        {
                            classDefinition += " extends $model.parentModel.name"
                        }
                        classDefinition += line.replaceAll(classDefinitionReplacementPattern, " ");
                        newClassFileBeforeClassDefinition.append(classDefinition + "\n");
                        if(line.trim().endsWith("{"))
                        {
                            classParts++;
                        }
                    }
                    else if(classParts == 1)
                    {
                        newClassFileBeforeClassDefinition.append(line + "\n");
                        if(line.trim().endsWith("{"))
                        {
                            classParts++;
                        }
                    }
                    else if(classParts == 2 && line.indexOf(AUTO_GENERATED_CODE_COMMENT) > 0)
                    {
                        classParts++;
                    }
                    else if(classParts == 3 && line.indexOf(AUTO_GENERATED_CODE_COMMENT) > 0)
                    {
                        classParts ++;
                    }
                    else
                    {
                        if(classParts == 0)
                        {
                            if(!line.endsWith("\n"))
                            {
                                newClassFileBeforeClassDefinition.append(line+ "\n");
                            }
                            else
                            {
                                newClassFileBeforeClassDefinition.append(line);                                
                            }
                        }
                        else if(classParts >3 || classParts == 2 || classParts == 1)
                        {
                            if(!line.endsWith("\n"))
                            {
                                newClassFileafterClassDefinition.append(line+ "\n");
                            }
                            else
                            {
                                newClassFileBeforeClassDefinition.append(line);                                
                            }
                        }
                    }
                }
                modelFile.setText(newClassFileBeforeClassDefinition.toString() + autoGeneratedCode + newClassFileafterClassDefinition);
            }
        }
    }
}

class ModelMetaData
{
    def datasourceConfiguration = [:];
    def masterDatasource;
    def hasMany = [:];
    def belongsTo = [];
    def mappedBy = [:];
    def propertyConfigurations = [:];
    def transientProps = [];
    def constraints = [:];
    def propertyList = [];
    def Model model;
    def numberOfDatasources
    def ModelMetaData(Model model)
    {
        this.model = model;
        createDatasourceConfiguration (model);
        processProperties(model);
        processRelations(model);

    }

    def createDatasourceConfiguration(Model model)
    {
        model.datasources.each{
            def dsConf = [:];
            def dsName = it.datasource.name;
            datasourceConfiguration[dsName] = dsConf;
            dsConf["master"] = it.master;
            if(it.master)
            {
                masterDatasource = dsConf;
            }

            def keys = [:];
            it.keyMappings.each{keyMapping->
                keys[keyMapping.property.name] = ["nameInDs":keyMapping.nameInDatasource?keyMapping.nameInDatasource:keyMapping.property.name];
            }
            dsConf["keys"] =  keys;
        }
    }

    def processProperties(Model model)
    {
        def masterKeyPropName = null;
        model.modelProperties.each{
            def generalPropConfig = [:];
            generalPropConfig["type"] = it.convertToRealType();
            generalPropConfig["name"] = it.name;

            if(it.defaultValue)
            {
                generalPropConfig["defaultValue"] = getDefaultValue(it);
            }
            propertyList += generalPropConfig;
            constraints[it.name] = [:];
            if(it.propertyDatasource != null && !it.propertyDatasource.master || it.propertySpecifyingDatasource != null)
            {
                transientProps += it.name;
                def federatedPropertyConfiguration = [:];
                federatedPropertyConfiguration["nameInDs"] =  it.nameInDatasource?it.nameInDatasource:it.name;
                if(it.propertyDatasource)
                {
                    federatedPropertyConfiguration["datasource"] =  it.propertyDatasource.datasource.name;
                }
                else
                {
                    federatedPropertyConfiguration["datasourceProperty"] =  it.propertySpecifyingDatasource.name;
                }
                federatedPropertyConfiguration["lazy"] = it.lazy;
                propertyConfigurations[it.name] = federatedPropertyConfiguration;
                constraints[it.name]["blank"] = it.blank;
                constraints[it.name]["nullable"] = it.blank;
            }
            else
            {
                if(!masterDatasource || !masterDatasource.keys.containsKey(it.name))
                {
                    constraints[it.name]["blank"] = it.blank;
                    constraints[it.name]["nullable"] = it.blank;
                }
                else
                {
                    masterKeyPropName = it.name;
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
            if(!uniqueKeys.isEmpty())
            {
                constraints[masterKeyPropName]["unique"] = uniqueKeys;     
            }
            else
            {
                constraints[masterKeyPropName]["unique"] = true;    
            }

        }
    }
    private def processRelation(cardinality, oppositeCardinality, name, oppositeName, oppositeType, isSecond)
    {
        if(cardinality == ModelRelation.ONE && oppositeCardinality == ModelRelation.MANY)
        {
            hasMany[name] = oppositeType;
        }
        else if(cardinality == ModelRelation.MANY && oppositeCardinality == ModelRelation.ONE || cardinality == ModelRelation.ONE && oppositeCardinality == ModelRelation.ONE)
        {
            constraints[name] = [nullable:true];
            def generalPropConfig = [:];
            generalPropConfig["type"] = oppositeType;
            generalPropConfig["name"] = name;
            generalPropConfig["defaultValue"] = null;
            propertyList += generalPropConfig;
        }
        else if(cardinality == ModelRelation.MANY && oppositeCardinality == ModelRelation.MANY)
        {
            if(isSecond)
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
    
    def processRelations(Model model)
    {
        model.fromRelations.each{
            processRelation(it.firstCardinality,it.secondCardinality,it.firstName,it.secondName,it.secondModel.name, false);
        }
        model.toRelations.each{
            processRelation(it.secondCardinality,it.firstCardinality,it.secondName,it.firstName,it.firstModel.name, true);
        }
    }

    def getDefaultValue(modelProperty)
    {
        if(modelProperty.type == model.ModelProperty.stringType)
        {
            return "\"$modelProperty.defaultValue\"";
        }
        else if(modelProperty.type == model.ModelProperty.numberType)
        {
            return "$modelProperty.defaultValue";
        }
        else if(modelProperty.type == model.ModelProperty.dateType)
        {
            return "new Date(${modelProperty.defaultValue})";
        }
        else
        {
            return "\"$modelProperty.defaultValue\"";
        }
    }
}