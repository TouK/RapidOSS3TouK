import script.CmdbScript




def scriptsToAdd=[]

//in maintenance
scriptsToAdd.add([name: "putInMaintenance", enabledForAllGroups:true]);
scriptsToAdd.add([name: "MaintenanceScheduler", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:true,period:60,logFileOwn:true]);




scriptsToAdd.each{  scriptParams ->

    try{
        CmdbScript.addScript(scriptParams)
    }
    catch(e)
    {
       logger.warn("createDefaults: Could not add script with params : ${scriptParams}.Reason:${e}",e)
    }
}



