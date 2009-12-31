package connector

import com.ifountain.rcmdb.domain.util.ControllerUtils
import script.CmdbScript

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Dec 15, 2009
 * Time: 9:33:11 AM
 * To change this template use File | Settings | File Templates.
 */
class NotificationConnectorController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {

    }

    def show = {
        NotificationConnector connector = NotificationConnector.get([id: params.id])
        if (!connector) {
            flash.message = "NotificationConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [connector: connector]
        }
    }

    def delete = {
        def connector = NotificationConnector.get([id: params.id])
        if (connector) {
            NotificationConnector.deleteConnector(connector)
            flash.message = "NotificationConnector ${params.id} deleted"
            redirect(action: list);
        }
        else {
            flash.message = "NotificationConnector not found with id ${params.id}"
            redirect(action: list);
        }
    }


    def create = {
        def connector = new NotificationConnector()
        connector.properties = params

        def connectionClass=NotificationConnector.getConnectionClass(connector.type);
        def datasourceClass=NotificationConnector.getDatasourceClass(connector.type);

        def script=new CmdbScript();
        script.setPropertyWithoutUpdate("scriptFile",NotificationConnector.getDefaultScriptFile(connector.type));
        script.setPropertyWithoutUpdate("period",NotificationConnector.getDefaultScriptPeriod(connector.type));
        return ['connector': connector, connection:connectionClass.newInstance(), datasource: datasourceClass.newInstance(),script:script]
    }
    def save = {
        def connectionClass=NotificationConnector.getConnectionClass(params.type);

        def connectorParams = ControllerUtils.getClassProperties(params, connectionClass);
        connectorParams.putAll(ControllerUtils.getClassProperties(params, NotificationConnector));
        connectorParams.putAll(ControllerUtils.getClassProperties(params, CmdbScript));

        def createdObjects = NotificationConnector.addConnector(connectorParams);
        def objectWithErrors = createdObjects.values().findAll {it.hasErrors()}
        if (objectWithErrors.size() > 0) {
            render(view: 'create', model: createdObjects)
        }
        else {
            redirect(action: show, id: createdObjects.connector.id);
        }
    }
    def edit = {
        def connector = NotificationConnector.get([id: params.id])

        if (!connector) {
            flash.message = "NotificationConnector not found with id ${params.id}"
            redirect(action: 'list');
        }
        else {
            return [connector: connector, connection: connector.ds.connection, datasource: connector.ds,script:connector.script]
        }
    }
    def update = {
        def connector = NotificationConnector.get([id: params.id])
        if (connector) {
            def connectionClass=NotificationConnector.getConnectionClass(connector.type);


            def connectorParams = ControllerUtils.getClassProperties(params, connectionClass);
            connectorParams.putAll(ControllerUtils.getClassProperties(params, NotificationConnector));
            connectorParams.putAll(ControllerUtils.getClassProperties(params, CmdbScript));
            def updatedObjects = NotificationConnector.updateConnector(connector, connectorParams);
            def objectWithErrors = updatedObjects.values().findAll {it.hasErrors()}
            if (objectWithErrors.size() > 0) {
                render(view: 'edit', model: updatedObjects)
            }
            else {
                redirect(action: show, id: updatedObjects.connector.id);
            }
        }
        else {
            flash.message = "NotificationConnector not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def testConnection = {
        def connector = NotificationConnector.get([id: params.id])

        if (!connector) {
            flash.message = "NotificationConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def connection = connector.ds.connection;
            if (!connection)
            {
                flash.message = "connection of connector not found"
                redirect(action: list)
            }
            else {
                try
                {
                    connection.checkConnection();
                    flash.message = "Successfully connected to server."
                } catch (Throwable t)
                {
                    addError("connection.test.exception", [connection.name, t.toString()]);
                    log.warn(this.errors.getAllErrors()[0]?.toString(), org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                    flash.errors = this.errors;
                }
                redirect(action: list);
            }

        }
    }
}