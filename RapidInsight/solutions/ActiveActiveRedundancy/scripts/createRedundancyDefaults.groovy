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
import script.CmdbScript
import org.apache.log4j.Logger


def scriptsToAdd=[]
 

                  
//connector and connection scripts
scriptsToAdd.add([name:"createRedundancyConnections",logFileOwn:true])


//synchronization scripts
scriptsToAdd.add([name:"updatedObjects", logFileOwn:true]);
scriptsToAdd.add([name:"synchronizeUpdatedObjects", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:true,period:60,logFileOwn:true]);
scriptsToAdd.add([name:"synchronizeDeletedObjects", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:true,period:60,startDelay:15,logFileOwn:true]);
scriptsToAdd.add([name:"clearExpiredDeletedObjects", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:true,period:3600*12,logFileOwn:true]);

//master switching scripts
scriptsToAdd.add([name:"enableLocalMaster", logFileOwn:true]);
scriptsToAdd.add([name:"disableLocalMaster", logFileOwn:true]);
scriptsToAdd.add([name:"redundancyMasterSwitcher", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:true,period:60,logFileOwn:true]);


scriptsToAdd.each{  scriptParams ->

    try{
        CmdbScript.addUniqueScript(scriptParams)
    }
    catch(e)
    {
       Logger.getRootLogger().warn("createDefaults: Could not add script with params : ${scriptParams}.Reason:${e}")
    }
}


def scriptsToRun=["disableLocalMaster"]

scriptsToRun.each{  scriptName ->

    try{
        CmdbScript.runScript(scriptName);
    }
    catch(e)
    {
       logger.warn("createDefaults:  Could not run script ${scriptName}.Reason:${e}",e)
    }
}

                        