import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class SmartsHsrpGroupController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ smartsHsrpGroupList: SmartsHsrpGroup.list( params ) ]
    }

    def show = {
        def smartsHsrpGroup = SmartsHsrpGroup.get([id:params.id])

        if(!smartsHsrpGroup) {
            flash.message = "SmartsHsrpGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsHsrpGroup.class != SmartsHsrpGroup)
            {
                def controllerName = smartsHsrpGroup.class.simpleName;
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
                return [ smartsHsrpGroup : smartsHsrpGroup ]
            }
        }
    }

    def delete = {
        def smartsHsrpGroup = SmartsHsrpGroup.get( [id:params.id])
        if(smartsHsrpGroup) {
            try{
                smartsHsrpGroup.remove()
                flash.message = "SmartsHsrpGroup ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [SmartsHsrpGroup, smartsHsrpGroup])
                flash.errors = this.errors;
                redirect(action:show, id:smartsHsrpGroup.id)
            }

        }
        else {
            flash.message = "SmartsHsrpGroup not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsHsrpGroup = SmartsHsrpGroup.get( [id:params.id] )

        if(!smartsHsrpGroup) {
            flash.message = "SmartsHsrpGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsHsrpGroup : smartsHsrpGroup ]
        }
    }


    def update = {
        def smartsHsrpGroup = SmartsHsrpGroup.get( [id:params.id] )
        if(smartsHsrpGroup) {
            smartsHsrpGroup.update(ControllerUtils.getClassProperties(params, SmartsHsrpGroup));
            if(!smartsHsrpGroup.hasErrors()) {
                flash.message = "SmartsHsrpGroup ${params.id} updated"
                redirect(action:show,id:smartsHsrpGroup.id)
            }
            else {
                render(view:'edit',model:[smartsHsrpGroup:smartsHsrpGroup])
            }
        }
        else {
            flash.message = "SmartsHsrpGroup not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsHsrpGroup = new SmartsHsrpGroup()
        smartsHsrpGroup.properties = params
        return ['smartsHsrpGroup':smartsHsrpGroup]
    }

    def save = {
        def smartsHsrpGroup = SmartsHsrpGroup.add(ControllerUtils.getClassProperties(params, SmartsHsrpGroup))
        if(!smartsHsrpGroup.hasErrors()) {
            flash.message = "SmartsHsrpGroup ${smartsHsrpGroup.id} created"
            redirect(action:show,id:smartsHsrpGroup.id)
        }
        else {
            render(view:'create',model:[smartsHsrpGroup:smartsHsrpGroup])
        }
    }

    def addTo = {
        def smartsHsrpGroup = SmartsHsrpGroup.get( [id:params.id] )
        if(!smartsHsrpGroup){
            flash.message = "SmartsHsrpGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(SmartsHsrpGroup, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsHsrpGroup:smartsHsrpGroup, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsHsrpGroup.id)
            }
        }
    }



    def addRelation = {
        def smartsHsrpGroup = SmartsHsrpGroup.get( [id:params.id] )
        if(!smartsHsrpGroup) {
            flash.message = "SmartsHsrpGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(SmartsHsrpGroup, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsHsrpGroup.addRelation(relationMap);
                      if(smartsHsrpGroup.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsHsrpGroup:smartsHsrpGroup, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsHsrpGroup ${params.id} updated"
                          redirect(action:edit,id:smartsHsrpGroup.id)
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
        def smartsHsrpGroup = SmartsHsrpGroup.get( [id:params.id] )
        if(!smartsHsrpGroup) {
            flash.message = "SmartsHsrpGroup not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(SmartsHsrpGroup, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsHsrpGroup.removeRelation(relationMap);
                      if(smartsHsrpGroup.hasErrors()){
                          render(view:'edit',model:[smartsHsrpGroup:smartsHsrpGroup])
                      }
                      else{
                          flash.message = "SmartsHsrpGroup ${params.id} updated"
                          redirect(action:edit,id:smartsHsrpGroup.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsHsrpGroup.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsHsrpGroup.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SmartsHsrpGroup")
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