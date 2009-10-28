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

import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class ScienceFictionController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [scienceFictionList: ScienceFiction.search("alias:*", params).results]
    }

    def show = {
        def scienceFiction = ScienceFiction.get([id: params.id])

        if (!scienceFiction) {
            flash.message = "ScienceFiction not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (scienceFiction.class != ScienceFiction)
            {
                def controllerName = scienceFiction.class.simpleName;
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
                return [scienceFiction: scienceFiction]
            }
        }
    }

    def delete = {
        def scienceFiction = ScienceFiction.get([id: params.id])
        if (scienceFiction) {
            scienceFiction.remove()
            flash.message = "ScienceFiction ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "ScienceFiction not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def scienceFiction = ScienceFiction.get([id: params.id])

        if (!scienceFiction) {
            flash.message = "ScienceFiction not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [scienceFiction: scienceFiction]
        }
    }


    def update = {
        def scienceFiction = ScienceFiction.get([id: params.id])
        if (scienceFiction) {
            scienceFiction.update(ControllerUtils.getClassProperties(params, ScienceFiction));
            if (!scienceFiction.hasErrors()) {
                flash.message = "ScienceFiction ${params.id} updated"
                redirect(action: show, id: scienceFiction.id)
            }
            else {
                render(view: 'edit', model: [scienceFiction: scienceFiction])
            }
        }
        else {
            flash.message = "ScienceFiction not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def scienceFiction = new ScienceFiction()
        scienceFiction.properties = params
        return ['scienceFiction': scienceFiction]
    }

    def save = {
        def scienceFiction = ScienceFiction.add(ControllerUtils.getClassProperties(params, ScienceFiction))
        if (!scienceFiction.hasErrors()) {
            flash.message = "ScienceFiction ${scienceFiction.id} created"
            redirect(action: show, id: scienceFiction.id)
        }
        else {
            render(view: 'create', model: [scienceFiction: scienceFiction])
        }
    }

    def addTo = {
        def scienceFiction = ScienceFiction.get([id: params.id])
        if (!scienceFiction) {
            flash.message = "ScienceFiction not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = DomainClassUtils.getStaticMapVariable(ScienceFiction, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [scienceFiction: scienceFiction, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: scienceFiction.id)
            }
        }
    }



    def addRelation = {
        def scienceFiction = ScienceFiction.get([id: params.id])
        if (!scienceFiction) {
            flash.message = "ScienceFiction not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(ScienceFiction, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    scienceFiction.addRelation(relationMap);
                    if (scienceFiction.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [scienceFiction: scienceFiction, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "ScienceFiction ${params.id} updated"
                        redirect(action: edit, id: scienceFiction.id)
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
        def scienceFiction = ScienceFiction.get([id: params.id])
        if (!scienceFiction) {
            flash.message = "ScienceFiction not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(ScienceFiction, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    scienceFiction.removeRelation(relationMap);
                    if (scienceFiction.hasErrors()) {
                        render(view: 'edit', model: [scienceFiction: scienceFiction])
                    }
                    else {
                        flash.message = "ScienceFiction ${params.id} updated"
                        redirect(action: edit, id: scienceFiction.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: scienceFiction.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: scienceFiction.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("ScienceFiction")
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