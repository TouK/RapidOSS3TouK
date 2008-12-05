/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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