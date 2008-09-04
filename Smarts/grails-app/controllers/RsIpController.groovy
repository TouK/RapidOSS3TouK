import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsIpController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsIpList: RsIp.list( params ) ]
    }

    def show = {
        def rsIp = RsIp.get([id:params.id])

        if(!rsIp) {
            flash.message = "RsIp not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsIp.class != RsIp)
            {
                def controllerName = rsIp.class.name;
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
                return [ rsIp : rsIp ]
            }
        }
    }

    def delete = {
        def rsIp = RsIp.get( [id:params.id])
        if(rsIp) {
            try{
                rsIp.remove()
                flash.message = "RsIp ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsIp, rsIp])
                flash.errors = this.errors;
                redirect(action:show, id:rsIp.id)
            }

        }
        else {
            flash.message = "RsIp not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsIp = RsIp.get( [id:params.id] )

        if(!rsIp) {
            flash.message = "RsIp not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsIp : rsIp ]
        }
    }


    def update = {
        def rsIp = RsIp.get( [id:params.id] )
        if(rsIp) {
            rsIp.update(ControllerUtils.getClassProperties(params, RsIp));
            if(!rsIp.hasErrors()) {
                flash.message = "RsIp ${params.id} updated"
                redirect(action:show,id:rsIp.id)
            }
            else {
                render(view:'edit',model:[rsIp:rsIp])
            }
        }
        else {
            flash.message = "RsIp not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsIp = new RsIp()
        rsIp.properties = params
        return ['rsIp':rsIp]
    }

    def save = {
        def rsIp = RsIp.add(ControllerUtils.getClassProperties(params, RsIp))
        if(!rsIp.hasErrors()) {
            flash.message = "RsIp ${rsIp.id} created"
            redirect(action:show,id:rsIp.id)
        }
        else {
            render(view:'create',model:[rsIp:rsIp])
        }
    }

    def addTo = {
        def rsIp = RsIp.get( [id:params.id] )
        if(!rsIp){
            flash.message = "RsIp not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsIp, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsIp:rsIp, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsIp.id)
            }
        }
    }



    def addRelation = {
        def rsIp = RsIp.get( [id:params.id] )
        if(!rsIp) {
            flash.message = "RsIp not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsIp, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsIp.addRelation(relationMap);
                      if(rsIp.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsIp:rsIp, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsIp ${params.id} updated"
                          redirect(action:edit,id:rsIp.id)
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
        def rsIp = RsIp.get( [id:params.id] )
        if(!rsIp) {
            flash.message = "RsIp not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsIp, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsIp.removeRelation(relationMap);
                      if(rsIp.hasErrors()){
                          render(view:'edit',model:[rsIp:rsIp])
                      }
                      else{
                          flash.message = "RsIp ${params.id} updated"
                          redirect(action:edit,id:rsIp.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsIp.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsIp.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsIp")
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