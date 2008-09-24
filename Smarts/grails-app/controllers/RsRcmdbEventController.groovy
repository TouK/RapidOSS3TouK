import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsRcmdbEventController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsRcmdbEventList: RsRcmdbEvent.list( params ) ]
    }

    def show = {
        def rsRcmdbEvent = RsRcmdbEvent.get([id:params.id])

        if(!rsRcmdbEvent) {
            flash.message = "RsRcmdbEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsRcmdbEvent.class != RsRcmdbEvent)
            {
                def controllerName = rsRcmdbEvent.class.name;
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
                return [ rsRcmdbEvent : rsRcmdbEvent ]
            }
        }
    }

    def delete = {
        def rsRcmdbEvent = RsRcmdbEvent.get( [id:params.id])
        if(rsRcmdbEvent) {
            try{
                rsRcmdbEvent.remove()
                flash.message = "RsRcmdbEvent ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsRcmdbEvent, rsRcmdbEvent])
                flash.errors = this.errors;
                redirect(action:show, id:rsRcmdbEvent.id)
            }

        }
        else {
            flash.message = "RsRcmdbEvent not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsRcmdbEvent = RsRcmdbEvent.get( [id:params.id] )

        if(!rsRcmdbEvent) {
            flash.message = "RsRcmdbEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsRcmdbEvent : rsRcmdbEvent ]
        }
    }


    def update = {
        def rsRcmdbEvent = RsRcmdbEvent.get( [id:params.id] )
        if(rsRcmdbEvent) {
            rsRcmdbEvent.update(ControllerUtils.getClassProperties(params, RsRcmdbEvent));
            if(!rsRcmdbEvent.hasErrors()) {
                flash.message = "RsRcmdbEvent ${params.id} updated"
                redirect(action:show,id:rsRcmdbEvent.id)
            }
            else {
                render(view:'edit',model:[rsRcmdbEvent:rsRcmdbEvent])
            }
        }
        else {
            flash.message = "RsRcmdbEvent not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsRcmdbEvent = new RsRcmdbEvent()
        rsRcmdbEvent.properties = params
        return ['rsRcmdbEvent':rsRcmdbEvent]
    }

    def save = {
        def rsRcmdbEvent = RsRcmdbEvent.add(ControllerUtils.getClassProperties(params, RsRcmdbEvent))
        if(!rsRcmdbEvent.hasErrors()) {
            flash.message = "RsRcmdbEvent ${rsRcmdbEvent.id} created"
            redirect(action:show,id:rsRcmdbEvent.id)
        }
        else {
            render(view:'create',model:[rsRcmdbEvent:rsRcmdbEvent])
        }
    }

    def addTo = {
        def rsRcmdbEvent = RsRcmdbEvent.get( [id:params.id] )
        if(!rsRcmdbEvent){
            flash.message = "RsRcmdbEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsRcmdbEvent, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsRcmdbEvent:rsRcmdbEvent, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsRcmdbEvent.id)
            }
        }
    }



    def addRelation = {
        def rsRcmdbEvent = RsRcmdbEvent.get( [id:params.id] )
        if(!rsRcmdbEvent) {
            flash.message = "RsRcmdbEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsRcmdbEvent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsRcmdbEvent.addRelation(relationMap);
                      if(rsRcmdbEvent.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsRcmdbEvent:rsRcmdbEvent, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsRcmdbEvent ${params.id} updated"
                          redirect(action:edit,id:rsRcmdbEvent.id)
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
        def rsRcmdbEvent = RsRcmdbEvent.get( [id:params.id] )
        if(!rsRcmdbEvent) {
            flash.message = "RsRcmdbEvent not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsRcmdbEvent, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsRcmdbEvent.removeRelation(relationMap);
                      if(rsRcmdbEvent.hasErrors()){
                          render(view:'edit',model:[rsRcmdbEvent:rsRcmdbEvent])
                      }
                      else{
                          flash.message = "RsRcmdbEvent ${params.id} updated"
                          redirect(action:edit,id:rsRcmdbEvent.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsRcmdbEvent.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsRcmdbEvent.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsRcmdbEvent")
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