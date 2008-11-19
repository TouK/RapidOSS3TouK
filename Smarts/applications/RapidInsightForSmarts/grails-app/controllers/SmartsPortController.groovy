import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class SmartsPortController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ smartsPortList: SmartsPort.list( params ) ]
    }

    def show = {
        def smartsPort = SmartsPort.get([id:params.id])

        if(!smartsPort) {
            flash.message = "SmartsPort not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsPort.class != SmartsPort)
            {
                def controllerName = smartsPort.class.simpleName;
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
                return [ smartsPort : smartsPort ]
            }
        }
    }

    def delete = {
        def smartsPort = SmartsPort.get( [id:params.id])
        if(smartsPort) {
            try{
                smartsPort.remove()
                flash.message = "SmartsPort ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [SmartsPort, smartsPort])
                flash.errors = this.errors;
                redirect(action:show, id:smartsPort.id)
            }

        }
        else {
            flash.message = "SmartsPort not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsPort = SmartsPort.get( [id:params.id] )

        if(!smartsPort) {
            flash.message = "SmartsPort not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsPort : smartsPort ]
        }
    }


    def update = {
        def smartsPort = SmartsPort.get( [id:params.id] )
        if(smartsPort) {
            smartsPort.update(ControllerUtils.getClassProperties(params, SmartsPort));
            if(!smartsPort.hasErrors()) {
                flash.message = "SmartsPort ${params.id} updated"
                redirect(action:show,id:smartsPort.id)
            }
            else {
                render(view:'edit',model:[smartsPort:smartsPort])
            }
        }
        else {
            flash.message = "SmartsPort not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsPort = new SmartsPort()
        smartsPort.properties = params
        return ['smartsPort':smartsPort]
    }

    def save = {
        def smartsPort = SmartsPort.add(ControllerUtils.getClassProperties(params, SmartsPort))
        if(!smartsPort.hasErrors()) {
            flash.message = "SmartsPort ${smartsPort.id} created"
            redirect(action:show,id:smartsPort.id)
        }
        else {
            render(view:'create',model:[smartsPort:smartsPort])
        }
    }

    def addTo = {
        def smartsPort = SmartsPort.get( [id:params.id] )
        if(!smartsPort){
            flash.message = "SmartsPort not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(SmartsPort, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsPort:smartsPort, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsPort.id)
            }
        }
    }



    def addRelation = {
        def smartsPort = SmartsPort.get( [id:params.id] )
        if(!smartsPort) {
            flash.message = "SmartsPort not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(SmartsPort, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsPort.addRelation(relationMap);
                      if(smartsPort.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsPort:smartsPort, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsPort ${params.id} updated"
                          redirect(action:edit,id:smartsPort.id)
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
        def smartsPort = SmartsPort.get( [id:params.id] )
        if(!smartsPort) {
            flash.message = "SmartsPort not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(SmartsPort, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsPort.removeRelation(relationMap);
                      if(smartsPort.hasErrors()){
                          render(view:'edit',model:[smartsPort:smartsPort])
                      }
                      else{
                          flash.message = "SmartsPort ${params.id} updated"
                          redirect(action:edit,id:smartsPort.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsPort.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsPort.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SmartsPort")
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