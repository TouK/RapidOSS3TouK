package connection

import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.text.SimpleDateFormat;
class SnmpConnectionController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ snmpConnectionList: SnmpConnection.list( params ) ]
    }

    def show = {
        def snmpConnection = SnmpConnection.get([id:params.id])

        if(!snmpConnection) {
            flash.message = "SnmpConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(snmpConnection.class != SnmpConnection)
            {
                def controllerName = snmpConnection.class.name;
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
                return [ snmpConnection : snmpConnection ]
            }
        }
    }

    def delete = {
        def snmpConnection = SnmpConnection.get( [id:params.id])
        if(snmpConnection) {
            try{
                snmpConnection.remove()
                flash.message = "SnmpConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[SnmpConnection, snmpConnection])]
                flash.errors = errors;
                redirect(action:show, id:snmpConnection.id)
            }

        }
        else {
            flash.message = "SnmpConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def snmpConnection = SnmpConnection.get( [id:params.id] )

        if(!snmpConnection) {
            flash.message = "SnmpConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ snmpConnection : snmpConnection ]
        }
    }

    
    def update = {
        def snmpConnection = SnmpConnection.get( [id:params.id] )
        if(snmpConnection) {
            snmpConnection.update(ControllerUtils.getClassProperties(params, SnmpConnection));
            if(!snmpConnection.hasErrors()) {
                flash.message = "SnmpConnection ${params.id} updated"
                redirect(action:show,id:snmpConnection.id)
            }
            else {
                render(view:'edit',model:[snmpConnection:snmpConnection])
            }
        }
        else {
            flash.message = "SnmpConnection not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def snmpConnection = new SnmpConnection()
        snmpConnection.properties = params
        return ['snmpConnection':snmpConnection]
    }

    def save = {
        def snmpConnection = SnmpConnection.add(ControllerUtils.getClassProperties(params, SnmpConnection))
        if(!snmpConnection.hasErrors()) {
            flash.message = "SnmpConnection ${snmpConnection.id} created"
            redirect(action:show,id:snmpConnection.id)
        }
        else {
            render(view:'create',model:[snmpConnection:snmpConnection])
        }
    }

    def addTo = {
        def snmpConnection = SnmpConnection.get( [id:params.id] )
        if(!snmpConnection){
            flash.message = "SnmpConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(snmpConnection.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [snmpConnection:snmpConnection, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:snmpConnection.id)
            }
        }
    }

    def addRelation = {
        def snmpConnection = SnmpConnection.get( [id:params.id] )
        if(!snmpConnection) {
            flash.message = "SnmpConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(snmpConnection.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      snmpConnection.addRelation(relationMap);
                      if(snmpConnection.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[snmpConnection:snmpConnection, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SnmpConnection ${params.id} updated"
                          redirect(action:edit,id:snmpConnection.id)
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
        def snmpConnection = SnmpConnection.get( [id:params.id] )
        if(!snmpConnection) {
            flash.message = "SnmpConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(snmpConnection.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      snmpConnection.removeRelation(relationMap);
                      if(snmpConnection.hasErrors()){
                          render(view:'edit',model:[snmpConnection:snmpConnection])
                      }
                      else{
                          flash.message = "SnmpConnection ${params.id} updated"
                          redirect(action:edit,id:snmpConnection.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:snmpConnection.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:snmpConnection.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("connection.SnmpConnection")
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