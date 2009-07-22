package connector

import connection.JabberConnection
import datasource.JabberDatasource
import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 13, 2009
* Time: 10:42:32 AM
* To change this template use File | Settings | File Templates.
*/
class JabberConnectorOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    static def getJabberConnectionName(String connectorName)
    {
        return "${connectorName}";
    }
    static def getJabberDatasourceName(String connectorName)
    {
        return "${connectorName}connectorDs";
    }

    public static Map addConnector(Map connectorParams) {
        def connectorParamsCopy = connectorParams.clone();
        def createdObjects = ["jabberConnection": new JabberConnection(), "jabberDatasource": new JabberDatasource()];
        def jabberConnector = JabberConnector.addUnique(connectorParamsCopy)
        createdObjects["jabberConnector"] = jabberConnector;
        if (!jabberConnector.hasErrors()) {
            connectorParamsCopy.name = JabberConnector.getJabberConnectionName(jabberConnector.name)
            def jabberConnection = JabberConnection.addUnique(connectorParamsCopy);
            createdObjects["jabberConnection"] = jabberConnection;
            if (!jabberConnection.hasErrors()) {
                def jabberDatasource = JabberDatasource.addUnique(name: JabberConnector.getJabberDatasourceName(jabberConnector.name), connection: jabberConnection);
                createdObjects["jabberDatasource"] = jabberDatasource;
                if (!jabberDatasource.hasErrors()) {
                    jabberConnector.addRelation(ds: jabberDatasource)
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

    public static Map updateConnector(JabberConnector jabberConnector, Map connectorParams) {
        def connectorParamsCopy = connectorParams.clone();
        def oldProperties = [:]
        def updatedObjects = [jabberConnector: jabberConnector, jabberConnection: jabberConnector.ds.connection, jabberDatasource: jabberConnector.ds];
        oldProperties[jabberConnector] = ControllerUtils.backupOldData(jabberConnector, connectorParamsCopy);
        jabberConnector.update(connectorParamsCopy);
        updatedObjects["jabberConnector"] = jabberConnector;
        if (!jabberConnector.hasErrors()) {
            def jabberConnection = jabberConnector.ds.connection;
            connectorParamsCopy.name = JabberConnector.getJabberConnectionName(connectorParamsCopy.name)
            oldProperties[jabberConnection] = ControllerUtils.backupOldData(jabberConnection, connectorParamsCopy);
            jabberConnection.update(connectorParamsCopy)
            updatedObjects["jabberConnection"] = jabberConnection;
            if (!jabberConnection.hasErrors()) {
                def jabberDatasource = jabberConnector.ds;
                oldProperties[jabberDatasource] = ControllerUtils.backupOldData(jabberDatasource, connectorParamsCopy);
                jabberDatasource.update(name: JabberConnector.getJabberDatasourceName(jabberConnector.name));
                updatedObjects["jabberDatasource"] = jabberDatasource;
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

    public static void deleteConnector(JabberConnector jabberConnector) {
        jabberConnector.ds?.connection?.remove();
        jabberConnector.ds?.remove();
        jabberConnector.remove()
    }
}