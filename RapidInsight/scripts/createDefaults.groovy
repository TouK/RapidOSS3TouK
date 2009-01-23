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

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 12, 2008
* Time: 10:50:03 AM
*/


def scriptsToAdd=[]
scriptsToAdd.add([name:"modelCreator"])
scriptsToAdd.add([name:"removeAll"])
scriptsToAdd.add([name:"acknowledge", enabledForAllGroups:true])
scriptsToAdd.add([name: "clearExpiredEvents", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:true,period:60,logFileOwn:true])
scriptsToAdd.add([name:"setOwnership", enabledForAllGroups:true])
scriptsToAdd.add([name:"queryList", enabledForAllGroups:true])
scriptsToAdd.add([name:"createQuery", enabledForAllGroups:true])
scriptsToAdd.add([name:"editQuery", enabledForAllGroups:true])
scriptsToAdd.add([name:"reloadOperations", enabledForAllGroups:true])
scriptsToAdd.add([name:"getViewFields", enabledForAllGroups:true])

scriptsToAdd.add([name: "emailGenerator", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:false,period:60,logFileOwn:true]);
scriptsToAdd.add([name: "emailSender", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:false,period:60,logFileOwn:true,staticParam:"connectorName:emailConnector"]);

scriptsToAdd.add([name: "importSampleRiData"]);
scriptsToAdd.add([name: "getUIHierarchy"]);
scriptsToAdd.add([name: "saveUIHierarchy"]);

scriptsToAdd.add([name:"autocomplete", enabledForAllGroups:true])
scriptsToAdd.add([name:"getHierarchy"])
scriptsToAdd.add([name:"getEventHistory", enabledForAllGroups:true])
scriptsToAdd.add([name:"getSummaryData", enabledForAllGroups:true])
scriptsToAdd.add([name:"getGeocodes"])
scriptsToAdd.add([name:"getDeviceLocations", enabledForAllGroups:true])
scriptsToAdd.add([name:"getDevicesByLocation", enabledForAllGroups:true])




// topology scripts
scriptsToAdd.add([name:"createMap", enabledForAllGroups:true])
scriptsToAdd.add([name:"editMap", enabledForAllGroups:true])
scriptsToAdd.add([name:"expandMap", enabledForAllGroups:true])
scriptsToAdd.add([name:"getMap", enabledForAllGroups:true])
scriptsToAdd.add([name:"mapList", enabledForAllGroups:true])
scriptsToAdd.add([name:"saveMap", enabledForAllGroups:true])
scriptsToAdd.add([name:"getMapData", enabledForAllGroups:true])
scriptsToAdd.add([name:"createDefaultQueries"])

scriptsToAdd.each{  scriptParams ->

    try{
        CmdbScript.addScript(scriptParams)
    }
    catch(e)
    {
       Logger.getRootLogger().warn("createDefaults: Could not add script with params : ${scriptParams}.Reason:${e}",e)
    }
}


def scriptsToRun=[]
scriptsToRun.add("createDefaultQueries")

scriptsToRun.each{  scriptName ->

    try{
        CmdbScript.runScript(scriptName);
    }
    catch(e)
    {
       Logger.getRootLogger().warn("createDefaults:  Could not run script ${scriptName}.Reason:${e}",e)
    }
}




