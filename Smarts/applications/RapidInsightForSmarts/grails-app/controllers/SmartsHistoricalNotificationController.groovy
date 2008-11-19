import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class SmartsHistoricalNotificationController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ smartsHistoricalNotificationList: SmartsHistoricalNotification.list( params ) ]
    }

    def show = {
        def smartsHistoricalNotification = SmartsHistoricalNotification.get([id:params.id])

        if(!smartsHistoricalNotification) {
            flash.message = "SmartsHistoricalNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsHistoricalNotification.class != SmartsHistoricalNotification)
            {
                def controllerName = smartsHistoricalNotification.class.simpleName;
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
                return [ smartsHistoricalNotification : smartsHistoricalNotification ]
            }
        }
    }

    def delete = {
        def smartsHistoricalNotification = SmartsHistoricalNotification.get( [id:params.id])
        if(smartsHistoricalNotification) {
            try{
                smartsHistoricalNotification.remove()
                flash.message = "SmartsHistoricalNotification ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [SmartsHistoricalNotification, smartsHistoricalNotification])
                flash.errors = this.errors;
                redirect(action:show, id:smartsHistoricalNotification.id)
            }

        }
        else {
            flash.message = "SmartsHistoricalNotification not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsHistoricalNotification = SmartsHistoricalNotification.get( [id:params.id] )

        if(!smartsHistoricalNotification) {
            flash.message = "SmartsHistoricalNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsHistoricalNotification : smartsHistoricalNotification ]
        }
    }


    def update = {
        def smartsHistoricalNotification = SmartsHistoricalNotification.get( [id:params.id] )
        if(smartsHistoricalNotification) {
            smartsHistoricalNotification.update(ControllerUtils.getClassProperties(params, SmartsHistoricalNotification));
            if(!smartsHistoricalNotification.hasErrors()) {
                flash.message = "SmartsHistoricalNotification ${params.id} updated"
                redirect(action:show,id:smartsHistoricalNotification.id)
            }
            else {
                render(view:'edit',model:[smartsHistoricalNotification:smartsHistoricalNotification])
            }
        }
        else {
            flash.message = "SmartsHistoricalNotification not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsHistoricalNotification = new SmartsHistoricalNotification()
        smartsHistoricalNotification.properties = params
        return ['smartsHistoricalNotification':smartsHistoricalNotification]
    }

    def save = {
        def smartsHistoricalNotification = SmartsHistoricalNotification.add(ControllerUtils.getClassProperties(params, SmartsHistoricalNotification))
        if(!smartsHistoricalNotification.hasErrors()) {
            flash.message = "SmartsHistoricalNotification ${smartsHistoricalNotification.id} created"
            redirect(action:show,id:smartsHistoricalNotification.id)
        }
        else {
            render(view:'create',model:[smartsHistoricalNotification:smartsHistoricalNotification])
        }
    }

    def addTo = {
        def smartsHistoricalNotification = SmartsHistoricalNotification.get( [id:params.id] )
        if(!smartsHistoricalNotification){
            flash.message = "SmartsHistoricalNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(SmartsHistoricalNotification, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsHistoricalNotification:smartsHistoricalNotification, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsHistoricalNotification.id)
            }
        }
    }



    def addRelation = {
        def smartsHistoricalNotification = SmartsHistoricalNotification.get( [id:params.id] )
        if(!smartsHistoricalNotification) {
            flash.message = "SmartsHistoricalNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(SmartsHistoricalNotification, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsHistoricalNotification.addRelation(relationMap);
                      if(smartsHistoricalNotification.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsHistoricalNotification:smartsHistoricalNotification, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsHistoricalNotification ${params.id} updated"
                          redirect(action:edit,id:smartsHistoricalNotification.id)
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
        def smartsHistoricalNotification = SmartsHistoricalNotification.get( [id:params.id] )
        if(!smartsHistoricalNotification) {
            flash.message = "SmartsHistoricalNotification not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(SmartsHistoricalNotification, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsHistoricalNotification.removeRelation(relationMap);
                      if(smartsHistoricalNotification.hasErrors()){
                          render(view:'edit',model:[smartsHistoricalNotification:smartsHistoricalNotification])
                      }
                      else{
                          flash.message = "SmartsHistoricalNotification ${params.id} updated"
                          redirect(action:edit,id:smartsHistoricalNotification.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsHistoricalNotification.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsHistoricalNotification.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SmartsHistoricalNotification")
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