import datasource.*;
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils

//modelName should be passed as param to this script
//other scripts should call this script with CmdbScript.runScript("synchronizeObjects", ["modelName":"SmartsNotification","logger":logger]);
//if this script wanted to be used only : comment in previous line
//modelName="SmartsNotification"

redundancyUtility=application.RapidApplication.getUtility("RedundancyUtility");

modelName="DeletedObjects";


logger.warn("---------------------------------------------------")
logger.info("Starting Syncronization ${modelName} ******************************")
OUTPUT=" Starting Syncronization ${modelName} ";

objectCountPerRequest=100;

//a main try statement , to put isRemote to current context, will be removed in finally 
try{
ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);


def datasources=HttpDatasource.searchEvery("name:redundancy*");
if(datasources.size()==0)
{
	logger.warn("${modelName} : Error : no redundancy server is defined");
    return "${modelName}: Error : no redundancy server is defined";
}

//For each server , process changed objects of the model
datasources.each{ ds ->	
	logger.info("Syncronization with ${ds.name} starts *******");
	
	eventUpdatedAtKey="${modelName}_${ds.name}_UpdatedAt";
	eventUpdatedAt=0;
	
	//lookup event
	def eventLookup=RsLookup.get(name:eventUpdatedAtKey);
	if(eventLookup!=null)
	{
		eventUpdatedAt=eventLookup.value;
	}
	
	
	def requestParams=[:];
	requestParams.login="rsadmin";
	requestParams.password="changeme";        
	requestParams.format="xml";
	requestParams.sort="rsUpdatedAt";
	requestParams.order="asc";
	requestParams.searchIn=modelName;
	requestParams.max=objectCountPerRequest;
	requestParams.query="rsUpdatedAt:[${eventUpdatedAt} TO *] ";
	
	
	
	try{
			//first offset is 0 , nextOffset is 100
			def nextOffset=0;
			requestParams.offset=nextOffset;		
			nextOffset+=objectCountPerRequest;
			
	
			def totalObjectCount=processRequest(ds,requestParams);		
			logger.info("${totalObjectCount} total objects changed from ${eventUpdatedAt}");
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
	        logger.warn("${modelName} : Error while processing xml. Reason ${e}",e);
	        OUTPUT+="<br> ${modelName} : Error while processing xml. Reason ${e}";
	}
}


}
finally
{
	ExecutionContextManagerUtils.removeObjectFromCurrentContext("isRemote");
}

logger.info("${modelName}: completed scynchronization ******************************");
OUTPUT+= "<br> ${modelName}: completed synchronization <br>"
return OUTPUT;



def processRequest(ds,requestParams)
{
	    logger.info("Requestinq ${requestParams.searchIn} , query : ${requestParams.query} , offset:${requestParams.offset} , max : ${requestParams.max}");
	    def searchUrl="script/run/updatedObjects";
	    
	    
		def xmlResult=ds.doRequest(searchUrl,requestParams);		
		if(xmlResult.indexOf("<Errors>")>=0)
		{	
			logger.warn("${modelName}:  Xml has error : ${xmlResult.toString()}");
			OUTPUT+= "<br> ${modelName}:  Xml has error : ${xmlResult.toString()}"
			return 0;
		}
	
        def xmlRoot=new XmlSlurper().parseText(xmlResult);
        def xmlObjects=xmlRoot.Object;

        def totalObjectCount=Long.parseLong(xmlRoot.@total.toString());
        logger.info(" Will process ${xmlObjects.size()} objects after offset ${requestParams.offset}");
        
        	
        xmlObjects.each{ xmlObject ->
                def props=[:];
                props.putAll(xmlObject.attributes());
                
                def modelClass=application.RapidApplication.getModelClass(props.modelName);
                //get the object and delete it
                def object=modelClass.searchEvery(props.searchQuery)[0];
                if(object)
                {
                	object.remove();
                	if(object.hasErrors())
                	{
                		logger.warn("Could not delete object : ${modelClass}, ${props.searchQuery}, Reason : ${object.errors}");
                	}
                	else
                	{
                		logger.debug("Successfuly deleted object : ${modelClass}, ${props.searchQuery}");
                	}
                }
                
        }
        
        //save the state     
        if(xmlObjects.size()>0)
        {
//        	since sorted by rsUpdatedAt looking at the last record updated at is enough			
        	def eventLastUpdatedAt=xmlObjects[xmlObjects.size()-1].@rsUpdatedAt;
			logger.info("Saving RsLookup ${[name:eventUpdatedAtKey,value:eventLastUpdatedAt]} ")
        	RsLookup.add([name:eventUpdatedAtKey,value:eventLastUpdatedAt]);
        	
        }
        logger.info(" Processed ${xmlObjects.size()} objects after offset ${requestParams.offset}");
        
        return totalObjectCount;
}



