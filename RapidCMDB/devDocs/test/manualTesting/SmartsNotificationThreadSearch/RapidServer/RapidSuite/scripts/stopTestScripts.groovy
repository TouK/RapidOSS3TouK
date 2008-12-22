import script.CmdbScript

com.ifountain.rcmdb.util.RCMDBDataStore.put("SmartsNotificationThreadSearchStop",new Object())

def scriptsToStop=[]

scriptsToStop.each{ scriptName ->
    def script=CmdbScript.get(name:scriptName);
    CmdbScript.updateScript(script,[enabled:false],false);
}