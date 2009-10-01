package connector

import com.ifountain.core.connection.ConnectionManager
import com.ifountain.rcmdb.domain.util.ControllerUtils
import connection.JiraConnection
import datasource.JiraDatasource


class JiraConnectorController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.sort) params.sort = "name"
        [jiraConnectorList: JiraConnector.list(params)]
    }

    def show = {
        JiraConnector jiraConnector = JiraConnector.get([id: params.id])
        if (!jiraConnector) {
            flash.message = "JiraConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
        	def connectionName = JiraConnector.getConnectionName(jiraConnector.name);
            def jiraConnection = JiraConnection.findByName(connectionName);
            def dsName = JiraConnector.getDatasourceName(jiraConnector.name);
            def jiraDatasource = JiraDatasource.findByName(dsName);
            return [jiraConnector: jiraConnector, jiraDatasource:jiraDatasource, jiraConnection:jiraConnection ]
        }
    }

    def delete = {
        def jiraConnector = JiraConnector.get([id: params.id])
        if (jiraConnector) {
            try {
                def connectionName = JiraConnector.getConnectionName(jiraConnector.name)
                def datasourceName = JiraConnector.getDatasourceName(jiraConnector.name)
                jiraConnector.remove()
                JiraDatasource.get(name: datasourceName)?.remove();
                JiraConnection.get(name: connectionName)?.remove();
                flash.message = "JiraConnector ${jiraConnector.name} deleted"
                redirect(action: list)
            }
            catch (e) {
                addError("connector.delete.exception", [jiraConnector.name, e.getMessage()])
                flash.errors = this.errors;
                redirect(action: show, id: jiraConnector.id)
            }

        }
        else {
            flash.message = "JiraConnector not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        JiraConnector jiraConnector = JiraConnector.get([id: params.id])
        if (!jiraConnector) {
            flash.message = "JiraConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
        	def connectionName = JiraConnector.getConnectionName(jiraConnector.name);
            def jiraConnection = JiraConnection.findByName(connectionName);
            def dsName = JiraConnector.getDatasourceName(jiraConnector.name);
            def jiraDatasource = JiraDatasource.findByName(dsName);
            return [jiraConnector: jiraConnector, jiraDatasource:jiraDatasource, jiraConnection:jiraConnection ]
        }
    }

    def update = {
		def jiraConnector = JiraConnector.get([id: params.id])
        if (jiraConnector) {
            def connectorParams = ControllerUtils.getClassProperties(params, JiraConnector)
            def connectionParams = ControllerUtils.getClassProperties(params, JiraConnection)
            def datasourceParams = ControllerUtils.getClassProperties(params, JiraDatasource)
            def updatedObjects = JiraConnector.updateConnector(jiraConnector, connectorParams, datasourceParams, connectionParams);
            if (updatedObjects.values().findAll {it.hasErrors()}.isEmpty()) {
                if (ConnectionManager.checkConnection(connectionParams.name)) {
                    flash.message = "JiraConnector ${updatedObjects.jiraConnector.name} updated"
                }
                else {
                    flash.message = "JiraConnector ${updatedObjects.jiraConnector.name} updated, but connection could not be established"
                }
                redirect(action: show, id: updatedObjects.jiraConnector.id)
            }
            else {
                render(view: 'edit', model: updatedObjects)
            }
        }
        else {
            flash.message = "JiraConnector not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def jiraConnector = new JiraConnector();
        jiraConnector.properties = params
        return [jiraConnector: jiraConnector, jiraDatasource:new JiraDatasource(), jiraConnection: new JiraConnection()]
    }

    def save = {
        def connectorParams = ControllerUtils.getClassProperties(params, JiraConnector)
        def datasourceParams = ControllerUtils.getClassProperties(params, JiraDatasource)
        def connectionParams = ControllerUtils.getClassProperties(params, JiraConnection)
        def createdObjects = JiraConnector.addConnector(connectorParams, datasourceParams, connectionParams)
        if (!createdObjects.jiraConnector.hasErrors() && !createdObjects.datasource.hasErrors() && !createdObjects.jiraConnection.hasErrors()) {
            if (ConnectionManager.checkConnection(connectionParams.name)) {
                flash.message = "JiraConnector ${createdObjects.jiraConnector.name} created"
            }
            else {
                flash.message = "JiraConnector ${createdObjects.jiraConnector.name} created, but connection could not be established"
            }
            redirect(action: show, id: createdObjects.jiraConnector.id)
        }
        else {
            render(view: 'create', model: createdObjects)
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("connector.JiraConnector")
        if (modelClass)
        {
            try
            {

                modelClass.metaClass.invokeStaticMethod(modelClass, "reloadOperations", [] as Object[]);
                flash.message = "Model operations reloaded"
                redirect(action:list)
            } catch (t){
                flash.message = "Exception occurred while reloading model operations Reason:${t.toString()}"
                 redirect(action:list)
            }
        }
        else{
            flash.message = "Model currently not loaded by application. You should reload application."
            redirect(action:list)
        }
    }
    def testConnection = {
        JiraConnector jiraConnector = JiraConnector.get([id: params.id])
        if (!jiraConnector) {
            flash.message = "JiraConnector not found with id ${params.id}"
            redirect(action:list);
        }
        else {
            try {
                jiraConnector.ds.connection.checkConnection()
                flash.message = "Successfully connected to server."
            }
            catch (Throwable t){
                addError("connection.test.exception", [jiraConnector.ds.connection.name, t.toString()]);
                log.warn("", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                flash.errors = this.errors;
            }
            redirect(action:list);
        }
    }
}