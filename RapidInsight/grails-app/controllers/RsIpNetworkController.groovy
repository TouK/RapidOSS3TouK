import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsIpNetworkController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsIpNetworkList: RsIpNetwork.list( params ) ]
    }

    def show = {
        def rsIpNetwork = RsIpNetwork.get([id:params.id])

        if(!rsIpNetwork) {
            flash.message = "RsIpNetwork not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsIpNetwork.class != RsIpNetwork)
            {
                def controllerName = rsIpNetwork.class.simpleName;
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
                return [ rsIpNetwork : rsIpNetwork ]
            }
        }
    }

    def delete = {
        def rsIpNetwork = RsIpNetwork.get( [id:params.id])
        if(rsIpNetwork) {
            try{
                rsIpNetwork.remove()
                flash.message = "RsIpNetwork ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsIpNetwork, rsIpNetwork])
                flash.errors = this.errors;
                redirect(action:show, id:rsIpNetwork.id)
            }

        }
        else {
            flash.message = "RsIpNetwork not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsIpNetwork = RsIpNetwork.get( [id:params.id] )

        if(!rsIpNetwork) {
            flash.message = "RsIpNetwork not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsIpNetwork : rsIpNetwork ]
        }
    }


    def update = {
        def rsIpNetwork = RsIpNetwork.get( [id:params.id] )
        if(rsIpNetwork) {
            rsIpNetwork.update(ControllerUtils.getClassProperties(params, RsIpNetwork));
            if(!rsIpNetwork.hasErrors()) {
                flash.message = "RsIpNetwork ${params.id} updated"
                redirect(action:show,id:rsIpNetwork.id)
            }
            else {
                render(view:'edit',model:[rsIpNetwork:rsIpNetwork])
            }
        }
        else {
            flash.message = "RsIpNetwork not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsIpNetwork = new RsIpNetwork()
        rsIpNetwork.properties = params
        return ['rsIpNetwork':rsIpNetwork]
    }

    def save = {
        def rsIpNetwork = RsIpNetwork.add(ControllerUtils.getClassProperties(params, RsIpNetwork))
        if(!rsIpNetwork.hasErrors()) {
            flash.message = "RsIpNetwork ${rsIpNetwork.id} created"
            redirect(action:show,id:rsIpNetwork.id)
        }
        else {
            render(view:'create',model:[rsIpNetwork:rsIpNetwork])
        }
    }

    def addTo = {
        def rsIpNetwork = RsIpNetwork.get( [id:params.id] )
        if(!rsIpNetwork){
            flash.message = "RsIpNetwork not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsIpNetwork, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsIpNetwork:rsIpNetwork, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsIpNetwork.id)
            }
        }
    }



    def addRelation = {
        def rsIpNetwork = RsIpNetwork.get( [id:params.id] )
        if(!rsIpNetwork) {
            flash.message = "RsIpNetwork not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsIpNetwork, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsIpNetwork.addRelation(relationMap);
                      if(rsIpNetwork.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsIpNetwork:rsIpNetwork, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsIpNetwork ${params.id} updated"
                          redirect(action:edit,id:rsIpNetwork.id)
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
        def rsIpNetwork = RsIpNetwork.get( [id:params.id] )
        if(!rsIpNetwork) {
            flash.message = "RsIpNetwork not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsIpNetwork, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsIpNetwork.removeRelation(relationMap);
                      if(rsIpNetwork.hasErrors()){
                          render(view:'edit',model:[rsIpNetwork:rsIpNetwork])
                      }
                      else{
                          flash.message = "RsIpNetwork ${params.id} updated"
                          redirect(action:edit,id:rsIpNetwork.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsIpNetwork.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsIpNetwork.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsIpNetwork")
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