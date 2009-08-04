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
import datasource.BaseDatasource
import com.ifountain.rcmdb.exception.MessageSourceException

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
                SnmpConnector.deleteConnector(snmpConnector)
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


    def edit = {
        SnmpConnector snmpConnector = SnmpConnector.get([id: params.id])
        if (!snmpConnector) {
            flash.message = "SnmpConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [snmpConnector: snmpConnector, script: snmpConnector.script, snmpConnection: snmpConnector.connection]
        }
    }

    def updateLogLevel = {
        SnmpConnector snmpConnector = SnmpConnector.get([id: params.id])
        if (snmpConnector) {
            CmdbScript.updateLogLevel(snmpConnector.script, params.logLevel ? params.logLevel : snmpConnector.script.logLevel, true);
            if (!snmpConnector.script.hasErrors()) {
                flash.message = "SnmpConnector with id ${params.id} successfully updated."
                redirect(action: list)
            }
            else {
                flash.errors = snmpConnector.script.errors;
                render(action: 'list')
            }
        }
        else {
            flash.message = "SnmpConnector not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def update = {
        SnmpConnector snmpConnector = SnmpConnector.get([id: params.id])
        if (snmpConnector) {
            def connectorParams = ControllerUtils.getClassProperties(params, CmdbScript);
            connectorParams.putAll(ControllerUtils.getClassProperties(params, SnmpConnection));
            connectorParams.putAll(ControllerUtils.getClassProperties(params, SnmpConnector));
            def updatedObjects = SnmpConnector.updateConnector(snmpConnector, connectorParams);
            MessageSourceException ex = updatedObjects.remove("exception");
            def objectWithErrors = updatedObjects.values().findAll {it.hasErrors()};
            if (ex != null || objectWithErrors.size() > 0) {
                if (ex != null) {
                    addError(ex.code, Arrays.asList(ex.args))
                    flash.errors = this.errors;
                }
                render(view: 'edit', model: updatedObjects)
            }
            else {
                flash.message = "SnmpConnector with id ${params.id} successfully updated."
                redirect(action: show, id: snmpConnector.id)
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
        def connectorParams = ControllerUtils.getClassProperties(params, CmdbScript);
        connectorParams.putAll(ControllerUtils.getClassProperties(params, SnmpConnection));
        connectorParams.putAll(ControllerUtils.getClassProperties(params, SnmpConnector));
        def createdObjects = SnmpConnector.addConnector(connectorParams);
        def objectsWithError = createdObjects.values().findAll {it.hasErrors()};
        if (objectsWithError.size() > 0) {
            render(view: 'create', model: createdObjects)
        }
        else {
            flash.message = "SnmpConnector ${createdObjects["snmpConnector"].id} created"
            redirect(action: show, id: createdObjects["snmpConnector"].id)
        }
    }

    def startConnector = {
        SnmpConnector snmpConnector = SnmpConnector.get([id: params.id])
        if (!snmpConnector) {
            flash.message = "SnmpConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            try
            {
                SnmpConnector.startConnector(snmpConnector)
                flash.message = "Connector ${snmpConnector.name} successfully started"
            }
            catch (Throwable t)
            {
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
            try
            {
                SnmpConnector.stopConnector(snmpConnector)
                flash.message = "Connector ${snmpConnector.name} successfully stopped"
            }
            catch (Throwable t)
            {
                addError("connector.stop.exception", [snmpConnector.name, t.toString()]);
                log.warn("", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                flash.errors = this.errors;
            }
            redirect(action: list)
        }
    }
    
}