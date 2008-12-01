package connection
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Nov 21, 2008
 * Time: 9:55:02 AM
 */
import com.ifountain.rcmdb.domain.util.ControllerUtils

class HypericConnectionController {
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ hypericConnectionList: HypericConnection.list( params ) ]
    }

    def show = {
        def hypericConnection = HypericConnection.get([id:params.id])

        if(!hypericConnection) {
            flash.message = "HypericConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(hypericConnection.class != HypericConnection)
            {
                def controllerName = hypericConnection.class.simpleName;
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
                return [ hypericConnection : hypericConnection ]
            }
        }
    }

    def delete = {
        def hypericConnection = HypericConnection.get( [id:params.id])
        if(hypericConnection) {
            try{
                hypericConnection.remove()
                flash.message = "HypericConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[HypericConnection, hypericConnection])]
                flash.errors = errors;
                redirect(action:show, id:hypericConnection.id)
            }

        }
        else {
            flash.message = "HypericConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def hypericConnection = HypericConnection.get( [id:params.id] )

        if(!hypericConnection) {
            flash.message = "HypericConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ hypericConnection : hypericConnection ]
        }
    }


    def update = {
        def hypericConnection = HypericConnection.get( [id:params.id] )
        if(hypericConnection) {
            hypericConnection.update(ControllerUtils.getClassProperties(params, HypericConnection));
            if(!hypericConnection.hasErrors()) {
                flash.message = "HypericConnection ${params.id} updated"
                redirect(action:list)
            }
            else {
                render(view:'edit',model:[hypericConnection:hypericConnection])
            }
        }
        else {
            flash.message = "HypericConnection not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def hypericConnection = new HypericConnection()
        hypericConnection.properties = params
        return ['hypericConnection':hypericConnection]
    }

    def save = {
        def hypericConnection = HypericConnection.add(ControllerUtils.getClassProperties(params, HypericConnection))
        if(!hypericConnection.hasErrors()) {
            flash.message = "HypericConnection ${hypericConnection.id} created"
            redirect(action:list)
        }
        else {
            render(view:'create',model:[hypericConnection:hypericConnection])
        }
    }

    def addTo = {
        def hypericConnection = HypericConnection.get( [id:params.id] )
        if(!hypericConnection){
            flash.message = "HypericConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(hypericConnection.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [hypericConnection:hypericConnection, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:hypericConnection.id)
            }
        }
    }

    def addRelation = {
        def hypericConnection = HypericConnection.get( [id:params.id] )
        if(!hypericConnection) {
            flash.message = "HypericConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(hypericConnection.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      hypericConnection.addRelation(relationMap);
                      if(hypericConnection.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[hypericConnection:hypericConnection, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "HypericConnection ${params.id} updated"
                          redirect(action:edit,id:hypericConnection.id)
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
        def hypericConnection = HypericConnection.get( [id:params.id] )
        if(!hypericConnection) {
            flash.message = "HypericConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(hypericConnection.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      hypericConnection.removeRelation(relationMap);
                      if(hypericConnection.hasErrors()){
                          render(view:'edit',model:[hypericConnection:hypericConnection])
                      }
                      else{
                          flash.message = "HypericConnection ${params.id} updated"
                          redirect(action:edit,id:hypericConnection.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:hypericConnection.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:hypericConnection.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("connection.HypericConnection")
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