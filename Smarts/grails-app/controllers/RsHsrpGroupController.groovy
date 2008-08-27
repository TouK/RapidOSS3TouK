import com.ifountain.rcmdb.domain.util.ControllerUtils;


class RsHsrpGroupController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsHsrpGroupList: RsHsrpGroup.list( params ) ]
    }

    def show = {
        def rsHsrpGroup = RsHsrpGroup.get([id:params.id])

        if(!rsHsrpGroup) {
            flash.message = "RsHsrpGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsHsrpGroup.class != RsHsrpGroup)
            {
                def controllerName = rsHsrpGroup.class.name;
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
                return [ rsHsrpGroup : rsHsrpGroup ]
            }
        }
    }

    def delete = {
        def rsHsrpGroup = RsHsrpGroup.get( [id:params.id])
        if(rsHsrpGroup) {
            try{
                rsHsrpGroup.remove()
                flash.message = "RsHsrpGroup ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsHsrpGroup, rsHsrpGroup])
                flash.errors = this.errors;
                redirect(action:show, id:rsHsrpGroup.id)
            }

        }
        else {
            flash.message = "RsHsrpGroup not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsHsrpGroup = RsHsrpGroup.get( [id:params.id] )

        if(!rsHsrpGroup) {
            flash.message = "RsHsrpGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsHsrpGroup : rsHsrpGroup ]
        }
    }


    def update = {
        def rsHsrpGroup = RsHsrpGroup.get( [id:params.id] )
        if(rsHsrpGroup) {
            rsHsrpGroup.update(ControllerUtils.getClassProperties(params, RsHsrpGroup));
            if(!rsHsrpGroup.hasErrors()) {
                flash.message = "RsHsrpGroup ${params.id} updated"
                redirect(action:show,id:rsHsrpGroup.id)
            }
            else {
                render(view:'edit',model:[rsHsrpGroup:rsHsrpGroup])
            }
        }
        else {
            flash.message = "RsHsrpGroup not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsHsrpGroup = new RsHsrpGroup()
        rsHsrpGroup.properties = params
        return ['rsHsrpGroup':rsHsrpGroup]
    }

    def save = {
        def rsHsrpGroup = RsHsrpGroup.add(ControllerUtils.getClassProperties(params, RsHsrpGroup))
        if(!rsHsrpGroup.hasErrors()) {
            flash.message = "RsHsrpGroup ${rsHsrpGroup.id} created"
            redirect(action:show,id:rsHsrpGroup.id)
        }
        else {
            render(view:'create',model:[rsHsrpGroup:rsHsrpGroup])
        }
    }

    def addTo = {
        def rsHsrpGroup = RsHsrpGroup.get( [id:params.id] )
        if(!rsHsrpGroup){
            flash.message = "RsHsrpGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = rsHsrpGroup.hasMany[relationName];
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsHsrpGroup:rsHsrpGroup, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsHsrpGroup.id)
            }
        }
    }

    def addRelation = {
        def rsHsrpGroup = RsHsrpGroup.get( [id:params.id] )
        if(!rsHsrpGroup) {
            flash.message = "RsHsrpGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = rsHsrpGroup.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsHsrpGroup.addRelation(relationMap);
                      if(rsHsrpGroup.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsHsrpGroup:rsHsrpGroup, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsHsrpGroup ${params.id} updated"
                          redirect(action:edit,id:rsHsrpGroup.id)
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
        def rsHsrpGroup = RsHsrpGroup.get( [id:params.id] )
        if(!rsHsrpGroup) {
            flash.message = "RsHsrpGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = rsHsrpGroup.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsHsrpGroup.removeRelation(relationMap);
                      if(rsHsrpGroup.hasErrors()){
                          render(view:'edit',model:[rsHsrpGroup:rsHsrpGroup])
                      }
                      else{
                          flash.message = "RsHsrpGroup ${params.id} updated"
                          redirect(action:edit,id:rsHsrpGroup.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsHsrpGroup.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsHsrpGroup.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsHsrpGroup")
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