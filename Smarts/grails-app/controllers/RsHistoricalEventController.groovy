import com.ifountain.rcmdb.domain.util.ControllerUtils;


class RsHistoricalEventController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsHistoricalNotificationList: RsHistoricalEvent.list( params ) ]
    }

    def show = {
        def rsHistoricalNotification = RsHistoricalEvent.get([id:params.id])

        if(!rsHistoricalNotification) {
            flash.message = "RsHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsHistoricalNotification.class != RsHistoricalEvent)
            {
                def controllerName = rsHistoricalNotification.class.name;
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
                return [ rsHistoricalNotification : rsHistoricalNotification ]
            }
        }
    }

    def delete = {
        def rsHistoricalNotification = RsHistoricalEvent.get( [id:params.id])
        if(rsHistoricalNotification) {
            try{
                rsHistoricalNotification.remove()
                flash.message = "RsHistoricalEvent ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsHistoricalEvent, rsHistoricalNotification])
                flash.errors = this.errors;
                redirect(action:show, id:rsHistoricalNotification.id)
            }

        }
        else {
            flash.message = "RsHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsHistoricalNotification = RsHistoricalEvent.get( [id:params.id] )

        if(!rsHistoricalNotification) {
            flash.message = "RsHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsHistoricalNotification : rsHistoricalNotification ]
        }
    }


    def update = {
        def rsHistoricalNotification = RsHistoricalEvent.get( [id:params.id] )
        if(rsHistoricalNotification) {
            rsHistoricalNotification.update(ControllerUtils.getClassProperties(params, RsHistoricalEvent));
            if(!rsHistoricalNotification.hasErrors()) {
                flash.message = "RsHistoricalEvent ${params.id} updated"
                redirect(action:show,id:rsHistoricalNotification.id)
            }
            else {
                render(view:'edit',model:[rsHistoricalNotification:rsHistoricalNotification])
            }
        }
        else {
            flash.message = "RsHistoricalEvent not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsHistoricalNotification = new RsHistoricalEvent()
        rsHistoricalNotification.properties = params
        return ['rsHistoricalNotification':rsHistoricalNotification]
    }

    def save = {
        def rsHistoricalNotification = RsHistoricalEvent.add(ControllerUtils.getClassProperties(params, RsHistoricalEvent))
        if(!rsHistoricalNotification.hasErrors()) {
            flash.message = "RsHistoricalEvent ${rsHistoricalNotification.id} created"
            redirect(action:show,id:rsHistoricalNotification.id)
        }
        else {
            render(view:'create',model:[rsHistoricalNotification:rsHistoricalNotification])
        }
    }

    def addTo = {
        def rsHistoricalNotification = RsHistoricalEvent.get( [id:params.id] )
        if(!rsHistoricalNotification){
            flash.message = "RsHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = rsHistoricalNotification.hasMany[relationName];
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsHistoricalNotification:rsHistoricalNotification, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsHistoricalNotification.id)
            }
        }
    }

    def addRelation = {
        def rsHistoricalNotification = RsHistoricalEvent.get( [id:params.id] )
        if(!rsHistoricalNotification) {
            flash.message = "RsHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = rsHistoricalNotification.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsHistoricalNotification.addRelation(relationMap);
                      if(rsHistoricalNotification.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsHistoricalNotification:rsHistoricalNotification, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsHistoricalEvent ${params.id} updated"
                          redirect(action:edit,id:rsHistoricalNotification.id)
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
        def rsHistoricalNotification = RsHistoricalEvent.get( [id:params.id] )
        if(!rsHistoricalNotification) {
            flash.message = "RsHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = rsHistoricalNotification.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsHistoricalNotification.removeRelation(relationMap);
                      if(rsHistoricalNotification.hasErrors()){
                          render(view:'edit',model:[rsHistoricalNotification:rsHistoricalNotification])
                      }
                      else{
                          flash.message = "RsHistoricalEvent ${params.id} updated"
                          redirect(action:edit,id:rsHistoricalNotification.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsHistoricalNotification.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsHistoricalNotification.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsHistoricalEvent")
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