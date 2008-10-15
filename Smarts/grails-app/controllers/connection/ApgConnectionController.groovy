package connection
import com.ifountain.rcmdb.domain.util.ControllerUtils
/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 10, 2008
 * Time: 6:07:16 PM
 * To change this template use File | Settings | File Templates.
 */
class ApgConnectionController {
   def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ apgConnectionList: ApgConnection.list( params ) ]
    }

    def show = {
        def apgConnection = ApgConnection.get([id:params.id])

        if(!apgConnection) {
            flash.message = "ApgConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(apgConnection.class != ApgConnection)
            {
                def controllerName = apgConnection.class.simpleName;
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
                return [ apgConnection : apgConnection ]
            }
        }
    }

    def delete = {
        def apgConnection = ApgConnection.get( [id:params.id])
        if(apgConnection) {
            try{
                apgConnection.remove()
                flash.message = "ApgConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[ApgConnection, apgConnection])]
                flash.errors = errors;
                redirect(action:show, id:apgConnection.id)
            }

        }
        else {
            flash.message = "ApgConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def apgConnection = ApgConnection.get( [id:params.id] )

        if(!apgConnection) {
            flash.message = "ApgConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ apgConnection : apgConnection ]
        }
    }


    def update = {
        def apgConnection = ApgConnection.get( [id:params.id] )
        if(apgConnection) {
            apgConnection.update(ControllerUtils.getClassProperties(params, ApgConnection));
            if(!apgConnection.hasErrors()) {
                flash.message = "ApgConnection ${params.id} updated"
                redirect(action:show,id:apgConnection.id)
            }
            else {
                render(view:'edit',model:[apgConnection:apgConnection])
            }
        }
        else {
            flash.message = "ApgConnection not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def apgConnection = new ApgConnection()
        apgConnection.properties = params
        return ['apgConnection':apgConnection]
    }

    def save = {
        def apgConnection = ApgConnection.add(ControllerUtils.getClassProperties(params, ApgConnection))
        if(!apgConnection.hasErrors()) {
            flash.message = "ApgConnection ${apgConnection.id} created"
            redirect(action:show,id:apgConnection.id)
        }
        else {
            render(view:'create',model:[apgConnection:apgConnection])
        }
    }

    def addTo = {
        def apgConnection = ApgConnection.get( [id:params.id] )
        if(!apgConnection){
            flash.message = "ApgConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(apgConnection.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [apgConnection:apgConnection, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:apgConnection.id)
            }
        }
    }

    def addRelation = {
        def apgConnection = ApgConnection.get( [id:params.id] )
        if(!apgConnection) {
            flash.message = "ApgConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(apgConnection.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      apgConnection.addRelation(relationMap);
                      if(apgConnection.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[apgConnection:apgConnection, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "ApgConnection ${params.id} updated"
                          redirect(action:edit,id:apgConnection.id)
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
        def apgConnection = ApgConnection.get( [id:params.id] )
        if(!apgConnection) {
            flash.message = "ApgConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(apgConnection.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      apgConnection.removeRelation(relationMap);
                      if(apgConnection.hasErrors()){
                          render(view:'edit',model:[apgConnection:apgConnection])
                      }
                      else{
                          flash.message = "ApgConnection ${params.id} updated"
                          redirect(action:edit,id:apgConnection.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:apgConnection.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:apgConnection.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName(ApgConnection.class.getName())
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