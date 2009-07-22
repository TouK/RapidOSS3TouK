package connector

import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import connection.EmailConnection
import datasource.EmailDatasource

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 12, 2009
 * Time: 2:54:11 PM
 * To change this template use File | Settings | File Templates.
 */

class EmailConnectorController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {

    }

    def show = {
        EmailConnector emailConnector = EmailConnector.get([id: params.id])
        if (!emailConnector) {
            flash.message = "EmailConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [emailConnector: emailConnector]
        }
    }

    def delete = {
        def emailConnector = EmailConnector.get([id: params.id])
        if (emailConnector) {
            EmailConnector.deleteConnector(emailConnector)
            flash.message = "EmailConnector ${params.id} deleted"
            redirect(action: list);
        }
        else {
            flash.message = "EmailConnector not found with id ${params.id}"
            redirect(action: list);
        }
    }


    def create = {
        def emailConnector = new EmailConnector()
        emailConnector.properties = params
        return ['emailConnector': emailConnector, emailConnection: new EmailConnection(), emailDatasource: new EmailDatasource()]
    }
    def save = {
        def connectorParams = ControllerUtils.getClassProperties(params, EmailConnection);
        connectorParams.putAll(ControllerUtils.getClassProperties(params, EmailConnector))
        def createdObjects = EmailConnector.addConnector(connectorParams);
        def objectWithErrors = createdObjects.values().findAll {it.hasErrors()}
        if (objectWithErrors.size() > 0) {
            render(view: 'create', model: createdObjects)
        }
        else {
            redirect(action: show, id: createdObjects.emailConnector.id);
        }
    }
    def edit = {
        def emailConnector = EmailConnector.get([id: params.id])

        if (!emailConnector) {
            flash.message = "EmailConnector not found with id ${params.id}"
            redirect(action: 'list');
        }
        else {
            return [emailConnector: emailConnector, emailConnection: emailConnector.emailConnection, emailDatasource: emailConnector.emailDatasource]
        }
    }
    def update = {
        def emailConnector = EmailConnector.get([id: params.id])
        if (emailConnector) {
            def connectorParams = ControllerUtils.getClassProperties(params, EmailConnection);
            connectorParams.putAll(ControllerUtils.getClassProperties(params, EmailConnector))
            def updatedObjects = EmailConnector.updateConnector(emailConnector, connectorParams);
            def objectWithErrors = updatedObjects.values().findAll {it.hasErrors()}
            if (objectWithErrors.size() > 0) {
                render(view: 'create', model: updatedObjects)
            }
            else {
                redirect(action: show, id: updatedObjects.emailConnector.id);
            }
        }
        else {
            flash.message = "EmailConnector not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def testConnection = {
        def emailConnector = EmailConnector.get([id: params.id])

        if (!emailConnector) {
            flash.message = "EmailConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def emailConnection = emailConnector.ds.connection;
            if (!emailConnection)
            {
                flash.message = "emailConnection of emailConnector not found"
                redirect(action: list)
            }
            else {
                try
                {
                    emailConnection.checkConnection();
                    flash.message = "Successfully connected to server."
                } catch (Throwable t)
                {
                    addError("connection.test.exception", [emailConnection.name, t.toString()]);
                    log.warn("", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                    flash.errors = this.errors;
                }
                redirect(action: list);
            }

        }
    }
}