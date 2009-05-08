import org.codehaus.groovy.grails.plugins.searchable.compass.config.SearchableCompassConfiguratorFactory
import org.codehaus.groovy.grails.plugins.searchable.compass.config.mapping.SearchableGrailsDomainClassMappingConfiguratorFactory
import org.codehaus.groovy.grails.plugins.searchable.compass.mapping.DefaultSearchableCompassClassMappingXmlBuilder
import org.codehaus.groovy.grails.plugins.searchable.compass.config.mapping.SearchableGrailsDomainClassMappingConfigurator
import org.compass.core.config.CompassConfiguration
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.compass.DefaultCompassConfiguration
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.compass.core.CompassQuery
import groovy.xml.MarkupBuilder
import org.compass.core.CompassHits

CONFIG=new fullExportConfiguration().getExportConfiguration();

//keeps  map of all the model classes , modelName -> model
ALL_MODELS=[:];
/*
MODELS_TO_EXPORT keeps the map of models that will be exported
MODELS_TO_EXPORT sample MODEL_TO_EXPORT["RsTopologyObject"]=[relations:true]
EVERY CHILD OR PARENT model should have an entry , no mode child model processing done
the map entry should have relations:true or false
*/
MODELS_TO_EXPORT=[:];

//is true when all mode is true
// when true relation model exported as a whole
//when false only relations ids which are in RELATION_IDS_TO_EXPORT are exported
EXPORT_ALL_RELATIONS=false;
//exported relation ids are keps in this map, not to export again
RELATION_IDS_TO_EXPORT=[:];

logger.info("*****************FULL EXPORT STARTING *************************")

backup();
generateAllModels();
generateModelsToExport();
exportModels();


logger.info("*****************FULL EXPORT ENDED *************************")

return "size ${MODELS_TO_EXPORT.size()}, content : ${MODELS_TO_EXPORT}";

def backup()
{
    logger.info("backing up current data to directory '${CONFIG.backupDir}'");
    
    //def ant=new AntBuilder();
    //ant.delete(dir:CONFIG.backupDir);

    //application.RsApplication.backup(CONFIG.backupDir+File.separator+"index");
    logger.info("backing done");

}
def generateAllModels()
{
    def domClasses = ApplicationHolder.application.getDomainClasses();
    domClasses.each{
        def modelName = it.clazz.name;
        def model = ApplicationHolder.application.getDomainClass(modelName).clazz;
        ALL_MODELS[modelName]=model;
    }
}
def generateModelsToExport()
{
    logger.info("generating model list");
    def tempModelList=[];

    def allMode=false;
    def confMode=false;

    //we traverse the models in config , find if there is all/conf mode , we add others to model list
    CONFIG.MODELS.each{ modelEntry ->
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
        EXPORT_ALL_RELATIONS=true;
        
        ALL_MODELS.each{ modelName, model ->
            tempModelList.add([model:modelName,childModels:false,relations:false])
        }
    }
    else
    {
        //if allmode is false, we have selective models
        //relation model is exported but with only relation ids which are in RELATION_IDS_TO_EXPORT 
        EXPORT_ALL_RELATIONS=false;

        //if conf mode conf models added to list
        if(confMode)
        {
            ALL_MODELS.each{ modelName, model ->
                if(modelName.indexOf('.')>-1)
                {
                    tempModelList.add([model:modelName,childModels:false])
                }
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
                subModelName=subClass.clazz.name
                MODELS_TO_EXPORT[subModelName]=[relations:modelEntry.relations];
            }
        }
    }
    if(MODELS_TO_EXPORT.size()>0)
    {
        MODELS_TO_EXPORT["application.ObjectId"]=[relations:false];
    }

    MODELS_TO_EXPORT.remove("relation.Relation");


    logger.info("generated MODELS_TO_EXPORT : ${MODELS_TO_EXPORT}");
    
}



def exportModels()
{
    logger.info("exporting backup data to directory '${CONFIG.exportDir}'");

    logger.info("recreating directory '${CONFIG.exportDir}'");
    def ant=new AntBuilder();
    ant.delete(dir:CONFIG.exportDir);
    ant.mkdir(dir:CONFIG.exportDir);
    
    def compass=getCompass(CONFIG.backupDir);
    def session = compass.openSession()
    def tx = session.beginTransaction()

    try {
        MODELS_TO_EXPORT.each{ modelName,modelEntry ->
             exportModel(session,modelName,modelEntry.relations);
        }
        exportRelationsModel(session);
    } finally {
        tx.commit()
        session.close()
    }
    logger.info("exporting successfuly done");
}
def exportModel(session,modelName,relations)
{
    logger.info("   exporting model ${modelName}");


    def modelAlias=ALL_MODELS[modelName].simpleName;


    def query="alias:*";
    CompassQuery queryObj = session.queryBuilder().queryString(query).toQuery();
    queryObj.addSort ("id")
    queryObj.setAliases ([modelAlias] as String[]);
    CompassHits hits = queryObj.hits();

    def objectPerFile=CONFIG.objectPerFile;
    def fileCount=Math.floor(hits.length()/objectPerFile).toInteger()+1;

    logger.info("      ${modelName} have ${hits.length()} instances, will be exported to ${fileCount} files")

    fileCount.times{ fileCounter ->
        logger.info("      exporting ${modelName} file ${fileCounter}");
        def writer = new StringWriter();
	    def builder = new MarkupBuilder(writer);
	    def objectIds=[];

        builder.Objects(model:modelName){
            
            objectPerFile.times { objectCounter ->
                int dataIndex= ( fileCounter * objectPerFile ) + objectCounter
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
        exportXml(strXml,"${CONFIG.exportDir}/${modelName}_${fileCounter}.xml");

        if(relations)
        {
            logger.info("         marking relations of objects to be exported in file ${modelName}_${fileCounter}.xml")
            markRelationsOfObjectIds(session,objectIds);
            logger.info("         marking relations done");
            objectIds.clear();
        }

        logger.info("      exported ${modelName} file ${fileCounter}");
    }



    //println " ${modelName} results : ${objects}";

    logger.info("   exported model ${modelName}");
}
def markRelationsOfObjectIds(session,objectIds)
{
    StringBuffer buf=new StringBuffer();
    buf.append("alias:relation.Relation");
    objectIds.each{ objectId ->
        buf.append(" OR objectId:${objectId} OR reverseObjectId:${objectId}");
    }
    def query=buf.toString();
    CompassQuery queryObj = session.queryBuilder().queryString(query).toQuery();
    queryObj.addSort ("id")
    CompassHits hits = queryObj.hits();

    hits.length().times{ dataIndex ->
        def object=hits.data(dataIndex);
        RELATION_IDS_TO_EXPORT[object.id]=true;
    }

    logger.debug("MARKING RELATIONS with query ${query}");
}
def exportRelationsModel(session)
{
    def modelName="relation.Relation";
    logger.info("   exporting model ${modelName}");


    def modelAlias=ALL_MODELS[modelName].simpleName;


    def query="alias:*";
    CompassQuery queryObj = session.queryBuilder().queryString(query).toQuery();
    queryObj.addSort ("id")
    queryObj.setAliases ([modelAlias] as String[]);
    CompassHits hits = queryObj.hits();

    def objectPerFile=CONFIG.objectPerFile;
    def fileCount=Math.floor(hits.length()/objectPerFile).toInteger()+1;

    logger.info("      ${modelName} have ${hits.length()} instances, will be exported to ${fileCount} files")
    if(!EXPORT_ALL_RELATIONS)
    {
        logger.info("      ! some of the relations may be skipped");
    }

    fileCount.times{ fileCounter ->
        logger.info("      exporting ${modelName} file ${fileCounter}");
        def writer = new StringWriter();
	    def builder = new MarkupBuilder(writer);
	    def objectIds=[];
        def relationCount=0;
        def skipCount=0;
        
        builder.Objects(model:modelName){

            objectPerFile.times { objectCounter ->
                int dataIndex= ( fileCounter * objectPerFile ) + objectCounter
                if(dataIndex<hits.length)
                {
                    def object=hits.data(dataIndex);
                    def includeRelation=false;
                    if(EXPORT_ALL_RELATIONS)
                    {
                        includeRelation=true;
                    }
                    else
                    {
                        if(RELATION_IDS_TO_EXPORT.containsKey(object.id))
                        {
                            includeRelation=true;
                        }
                    }
                    if(includeRelation)
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
        exportXml(strXml,"${CONFIG.exportDir}/${modelName}_${fileCounter}.xml");


        logger.info("      exported ${modelName} file ${fileCounter}");

        logger.info("      added ${relationCount} relations, skipped ${skipCount} relations");
    }



    logger.info("   exported model ${modelName}");
}
def exportXml(strXml, fileName){
	def fw = new FileWriter(new File(fileName));
	fw.write(strXml);
	fw.close();
}

def getCompass(dataDir)
{
    def configurator = SearchableCompassConfiguratorFactory.getDomainClassMappingConfigurator(
        ApplicationHolder.application,
        [SearchableGrailsDomainClassMappingConfiguratorFactory.getSearchableClassPropertyMappingConfigurator([:], [], new DefaultSearchableCompassClassMappingXmlBuilder())] as SearchableGrailsDomainClassMappingConfigurator[]
    )
    def config = new CompassConfiguration()

    config.setConnection(new StringBuffer(System.getProperty("base.dir")).append(File.separator).append(dataDir).toString());

    def defaultSettings=DefaultCompassConfiguration.getDefaultSettings(ConfigurationHolder.getConfig());
    defaultSettings.remove("compass.engine.store.indexDeletionPolicy.type");
    defaultSettings.each{ key , val ->
        config.getSettings().setSetting (key,val);
    }


    configurator.configure(config, [:])

    def compass = config.buildCompass()
    return compass;
}



