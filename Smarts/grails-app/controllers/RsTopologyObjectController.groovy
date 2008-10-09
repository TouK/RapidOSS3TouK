import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsTopologyObjectController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsTopologyObjectList: RsTopologyObject.list( params ) ]
    }

    def show = {
        def rsTopologyObject = RsTopologyObject.get([id:params.id])

        if(!rsTopologyObject) {
            flash.message = "RsTopologyObject not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsTopologyObject.class != RsTopologyObject)
            {
                def controllerName = rsTopologyObject.class.name;
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
                return [ rsTopologyObject : rsTopologyObject ]
            }
        }
    }

    def delete = {
        def rsTopologyObject = RsTopologyObject.get( [id:params.id])
        if(rsTopologyObject) {
            try{
                rsTopologyObject.remove()
                flash.message = "RsTopologyObject ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsTopologyObject, rsTopologyObject])
                flash.errors = this.errors;
                redirect(action:show, id:rsTopologyObject.id)
            }

        }
        else {
            flash.message = "RsTopologyObject not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsTopologyObject = RsTopologyObject.get( [id:params.id] )

        if(!rsTopologyObject) {
            flash.message = "RsTopologyObject not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsTopologyObject : rsTopologyObject ]
        }
    }


    def update = {
        def rsTopologyObject = RsTopologyObject.get( [id:params.id] )
        if(rsTopologyObject) {
            rsTopologyObject.update(ControllerUtils.getClassProperties(params, RsTopologyObject));
            if(!rsTopologyObject.hasErrors()) {
                flash.message = "RsTopologyObject ${params.id} updated"
                redirect(action:show,id:rsTopologyObject.id)
            }
            else {
                render(view:'edit',model:[rsTopologyObject:rsTopologyObject])
            }
        }
        else {
            flash.message = "RsTopologyObject not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsTopologyObject = new RsTopologyObject()
        rsTopologyObject.properties = params
        return ['rsTopologyObject':rsTopologyObject]
    }

    def save = {
        def rsTopologyObject = RsTopologyObject.add(ControllerUtils.getClassProperties(params, RsTopologyObject))
        if(!rsTopologyObject.hasErrors()) {
            flash.message = "RsTopologyObject ${rsTopologyObject.id} created"
            redirect(action:show,id:rsTopologyObject.id)
        }
        else {
            render(view:'create',model:[rsTopologyObject:rsTopologyObject])
        }
    }

    def addTo = {
        def rsTopologyObject = RsTopologyObject.get( [id:params.id] )
        if(!rsTopologyObject){
            flash.message = "RsTopologyObject not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsTopologyObject, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsTopologyObject:rsTopologyObject, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsTopologyObject.id)
            }
        }
    }



    def addRelation = {
        def rsTopologyObject = RsTopologyObject.get( [id:params.id] )
        if(!rsTopologyObject) {
            flash.message = "RsTopologyObject not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsTopologyObject, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsTopologyObject.addRelation(relationMap);
                      if(rsTopologyObject.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsTopologyObject:rsTopologyObject, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsTopologyObject ${params.id} updated"
                          redirect(action:edit,id:rsTopologyObject.id)
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
        def rsTopologyObject = RsTopologyObject.get( [id:params.id] )
        if(!rsTopologyObject) {
            flash.message = "RsTopologyObject not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsTopologyObject, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsTopologyObject.removeRelation(relationMap);
                      if(rsTopologyObject.hasErrors()){
                          render(view:'edit',model:[rsTopologyObject:rsTopologyObject])
                      }
                      else{
                          flash.message = "RsTopologyObject ${params.id} updated"
                          redirect(action:edit,id:rsTopologyObject.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsTopologyObject.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsTopologyObject.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsTopologyObject")
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