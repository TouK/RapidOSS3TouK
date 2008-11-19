import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class SmartsComputerSystemController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ smartsComputerSystemList: SmartsComputerSystem.list( params ) ]
    }

    def show = {
        def smartsComputerSystem = SmartsComputerSystem.get([id:params.id])

        if(!smartsComputerSystem) {
            flash.message = "SmartsComputerSystem not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsComputerSystem.class != SmartsComputerSystem)
            {
                def controllerName = smartsComputerSystem.class.simpleName;
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
                return [ smartsComputerSystem : smartsComputerSystem ]
            }
        }
    }

    def delete = {
        def smartsComputerSystem = SmartsComputerSystem.get( [id:params.id])
        if(smartsComputerSystem) {
            try{
                smartsComputerSystem.remove()
                flash.message = "SmartsComputerSystem ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [SmartsComputerSystem, smartsComputerSystem])
                flash.errors = this.errors;
                redirect(action:show, id:smartsComputerSystem.id)
            }

        }
        else {
            flash.message = "SmartsComputerSystem not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsComputerSystem = SmartsComputerSystem.get( [id:params.id] )

        if(!smartsComputerSystem) {
            flash.message = "SmartsComputerSystem not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsComputerSystem : smartsComputerSystem ]
        }
    }


    def update = {
        def smartsComputerSystem = SmartsComputerSystem.get( [id:params.id] )
        if(smartsComputerSystem) {
            smartsComputerSystem.update(ControllerUtils.getClassProperties(params, SmartsComputerSystem));
            if(!smartsComputerSystem.hasErrors()) {
                flash.message = "SmartsComputerSystem ${params.id} updated"
                redirect(action:show,id:smartsComputerSystem.id)
            }
            else {
                render(view:'edit',model:[smartsComputerSystem:smartsComputerSystem])
            }
        }
        else {
            flash.message = "SmartsComputerSystem not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsComputerSystem = new SmartsComputerSystem()
        smartsComputerSystem.properties = params
        return ['smartsComputerSystem':smartsComputerSystem]
    }

    def save = {
        def smartsComputerSystem = SmartsComputerSystem.add(ControllerUtils.getClassProperties(params, SmartsComputerSystem))
        if(!smartsComputerSystem.hasErrors()) {
            flash.message = "SmartsComputerSystem ${smartsComputerSystem.id} created"
            redirect(action:show,id:smartsComputerSystem.id)
        }
        else {
            render(view:'create',model:[smartsComputerSystem:smartsComputerSystem])
        }
    }

    def addTo = {
        def smartsComputerSystem = SmartsComputerSystem.get( [id:params.id] )
        if(!smartsComputerSystem){
            flash.message = "SmartsComputerSystem not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(SmartsComputerSystem, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsComputerSystem:smartsComputerSystem, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsComputerSystem.id)
            }
        }
    }



    def addRelation = {
        def smartsComputerSystem = SmartsComputerSystem.get( [id:params.id] )
        if(!smartsComputerSystem) {
            flash.message = "SmartsComputerSystem not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(SmartsComputerSystem, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsComputerSystem.addRelation(relationMap);
                      if(smartsComputerSystem.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsComputerSystem:smartsComputerSystem, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsComputerSystem ${params.id} updated"
                          redirect(action:edit,id:smartsComputerSystem.id)
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
        def smartsComputerSystem = SmartsComputerSystem.get( [id:params.id] )
        if(!smartsComputerSystem) {
            flash.message = "SmartsComputerSystem not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(SmartsComputerSystem, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsComputerSystem.removeRelation(relationMap);
                      if(smartsComputerSystem.hasErrors()){
                          render(view:'edit',model:[smartsComputerSystem:smartsComputerSystem])
                      }
                      else{
                          flash.message = "SmartsComputerSystem ${params.id} updated"
                          redirect(action:edit,id:smartsComputerSystem.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsComputerSystem.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsComputerSystem.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SmartsComputerSystem")
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