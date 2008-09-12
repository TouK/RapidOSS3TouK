package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils
/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 10, 2008
 * Time: 6:14:17 PM
 * To change this template use File | Settings | File Templates.
 */
class ApgReportDatasourceController {
   def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ apgReportDatasourceList: ApgReportDatasource.list( params ) ]
    }

    def show = {
        def apgReportDatasource = ApgReportDatasource.get([id:params.id])

        if(!apgReportDatasource) {
            flash.message = "ApgReportDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(apgReportDatasource.class != ApgReportDatasource)
            {
                def controllerName = apgReportDatasource.class.name;
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
                return [ apgReportDatasource : apgReportDatasource ]
            }
        }
    }

    def delete = {
        def apgReportDatasource = ApgReportDatasource.get( [id:params.id])
        if(apgReportDatasource) {
            try{
                apgReportDatasource.remove()
                flash.message = "ApgReportDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[ApgReportDatasource, apgReportDatasource])]
                flash.errors = errors;
                redirect(action:show, id:apgReportDatasource.id)
            }

        }
        else {
            flash.message = "ApgReportDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def apgReportDatasource = ApgReportDatasource.get( [id:params.id] )

        if(!apgReportDatasource) {
            flash.message = "ApgReportDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ apgReportDatasource : apgReportDatasource ]
        }
    }


    def update = {
        def apgReportDatasource = ApgReportDatasource.get( [id:params.id] )
        if(apgReportDatasource) {
            apgReportDatasource.update(ControllerUtils.getClassProperties(params, ApgReportDatasource));
            if(!apgReportDatasource.hasErrors()) {
                flash.message = "ApgReportDatasource ${params.id} updated"
                redirect(action:show,id:apgReportDatasource.id)
            }
            else {
                render(view:'edit',model:[apgReportDatasource:apgReportDatasource])
            }
        }
        else {
            flash.message = "ApgReportDatasource not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def apgReportDatasource = new ApgReportDatasource()
        apgReportDatasource.properties = params
        return ['apgReportDatasource':apgReportDatasource]
    }

    def save = {
        def apgReportDatasource = ApgReportDatasource.add(ControllerUtils.getClassProperties(params, ApgReportDatasource))
        if(!apgReportDatasource.hasErrors()) {
            flash.message = "ApgReportDatasource ${apgReportDatasource.id} created"
            redirect(action:show,id:apgReportDatasource.id)
        }
        else {
            render(view:'create',model:[apgReportDatasource:apgReportDatasource])
        }
    }

    def addTo = {
        def apgReportDatasource = ApgReportDatasource.get( [id:params.id] )
        if(!apgReportDatasource){
            flash.message = "ApgReportDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(apgReportDatasource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [apgReportDatasource:apgReportDatasource, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:apgReportDatasource.id)
            }
        }
    }

    def addRelation = {
        def apgReportDatasource = ApgReportDatasource.get( [id:params.id] )
        if(!apgReportDatasource) {
            flash.message = "ApgReportDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(apgReportDatasource.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      apgReportDatasource.addRelation(relationMap);
                      if(apgReportDatasource.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[apgReportDatasource:apgReportDatasource, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "ApgReportDatasource ${params.id} updated"
                          redirect(action:edit,id:apgReportDatasource.id)
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
        def apgReportDatasource = ApgReportDatasource.get( [id:params.id] )
        if(!apgReportDatasource) {
            flash.message = "ApgReportDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(apgReportDatasource.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      apgReportDatasource.removeRelation(relationMap);
                      if(apgReportDatasource.hasErrors()){
                          render(view:'edit',model:[apgReportDatasource:apgReportDatasource])
                      }
                      else{
                          flash.message = "ApgReportDatasource ${params.id} updated"
                          redirect(action:edit,id:apgReportDatasource.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:apgReportDatasource.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:apgReportDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName(ApgReportDatasource.class.getName())
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