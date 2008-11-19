import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class SmartsComputerSystemComponentController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ smartsComputerSystemComponentList: SmartsComputerSystemComponent.list( params ) ]
    }

    def show = {
        def smartsComputerSystemComponent = SmartsComputerSystemComponent.get([id:params.id])

        if(!smartsComputerSystemComponent) {
            flash.message = "SmartsComputerSystemComponent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsComputerSystemComponent.class != SmartsComputerSystemComponent)
            {
                def controllerName = smartsComputerSystemComponent.class.simpleName;
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
                return [ smartsComputerSystemComponent : smartsComputerSystemComponent ]
            }
        }
    }

    def delete = {
        def smartsComputerSystemComponent = SmartsComputerSystemComponent.get( [id:params.id])
        if(smartsComputerSystemComponent) {
            try{
                smartsComputerSystemComponent.remove()
                flash.message = "SmartsComputerSystemComponent ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [SmartsComputerSystemComponent, smartsComputerSystemComponent])
                flash.errors = this.errors;
                redirect(action:show, id:smartsComputerSystemComponent.id)
            }

        }
        else {
            flash.message = "SmartsComputerSystemComponent not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsComputerSystemComponent = SmartsComputerSystemComponent.get( [id:params.id] )

        if(!smartsComputerSystemComponent) {
            flash.message = "SmartsComputerSystemComponent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsComputerSystemComponent : smartsComputerSystemComponent ]
        }
    }


    def update = {
        def smartsComputerSystemComponent = SmartsComputerSystemComponent.get( [id:params.id] )
        if(smartsComputerSystemComponent) {
            smartsComputerSystemComponent.update(ControllerUtils.getClassProperties(params, SmartsComputerSystemComponent));
            if(!smartsComputerSystemComponent.hasErrors()) {
                flash.message = "SmartsComputerSystemComponent ${params.id} updated"
                redirect(action:show,id:smartsComputerSystemComponent.id)
            }
            else {
                render(view:'edit',model:[smartsComputerSystemComponent:smartsComputerSystemComponent])
            }
        }
        else {
            flash.message = "SmartsComputerSystemComponent not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsComputerSystemComponent = new SmartsComputerSystemComponent()
        smartsComputerSystemComponent.properties = params
        return ['smartsComputerSystemComponent':smartsComputerSystemComponent]
    }

    def save = {
        def smartsComputerSystemComponent = SmartsComputerSystemComponent.add(ControllerUtils.getClassProperties(params, SmartsComputerSystemComponent))
        if(!smartsComputerSystemComponent.hasErrors()) {
            flash.message = "SmartsComputerSystemComponent ${smartsComputerSystemComponent.id} created"
            redirect(action:show,id:smartsComputerSystemComponent.id)
        }
        else {
            render(view:'create',model:[smartsComputerSystemComponent:smartsComputerSystemComponent])
        }
    }

    def addTo = {
        def smartsComputerSystemComponent = SmartsComputerSystemComponent.get( [id:params.id] )
        if(!smartsComputerSystemComponent){
            flash.message = "SmartsComputerSystemComponent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(SmartsComputerSystemComponent, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsComputerSystemComponent:smartsComputerSystemComponent, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsComputerSystemComponent.id)
            }
        }
    }



    def addRelation = {
        def smartsComputerSystemComponent = SmartsComputerSystemComponent.get( [id:params.id] )
        if(!smartsComputerSystemComponent) {
            flash.message = "SmartsComputerSystemComponent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(SmartsComputerSystemComponent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsComputerSystemComponent.addRelation(relationMap);
                      if(smartsComputerSystemComponent.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsComputerSystemComponent:smartsComputerSystemComponent, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsComputerSystemComponent ${params.id} updated"
                          redirect(action:edit,id:smartsComputerSystemComponent.id)
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
        def smartsComputerSystemComponent = SmartsComputerSystemComponent.get( [id:params.id] )
        if(!smartsComputerSystemComponent) {
            flash.message = "SmartsComputerSystemComponent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(SmartsComputerSystemComponent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsComputerSystemComponent.removeRelation(relationMap);
                      if(smartsComputerSystemComponent.hasErrors()){
                          render(view:'edit',model:[smartsComputerSystemComponent:smartsComputerSystemComponent])
                      }
                      else{
                          flash.message = "SmartsComputerSystemComponent ${params.id} updated"
                          redirect(action:edit,id:smartsComputerSystemComponent.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsComputerSystemComponent.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsComputerSystemComponent.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SmartsComputerSystemComponent")
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