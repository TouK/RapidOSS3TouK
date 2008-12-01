import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class HypericResourceController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ hypericResourceList: HypericResource.list( params ) ]
    }

    def show = {
        def hypericResource = HypericResource.get([id:params.id])

        if(!hypericResource) {
            flash.message = "HypericResource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(hypericResource.class != HypericResource)
            {
                def controllerName = hypericResource.class.simpleName;
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
                return [ hypericResource : hypericResource ]
            }
        }
    }

    def delete = {
        def hypericResource = HypericResource.get( [id:params.id])
        if(hypericResource) {
            try{
                hypericResource.remove()
                flash.message = "HypericResource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [HypericResource, hypericResource])
                flash.errors = this.errors;
                redirect(action:show, id:hypericResource.id)
            }

        }
        else {
            flash.message = "HypericResource not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def hypericResource = HypericResource.get( [id:params.id] )

        if(!hypericResource) {
            flash.message = "HypericResource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ hypericResource : hypericResource ]
        }
    }


    def update = {
        def hypericResource = HypericResource.get( [id:params.id] )
        if(hypericResource) {
            hypericResource.update(ControllerUtils.getClassProperties(params, HypericResource));
            if(!hypericResource.hasErrors()) {
                flash.message = "HypericResource ${params.id} updated"
                redirect(action:show,id:hypericResource.id)
            }
            else {
                render(view:'edit',model:[hypericResource:hypericResource])
            }
        }
        else {
            flash.message = "HypericResource not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def hypericResource = new HypericResource()
        hypericResource.properties = params
        return ['hypericResource':hypericResource]
    }

    def save = {
        def hypericResource = HypericResource.add(ControllerUtils.getClassProperties(params, HypericResource))
        if(!hypericResource.hasErrors()) {
            flash.message = "HypericResource ${hypericResource.id} created"
            redirect(action:show,id:hypericResource.id)
        }
        else {
            render(view:'create',model:[hypericResource:hypericResource])
        }
    }

    def addTo = {
        def hypericResource = HypericResource.get( [id:params.id] )
        if(!hypericResource){
            flash.message = "HypericResource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(HypericResource, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [hypericResource:hypericResource, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:hypericResource.id)
            }
        }
    }



    def addRelation = {
        def hypericResource = HypericResource.get( [id:params.id] )
        if(!hypericResource) {
            flash.message = "HypericResource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(HypericResource, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      hypericResource.addRelation(relationMap);
                      if(hypericResource.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[hypericResource:hypericResource, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "HypericResource ${params.id} updated"
                          redirect(action:edit,id:hypericResource.id)
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
        def hypericResource = HypericResource.get( [id:params.id] )
        if(!hypericResource) {
            flash.message = "HypericResource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(HypericResource, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      hypericResource.removeRelation(relationMap);
                      if(hypericResource.hasErrors()){
                          render(view:'edit',model:[hypericResource:hypericResource])
                      }
                      else{
                          flash.message = "HypericResource ${params.id} updated"
                          redirect(action:edit,id:hypericResource.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:hypericResource.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:hypericResource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("HypericResource")
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