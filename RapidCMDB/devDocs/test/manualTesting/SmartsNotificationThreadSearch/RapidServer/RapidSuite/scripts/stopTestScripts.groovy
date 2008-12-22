import script.CmdbScript

def scriptsToStop=[]

scriptsToStop.each{ scriptName ->
    def script=CmdbScript.get(name:scriptName);
    CmdbScript.updateScript(script,[enabled:false],false);
}