package com.ifountain.rcmdb.scripting

import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rcmdb.exception.scripting.ScriptingException
import org.apache.commons.lang.StringUtils
import org.apache.commons.io.FileUtils

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
class ScriptManager {
    public static final String SCRIPT_DIRECTORY = "scripts";
    private static ScriptManager manager;
    private def scripts;
    private def classLoader;
    private def baseDirectory;
    private ScriptManager() {
    }

    public static ScriptManager getInstance() {
        if (manager == null) {
            manager = new ScriptManager();
        }
        return manager;
    }

    public void initialize() {
        scripts = [:];
        classLoader = ApplicationHolder.application.classLoader;
        baseDirectory = System.getProperty("base.dir");
        File scriptDirFile = new File(baseDirectory + "/${SCRIPT_DIRECTORY}");
        def scriptFiles = FileUtils.listFiles(scriptDirFile, ["groovy"] as String[], false);
        scriptFiles.each {script ->
            try
            {
                addScript(script.name);
            }
            catch (t)
            {}
        }
    }
    def addScript(String scriptPath) throws ScriptingException
    {
        scriptPath = StringUtils.substringBefore(scriptPath, ".groovy")
        scripts[scriptPath] = getScriptClass(scriptPath);
    }


    def getScript(String scriptPath)
    {
        scriptPath = StringUtils.substringBefore(scriptPath, ".groovy")
        return scripts[scriptPath];
    }

    private Class getScriptClass(String scriptPath) throws ScriptingException
    {
        scriptPath = StringUtils.substringBefore(scriptPath, ".groovy")
        def scriptClassLoader = new GroovyClassLoader(classLoader);
        scriptClassLoader.addClasspath(baseDirectory + "/${SCRIPT_DIRECTORY}");
        try
        {
            return scriptClassLoader.loadClass(scriptPath);
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
        addScript(scriptPath);
    }

    def runScript(scriptPath, bindings) throws ScriptingException
    {
        scriptPath = StringUtils.substringBefore(scriptPath, ".groovy")
        def scriptClass = scripts[scriptPath];
        if (scriptClass)
        {

            def scriptObject = scriptClass.newInstance();
            bindings.each {propName, propValue ->
                scriptObject.setProperty(propName, propValue);
            }
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
        else
        {
            throw ScriptingException.scriptDoesnotExist(scriptPath);
        }
    }

    public void destroy() {
        clearScripts();
    }

    public void clearScripts(){
       scripts.clear(); 
    }

    public void setClassLoader(classLoader){
        this.classLoader = classLoader;
    }

    public void setBaseDirectory(directory){
        this.baseDirectory = directory;
    }

}