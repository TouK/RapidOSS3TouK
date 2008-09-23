import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsAbstractEventController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsAbstractEventList: RsAbstractEvent.list( params ) ]
    }

    def show = {
        def rsAbstractEvent = RsAbstractEvent.get([id:params.id])

        if(!rsAbstractEvent) {
            flash.message = "RsAbstractEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsAbstractEvent.class != RsAbstractEvent)
            {
                def controllerName = rsAbstractEvent.class.name;
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
                return [ rsAbstractEvent : rsAbstractEvent ]
            }
        }
    }

    def delete = {
        def rsAbstractEvent = RsAbstractEvent.get( [id:params.id])
        if(rsAbstractEvent) {
            try{
                rsAbstractEvent.remove()
                flash.message = "RsAbstractEvent ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsAbstractEvent, rsAbstractEvent])
                flash.errors = this.errors;
                redirect(action:show, id:rsAbstractEvent.id)
            }

        }
        else {
            flash.message = "RsAbstractEvent not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsAbstractEvent = RsAbstractEvent.get( [id:params.id] )

        if(!rsAbstractEvent) {
            flash.message = "RsAbstractEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsAbstractEvent : rsAbstractEvent ]
        }
    }


    def update = {
        def rsAbstractEvent = RsAbstractEvent.get( [id:params.id] )
        if(rsAbstractEvent) {
            rsAbstractEvent.update(ControllerUtils.getClassProperties(params, RsAbstractEvent));
            if(!rsAbstractEvent.hasErrors()) {
                flash.message = "RsAbstractEvent ${params.id} updated"
                redirect(action:show,id:rsAbstractEvent.id)
            }
            else {
                render(view:'edit',model:[rsAbstractEvent:rsAbstractEvent])
            }
        }
        else {
            flash.message = "RsAbstractEvent not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsAbstractEvent = new RsAbstractEvent()
        rsAbstractEvent.properties = params
        return ['rsAbstractEvent':rsAbstractEvent]
    }

    def save = {
        def rsAbstractEvent = RsAbstractEvent.add(ControllerUtils.getClassProperties(params, RsAbstractEvent))
        if(!rsAbstractEvent.hasErrors()) {
            flash.message = "RsAbstractEvent ${rsAbstractEvent.id} created"
            redirect(action:show,id:rsAbstractEvent.id)
        }
        else {
            render(view:'create',model:[rsAbstractEvent:rsAbstractEvent])
        }
    }

    def addTo = {
        def rsAbstractEvent = RsAbstractEvent.get( [id:params.id] )
        if(!rsAbstractEvent){
            flash.message = "RsAbstractEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsAbstractEvent, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsAbstractEvent:rsAbstractEvent, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsAbstractEvent.id)
            }
        }
    }



    def addRelation = {
        def rsAbstractEvent = RsAbstractEvent.get( [id:params.id] )
        if(!rsAbstractEvent) {
            flash.message = "RsAbstractEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsAbstractEvent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsAbstractEvent.addRelation(relationMap);
                      if(rsAbstractEvent.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsAbstractEvent:rsAbstractEvent, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsAbstractEvent ${params.id} updated"
                          redirect(action:edit,id:rsAbstractEvent.id)
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
        def rsAbstractEvent = RsAbstractEvent.get( [id:params.id] )
        if(!rsAbstractEvent) {
            flash.message = "RsAbstractEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsAbstractEvent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsAbstractEvent.removeRelation(relationMap);
                      if(rsAbstractEvent.hasErrors()){
                          render(view:'edit',model:[rsAbstractEvent:rsAbstractEvent])
                      }
                      else{
                          flash.message = "RsAbstractEvent ${params.id} updated"
                          redirect(action:edit,id:rsAbstractEvent.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsAbstractEvent.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsAbstractEvent.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsAbstractEvent")
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