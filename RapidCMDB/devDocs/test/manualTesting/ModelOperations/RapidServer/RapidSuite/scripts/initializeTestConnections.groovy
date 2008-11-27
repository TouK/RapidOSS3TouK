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
testScriptParamsList.add([name:"addInstances",period:60,startDelay:0,logLevel:logLevel,logFileOwn:true]);
testScriptParamsList.add([name:"addRelations",period:60,startDelay:15,logLevel:logLevel,logFileOwn:true]);
testScriptParamsList.add([name:"removeRelations",period:60,startDelay:30,logLevel:logLevel,logFileOwn:true]);
testScriptParamsList.add([name:"removeInstances",period:60,startDelay:45,logLevel:logLevel,logFileOwn:true]);
testScriptParamsList.add([name:"searchInstances",period:60,startDelay:50,logLevel:logLevel,logFileOwn:true]);




//script to initalize the scheduled testscripts that will run during test
//Note that addScript with enabled true will schedule the script, and then bootstrap will schedult and exception will be generated
// so we addscripts with enabled false, then update them as enabled , so only bootstrap will schedule them
for (scriptParams in testScriptParamsList)
{
    scriptParams.type=CmdbScript.SCHEDULED;
    scriptParams.scheduleType=CmdbScript.PERIODIC;
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

