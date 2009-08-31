import script.CmdbScript

def scriptsToAdd=[]
scriptsToAdd.add([name:"startFileWatchers", logFileOwn:true])
scriptsToAdd.add([name:"modificationOperation", logFileOwn:true])
scriptsToAdd.add([name:"getModificationFieldList", logFileOwn:true])
scriptsToAdd.add([name:"getActiveModifications", logFileOwn:true])
scriptsToAdd.add([name:"ignoreAllChanges", logFileOwn:true])


scriptsToAdd.each{  scriptParams ->

    try{
        CmdbScript.addUniqueScript(scriptParams)
    }
    catch(e)
    {
       logger.warn("createDefaults: Could not add script ${scriptParams.name}. Reason:${e}")
    }
}

def script = CmdbScript.get(name:"startFileWatchers");
CmdbScript.runScript(script, [:]);
