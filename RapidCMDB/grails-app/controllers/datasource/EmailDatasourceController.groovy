package datasource
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


class EmailDatasourceController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.sort) params.sort = "name"
        [emailDatasourceList: EmailDatasource.list(params)]
    }

    def show = {
        def emailDatasource = EmailDatasource.get([id: params.id])

        if (!emailDatasource) {
            flash.message = "EmailDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (emailDatasource.class != EmailDatasource)
            {
                def controllerName = emailDatasource.class.simpleName;
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
                return [emailDatasource: emailDatasource]
            }
        }
    }

    def delete = {
        def emailDatasource = EmailDatasource.get([id: params.id])
        if (emailDatasource) {
            emailDatasource.remove()
            flash.message = "EmailDatasource ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "EmailDatasource not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def emailDatasource = EmailDatasource.get([id: params.id])

        if (!emailDatasource) {
            flash.message = "EmailDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [emailDatasource: emailDatasource]
        }
    }


    def update = {
        def emailDatasource = EmailDatasource.get([id: params.id])
        if (emailDatasource) {
            emailDatasource.update(ControllerUtils.getClassProperties(params, EmailDatasource));
            if (!emailDatasource.hasErrors()) {
                flash.message = "EmailDatasource ${params.id} updated"
                redirect(action: show, id: emailDatasource.id)
            }
            else {
                render(view: 'edit', model: [emailDatasource: emailDatasource])
            }
        }
        else {
            flash.message = "EmailDatasource not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def emailDatasource = new EmailDatasource()
        emailDatasource.properties = params
        return ['emailDatasource': emailDatasource]
    }

    def save = {
        def emailDatasource = EmailDatasource.add(ControllerUtils.getClassProperties(params, EmailDatasource))
        if (!emailDatasource.hasErrors()) {
            flash.message = "EmailDatasource ${emailDatasource.id} created"
            redirect(action: show, id: emailDatasource.id)
        }
        else {
            render(view: 'create', model: [emailDatasource: emailDatasource])
        }
    }

    def addTo = {
        def emailDatasource = EmailDatasource.get([id: params.id])
        if (!emailDatasource) {
            flash.message = "EmailDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = DomainClassUtils.getStaticMapVariable(EmailDatasource, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [emailDatasource: emailDatasource, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: emailDatasource.id)
            }
        }
    }



    def addRelation = {
        def emailDatasource = EmailDatasource.get([id: params.id])
        if (!emailDatasource) {
            flash.message = "EmailDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(EmailDatasource, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    emailDatasource.addRelation(relationMap);
                    if (emailDatasource.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [emailDatasource: emailDatasource, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "EmailDatasource ${params.id} updated"
                        redirect(action: edit, id: emailDatasource.id)
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
        def emailDatasource = EmailDatasource.get([id: params.id])
        if (!emailDatasource) {
            flash.message = "EmailDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(EmailDatasource, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    emailDatasource.removeRelation(relationMap);
                    if (emailDatasource.hasErrors()) {
                        render(view: 'edit', model: [emailDatasource: emailDatasource])
                    }
                    else {
                        flash.message = "EmailDatasource ${params.id} updated"
                        redirect(action: edit, id: emailDatasource.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: emailDatasource.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: emailDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("EmailDatasource")
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