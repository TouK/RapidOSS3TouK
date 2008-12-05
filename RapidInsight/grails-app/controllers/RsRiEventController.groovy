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


class RsRiEventController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsRiEventList: RsRiEvent.list( params ) ]
    }

    def show = {
        def rsRiEvent = RsRiEvent.get([id:params.id])

        if(!rsRiEvent) {
            flash.message = "RsRiEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsRiEvent.class != RsRiEvent)
            {
                def controllerName = rsRiEvent.class.simpleName;
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
                return [ rsRiEvent : rsRiEvent ]
            }
        }
    }

    def delete = {
        def rsRiEvent = RsRiEvent.get( [id:params.id])
        if(rsRiEvent) {
            try{
                rsRiEvent.remove()
                flash.message = "RsRiEvent ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsRiEvent, rsRiEvent])
                flash.errors = this.errors;
                redirect(action:show, id:rsRiEvent.id)
            }

        }
        else {
            flash.message = "RsRiEvent not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsRiEvent = RsRiEvent.get( [id:params.id] )

        if(!rsRiEvent) {
            flash.message = "RsRiEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsRiEvent : rsRiEvent ]
        }
    }


    def update = {
        def rsRiEvent = RsRiEvent.get( [id:params.id] )
        if(rsRiEvent) {
            rsRiEvent.update(ControllerUtils.getClassProperties(params, RsRiEvent));
            if(!rsRiEvent.hasErrors()) {
                flash.message = "RsRiEvent ${params.id} updated"
                redirect(action:show,id:rsRiEvent.id)
            }
            else {
                render(view:'edit',model:[rsRiEvent:rsRiEvent])
            }
        }
        else {
            flash.message = "RsRiEvent not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsRiEvent = new RsRiEvent()
        rsRiEvent.properties = params
        return ['rsRiEvent':rsRiEvent]
    }

    def save = {
        def rsRiEvent = RsRiEvent.add(ControllerUtils.getClassProperties(params, RsRiEvent))
        if(!rsRiEvent.hasErrors()) {
            flash.message = "RsRiEvent ${rsRiEvent.id} created"
            redirect(action:show,id:rsRiEvent.id)
        }
        else {
            render(view:'create',model:[rsRiEvent:rsRiEvent])
        }
    }

    def addTo = {
        def rsRiEvent = RsRiEvent.get( [id:params.id] )
        if(!rsRiEvent){
            flash.message = "RsRiEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsRiEvent, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsRiEvent:rsRiEvent, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsRiEvent.id)
            }
        }
    }



    def addRelation = {
        def rsRiEvent = RsRiEvent.get( [id:params.id] )
        if(!rsRiEvent) {
            flash.message = "RsRiEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsRiEvent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsRiEvent.addRelation(relationMap);
                      if(rsRiEvent.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsRiEvent:rsRiEvent, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsRiEvent ${params.id} updated"
                          redirect(action:edit,id:rsRiEvent.id)
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
        def rsRiEvent = RsRiEvent.get( [id:params.id] )
        if(!rsRiEvent) {
            flash.message = "RsRiEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsRiEvent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsRiEvent.removeRelation(relationMap);
                      if(rsRiEvent.hasErrors()){
                          render(view:'edit',model:[rsRiEvent:rsRiEvent])
                      }
                      else{
                          flash.message = "RsRiEvent ${params.id} updated"
                          redirect(action:edit,id:rsRiEvent.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsRiEvent.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsRiEvent.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsRiEvent")
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