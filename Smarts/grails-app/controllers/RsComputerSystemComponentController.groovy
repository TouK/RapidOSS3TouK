import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsComputerSystemComponentController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsComputerSystemComponentList: RsComputerSystemComponent.list( params ) ]
    }

    def show = {
        def rsComputerSystemComponent = RsComputerSystemComponent.get([id:params.id])

        if(!rsComputerSystemComponent) {
            flash.message = "RsComputerSystemComponent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsComputerSystemComponent.class != RsComputerSystemComponent)
            {
                def controllerName = rsComputerSystemComponent.class.simpleName;
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
                return [ rsComputerSystemComponent : rsComputerSystemComponent ]
            }
        }
    }

    def delete = {
        def rsComputerSystemComponent = RsComputerSystemComponent.get( [id:params.id])
        if(rsComputerSystemComponent) {
            try{
                rsComputerSystemComponent.remove()
                flash.message = "RsComputerSystemComponent ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsComputerSystemComponent, rsComputerSystemComponent])
                flash.errors = this.errors;
                redirect(action:show, id:rsComputerSystemComponent.id)
            }

        }
        else {
            flash.message = "RsComputerSystemComponent not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsComputerSystemComponent = RsComputerSystemComponent.get( [id:params.id] )

        if(!rsComputerSystemComponent) {
            flash.message = "RsComputerSystemComponent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsComputerSystemComponent : rsComputerSystemComponent ]
        }
    }


    def update = {
        def rsComputerSystemComponent = RsComputerSystemComponent.get( [id:params.id] )
        if(rsComputerSystemComponent) {
            rsComputerSystemComponent.update(ControllerUtils.getClassProperties(params, RsComputerSystemComponent));
            if(!rsComputerSystemComponent.hasErrors()) {
                flash.message = "RsComputerSystemComponent ${params.id} updated"
                redirect(action:show,id:rsComputerSystemComponent.id)
            }
            else {
                render(view:'edit',model:[rsComputerSystemComponent:rsComputerSystemComponent])
            }
        }
        else {
            flash.message = "RsComputerSystemComponent not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsComputerSystemComponent = new RsComputerSystemComponent()
        rsComputerSystemComponent.properties = params
        return ['rsComputerSystemComponent':rsComputerSystemComponent]
    }

    def save = {
        def rsComputerSystemComponent = RsComputerSystemComponent.add(ControllerUtils.getClassProperties(params, RsComputerSystemComponent))
        if(!rsComputerSystemComponent.hasErrors()) {
            flash.message = "RsComputerSystemComponent ${rsComputerSystemComponent.id} created"
            redirect(action:show,id:rsComputerSystemComponent.id)
        }
        else {
            render(view:'create',model:[rsComputerSystemComponent:rsComputerSystemComponent])
        }
    }

    def addTo = {
        def rsComputerSystemComponent = RsComputerSystemComponent.get( [id:params.id] )
        if(!rsComputerSystemComponent){
            flash.message = "RsComputerSystemComponent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsComputerSystemComponent, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsComputerSystemComponent:rsComputerSystemComponent, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsComputerSystemComponent.id)
            }
        }
    }



    def addRelation = {
        def rsComputerSystemComponent = RsComputerSystemComponent.get( [id:params.id] )
        if(!rsComputerSystemComponent) {
            flash.message = "RsComputerSystemComponent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsComputerSystemComponent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsComputerSystemComponent.addRelation(relationMap);
                      if(rsComputerSystemComponent.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsComputerSystemComponent:rsComputerSystemComponent, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsComputerSystemComponent ${params.id} updated"
                          redirect(action:edit,id:rsComputerSystemComponent.id)
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
        def rsComputerSystemComponent = RsComputerSystemComponent.get( [id:params.id] )
        if(!rsComputerSystemComponent) {
            flash.message = "RsComputerSystemComponent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsComputerSystemComponent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsComputerSystemComponent.removeRelation(relationMap);
                      if(rsComputerSystemComponent.hasErrors()){
                          render(view:'edit',model:[rsComputerSystemComponent:rsComputerSystemComponent])
                      }
                      else{
                          flash.message = "RsComputerSystemComponent ${params.id} updated"
                          redirect(action:edit,id:rsComputerSystemComponent.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsComputerSystemComponent.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsComputerSystemComponent.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsComputerSystemComponent")
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