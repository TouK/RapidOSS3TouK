package com.ifountain.rcmdb.domain

import application.RsApplication
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.plugins.searchable.compass.config.SearchableCompassConfiguratorFactory
import org.codehaus.groovy.grails.plugins.searchable.compass.config.mapping.SearchableGrailsDomainClassMappingConfiguratorFactory
import org.codehaus.groovy.grails.plugins.searchable.compass.mapping.DefaultSearchableCompassClassMappingXmlBuilder
import org.codehaus.groovy.grails.plugins.searchable.compass.config.mapping.SearchableGrailsDomainClassMappingConfigurator
import org.compass.core.config.CompassConfiguration
import com.ifountain.compass.DefaultCompassConfiguration
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.compass.core.CompassQuery
import groovy.xml.MarkupBuilder
import org.compass.core.CompassHits

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 12, 2009
* Time: 8:44:22 AM
* To change this template use File | Settings | File Templates.
*/
class FullExportImportUtility {

    def logger;
    def compass;
    def compassSession;
    def RELATION_IDS_TO_EXPORT=[:];

    def FullExportImportUtility(logger)
    {
        this.logger=logger;
    }
    def fullExport(CONFIG)
    {
        backup(CONFIG.backupDir);
        RELATION_IDS_TO_EXPORT.clear();

        def EXPORT_CONFIG=generateModelsToExport(CONFIG.MODELS);
        def MODELS_TO_EXPORT=EXPORT_CONFIG.MODELS_TO_EXPORT;
        def EXPORT_MARKED_RELATIONS=EXPORT_CONFIG.EXPORT_MARKED_RELATIONS;

        beginCompass(CONFIG.backupDir);

        def tx = beginCompassTransaction();
        try {
            exportModels (CONFIG.exportDir,CONFIG.objectsPerFile,MODELS_TO_EXPORT)
            if(EXPORT_MARKED_RELATIONS)
            {
                exportMarkedRelations (CONFIG.exportDir,CONFIG.objectsPerFile)
            }
        }
        finally {
            endCompassTransaction (tx);
            endCompass();
        }

    }
    protected def backup(backupDir)
    {
        logger.info("backing up current data to directory '${backupDir}'");
        def ant=new AntBuilder();
        ant.delete(dir:backupDir);

        RsApplication.backup(backupDir+File.separator+"index");
        logger.info("backing up done");
    }
    protected def generateModelsToExport(MODELS)
    {
        def MODELS_TO_EXPORT=[:];
        def EXPORT_MARKED_RELATIONS=false;

        logger.info("generating models to export");
        def tempModelList=[];

        def allMode=false;
        def confMode=false;

        //we traverse the models in config , find if there is all/conf mode , we add others to model list
        MODELS.each{ modelEntry ->
            if(modelEntry.model == "all")
            {
                allMode=true;
            }
            else if(modelEntry.model == "conf")
            {
                confMode=true;
            }
            else
            {
                tempModelList.add(modelEntry);
            }
        }

        //if all mode model list cleared and all models added
        //the relation model will also be exported
        if(allMode)
        {
            tempModelList.clear();
            EXPORT_MARKED_RELATIONS=false;

            getAllModelNames().each{ modelName ->
                tempModelList.add([model:modelName,childModels:false,relations:false])
            }
        }
        else
        {
            //if allmode is false, we have selective models
            //relation model is exported but with only relation ids which are in RELATION_IDS_TO_EXPORT
            EXPORT_MARKED_RELATIONS=true;

            //if conf mode conf models added to list
            if(confMode)
            {
                getConfModelNames().each{ modelName ->
                    tempModelList.add([model:modelName,childModels:false])
                }
            }
        }

        tempModelList.each{ modelEntry ->
            if(!modelEntry.containsKey("childModels"))
            {
                modelEntry.childModels=true;
            }
            if(!modelEntry.containsKey("relations"))
            {
                modelEntry.relations=true;
            }
            def modelName=modelEntry.model
            MODELS_TO_EXPORT[modelName]=[relations:modelEntry.relations];
            if(modelEntry.childModels)
            {
                def domainClass=ApplicationHolder.application.getDomainClass(modelName);
                domainClass.subClasses.each{ subClass ->
                    def subModelName=subClass.clazz.name
                    MODELS_TO_EXPORT[subModelName]=[relations:modelEntry.relations];
                }
            }
        }
        if(MODELS_TO_EXPORT.size()>0)
        {
            MODELS_TO_EXPORT["application.ObjectId"]=[relations:false];
        }
        if(EXPORT_MARKED_RELATIONS)
        {
            MODELS_TO_EXPORT.remove("relation.Relation");
        }
        logger.info("generated MODELS_TO_EXPORT : ${MODELS_TO_EXPORT}");

        def EXPORT_CONFIG=[MODELS_TO_EXPORT:MODELS_TO_EXPORT,EXPORT_MARKED_RELATIONS:EXPORT_MARKED_RELATIONS];
        return EXPORT_CONFIG;
    }
    protected def getAllModelNames()
    {
        def modelNames=[];
        ApplicationHolder.application.getDomainClasses().each{ domainClass ->
            def modelName=domainClass.clazz.name;
            modelNames.add(modelName);

        }
        return modelNames;
    }
    protected def getConfModelNames()
    {
        def modelNames=[];
        ApplicationHolder.application.getDomainClasses().each{ domainClass ->
            def modelName=domainClass.clazz.name;
            if(modelName.indexOf('.')>-1)
            {
                modelNames.add(modelName);
            }
        }
        return modelNames;
    }
    protected def getModelAlias(modelName)
    {
        ApplicationHolder.application.getDomainClass(modelName).clazz.simpleName;
    }

    protected def exportModels(exportDir,objectsPerFile,MODELS_TO_EXPORT)
    {
        logger.info("exporting backup data to directory '${exportDir}'");

        logger.info("recreating directory '${exportDir}'");
        def ant=new AntBuilder();
        ant.delete(dir:exportDir);
        ant.mkdir(dir:exportDir);

        MODELS_TO_EXPORT.each{ modelName,modelEntry ->
             exportModel(exportDir,objectsPerFile,modelName,modelEntry.relations);
        }

        logger.info("exporting successfuly done");
    }
    protected int getFileCount(instanceCount,objectsPerFile)
    {
        if(objectsPerFile == 0)
            return 0;
        return Math.ceil(instanceCount/objectsPerFile).toInteger();
    }
    protected CompassHits getModelHits(modelName,query)
    {
        def modelAlias=getModelAlias(modelName);

        CompassQuery queryObj = getCompassSession().queryBuilder().queryString(query).toQuery();
        queryObj.addSort ("id")
        queryObj.setAliases ([modelAlias] as String[]);
        CompassHits hits = queryObj.hits();

        return hits;
    }
    protected def exportModel(exportDir,objectsPerFile,modelName,relations)
    {
        logger.info("   exporting model ${modelName}");


        def hits=getModelHits(modelName,"alias:*");


        def fileCount=getFileCount(hits.length(),objectsPerFile);

        logger.info("      ${modelName} have ${hits.length()} instances, will be exported to ${fileCount} files")

        fileCount.times{ fileCounter ->
            logger.info("      exporting ${modelName} file ${fileCounter}");
            def writer = new StringWriter();
            def builder = new MarkupBuilder(writer);
            def objectIds=[];

            builder.Objects(model:modelName){

                objectsPerFile.times { objectCounter ->
                    int dataIndex= ( fileCounter * objectsPerFile ) + objectCounter
                    if(dataIndex<hits.length)
                    {
                        def object=hits.data(dataIndex);
                        if(relations)
                        {
                            objectIds.add(object.id);
                        }
                        def props=object.asMap();
                        builder.Object(props);
                    }
                }
            }
            def strXml = writer.toString();
            exportXml(strXml,"${exportDir}/${modelName}_${fileCounter}.xml");

            if(relations)
            {
                logger.info("         marking relations of objects to be exported in file ${modelName}_${fileCounter}.xml")
                markRelationsOfObjectIds(objectIds);
                logger.info("         marking relations done");
                objectIds.clear();
            }

            logger.info("      exported ${modelName} file ${fileCounter}");
        }



        //println " ${modelName} results : ${objects}";

        logger.info("   exported model ${modelName}");
    }
    protected def markRelationsOfObjectIds(objectIds)
    {
        StringBuffer buf=new StringBuffer();
        buf.append("alias:relation.Relation");
        objectIds.each{ objectId ->
            buf.append(" OR objectId:${objectId} OR reverseObjectId:${objectId}");
        }
        def query=buf.toString();
        def hits=getModelHits("relation.Relation",query);

        hits.length().times{ dataIndex ->
            def object=hits.data(dataIndex);
            RELATION_IDS_TO_EXPORT[object.id]=true;
        }

        logger.debug("MARKING RELATIONS with query ${query}");
    }
    protected def exportMarkedRelations(exportDir,objectsPerFile)
    {
        def modelName="relation.Relation";
        logger.info("   exporting model ${modelName}");

        def hits=getModelHits(modelName,"alias:*");

        def fileCount=getFileCount(hits.length(),objectsPerFile);

        logger.info("      ${modelName} have ${hits.length()} instances, will be exported to ${fileCount} files")

        logger.info("      ! some of the relations may be skipped");


        fileCount.times{ fileCounter ->
            logger.info("      exporting ${modelName} file ${fileCounter}");
            def writer = new StringWriter();
            def builder = new MarkupBuilder(writer);
            def objectIds=[];
            def relationCount=0;
            def skipCount=0;

            builder.Objects(model:modelName){

                objectsPerFile.times { objectCounter ->
                    int dataIndex= ( fileCounter * objectsPerFile ) + objectCounter
                    if(dataIndex<hits.length)
                    {
                        def object=hits.data(dataIndex);

                        if(RELATION_IDS_TO_EXPORT.containsKey(object.id))
                        {
                            relationCount++;
                            def props=object.asMap();
                            builder.Object(props);
                        }
                        else
                        {
                            skipCount++;
                        }
                    }
                }
            }
            def strXml = writer.toString();
            exportXml(strXml,"${exportDir}/${modelName}_${fileCounter}.xml");


            logger.info("      exported ${modelName} file ${fileCounter}");

            logger.info("      added ${relationCount} relations, skipped ${skipCount} relations");
        }



        logger.info("   exported model ${modelName}");
    }
    protected def exportXml(strXml, fileName){
        def fw = new FileWriter(new File(fileName));
        fw.write(strXml);
        fw.close();
    }
    protected def beginCompass(dataDir)
    {
        def configurator = SearchableCompassConfiguratorFactory.getDomainClassMappingConfigurator(
            ApplicationHolder.application,
            [SearchableGrailsDomainClassMappingConfiguratorFactory.getSearchableClassPropertyMappingConfigurator([:], [], new DefaultSearchableCompassClassMappingXmlBuilder())] as SearchableGrailsDomainClassMappingConfigurator[]
        )
        def config = new CompassConfiguration()

//        String compassConnection = System.getProperty("index.dir") != null?System.getProperty("index.dir"):new StringBuffer(System.getProperty("base.dir")).
//                append(File.separator).
//                append(dataDir).
//                toString();
        String compassConnection=dataDir;                
        config.setConnection(compassConnection);

        def defaultSettings=DefaultCompassConfiguration.getDefaultSettings(ConfigurationHolder.getConfig());
        defaultSettings.remove("compass.engine.store.indexDeletionPolicy.type");
        defaultSettings.each{ key , val ->
            config.getSettings().setSetting (key,val);
        }

        configurator.configure(config, [:])

        this.compass = config.buildCompass()
        this.compassSession=this.compass.openSession();
    }
    protected def endCompass()
    {
        this.compassSession.close()
        this.compass.close()
    }
    protected def beginCompassTransaction()
    {
        return compassSession.beginTransaction()
    }
    protected def endCompassTransaction(tx)
    {
        tx.commit();
    }



}