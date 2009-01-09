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
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import connection.EmailConnection
import datasource.EmailDatasource;

class EmailConnectionTemplateController {


    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        
    }


    def delete = {
        def emailConnectionTemplate = EmailConnectionTemplate.get([id: params.id])
        if (emailConnectionTemplate) {
            try {
                emailConnectionTemplate.emailConnection.remove();
                emailConnectionTemplate.remove()
                flash.message = "EmailConnectionTemplate ${params.id} deleted"
                redirect(action: list);
            }
            catch (e) {
                addError("default.couldnot.delete", [EmailConnectionTemplate, emailConnectionTemplate])
                flash.errors = this.errors;
                redirect(action: show, id: emailConnectionTemplate.id)
            }

        }
        else {
            flash.message = "EmailConnectionTemplate not found with id ${params.id}"
            redirect(action: list);
        }
    }


    def create = {
        def emailConnectionTemplate = new EmailConnectionTemplate()
        emailConnectionTemplate.properties = params
        return ['emailConnectionTemplate': emailConnectionTemplate, emailConnection: new EmailConnection(), emailDatasource: new EmailDatasource()]
    }
    def save = {
        def emailConnectionTemplate = EmailConnectionTemplate.add(ControllerUtils.getClassProperties(params, EmailConnectionTemplate))
        if (!emailConnectionTemplate.hasErrors()) {
            def emailConnection = EmailConnection.add(ControllerUtils.getClassProperties(params, EmailConnection));
            if (!emailConnection.hasErrors()) {
                emailConnectionTemplate.addRelation(emailConnection:emailConnection)
                def emailDatasource = EmailDatasource.add(name:emailConnection.name + "templateDs", connection:emailConnection);
                if(!emailDatasource.hasErrors()){
                    emailConnectionTemplate.addRelation(emailDatasource:emailDatasource)
                    flash.message = "EmailConnectionTemplate ${emailConnectionTemplate.name} created"
                    redirect(action: 'list');
                
                }
                else{
                    emailConnection.remove();
                    emailConnectionTemplate.remove();
                    render(view: 'create', model: [emailConnectionTemplate: emailConnectionTemplate, emailConnection:emailConnection, emailDatasouce:new EmailDatasource()])
                }

            }
            else {
               emailConnectionTemplate.remove();
               render(view: 'create', model: [emailConnectionTemplate: emailConnectionTemplate, emailConnection:EmailConnection(), emailDatasouce:new EmailDatasource()])
            }
        }
        else {
            render(view: 'create', model: [emailConnectionTemplate: emailConnectionTemplate, emailConnection:new EmailConnection(), emailDatasouce:new EmailDatasource()])
        }

    }
    def edit = {
        def emailConnectionTemplate = EmailConnectionTemplate.get([id: params.id])

        if (!emailConnectionTemplate) {
            flash.message = "EmailConnectionTemplate not found with id ${params.id}"
            redirect(uri: 'emailConnectionTemplate');
        }
        else {
            return [emailConnectionTemplate: emailConnectionTemplate, emailConnection: emailConnectionTemplate.emailConnection, emailDatasource: emailConnectionTemplate.emailDatasource]
        }
    }
    def update = {
        def emailConnectionTemplate = EmailConnectionTemplate.get([id: params.id])
        if (emailConnectionTemplate) {            
            if(emailConnectionTemplate.name != params.name){

                def willSendError = false;
                def emailConnection = emailConnectionTemplate.emailConnection;
                def emailDatasource = emailConnectionTemplate.emailDatasource;

                def conn = Connection.get(name:params.name);
                if(conn){
                    emailConnection.update(name:params.name); //this update will generate error if connection with this name exists
                    willSendError = true;
                }
                def emailDs = BaseDatasource.get(name:params.name + "templateDs");
                if(emailDs){
                    emailDatasource.update(name:params.name + "templateDs") //this update will generate error if connection with this name exists
                    willSendError = true;
                }

                if(willSendError){
                    render(view: 'edit', model: [emailConnectionTemplate: emailConnectionTemplate, emailConnection:emailConnection,  emailDatasource:emailDatasource])
                    return;
                }
            }

            def emailConnection = emailConnectionTemplate.emailConnection;
            emailConnection.update(ControllerUtils.getClassProperties(params, EmailConnection))
            if(!emailConnection.hasErrors()){
               emailConnectionTemplate.update(ControllerUtils.getClassProperties(params, EmailConnectionTemplate));
               emailConnectionTemplate.emailDatasource.update(name:emailConnection.name + "templateDs");

               flash.message = "EmailConnectionTemplate ${params.id} updated"               
               redirect(action: list);
            }
            else{
                render(view: 'edit', model: [emailConnectionTemplate: emailConnectionTemplate, emailConnection:emailConnection, emailDatasource:emailConnectionTemplate.emailDatasource])
            }
        }
        else {
            flash.message = "EmailConnectionTemplate not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def addTo = {
        def emailConnectionTemplate = EmailConnectionTemplate.get([id: params.id])
        if (!emailConnectionTemplate) {
            flash.message = "EmailConnectionTemplate not found with id ${params.id}"
            redirect(action: 'list',params:[:]);
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = DomainClassUtils.getStaticMapVariable(EmailConnectionTemplate, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [emailConnectionTemplate: emailConnectionTemplate, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: emailConnectionTemplate.id)
            }
        }
    }



    def addRelation = {
        def emailConnectionTemplate = EmailConnectionTemplate.get([id: params.id])
        if (!emailConnectionTemplate) {
            flash.message = "EmailConnectionTemplate not found with id ${params.id}"
            redirect(action: 'list',params:[:]);
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(EmailConnectionTemplate, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    emailConnectionTemplate.addRelation(relationMap);
                    if (emailConnectionTemplate.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [emailConnectionTemplate: emailConnectionTemplate, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "EmailConnectionTemplate ${params.id} updated"
                        redirect(action: edit, id: emailConnectionTemplate.id)
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
        def emailConnectionTemplate = EmailConnectionTemplate.get([id: params.id])
        if (!emailConnectionTemplate) {
            flash.message = "EmailConnectionTemplate not found with id ${params.id}"
            redirect(action: 'list',params:[:]);
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(EmailConnectionTemplate, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    emailConnectionTemplate.removeRelation(relationMap);
                    if (emailConnectionTemplate.hasErrors()) {
                        render(view: 'edit', model: [emailConnectionTemplate: emailConnectionTemplate])
                    }
                    else {
                        flash.message = "EmailConnectionTemplate ${params.id} updated"
                        redirect(action: edit, id: emailConnectionTemplate.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: emailConnectionTemplate.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: emailConnectionTemplate.id)
            }
        }
    }
//
    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("connection.EmailConnectionTemplate")
        if (modelClass)
        {
            try
            {

                modelClass.metaClass.invokeStaticMethod(modelClass, "reloadOperations", [] as Object[]);
                flash.message = "Model operations reloaded"
                redirect(action: 'list',params:[:]);
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