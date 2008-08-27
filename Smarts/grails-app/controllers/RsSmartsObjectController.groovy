import com.ifountain.rcmdb.domain.util.ControllerUtils;


class RsSmartsObjectController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsSmartsObjectList: RsSmartsObject.list( params ) ]
    }

    def show = {
        def rsSmartsObject = RsSmartsObject.get([id:params.id])

        if(!rsSmartsObject) {
            flash.message = "RsSmartsObject not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsSmartsObject.class != RsSmartsObject)
            {
                def controllerName = rsSmartsObject.class.name;
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
                return [ rsSmartsObject : rsSmartsObject ]
            }
        }
    }

    def delete = {
        def rsSmartsObject = RsSmartsObject.get( [id:params.id])
        if(rsSmartsObject) {
            try{
                rsSmartsObject.remove()
                flash.message = "RsSmartsObject ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsSmartsObject, rsSmartsObject])
                flash.errors = this.errors;
                redirect(action:show, id:rsSmartsObject.id)
            }

        }
        else {
            flash.message = "RsSmartsObject not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsSmartsObject = RsSmartsObject.get( [id:params.id] )

        if(!rsSmartsObject) {
            flash.message = "RsSmartsObject not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsSmartsObject : rsSmartsObject ]
        }
    }


    def update = {
        def rsSmartsObject = RsSmartsObject.get( [id:params.id] )
        if(rsSmartsObject) {
            rsSmartsObject.update(ControllerUtils.getClassProperties(params, RsSmartsObject));
            if(!rsSmartsObject.hasErrors()) {
                flash.message = "RsSmartsObject ${params.id} updated"
                redirect(action:show,id:rsSmartsObject.id)
            }
            else {
                render(view:'edit',model:[rsSmartsObject:rsSmartsObject])
            }
        }
        else {
            flash.message = "RsSmartsObject not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsSmartsObject = new RsSmartsObject()
        rsSmartsObject.properties = params
        return ['rsSmartsObject':rsSmartsObject]
    }

    def save = {
        def rsSmartsObject = RsSmartsObject.add(ControllerUtils.getClassProperties(params, RsSmartsObject))
        if(!rsSmartsObject.hasErrors()) {
            flash.message = "RsSmartsObject ${rsSmartsObject.id} created"
            redirect(action:show,id:rsSmartsObject.id)
        }
        else {
            render(view:'create',model:[rsSmartsObject:rsSmartsObject])
        }
    }

    def addTo = {
        def rsSmartsObject = RsSmartsObject.get( [id:params.id] )
        if(!rsSmartsObject){
            flash.message = "RsSmartsObject not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = rsSmartsObject.hasMany[relationName];
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsSmartsObject:rsSmartsObject, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsSmartsObject.id)
            }
        }
    }

    def addRelation = {
        def rsSmartsObject = RsSmartsObject.get( [id:params.id] )
        if(!rsSmartsObject) {
            flash.message = "RsSmartsObject not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = rsSmartsObject.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsSmartsObject.addRelation(relationMap);
                      if(rsSmartsObject.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsSmartsObject:rsSmartsObject, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsSmartsObject ${params.id} updated"
                          redirect(action:edit,id:rsSmartsObject.id)
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
        def rsSmartsObject = RsSmartsObject.get( [id:params.id] )
        if(!rsSmartsObject) {
            flash.message = "RsSmartsObject not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = rsSmartsObject.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsSmartsObject.removeRelation(relationMap);
                      if(rsSmartsObject.hasErrors()){
                          render(view:'edit',model:[rsSmartsObject:rsSmartsObject])
                      }
                      else{
                          flash.message = "RsSmartsObject ${params.id} updated"
                          redirect(action:edit,id:rsSmartsObject.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsSmartsObject.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsSmartsObject.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsSmartsObject")
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