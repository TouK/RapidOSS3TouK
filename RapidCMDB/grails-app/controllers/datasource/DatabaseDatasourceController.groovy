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
package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.text.SimpleDateFormat;
class DatabaseDatasourceController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.sort) params.sort = "name"
        [databaseDatasourceList: DatabaseDatasource.list(params)]
    }

    def show = {
        def databaseDatasource = DatabaseDatasource.get([id: params.id])

        if (!databaseDatasource) {
            flash.message = "DatabaseDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (databaseDatasource.class != DatabaseDatasource)
            {
                def controllerName = databaseDatasource.class.simpleName;
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
                return [databaseDatasource: databaseDatasource]
            }
        }
    }

    def delete = {
        def databaseDatasource = DatabaseDatasource.get([id: params.id])
        if (databaseDatasource) {
            databaseDatasource.remove()
            flash.message = "DatabaseDatasource ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "DatabaseDatasource not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def databaseDatasource = DatabaseDatasource.get([id: params.id])

        if (!databaseDatasource) {
            flash.message = "DatabaseDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [databaseDatasource: databaseDatasource]
        }
    }


    def update = {
        def databaseDatasource = DatabaseDatasource.get([id: params.id])
        if (databaseDatasource) {
            databaseDatasource.update(ControllerUtils.getClassProperties(params, DatabaseDatasource));
            if (!databaseDatasource.hasErrors()) {
                flash.message = "DatabaseDatasource ${params.id} updated"
                redirect(action: show, id: databaseDatasource.id)
            }
            else {
                render(view: 'edit', model: [databaseDatasource: databaseDatasource])
            }
        }
        else {
            flash.message = "DatabaseDatasource not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def databaseDatasource = new DatabaseDatasource()
        databaseDatasource.properties = params
        return ['databaseDatasource': databaseDatasource]
    }

    def save = {
        def databaseDatasource = DatabaseDatasource.add(ControllerUtils.getClassProperties(params, DatabaseDatasource))
        if (!databaseDatasource.hasErrors()) {
            flash.message = "DatabaseDatasource ${databaseDatasource.id} created"
            redirect(action: show, id: databaseDatasource.id)
        }
        else {
            render(view: 'create', model: [databaseDatasource: databaseDatasource])
        }
    }

    def addTo = {
        def databaseDatasource = DatabaseDatasource.get([id: params.id])
        if (!databaseDatasource) {
            flash.message = "DatabaseDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(databaseDatasource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [databaseDatasource: databaseDatasource, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: databaseDatasource.id)
            }
        }
    }

    def addRelation = {
        def databaseDatasource = DatabaseDatasource.get([id: params.id])
        if (!databaseDatasource) {
            flash.message = "DatabaseDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(databaseDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    databaseDatasource.addRelation(relationMap);
                    if (databaseDatasource.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [databaseDatasource: databaseDatasource, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "DatabaseDatasource ${params.id} updated"
                        redirect(action: edit, id: databaseDatasource.id)
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
        def databaseDatasource = DatabaseDatasource.get([id: params.id])
        if (!databaseDatasource) {
            flash.message = "DatabaseDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(databaseDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    databaseDatasource.removeRelation(relationMap);
                    if (databaseDatasource.hasErrors()) {
                        render(view: 'edit', model: [databaseDatasource: databaseDatasource])
                    }
                    else {
                        flash.message = "DatabaseDatasource ${params.id} updated"
                        redirect(action: edit, id: databaseDatasource.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: databaseDatasource.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: databaseDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("datasource.DatabaseDatasource")
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