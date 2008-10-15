import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsEventController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsEventList: RsEvent.list( params ) ]
    }

    def show = {
        def rsEvent = RsEvent.get([id:params.id])

        if(!rsEvent) {
            flash.message = "RsEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsEvent.class != RsEvent)
            {
                def controllerName = rsEvent.class.simpleName;
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
                return [ rsEvent : rsEvent ]
            }
        }
    }

    def delete = {
        def rsEvent = RsEvent.get( [id:params.id])
        if(rsEvent) {
            try{
                rsEvent.remove()
                flash.message = "RsEvent ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsEvent, rsEvent])
                flash.errors = this.errors;
                redirect(action:show, id:rsEvent.id)
            }

        }
        else {
            flash.message = "RsEvent not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsEvent = RsEvent.get( [id:params.id] )

        if(!rsEvent) {
            flash.message = "RsEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsEvent : rsEvent ]
        }
    }


    def update = {
        def rsEvent = RsEvent.get( [id:params.id] )
        if(rsEvent) {
            rsEvent.update(ControllerUtils.getClassProperties(params, RsEvent));
            if(!rsEvent.hasErrors()) {
                flash.message = "RsEvent ${params.id} updated"
                redirect(action:show,id:rsEvent.id)
            }
            else {
                render(view:'edit',model:[rsEvent:rsEvent])
            }
        }
        else {
            flash.message = "RsEvent not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsEvent = new RsEvent()
        rsEvent.properties = params
        return ['rsEvent':rsEvent]
    }

    def save = {
        def rsEvent = RsEvent.add(ControllerUtils.getClassProperties(params, RsEvent))
        if(!rsEvent.hasErrors()) {
            flash.message = "RsEvent ${rsEvent.id} created"
            redirect(action:show,id:rsEvent.id)
        }
        else {
            render(view:'create',model:[rsEvent:rsEvent])
        }
    }

    def addTo = {
        def rsEvent = RsEvent.get( [id:params.id] )
        if(!rsEvent){
            flash.message = "RsEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsEvent, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsEvent:rsEvent, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsEvent.id)
            }
        }
    }



    def addRelation = {
        def rsEvent = RsEvent.get( [id:params.id] )
        if(!rsEvent) {
            flash.message = "RsEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsEvent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsEvent.addRelation(relationMap);
                      if(rsEvent.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsEvent:rsEvent, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsEvent ${params.id} updated"
                          redirect(action:edit,id:rsEvent.id)
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
        def rsEvent = RsEvent.get( [id:params.id] )
        if(!rsEvent) {
            flash.message = "RsEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsEvent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsEvent.removeRelation(relationMap);
                      if(rsEvent.hasErrors()){
                          render(view:'edit',model:[rsEvent:rsEvent])
                      }
                      else{
                          flash.message = "RsEvent ${params.id} updated"
                          redirect(action:edit,id:rsEvent.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsEvent.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsEvent.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsEvent")
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