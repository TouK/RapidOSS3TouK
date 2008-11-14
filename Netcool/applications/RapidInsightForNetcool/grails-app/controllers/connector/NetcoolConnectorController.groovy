package connector;
import com.ifountain.rcmdb.domain.util.ControllerUtils
import connection.NetcoolConnection
import groovy.text.SimpleTemplateEngine
import datasource.NetcoolDatasource
import script.CmdbScript
import com.ifountain.core.connection.ConnectionManager;


class NetcoolConnectorController {
    def final static PROPS_TO_BE_EXCLUDED = ["id": "id", "_action_Update": "_action_Update", "controller": "controller", "action": "action"]
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        redirect(uri: '/netcoolAdmin.gsp');
    }

    def show = {
        def netcoolConnector = NetcoolConnector.get([id: params.id])

        if (!netcoolConnector) {
            flash.message = "NetcoolConnector not found with id ${params.id}"
            redirect(uri: "/netcoolAdmin.gsp")
        }
        else {
            def connectionName = NetcoolConnector.getConnectionName(netcoolConnector.name);
            def netcoolConnection = NetcoolConnection.findByName(connectionName)
            if (!netcoolConnector) {
                flash.message = "NetcoolConnection not found with name ${connectionName}"
                redirect(uri: "/netcoolAdmin.gsp")
            }
            else {
                return [netcoolConnector: netcoolConnector, netcoolConnection: netcoolConnection]
            }
        }
    }

    def delete = {
        def netcoolConnector = NetcoolConnector.get([id: params.id])
        if (netcoolConnector) {
            try {
                def connectionName = NetcoolConnector.getConnectionName(netcoolConnector.name)
                def datasourceName = NetcoolConnector.getDatasourceName(netcoolConnector.name)
                def scriptName = NetcoolConnector.getScriptName(netcoolConnector.name)
                def script = CmdbScript.get(name: scriptName)
                CmdbScript.deleteScript(script);
                new File("${System.getProperty("base.dir")}/scripts/${scriptName}.groovy").delete();
                NetcoolConnectorFactory.removeConnector(netcoolConnector.name);
                NetcoolConnection.get(name: connectionName)?.remove();
                NetcoolDatasource.get(name: datasourceName)?.remove();
                netcoolConnector.remove()
                flash.message = "NetcoolConnector ${netcoolConnector.name} deleted"
                redirect(uri: "/netcoolAdmin.gsp")
            }
            catch (e) {
                addError("connector.delete.exception", [netcoolConnector.name, e.getMessage()])
                flash.errors = this.errors;
                redirect(action: show, id: netcoolConnector.id)
            }

        }
        else {
            flash.message = "NetcoolConnector not found with id ${params.id}"
            redirect(uri: "/netcoolAdmin.gsp")
        }
    }

    def edit = {
        def netcoolConnector = NetcoolConnector.get([id: params.id])

        if (!netcoolConnector) {
            flash.message = "NetcoolConnector not found with id ${params.id}"
            redirect(uri: "/netcoolAdmin.gsp")
        }
        else {
            def connectionName = NetcoolConnector.getConnectionName(netcoolConnector.name);
            def netcoolConnection = NetcoolConnection.findByName(connectionName)
            if (!netcoolConnector) {
                flash.message = "NetcoolConnection not found with name ${connectionName}"
                redirect(uri: "/netcoolAdmin.gsp")
            }
            else {
                return [netcoolConnector: netcoolConnector, netcoolConnection: netcoolConnection]
            }
        }
    }


    def update = {
        def netcoolConnector = NetcoolConnector.get([id: params.id])
        if (netcoolConnector) {
            def connectorParams = ControllerUtils.getClassProperties(params, NetcoolConnector)
            def connectionParams = ControllerUtils.getClassProperties(params, NetcoolConnection)
            connectorParams.name = netcoolConnector.name;
            connectionParams.name = NetcoolConnector.getConnectionName(netcoolConnector.name);
            netcoolConnector.update(connectorParams);
            if (!netcoolConnector.hasErrors()) {
                def netcoolConnection = NetcoolConnection.get(name: connectionParams.name);
                def tempConnection = new NetcoolConnection(connectionParams);
                if (netcoolConnection.host == tempConnection.host && netcoolConnection.port == tempConnection.port) {
                    NetcoolConnectorFactory.setLogLevel(netcoolConnector.name, netcoolConnector.logLevel);
                    flash.message = "NetcoolConnector ${netcoolConnector.name} updated"
                     redirect(uri: "/netcoolAdmin.gsp")
                }
                else {
                    netcoolConnection.update(connectionParams);
                    if (!netcoolConnection.hasErrors()) {
                        NetcoolConnectorFactory.removeConnector(netcoolConnector.name);
                        createConversionScript();
                        if (checkConnection(connectionParams.name)) {
                            flash.message = "NetcoolConnector ${netcoolConnector.name} updated"
                        }
                        else {
                            flash.message = "NetcoolConnector ${netcoolConnector.name} updated, but connection could not be established"
                        }
                        redirect(uri: "/netcoolAdmin.gsp")
                    }
                    else {
                        render(view: 'edit', model: [netcoolConnector: netcoolConnector, netcoolConnection: netcoolConnection])
                    }
                }
            }
            else {
                render(view: 'edit', model: [netcoolConnector: netcoolConnector, netcoolConnection: new NetcoolConnection(connectionParams)])
            }
        }
        else {
            flash.message = "NetcoolConnector not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def netcoolConnector = new NetcoolConnector()
        netcoolConnector.properties = params
        def netcoolConnection = new NetcoolConnection(port: 4100, username: 'root');
        return ['netcoolConnector': netcoolConnector, 'netcoolConnection': netcoolConnection]
    }

    def save = {
        def connectorParams = ControllerUtils.getClassProperties(params, NetcoolConnector)
        def connectionParams = ControllerUtils.getClassProperties(params, NetcoolConnection)
        def netcoolConnector = NetcoolConnector.add(connectorParams)
        if (!netcoolConnector.hasErrors()) {
            def connectionName = NetcoolConnector.getConnectionName(netcoolConnector.name);
            connectionParams.name = connectionName;
            def netcoolConnection = NetcoolConnection.add(connectionParams)
            if (!netcoolConnection.hasErrors()) {
                def datasourceName = NetcoolConnector.getDatasourceName(netcoolConnector.name)
                def datasource = datasource.NetcoolDatasource.add(name: datasourceName, connection: netcoolConnection);
                createConversionScript();
                createConnectorScript(netcoolConnector.name);
                if (ConnectionManager.checkConnection(connectionParams.name)) {
                    flash.message = "NetcoolConnector ${netcoolConnector.name} created"
                }
                else {
                    flash.message = "NetcoolConnector ${netcoolConnector.name} created, but connection could not be established"
                }
                 redirect(uri: "/netcoolAdmin.gsp")
            }
            else {
                netcoolConnector.remove();
                render(view: 'create', model: [netcoolConnector: netcoolConnector, netcoolConnection: netcoolConnection])
            }

        }
        else {
            render(view: 'create', model: [netcoolConnector: netcoolConnector, netcoolConnection: new NetcoolConnection(connectionParams)])
        }
    }

    def startConnector = {
        def netcoolConnector = NetcoolConnector.get([id: params.id])
        if (!netcoolConnector) {
            flash.message = "NetcoolConnector not found with id ${params.id}"
            redirect(uri: '/netcoolAdmin.gsp');
        }
        else {
            NetcoolConnectorFactory.removeConnector(netcoolConnector.name);
            def scriptName = NetcoolConnector.getScriptName(netcoolConnector.name)
            def connName = NetcoolConnector.getConnectionName(netcoolConnector.name)
            def script = CmdbScript.get(name: scriptName);
            try
            {
                if (ConnectionManager.checkConnection(connName)) {
                    CmdbScript.updateScript(script, [enabled: true], false);
                    flash.message = "Connector ${netcoolConnector.name} successfully started"
                }
                else {
                    throw new Exception("Could not connect to netcool server.")
                }
            }
            catch (Throwable t)
            {
                NetcoolConnectorFactory.removeConnector(netcoolConnector.name);
                addError("connector.start.exception", [netcoolConnector.name, t.toString()]);
                log.warn("", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                flash.errors = this.errors;
            }

            redirect(uri: '/netcoolAdmin.gsp');
        }
    }

    def stopConnector = {
        def netcoolConnector = NetcoolConnector.get([id: params.id])
        if (!netcoolConnector) {
            flash.message = "NetcoolConnector not found with id ${params.id}"
            redirect(uri: '/netcoolAdmin.gsp');
        }
        else {
            NetcoolConnectorFactory.removeConnector(netcoolConnector.name);
            def scriptName = NetcoolConnector.getScriptName(netcoolConnector.name)
            def script = CmdbScript.updateScript(CmdbScript.get(name: scriptName), [enabled: false], true);
            flash.message = "Connector ${netcoolConnector.name} successfully stopped"
            redirect(uri: '/netcoolAdmin.gsp');
        }
    }

    def createConversionScript()
    {
        if (CmdbScript.get(name: "getConversionParameters") == null)
        {
            CmdbScript.addScript(name: "getConversionParameters", enabled: false, type: CmdbScript.SCHEDULED, period: 3600);
            try
            {
                CmdbScript.runScript("getConversionParameters", [:])
                CmdbScript.addScript(name: "getConversionParameters", enabled: true, type: CmdbScript.SCHEDULED, period: 3600);
            } catch (Throwable e)
            {
                CmdbScript.deleteScript("getConversionParameters");
            }

        }
    }
    def createConnectorScript(connectorName)
    {
        def scriptName = NetcoolConnector.getScriptName(connectorName);
        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        def template = engine.createTemplate(new File("${System.getProperty("base.dir")}/grails-app/templates/groovy/NetcoolConnectorScriptTemplate.txt"))
        def fw = new FileWriter(new File("${System.getProperty("base.dir")}/scripts/${scriptName}.groovy"));
        template.make([connectorName: connectorName]).writeTo(fw);
        fw.close();
        CmdbScript.addScript(name: scriptName, enabled: false, type: CmdbScript.SCHEDULED, period: 30);
    }
}