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


class RsHistoricalEventController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsHistoricalEventList: RsHistoricalEvent.list( params ) ]
    }

    def show = {
        def rsHistoricalEvent = RsHistoricalEvent.get([id:params.id])

        if(!rsHistoricalEvent) {
            flash.message = "RsHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsHistoricalEvent.class != RsHistoricalEvent)
            {
                def controllerName = rsHistoricalEvent.class.simpleName;
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
                return [ rsHistoricalEvent : rsHistoricalEvent ]
            }
        }
    }

    def delete = {
        def rsHistoricalEvent = RsHistoricalEvent.get( [id:params.id])
        if(rsHistoricalEvent) {
            try{
                rsHistoricalEvent.remove()
                flash.message = "RsHistoricalEvent ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsHistoricalEvent, rsHistoricalEvent])
                flash.errors = this.errors;
                redirect(action:show, id:rsHistoricalEvent.id)
            }

        }
        else {
            flash.message = "RsHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsHistoricalEvent = RsHistoricalEvent.get( [id:params.id] )

        if(!rsHistoricalEvent) {
            flash.message = "RsHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsHistoricalEvent : rsHistoricalEvent ]
        }
    }


    def update = {
        def rsHistoricalEvent = RsHistoricalEvent.get( [id:params.id] )
        if(rsHistoricalEvent) {
            rsHistoricalEvent.update(ControllerUtils.getClassProperties(params, RsHistoricalEvent));
            if(!rsHistoricalEvent.hasErrors()) {
                flash.message = "RsHistoricalEvent ${params.id} updated"
                redirect(action:show,id:rsHistoricalEvent.id)
            }
            else {
                render(view:'edit',model:[rsHistoricalEvent:rsHistoricalEvent])
            }
        }
        else {
            flash.message = "RsHistoricalEvent not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsHistoricalEvent = new RsHistoricalEvent()
        rsHistoricalEvent.properties = params
        return ['rsHistoricalEvent':rsHistoricalEvent]
    }

    def save = {
        def rsHistoricalEvent = RsHistoricalEvent.add(ControllerUtils.getClassProperties(params, RsHistoricalEvent))
        if(!rsHistoricalEvent.hasErrors()) {
            flash.message = "RsHistoricalEvent ${rsHistoricalEvent.id} created"
            redirect(action:show,id:rsHistoricalEvent.id)
        }
        else {
            render(view:'create',model:[rsHistoricalEvent:rsHistoricalEvent])
        }
    }

    def addTo = {
        def rsHistoricalEvent = RsHistoricalEvent.get( [id:params.id] )
        if(!rsHistoricalEvent){
            flash.message = "RsHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsHistoricalEvent, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsHistoricalEvent:rsHistoricalEvent, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsHistoricalEvent.id)
            }
        }
    }



    def addRelation = {
        def rsHistoricalEvent = RsHistoricalEvent.get( [id:params.id] )
        if(!rsHistoricalEvent) {
            flash.message = "RsHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsHistoricalEvent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsHistoricalEvent.addRelation(relationMap);
                      if(rsHistoricalEvent.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsHistoricalEvent:rsHistoricalEvent, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsHistoricalEvent ${params.id} updated"
                          redirect(action:edit,id:rsHistoricalEvent.id)
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
        def rsHistoricalEvent = RsHistoricalEvent.get( [id:params.id] )
        if(!rsHistoricalEvent) {
            flash.message = "RsHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsHistoricalEvent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsHistoricalEvent.removeRelation(relationMap);
                      if(rsHistoricalEvent.hasErrors()){
                          render(view:'edit',model:[rsHistoricalEvent:rsHistoricalEvent])
                      }
                      else{
                          flash.message = "RsHistoricalEvent ${params.id} updated"
                          redirect(action:edit,id:rsHistoricalEvent.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsHistoricalEvent.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsHistoricalEvent.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsHistoricalEvent")
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