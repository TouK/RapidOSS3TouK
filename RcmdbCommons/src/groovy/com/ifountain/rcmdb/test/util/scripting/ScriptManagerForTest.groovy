package com.ifountain.rcmdb.test.util.scripting

import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.rcmdb.util.RapidCMDBConstants

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jun 26, 2009
* Time: 10:44:45 AM
* To change this template use File | Settings | File Templates.
*/
class ScriptManagerForTest {

    private static def groovyClassLoager;
    private static def scriptBaseDirectory;
    private static def scriptClasses;

    static def initialize(gcl)
    {
        initialize(gcl, null);
    }
    static def initialize(gcl, scriptDirectory)
    {
        groovyClassLoager = gcl;
        scriptClasses = [:];
        scriptBaseDirectory = scriptDirectory;

    }

    public static def runScriptWithWeb(String scriptFilePath, Map scriptParams, webMock)
    {
        scriptParams.web = webMock;
        return runScript(scriptFilePath, scriptParams);
    }
    public static def runScript(String scriptName, Map scriptParams)
    {
        def scriptInstance = getScriptInstance(scriptName, scriptParams);
        return scriptInstance.run();
    }

    public static def getScriptInstance(String scriptName, Map scriptParams)
    {
        def scriptClass = scriptClasses[scriptName];
        if (scriptClass == null)
        {
            throw new Exception("Script ${scriptName} should be added first");
        }

        def scriptInstance = scriptClass.newInstance();
        scriptInstance.setProperty(RapidCMDBConstants.LOGGER, TestLogUtils.log);
        scriptInstance.setProperty("params", [:]);
        scriptParams.each {propName, propVal ->
            scriptInstance.setProperty(propName, propVal);
        }
        return scriptInstance;
    }


    public static Class addScript(String scriptFilePath)
    {
        def scriptRealPath = null;
        def scriptClassLoader = new GroovyClassLoader(groovyClassLoager);
        if (scriptBaseDirectory == null)
        {
            scriptRealPath = "${scriptFilePath}.groovy";
        }
        else
        {
            scriptRealPath = "${scriptBaseDirectory}/${scriptFilePath}.groovy";
            scriptClassLoader.addClasspath(scriptBaseDirectory);
        }

        def scriptClass = scriptClassLoader.parseClass(new File(scriptRealPath));
        scriptClasses[scriptFilePath] = scriptClass;
        return scriptClass;
    }

}
