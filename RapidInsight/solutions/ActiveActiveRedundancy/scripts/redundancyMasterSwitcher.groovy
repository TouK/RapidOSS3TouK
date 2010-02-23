import datasource.HttpDatasource;

OUTPUT="";
logger.warn("---------------------------------------------------")
logWarn("redundancyMasterSwitcher starts")

def localIsMasterLookup=RedundancyLookup.get(name:"isMaster");
localIsMaster=false;
if(localIsMasterLookup?.value =="true")
{
    localIsMaster=true;
}

if(!localIsMaster)
{
    logWarn("Local is not master : checking whether there is a remote master");

    def datasources=HttpDatasource.searchEvery("name:redundancy*");
    if(datasources.size()==0)
    {
        logWarn("Error : no redundancy server is defined, will not make any changes to local Master status");
        return OUTPUT;
    }

    def remoteMasterDs=getRemoteMasterDs(datasources);
    if(remoteMasterDs == null)
    {
        logWarn("Not found any remote master, will make Local Server master");
        makeLocalMaster();
    }
    else
    {
        logWarn("Found Remote master ${remoteMasterDs.name}, Local Server will stay as slave");
        if(localIsMasterLookup==null)
        {
            makeLocalSlave();
        }
        syncWithRemoteMaster(remoteMasterDs);
    }
}
else
{
    logWarn("Local is master : will not check remote servers ");
}


logWarn("Ended redundancyMasterSwitcher");

return OUTPUT;

def makeLocalMaster()
{
    logger.warn("Making local server master");
    script.CmdbScript.runScript("enableLocalMaster",[:]);
    logger.warn("Making local server master done");    
}
def makeLocalSlave()
{
    logger.warn("Making local server slave");
    script.CmdbScript.runScript("disableLocalMaster",[:]);
    logger.warn("Making local server slave done");
}
def syncWithRemoteMaster(ds)
{
    logger.warn("Synchronization with Master ${ds.name} starts");

    def requestParams=[:];
	requestParams.login="rsadmin";
	requestParams.password="changeme";
	requestParams.format="xml";
	requestParams.sort="rsUpdatedAt";
	requestParams.order="asc";
	requestParams.searchIn="RsLookup";
	requestParams.max="10";
	requestParams.query="name:messageGenerator*";

    def searchUrl="script/run/updatedObjects";

    def xmlResult="";
    try{
        xmlResult=ds.doRequest(searchUrl,requestParams);
    }
    catch(e)
    {
        logWarn("Error : Could not Synchronize with Master ${ds.name}: Remote server ${ds.name} is not accessible. Reason ${e}");
        return;
    }

    if(xmlResult.indexOf("<Errors>")>=0)
    {
        logWarn(" Xml Error : Could not Synchronize with Master ${ds.name}: Master response From ${ds.name} : ${xmlResult.toString()}");
        return;
    }

    def xmlRoot=new XmlSlurper().parseText(xmlResult);
    def xmlObjects=xmlRoot.Object;
    xmlObjects.each{ xmlObject ->
        def props=[:];
        props.putAll(xmlObject.attributes());
        logger.debug("Adding RsLookup from Master with props  : ${props}")
        RsLookup.add(props);
    }

    logger.warn("Synchronization with Master ${ds.name} ends");
    
}

def getRemoteMasterDs(datasources)
{
    def masterDs=null;
    //For each server , ask if it is remote
    datasources.each{ ds ->    
        if(masterDs==null && isRemoteServerMaster(ds))
        {
            logWarn("Remote ${ds.name} will be treated as master");
            masterDs=ds;
        }
        else
        {
            logWarn("Remote ${ds.name} will be treated as slave");            
        }
    }
    return masterDs;
}

def isRemoteServerMaster(ds)
{
    logger.info("Checking whether ${ds.name} isMaster *******");
    
    def requestParams=[:];
	requestParams.login="rsadmin";
	requestParams.password="changeme";
	requestParams.format="xml";
	requestParams.sort="rsUpdatedAt";
	requestParams.order="asc";
	requestParams.searchIn="RedundancyLookup";
	requestParams.max="1";
	requestParams.query="name:isMaster";

    def searchUrl="script/run/updatedObjects";
    def xmlResult="";
    try{
        xmlResult=ds.doRequest(searchUrl,requestParams);
    }
    catch(e)
    {
        logWarn("Error: Remote server ${ds.name} is not accessible. Reason ${e}");
        return false;
    }

    if(xmlResult.indexOf("<Errors>")>=0)
    {
        logWarn(" Xml Error : Master  response From ${ds.name} : ${xmlResult.toString()}");        
        return false;
    }

    def isRemoteMaster=false;
    
    def xmlRoot=new XmlSlurper().parseText(xmlResult);
    def xmlObjects=xmlRoot.Object;
    xmlObjects.each{ xmlObject ->
        def props=[:];
        props.putAll(xmlObject.attributes());
        if(props.value == "true")
        {
            isRemoteMaster=true;
            logger.info("Remote server ${ds.name} isMaster");
        }
    }
    if(!isRemoteMaster)
    {
        logger.info("Remote server ${ds.name} is not Master");
    }

    return isRemoteMaster;
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