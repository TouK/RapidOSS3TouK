package datasource;
import com.ifountain.rcmdb.domain.util.ControllerUtils;

class NetcoolConversionParameterController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ netcoolConversionParameterList: NetcoolConversionParameter.list( params ) ]
    }

    def show = {
        def netcoolConversionParameter = NetcoolConversionParameter.get([id:params.id])

        if(!netcoolConversionParameter) {
            flash.message = "NetcoolConversionParameter not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(netcoolConversionParameter.class != NetcoolConversionParameter)
            {
                def controllerName = netcoolConversionParameter.class.name;
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
                return [ netcoolConversionParameter : netcoolConversionParameter ]
            }
        }
    }

    def delete = {
        def netcoolConversionParameter = NetcoolConversionParameter.get( [id:params.id])
        if(netcoolConversionParameter) {
            try{
                netcoolConversionParameter.remove()
                flash.message = "NetcoolConversionParameter ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[NetcoolConversionParameter, netcoolConversionParameter])]
                flash.errors = errors;
                redirect(action:show, id:netcoolConversionParameter.id)
            }

        }
        else {
            flash.message = "NetcoolConversionParameter not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def netcoolConversionParameter = NetcoolConversionParameter.get( [id:params.id] )

        if(!netcoolConversionParameter) {
            flash.message = "NetcoolConversionParameter not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ netcoolConversionParameter : netcoolConversionParameter ]
        }
    }


    def update = {
        def netcoolConversionParameter = NetcoolConversionParameter.get( [id:params.id] )
        if(netcoolConversionParameter) {
            netcoolConversionParameter.update(ControllerUtils.getClassProperties(params, NetcoolConversionParameter));
            if(!netcoolConversionParameter.hasErrors()) {
                flash.message = "NetcoolConversionParameter ${params.id} updated"
                redirect(action:show,id:netcoolConversionParameter.id)
            }
            else {
                render(view:'edit',model:[netcoolConversionParameter:netcoolConversionParameter])
            }
        }
        else {
            flash.message = "NetcoolConversionParameter not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def netcoolConversionParameter = new NetcoolConversionParameter()
        netcoolConversionParameter.properties = params
        return ['netcoolConversionParameter':netcoolConversionParameter]
    }

    def save = {
        def netcoolConversionParameter = NetcoolConversionParameter.add(ControllerUtils.getClassProperties(params, NetcoolConversionParameter))
        if(!netcoolConversionParameter.hasErrors()) {
            flash.message = "NetcoolConversionParameter ${netcoolConversionParameter.id} created"
            redirect(action:show,id:netcoolConversionParameter.id)
        }
        else {
            render(view:'create',model:[netcoolConversionParameter:netcoolConversionParameter])
        }
    }

    def addTo = {
        def netcoolConversionParameter = NetcoolConversionParameter.get( [id:params.id] )
        if(!netcoolConversionParameter){
            flash.message = "NetcoolConversionParameter not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = netcoolConversionParameter.hasMany[relationName];
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [netcoolConversionParameter:netcoolConversionParameter, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:netcoolConversionParameter.id)
            }
        }
    }

    def addRelation = {
        def netcoolConversionParameter = NetcoolConversionParameter.get( [id:params.id] )
        if(!netcoolConversionParameter) {
            flash.message = "NetcoolConversionParameter not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = netcoolConversionParameter.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      netcoolConversionParameter.addRelation(relationMap);
                      if(netcoolConversionParameter.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[netcoolConversionParameter:netcoolConversionParameter, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "NetcoolConversionParameter ${params.id} updated"
                          redirect(action:edit,id:netcoolConversionParameter.id)
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
        def netcoolConversionParameter = NetcoolConversionParameter.get( [id:params.id] )
        if(!netcoolConversionParameter) {
            flash.message = "NetcoolConversionParameter not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = netcoolConversionParameter.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      netcoolConversionParameter.removeRelation(relationMap);
                      if(netcoolConversionParameter.hasErrors()){
                          render(view:'edit',model:[netcoolConversionParameter:netcoolConversionParameter])
                      }
                      else{
                          flash.message = "NetcoolConversionParameter ${params.id} updated"
                          redirect(action:edit,id:netcoolConversionParameter.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:netcoolConversionParameter.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:netcoolConversionParameter.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("NetcoolConversionParameter")
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