import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class SmartsIpNetworkController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ smartsIpNetworkList: SmartsIpNetwork.list( params ) ]
    }

    def show = {
        def smartsIpNetwork = SmartsIpNetwork.get([id:params.id])

        if(!smartsIpNetwork) {
            flash.message = "SmartsIpNetwork not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsIpNetwork.class != SmartsIpNetwork)
            {
                def controllerName = smartsIpNetwork.class.simpleName;
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
                return [ smartsIpNetwork : smartsIpNetwork ]
            }
        }
    }

    def delete = {
        def smartsIpNetwork = SmartsIpNetwork.get( [id:params.id])
        if(smartsIpNetwork) {
            try{
                smartsIpNetwork.remove()
                flash.message = "SmartsIpNetwork ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [SmartsIpNetwork, smartsIpNetwork])
                flash.errors = this.errors;
                redirect(action:show, id:smartsIpNetwork.id)
            }

        }
        else {
            flash.message = "SmartsIpNetwork not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsIpNetwork = SmartsIpNetwork.get( [id:params.id] )

        if(!smartsIpNetwork) {
            flash.message = "SmartsIpNetwork not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsIpNetwork : smartsIpNetwork ]
        }
    }


    def update = {
        def smartsIpNetwork = SmartsIpNetwork.get( [id:params.id] )
        if(smartsIpNetwork) {
            smartsIpNetwork.update(ControllerUtils.getClassProperties(params, SmartsIpNetwork));
            if(!smartsIpNetwork.hasErrors()) {
                flash.message = "SmartsIpNetwork ${params.id} updated"
                redirect(action:show,id:smartsIpNetwork.id)
            }
            else {
                render(view:'edit',model:[smartsIpNetwork:smartsIpNetwork])
            }
        }
        else {
            flash.message = "SmartsIpNetwork not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsIpNetwork = new SmartsIpNetwork()
        smartsIpNetwork.properties = params
        return ['smartsIpNetwork':smartsIpNetwork]
    }

    def save = {
        def smartsIpNetwork = SmartsIpNetwork.add(ControllerUtils.getClassProperties(params, SmartsIpNetwork))
        if(!smartsIpNetwork.hasErrors()) {
            flash.message = "SmartsIpNetwork ${smartsIpNetwork.id} created"
            redirect(action:show,id:smartsIpNetwork.id)
        }
        else {
            render(view:'create',model:[smartsIpNetwork:smartsIpNetwork])
        }
    }

    def addTo = {
        def smartsIpNetwork = SmartsIpNetwork.get( [id:params.id] )
        if(!smartsIpNetwork){
            flash.message = "SmartsIpNetwork not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(SmartsIpNetwork, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsIpNetwork:smartsIpNetwork, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsIpNetwork.id)
            }
        }
    }



    def addRelation = {
        def smartsIpNetwork = SmartsIpNetwork.get( [id:params.id] )
        if(!smartsIpNetwork) {
            flash.message = "SmartsIpNetwork not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(SmartsIpNetwork, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsIpNetwork.addRelation(relationMap);
                      if(smartsIpNetwork.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsIpNetwork:smartsIpNetwork, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsIpNetwork ${params.id} updated"
                          redirect(action:edit,id:smartsIpNetwork.id)
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
        def smartsIpNetwork = SmartsIpNetwork.get( [id:params.id] )
        if(!smartsIpNetwork) {
            flash.message = "SmartsIpNetwork not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(SmartsIpNetwork, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsIpNetwork.removeRelation(relationMap);
                      if(smartsIpNetwork.hasErrors()){
                          render(view:'edit',model:[smartsIpNetwork:smartsIpNetwork])
                      }
                      else{
                          flash.message = "SmartsIpNetwork ${params.id} updated"
                          redirect(action:edit,id:smartsIpNetwork.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsIpNetwork.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsIpNetwork.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SmartsIpNetwork")
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