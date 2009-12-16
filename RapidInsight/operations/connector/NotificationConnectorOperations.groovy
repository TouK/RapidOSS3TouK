package connector

import com.ifountain.rcmdb.domain.util.ControllerUtils
import script.CmdbScript
import java.util.regex.Pattern

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Dec 14, 2009
 * Time: 4:44:27 PM
 * To change this template use File | Settings | File Templates.
 */
class NotificationConnectorOperations  extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    static def getConnectionName(String connectorName)
    {
        return "${connectorName}";
    }
    static def getDatasourceName(String connectorName)
    {
        return "${connectorName}connectorDs";
    }
    static def getScriptName(String connectorName) {
        return "${connectorName}Sender";
    }
    static def getDefaultScriptFile(String connectorType)
    {
         String firstChar=connectorType.getAt(0);
         def scriptFilePrefix=connectorType.replaceFirst(firstChar,firstChar.toLowerCase())
         return "${scriptFilePrefix}Sender".toString();
    }
    static def getDatasourceClass(String connectorType)
    {
         return application.RsApplication.getModelClass("datasource.${connectorType}Datasource".toString());
    }
    static def getConnectionClass(String connectorType)
    {
         return application.RsApplication.getModelClass("connection.${connectorType}Connection".toString());
    }


    public static Map addConnector(Map connectorParams) {
        def connectorParamsCopy = connectorParams.clone();

        def connector = NotificationConnector.addUnique(connectorParamsCopy)
        def connectionClass=getConnectionClass(connector.type);
        def datasourceClass=getDatasourceClass(connector.type);

        def createdObjects = ["connection": connectionClass.newInstance(), "datasource": datasourceClass.newInstance(),"script":new CmdbScript()];
        createdObjects["connector"] = connector;

        if (!connector.hasErrors()) {
            connectorParamsCopy.name = getConnectionName(connector.name)
            def connection = connectionClass.addUnique(connectorParamsCopy);
            createdObjects["connection"] = connection;
            if (!connection.hasErrors()) {
                def datasource = datasourceClass.addUnique(name: getDatasourceName(connector.name), connection: connection);                                                
                createdObjects["datasource"] = datasource;
                if (!datasource.hasErrors()) {
                    def scriptName = getScriptName(connector.name);
                    def staticParam = "connectorName:${connector.name}";
                    CmdbScript script = CmdbScript.addUniqueScript([name: scriptName, scriptFile: connectorParams.scriptFile, enabled: false, type: CmdbScript.SCHEDULED, period: 60, logFileOwn: true, staticParam: staticParam, logLevel: connectorParams.logLevel], true);
                    createdObjects["script"] = script;
                    if (!script.hasErrors()) {
                        connector.addRelation(ds: datasource)
                    }
                }
            }
        }
        def objectsWithError = createdObjects.values().findAll {it.hasErrors()}
        // if there is any object with error
        if (objectsWithError.size() > 0)
        {
            // we delete all the objects which does not have error
            createdObjects.each {key, object ->
                if (!object.hasErrors() && object.id != null)
                {
                    if(object.class.countHits("id:${object.id}")>0)
                    {
                       object.remove();
                    }
                }
            }
        }

        message.RsMessageRule.cacheConnectorDestinationNames();


        return createdObjects;
    }

    public static Map updateConnector(NotificationConnector connector, Map connectorParams) {
        def connectorParamsCopy = connectorParams.clone();
        def oldProperties = [:]

        def scriptName = NotificationConnector.getScriptName(connector.name);
        CmdbScript script = CmdbScript.get(name: scriptName);

        def updatedObjects = [connector: connector, connection: connector.ds.connection, datasource: connector.ds,script:script];
        oldProperties[connector] = ControllerUtils.backupOldData(connector, connectorParamsCopy);
        connector.update(connectorParamsCopy);
        updatedObjects["connector"] = connector;
        if (!connector.hasErrors()) {
            def connection = connector.ds.connection;
            connectorParamsCopy.name = getConnectionName(connectorParamsCopy.name)
            oldProperties[connection] = ControllerUtils.backupOldData(connection, connectorParamsCopy);
            connection.update(connectorParamsCopy)
            updatedObjects["connection"] = connection;
            if (!connection.hasErrors()) {
                def datasource = connector.ds;
                oldProperties[datasource] = ControllerUtils.backupOldData(datasource, connectorParamsCopy);
                datasource.update(name: getDatasourceName(connector.name));
                updatedObjects["datasource"] = datasource;
                if(!datasource.hasErrors())
                {
                    def staticParam = "connectorName:${connector.name}";
                    scriptName = getScriptName(connector.name);
                    def scriptUpdateParams=[name: scriptName, scriptFile: connectorParams.scriptFile, staticParam: staticParam, logLevel: connectorParams.logLevel];
                    oldProperties[script] = ControllerUtils.backupOldData(script, scriptUpdateParams);
                    CmdbScript.updateScript(script,scriptUpdateParams, true);
                    updatedObjects["script"] = script;
                }
            }
        }
        def rollback = false;
        updatedObjects.each {key, object ->
            if (object.hasErrors())
            {
                rollback = true;
            }
        }
        if (rollback)
        {
            restoreOldData(oldProperties);
        }
        message.RsMessageRule.cacheConnectorDestinationNames();

        return updatedObjects;
    }

    private static void restoreOldData(oldProperties)
    {
        oldProperties.each {object, oldObjectProperties ->
            object.get(id: object.id)?.update(oldObjectProperties);
        }
    }

    public static void deleteConnector(NotificationConnector connector) {
        connector.ds?.connection?.remove();
        connector.ds?.remove();
        def script=CmdbScript.get(name:getScriptName(connector.name));
        if(script)
        {
            CmdbScript.deleteScript(script);
        }

        connector.remove()
        message.RsMessageRule.cacheConnectorDestinationNames();
    }
}