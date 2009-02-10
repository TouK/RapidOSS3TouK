package com.ifountain.rcmdb.scripting

import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import script.CmdbScript
import org.apache.log4j.Logger

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: May 20, 2008
 * Time: 2:07:57 PM
 */
public class ScriptManager {
    Logger logger = Logger.getLogger("scripting");
    public static final String SCRIPT_DIRECTORY = "scripts";
    private static ScriptManager manager;
    private def scripts;
    private def classLoader;
    private def baseDirectory;
    private Map defaultSupportedMethods;
    private ScriptManager() {
    }

    public static ScriptManager getInstance() {
        if (manager == null) {
            manager = new ScriptManager();
        }
        return manager;
    }
    public static void destroyInstance() {
        if (manager != null) {
            manager.destroy();
            manager = null;
        }
    }
    public void initialize(ClassLoader parentClassLoader, String baseDir, List startupScriptList, Map defaultSupportedMethods) {
        scripts = [:];
        this.defaultSupportedMethods = defaultSupportedMethods;
        classLoader = parentClassLoader;
        baseDirectory = baseDir;
        File scriptDirFile = new File(baseDirectory + "/${SCRIPT_DIRECTORY}");
        scriptDirFile.mkdirs();
        def scriptFiles = FileUtils.listFiles(scriptDirFile, ["groovy"] as String[], false);
        scriptFiles.each {script ->
            try
            {
                addScript(script.name);
            }
            catch (t)
            {}
        }
        startupScriptList.each{
            try
            {
                addScript(it);
                runScript(it, [:],Logger.getRootLogger());
            }
            catch (Throwable t)
            {
                logger.warn ("An exception occurred while executing startup script ${it}.", t);
            }

        }
    }
    def addScript(String scriptPath) throws ScriptingException
    {
        if(getScript(scriptPath) == null)
        {
            _addScript(scriptPath);
        }
    }
    private def _addScript(String scriptPath) throws ScriptingException
    {
        scriptPath = getScriptPath(scriptPath)
        scripts[scriptPath] = getScriptClass(scriptPath);
    }

    def removeScript(String scriptPath)
    {           
        scripts.remove(getScriptPath(scriptPath))
    }


    def getScript(String scriptPath)
    {           
        return scripts[getScriptPath(scriptPath)];
    }

    private def getScriptPath(String scriptPath)
    {
       return StringUtils.substringBefore(scriptPath, ".groovy");     
    }
    private Class getScriptClass(String scriptPath) throws ScriptingException
    {
        scriptPath = getScriptPath(scriptPath)
        def scriptClassLoader = new GroovyClassLoader(classLoader);
        scriptClassLoader.addClasspath(baseDirectory + "/${SCRIPT_DIRECTORY}");
        try
        {
            Class cls = scriptClassLoader.loadClass(scriptPath);

            defaultSupportedMethods.each{String methodName, methodClosure->
                cls.metaClass."${methodName}" = methodClosure;                
            }
            
            cls.metaClass.operationInstance=null;
            cls.metaClass.methodMissing = {String name, args ->
                  def oprInstance=delegate.getProperty("operationInstance");
                  if(oprInstance!=null)
                  {
                    oprInstance.domainObject=delegate;
                    try {
                        return oprInstance.invokeMethod(name, args)
                    }
                    catch (MissingMethodException e) {
                        if (e.getType().name != oprInstance.class.name || e.getMethod() != name)
                        {
                            throw e;
                        }
                    }
                     
                  }
                  throw new MissingMethodException(name, delegate.metaClass.theClass, args);
            }

            
            return cls;
        }
        catch (Throwable t)
        {
            throw ScriptingException.compileScriptException(scriptPath, t);
        }
    }

    def checkScript(String scriptPath) throws ScriptingException
    {
        getScriptClass(scriptPath);
    }

    def reloadScript(String scriptPath) throws ScriptingException
    {
        _addScript(scriptPath);
    }
    def runScript(scriptPath, bindings,scriptLogger) throws ScriptingException
    {
        return runScript(scriptPath,bindings,scriptLogger,null);
    }
    def runScript(scriptPath, bindings,scriptLogger,operationClass) throws ScriptingException
    {
        def scriptObject = getScriptObject(scriptPath,bindings,scriptLogger,operationClass);        
        def scriptClass = getScript(scriptPath);
        try
        {

            def result = scriptObject.run();
            return result;
        }
        catch (Throwable exception)
        {
            StackTraceElement[] elements = exception.getStackTrace();
            def lineNumber = -1;
            for (element in elements)
            {
                if (element.getClassName() == scriptClass.getName())
                {
                    lineNumber = element.getLineNumber();
                    break;
                }
            }
            throw ScriptingException.runScriptException(scriptPath, lineNumber, exception);
        }
        

    }

    public def getScriptObject(scriptPath,bindings,scriptLogger,operationClass){

        def scriptClass = getScript(scriptPath);
        if (scriptClass)
        {
            def scriptObject=scriptClass.newInstance();
            bindings.each {propName, propValue ->
                scriptObject.setProperty(propName, propValue);
            }
            scriptObject.setProperty("logger", scriptLogger);
            if(operationClass!=null && operationClass!="")
            {
                scriptObject.setProperty ("operationInstance",operationClass.newInstance())
            }
            return scriptObject;            
        }
        else
        {
            throw ScriptingException.scriptDoesnotExist(scriptPath);
        }
    }

    private void destroy() {
        clearScripts();
    }

    public void clearScripts(){
       if(scripts!=null)
       {
            scripts.clear();
       }
    }

    public void setClassLoader(classLoader){
        this.classLoader = classLoader;
    }

    public void setBaseDirectory(directory){
        this.baseDirectory = directory;
    }

}