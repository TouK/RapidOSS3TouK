package com.ifountain.rcmdb.test.util.scripting

import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.comp.test.util.logging.TestLogUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jun 26, 2009
* Time: 10:44:45 AM
* To change this template use File | Settings | File Templates.
*/
class ScriptManagerForTest {

    private static def groovyClassLoager;
    private static def scriptRunParams;
    private static def scriptBaseDirectory;
    private static def scriptClasses;

    public static final String SCRIPT_RUN_RESULT_KEY="SCRIPT_RUN_RESULT";
    public static final String WEB_RENDER_PARAMS_KEY="WEB_RENDER_PARAMS";


    static def initialize(gcl)
    {
        initialize(gcl,null);
    }
    static def initialize(gcl,scriptDirectory)
    {
        groovyClassLoager=gcl;
        scriptRunParams=[:];
        scriptClasses=[:];
        scriptBaseDirectory=scriptDirectory;

    }

    public static def runScriptWithWeb(String scriptFilePath, Map scriptParams, webMock)
    {
        scriptParams.web=webMock;
        return  runScript(scriptFilePath,scriptParams);
    }
    public static def runScript(String scriptFilePath, Map scriptParams)
    {
        def scriptClass = scriptClasses[scriptFilePath];
        if(scriptClass == null )
        {
            throw new Exception("Script ${scriptFilePath} should be added first");
        }

        def scriptInstance = scriptClass.newInstance();
        def params = new HashMap(scriptParams);
        scriptParams.each{ propName, propVal ->
            scriptInstance.setProperty(propName, propVal);
        }
        scriptInstance.setProperty(RapidCMDBConstants.LOGGER, TestLogUtils.log);

        def result=scriptInstance.run();
        addEntryForScript(scriptFilePath,"runResult",result)
        return result;
    }
    public static def getScriptRunResultForScript(scriptFilePath)
    {
        return scriptRunParams[scriptFilePath][SCRIPT_RUN_RESULT_KEY];
    }
    public static def getScriptWebRenderParamsForScript(scriptFilePath)
    {
        return scriptRunParams[scriptFilePath][WEB_RENDER_PARAMS_KEY]; 
    }
    private static void addEntryForScript(scriptFilePath,entry,value)
        {
           if(!scriptRunParams.containsKey(scriptFilePath))
           {
              scriptRunParams[scriptFilePath]=[:];
           }
           scriptRunParams[scriptFilePath][entry]=value;
        }

    public static def getDefaultWebForScripts(String scriptFilePath,username)
    {
        def web = [
                    session: [username: username],
                    render: {Map renderParams ->
                        addEntryForScript(scriptFilePath,"webRenderParams",renderParams.clone())
                    }
        ]
    }

    public static Class addScript(String scriptFilePath)
    {
        def scriptRealPath=null;
        def scriptClassLoader = new GroovyClassLoader(groovyClassLoager);
        if(scriptBaseDirectory == null)
        {
           scriptRealPath="${scriptFilePath}.groovy";
        }
        else
        {
           scriptRealPath="${scriptBaseDirectory}/${scriptFilePath}.groovy";
           scriptClassLoader.addClasspath(scriptBaseDirectory);
        }

        def scriptClass = scriptClassLoader.parseClass(new File(scriptRealPath));
        scriptClasses[scriptFilePath]=scriptClass;
        return scriptClass;
    }




}