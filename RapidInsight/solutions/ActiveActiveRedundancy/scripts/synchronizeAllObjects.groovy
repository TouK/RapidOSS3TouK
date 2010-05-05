import datasource.*;
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils


// ---------------------------------------
// CONFIGURATION STARTS

MODELS_TO_SYNC=[];

//User & Groups & Notification System classes
MODELS_TO_SYNC.add("message.RsMessageRule");
MODELS_TO_SYNC.add("message.RsMessageRuleCalendar");
MODELS_TO_SYNC.add("auth.RsUser");
MODELS_TO_SYNC.add("auth.Group");
MODELS_TO_SYNC.add("auth.RsUserInformation");

//Maintenance Classes
MODELS_TO_SYNC.add("RsInMaintenance");
MODELS_TO_SYNC.add("RsInMaintenanceSchedule");

//UI Classes
MODELS_TO_SYNC.add("search.SearchQuery");
MODELS_TO_SYNC.add("search.SearchQueryGroup");

MODELS_TO_SYNC.add("ui.ComponentConfig");

MODELS_TO_SYNC.add("ui.GridColumn");
MODELS_TO_SYNC.add("ui.GridView");

MODELS_TO_SYNC.add("ui.map.TopoMap");
MODELS_TO_SYNC.add("ui.map.MapGroup");



// CONFIGURATION ENDS
// ---------------------------------------


redundancyUtility=application.RapidApplication.getUtility("RedundancyUtility");

withRelations=true;

OUTPUT="";
logger.info("---------------------------------------------------")
logInfo("Starting Syncronization ");

objectCountPerRequest=100;

//a main try statement , to put isRemote to current context, will be removed in finally
try{
ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);


def datasources=HttpDatasource.searchEvery("name:redundancy*");
if(datasources.size()==0)
{
    logWarn("Error : no redundancy server is defined");
    return OUTPUT;
}

//For each server , process All Objects
datasources.each{ ds ->
	logger.info("Syncronization with ${ds.name} starts *******");

    //for each model
    MODELS_TO_SYNC.each{ modelName ->
        logger.info("Syncronization for ${modelName} with ${ds.name} starts *******");
        def requestParams=[:];
        requestParams.login="rsadmin";
        requestParams.password="changeme";
        requestParams.format="xml";
        requestParams.sort="rsUpdatedAt";
        requestParams.order="asc";
        requestParams.searchIn=modelName;
        requestParams.max=objectCountPerRequest;
        requestParams.query="alias:*";

        try{
                //first offset is 0 , nextOffset is 100
                def nextOffset=0;
                requestParams.offset=nextOffset;
                nextOffset+=objectCountPerRequest;


                def totalObjectCount=processRequest(ds,requestParams);
                logger.info("${totalObjectCount} total objects .");
                def syncFinished=(nextOffset>totalObjectCount);


                while(! IS_STOPPED() && !syncFinished)
                {
                    requestParams.offset=nextOffset;
                    nextOffset+=objectCountPerRequest;
                    processRequest(ds,requestParams);

                    syncFinished=(nextOffset>totalObjectCount);
                }
                logger.info("Syncronization for ${modelName} with ${ds.name} ends *******");

        }
        catch(e)
        {
            logWarn("${modelName} : Error while processing xml. Reason ${e}");
        }
    }

    logger.info("Syncronization with ${ds.name} ends *******");
}


}
finally
{
	ExecutionContextManagerUtils.removeObjectFromCurrentContext("isRemote");
}

logInfo("completed synchronization <br>");
return OUTPUT;



def processRequest(ds,requestParams)
{
	    logger.info("Requestinq ${requestParams.searchIn} , query : ${requestParams.query} , offset:${requestParams.offset} , max : ${requestParams.max}");
	    def searchUrl="script/run/updatedObjects";
	    if(withRelations)
	    {
	    	requestParams.withRelations="true";
	    }

		def xmlResult=ds.doRequest(searchUrl,requestParams);
		if(xmlResult.indexOf("<Errors>")>=0)
		{
			logWarn("${requestParams.searchIn}:  Xml has error : ${xmlResult.toString()}");
			return 0;
		}

        def xmlRoot=new XmlSlurper().parseText(xmlResult);
        def xmlTopRow=xmlRoot.attributes();
        def xmlObjects=xmlRoot.Object;

        def totalObjectCount=Long.parseLong(xmlRoot.@total.toString());
        logger.info(" Will process ${xmlObjects.size()} objects after offset ${requestParams.offset}");


        xmlObjects.each{ xmlObject ->
                def props=[:];
                props.putAll(xmlObject.attributes());


                def idRelatedObjects=xmlObject.IdRelatedObject;
                idRelatedObjects.each{ idRelatedObject ->
	                def relatedProps=idRelatedObject.attributes();
					def relationName=relatedProps.relationName;

                	def domainClass=org.codehaus.groovy.grails.commons.ApplicationHolder.application.getDomainClass(relatedProps.alias);
                	def relatedObject=domainClass.clazz.searchEvery(relatedProps.searchQuery)[0];
                	if(relatedObject)
                	{
                		props[relationName]=relatedObject.id;
                	}
                }

                modelClass=application.RapidApplication.getModelClass(props.alias);
                logger.debug("object Remote id: ${props.id} will be added.")
                props.remove("rsInsertedAt");
                props.remove("rsUpdatedAt");

                if(withRelations)
                {
                    //process relations
                    def relatedObjects=[:];
                    def xmlRelatedObjects=xmlObject.RelatedObject;
                    xmlRelatedObjects.each{ xmlRelatedObject ->
                        def relatedProps=xmlRelatedObject.attributes();
                        def relationName=relatedProps.relationName;

                        def domainClass=org.codehaus.groovy.grails.commons.ApplicationHolder.application.getDomainClass(relatedProps.alias);
                        def relatedObject=domainClass.clazz.searchEvery(relatedProps.searchQuery)[0];
                        if(relatedObject)
                        {
                            if(!relatedObjects.containsKey(relationName))
                            {
                                relatedObjects[relationName]=[];
                            }
                            relatedObjects[relationName].add(relatedObject);
                        }

                    }
                    if(relatedObjects.size()>0)
                    {
                        relatedObjects.each{ relationName, relatedObjectList ->
                            logger.debug("Adding relation to object props ${relationName} : ${relatedObjectList} ");
                            props[relationName]=relatedObjectList;
                        }
                    }
                }

                def object=modelClass.add(props);
                if(object.hasErrors())
                {
                   logger.warn("could not add ${modelName} with props ${props}. Reason ${object.errors}");
                }
                else
                {
                    logger.debug("object Local id: ${object.id} added successfuly.");
                }

        }

        logger.info(" Processed ${xmlObjects.size()} objects after offset ${requestParams.offset}");

        return totalObjectCount;
}


def logWarn(message)
{
   logger.warn(message);
   OUTPUT += "WARN : ${message} <br>";
}

def logInfo(message)
{
   logger.info(message);
   OUTPUT += "INFO : ${message} <br>";
}




