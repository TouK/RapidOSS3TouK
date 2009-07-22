package connector

import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import connection.JabberConnection
import datasource.JabberDatasource

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 12, 2009
 * Time: 2:54:11 PM
 * To change this template use File | Settings | File Templates.
 */

class JabberConnectorController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {

    }

    def show = {
        JabberConnector jabberConnector = JabberConnector.get([id: params.id])
        if (!jabberConnector) {
            flash.message = "JabberConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [jabberConnector: jabberConnector]
        }
    }

    def delete = {
        def jabberConnector = JabberConnector.get([id: params.id])
        if (jabberConnector) {
            JabberConnector.deleteConnector(jabberConnector)
            flash.message = "JabberConnector ${params.id} deleted"
            redirect(action: list);
        }
        else {
            flash.message = "JabberConnector not found with id ${params.id}"
            redirect(action: list);
        }
    }


    def create = {
        def jabberConnector = new JabberConnector()
        jabberConnector.properties = params
        return ['jabberConnector': jabberConnector, jabberConnection: new JabberConnection(), jabberDatasource: new JabberDatasource()]
    }
    def save = {
        def connectorParams = ControllerUtils.getClassProperties(params, JabberConnection);
        connectorParams.putAll(ControllerUtils.getClassProperties(params, JabberConnector))
        def createdObjects = JabberConnector.addConnector(connectorParams);
        def objectWithErrors = createdObjects.values().findAll {it.hasErrors()}
        if (objectWithErrors.size() > 0) {
            render(view: 'create', model: createdObjects)
        }
        else {
            redirect(action: show, id: createdObjects.jabberConnector.id);
        }
    }
    def edit = {
        def jabberConnector = JabberConnector.get([id: params.id])

        if (!jabberConnector) {
            flash.message = "JabberConnector not found with id ${params.id}"
            redirect(action: 'list');
        }
        else {
            return [jabberConnector: jabberConnector, jabberConnection: jabberConnector.ds.connection, jabberDatasource: jabberConnector.ds]
        }
    }
    def update = {
        def jabberConnector = JabberConnector.get([id: params.id])
        if (jabberConnector) {
            def connectorParams = ControllerUtils.getClassProperties(params, JabberConnection);
            connectorParams.putAll(ControllerUtils.getClassProperties(params, JabberConnector))
            def updatedObjects = JabberConnector.updateConnector(jabberConnector, connectorParams);
            def objectWithErrors = updatedObjects.values().findAll {it.hasErrors()}
            if (objectWithErrors.size() > 0) {
                render(view: 'create', model: updatedObjects)
            }
            else {
                redirect(action: show, id: updatedObjects.jabberConnector.id);
            }
        }
        else {
            flash.message = "JabberConnector not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def testConnection = {
        def jabberConnector = JabberConnector.get([id: params.id])

        if (!jabberConnector) {
            flash.message = "JabberConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def jabberConnection = jabberConnector.ds.connection;
            if (!jabberConnection)
            {
                flash.message = "jabberConnection of jabberConnector not found"
                redirect(action: list)
            }
            else {
                try
                {
                    jabberConnection.checkConnection();
                    flash.message = "Successfully connected to server."
                } catch (Throwable t)
                {
                    addError("connection.test.exception", [jabberConnection.name, t.toString()]);
                    log.warn("", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                    flash.errors = this.errors;
                }
                redirect(action: list);
            }

        }
    }
}