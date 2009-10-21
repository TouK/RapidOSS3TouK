package connector

import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import connection.SametimeConnection
import datasource.SametimeDatasource

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 12, 2009
 * Time: 2:54:11 PM
 * To change this template use File | Settings | File Templates.
 */

class SametimeConnectorController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {

    }

    def show = {
        SametimeConnector sametimeConnector = SametimeConnector.get([id: params.id])
        if (!sametimeConnector) {
            flash.message = "SametimeConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [sametimeConnector: sametimeConnector]
        }
    }

    def delete = {
        def sametimeConnector = SametimeConnector.get([id: params.id])
        if (sametimeConnector) {
            SametimeConnector.deleteConnector(sametimeConnector)
            flash.message = "SametimeConnector ${params.id} deleted"
            redirect(action: list);
        }
        else {
            flash.message = "SametimeConnector not found with id ${params.id}"
            redirect(action: list);
        }
    }


    def create = {
        def sametimeConnector = new SametimeConnector()
        sametimeConnector.properties = params
        return ['sametimeConnector': sametimeConnector, sametimeConnection: new SametimeConnection(), sametimeDatasource: new SametimeDatasource()]
    }
    def save = {
        def connectorParams = ControllerUtils.getClassProperties(params, SametimeConnection);
        connectorParams.putAll(ControllerUtils.getClassProperties(params, SametimeConnector))
        def createdObjects = SametimeConnector.addConnector(connectorParams);
        def objectWithErrors = createdObjects.values().findAll {it.hasErrors()}
        if (objectWithErrors.size() > 0) {
            render(view: 'create', model: createdObjects)
        }
        else {
            redirect(action: show, id: createdObjects.sametimeConnector.id);
        }
    }
    def edit = {
        def sametimeConnector = SametimeConnector.get([id: params.id])

        if (!sametimeConnector) {
            flash.message = "SametimeConnector not found with id ${params.id}"
            redirect(action: 'list');
        }
        else {
            return [sametimeConnector: sametimeConnector, sametimeConnection: sametimeConnector.ds.connection, sametimeDatasource: sametimeConnector.ds]
        }
    }
    def update = {
        def sametimeConnector = SametimeConnector.get([id: params.id])
        if (sametimeConnector) {
            def connectorParams = ControllerUtils.getClassProperties(params, SametimeConnection);
            connectorParams.putAll(ControllerUtils.getClassProperties(params, SametimeConnector))
            def updatedObjects = SametimeConnector.updateConnector(sametimeConnector, connectorParams);
            def objectWithErrors = updatedObjects.values().findAll {it.hasErrors()}
            if (objectWithErrors.size() > 0) {
                render(view: 'create', model: updatedObjects)
            }
            else {
                redirect(action: show, id: updatedObjects.sametimeConnector.id);
            }
        }
        else {
            flash.message = "SametimeConnector not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def testConnection = {
        def sametimeConnector = SametimeConnector.get([id: params.id])

        if (!sametimeConnector) {
            flash.message = "SametimeConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def sametimeConnection = sametimeConnector.ds.connection;
            if (!sametimeConnection)
            {
                flash.message = "sametimeConnection of sametimeConnector not found"
                redirect(action: list)
            }
            else {
                try
                {
                    sametimeConnection.checkConnection();
                    flash.message = "Successfully connected to server."
                } catch (Throwable t)
                {
                    addError("connection.test.exception", [sametimeConnection.name, t.toString()]);
                    log.warn(this.errors.getAllErrors()[0]?.toString(), org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                    flash.errors = this.errors;
                }
                redirect(action: list);
            }

        }
    }
}