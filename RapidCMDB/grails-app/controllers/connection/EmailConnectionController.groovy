package connection

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


class EmailConnectionController {

    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ emailConnectionList: EmailConnection.list( params ) ]
    }

    def show = {
        def emailConnection = EmailConnection.get([id:params.id])

        if(!emailConnection) {
            flash.message = "EmailConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(emailConnection.class != EmailConnection)
            {
                def controllerName = emailConnection.class.simpleName;
                if(controllerName.length() == 1)
                {
                    controllerName = controllerName.toLowerCase();
                }
                else
                {
                    controllerName = controllerName.substring(0,1).toLowerCase()+controllerName.substring(1);
                }
                redirect(action:show, controller:controllerName, id:params.id)
            }
            else
            {
                return [ emailConnection : emailConnection ]
            }
        }
    }

    def delete = {
        def emailConnection = EmailConnection.get( [id:params.id])
        if(emailConnection) {
            try{
                emailConnection.remove()
                flash.message = "EmailConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [EmailConnection, emailConnection])
                flash.errors = this.errors;
                redirect(action:show, id:emailConnection.id)
            }

        }
        else {
            flash.message = "EmailConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def emailConnection = EmailConnection.get( [id:params.id] )

        if(!emailConnection) {
            flash.message = "EmailConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ emailConnection : emailConnection ]
        }
    }


    def update = {
        def emailConnection = EmailConnection.get( [id:params.id] )
        if(emailConnection) {
            emailConnection.update(ControllerUtils.getClassProperties(params, EmailConnection));
            if(!emailConnection.hasErrors()) {
                flash.message = "EmailConnection ${params.id} updated"
                redirect(action:show,id:emailConnection.id)
            }
            else {
                render(view:'edit',model:[emailConnection:emailConnection])
            }
        }
        else {
            flash.message = "EmailConnection not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def emailConnection = new EmailConnection()
        emailConnection.properties = params
        return ['emailConnection':emailConnection]
    }

    def save = {
        def emailConnection = EmailConnection.add(ControllerUtils.getClassProperties(params, EmailConnection))
        if(!emailConnection.hasErrors()) {
            flash.message = "EmailConnection ${emailConnection.id} created"
            redirect(action:show,id:emailConnection.id)
        }
        else {
            render(view:'create',model:[emailConnection:emailConnection])
        }
    }

    def addTo = {
        def emailConnection = EmailConnection.get( [id:params.id] )
        if(!emailConnection){
            flash.message = "EmailConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(EmailConnection, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [emailConnection:emailConnection, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:emailConnection.id)
            }
        }
    }



    def addRelation = {
        def emailConnection = EmailConnection.get( [id:params.id] )
        if(!emailConnection) {
            flash.message = "EmailConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(EmailConnection, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      emailConnection.addRelation(relationMap);
                      if(emailConnection.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[emailConnection:emailConnection, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "EmailConnection ${params.id} updated"
                          redirect(action:edit,id:emailConnection.id)
                      }

                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:addTo, id:params.id, relationName:relationName)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:addTo, id:params.id, relationName:relationName)
            }
        }
    }

    def removeRelation = {
        def emailConnection = EmailConnection.get( [id:params.id] )
        if(!emailConnection) {
            flash.message = "EmailConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(EmailConnection, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      emailConnection.removeRelation(relationMap);
                      if(emailConnection.hasErrors()){
                          render(view:'edit',model:[emailConnection:emailConnection])
                      }
                      else{
                          flash.message = "EmailConnection ${params.id} updated"
                          redirect(action:edit,id:emailConnection.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:emailConnection.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:emailConnection.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("EmailConnection")
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