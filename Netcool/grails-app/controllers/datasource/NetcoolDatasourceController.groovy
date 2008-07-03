package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.text.SimpleDateFormat;
class NetcoolDatasourceController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ netcoolDatasourceList: NetcoolDatasource.list( params ) ]
    }

    def show = {
        def netcoolDatasource = NetcoolDatasource.get([id:params.id])

        if(!netcoolDatasource) {
            flash.message = "NetcoolDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(netcoolDatasource.class != NetcoolDatasource)
            {
                def controllerName = netcoolDatasource.class.name;
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
                return [ netcoolDatasource : netcoolDatasource ]
            }
        }
    }

    def delete = {
        def netcoolDatasource = NetcoolDatasource.get( [id:params.id])
        if(netcoolDatasource) {
            try{
                netcoolDatasource.remove()
                flash.message = "NetcoolDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[NetcoolDatasource, netcoolDatasource])]
                flash.errors = errors;
                redirect(action:show, id:netcoolDatasource.id)
            }

        }
        else {
            flash.message = "NetcoolDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def netcoolDatasource = NetcoolDatasource.get( [id:params.id] )

        if(!netcoolDatasource) {
            flash.message = "NetcoolDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ netcoolDatasource : netcoolDatasource ]
        }
    }


    def update = {
        def netcoolDatasource = NetcoolDatasource.get( [id:params.id] )
        if(netcoolDatasource) {
            netcoolDatasource.update(ControllerUtils.getClassProperties(params, NetcoolDatasource));
            if(!netcoolDatasource.hasErrors()) {
                flash.message = "NetcoolDatasource ${params.id} updated"
                redirect(action:show,id:netcoolDatasource.id)
            }
            else {
                render(view:'edit',model:[netcoolDatasource:netcoolDatasource])
            }
        }
        else {
            flash.message = "NetcoolDatasource not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def netcoolDatasource = new NetcoolDatasource()
        netcoolDatasource.properties = params
        return ['netcoolDatasource':netcoolDatasource]
    }

    def save = {
        def netcoolDatasource = NetcoolDatasource.add(ControllerUtils.getClassProperties(params, NetcoolDatasource))
        if(!netcoolDatasource.hasErrors()) {
            flash.message = "NetcoolDatasource ${netcoolDatasource.id} created"
            redirect(action:show,id:netcoolDatasource.id)
        }
        else {
            render(view:'create',model:[netcoolDatasource:netcoolDatasource])
        }
    }

    def addTo = {
        def netcoolDatasource = NetcoolDatasource.get( [id:params.id] )
        if(!netcoolDatasource){
            flash.message = "NetcoolDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = netcoolDatasource.hasMany[relationName];
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [netcoolDatasource:netcoolDatasource, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:netcoolDatasource.id)
            }
        }
    }

    def addRelation = {
        def netcoolDatasource = NetcoolDatasource.get( [id:params.id] )
        if(!netcoolDatasource) {
            flash.message = "NetcoolDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = netcoolDatasource.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      netcoolDatasource.addRelation(relationMap);
                      if(netcoolDatasource.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[netcoolDatasource:netcoolDatasource, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "NetcoolDatasource ${params.id} updated"
                          redirect(action:edit,id:netcoolDatasource.id)
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
        def netcoolDatasource = NetcoolDatasource.get( [id:params.id] )
        if(!netcoolDatasource) {
            flash.message = "NetcoolDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = netcoolDatasource.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      netcoolDatasource.removeRelation(relationMap);
                      if(netcoolDatasource.hasErrors()){
                          render(view:'edit',model:[netcoolDatasource:netcoolDatasource])
                      }
                      else{
                          flash.message = "NetcoolDatasource ${params.id} updated"
                          redirect(action:edit,id:netcoolDatasource.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:netcoolDatasource.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:netcoolDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("NetcoolDatasource")
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