package connector

import connection.SmsConnection
import datasource.SmsDatasource
import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 13, 2009
* Time: 10:42:32 AM
* To change this template use File | Settings | File Templates.
*/
class SmsConnectorOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    static def getSmsConnectionName(String connectorName)
    {
        return "${connectorName}";
    }
    static def getSmsDatasourceName(String connectorName)
    {
        return "${connectorName}connectorDs";
    }

    public static Map addConnector(Map connectorParams) {
        def connectorParamsCopy = connectorParams.clone();
        def createdObjects = ["smsConnection": new SmsConnection(), "smsDatasource": new SmsDatasource()];
        def smsConnector = SmsConnector.addUnique(connectorParamsCopy)
        createdObjects["smsConnector"] = smsConnector;
        if (!smsConnector.hasErrors()) {
            connectorParamsCopy.name = SmsConnector.getSmsConnectionName(smsConnector.name)
            def smsConnection = SmsConnection.addUnique(connectorParamsCopy);
            createdObjects["smsConnection"] = smsConnection;
            if (!smsConnection.hasErrors()) {
                def smsDatasource = SmsDatasource.addUnique(name: SmsConnector.getSmsDatasourceName(smsConnector.name), connection: smsConnection);
                createdObjects["smsDatasource"] = smsDatasource;
                if (!smsDatasource.hasErrors()) {
                    smsConnector.addRelation(ds: smsDatasource)
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
                    object.remove();
                }
            }
        }
        return createdObjects;
    }

    public static Map updateConnector(SmsConnector smsConnector, Map connectorParams) {
        def connectorParamsCopy = connectorParams.clone();
        def oldProperties = [:]
        def updatedObjects = [smsConnector: smsConnector, smsConnection: smsConnector.ds.connection, smsDatasource: smsConnector.ds];
        oldProperties[smsConnector] = ControllerUtils.backupOldData(smsConnector, connectorParamsCopy);
        smsConnector.update(connectorParamsCopy);
        updatedObjects["smsConnector"] = smsConnector;
        if (!smsConnector.hasErrors()) {
            def smsConnection = smsConnector.ds.connection;
            connectorParamsCopy.name = SmsConnector.getSmsConnectionName(connectorParamsCopy.name)
            oldProperties[smsConnection] = ControllerUtils.backupOldData(smsConnection, connectorParamsCopy);
            smsConnection.update(connectorParamsCopy)
            updatedObjects["smsConnection"] = smsConnection;
            if (!smsConnection.hasErrors()) {
                def smsDatasource = smsConnector.ds;
                oldProperties[smsDatasource] = ControllerUtils.backupOldData(smsDatasource, connectorParamsCopy);
                smsDatasource.update(name: SmsConnector.getSmsDatasourceName(smsConnector.name));
                updatedObjects["smsDatasource"] = smsDatasource;
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
        return updatedObjects;
    }

    private static void restoreOldData(oldProperties)
    {
        oldProperties.each {object, oldObjectProperties ->
            object.get(id: object.id)?.update(oldObjectProperties);
        }
    }

    public static void deleteConnector(SmsConnector smsConnector) {
        smsConnector.ds?.connection?.remove();
        smsConnector.ds?.remove();
        smsConnector.remove()
    }
}