import script.CmdbScript

com.ifountain.rcmdb.util.DataStore.put("SmartsNotificationThreadSearchStop",new Object())

try {
    CmdbScript.stopListening(connector.SmartsConnector.get(name:"smnot").ds.listeningScript);
}
catch(e)
{
    logger.warn("Exception occured while stopping listening script smnot .Reason : ${e}");
}

def scriptsToStop=[]

scriptsToStop.each{ scriptName ->
    def script=CmdbScript.get(name:scriptName);
    CmdbScript.updateScript(script,[enabled:false],false);
}