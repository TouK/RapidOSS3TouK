import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class SmartsNotificationController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ smartsNotificationList: SmartsNotification.list( params ) ]
    }

    def show = {
        def smartsNotification = SmartsNotification.get([id:params.id])

        if(!smartsNotification) {
            flash.message = "SmartsNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsNotification.class != SmartsNotification)
            {
                def controllerName = smartsNotification.class.simpleName;
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
                return [ smartsNotification : smartsNotification ]
            }
        }
    }

    def delete = {
        def smartsNotification = SmartsNotification.get( [id:params.id])
        if(smartsNotification) {
            try{
                smartsNotification.remove()
                flash.message = "SmartsNotification ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [SmartsNotification, smartsNotification])
                flash.errors = this.errors;
                redirect(action:show, id:smartsNotification.id)
            }

        }
        else {
            flash.message = "SmartsNotification not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsNotification = SmartsNotification.get( [id:params.id] )

        if(!smartsNotification) {
            flash.message = "SmartsNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsNotification : smartsNotification ]
        }
    }


    def update = {
        def smartsNotification = SmartsNotification.get( [id:params.id] )
        if(smartsNotification) {
            smartsNotification.update(ControllerUtils.getClassProperties(params, SmartsNotification));
            if(!smartsNotification.hasErrors()) {
                flash.message = "SmartsNotification ${params.id} updated"
                redirect(action:show,id:smartsNotification.id)
            }
            else {
                render(view:'edit',model:[smartsNotification:smartsNotification])
            }
        }
        else {
            flash.message = "SmartsNotification not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsNotification = new SmartsNotification()
        smartsNotification.properties = params
        return ['smartsNotification':smartsNotification]
    }

    def save = {
        def smartsNotification = SmartsNotification.add(ControllerUtils.getClassProperties(params, SmartsNotification))
        if(!smartsNotification.hasErrors()) {
            flash.message = "SmartsNotification ${smartsNotification.id} created"
            redirect(action:show,id:smartsNotification.id)
        }
        else {
            render(view:'create',model:[smartsNotification:smartsNotification])
        }
    }

    def addTo = {
        def smartsNotification = SmartsNotification.get( [id:params.id] )
        if(!smartsNotification){
            flash.message = "SmartsNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(SmartsNotification, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsNotification:smartsNotification, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsNotification.id)
            }
        }
    }



    def addRelation = {
        def smartsNotification = SmartsNotification.get( [id:params.id] )
        if(!smartsNotification) {
            flash.message = "SmartsNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(SmartsNotification, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsNotification.addRelation(relationMap);
                      if(smartsNotification.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsNotification:smartsNotification, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsNotification ${params.id} updated"
                          redirect(action:edit,id:smartsNotification.id)
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
        def smartsNotification = SmartsNotification.get( [id:params.id] )
        if(!smartsNotification) {
            flash.message = "SmartsNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(SmartsNotification, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsNotification.removeRelation(relationMap);
                      if(smartsNotification.hasErrors()){
                          render(view:'edit',model:[smartsNotification:smartsNotification])
                      }
                      else{
                          flash.message = "SmartsNotification ${params.id} updated"
                          redirect(action:edit,id:smartsNotification.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsNotification.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsNotification.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SmartsNotification")
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