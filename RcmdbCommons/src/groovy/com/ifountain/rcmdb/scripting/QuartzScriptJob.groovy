package com.ifountain.rcmdb.scripting

import org.apache.log4j.Logger
import org.quartz.JobExecutionContext
import org.quartz.StatefulJob
import script.CmdbScript

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
 * Date: May 27, 2008
 * Time: 3:12:55 PM
 */
class QuartzScriptJob implements StatefulJob {

    public void execute(JobExecutionContext jobExecutionContext) {
        runScript(jobExecutionContext.getJobDetail().getName());
    }
    protected void runScript(scriptName)
    {           
        Logger logger = Logger.getRootLogger();
        CmdbScript script = CmdbScript.get(name:scriptName);
        if(script == null)
        {
            logger.warn("Periodic script ${scriptName} does not exist");            
        }
        else
        {
            try
            {
                logger.debug("Running periodic script " + scriptName);                
                def result = CmdbScript.runScript(script,[:]);
                logger.info("Periodic script ${scriptName} successfuly executed.")
            }
            catch (t)
            {
                def scriptLogger=CmdbScript.getScriptLogger(script);
                scriptLogger.warn("Exception in periodic script ${scriptName}", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
            }
        }
    }

}