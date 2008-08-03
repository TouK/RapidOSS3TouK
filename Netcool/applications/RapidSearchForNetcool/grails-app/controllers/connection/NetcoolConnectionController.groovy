package connection

import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.text.SimpleDateFormat
import groovy.text.SimpleTemplateEngine
import datasource.NetcoolDatasource
import script.CmdbScript;
class NetcoolConnectionController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    def final static PROPS_TO_BE_EXCLUDED = ["id": "id", "_action_Update": "_action_Update", "controller": "controller", "action": "action"]
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [netcoolConnectionList: NetcoolConnection.list(params)]
    }

    def show = {
        def netcoolConnection = NetcoolConnection.get([id: params.id])

        if (!netcoolConnection) {
            flash.message = "NetcoolConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (netcoolConnection.class != NetcoolConnection)
            {
                def controllerName = netcoolConnection.class.name;
                if (controllerName.length() == 1)
                {
                    controllerName = controllerName.toLowerCase();
                }
                else
                {
                    controllerName = controllerName.substring(0, 1).toLowerCase() + controllerName.substring(1);
                }
                redirect(action: show, controller: controllerName, id: params.id)
            }
            else
            {
                return [netcoolConnection: netcoolConnection]
            }
        }
    }

    def delete = {
        NetcoolConnection netcoolConnection = NetcoolConnection.get([id: params.id])
        if (netcoolConnection) {
            try {
                netcoolConnection.remove();
                datasource.NetcoolDatasource.get(name: netcoolConnection.name)?.remove();
                def script = CmdbScript.get(name: "${datasource.name}Connector")
                CmdbScript.deleteScript(script);
                new File("${System.getProperty("base.dir")}/scripts/${datasource.name}Connector.groovy").delete();
                flash.message = "NetcoolConnection ${params.id} deleted"
                redirect(action: list)
            }
            catch (e) {
                def errors = [message(code: "default.couldnot.delete", args: [NetcoolConnection, netcoolConnection])]
                flash.errors = errors;
                redirect(action: show, id: netcoolConnection.id)
            }

        }
        else {
            flash.message = "NetcoolConnection not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def netcoolConnection = NetcoolConnection.get([id: params.id])

        if (!netcoolConnection) {
            flash.message = "NetcoolConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [netcoolConnection: netcoolConnection]
        }
    }


    def update = {
        def netcoolConnection = NetcoolConnection.get([id: params.id])
        if (netcoolConnection) {
            netcoolConnection.update(ControllerUtils.getClassProperties(params, NetcoolConnection));
            if (!netcoolConnection.hasErrors()) {
                flash.message = "NetcoolConnection ${params.id} updated"
                redirect(action: show, id: netcoolConnection.id)
            }
            else {
                render(view: 'edit', model: [netcoolConnection: netcoolConnection])
            }
        }
        else {
            flash.message = "NetcoolConnection not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def netcoolConnection = new NetcoolConnection()
        netcoolConnection.properties = params
        return ['netcoolConnection': netcoolConnection]
    }

    def save = {
        NetcoolConnection netcoolConnection = NetcoolConnection.add(ControllerUtils.getClassProperties(params, NetcoolConnection))
        if (!netcoolConnection.hasErrors()) {
            def datasource = datasource.NetcoolDatasource.add(name: netcoolConnection.name, connection: netcoolConnection);
            createConnectorScript(datasource);
            flash.message = "NetcoolConnection ${netcoolConnection.id} created"
            redirect(action: show, id: netcoolConnection.id)
        }
        else {
            render(view: 'create', model: [netcoolConnection: netcoolConnection])
        }
    }

    def startConnector = {
        def netcoolConnection = NetcoolConnection.get([id: params.id])
        if (!netcoolConnection) {
            flash.message = "NetcoolConnector not found with id ${params.id}"
            redirect(uri: '/admin.gsp');
        }
        else {
            connector.NetcoolConnectorFactory.removeConnector(netcoolConnection.name);
            def scriptName = "${netcoolConnection.name}Connector";
            def script = CmdbScript.updateScript(CmdbScript.get(name: scriptName), [enabled: true], true);
            flash.message = "Connectors successfully started"
            redirect(uri: '/admin.gsp');
        }
    }

    def stopConnector = {
        def netcoolConnection = NetcoolConnection.get([id: params.id])
        if (!netcoolConnection) {
            flash.message = "NetcoolConnector not found with id ${params.id}"
            redirect(uri: '/admin.gsp');
        }
        else{
            connector.NetcoolConnectorFactory.removeConnector(netcoolConnection.name);
            def scriptName = "${netcoolConnection.name}Connector";
            def script = CmdbScript.updateScript(CmdbScript.get(name: scriptName), [enabled: false], true);
            flash.message = "Connectors successfully stopped"
            redirect(uri: '/admin.gsp');
        }
    }


    def createConnectorScript(NetcoolDatasource datasource)
    {
        if (CmdbScript.get(name: "getConversionParameters") == null)
        {
            CmdbScript.addScript(name: "getConversionParameters", enabled: false, type: CmdbScript.SCHEDULED, period: 3600);
            try
            {
                CmdbScript.runScript("getConversionParameters", [:])
            } catch (Throwable e)
            {
            }
            CmdbScript.addScript(name: "getConversionParameters", enabled: true, type: CmdbScript.SCHEDULED, period: 3600);
        }
        def scriptName = "${datasource.name}Connector";
        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        def template = engine.createTemplate(new File("${System.getProperty("base.dir")}/grails-app/templates/groovy/NetcoolConnectorScriptTemplate.txt"))
        def fw = new FileWriter(new File("${System.getProperty("base.dir")}/scripts/${scriptName}.groovy"));
        template.make([datasourceName: datasource.name]).writeTo(fw);
        fw.close();
        CmdbScript.addScript(name: scriptName, enabled: false, type: CmdbScript.SCHEDULED, period: 30);
    }
}