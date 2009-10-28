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
class HttpDatasourceController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.sort) params.sort = "name"
        [httpDatasourceList: HttpDatasource.search("alias:*", params).results]
    }

    def show = {
        def httpDatasource = HttpDatasource.get([id: params.id])

        if (!httpDatasource) {
            flash.message = "HttpDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (httpDatasource.class != HttpDatasource)
            {
                def controllerName = httpDatasource.class.simpleName;
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
                return [httpDatasource: httpDatasource]
            }
        }
    }

    def delete = {
        def httpDatasource = HttpDatasource.get([id: params.id])
        if (httpDatasource) {
            httpDatasource.remove()
            flash.message = "HttpDatasource ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "HttpDatasource not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def httpDatasource = HttpDatasource.get([id: params.id])

        if (!httpDatasource) {
            flash.message = "HttpDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [httpDatasource: httpDatasource]
        }
    }

    def update = {
        def httpDatasource = HttpDatasource.get([id: params.id])
        if (httpDatasource) {
            httpDatasource.update(ControllerUtils.getClassProperties(params, HttpDatasource));
            if (!httpDatasource.hasErrors()) {
                flash.message = "HttpDatasource ${params.id} updated"
                redirect(action: show, id: httpDatasource.id)
            }
            else {
                render(view: 'edit', model: [httpDatasource: httpDatasource])
            }
        }
        else {
            flash.message = "HttpDatasource not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def httpDatasource = new HttpDatasource()
        httpDatasource.properties = params
        return ['httpDatasource': httpDatasource]
    }

    def save = {
        def httpDatasource = HttpDatasource.add(ControllerUtils.getClassProperties(params, HttpDatasource))
        if (!httpDatasource.hasErrors()) {
            flash.message = "HttpDatasource ${httpDatasource.id} created"
            redirect(action: show, id: httpDatasource.id)
        }
        else {
            render(view: 'create', model: [httpDatasource: httpDatasource])
        }
    }

    def addTo = {
        def httpDatasource = HttpDatasource.get([id: params.id])
        if (!httpDatasource) {
            flash.message = "HttpDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(httpDatasource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [httpDatasource: httpDatasource, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: httpDatasource.id)
            }
        }
    }

    def addRelation = {
        def httpDatasource = HttpDatasource.get([id: params.id])
        if (!httpDatasource) {
            flash.message = "HttpDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(httpDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    httpDatasource.addRelation(relationMap);
                    if (httpDatasource.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [httpDatasource: httpDatasource, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "HttpDatasource ${params.id} updated"
                        redirect(action: edit, id: httpDatasource.id)
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
        def httpDatasource = HttpDatasource.get([id: params.id])
        if (!httpDatasource) {
            flash.message = "HttpDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(httpDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    httpDatasource.removeRelation(relationMap);
                    if (httpDatasource.hasErrors()) {
                        render(view: 'edit', model: [httpDatasource: httpDatasource])
                    }
                    else {
                        flash.message = "HttpDatasource ${params.id} updated"
                        redirect(action: edit, id: httpDatasource.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: httpDatasource.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: httpDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("datasource.HttpDatasource")
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