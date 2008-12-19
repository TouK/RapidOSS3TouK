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
/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Nov 27, 2008
 * Time: 9:51:33 AM
 * To change this template use File | Settings | File Templates.
 */
import script.CmdbScript;






def logLevel=org.apache.log4j.Level.DEBUG.toString();
def testScriptParamsList=[]
testScriptParamsList.add([name:"addInstances",period:60,startDelay:0,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.PERIODIC]);
testScriptParamsList.add([name:"addRelations",period:60,startDelay:15,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.PERIODIC]);
testScriptParamsList.add([name:"removeRelations",period:60,startDelay:30,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.PERIODIC]);
testScriptParamsList.add([name:"removeInstances",period:60,startDelay:45,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.PERIODIC]);
testScriptParamsList.add([name:"searchInstances",period:60,startDelay:50,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.PERIODIC]);
testScriptParamsList.add([name:"stopTestScripts",cronExpression:"0 0 7 * * ?",startDelay:0,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.CRON]);
testScriptParamsList.add([name:"garbageCollector",cronExpression:"0 0/3 7,8 * * ?",startDelay:0,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.CRON]);
testScriptParamsList.add([name:"processTestResults",cronExpression:"0 30 7 * * ?",startDelay:0,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.CRON]);



//script to initalize the scheduled testscripts that will run during test
//Note that addScript with enabled true will schedule the script, and then bootstrap will schedult and exception will be generated
// so we addscripts with enabled false, then update them as enabled , so only bootstrap will schedule them
for (scriptParams in testScriptParamsList)
{
    scriptParams.type=CmdbScript.SCHEDULED;

    scriptParams.enabled=false;

    CmdbScript testScript = CmdbScript.addScript(scriptParams, true);
    if(testScript.hasErrors())
    {
        logger.warn("Can not create testScript ${scriptParams.name} for test. Reason : ${testScript.errors}");
        testScript.remove();
    }
    else{
        testScript.update(enabled:true);
        logger.warn("created testScript ${scriptParams.name} for test");
    }
}

utils.TestResultsProcessor.recordFirstMemory();