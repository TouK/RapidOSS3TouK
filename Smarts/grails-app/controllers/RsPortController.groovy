import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsPortController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsPortList: RsPort.list( params ) ]
    }

    def show = {
        def rsPort = RsPort.get([id:params.id])

        if(!rsPort) {
            flash.message = "RsPort not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsPort.class != RsPort)
            {
                def controllerName = rsPort.class.name;
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
                return [ rsPort : rsPort ]
            }
        }
    }

    def delete = {
        def rsPort = RsPort.get( [id:params.id])
        if(rsPort) {
            try{
                rsPort.remove()
                flash.message = "RsPort ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsPort, rsPort])
                flash.errors = this.errors;
                redirect(action:show, id:rsPort.id)
            }

        }
        else {
            flash.message = "RsPort not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsPort = RsPort.get( [id:params.id] )

        if(!rsPort) {
            flash.message = "RsPort not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsPort : rsPort ]
        }
    }


    def update = {
        def rsPort = RsPort.get( [id:params.id] )
        if(rsPort) {
            rsPort.update(ControllerUtils.getClassProperties(params, RsPort));
            if(!rsPort.hasErrors()) {
                flash.message = "RsPort ${params.id} updated"
                redirect(action:show,id:rsPort.id)
            }
            else {
                render(view:'edit',model:[rsPort:rsPort])
            }
        }
        else {
            flash.message = "RsPort not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsPort = new RsPort()
        rsPort.properties = params
        return ['rsPort':rsPort]
    }

    def save = {
        def rsPort = RsPort.add(ControllerUtils.getClassProperties(params, RsPort))
        if(!rsPort.hasErrors()) {
            flash.message = "RsPort ${rsPort.id} created"
            redirect(action:show,id:rsPort.id)
        }
        else {
            render(view:'create',model:[rsPort:rsPort])
        }
    }

    def addTo = {
        def rsPort = RsPort.get( [id:params.id] )
        if(!rsPort){
            flash.message = "RsPort not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsPort, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsPort:rsPort, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsPort.id)
            }
        }
    }



    def addRelation = {
        def rsPort = RsPort.get( [id:params.id] )
        if(!rsPort) {
            flash.message = "RsPort not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsPort, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsPort.addRelation(relationMap);
                      if(rsPort.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsPort:rsPort, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsPort ${params.id} updated"
                          redirect(action:edit,id:rsPort.id)
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
        def rsPort = RsPort.get( [id:params.id] )
        if(!rsPort) {
            flash.message = "RsPort not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsPort, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsPort.removeRelation(relationMap);
                      if(rsPort.hasErrors()){
                          render(view:'edit',model:[rsPort:rsPort])
                      }
                      else{
                          flash.message = "RsPort ${params.id} updated"
                          redirect(action:edit,id:rsPort.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsPort.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsPort.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsPort")
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