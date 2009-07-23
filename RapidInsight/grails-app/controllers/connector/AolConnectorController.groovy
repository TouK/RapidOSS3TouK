package connector

import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import connection.AolConnection
import datasource.AolDatasource

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 12, 2009
 * Time: 2:54:11 PM
 * To change this template use File | Settings | File Templates.
 */

class AolConnectorController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {

    }

    def show = {
        AolConnector aolConnector = AolConnector.get([id: params.id])
        if (!aolConnector) {
            flash.message = "AolConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [aolConnector: aolConnector]
        }
    }

    def delete = {
        def aolConnector = AolConnector.get([id: params.id])
        if (aolConnector) {
            AolConnector.deleteConnector(aolConnector)
            flash.message = "AolConnector ${params.id} deleted"
            redirect(action: list);
        }
        else {
            flash.message = "AolConnector not found with id ${params.id}"
            redirect(action: list);
        }
    }


    def create = {
        def aolConnector = new AolConnector()
        aolConnector.properties = params
        return ['aolConnector': aolConnector, aolConnection: new AolConnection(), aolDatasource: new AolDatasource()]
    }
    def save = {
        def connectorParams = ControllerUtils.getClassProperties(params, AolConnection);
        connectorParams.putAll(ControllerUtils.getClassProperties(params, AolConnector))
        def createdObjects = AolConnector.addConnector(connectorParams);
        def objectWithErrors = createdObjects.values().findAll {it.hasErrors()}
        if (objectWithErrors.size() > 0) {
            render(view: 'create', model: createdObjects)
        }
        else {
            redirect(action: show, id: createdObjects.aolConnector.id);
        }
    }
    def edit = {
        def aolConnector = AolConnector.get([id: params.id])

        if (!aolConnector) {
            flash.message = "AolConnector not found with id ${params.id}"
            redirect(action: 'list');
        }
        else {
            return [aolConnector: aolConnector, aolConnection: aolConnector.ds.connection, aolDatasource: aolConnector.ds]
        }
    }
    def update = {
        def aolConnector = AolConnector.get([id: params.id])
        if (aolConnector) {
            def connectorParams = ControllerUtils.getClassProperties(params, AolConnection);
            connectorParams.putAll(ControllerUtils.getClassProperties(params, AolConnector))
            def updatedObjects = AolConnector.updateConnector(aolConnector, connectorParams);
            def objectWithErrors = updatedObjects.values().findAll {it.hasErrors()}
            if (objectWithErrors.size() > 0) {
                render(view: 'create', model: updatedObjects)
            }
            else {
                redirect(action: show, id: updatedObjects.aolConnector.id);
            }
        }
        else {
            flash.message = "AolConnector not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def testConnection = {
        def aolConnector = AolConnector.get([id: params.id])

        if (!aolConnector) {
            flash.message = "AolConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def aolConnection = aolConnector.ds.connection;
            if (!aolConnection)
            {
                flash.message = "aolConnection of aolConnector not found"
                redirect(action: list)
            }
            else {
                try
                {
                    aolConnection.checkConnection();
                    flash.message = "Successfully connected to server."
                } catch (Throwable t)
                {
                    addError("connection.test.exception", [aolConnection.name, t.toString()]);
                    log.warn("", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                    flash.errors = this.errors;
                }
                redirect(action: list);
            }

        }
    }
}