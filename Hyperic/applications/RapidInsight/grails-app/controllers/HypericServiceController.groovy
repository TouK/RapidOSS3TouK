import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class HypericServiceController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ hypericServiceList: HypericService.list( params ) ]
    }

    def show = {
        def hypericService = HypericService.get([id:params.id])

        if(!hypericService) {
            flash.message = "HypericService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(hypericService.class != HypericService)
            {
                def controllerName = hypericService.class.simpleName;
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
                return [ hypericService : hypericService ]
            }
        }
    }

    def delete = {
        def hypericService = HypericService.get( [id:params.id])
        if(hypericService) {
            try{
                hypericService.remove()
                flash.message = "HypericService ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [HypericService, hypericService])
                flash.errors = this.errors;
                redirect(action:show, id:hypericService.id)
            }

        }
        else {
            flash.message = "HypericService not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def hypericService = HypericService.get( [id:params.id] )

        if(!hypericService) {
            flash.message = "HypericService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ hypericService : hypericService ]
        }
    }


    def update = {
        def hypericService = HypericService.get( [id:params.id] )
        if(hypericService) {
            hypericService.update(ControllerUtils.getClassProperties(params, HypericService));
            if(!hypericService.hasErrors()) {
                flash.message = "HypericService ${params.id} updated"
                redirect(action:show,id:hypericService.id)
            }
            else {
                render(view:'edit',model:[hypericService:hypericService])
            }
        }
        else {
            flash.message = "HypericService not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def hypericService = new HypericService()
        hypericService.properties = params
        return ['hypericService':hypericService]
    }

    def save = {
        def hypericService = HypericService.add(ControllerUtils.getClassProperties(params, HypericService))
        if(!hypericService.hasErrors()) {
            flash.message = "HypericService ${hypericService.id} created"
            redirect(action:show,id:hypericService.id)
        }
        else {
            render(view:'create',model:[hypericService:hypericService])
        }
    }

    def addTo = {
        def hypericService = HypericService.get( [id:params.id] )
        if(!hypericService){
            flash.message = "HypericService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(HypericService, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [hypericService:hypericService, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:hypericService.id)
            }
        }
    }



    def addRelation = {
        def hypericService = HypericService.get( [id:params.id] )
        if(!hypericService) {
            flash.message = "HypericService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(HypericService, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      hypericService.addRelation(relationMap);
                      if(hypericService.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[hypericService:hypericService, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "HypericService ${params.id} updated"
                          redirect(action:edit,id:hypericService.id)
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
        def hypericService = HypericService.get( [id:params.id] )
        if(!hypericService) {
            flash.message = "HypericService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(HypericService, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      hypericService.removeRelation(relationMap);
                      if(hypericService.hasErrors()){
                          render(view:'edit',model:[hypericService:hypericService])
                      }
                      else{
                          flash.message = "HypericService ${params.id} updated"
                          redirect(action:edit,id:hypericService.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:hypericService.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:hypericService.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("HypericService")
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