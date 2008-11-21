import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class SmartsHSRPEndpointController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ smartsHSRPEndpointList: SmartsHSRPEndpoint.list( params ) ]
    }

    def show = {
        def smartsHSRPEndpoint = SmartsHSRPEndpoint.get([id:params.id])

        if(!smartsHSRPEndpoint) {
            flash.message = "SmartsHSRPEndpoint not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsHSRPEndpoint.class != SmartsHSRPEndpoint)
            {
                def controllerName = smartsHSRPEndpoint.class.simpleName;
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
                return [ smartsHSRPEndpoint : smartsHSRPEndpoint ]
            }
        }
    }

    def delete = {
        def smartsHSRPEndpoint = SmartsHSRPEndpoint.get( [id:params.id])
        if(smartsHSRPEndpoint) {
            try{
                smartsHSRPEndpoint.remove()
                flash.message = "SmartsHSRPEndpoint ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [SmartsHSRPEndpoint, smartsHSRPEndpoint])
                flash.errors = this.errors;
                redirect(action:show, id:smartsHSRPEndpoint.id)
            }

        }
        else {
            flash.message = "SmartsHSRPEndpoint not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsHSRPEndpoint = SmartsHSRPEndpoint.get( [id:params.id] )

        if(!smartsHSRPEndpoint) {
            flash.message = "SmartsHSRPEndpoint not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsHSRPEndpoint : smartsHSRPEndpoint ]
        }
    }


    def update = {
        def smartsHSRPEndpoint = SmartsHSRPEndpoint.get( [id:params.id] )
        if(smartsHSRPEndpoint) {
            smartsHSRPEndpoint.update(ControllerUtils.getClassProperties(params, SmartsHSRPEndpoint));
            if(!smartsHSRPEndpoint.hasErrors()) {
                flash.message = "SmartsHSRPEndpoint ${params.id} updated"
                redirect(action:show,id:smartsHSRPEndpoint.id)
            }
            else {
                render(view:'edit',model:[smartsHSRPEndpoint:smartsHSRPEndpoint])
            }
        }
        else {
            flash.message = "SmartsHSRPEndpoint not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsHSRPEndpoint = new SmartsHSRPEndpoint()
        smartsHSRPEndpoint.properties = params
        return ['smartsHSRPEndpoint':smartsHSRPEndpoint]
    }

    def save = {
        def smartsHSRPEndpoint = SmartsHSRPEndpoint.add(ControllerUtils.getClassProperties(params, SmartsHSRPEndpoint))
        if(!smartsHSRPEndpoint.hasErrors()) {
            flash.message = "SmartsHSRPEndpoint ${smartsHSRPEndpoint.id} created"
            redirect(action:show,id:smartsHSRPEndpoint.id)
        }
        else {
            render(view:'create',model:[smartsHSRPEndpoint:smartsHSRPEndpoint])
        }
    }

    def addTo = {
        def smartsHSRPEndpoint = SmartsHSRPEndpoint.get( [id:params.id] )
        if(!smartsHSRPEndpoint){
            flash.message = "SmartsHSRPEndpoint not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(SmartsHSRPEndpoint, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsHSRPEndpoint:smartsHSRPEndpoint, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsHSRPEndpoint.id)
            }
        }
    }



    def addRelation = {
        def smartsHSRPEndpoint = SmartsHSRPEndpoint.get( [id:params.id] )
        if(!smartsHSRPEndpoint) {
            flash.message = "SmartsHSRPEndpoint not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(SmartsHSRPEndpoint, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsHSRPEndpoint.addRelation(relationMap);
                      if(smartsHSRPEndpoint.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsHSRPEndpoint:smartsHSRPEndpoint, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsHSRPEndpoint ${params.id} updated"
                          redirect(action:edit,id:smartsHSRPEndpoint.id)
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
        def smartsHSRPEndpoint = SmartsHSRPEndpoint.get( [id:params.id] )
        if(!smartsHSRPEndpoint) {
            flash.message = "SmartsHSRPEndpoint not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(SmartsHSRPEndpoint, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsHSRPEndpoint.removeRelation(relationMap);
                      if(smartsHSRPEndpoint.hasErrors()){
                          render(view:'edit',model:[smartsHSRPEndpoint:smartsHSRPEndpoint])
                      }
                      else{
                          flash.message = "SmartsHSRPEndpoint ${params.id} updated"
                          redirect(action:edit,id:smartsHSRPEndpoint.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsHSRPEndpoint.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsHSRPEndpoint.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SmartsHSRPEndpoint")
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