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


/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 12, 2008
* Time: 10:50:03 AM
*/

message.RsMessageRule.cacheConnectorDestinationNames();


def scriptsToAdd=[]
scriptsToAdd.add([name:"modelCreator",logFileOwn:true])
scriptsToAdd.add([name:"removeAll"])
scriptsToAdd.add([name:"scriptTestRunner", logFileOwn:true])
scriptsToAdd.add([name:"getTimeRangeButtonConfiguration", logFileOwn:true, enabledForAllGroups:true])
scriptsToAdd.add([name:"getTimeRangeData", logFileOwn:true, enabledForAllGroups:true])
scriptsToAdd.add([name:"acknowledge", enabledForAllGroups:true])
scriptsToAdd.add([name:"setOwnership", enabledForAllGroups:true])
scriptsToAdd.add([name:"queryList", enabledForAllGroups:true])
scriptsToAdd.add([name:"queryFormHelper", enabledForAllGroups:true])
scriptsToAdd.add([name:"reloadOperations", enabledForAllGroups:true])
scriptsToAdd.add([name:"getViewFields", enabledForAllGroups:true])

scriptsToAdd.add([name: "markModifications", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:true,period:3600*3,logFileOwn:true]);
scriptsToAdd.add([name: "getModifications", logFileOwn:true]);
scriptsToAdd.add([name: "markMessageForResend"])
scriptsToAdd.add([name: "clearNotificationMessages", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.CRON,enabled:true,cronExpression:'0 30 0 * * ?',logFileOwn:true])
scriptsToAdd.add([name: "messageGenerator", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:false,period:60,logFileOwn:true]);

scriptsToAdd.add([name: "importSampleRiData"]);
scriptsToAdd.add([name: "executeShell", enabledForAllGroups:true]);

scriptsToAdd.add([name:"autocomplete", enabledForAllGroups:true])
scriptsToAdd.add([name:"getHierarchy", enabledForAllGroups:true])
scriptsToAdd.add([name:"getEventHistory", enabledForAllGroups:true])
scriptsToAdd.add([name:"getSummaryData", enabledForAllGroups:true])
scriptsToAdd.add([name:"getGeocodes", logFileOwn:true])
scriptsToAdd.add([name:"getDeviceLocations", enabledForAllGroups:true])
scriptsToAdd.add([name:"getDevicesByLocation", enabledForAllGroups:true])
scriptsToAdd.add([name:"getClassesForSearch", enabledForAllGroups:true])

// topology scripts
scriptsToAdd.add([name:"expandMap", enabledForAllGroups:true])
scriptsToAdd.add([name:"getMapData", enabledForAllGroups:true])
scriptsToAdd.add([name:"getMapNodeEvents", enabledForAllGroups:true])
scriptsToAdd.add([name:"createDefaultQueries"])

//event management scripts
scriptsToAdd.add([name:"saveHistoricalEventCache", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:true,period:60,logFileOwn:true]);
scriptsToAdd.add([name:"clearHistoricalEvents", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.CRON,enabled:true,cronExpression:'0 0 0 * * ?',logFileOwn:true])
scriptsToAdd.add([name:"clearExpiredEvents", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:true,period:60,logFileOwn:true])

//instrumentation & ROSS management scripts
scriptsToAdd.add([name:"createInstrumentationParameters"])
scriptsToAdd.add([name:"enableInstrumentation"])
scriptsToAdd.add([name:"disableInstrumentation"])
scriptsToAdd.add([name:"memorySummarizer", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:true,period:60,logFileOwn:true]);
scriptsToAdd.add([name:"memoryHistogram", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:false,period:300,logFileOwn:true]);
scriptsToAdd.add([name:"threadLister", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.PERIODIC,enabled:false,period:300,logFileOwn:true]);
scriptsToAdd.add([name:"memoryDump"])
scriptsToAdd.add([name:"getCompassStatistics"])
scriptsToAdd.add([name:"resetCompassStatistics"])
scriptsToAdd.add([name:"backup", type:CmdbScript.SCHEDULED, scheduleType:CmdbScript.CRON,enabled:false,cronExpression:'0 0 0 * * ?',logFileOwn:true]);

scriptsToAdd.each{  scriptParams ->

    try{
        CmdbScript.addUniqueScript(scriptParams)
    }
    catch(e)
    {
       logger.warn("createDefaults: Could not add script ${scriptParams.name}. Reason:${e}")
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
       logger.warn("createDefaults:  Could not run script ${scriptName}.Reason:${e}",e)
    }
}




