package connector

import connection.EmailConnection
import datasource.EmailDatasource
import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 13, 2009
* Time: 10:42:32 AM
* To change this template use File | Settings | File Templates.
*/
class EmailConnectorOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    static def getEmailConnectionName(String connectorName)
    {
        return "${connectorName}";
    }
    static def getEmailDatasourceName(String connectorName)
    {
        return "${connectorName}connectorDs";
    }

    public static Map addConnector(Map connectorParams) {
        def connectorParamsCopy = connectorParams.clone();
        def createdObjects = ["emailConnection": new EmailConnection(), "emailDatasource": new EmailDatasource()];
        def emailConnector = EmailConnector.addUnique(connectorParamsCopy)
        createdObjects["emailConnector"] = emailConnector;
        if (!emailConnector.hasErrors()) {
            connectorParamsCopy.name = EmailConnector.getEmailConnectionName(emailConnector.name)
            def emailConnection = EmailConnection.addUnique(connectorParamsCopy);
            createdObjects["emailConnection"] = emailConnection;
            if (!emailConnection.hasErrors()) {
                def emailDatasource = EmailDatasource.addUnique(name: EmailConnector.getEmailDatasourceName(emailConnector.name), connection: emailConnection);
                createdObjects["emailDatasource"] = emailDatasource;
                if (!emailDatasource.hasErrors()) {
                    emailConnector.addRelation(ds: emailDatasource)
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

    public static Map updateConnector(EmailConnector emailConnector, Map connectorParams) {
        def connectorParamsCopy = connectorParams.clone();
        def oldProperties = [:]
        def updatedObjects = [emailConnector: emailConnector, emailConnection: emailConnector.ds.connection, emailDatasource: emailConnector.ds];
        oldProperties[emailConnector] = ControllerUtils.backupOldData(emailConnector, connectorParamsCopy);
        emailConnector.update(connectorParamsCopy);
        updatedObjects["emailConnector"] = emailConnector;
        if (!emailConnector.hasErrors()) {
            def emailConnection = emailConnector.ds.connection;
            connectorParamsCopy.name = EmailConnector.getEmailConnectionName(connectorParamsCopy.name)
            oldProperties[emailConnection] = ControllerUtils.backupOldData(emailConnection, connectorParamsCopy);
            emailConnection.update(connectorParamsCopy)
            updatedObjects["emailConnection"] = emailConnection;
            if (!emailConnection.hasErrors()) {
                def emailDatasource = emailConnector.ds;
                oldProperties[emailDatasource] = ControllerUtils.backupOldData(emailDatasource, connectorParamsCopy);
                emailDatasource.update(name: EmailConnector.getEmailDatasourceName(emailConnector.name));
                updatedObjects["emailDatasource"] = emailDatasource;
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

    public static void deleteConnector(EmailConnector emailConnector) {
        emailConnector.ds?.connection?.remove();
        emailConnector.ds?.remove();
        emailConnector.remove()
    }
}