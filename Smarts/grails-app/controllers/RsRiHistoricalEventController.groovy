import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsRiHistoricalEventController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsRiHistoricalEventList: RsRiHistoricalEvent.list( params ) ]
    }

    def show = {
        def rsRiHistoricalEvent = RsRiHistoricalEvent.get([id:params.id])

        if(!rsRiHistoricalEvent) {
            flash.message = "RsRiHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsRiHistoricalEvent.class != RsRiHistoricalEvent)
            {
                def controllerName = rsRiHistoricalEvent.class.simpleName;
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
                return [ rsRiHistoricalEvent : rsRiHistoricalEvent ]
            }
        }
    }

    def delete = {
        def rsRiHistoricalEvent = RsRiHistoricalEvent.get( [id:params.id])
        if(rsRiHistoricalEvent) {
            try{
                rsRiHistoricalEvent.remove()
                flash.message = "RsRiHistoricalEvent ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsRiHistoricalEvent, rsRiHistoricalEvent])
                flash.errors = this.errors;
                redirect(action:show, id:rsRiHistoricalEvent.id)
            }

        }
        else {
            flash.message = "RsRiHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsRiHistoricalEvent = RsRiHistoricalEvent.get( [id:params.id] )

        if(!rsRiHistoricalEvent) {
            flash.message = "RsRiHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsRiHistoricalEvent : rsRiHistoricalEvent ]
        }
    }


    def update = {
        def rsRiHistoricalEvent = RsRiHistoricalEvent.get( [id:params.id] )
        if(rsRiHistoricalEvent) {
            rsRiHistoricalEvent.update(ControllerUtils.getClassProperties(params, RsRiHistoricalEvent));
            if(!rsRiHistoricalEvent.hasErrors()) {
                flash.message = "RsRiHistoricalEvent ${params.id} updated"
                redirect(action:show,id:rsRiHistoricalEvent.id)
            }
            else {
                render(view:'edit',model:[rsRiHistoricalEvent:rsRiHistoricalEvent])
            }
        }
        else {
            flash.message = "RsRiHistoricalEvent not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsRiHistoricalEvent = new RsRiHistoricalEvent()
        rsRiHistoricalEvent.properties = params
        return ['rsRiHistoricalEvent':rsRiHistoricalEvent]
    }

    def save = {
        def rsRiHistoricalEvent = RsRiHistoricalEvent.add(ControllerUtils.getClassProperties(params, RsRiHistoricalEvent))
        if(!rsRiHistoricalEvent.hasErrors()) {
            flash.message = "RsRiHistoricalEvent ${rsRiHistoricalEvent.id} created"
            redirect(action:show,id:rsRiHistoricalEvent.id)
        }
        else {
            render(view:'create',model:[rsRiHistoricalEvent:rsRiHistoricalEvent])
        }
    }

    def addTo = {
        def rsRiHistoricalEvent = RsRiHistoricalEvent.get( [id:params.id] )
        if(!rsRiHistoricalEvent){
            flash.message = "RsRiHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsRiHistoricalEvent, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsRiHistoricalEvent:rsRiHistoricalEvent, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsRiHistoricalEvent.id)
            }
        }
    }



    def addRelation = {
        def rsRiHistoricalEvent = RsRiHistoricalEvent.get( [id:params.id] )
        if(!rsRiHistoricalEvent) {
            flash.message = "RsRiHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsRiHistoricalEvent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsRiHistoricalEvent.addRelation(relationMap);
                      if(rsRiHistoricalEvent.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsRiHistoricalEvent:rsRiHistoricalEvent, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsRiHistoricalEvent ${params.id} updated"
                          redirect(action:edit,id:rsRiHistoricalEvent.id)
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
        def rsRiHistoricalEvent = RsRiHistoricalEvent.get( [id:params.id] )
        if(!rsRiHistoricalEvent) {
            flash.message = "RsRiHistoricalEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsRiHistoricalEvent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsRiHistoricalEvent.removeRelation(relationMap);
                      if(rsRiHistoricalEvent.hasErrors()){
                          render(view:'edit',model:[rsRiHistoricalEvent:rsRiHistoricalEvent])
                      }
                      else{
                          flash.message = "RsRiHistoricalEvent ${params.id} updated"
                          redirect(action:edit,id:rsRiHistoricalEvent.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsRiHistoricalEvent.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsRiHistoricalEvent.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsRiHistoricalEvent")
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