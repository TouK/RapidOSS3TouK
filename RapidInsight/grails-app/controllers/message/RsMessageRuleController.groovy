package message

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


class RsMessageRuleController {
    
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {

    }

    def show = {
        def rsMessageRule = RsMessageRule.get([id:params.id])

        if(!rsMessageRule) {
            flash.message = "RsMessageRule not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsMessageRule.class != RsMessageRule)
            {
                def controllerName = rsMessageRule.class.simpleName;
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
                return [ rsMessageRule : rsMessageRule ]
            }
        }
    }

    def delete = {
        def rsMessageRule = RsMessageRule.get( [id:params.id])
        if(rsMessageRule) {
            try{
                rsMessageRule.remove()
                flash.message = "RsMessageRule ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsMessageRule, rsMessageRule])
                flash.errors = this.errors;
                redirect(action:list, id:rsMessageRule.id)
            }

        }
        else {
            flash.message = "RsMessageRule not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsMessageRule = RsMessageRule.get( [id:params.id] )

        if(!rsMessageRule) {
            flash.message = "RsMessageRule not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsMessageRule : rsMessageRule ]
        }
    }


    def update = {
        def rsMessageRule = RsMessageRule.get( [id:params.id] )
        if(rsMessageRule) {
            rsMessageRule.update(ControllerUtils.getClassProperties(params, RsMessageRule));
            if(!rsMessageRule.hasErrors()) {
                flash.message = "RsMessageRule ${params.id} updated"
                redirect(action:list,id:rsMessageRule.id)
            }
            else {
                render(view:'edit',model:[rsMessageRule:rsMessageRule])
            }
        }
        else {
            flash.message = "RsMessageRule not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsMessageRule = new RsMessageRule()
        rsMessageRule.properties = params
        return ['rsMessageRule':rsMessageRule]
    }

    def save = {
        params.userId=auth.RsUser.get(username:session.username)?.id
        if(params.userId!=null)
        {
           params.userId=String.valueOf(params.userId) 
        }
        
        def rsMessageRule = RsMessageRule.add(ControllerUtils.getClassProperties(params, RsMessageRule))
        if(!rsMessageRule.hasErrors()) {
            flash.message = "RsMessageRule ${rsMessageRule.id} created"
            redirect(action:list,id:rsMessageRule.id)
        }
        else {
            render(view:'create',model:[rsMessageRule:rsMessageRule])
        }
    }
    def enableRule = {
        def rsMessageRule = RsMessageRule.get( [id:params.id] )
        if(rsMessageRule) {
            rsMessageRule.update(enabled:true);
            if(!rsMessageRule.hasErrors()) {
                flash.message = "RsMessageRule ${params.id} enabled"
                redirect(action:list,id:rsMessageRule.id)
            }
            else {
                render(view:'list',model:[rsMessageRule:rsMessageRule])
            }
        }
        else {
            flash.message = "RsMessageRule not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }
    def disableRule = {
        def rsMessageRule = RsMessageRule.get( [id:params.id] )
        if(rsMessageRule) {
            rsMessageRule.update(enabled:false);
            if(!rsMessageRule.hasErrors()) {
                flash.message = "RsMessageRule ${params.id} disabled"
                redirect(action:list,id:rsMessageRule.id)
            }
            else {
                render(view:'list',model:[rsMessageRule:rsMessageRule])
            }
        }
        else {
            flash.message = "RsMessageRule not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }
    def addTo = {
        def rsMessageRule = RsMessageRule.get( [id:params.id] )
        if(!rsMessageRule){
            flash.message = "RsMessageRule not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsMessageRule, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsMessageRule:rsMessageRule, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsMessageRule.id)
            }
        }
    }



    def addRelation = {
        def rsMessageRule = RsMessageRule.get( [id:params.id] )
        if(!rsMessageRule) {
            flash.message = "RsMessageRule not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsMessageRule, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsMessageRule.addRelation(relationMap);
                      if(rsMessageRule.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsMessageRule:rsMessageRule, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsMessageRule ${params.id} updated"
                          redirect(action:edit,id:rsMessageRule.id)
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
        def rsMessageRule = RsMessageRule.get( [id:params.id] )
        if(!rsMessageRule) {
            flash.message = "RsMessageRule not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsMessageRule, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsMessageRule.removeRelation(relationMap);
                      if(rsMessageRule.hasErrors()){
                          render(view:'edit',model:[rsMessageRule:rsMessageRule])
                      }
                      else{
                          flash.message = "RsMessageRule ${params.id} updated"
                          redirect(action:edit,id:rsMessageRule.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsMessageRule.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsMessageRule.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsMessageRule")
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