package connection

import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.text.SimpleDateFormat;
class DatabaseConnectionController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ databaseConnectionList: DatabaseConnection.list( params ) ]
    }

    def show = {
        def databaseConnection = DatabaseConnection.get([id:params.id])

        if(!databaseConnection) {
            flash.message = "DatabaseConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(databaseConnection.class != DatabaseConnection)
            {
                def controllerName = databaseConnection.class.name;
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
                return [ databaseConnection : databaseConnection ]
            }
        }
    }

    def delete = {
        def databaseConnection = DatabaseConnection.get( [id:params.id])
        if(databaseConnection) {
            try{
                databaseConnection.remove()
                flash.message = "DatabaseConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[DatabaseConnection, databaseConnection])]
                flash.errors = errors;
                redirect(action:show, id:databaseConnection.id)
            }

        }
        else {
            flash.message = "DatabaseConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def databaseConnection = DatabaseConnection.get( [id:params.id] )

        if(!databaseConnection) {
            flash.message = "DatabaseConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ databaseConnection : databaseConnection ]
        }
    }

    
    def update = {
        def databaseConnection = DatabaseConnection.get( [id:params.id] )
        if(databaseConnection) {
            databaseConnection.update(ControllerUtils.getClassProperties(params, DatabaseConnection));
            if(!databaseConnection.hasErrors()) {
                flash.message = "DatabaseConnection ${params.id} updated"
                redirect(action:show,id:databaseConnection.id)
            }
            else {
                render(view:'edit',model:[databaseConnection:databaseConnection])
            }
        }
        else {
            flash.message = "DatabaseConnection not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def databaseConnection = new DatabaseConnection()
        databaseConnection.properties = params
        return ['databaseConnection':databaseConnection]
    }

    def save = {
        def databaseConnection = DatabaseConnection.add(ControllerUtils.getClassProperties(params, DatabaseConnection))
        if(!databaseConnection.hasErrors()) {
            flash.message = "DatabaseConnection ${databaseConnection.id} created"
            redirect(action:show,id:databaseConnection.id)
        }
        else {
            render(view:'create',model:[databaseConnection:databaseConnection])
        }
    }

    def addTo = {
        def databaseConnection = DatabaseConnection.get( [id:params.id] )
        if(!databaseConnection){
            flash.message = "DatabaseConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = databaseConnection.hasMany[relationName];
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [databaseConnection:databaseConnection, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:databaseConnection.id)
            }
        }
    }

    def addRelation = {
        def databaseConnection = DatabaseConnection.get( [id:params.id] )
        if(!databaseConnection) {
            flash.message = "DatabaseConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = databaseConnection.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      databaseConnection.addRelation(relationMap);
                      if(databaseConnection.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[databaseConnection:databaseConnection, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "DatabaseConnection ${params.id} updated"
                          redirect(action:edit,id:databaseConnection.id)
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
        def databaseConnection = DatabaseConnection.get( [id:params.id] )
        if(!databaseConnection) {
            flash.message = "DatabaseConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = databaseConnection.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      databaseConnection.removeRelation(relationMap);
                      if(databaseConnection.hasErrors()){
                          render(view:'edit',model:[databaseConnection:databaseConnection])
                      }
                      else{
                          flash.message = "DatabaseConnection ${params.id} updated"
                          redirect(action:edit,id:databaseConnection.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:databaseConnection.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:databaseConnection.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("DatabaseConnection")
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