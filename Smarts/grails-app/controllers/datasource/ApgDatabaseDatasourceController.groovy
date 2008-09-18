package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils
/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 10, 2008
 * Time: 6:12:32 PM
 * To change this template use File | Settings | File Templates.
 */
class ApgDatabaseDatasourceController {
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ apgDatabaseDatasourceList: ApgDatabaseDatasource.list( params ) ]
    }

    def show = {
        def apgDatabaseDatasource = ApgDatabaseDatasource.get([id:params.id])

        if(!apgDatabaseDatasource) {
            flash.message = "ApgDatabaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(apgDatabaseDatasource.class != ApgDatabaseDatasource)
            {
                def controllerName = apgDatabaseDatasource.class.name;
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
                return [ apgDatabaseDatasource : apgDatabaseDatasource ]
            }
        }
    }

    def delete = {
        def apgDatabaseDatasource = ApgDatabaseDatasource.get( [id:params.id])
        if(apgDatabaseDatasource) {
            try{
                apgDatabaseDatasource.remove()
                flash.message = "ApgDatabaseDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[ApgDatabaseDatasource, apgDatabaseDatasource])]
                flash.errors = errors;
                redirect(action:show, id:apgDatabaseDatasource.id)
            }

        }
        else {
            flash.message = "ApgDatabaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def apgDatabaseDatasource = ApgDatabaseDatasource.get( [id:params.id] )

        if(!apgDatabaseDatasource) {
            flash.message = "ApgDatabaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ apgDatabaseDatasource : apgDatabaseDatasource ]
        }
    }


    def update = {
        def apgDatabaseDatasource = ApgDatabaseDatasource.get( [id:params.id] )
        if(apgDatabaseDatasource) {
            apgDatabaseDatasource.update(ControllerUtils.getClassProperties(params, ApgDatabaseDatasource));
            if(!apgDatabaseDatasource.hasErrors()) {
                flash.message = "ApgDatabaseDatasource ${params.id} updated"
                redirect(action:show,id:apgDatabaseDatasource.id)
            }
            else {
                render(view:'edit',model:[apgDatabaseDatasource:apgDatabaseDatasource])
            }
        }
        else {
            flash.message = "ApgDatabaseDatasource not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def apgDatabaseDatasource = new ApgDatabaseDatasource()
        apgDatabaseDatasource.properties = params
        return ['apgDatabaseDatasource':apgDatabaseDatasource]
    }

    def save = {
        def apgDatabaseDatasource = ApgDatabaseDatasource.add(ControllerUtils.getClassProperties(params, ApgDatabaseDatasource))
        if(!apgDatabaseDatasource.hasErrors()) {
            flash.message = "ApgDatabaseDatasource ${apgDatabaseDatasource.id} created"
            redirect(action:show,id:apgDatabaseDatasource.id)
        }
        else {
            render(view:'create',model:[apgDatabaseDatasource:apgDatabaseDatasource])
        }
    }

    def addTo = {
        def apgDatabaseDatasource = ApgDatabaseDatasource.get( [id:params.id] )
        if(!apgDatabaseDatasource){
            flash.message = "ApgDatabaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(apgDatabaseDatasource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [apgDatabaseDatasource:apgDatabaseDatasource, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:apgDatabaseDatasource.id)
            }
        }
    }

    def addRelation = {
        def apgDatabaseDatasource = ApgDatabaseDatasource.get( [id:params.id] )
        if(!apgDatabaseDatasource) {
            flash.message = "ApgDatabaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(apgDatabaseDatasource.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      apgDatabaseDatasource.addRelation(relationMap);
                      if(apgDatabaseDatasource.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[apgDatabaseDatasource:apgDatabaseDatasource, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "ApgDatabaseDatasource ${params.id} updated"
                          redirect(action:edit,id:apgDatabaseDatasource.id)
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
        def apgDatabaseDatasource = ApgDatabaseDatasource.get( [id:params.id] )
        if(!apgDatabaseDatasource) {
            flash.message = "ApgDatabaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(apgDatabaseDatasource.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      apgDatabaseDatasource.removeRelation(relationMap);
                      if(apgDatabaseDatasource.hasErrors()){
                          render(view:'edit',model:[apgDatabaseDatasource:apgDatabaseDatasource])
                      }
                      else{
                          flash.message = "ApgDatabaseDatasource ${params.id} updated"
                          redirect(action:edit,id:apgDatabaseDatasource.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:apgDatabaseDatasource.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:apgDatabaseDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName(ApgDatabaseDatasource.class.getName())
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