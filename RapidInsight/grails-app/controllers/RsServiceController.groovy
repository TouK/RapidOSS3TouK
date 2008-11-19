import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsServiceController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsServiceList: RsService.list( params ) ]
    }

    def show = {
        def rsService = RsService.get([id:params.id])

        if(!rsService) {
            flash.message = "RsService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsService.class != RsService)
            {
                def controllerName = rsService.class.simpleName;
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
                return [ rsService : rsService ]
            }
        }
    }

    def delete = {
        def rsService = RsService.get( [id:params.id])
        if(rsService) {
            try{
                rsService.remove()
                flash.message = "RsService ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsService, rsService])
                flash.errors = this.errors;
                redirect(action:show, id:rsService.id)
            }

        }
        else {
            flash.message = "RsService not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsService = RsService.get( [id:params.id] )

        if(!rsService) {
            flash.message = "RsService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsService : rsService ]
        }
    }


    def update = {
        def rsService = RsService.get( [id:params.id] )
        if(rsService) {
            rsService.update(ControllerUtils.getClassProperties(params, RsService));
            if(!rsService.hasErrors()) {
                flash.message = "RsService ${params.id} updated"
                redirect(action:show,id:rsService.id)
            }
            else {
                render(view:'edit',model:[rsService:rsService])
            }
        }
        else {
            flash.message = "RsService not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsService = new RsService()
        rsService.properties = params
        return ['rsService':rsService]
    }

    def save = {
        def rsService = RsService.add(ControllerUtils.getClassProperties(params, RsService))
        if(!rsService.hasErrors()) {
            flash.message = "RsService ${rsService.id} created"
            redirect(action:show,id:rsService.id)
        }
        else {
            render(view:'create',model:[rsService:rsService])
        }
    }

    def addTo = {
        def rsService = RsService.get( [id:params.id] )
        if(!rsService){
            flash.message = "RsService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsService, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsService:rsService, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsService.id)
            }
        }
    }



    def addRelation = {
        def rsService = RsService.get( [id:params.id] )
        if(!rsService) {
            flash.message = "RsService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsService, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsService.addRelation(relationMap);
                      if(rsService.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsService:rsService, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsService ${params.id} updated"
                          redirect(action:edit,id:rsService.id)
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
        def rsService = RsService.get( [id:params.id] )
        if(!rsService) {
            flash.message = "RsService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsService, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsService.removeRelation(relationMap);
                      if(rsService.hasErrors()){
                          render(view:'edit',model:[rsService:rsService])
                      }
                      else{
                          flash.message = "RsService ${params.id} updated"
                          redirect(action:edit,id:rsService.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsService.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsService.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsService")
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