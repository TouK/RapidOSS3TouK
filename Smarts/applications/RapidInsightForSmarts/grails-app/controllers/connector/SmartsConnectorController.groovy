package connector
import com.ifountain.rcmdb.domain.util.ControllerUtils
import script.CmdbScript
import com.ifountain.core.connection.ConnectionManager
import datasource.SmartsTopologyDatasource
import connection.SmartsConnection
import datasource.SmartsNotificationDatasource
import com.ifountain.smarts.connection.SmartsConnectionImpl;
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 27, 2008
 * Time: 2:59:12 PM
 * To change this template use File | Settings | File Templates.
 */
class SmartsConnectorController {
    def final static PROPS_TO_BE_EXCLUDED = ["id": "id", "_action_Update": "_action_Update", "controller": "controller", "action": "action"]
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        redirect(uri: '/admin.gsp');
    }

    def show = {
        SmartsConnector smartsConnector = SmartsConnector.get([id: params.id])

        if (!smartsConnector) {
            flash.message = "SmartsConnector not found with id ${params.id}"
            redirect(uri: "/admin.gsp")
        }
        else {
            return [smartsConnector: smartsConnector]
        }
    }

    def delete = {
        SmartsConnector smartsConnector = SmartsConnector.get([id: params.id])
        if (smartsConnector) {
            try {
                deleteConnector(smartsConnector)
                flash.message = "SmartsConnector ${smartsConnector.name} deleted"
                redirect(uri: "/admin.gsp")
            }
            catch (e) {
                e.printStackTrace();
                addError("connector.delete.exception", [smartsConnector.name, e.getMessage()])
                flash.errors = this.errors;
                redirect(action: show, id: smartsConnector.id)
            }

        }
        else {
            flash.message = "SmartsConnector not found with id ${params.id}"
            redirect(uri: "/admin.gsp")
        }
    }


    def deleteConnector(SmartsConnector smartsConnector)
    {
        CmdbScript.deleteScript(smartsConnector.ds.listeningScript);
        smartsConnector.ds.connection.remove();
        smartsConnector.remove();
    }

    def edit = {
        SmartsConnector smartsConnector = SmartsConnector.get([id: params.id])

        if (!smartsConnector) {
            flash.message = "SmartsConnector not found with id ${params.id}"
            redirect(uri: "/admin.gsp")
        }
        else {
            return [smartsConnector: smartsConnector]
        }
    }


    def update = {
        SmartsConnector smartsConnector = SmartsConnector.get([id: params.id])
        if (smartsConnector) {
            if(params.type == "Topology" && smartsConnector instanceof SmartsListeningTopologyConnector)
            {
                Map connectorParams = ControllerUtils.getClassProperties(params, SmartsListeningTopologyConnector)
                smartsConnector.update(connectorParams);
            }
            else if(params.type == "Notification" && smartsConnector instanceof SmartsListeningNotificationConnector)
            {
                Map connectorParams = ControllerUtils.getClassProperties(params, SmartsListeningNotificationConnector)
                smartsConnector.update(connectorParams);
            }
            else
            {
                deleteConnector(smartsConnector);
                save();
                return;
            }
            if (!smartsConnector.hasErrors()) {
                def domain = params.domain;
                def domainType = params.domainType;
                SmartsConnection smartsConnection = smartsConnector.ds.connection
                connection.SmartsConnectionTemplate template = smartsConnector.connectionTemplate;
                def isConnectionParamsChanged = smartsConnection.brokerUsername != template.brokerUsername || smartsConnection.brokerPassword != template.brokerPassword || smartsConnection.broker != template.broker || smartsConnection.domain != domain || smartsConnection.domainType != domainType || smartsConnection.username != template.username || smartsConnection.userPassword != template.password
                if(isConnectionParamsChanged)
                {
                    def connectionParams = [domain:domain, domainType:domainType, broker:template.broker, username:template.username, userPassword:template.password, brokerUsername:template.brokerUsername, brokerPassword:template.brokerPassword]
                    smartsConnection.update(connectionParams);
                    if (!smartsConnection.hasErrors()) {
                        if(isConnectionParamsChanged)
                        {
                            def wasAdapterSubscribed = smartsConnector.ds.isSubscribed;
                            CmdbScript.stopListening(smartsConnector.getScriptName(smartsConnector.name));
                            if(wasAdapterSubscribed){
                                CmdbScript.startListening(smartsConnector.getScriptName(smartsConnector.name));    
                            }

                        }
                        redirect(uri: "/admin.gsp")
                    }
                    else {
                        render(view: 'edit', model: [smartsConnector: smartsConnector])
                    }
                }
                else
                {
                    redirect(uri: "/admin.gsp")    
                }
            }
            else {
                render(view: 'edit', model: [smartsConnector: smartsConnector])
            }
        }
        else {
            flash.message = "SmartsConnector not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def smartsConnector = null;
        if(params.type == "Topology")
        {
            smartsConnector = new SmartsListeningTopologyConnector()
        }
        else
        {
            smartsConnector = new SmartsListeningNotificationConnector()            
        }
        smartsConnector.properties = params
        return [smartsConnector: smartsConnector]
    }

    def save = {

        SmartsConnector smartsConnector = null;
        if(params.type == "Topology")
        {
            def connectorParams = ControllerUtils.getClassProperties(params, SmartsListeningTopologyConnector);
            smartsConnector = SmartsListeningTopologyConnector.add(connectorParams)
        }
        else
        {
            def connectorParams = ControllerUtils.getClassProperties(params, SmartsListeningNotificationConnector);
            smartsConnector = SmartsListeningNotificationConnector.add(connectorParams)            
        }
        if (!smartsConnector.hasErrors()) {
            def connectionName = smartsConnector.getConnectionName(smartsConnector.name);
            def connectionParams = [name:connectionName];
            connectionParams.broker = smartsConnector.connectionTemplate.broker
            connectionParams.username = smartsConnector.connectionTemplate.username
            connectionParams.userPassword = smartsConnector.connectionTemplate.password
            connectionParams.brokerUsername = smartsConnector.connectionTemplate.brokerUsername
            connectionParams.brokerPassword = smartsConnector.connectionTemplate.brokerPassword
            connectionParams.domain = params.domain
            connectionParams.domainType = params.domainType
            SmartsConnection smartsConnection = SmartsConnection.add(connectionParams)

            if (!smartsConnection.hasErrors()) {
                def datasourceName = smartsConnector.getDatasourceName(smartsConnector.name);
                def datasource = null;
                def scriptName = smartsConnector.getScriptName(smartsConnector.name);
                if(params.type == "Topology")
                {
                    CmdbScript script = CmdbScript.addScript(name:scriptName, scriptFile:"topologySubscriber",type:CmdbScript.LISTENING)
                    datasource = SmartsTopologyDatasource.add(name: datasourceName, connection: smartsConnection, listeningScript:script);
                }
                else
                {
                    CmdbScript script = CmdbScript.addScript(name:scriptName, scriptFile:"notificationSubscriber",type:CmdbScript.LISTENING)
                    datasource = SmartsNotificationDatasource.add(name: datasourceName, connection: smartsConnection, listeningScript:script);
                }
                smartsConnector.addRelation(ds:datasource);
                redirect(uri: "/admin.gsp")
            }
            else {
                smartsConnector.remove();
                render(view: 'create', model: [smartsConnector: smartsConnector, smartsConnection: smartsConnection])
            }

        }
        else {
            render(view: 'create', model: [smartsConnector: smartsConnector, smartsConnection: null])
        }
    }

    def startConnector = {
        SmartsConnector smartsConnector = SmartsConnector.get([id: params.id])
        if (!smartsConnector) {
            flash.message = "SmartsConnector not found with id ${params.id}"
            redirect(uri: '/admin.gsp');
        }
        else {
            def script = smartsConnector.ds.listeningScript
            try
            {
                def connTemplate = smartsConnector.connectionTemplate;
                def connectionParams = ["broker":connTemplate.broker, "username":connTemplate.username, "userPassword":connTemplate.password,
                    "brokerUsername":connTemplate.brokerUsername, "brokerPassword":connTemplate.brokerPassword];
                smartsConnector.ds.connection.update(connectionParams);
                if (checkConnection(smartsConnector.ds.connection.name)) {
                    CmdbScript.startListening(script.name);
                    flash.message = "Connector ${smartsConnector.name} successfully started"
                }
                else {
                    throw new Exception("Could not connect to smarts server.")
                }
            }
            catch (Throwable t)
            {
                t.printStackTrace();
                addError("connector.start.exception", [smartsConnector.name, t.toString()]);
                log.warn("", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                flash.errors = this.errors;
            }

            redirect(uri: '/admin.gsp');
        }
    }

    def stopConnector = {
        SmartsConnector smartsConnector = SmartsConnector.get([id: params.id])
        if (!smartsConnector) {
            flash.message = "SmartsConnector not found with id ${params.id}"
            redirect(uri: '/admin.gsp');
        }
        else {
            def script = smartsConnector.ds.listeningScript
            CmdbScript.stopListening(script.name);
            flash.message = "Connector ${smartsConnector.name} successfully stopped"
            redirect(uri: '/admin.gsp');
        }
    }


    def checkConnection(connectionName) {
        try {
            SmartsConnectionImpl conn = ConnectionManager.getConnection(connectionName);
            if (conn.isConnected()) {
                return true;
            }
        }
        catch (e) {
        }
        return false;
    }
}


