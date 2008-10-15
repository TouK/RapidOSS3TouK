import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsGroupController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsGroupList: RsGroup.list( params ) ]
    }

    def show = {
        def rsGroup = RsGroup.get([id:params.id])

        if(!rsGroup) {
            flash.message = "RsGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsGroup.class != RsGroup)
            {
                def controllerName = rsGroup.class.simpleName;
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
                return [ rsGroup : rsGroup ]
            }
        }
    }

    def delete = {
        def rsGroup = RsGroup.get( [id:params.id])
        if(rsGroup) {
            try{
                rsGroup.remove()
                flash.message = "RsGroup ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsGroup, rsGroup])
                flash.errors = this.errors;
                redirect(action:show, id:rsGroup.id)
            }

        }
        else {
            flash.message = "RsGroup not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsGroup = RsGroup.get( [id:params.id] )

        if(!rsGroup) {
            flash.message = "RsGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsGroup : rsGroup ]
        }
    }


    def update = {
        def rsGroup = RsGroup.get( [id:params.id] )
        if(rsGroup) {
            rsGroup.update(ControllerUtils.getClassProperties(params, RsGroup));
            if(!rsGroup.hasErrors()) {
                flash.message = "RsGroup ${params.id} updated"
                redirect(action:show,id:rsGroup.id)
            }
            else {
                render(view:'edit',model:[rsGroup:rsGroup])
            }
        }
        else {
            flash.message = "RsGroup not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsGroup = new RsGroup()
        rsGroup.properties = params
        return ['rsGroup':rsGroup]
    }

    def save = {
        def rsGroup = RsGroup.add(ControllerUtils.getClassProperties(params, RsGroup))
        if(!rsGroup.hasErrors()) {
            flash.message = "RsGroup ${rsGroup.id} created"
            redirect(action:show,id:rsGroup.id)
        }
        else {
            render(view:'create',model:[rsGroup:rsGroup])
        }
    }

    def addTo = {
        def rsGroup = RsGroup.get( [id:params.id] )
        if(!rsGroup){
            flash.message = "RsGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsGroup, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsGroup:rsGroup, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsGroup.id)
            }
        }
    }



    def addRelation = {
        def rsGroup = RsGroup.get( [id:params.id] )
        if(!rsGroup) {
            flash.message = "RsGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsGroup, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsGroup.addRelation(relationMap);
                      if(rsGroup.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsGroup:rsGroup, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsGroup ${params.id} updated"
                          redirect(action:edit,id:rsGroup.id)
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
        def rsGroup = RsGroup.get( [id:params.id] )
        if(!rsGroup) {
            flash.message = "RsGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsGroup, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsGroup.removeRelation(relationMap);
                      if(rsGroup.hasErrors()){
                          render(view:'edit',model:[rsGroup:rsGroup])
                      }
                      else{
                          flash.message = "RsGroup ${params.id} updated"
                          redirect(action:edit,id:rsGroup.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsGroup.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsGroup.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsGroup")
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