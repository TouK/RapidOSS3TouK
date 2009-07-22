package connector

import connection.SametimeConnection
import datasource.SametimeDatasource
import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 13, 2009
* Time: 10:42:32 AM
* To change this template use File | Settings | File Templates.
*/
class SametimeConnectorOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    static def getSametimeConnectionName(String connectorName)
    {
        return "${connectorName}";
    }
    static def getSametimeDatasourceName(String connectorName)
    {
        return "${connectorName}connectorDs";
    }

    public static Map addConnector(Map connectorParams) {
        def connectorParamsCopy = connectorParams.clone();
        def createdObjects = ["sametimeConnection": new SametimeConnection(), "sametimeDatasource": new SametimeDatasource()];
        def sametimeConnector = SametimeConnector.addUnique(connectorParamsCopy)
        createdObjects["sametimeConnector"] = sametimeConnector;
        if (!sametimeConnector.hasErrors()) {
            connectorParamsCopy.name = SametimeConnector.getSametimeConnectionName(sametimeConnector.name)
            def sametimeConnection = SametimeConnection.addUnique(connectorParamsCopy);
            createdObjects["sametimeConnection"] = sametimeConnection;
            if (!sametimeConnection.hasErrors()) {
                def sametimeDatasource = SametimeDatasource.addUnique(name: SametimeConnector.getSametimeDatasourceName(sametimeConnector.name), connection: sametimeConnection);
                createdObjects["sametimeDatasource"] = sametimeDatasource;
                if (!sametimeDatasource.hasErrors()) {
                    sametimeConnector.addRelation(ds: sametimeDatasource)
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

    public static Map updateConnector(SametimeConnector sametimeConnector, Map connectorParams) {
        def connectorParamsCopy = connectorParams.clone();
        def oldProperties = [:]
        def updatedObjects = [sametimeConnector: sametimeConnector, sametimeConnection: sametimeConnector.ds.connection, sametimeDatasource: sametimeConnector.ds];
        oldProperties[sametimeConnector] = ControllerUtils.backupOldData(sametimeConnector, connectorParamsCopy);
        sametimeConnector.update(connectorParamsCopy);
        updatedObjects["sametimeConnector"] = sametimeConnector;
        if (!sametimeConnector.hasErrors()) {
            def sametimeConnection = sametimeConnector.ds.connection;
            connectorParamsCopy.name = SametimeConnector.getSametimeConnectionName(connectorParamsCopy.name)
            oldProperties[sametimeConnection] = ControllerUtils.backupOldData(sametimeConnection, connectorParamsCopy);
            sametimeConnection.update(connectorParamsCopy)
            updatedObjects["sametimeConnection"] = sametimeConnection;
            if (!sametimeConnection.hasErrors()) {
                def sametimeDatasource = sametimeConnector.ds;
                oldProperties[sametimeDatasource] = ControllerUtils.backupOldData(sametimeDatasource, connectorParamsCopy);
                sametimeDatasource.update(name: SametimeConnector.getSametimeDatasourceName(sametimeConnector.name));
                updatedObjects["sametimeDatasource"] = sametimeDatasource;
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

    public static void deleteConnector(SametimeConnector sametimeConnector) {
        sametimeConnector.ds?.connection?.remove();
        sametimeConnector.ds?.remove();
        sametimeConnector.remove()
    }
}