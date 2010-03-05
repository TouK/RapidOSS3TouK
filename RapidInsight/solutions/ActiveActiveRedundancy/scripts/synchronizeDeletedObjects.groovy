import datasource.*;
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils

//modelName should be passed as param to this script
//other scripts should call this script with CmdbScript.runScript("synchronizeObjects", ["modelName":"SmartsNotification","logger":logger]);
//if this script wanted to be used only : comment in previous line
//modelName="SmartsNotification"

redundancyUtility=application.RapidApplication.getUtility("RedundancyUtility");

modelName="DeletedObjects";
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
	logger.warn("${modelName} : Error : no redundancy server is defined");
    return "${modelName}: Error : no redundancy server is defined";
}

//For each server , process changed objects of the model
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
	requestParams.query="rsUpdatedAt:[${modelUpdatedAt} TO *] ";



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

logInfo("completed Syncronization ${modelName}");
return OUTPUT;



def processRequest(ds,requestParams)
{
	    logger.info("Requestinq ${requestParams.searchIn} , query : ${requestParams.query} , offset:${requestParams.offset} , max : ${requestParams.max}");
	    def searchUrl="script/run/updatedObjects";


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