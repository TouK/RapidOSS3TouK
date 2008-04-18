package scripting;
import com.ifountain.exceptions.scripting.ScriptingException;
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import script.CmdbScript
import org.apache.commons.io.filefilter.DirectoryFileFilter
import org.apache.commons.io.filefilter.SuffixFileFilter
import org.apache.commons.lang.StringUtils
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Apr 17, 2008
* Time: 10:36:49 AM
* To change this template use File | Settings | File Templates.
*/
class ScriptingService implements InitializingBean, DisposableBean{
    def scripts;
    def classLoader;
    def baseDirectory;
    public static final String SCRIPT_DIRECTORY = "scripts";
    def addScript (String scriptPath)   throws ScriptingException
    {
        scriptPath = StringUtils.substringBefore (scriptPath, ".groovy")
        scripts[scriptPath] = getScriptClass(scriptPath);
    }


    def getScript(String scriptPath)
    {
        scriptPath = StringUtils.substringBefore (scriptPath, ".groovy")
        return scripts[scriptPath];
    }

    private Class getScriptClass(String scriptPath) throws ScriptingException
    {
        scriptPath = StringUtils.substringBefore (scriptPath, ".groovy")
        def scriptClassLoader = new GroovyClassLoader(classLoader);
        scriptClassLoader.addClasspath (baseDirectory + "/${SCRIPT_DIRECTORY}");
        try
        {
            return scriptClassLoader.loadClass (scriptPath);
        }
        catch(Throwable t)
        {
             throw ScriptingException.compileScriptException(scriptPath, t);
        }
    }

    def checkScript (String scriptPath)  throws ScriptingException
    {
        getScriptClass (scriptPath);
    }

    def reloadScript (String scriptPath)  throws ScriptingException
    {
        addScript(scriptPath);
    }

    def runScript(scriptPath, bindings)  throws ScriptingException
    {
        scriptPath = StringUtils.substringBefore (scriptPath, ".groovy")
        def scriptClass = scripts[scriptPath];
        if(scriptClass)
        {
            def scriptObject = scriptClass.newInstance();
            bindings.each{propName, propValue->
                scriptObject.setProperty(propName, propValue);
            }
            try
            {
                return scriptObject.run();
            }
            catch(Throwable exception)
            {
                StackTraceElement[] elements = exception.getStackTrace();
                def lineNumber = -1;
                for(element in elements)
                {
                    if(element.getClassName() == scriptClass.getName())
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

    public void afterPropertiesSet() {
        scripts = [:];
        classLoader = ApplicationHolder.application.classLoader;
        baseDirectory = System.getProperty("base.dir");
        File scriptDirFile = new File(baseDirectory + "/${SCRIPT_DIRECTORY}");
        def scriptFiles = FileUtils.listFiles (scriptDirFile, ["groovy"] as String[], false);
        scriptFiles.each{script->
            try
            {
                addScript (script.name);
            }
            catch(t)
            {
//                log.warn("Could not load script ${script.name}", t);
            }
        }
    }

    public void destroy() {
        scripts.clear();
    }

    
}