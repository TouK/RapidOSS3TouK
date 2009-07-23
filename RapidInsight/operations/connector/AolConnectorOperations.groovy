package connector

import connection.AolConnection
import datasource.AolDatasource
import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 13, 2009
* Time: 10:42:32 AM
* To change this template use File | Settings | File Templates.
*/
class AolConnectorOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    static def getAolConnectionName(String connectorName)
    {
        return "${connectorName}";
    }
    static def getAolDatasourceName(String connectorName)
    {
        return "${connectorName}connectorDs";
    }

    public static Map addConnector(Map connectorParams) {
        def connectorParamsCopy = connectorParams.clone();
        def createdObjects = ["aolConnection": new AolConnection(), "aolDatasource": new AolDatasource()];
        def aolConnector = AolConnector.addUnique(connectorParamsCopy)
        createdObjects["aolConnector"] = aolConnector;
        if (!aolConnector.hasErrors()) {
            connectorParamsCopy.name = AolConnector.getAolConnectionName(aolConnector.name)
            def aolConnection = AolConnection.addUnique(connectorParamsCopy);
            createdObjects["aolConnection"] = aolConnection;
            if (!aolConnection.hasErrors()) {
                def aolDatasource = AolDatasource.addUnique(name: AolConnector.getAolDatasourceName(aolConnector.name), connection: aolConnection);
                createdObjects["aolDatasource"] = aolDatasource;
                if (!aolDatasource.hasErrors()) {
                    aolConnector.addRelation(ds: aolDatasource)
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

    public static Map updateConnector(AolConnector aolConnector, Map connectorParams) {
        def connectorParamsCopy = connectorParams.clone();
        def oldProperties = [:]
        def updatedObjects = [aolConnector: aolConnector, aolConnection: aolConnector.ds.connection, aolDatasource: aolConnector.ds];
        oldProperties[aolConnector] = ControllerUtils.backupOldData(aolConnector, connectorParamsCopy);
        aolConnector.update(connectorParamsCopy);
        updatedObjects["aolConnector"] = aolConnector;
        if (!aolConnector.hasErrors()) {
            def aolConnection = aolConnector.ds.connection;
            connectorParamsCopy.name = AolConnector.getAolConnectionName(connectorParamsCopy.name)
            oldProperties[aolConnection] = ControllerUtils.backupOldData(aolConnection, connectorParamsCopy);
            aolConnection.update(connectorParamsCopy)
            updatedObjects["aolConnection"] = aolConnection;
            if (!aolConnection.hasErrors()) {
                def aolDatasource = aolConnector.ds;
                oldProperties[aolDatasource] = ControllerUtils.backupOldData(aolDatasource, connectorParamsCopy);
                aolDatasource.update(name: AolConnector.getAolDatasourceName(aolConnector.name));
                updatedObjects["aolDatasource"] = aolDatasource;
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

    public static void deleteConnector(AolConnector aolConnector) {
        aolConnector.ds?.connection?.remove();
        aolConnector.ds?.remove();
        aolConnector.remove()
    }
}