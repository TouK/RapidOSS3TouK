package connection
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Nov 21, 2008
 * Time: 9:55:02 AM
 */
import com.ifountain.rcmdb.domain.util.ControllerUtils

class OpenNMSHttpConnectionController {
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ openNMSHttpConnectionList: OpenNMSHttpConnection.list( params ) ]
    }

    def show = {
        def openNMSHttpConnection = OpenNMSHttpConnection.get([id:params.id])

        if(!openNMSHttpConnection) {
            flash.message = "OpenNMSHttpConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(openNMSHttpConnection.class != OpenNMSHttpConnection)
            {
                def controllerName = openNMSHttpConnection.class.simpleName;
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
                return [ openNMSHttpConnection : openNMSHttpConnection ]
            }
        }
    }

    def delete = {
        def openNMSHttpConnection = OpenNMSHttpConnection.get( [id:params.id])
        if(openNMSHttpConnection) {
            try{
                openNMSHttpConnection.remove()
                flash.message = "OpenNMSHttpConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[OpenNMSHttpConnection, openNMSHttpConnection])]
                flash.errors = errors;
                redirect(action:show, id:openNMSHttpConnection.id)
            }

        }
        else {
            flash.message = "OpenNMSHttpConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def openNMSHttpConnection = OpenNMSHttpConnection.get( [id:params.id] )

        if(!openNMSHttpConnection) {
            flash.message = "OpenNMSHttpConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ openNMSHttpConnection : openNMSHttpConnection ]
        }
    }


    def update = {
        def openNMSHttpConnection = OpenNMSHttpConnection.get( [id:params.id] )
        if(openNMSHttpConnection) {
            openNMSHttpConnection.update(ControllerUtils.getClassProperties(params, OpenNMSHttpConnection));
            if(!openNMSHttpConnection.hasErrors()) {
                flash.message = "OpenNMSHttpConnection ${params.id} updated"
                redirect(action:show,id:openNMSHttpConnection.id)
            }
            else {
                render(view:'edit',model:[openNMSHttpConnection:openNMSHttpConnection])
            }
        }
        else {
            flash.message = "OpenNMSHttpConnection not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def openNMSHttpConnection = new OpenNMSHttpConnection()
        openNMSHttpConnection.properties = params
        return ['openNMSHttpConnection':openNMSHttpConnection]
    }

    def save = {
        def openNMSHttpConnection = OpenNMSHttpConnection.add(ControllerUtils.getClassProperties(params, OpenNMSHttpConnection))
        if(!openNMSHttpConnection.hasErrors()) {
            flash.message = "OpenNMSHttpConnection ${openNMSHttpConnection.id} created"
            redirect(action:show,id:openNMSHttpConnection.id)
        }
        else {
            render(view:'create',model:[openNMSHttpConnection:openNMSHttpConnection])
        }
    }

    def addTo = {
        def openNMSHttpConnection = OpenNMSHttpConnection.get( [id:params.id] )
        if(!openNMSHttpConnection){
            flash.message = "OpenNMSHttpConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(openNMSHttpConnection.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [openNMSHttpConnection:openNMSHttpConnection, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:openNMSHttpConnection.id)
            }
        }
    }

    def addRelation = {
        def openNMSHttpConnection = OpenNMSHttpConnection.get( [id:params.id] )
        if(!openNMSHttpConnection) {
            flash.message = "OpenNMSHttpConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(openNMSHttpConnection.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      openNMSHttpConnection.addRelation(relationMap);
                      if(openNMSHttpConnection.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[openNMSHttpConnection:openNMSHttpConnection, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "OpenNMSHttpConnection ${params.id} updated"
                          redirect(action:edit,id:openNMSHttpConnection.id)
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
        def openNMSHttpConnection = OpenNMSHttpConnection.get( [id:params.id] )
        if(!openNMSHttpConnection) {
            flash.message = "OpenNMSHttpConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(openNMSHttpConnection.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      openNMSHttpConnection.removeRelation(relationMap);
                      if(openNMSHttpConnection.hasErrors()){
                          render(view:'edit',model:[openNMSHttpConnection:openNMSHttpConnection])
                      }
                      else{
                          flash.message = "OpenNMSHttpConnection ${params.id} updated"
                          redirect(action:edit,id:openNMSHttpConnection.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:openNMSHttpConnection.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:openNMSHttpConnection.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("connection.OpenNMSHttpConnection")
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