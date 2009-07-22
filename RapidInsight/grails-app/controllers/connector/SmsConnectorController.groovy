package connector

import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import connection.SmsConnection
import datasource.SmsDatasource

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 12, 2009
 * Time: 2:54:11 PM
 * To change this template use File | Settings | File Templates.
 */

class SmsConnectorController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {

    }

    def show = {
        SmsConnector smsConnector = SmsConnector.get([id: params.id])
        if (!smsConnector) {
            flash.message = "SmsConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [smsConnector: smsConnector]
        }
    }

    def delete = {
        def smsConnector = SmsConnector.get([id: params.id])
        if (smsConnector) {
            SmsConnector.deleteConnector(smsConnector)
            flash.message = "SmsConnector ${params.id} deleted"
            redirect(action: list);
        }
        else {
            flash.message = "SmsConnector not found with id ${params.id}"
            redirect(action: list);
        }
    }


    def create = {
        def smsConnector = new SmsConnector()
        smsConnector.properties = params
        return ['smsConnector': smsConnector, smsConnection: new SmsConnection(), smsDatasource: new SmsDatasource()]
    }
    def save = {
        def connectorParams = ControllerUtils.getClassProperties(params, SmsConnection);
        connectorParams.putAll(ControllerUtils.getClassProperties(params, SmsConnector))
        def createdObjects = SmsConnector.addConnector(connectorParams);
        def objectWithErrors = createdObjects.values().findAll {it.hasErrors()}
        if (objectWithErrors.size() > 0) {
            render(view: 'create', model: createdObjects)
        }
        else {
            redirect(action: show, id: createdObjects.smsConnector.id);
        }
    }
    def edit = {
        def smsConnector = SmsConnector.get([id: params.id])

        if (!smsConnector) {
            flash.message = "SmsConnector not found with id ${params.id}"
            redirect(action: 'list');
        }
        else {
            return [smsConnector: smsConnector, smsConnection: smsConnector.ds.connection, smsDatasource: smsConnector.ds]
        }
    }
    def update = {
        def smsConnector = SmsConnector.get([id: params.id])
        if (smsConnector) {
            def connectorParams = ControllerUtils.getClassProperties(params, SmsConnection);
            connectorParams.putAll(ControllerUtils.getClassProperties(params, SmsConnector))
            def updatedObjects = SmsConnector.updateConnector(smsConnector, connectorParams);
            def objectWithErrors = updatedObjects.values().findAll {it.hasErrors()}
            if (objectWithErrors.size() > 0) {
                render(view: 'create', model: updatedObjects)
            }
            else {
                redirect(action: show, id: updatedObjects.smsConnector.id);
            }
        }
        else {
            flash.message = "SmsConnector not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def testConnection = {
        def smsConnector = SmsConnector.get([id: params.id])

        if (!smsConnector) {
            flash.message = "SmsConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def smsConnection = smsConnector.ds.connection;
            if (!smsConnection)
            {
                flash.message = "smsConnection of smsConnector not found"
                redirect(action: list)
            }
            else {
                try
                {
                    smsConnection.checkConnection();
                    flash.message = "Successfully connected to server."
                } catch (Throwable t)
                {
                    addError("connection.test.exception", [smsConnection.name, t.toString()]);
                    log.warn("", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                    flash.errors = this.errors;
                }
                redirect(action: list);
            }

        }
    }
}