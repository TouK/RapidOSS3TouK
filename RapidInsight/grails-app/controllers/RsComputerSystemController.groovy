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


class RsComputerSystemController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsComputerSystemList: RsComputerSystem.list( params ) ]
    }

    def show = {
        def rsComputerSystem = RsComputerSystem.get([id:params.id])

        if(!rsComputerSystem) {
            flash.message = "RsComputerSystem not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsComputerSystem.class != RsComputerSystem)
            {
                def controllerName = rsComputerSystem.class.simpleName;
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
                return [ rsComputerSystem : rsComputerSystem ]
            }
        }
    }

    def delete = {
        def rsComputerSystem = RsComputerSystem.get( [id:params.id])
        if(rsComputerSystem) {
            try{
                rsComputerSystem.remove()
                flash.message = "RsComputerSystem ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsComputerSystem, rsComputerSystem])
                flash.errors = this.errors;
                redirect(action:show, id:rsComputerSystem.id)
            }

        }
        else {
            flash.message = "RsComputerSystem not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsComputerSystem = RsComputerSystem.get( [id:params.id] )

        if(!rsComputerSystem) {
            flash.message = "RsComputerSystem not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsComputerSystem : rsComputerSystem ]
        }
    }


    def update = {
        def rsComputerSystem = RsComputerSystem.get( [id:params.id] )
        if(rsComputerSystem) {
            rsComputerSystem.update(ControllerUtils.getClassProperties(params, RsComputerSystem));
            if(!rsComputerSystem.hasErrors()) {
                flash.message = "RsComputerSystem ${params.id} updated"
                redirect(action:show,id:rsComputerSystem.id)
            }
            else {
                render(view:'edit',model:[rsComputerSystem:rsComputerSystem])
            }
        }
        else {
            flash.message = "RsComputerSystem not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsComputerSystem = new RsComputerSystem()
        rsComputerSystem.properties = params
        return ['rsComputerSystem':rsComputerSystem]
    }

    def save = {
        def rsComputerSystem = RsComputerSystem.add(ControllerUtils.getClassProperties(params, RsComputerSystem))
        if(!rsComputerSystem.hasErrors()) {
            flash.message = "RsComputerSystem ${rsComputerSystem.id} created"
            redirect(action:show,id:rsComputerSystem.id)
        }
        else {
            render(view:'create',model:[rsComputerSystem:rsComputerSystem])
        }
    }

    def addTo = {
        def rsComputerSystem = RsComputerSystem.get( [id:params.id] )
        if(!rsComputerSystem){
            flash.message = "RsComputerSystem not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsComputerSystem, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsComputerSystem:rsComputerSystem, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsComputerSystem.id)
            }
        }
    }



    def addRelation = {
        def rsComputerSystem = RsComputerSystem.get( [id:params.id] )
        if(!rsComputerSystem) {
            flash.message = "RsComputerSystem not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsComputerSystem, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsComputerSystem.addRelation(relationMap);
                      if(rsComputerSystem.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsComputerSystem:rsComputerSystem, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsComputerSystem ${params.id} updated"
                          redirect(action:edit,id:rsComputerSystem.id)
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
        def rsComputerSystem = RsComputerSystem.get( [id:params.id] )
        if(!rsComputerSystem) {
            flash.message = "RsComputerSystem not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsComputerSystem, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsComputerSystem.removeRelation(relationMap);
                      if(rsComputerSystem.hasErrors()){
                          render(view:'edit',model:[rsComputerSystem:rsComputerSystem])
                      }
                      else{
                          flash.message = "RsComputerSystem ${params.id} updated"
                          redirect(action:edit,id:rsComputerSystem.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsComputerSystem.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsComputerSystem.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsComputerSystem")
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