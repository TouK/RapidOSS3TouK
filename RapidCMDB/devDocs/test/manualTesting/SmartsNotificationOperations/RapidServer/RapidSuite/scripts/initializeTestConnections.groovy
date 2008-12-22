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
import connection.SmartsConnection;
import connection.SmartsConnectionTemplate;
import connector.SmartsListeningNotificationConnector;
import script.CmdbScript;
import datasource.SmartsNotificationDatasource;
import com.ifountain.core.connection.ConnectionManager;
import connection.HttpConnection;
import datasource.HttpDatasource;

// Parameter definitions for smarts Notification Connector
def smartsConnectionTemplateParams=[:]
smartsConnectionTemplateParams.name="smartscon"
smartsConnectionTemplateParams.broker="192.168.1.100:426"
smartsConnectionTemplateParams.brokerUsername="SecureBroker"
smartsConnectionTemplateParams.brokerPassword="Secure"
smartsConnectionTemplateParams.username="admin"
smartsConnectionTemplateParams.password="changeme"

def smartsConnectorParams=[:]
smartsConnectorParams.name="smnot"
smartsConnectorParams.notificationList="ALL_NOTIFICATIONS"
smartsConnectorParams.tailMode=false

def smartsConnectionParams=[:]
smartsConnectionParams.domain = "INCHARGE-SA"
smartsConnectionParams.domainType = "SAM"

def smartsScriptParams=[:]
smartsScriptParams.scriptFile="notificationSubscriber"
smartsScriptParams.logLevel=org.apache.log4j.Level.DEBUG.toString()
smartsScriptParams.logFileOwn=true

def smartsDatasourceParams=[:]
smartsDatasourceParams.reconnectInterval=3

//Parameter definitions for http datasource to localhost for search

def httpConnectionParams=[:]
httpConnectionParams.name="localhttpcon"
httpConnectionParams.baseUrl="http://localhost:12222/RapidSuite/"

def httpDatasourceParams=[:]
httpDatasourceParams.name="localhttpds"

def logLevel=org.apache.log4j.Level.DEBUG.toString();
def testScriptParamsList=[]
testScriptParamsList.add([name:"notificationAdder",period:60,startDelay:0,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.PERIODIC]);
testScriptParamsList.add([name:"notificationDeleter",period:60,startDelay:15,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.PERIODIC]);
testScriptParamsList.add([name:"notificationSearcher",period:60,startDelay:30,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.PERIODIC]);
testScriptParamsList.add([name:"notificationWebSearcher",period:60,startDelay:45,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.PERIODIC]);
testScriptParamsList.add([name:"stopTestScripts",cronExpression:"0 0 7 * * ?",startDelay:0,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.CRON]);
testScriptParamsList.add([name:"garbageCollector",cronExpression:"0 0/3 7,8 * * ?",startDelay:0,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.CRON]);
testScriptParamsList.add([name:"processTestResults",cronExpression:"0 30 7 * * ?",startDelay:0,logLevel:logLevel,logFileOwn:true,scheduleType:CmdbScript.CRON]);



//script to initialize and starts smarts notification connector

def smartsConnectionTemplate = SmartsConnectionTemplate.add(smartsConnectionTemplateParams)

if(smartsConnectionTemplate.hasErrors()) {
    logger.warn("Can not create smartsConnectionTemplate for test. Reason : ${smartsConnectionTemplate.errors}");
    smartsConnectionTemplate.remove();
}
else{
    logger.warn("Created smartsConnectionTemplate for test");
    smartsConnectorParams.connectionTemplate=smartsConnectionTemplate
    def smartsConnector = SmartsListeningNotificationConnector.add(smartsConnectorParams)
    if(smartsConnector.hasErrors())
    {
        logger.warn("Can not create smartsConnector for test. Reason : ${smartsConnector.errors}");
        smartsConnector.remove();
        smartsConnectionTemplate.remove();
    }
    else
    {
        logger.warn("Created smartsConnector for test");

        smartsConnectionParams.name=smartsConnector.getConnectionName(smartsConnector.name);
        smartsConnectionParams.broker = smartsConnector.connectionTemplate.broker
        smartsConnectionParams.username = smartsConnector.connectionTemplate.username
        smartsConnectionParams.userPassword = smartsConnector.connectionTemplate.password
        smartsConnectionParams.brokerUsername = smartsConnector.connectionTemplate.brokerUsername
        smartsConnectionParams.brokerPassword = smartsConnector.connectionTemplate.brokerPassword

        SmartsConnection smartsConnection = SmartsConnection.add(smartsConnectionParams)

        if (smartsConnection.hasErrors()) {
            logger.warn("Can not create smartsConnection for test. Reason : ${smartsConnection.errors}");
            smartsConnection.remove();
            smartsConnector.remove();
            smartsConnectionTemplate.remove();
        }
        else{
            logger.warn("Created smartsConnection for test");

            def staticParam="connectorName:${smartsConnector.name}";
            
            smartsScriptParams.name=smartsConnector.getScriptName(smartsConnector.name);
            smartsScriptParams.staticParam=staticParam;
            smartsScriptParams.type=CmdbScript.LISTENING
            
            CmdbScript script = CmdbScript.addScript(smartsScriptParams, true);
            if(script.hasErrors())
            {
                logger.warn("Can not create CmdbScript for test. Reason : ${script.errors}");
                script.remove();
                smartsConnection.remove();
                smartsConnector.remove();
                smartsConnectionTemplate.remove();
            }
            else
            {
                logger.warn("created CmdbScript for test");
                smartsDatasourceParams.name=smartsConnector.getDatasourceName(smartsConnector.name);
                smartsDatasourceParams.connection=smartsConnection
                smartsDatasourceParams.listeningScript=script
                //when this is set to true bootstrap will start the listening script after running startup scripts
                smartsDatasourceParams.isSubscribed=true


                def datasource = SmartsNotificationDatasource.add(smartsDatasourceParams);
                if(datasource.hasErrors())
                {
                    logger.warn("Can not create SmartsNotificationDatasource for test. Reason : ${datasource.errors}");
                    script.remove();
                    smartsConnection.remove();
                    smartsConnector.remove();
                    smartsConnectionTemplate.remove();
                }
                else
                {
                    logger.warn("created SmartsNotificationDatasource for test");
                    smartsConnector.addRelation(ds:datasource);
                    //no need to run the script since  bootstrap will start the listening script after running startup scripts when ds is saved as isSubscribed : true
                    /*
                    Thread.start{


                        logger.warn("startin thread");

                        try{
	                        if (ConnectionManager.checkConnection(smartsConnector.ds.connection.name)) {
		                        logger.warn("gonna start listening");
	                            CmdbScript.startListening(script.name);
	                            logger.warn( "Connector ${smartsConnector.name} successfully started");
	                        }
	                        else {
	                            logger.warn("Could not connect to smarts server.")
	                        }
                    	}
                    	catch(e)
                    	{
	                    	logger.warn("Exception occured during starting listening script",e);
	                    }

                        logger.warn("endin thread");
                    }
                    */

                }


            }
        }
    }
}

//script to initialize http connection

def httpConnection=HttpConnection.add(httpConnectionParams)
if(httpConnection.hasErrors())
{
    logger.warn("Can not create httpConnection for test. Reason : ${httpConnection.errors}");
    httpConnection.remove();
}
else
{
    logger.warn("created httpConnection for test");
    httpDatasourceParams.connection=httpConnection
    def httpDatasource=HttpDatasource.add(httpDatasourceParams)
    if(httpDatasource.hasErrors())
    {
        logger.warn("Can not create httpDatasource for test. Reason : ${httpDatasource.errors}");
        httpDatasource.remove();
    }
    else
    {
        logger.warn("created httpDatasource for test");

        
    }
}

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
println "Fist Memory recorded as ${utils.TestResultsProcessor.getFirstMemory()}"