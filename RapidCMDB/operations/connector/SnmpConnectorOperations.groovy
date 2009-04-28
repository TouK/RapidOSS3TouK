package connector

import connection.SnmpConnection
import datasource.SnmpDatasource
import script.CmdbScript
import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.exception.MessageSourceException

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Apr 8, 2009
* Time: 2:46:47 PM
*/
class SnmpConnectorOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {

    public static Map addConnector(Map connectorParams) {
        def createdObjects = ["snmpConnector": new SnmpConnector(), "snmpConnection": new SnmpConnection(),
                "datasource": new SnmpDatasource(), "script": new CmdbScript()]
        def connectorParamsCopy = connectorParams.clone();
        try {
            SnmpConnector snmpConnector = SnmpConnector.addUnique(connectorParamsCopy);
            createdObjects["snmpConnector"] = snmpConnector;
            if (!snmpConnector.hasErrors()) {
                connectorParamsCopy.name = snmpConnector.getConnectionName(snmpConnector.name);
                SnmpConnection snmpConnection = SnmpConnection.addUnique(connectorParamsCopy)
                createdObjects["snmpConnection"] = snmpConnection;
                if (!snmpConnection.hasErrors()) {
                    snmpConnector.addRelation(connection: snmpConnection);
                    connectorParamsCopy.name = snmpConnector.name;
                    connectorParamsCopy.type = CmdbScript.LISTENING;
                    def scriptClassParams = ControllerUtils.getClassProperties(connectorParamsCopy, CmdbScript);
                    scriptClassParams.logFileOwn = true;
                    CmdbScript script = CmdbScript.addUniqueScript(scriptClassParams, true);
                    createdObjects["script"] = script;
                    if (!script.hasErrors())
                    {
                        snmpConnector.addRelation(script: script);
                        def datasourceName = snmpConnector.getDatasourceName(snmpConnector.name);
                        def datasource = SnmpDatasource.addUnique(name: datasourceName, connection: snmpConnection, listeningScript: script);
                        createdObjects["datasource"] = datasource;
                    }

                }
            }
        }
        finally {
            //get all the objects which has errors
            def objectsWithError = createdObjects.values().findAll {it.hasErrors()}
            // if there is any object with error
            if (objectsWithError.size() > 0)
            {
                // we delete all the objects which does not have error
                createdObjects.each {key, object ->
                    if (!object.hasErrors() && object.id != null)
                    {
                        object.remove();
                    }
                }
            }
        }
        return createdObjects;
    }

    public static Map updateConnector(SnmpConnector snmpConnector, Map connectorParams) {
        return updateConnector(snmpConnector, connectorParams, true)
    }
    public static Map updateConnector(SnmpConnector snmpConnector, Map connectorParams, boolean fromController) {
        def updatedObjects = ["snmpConnector": snmpConnector, "snmpConnection": snmpConnector.connection,
                "script": snmpConnector.script, "datasource": snmpConnector.script.listeningDatasource]
        def oldProperties = [:]
        def throwedException = null;
        def connectorParamsCopy = connectorParams.clone();
        try {
            if (snmpConnector.script.listeningDatasource.isFree()) {
                oldProperties[snmpConnector] = ControllerUtils.backupOldData(snmpConnector, connectorParamsCopy);
                snmpConnector.update(connectorParamsCopy);
                updatedObjects["snmpConnector"] = snmpConnector
                if (!snmpConnector.hasErrors()) {
                    def connection = snmpConnector.connection
                    connectorParamsCopy.name = connection.name;
                    def isConnectionChanged = connectorParamsCopy.host != connection.host || connection.port != connectorParamsCopy.port;
                    if (isConnectionChanged) {
                        oldProperties[connection] = ControllerUtils.backupOldData(connection, connectorParamsCopy);
                        connection.update(connectorParamsCopy);
                    }
                    updatedObjects["snmpConnection"] = connection
                    if (!connection.hasErrors()) {
                        connectorParamsCopy.name = snmpConnector.name;
                        def scriptClassParams = ControllerUtils.getClassProperties(connectorParamsCopy, CmdbScript);
                        def script = snmpConnector.script
                        oldProperties[script] = ControllerUtils.backupOldData(script, connectorParamsCopy);
                        CmdbScript.updateScript(script, scriptClassParams, true);
                        updatedObjects["script"] = script;
                    }
                }
            }
            else {
                throw new MessageSourceException("connector.update.exception", [] as Object[])
            }
        }
        catch (MessageSourceException e) {
            throwedException = e;
            if (!fromController) {
                throw e;
            }
        }
        finally {
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
            if (throwedException != null) {
                updatedObjects["exception"] = throwedException;
            }
        }
        return updatedObjects;
    }

    private static void restoreOldData(oldProperties)
    {
        oldProperties.each {object, oldObjectProperties ->
            if (object instanceof CmdbScript)
            {
                CmdbScript.updateScript(object.get(id: object.id), oldObjectProperties, true);
            }
            else
            {
                object.get(id: object.id)?.update(oldObjectProperties);
            }
        }
    }

    public static void deleteConnector(SnmpConnector snmpConnector) {
        if (snmpConnector.script) {
            CmdbScript.deleteScript(snmpConnector.script);
        }
        snmpConnector.connection?.remove();
        snmpConnector.remove();
    }

    public static void startConnector(SnmpConnector snmpConnector) {
        def script = snmpConnector.script
        CmdbScript.startListening(script.name);
    }

    public static void stopConnector(SnmpConnector snmpConnector) {
        def script = snmpConnector.script
        CmdbScript.stopListening(script.name);
    }
}