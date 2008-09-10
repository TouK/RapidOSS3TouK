import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsSmartsNotificationController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsSmartsNotificationList: RsSmartsNotification.list( params ) ]
    }

    def show = {
        def rsSmartsNotification = RsSmartsNotification.get([id:params.id])

        if(!rsSmartsNotification) {
            flash.message = "RsSmartsNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsSmartsNotification.class != RsSmartsNotification)
            {
                def controllerName = rsSmartsNotification.class.name;
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
                return [ rsSmartsNotification : rsSmartsNotification ]
            }
        }
    }

    def delete = {
        def rsSmartsNotification = RsSmartsNotification.get( [id:params.id])
        if(rsSmartsNotification) {
            try{
                rsSmartsNotification.remove()
                flash.message = "RsSmartsNotification ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsSmartsNotification, rsSmartsNotification])
                flash.errors = this.errors;
                redirect(action:show, id:rsSmartsNotification.id)
            }

        }
        else {
            flash.message = "RsSmartsNotification not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsSmartsNotification = RsSmartsNotification.get( [id:params.id] )

        if(!rsSmartsNotification) {
            flash.message = "RsSmartsNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsSmartsNotification : rsSmartsNotification ]
        }
    }


    def update = {
        def rsSmartsNotification = RsSmartsNotification.get( [id:params.id] )
        if(rsSmartsNotification) {
            rsSmartsNotification.update(ControllerUtils.getClassProperties(params, RsSmartsNotification));
            if(!rsSmartsNotification.hasErrors()) {
                flash.message = "RsSmartsNotification ${params.id} updated"
                redirect(action:show,id:rsSmartsNotification.id)
            }
            else {
                render(view:'edit',model:[rsSmartsNotification:rsSmartsNotification])
            }
        }
        else {
            flash.message = "RsSmartsNotification not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsSmartsNotification = new RsSmartsNotification()
        rsSmartsNotification.properties = params
        return ['rsSmartsNotification':rsSmartsNotification]
    }

    def save = {
        def rsSmartsNotification = RsSmartsNotification.add(ControllerUtils.getClassProperties(params, RsSmartsNotification))
        if(!rsSmartsNotification.hasErrors()) {
            flash.message = "RsSmartsNotification ${rsSmartsNotification.id} created"
            redirect(action:show,id:rsSmartsNotification.id)
        }
        else {
            render(view:'create',model:[rsSmartsNotification:rsSmartsNotification])
        }
    }

    def addTo = {
        def rsSmartsNotification = RsSmartsNotification.get( [id:params.id] )
        if(!rsSmartsNotification){
            flash.message = "RsSmartsNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsSmartsNotification, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsSmartsNotification:rsSmartsNotification, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsSmartsNotification.id)
            }
        }
    }



    def addRelation = {
        def rsSmartsNotification = RsSmartsNotification.get( [id:params.id] )
        if(!rsSmartsNotification) {
            flash.message = "RsSmartsNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsSmartsNotification, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsSmartsNotification.addRelation(relationMap);
                      if(rsSmartsNotification.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsSmartsNotification:rsSmartsNotification, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsSmartsNotification ${params.id} updated"
                          redirect(action:edit,id:rsSmartsNotification.id)
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
        def rsSmartsNotification = RsSmartsNotification.get( [id:params.id] )
        if(!rsSmartsNotification) {
            flash.message = "RsSmartsNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsSmartsNotification, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsSmartsNotification.removeRelation(relationMap);
                      if(rsSmartsNotification.hasErrors()){
                          render(view:'edit',model:[rsSmartsNotification:rsSmartsNotification])
                      }
                      else{
                          flash.message = "RsSmartsNotification ${params.id} updated"
                          redirect(action:edit,id:rsSmartsNotification.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsSmartsNotification.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsSmartsNotification.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsSmartsNotification")
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