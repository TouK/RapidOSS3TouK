package connection

import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.text.SimpleDateFormat;
class RapidInsightConnectionController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rapidInsightConnectionList: RapidInsightConnection.list( params ) ]
    }

    def show = {
        def rapidInsightConnection = RapidInsightConnection.get([id:params.id])

        if(!rapidInsightConnection) {
            flash.message = "RapidInsightConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rapidInsightConnection.class != RapidInsightConnection)
            {
                def controllerName = rapidInsightConnection.class.name;
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
                return [ rapidInsightConnection : rapidInsightConnection ]
            }
        }
    }

    def delete = {
        def rapidInsightConnection = RapidInsightConnection.get( [id:params.id])
        if(rapidInsightConnection) {
            try{
                rapidInsightConnection.remove()
                flash.message = "RapidInsightConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[RapidInsightConnection, rapidInsightConnection])]
                flash.errors = errors;
                redirect(action:show, id:rapidInsightConnection.id)
            }

        }
        else {
            flash.message = "RapidInsightConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rapidInsightConnection = RapidInsightConnection.get( [id:params.id] )

        if(!rapidInsightConnection) {
            flash.message = "RapidInsightConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rapidInsightConnection : rapidInsightConnection ]
        }
    }

    
    def update = {
        def rapidInsightConnection = RapidInsightConnection.get( [id:params.id] )
        if(rapidInsightConnection) {
            rapidInsightConnection.update(ControllerUtils.getClassProperties(params, RapidInsightConnection));
            if(!rapidInsightConnection.hasErrors()) {
                flash.message = "RapidInsightConnection ${params.id} updated"
                redirect(action:show,id:rapidInsightConnection.id)
            }
            else {
                render(view:'edit',model:[rapidInsightConnection:rapidInsightConnection])
            }
        }
        else {
            flash.message = "RapidInsightConnection not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rapidInsightConnection = new RapidInsightConnection()
        rapidInsightConnection.properties = params
        return ['rapidInsightConnection':rapidInsightConnection]
    }

    def save = {
        def rapidInsightConnection = RapidInsightConnection.add(ControllerUtils.getClassProperties(params, RapidInsightConnection))
        if(!rapidInsightConnection.hasErrors()) {
            flash.message = "RapidInsightConnection ${rapidInsightConnection.id} created"
            redirect(action:show,id:rapidInsightConnection.id)
        }
        else {
            render(view:'create',model:[rapidInsightConnection:rapidInsightConnection])
        }
    }

    def addTo = {
        def rapidInsightConnection = RapidInsightConnection.get( [id:params.id] )
        if(!rapidInsightConnection){
            flash.message = "RapidInsightConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = rapidInsightConnection.relations[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rapidInsightConnection:rapidInsightConnection, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rapidInsightConnection.id)
            }
        }
    }

    def addRelation = {
        def rapidInsightConnection = RapidInsightConnection.get( [id:params.id] )
        if(!rapidInsightConnection) {
            flash.message = "RapidInsightConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = rapidInsightConnection.relations[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rapidInsightConnection.addRelation(relationMap);
                      if(rapidInsightConnection.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rapidInsightConnection:rapidInsightConnection, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RapidInsightConnection ${params.id} updated"
                          redirect(action:edit,id:rapidInsightConnection.id)
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
        def rapidInsightConnection = RapidInsightConnection.get( [id:params.id] )
        if(!rapidInsightConnection) {
            flash.message = "RapidInsightConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = rapidInsightConnection.relations[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rapidInsightConnection.removeRelation(relationMap);
                      if(rapidInsightConnection.hasErrors()){
                          render(view:'edit',model:[rapidInsightConnection:rapidInsightConnection])
                      }
                      else{
                          flash.message = "RapidInsightConnection ${params.id} updated"
                          redirect(action:edit,id:rapidInsightConnection.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rapidInsightConnection.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rapidInsightConnection.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RapidInsightConnection")
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