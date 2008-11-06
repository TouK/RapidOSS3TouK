package com.ifountain.rcmdb.scripting

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FalseFileFilter
import org.apache.commons.io.filefilter.SuffixFileFilter
import org.apache.log4j.Logger
import org.apache.commons.lang.StringUtils


/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 6, 2008
 * Time: 5:34:06 PM
 * To change this template use File | Settings | File Templates.
 */
class ScriptingUtils {
    public static String STARTUP_SCRIPT_DIR = "grails-app/conf"
    static Logger logger = Logger.getLogger("scripting");
    public static List getStartupScriptList(String baseDir, ClassLoader classLoader)
    {
        def confDirFile = new File(baseDir + "/"+STARTUP_SCRIPT_DIR);
        if(!confDirFile.exists()) return [];
        def startupScriptConfigFiles = FileUtils.listFiles(confDirFile, new SuffixFileFilter("StartupScripts.groovy"), new FalseFileFilter());
        def allScriptList = [];
        startupScriptConfigFiles.each{File startupScriptFile->
            try
            {
                def startupScriptClass = classLoader.loadClass(StringUtils.substringBefore(startupScriptFile.getName(), "."));
                def stringList = startupScriptClass.scripts;
                allScriptList.addAll(stringList);
            }
            catch(Throwable t)
            {
                logger.warn ("Could not get startup script from file ${startupScriptFile.absolutePath}", t);
            }
        }
        return allScriptList;
    }
}