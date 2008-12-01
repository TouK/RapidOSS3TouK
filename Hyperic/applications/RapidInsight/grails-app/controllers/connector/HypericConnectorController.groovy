package connector

import com.ifountain.rcmdb.domain.util.ControllerUtils
import datasource.HypericDatasource
import script.CmdbScript

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Dec 1, 2008
* Time: 3:02:27 PM
*/
class HypericConnectorController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        redirect(uri: '/hypericAdmin.gsp');
    }

    def show = {
        HypericConnector hypericConnector = HypericConnector.get([id: params.id])
        if (!hypericConnector) {
            flash.message = "HypericConnector not found with id ${params.id}"
            redirect(uri: "/hypericAdmin.gsp")
        }
        else {
            return [hypericConnector: hypericConnector]
        }
    }

    def delete = {
        HypericConnector hypericConnector = HypericConnector.get([id: params.id])
        if (hypericConnector) {
            try {
                deleteConnector(hypericConnector)
                flash.message = "HypericConnector ${hypericConnector.name} deleted"
                redirect(uri: "/hypericAdmin.gsp")
            }
            catch (e) {
                e.printStackTrace();
                addError("connector.delete.exception", [hypericConnector.name, e.getMessage()])
                flash.errors = this.errors;
                redirect(action: show, id: hypericConnector.id)
            }

        }
        else {
            flash.message = "HypericConnector not found with id ${params.id}"
            redirect(uri: "/hypericAdmin.gsp")
        }
    }


    def deleteConnector(HypericConnector hypericConnector)
    {
        CmdbScript.deleteScript(hypericConnector.script);
        hypericConnector.datasource.remove();
        hypericConnector.remove();
    }

    def edit = {
        HypericConnector hypericConnector = HypericConnector.get([id: params.id])

        if (!hypericConnector) {
            flash.message = "HypericConnector not found with id ${params.id}"
            redirect(uri: "/hypericAdmin.gsp")
        }
        else {
            return [hypericConnector: hypericConnector, script: hypericConnector.script, datasource: hypericConnector.datasource]
        }
    }


    def update = {
        HypericConnector hypericConnector = HypericConnector.get([id: params.id])
        if (hypericConnector) {
            if (params.type != hypericConnector.type) {
                deleteConnector(hypericConnector)
                save()
                return;
            }
            else {
                hypericConnector.update(ControllerUtils.getClassProperties(params, HypericConnector));
                if (!hypericConnector.hasErrors()) {
                    def scriptFile = HypericConnector.getScriptFile(hypericConnector);
                    def scriptParams = [scriptFile: scriptFile, logFile: hypericConnector.name, logLevel: params.logLevel, staticParam: hypericConnector.name, period: params.period]
                    def scriptClassParams = ControllerUtils.getClassProperties(scriptParams, CmdbScript);
                    scriptClassParams["logFileOwn"] = true;
                    CmdbScript script = CmdbScript.updateScript(hypericConnector.script, scriptClassParams, true);
                    if (!script.hasErrors()) {
                        def datasource = hypericConnector.datasource.update(name: hypericConnector.name, connection: hypericConnection)
                        if (!datasource.hasErrors())
                        {

                            redirect(uri: "/hypericAdmin.gsp")
                        }
                        else
                        {

                            render(view: 'edit', model: [hypericConnector: hypericConnector, script: script, datasource: datasource])
                        }
                    }
                    else {
                        render(view: 'edit', model: [hypericConnector: hypericConnector, script: script, datasource: hypericConnector.datasource])
                    }
                }
                else {
                    render(view: 'edit', model: [hypericConnector: hypericConnector, script: hypericConnector.script, datasource: hypericConnector.datasource])
                }
            }
        }
        else {
            flash.message = "HypericConnector not found with id ${params.id}"
            redirect(uri: '/hypericAdmin.gsp');
        }
    }

    def create = {
        def hypericConnector = new HypericConnector();
        hypericConnector.properties = params
        return [hypericConnector: hypericConnector, script: new CmdbScript(), datasource: new HypericDatasource()]
    }

    def save = {
        HypericConnector hypericConnector = HypericConnector.add(ControllerUtils.getClassProperties(params, HypericConnector));
        if (!hypericConnector.hasErrors()) {
            def scriptName = HypericConnector.getScriptName(hypericConnector);
            def scriptFile = HypericConnector.getScriptFile(hypericConnector);
            def scriptParams = [name: scriptName, scriptFile: scriptFile, type: CmdbScript.SCHEDULED, logFile: hypericConnector.name, logLevel: params.logLevel, staticParam: hypericConnector.name, period: params.period]
            def scriptClassParams = ControllerUtils.getClassProperties(scriptParams, CmdbScript);
            scriptClassParams["logFileOwn"] = true;
            CmdbScript script = CmdbScript.addScript(scriptClassParams, true);
            if (!script.hasErrors())
            {
                hypericConnector.addRelation(script: script);
                def datasource = HypericDatasource.add(name: hypericConnector.name, connection: hypericConnector.connection)
                if (!datasource.hasErrors())
                {
                    hypericConnector.addRelation(datasource: datasource);
                    redirect(uri: "/hypericAdmin.gsp")
                }
                else
                {
                    script.remove();
                    hypericConnector.remove();
                    render(view: 'create', model: [hypericConnector: hypericConnector, script: script, datasource: datasource])
                }
            }
            else
            {
                hypericConnector.remove();
                render(view: 'create', model: [hypericConnector: hypericConnector, script: script, datasource: new HypericDatasource()]);
            }
        }
        else {
            render(view: 'create', model: [hypericConnector: hypericConnector, script: new CmdbScript(), datasource: new HypericDatasource()])
        }
    }

    def startConnector = {
        HypericConnector hypericConnector = HypericConnector.get([id: params.id])
        if (!hypericConnector) {
            flash.message = "HypericConnector not found with id ${params.id}"
            redirect(uri: '/hypericAdmin.gsp');
        }
        else {
            try
            {
                CmdbScript.updateScript(hypericConnector.script, [enabled: true], false)
            }
            catch (Throwable t)
            {
                t.printStackTrace();
                addError("connector.start.exception", [hypericConnector.name, t.toString()]);
                log.warn("", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                flash.errors = this.errors;
            }

            redirect(uri: '/hypericAdmin.gsp');
        }
    }

    def stopConnector = {
        HypericConnector hypericConnector = HypericConnector.get([id: params.id])
        if (!hypericConnector) {
            flash.message = "HypericConnector not found with id ${params.id}"
            redirect(uri: '/hypericAdmin.gsp');
        }
        else {
            CmdbScript.updateScript(hypericConnector.script, [enabled: false], false)
            flash.message = "Connector ${hypericConnector.name} successfully stopped"
            redirect(uri: '/hypericAdmin.gsp');
        }
    }
}