import script.CmdbScript;

OUTPUT="";
logger.info("---------------------------------------------------")
logWarn("disableLocalMaster starts")

logWarn("Saving isMaster : false to RedundancyLookup");
RsLookup.add(name:"isMaster",value:"false");

def scriptsToUpdate=[];
scriptsToUpdate.add(CmdbScript.get(name:"messageGenerator"));
connector.NotificationConnector.list().each{ notificationConnector ->
    scriptsToUpdate.add(notificationConnector.getScript());
}

scriptsToUpdate.each{ script ->
    if(script!=null)
    {
        logWarn("Disabling script ${script.name}");
        CmdbScript.updateScript(script,[enabled:false],false);
    }
}

logWarn("disableLocalMaster ends")


def logWarn(message)
{
   logger.warn(message);
   OUTPUT += "WARN : ${message} <br>";
}