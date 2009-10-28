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
package connection

import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.text.SimpleDateFormat;
class ConnectionController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.sort) params.sort = "name"
        [connectionList: Connection.search("alias:*", params).results]
    }

    def show = {
        def connection = Connection.get([id: params.id])

        if (!connection) {
            flash.message = "Connection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (connection.class != Connection)
            {
                def controllerName = connection.class.simpleName;
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
                return [connection: connection]
            }
        }
    }

    def delete = {
        def connection = Connection.get([id: params.id])
        if (connection) {
            connection.remove()
            flash.message = "Connection ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "Connection not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def connection = Connection.get([id: params.id])

        if (!connection) {
            flash.message = "Connection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [connection: connection]
        }
    }


    def update = {
        def connection = Connection.get([id: params.id])
        if (connection) {
            connection.update(ControllerUtils.getClassProperties(params, Connection));
            if (!connection.hasErrors()) {
                flash.message = "Connection ${params.id} updated"
                redirect(action: show, id: connection.id)
            }
            else {
                render(view: 'edit', model: [connection: connection])
            }
        }
        else {
            flash.message = "Connection not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def connection = new Connection()
        connection.properties = params
        return ['connection': connection]
    }

    def save = {
        def connection = Connection.add(ControllerUtils.getClassProperties(params, Connection))
        if (!connection.hasErrors()) {
            flash.message = "Connection ${connection.id} created"
            redirect(action: show, id: connection.id)
        }
        else {
            render(view: 'create', model: [connection: connection])
        }
    }

    def addTo = {
        def connection = Connection.get([id: params.id])
        if (!connection) {
            flash.message = "Connection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(connection.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [connection: connection, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: connection.id)
            }
        }
    }

    def addRelation = {
        def connection = Connection.get([id: params.id])
        if (!connection) {
            flash.message = "Connection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(connection.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    connection.addRelation(relationMap);
                    if (connection.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [connection: connection, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "Connection ${params.id} updated"
                        redirect(action: edit, id: connection.id)
                    }

                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: addTo, id: params.id, relationName: relationName)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: addTo, id: params.id, relationName: relationName)
            }
        }
    }

    def removeRelation = {
        def connection = Connection.get([id: params.id])
        if (!connection) {
            flash.message = "Connection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(connection.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    connection.removeRelation(relationMap);
                    if (connection.hasErrors()) {
                        render(view: 'edit', model: [connection: connection])
                    }
                    else {
                        flash.message = "Connection ${params.id} updated"
                        redirect(action: edit, id: connection.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: connection.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: connection.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("connection.Connection")
        if (modelClass)
        {
            try
            {

                modelClass.metaClass.invokeStaticMethod(modelClass, "reloadOperations", [] as Object[]);
                flash.message = "Model operations reloaded"
                redirect(action: list)
            } catch (t)
            {
                flash.message = "Exception occurred while reloading model operations Reason:${t.toString()}"
                redirect(action: list)
            }
        }
        else
        {
            flash.message = "Model currently not loaded by application. You should reload application."
            redirect(action: list)
        }
    }
}