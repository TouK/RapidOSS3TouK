import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class OpenNmsServiceController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ openNmsServiceList: OpenNmsService.list( params ) ]
    }

    def show = {
        def openNmsService = OpenNmsService.get([id:params.id])

        if(!openNmsService) {
            flash.message = "OpenNmsService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(openNmsService.class != OpenNmsService)
            {
                def controllerName = openNmsService.class.simpleName;
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
                return [ openNmsService : openNmsService ]
            }
        }
    }

    def delete = {
        def openNmsService = OpenNmsService.get( [id:params.id])
        if(openNmsService) {
            try{
                openNmsService.remove()
                flash.message = "OpenNmsService ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [OpenNmsService, openNmsService])
                flash.errors = this.errors;
                redirect(action:show, id:openNmsService.id)
            }

        }
        else {
            flash.message = "OpenNmsService not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def openNmsService = OpenNmsService.get( [id:params.id] )

        if(!openNmsService) {
            flash.message = "OpenNmsService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ openNmsService : openNmsService ]
        }
    }


    def update = {
        def openNmsService = OpenNmsService.get( [id:params.id] )
        if(openNmsService) {
            openNmsService.update(ControllerUtils.getClassProperties(params, OpenNmsService));
            if(!openNmsService.hasErrors()) {
                flash.message = "OpenNmsService ${params.id} updated"
                redirect(action:show,id:openNmsService.id)
            }
            else {
                render(view:'edit',model:[openNmsService:openNmsService])
            }
        }
        else {
            flash.message = "OpenNmsService not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def openNmsService = new OpenNmsService()
        openNmsService.properties = params
        return ['openNmsService':openNmsService]
    }

    def save = {
        def openNmsService = OpenNmsService.add(ControllerUtils.getClassProperties(params, OpenNmsService))
        if(!openNmsService.hasErrors()) {
            flash.message = "OpenNmsService ${openNmsService.id} created"
            redirect(action:show,id:openNmsService.id)
        }
        else {
            render(view:'create',model:[openNmsService:openNmsService])
        }
    }

    def addTo = {
        def openNmsService = OpenNmsService.get( [id:params.id] )
        if(!openNmsService){
            flash.message = "OpenNmsService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(OpenNmsService, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [openNmsService:openNmsService, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:openNmsService.id)
            }
        }
    }



    def addRelation = {
        def openNmsService = OpenNmsService.get( [id:params.id] )
        if(!openNmsService) {
            flash.message = "OpenNmsService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(OpenNmsService, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      openNmsService.addRelation(relationMap);
                      if(openNmsService.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[openNmsService:openNmsService, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "OpenNmsService ${params.id} updated"
                          redirect(action:edit,id:openNmsService.id)
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
        def openNmsService = OpenNmsService.get( [id:params.id] )
        if(!openNmsService) {
            flash.message = "OpenNmsService not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(OpenNmsService, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      openNmsService.removeRelation(relationMap);
                      if(openNmsService.hasErrors()){
                          render(view:'edit',model:[openNmsService:openNmsService])
                      }
                      else{
                          flash.message = "OpenNmsService ${params.id} updated"
                          redirect(action:edit,id:openNmsService.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:openNmsService.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:openNmsService.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("OpenNmsService")
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