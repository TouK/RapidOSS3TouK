import script.CmdbScript





def scriptsToAdd=[]
scriptsToAdd.add([name:"addEventRemote",logFileOwn:true])
scriptsToAdd.add([name:"clearEventRemote",logFileOwn:true])
scriptsToAdd.add([name:"createSampleServices", logFileOwn:true])
scriptsToAdd.add([name:"createSampleEvents", logFileOwn:true])

scriptsToAdd.each{  scriptParams ->

    try{
        CmdbScript.addUniqueScript(scriptParams)
    }
    catch(e)
    {
       logger.warn("createDefaults: Could not add script ${scriptParams.name}. Reason:${e}")
    }
}




