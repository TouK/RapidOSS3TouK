package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.text.SimpleDateFormat
import script.CmdbScript;
class SnmpDatasourceController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']
    def list = {
        if(!params.max) params.max = 10
        [ snmpDatasourceList: SnmpDatasource.list( params ) ]
    }

    def show = {
        def snmpDatasource = SnmpDatasource.get([id:params.id])

        if(!snmpDatasource) {
            flash.message = "SnmpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def script = CmdbScript.findByName(snmpDatasource.scriptName)
            if(snmpDatasource.class != SnmpDatasource)
            {
                def controllerName = snmpDatasource.class.name;
                if(controllerName.length() == 1)
                {
                    controllerName = controllerName.toLowerCase();
                }
                else
                {
                    controllerName = controllerName.substring(0,1).toLowerCase()+controllerName.substring(1);
                }
                redirect(action:show, controller:controllerName, id:params.id, script:script)
            }
            else
            {
                return [ snmpDatasource : snmpDatasource, script:script ]
            }
        }
    }

    def delete = {
        def snmpDatasource = SnmpDatasource.get( [id:params.id])
        if(snmpDatasource) {
            try{
                snmpDatasource.remove()
                flash.message = "SnmpDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[SnmpDatasource, snmpDatasource])]
                flash.errors = errors;
                redirect(action:show, id:snmpDatasource.id)
            }

        }
        else {
            flash.message = "SnmpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def snmpDatasource = SnmpDatasource.get( [id:params.id] )

        if(!snmpDatasource) {
            flash.message = "SnmpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ snmpDatasource : snmpDatasource ]
        }
    }

    
    def update = {
        def snmpDatasource = SnmpDatasource.get( [id:params.id] )
        if(snmpDatasource) {
            snmpDatasource.update(ControllerUtils.getClassProperties(params, SnmpDatasource));
            if(!snmpDatasource.hasErrors()) {
                flash.message = "SnmpDatasource ${params.id} updated"
                redirect(action:show,id:snmpDatasource.id)
            }
            else {
                render(view:'edit',model:[snmpDatasource:snmpDatasource])
            }
        }
        else {
            flash.message = "SnmpDatasource not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def snmpDatasource = new SnmpDatasource()
        snmpDatasource.properties = params
        return ['snmpDatasource':snmpDatasource]
    }

    def save = {
        def snmpDatasource = SnmpDatasource.add(ControllerUtils.getClassProperties(params, SnmpDatasource))
        if(!snmpDatasource.hasErrors()) {
            flash.message = "SnmpDatasource ${snmpDatasource.id} created"
            redirect(action:show,id:snmpDatasource.id)
        }
        else {
            render(view:'create',model:[snmpDatasource:snmpDatasource])
        }
    }

    def addTo = {
        def snmpDatasource = SnmpDatasource.get( [id:params.id] )
        if(!snmpDatasource){
            flash.message = "SnmpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = snmpDatasource.hasMany[relationName];
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [snmpDatasource:snmpDatasource, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:snmpDatasource.id)
            }
        }
    }

    def addRelation = {
        def snmpDatasource = SnmpDatasource.get( [id:params.id] )
        if(!snmpDatasource) {
            flash.message = "SnmpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = snmpDatasource.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      snmpDatasource.addRelation(relationMap);
                      if(snmpDatasource.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[snmpDatasource:snmpDatasource, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SnmpDatasource ${params.id} updated"
                          redirect(action:edit,id:snmpDatasource.id)
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
        def snmpDatasource = SnmpDatasource.get( [id:params.id] )
        if(!snmpDatasource) {
            flash.message = "SnmpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = snmpDatasource.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      snmpDatasource.removeRelation(relationMap);
                      if(snmpDatasource.hasErrors()){
                          render(view:'edit',model:[snmpDatasource:snmpDatasource])
                      }
                      else{
                          flash.message = "SnmpDatasource ${params.id} updated"
                          redirect(action:edit,id:snmpDatasource.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:snmpDatasource.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:snmpDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SnmpDatasource")
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