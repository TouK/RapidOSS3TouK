import datasource.*;
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils


redundancyUtility=application.RapidApplication.getUtility("RedundancyUtility");

modelName="RealUpdatedObjects";
withRelations=true;

OUTPUT="";
logger.info("---------------------------------------------------")
logInfo("Starting Syncronization ${modelName}");

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

//For each server , process UpdatedObjects
datasources.each{ ds ->	
	logger.info("Syncronization with ${ds.name} starts *******");
	
	modelUpdatedAtKey="${modelName}_${ds.name}_UpdatedAt";
	modelUpdatedAt=0;
	
	//lookup model
	def modelLookup=RsLookup.get(name:modelUpdatedAtKey);
	if(modelLookup!=null)
	{
		modelUpdatedAt=Long.parseLong(modelLookup.value)+1;
	}
	
	
	def requestParams=[:];
	requestParams.login="rsadmin";
	requestParams.password="changeme";        
	requestParams.format="xml";
	requestParams.sort="rsUpdatedAt";
	requestParams.order="asc";
	requestParams.searchIn=modelName;
	requestParams.max=objectCountPerRequest;
	requestParams.query="rsUpdatedAt:[${modelUpdatedAt} TO *]";

	try{
			//first offset is 0 , nextOffset is 100
			def nextOffset=0;
			requestParams.offset=nextOffset;		
			nextOffset+=objectCountPerRequest;
			
	
			def totalObjectCount=processRequest(ds,requestParams);		
			logger.info("${totalObjectCount} total objects changed from ${modelUpdatedAt}");
			def syncFinished=(nextOffset>totalObjectCount);
			
			
			while(! IS_STOPPED() && !syncFinished)
			{
				requestParams.offset=nextOffset;
				nextOffset+=objectCountPerRequest;
				processRequest(ds,requestParams);
				
				syncFinished=(nextOffset>totalObjectCount);			
			}
			logger.info("Syncronization with ${ds.name} ends *******");			
	        
	}
	catch(e)
	{
        logWarn("${modelName} : Error while processing xml. Reason ${e}");
	}
}


}
finally
{
	ExecutionContextManagerUtils.removeObjectFromCurrentContext("isRemote");
}

logInfo("${modelName}: completed synchronization <br>");
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
			logWarn("${modelName}:  Xml has error : ${xmlResult.toString()}");			
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
                //check if a newer object exists with countHits	
                def objectSearchQuery=redundancyUtility.getKeySearchQueryForObject(props.alias,props);                
                def newerObjectCount=modelClass.countHits(objectSearchQuery+" AND rsUpdatedAt:[${props.rsUpdatedAt} TO *]");
                if(newerObjectCount>0)
                {
                	logger.info("!!! Skipping object Remote id: ${props.id} from ${ds.name}, because a newer version exists in Local server.");	                
                }
                else
                {
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
	                   logger.warn("could not update ${modelName} with props ${props}. Reason ${object.errors}");
	                }
	                else
	                {
	                	logger.debug("object Local id: ${object.id} added successfuly.");
               		}
                }
        }
        
        //save the state     
        if(xmlObjects.size()>0)
        {
//        	since sorted by rsUpdatedAt looking at the last record updated at is enough			
        	def modelLastUpdatedAt=xmlTopRow.rsUpdatedAt;
			logger.info("Saving RsLookup ${[name:modelUpdatedAtKey,value:modelLastUpdatedAt]} ")
        	RsLookup.add([name:modelUpdatedAtKey,value:modelLastUpdatedAt]);
        	
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




