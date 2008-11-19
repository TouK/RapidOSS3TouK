import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class SmartsInterfaceController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ smartsInterfaceList: SmartsInterface.list( params ) ]
    }

    def show = {
        def smartsInterface = SmartsInterface.get([id:params.id])

        if(!smartsInterface) {
            flash.message = "SmartsInterface not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsInterface.class != SmartsInterface)
            {
                def controllerName = smartsInterface.class.simpleName;
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
                return [ smartsInterface : smartsInterface ]
            }
        }
    }

    def delete = {
        def smartsInterface = SmartsInterface.get( [id:params.id])
        if(smartsInterface) {
            try{
                smartsInterface.remove()
                flash.message = "SmartsInterface ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [SmartsInterface, smartsInterface])
                flash.errors = this.errors;
                redirect(action:show, id:smartsInterface.id)
            }

        }
        else {
            flash.message = "SmartsInterface not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsInterface = SmartsInterface.get( [id:params.id] )

        if(!smartsInterface) {
            flash.message = "SmartsInterface not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsInterface : smartsInterface ]
        }
    }


    def update = {
        def smartsInterface = SmartsInterface.get( [id:params.id] )
        if(smartsInterface) {
            smartsInterface.update(ControllerUtils.getClassProperties(params, SmartsInterface));
            if(!smartsInterface.hasErrors()) {
                flash.message = "SmartsInterface ${params.id} updated"
                redirect(action:show,id:smartsInterface.id)
            }
            else {
                render(view:'edit',model:[smartsInterface:smartsInterface])
            }
        }
        else {
            flash.message = "SmartsInterface not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsInterface = new SmartsInterface()
        smartsInterface.properties = params
        return ['smartsInterface':smartsInterface]
    }

    def save = {
        def smartsInterface = SmartsInterface.add(ControllerUtils.getClassProperties(params, SmartsInterface))
        if(!smartsInterface.hasErrors()) {
            flash.message = "SmartsInterface ${smartsInterface.id} created"
            redirect(action:show,id:smartsInterface.id)
        }
        else {
            render(view:'create',model:[smartsInterface:smartsInterface])
        }
    }

    def addTo = {
        def smartsInterface = SmartsInterface.get( [id:params.id] )
        if(!smartsInterface){
            flash.message = "SmartsInterface not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(SmartsInterface, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsInterface:smartsInterface, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsInterface.id)
            }
        }
    }



    def addRelation = {
        def smartsInterface = SmartsInterface.get( [id:params.id] )
        if(!smartsInterface) {
            flash.message = "SmartsInterface not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(SmartsInterface, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsInterface.addRelation(relationMap);
                      if(smartsInterface.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsInterface:smartsInterface, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsInterface ${params.id} updated"
                          redirect(action:edit,id:smartsInterface.id)
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
        def smartsInterface = SmartsInterface.get( [id:params.id] )
        if(!smartsInterface) {
            flash.message = "SmartsInterface not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(SmartsInterface, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsInterface.removeRelation(relationMap);
                      if(smartsInterface.hasErrors()){
                          render(view:'edit',model:[smartsInterface:smartsInterface])
                      }
                      else{
                          flash.message = "SmartsInterface ${params.id} updated"
                          redirect(action:edit,id:smartsInterface.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsInterface.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsInterface.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SmartsInterface")
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