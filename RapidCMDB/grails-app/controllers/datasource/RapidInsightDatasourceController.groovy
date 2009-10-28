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
class RapidInsightDatasourceController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.sort) params.sort = "name"
        [rapidInsightDatasourceList: RapidInsightDatasource.search("alias:*", params).results]
    }

    def show = {
        def rapidInsightDatasource = RapidInsightDatasource.get([id: params.id])

        if (!rapidInsightDatasource) {
            flash.message = "RapidInsightDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (rapidInsightDatasource.class != RapidInsightDatasource)
            {
                def controllerName = rapidInsightDatasource.class.simpleName;
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
                return [rapidInsightDatasource: rapidInsightDatasource]
            }
        }
    }

    def delete = {
        def rapidInsightDatasource = RapidInsightDatasource.get([id: params.id])
        if (rapidInsightDatasource) {
            rapidInsightDatasource.remove()
            flash.message = "RapidInsightDatasource ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "RapidInsightDatasource not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def rapidInsightDatasource = RapidInsightDatasource.get([id: params.id])

        if (!rapidInsightDatasource) {
            flash.message = "RapidInsightDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [rapidInsightDatasource: rapidInsightDatasource]
        }
    }


    def update = {
        def rapidInsightDatasource = RapidInsightDatasource.get([id: params.id])
        if (rapidInsightDatasource) {
            rapidInsightDatasource.update(ControllerUtils.getClassProperties(params, RapidInsightDatasource));
            if (!rapidInsightDatasource.hasErrors()) {
                flash.message = "RapidInsightDatasource ${params.id} updated"
                redirect(action: show, id: rapidInsightDatasource.id)
            }
            else {
                render(view: 'edit', model: [rapidInsightDatasource: rapidInsightDatasource])
            }
        }
        else {
            flash.message = "RapidInsightDatasource not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def rapidInsightDatasource = new RapidInsightDatasource()
        rapidInsightDatasource.properties = params
        return ['rapidInsightDatasource': rapidInsightDatasource]
    }

    def save = {
        def rapidInsightDatasource = RapidInsightDatasource.add(ControllerUtils.getClassProperties(params, RapidInsightDatasource))
        if (!rapidInsightDatasource.hasErrors()) {
            flash.message = "RapidInsightDatasource ${rapidInsightDatasource.id} created"
            redirect(action: show, id: rapidInsightDatasource.id)
        }
        else {
            render(view: 'create', model: [rapidInsightDatasource: rapidInsightDatasource])
        }
    }

    def addTo = {
        def rapidInsightDatasource = RapidInsightDatasource.get([id: params.id])
        if (!rapidInsightDatasource) {
            flash.message = "RapidInsightDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(rapidInsightDatasource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rapidInsightDatasource: rapidInsightDatasource, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: rapidInsightDatasource.id)
            }
        }
    }

    def addRelation = {
        def rapidInsightDatasource = RapidInsightDatasource.get([id: params.id])
        if (!rapidInsightDatasource) {
            flash.message = "RapidInsightDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(rapidInsightDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    rapidInsightDatasource.addRelation(relationMap);
                    if (rapidInsightDatasource.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [rapidInsightDatasource: rapidInsightDatasource, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "RapidInsightDatasource ${params.id} updated"
                        redirect(action: edit, id: rapidInsightDatasource.id)
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
        def rapidInsightDatasource = RapidInsightDatasource.get([id: params.id])
        if (!rapidInsightDatasource) {
            flash.message = "RapidInsightDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(rapidInsightDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    rapidInsightDatasource.removeRelation(relationMap);
                    if (rapidInsightDatasource.hasErrors()) {
                        render(view: 'edit', model: [rapidInsightDatasource: rapidInsightDatasource])
                    }
                    else {
                        flash.message = "RapidInsightDatasource ${params.id} updated"
                        redirect(action: edit, id: rapidInsightDatasource.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: rapidInsightDatasource.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: rapidInsightDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("datasource.RapidInsightDatasource")
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