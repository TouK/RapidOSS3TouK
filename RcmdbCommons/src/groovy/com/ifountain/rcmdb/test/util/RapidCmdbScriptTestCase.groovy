package com.ifountain.rcmdb.test.util

import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 7, 2009
* Time: 2:59:49 PM
* To change this template use File | Settings | File Templates.
*/
class RapidCmdbScriptTestCase extends RapidCmdbWithCompassTestCase{
    Class scriptClass;
    Script scriptInstance;
    public Script loadScript(String path, Map properties = [:])
    {
        scriptInstance = ScriptManagerForTest.getScriptInstance(path, properties);
        return scriptInstance;
    }

    def runScript(String scriptName, Map scriptParams)
    {
        return ScriptManagerForTest.runScript (scriptName, scriptParams);        
    }
    def runScriptWithWeb(String scriptName, Map scriptParams, webMock)
    {
        return ScriptManagerForTest.runScriptWithWeb (scriptName, scriptParams, webMock);
    }

    void initializeScriptManager(String scriptDir)
    {
        def startWithSep = scriptDir.startsWith ("/")|| scriptDir.startsWith ("\\");
        if(!startWithSep)
        {
            scriptDir = "/"+scriptDir;    
        }
        def base_directory = "${getWorkspacePath()}${scriptDir}";
        println "base path is :"+new File(base_directory).getCanonicalPath();
        ScriptManagerForTest.initialize (gcl,base_directory);
    }

    def addScript(String scripName)
    {
        ScriptManagerForTest.addScript(scripName);
    }
}