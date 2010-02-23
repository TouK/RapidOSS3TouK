import script.CmdbScript;

OUTPUT="";
logger.info("---------------------------------------------------")
logWarn("enableLocalMaster starts")

logWarn("Saving isMaster : true to RedundancyLookup");
RedundancyLookup.add(name:"isMaster",value:"true");

def scriptsToUpdate=[];
scriptsToUpdate.add(CmdbScript.get(name:"messageGenerator"));
connector.NotificationConnector.list().each{ notificationConnector ->
    scriptsToUpdate.add(notificationConnector.getScript());
}

scriptsToUpdate.each{ script ->
    if(script!=null)
    {
        logWarn("Enabling script ${script.name}");
        CmdbScript.updateScript(script,[enabled:true],false);
    }
}


logWarn("enableLocalMaster ends")


def logWarn(message)
{
   logger.warn(message);
   OUTPUT += "WARN : ${message} <br>";
}