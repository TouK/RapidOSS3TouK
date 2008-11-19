import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class SmartsIpController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ smartsIpList: SmartsIp.list( params ) ]
    }

    def show = {
        def smartsIp = SmartsIp.get([id:params.id])

        if(!smartsIp) {
            flash.message = "SmartsIp not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsIp.class != SmartsIp)
            {
                def controllerName = smartsIp.class.simpleName;
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
                return [ smartsIp : smartsIp ]
            }
        }
    }

    def delete = {
        def smartsIp = SmartsIp.get( [id:params.id])
        if(smartsIp) {
            try{
                smartsIp.remove()
                flash.message = "SmartsIp ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [SmartsIp, smartsIp])
                flash.errors = this.errors;
                redirect(action:show, id:smartsIp.id)
            }

        }
        else {
            flash.message = "SmartsIp not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsIp = SmartsIp.get( [id:params.id] )

        if(!smartsIp) {
            flash.message = "SmartsIp not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsIp : smartsIp ]
        }
    }


    def update = {
        def smartsIp = SmartsIp.get( [id:params.id] )
        if(smartsIp) {
            smartsIp.update(ControllerUtils.getClassProperties(params, SmartsIp));
            if(!smartsIp.hasErrors()) {
                flash.message = "SmartsIp ${params.id} updated"
                redirect(action:show,id:smartsIp.id)
            }
            else {
                render(view:'edit',model:[smartsIp:smartsIp])
            }
        }
        else {
            flash.message = "SmartsIp not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsIp = new SmartsIp()
        smartsIp.properties = params
        return ['smartsIp':smartsIp]
    }

    def save = {
        def smartsIp = SmartsIp.add(ControllerUtils.getClassProperties(params, SmartsIp))
        if(!smartsIp.hasErrors()) {
            flash.message = "SmartsIp ${smartsIp.id} created"
            redirect(action:show,id:smartsIp.id)
        }
        else {
            render(view:'create',model:[smartsIp:smartsIp])
        }
    }

    def addTo = {
        def smartsIp = SmartsIp.get( [id:params.id] )
        if(!smartsIp){
            flash.message = "SmartsIp not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(SmartsIp, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsIp:smartsIp, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsIp.id)
            }
        }
    }



    def addRelation = {
        def smartsIp = SmartsIp.get( [id:params.id] )
        if(!smartsIp) {
            flash.message = "SmartsIp not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(SmartsIp, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsIp.addRelation(relationMap);
                      if(smartsIp.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsIp:smartsIp, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsIp ${params.id} updated"
                          redirect(action:edit,id:smartsIp.id)
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
        def smartsIp = SmartsIp.get( [id:params.id] )
        if(!smartsIp) {
            flash.message = "SmartsIp not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(SmartsIp, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsIp.removeRelation(relationMap);
                      if(smartsIp.hasErrors()){
                          render(view:'edit',model:[smartsIp:smartsIp])
                      }
                      else{
                          flash.message = "SmartsIp ${params.id} updated"
                          redirect(action:edit,id:smartsIp.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsIp.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsIp.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SmartsIp")
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