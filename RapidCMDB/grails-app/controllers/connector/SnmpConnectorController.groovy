/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package connector

import connection.SnmpConnection
import script.CmdbScript
import datasource.SnmpDatasource
import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 18, 2008
* Time: 2:22:42 PM
*/
class SnmpConnectorController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [snmpConnectorList: SnmpConnector.list(params)]
    }

    def show = {
        SnmpConnector snmpConnector = SnmpConnector.get([id: params.id])
        if (!snmpConnector) {
            flash.message = "SnmpConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [snmpConnector: snmpConnector]
        }
    }

    def delete = {
        SnmpConnector snmpConnector = SnmpConnector.get([id: params.id])
        if (snmpConnector) {
            try {
                deleteConnector(snmpConnector)
                flash.message = "SnmpConnector ${snmpConnector.name} deleted"
                redirect(action: list)
            }
            catch (e) {
                e.printStackTrace();
                addError("connector.delete.exception", [snmpConnector.name, e.getMessage()])
                flash.errors = this.errors;
                redirect(action: show, id: snmpConnector.id)
            }

        }
        else {
            flash.message = "SnmpConnector not found with id ${params.id}"
            redirect(action: list)
        }
    }


    def deleteConnector(SnmpConnector snmpConnector)
    {
        if(snmpConnector.script){
            CmdbScript.deleteScript(snmpConnector.script);    
        }
        snmpConnector.connection?.remove();
        snmpConnector.remove();
    }

    def edit = {
        SnmpConnector snmpConnector = SnmpConnector.get([id: params.id])
        if (!snmpConnector) {
            flash.message = "SnmpConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [snmpConnector: snmpConnector, script:snmpConnector.script, snmpConnection:snmpConnector.connection]
        }
    }


    def update = {
        SnmpConnector snmpConnector = SnmpConnector.get([id: params.id])
        if (snmpConnector) {
            snmpConnector.update(ControllerUtils.getClassProperties(params, SnmpConnector));
            if (!snmpConnector.hasErrors()) {
                params.name = snmpConnector.connection.name;
                def connectionParams = ControllerUtils.getClassProperties(params, SnmpConnection);
                def isConnectionChanged = connectionParams.host != snmpConnector.connection.host || snmpConnector.connection.port.toString() != connectionParams.port;
                if (isConnectionChanged) {
                    snmpConnector.connection.update(connectionParams);
                    if (!snmpConnector.connection.hasErrors()) {
                        CmdbScript.stopListening(snmpConnector.script.name)
                    }
                    else {
                        render(view: 'edit', model: [snmpConnector: snmpConnector, snmpConnection: snmpConnector.connection, script: snmpConnector.script])
                        return;
                    }
                }
                params.name = snmpConnector.name;
                def scriptClassParams = ControllerUtils.getClassProperties(params, CmdbScript);
                def isScriptChanged = scriptClassParams.name != snmpConnector.script.name || scriptClassParams.scriptFile != snmpConnector.script.scriptFile
                CmdbScript.updateScript(snmpConnector.script, scriptClassParams, true);
                if (!snmpConnector.script.hasErrors()) {
                    if (isScriptChanged) {
                        CmdbScript.stopListening(snmpConnector.script.name)
                    }
                    flash.message = "SnmpConnector with id ${params.id} successfully updated."
                    redirect(action: list)
                }
                else {
                   render(view: 'edit', model: [snmpConnector: snmpConnector, snmpConnection: snmpConnector.connection, script: snmpConnector.script])
                }

            }
            else {
                render(view: 'edit', model: [snmpConnector: snmpConnector, snmpConnection: snmpConnector.connection, script: snmpConnector.script])
            }
        }
        else {
            flash.message = "SnmpConnector not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def snmpConnector = new SnmpConnector();
        snmpConnector.properties = params
        return [snmpConnector: snmpConnector, snmpConnection: new SnmpConnection(), script: new CmdbScript()]
    }

    def save = {
        SnmpConnector snmpConnector = SnmpConnector.add(ControllerUtils.getClassProperties(params, SnmpConnector));
        if (!snmpConnector.hasErrors()) {
            params.name = snmpConnector.getConnectionName(snmpConnector.name);
            SnmpConnection snmpConnection = SnmpConnection.add(ControllerUtils.getClassProperties(params, SnmpConnection))
            if (!snmpConnection.hasErrors()) {
                snmpConnector.addRelation(connection:snmpConnection);
                def datasourceName = snmpConnector.getDatasourceName(snmpConnector.name);
                params.name=snmpConnector.name;
                params.type = CmdbScript.LISTENING;
                def scriptClassParams = ControllerUtils.getClassProperties(params, CmdbScript);
                CmdbScript script = CmdbScript.addScript(scriptClassParams, true);
                if (!script.hasErrors())
                {
                    def datasource = SnmpDatasource.add(name: datasourceName, connection: snmpConnection, listeningScript: script);
                    snmpConnector.addRelation(script: script);
                    if (!datasource.hasErrors())
                    {
                        redirect(action: list)
                    }
                    else
                    {
                        script.remove();
                        snmpConnector.remove();
                        render(view: 'create', model: [snmpConnector: snmpConnector, snmpConnection: snmpConnection, script: new CmdbScript()])
                    }
                }
                else
                {
                    snmpConnector.remove();
                    render(view: 'create', model: [snmpConnector: snmpConnector, snmpConnection: snmpConnection, script: script]);
                }
            }
            else {
                render(view: 'create', model: [snmpConnector: snmpConnector, snmpConnection: snmpConnection, script: new CmdbScript()])
            }

        }
        else {
            render(view: 'create', model: [snmpConnector: snmpConnector, snmpConnection: new SnmpConnection(), script: new CmdbScript()])
        }
    }

    def startConnector = {
        SnmpConnector snmpConnector = SnmpConnector.get([id: params.id])
        if (!snmpConnector) {
            flash.message = "SnmpConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def script = snmpConnector.script
            try
            {
                CmdbScript.startListening(script.name);
                flash.message = "Connector ${snmpConnector.name} successfully started"
            }
            catch (Throwable t)
            {
                t.printStackTrace();
                addError("connector.start.exception", [snmpConnector.name, t.toString()]);
                log.warn("", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                flash.errors = this.errors;
            }
            redirect(action: list)
        }
    }

    def stopConnector = {
        SnmpConnector snmpConnector = SnmpConnector.get([id: params.id])
        if (!snmpConnector) {
            flash.message = "SnmpConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def script = snmpConnector.script
            CmdbScript.stopListening(script.name);
            flash.message = "Connector ${snmpConnector.name} successfully stopped"
            redirect(action: list)
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("connector.SnmpConnector")
        if (modelClass)
        {
            try
            {

                modelClass.metaClass.invokeStaticMethod(modelClass, "reloadOperations", [] as Object[]);
                flash.message = "Model operations reloaded"
                redirect(action:list)
            } catch (t)
            {
                flash.message = "Exception occurred while reloading model operations Reason:${t.toString()}"
                 redirect(action:list)
            }
        }
        else
        {
            flash.message = "Model currently not loaded by application. You should reload application."
            redirect(action:list)
        }
    }
}