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

def smartsConnectionTemplate = SmartsConnectionTemplate.add(name:"smartscon",broker:"192.168.1.100:426",brokerUsername:"SecureBroker",brokerPassWord:"Secure",username:"admin",password:"changeme")
if(smartsConnectionTemplate.hasErrors()) {
    logger.warn("Can not create smartsConnectionTemplate for test. Reason : ${smartsConnectionTemplate.errors}");
    smartsConnectionTemplate.remove();    
}
else{
    logger.warn("Created smartsConnectionTemplate for test");
    def smartsConnector = SmartsListeningNotificationConnector.add(name:"smnot",connectionTemplate:smartsConnectionTemplate,notificationList:"ALL_NOTIFICATIONS",tailMode:false)
    if(smartsConnector.hasErrors())
    {
        logger.warn("Can not create smartsConnector for test. Reason : ${smartsConnector.errors}");
        smartsConnector.remove();
        smartsConnectionTemplate.remove();
    }
    else
    {
        logger.warn("Created smartsConnector for test");
        def connectionName = smartsConnector.getConnectionName(smartsConnector.name);
        def connectionParams = [name:connectionName];
        connectionParams.broker = smartsConnector.connectionTemplate.broker
        connectionParams.username = smartsConnector.connectionTemplate.username
        connectionParams.userPassword = smartsConnector.connectionTemplate.password
        connectionParams.brokerUsername = smartsConnector.connectionTemplate.brokerUsername
        connectionParams.brokerPassword = smartsConnector.connectionTemplate.brokerPassword
        connectionParams.domain = "INCHARGE-SA"
        connectionParams.domainType = "SAM"
        SmartsConnection smartsConnection = SmartsConnection.add(connectionParams)

        if (smartsConnection.hasErrors()) {
            logger.warn("Can not create smartsConnection for test. Reason : ${smartsConnection.errors}");
            smartsConnection.remove();
            smartsConnector.remove();
            smartsConnectionTemplate.remove();
        }
        else{
            logger.warn("Created smartsConnection for test");
            def scriptName = smartsConnector.getScriptName(smartsConnector.name);
            def scriptFile = "notificationSubscriber";
            def staticParam="notificationList:${smartsConnector.notificationList},tailMode:${String.valueOf(smartsConnector.tailMode)}";
            def scriptParams=[name:scriptName, scriptFile:scriptFile,type:CmdbScript.LISTENING,logFile:smartsConnector.name,logLevel:org.apache.log4j.Level.DEBUG.toString(),staticParam:staticParam,logFileOwn:true]
            
            CmdbScript script = CmdbScript.addScript(scriptParams, true);
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
                def datasourceName = smartsConnector.getDatasourceName(smartsConnector.name);
                def datasource = SmartsNotificationDatasource.add(name: datasourceName, connection: smartsConnection, listeningScript:script,reconnectInterval:3);
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
                }


            }
        }
    }
}



