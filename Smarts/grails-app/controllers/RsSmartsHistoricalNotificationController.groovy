import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsSmartsHistoricalNotificationController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsSmartsHistoricalNotificationList: RsSmartsHistoricalNotification.list( params ) ]
    }

    def show = {
        def rsSmartsHistoricalNotification = RsSmartsHistoricalNotification.get([id:params.id])

        if(!rsSmartsHistoricalNotification) {
            flash.message = "RsSmartsHistoricalNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsSmartsHistoricalNotification.class != RsSmartsHistoricalNotification)
            {
                def controllerName = rsSmartsHistoricalNotification.class.name;
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
                return [ rsSmartsHistoricalNotification : rsSmartsHistoricalNotification ]
            }
        }
    }

    def delete = {
        def rsSmartsHistoricalNotification = RsSmartsHistoricalNotification.get( [id:params.id])
        if(rsSmartsHistoricalNotification) {
            try{
                rsSmartsHistoricalNotification.remove()
                flash.message = "RsSmartsHistoricalNotification ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsSmartsHistoricalNotification, rsSmartsHistoricalNotification])
                flash.errors = this.errors;
                redirect(action:show, id:rsSmartsHistoricalNotification.id)
            }

        }
        else {
            flash.message = "RsSmartsHistoricalNotification not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsSmartsHistoricalNotification = RsSmartsHistoricalNotification.get( [id:params.id] )

        if(!rsSmartsHistoricalNotification) {
            flash.message = "RsSmartsHistoricalNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsSmartsHistoricalNotification : rsSmartsHistoricalNotification ]
        }
    }


    def update = {
        def rsSmartsHistoricalNotification = RsSmartsHistoricalNotification.get( [id:params.id] )
        if(rsSmartsHistoricalNotification) {
            rsSmartsHistoricalNotification.update(ControllerUtils.getClassProperties(params, RsSmartsHistoricalNotification));
            if(!rsSmartsHistoricalNotification.hasErrors()) {
                flash.message = "RsSmartsHistoricalNotification ${params.id} updated"
                redirect(action:show,id:rsSmartsHistoricalNotification.id)
            }
            else {
                render(view:'edit',model:[rsSmartsHistoricalNotification:rsSmartsHistoricalNotification])
            }
        }
        else {
            flash.message = "RsSmartsHistoricalNotification not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsSmartsHistoricalNotification = new RsSmartsHistoricalNotification()
        rsSmartsHistoricalNotification.properties = params
        return ['rsSmartsHistoricalNotification':rsSmartsHistoricalNotification]
    }

    def save = {
        def rsSmartsHistoricalNotification = RsSmartsHistoricalNotification.add(ControllerUtils.getClassProperties(params, RsSmartsHistoricalNotification))
        if(!rsSmartsHistoricalNotification.hasErrors()) {
            flash.message = "RsSmartsHistoricalNotification ${rsSmartsHistoricalNotification.id} created"
            redirect(action:show,id:rsSmartsHistoricalNotification.id)
        }
        else {
            render(view:'create',model:[rsSmartsHistoricalNotification:rsSmartsHistoricalNotification])
        }
    }

    def addTo = {
        def rsSmartsHistoricalNotification = RsSmartsHistoricalNotification.get( [id:params.id] )
        if(!rsSmartsHistoricalNotification){
            flash.message = "RsSmartsHistoricalNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsSmartsHistoricalNotification, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsSmartsHistoricalNotification:rsSmartsHistoricalNotification, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsSmartsHistoricalNotification.id)
            }
        }
    }



    def addRelation = {
        def rsSmartsHistoricalNotification = RsSmartsHistoricalNotification.get( [id:params.id] )
        if(!rsSmartsHistoricalNotification) {
            flash.message = "RsSmartsHistoricalNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsSmartsHistoricalNotification, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsSmartsHistoricalNotification.addRelation(relationMap);
                      if(rsSmartsHistoricalNotification.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsSmartsHistoricalNotification:rsSmartsHistoricalNotification, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsSmartsHistoricalNotification ${params.id} updated"
                          redirect(action:edit,id:rsSmartsHistoricalNotification.id)
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
        def rsSmartsHistoricalNotification = RsSmartsHistoricalNotification.get( [id:params.id] )
        if(!rsSmartsHistoricalNotification) {
            flash.message = "RsSmartsHistoricalNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsSmartsHistoricalNotification, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsSmartsHistoricalNotification.removeRelation(relationMap);
                      if(rsSmartsHistoricalNotification.hasErrors()){
                          render(view:'edit',model:[rsSmartsHistoricalNotification:rsSmartsHistoricalNotification])
                      }
                      else{
                          flash.message = "RsSmartsHistoricalNotification ${params.id} updated"
                          redirect(action:edit,id:rsSmartsHistoricalNotification.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsSmartsHistoricalNotification.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsSmartsHistoricalNotification.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsSmartsHistoricalNotification")
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