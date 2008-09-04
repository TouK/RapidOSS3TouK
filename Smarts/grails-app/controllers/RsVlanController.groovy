import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsVlanController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsVlanList: RsVlan.list( params ) ]
    }

    def show = {
        def rsVlan = RsVlan.get([id:params.id])

        if(!rsVlan) {
            flash.message = "RsVlan not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsVlan.class != RsVlan)
            {
                def controllerName = rsVlan.class.name;
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
                return [ rsVlan : rsVlan ]
            }
        }
    }

    def delete = {
        def rsVlan = RsVlan.get( [id:params.id])
        if(rsVlan) {
            try{
                rsVlan.remove()
                flash.message = "RsVlan ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsVlan, rsVlan])
                flash.errors = this.errors;
                redirect(action:show, id:rsVlan.id)
            }

        }
        else {
            flash.message = "RsVlan not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsVlan = RsVlan.get( [id:params.id] )

        if(!rsVlan) {
            flash.message = "RsVlan not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsVlan : rsVlan ]
        }
    }


    def update = {
        def rsVlan = RsVlan.get( [id:params.id] )
        if(rsVlan) {
            rsVlan.update(ControllerUtils.getClassProperties(params, RsVlan));
            if(!rsVlan.hasErrors()) {
                flash.message = "RsVlan ${params.id} updated"
                redirect(action:show,id:rsVlan.id)
            }
            else {
                render(view:'edit',model:[rsVlan:rsVlan])
            }
        }
        else {
            flash.message = "RsVlan not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsVlan = new RsVlan()
        rsVlan.properties = params
        return ['rsVlan':rsVlan]
    }

    def save = {
        def rsVlan = RsVlan.add(ControllerUtils.getClassProperties(params, RsVlan))
        if(!rsVlan.hasErrors()) {
            flash.message = "RsVlan ${rsVlan.id} created"
            redirect(action:show,id:rsVlan.id)
        }
        else {
            render(view:'create',model:[rsVlan:rsVlan])
        }
    }

    def addTo = {
        def rsVlan = RsVlan.get( [id:params.id] )
        if(!rsVlan){
            flash.message = "RsVlan not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsVlan, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsVlan:rsVlan, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsVlan.id)
            }
        }
    }



    def addRelation = {
        def rsVlan = RsVlan.get( [id:params.id] )
        if(!rsVlan) {
            flash.message = "RsVlan not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsVlan, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsVlan.addRelation(relationMap);
                      if(rsVlan.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsVlan:rsVlan, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsVlan ${params.id} updated"
                          redirect(action:edit,id:rsVlan.id)
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
        def rsVlan = RsVlan.get( [id:params.id] )
        if(!rsVlan) {
            flash.message = "RsVlan not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsVlan, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsVlan.removeRelation(relationMap);
                      if(rsVlan.hasErrors()){
                          render(view:'edit',model:[rsVlan:rsVlan])
                      }
                      else{
                          flash.message = "RsVlan ${params.id} updated"
                          redirect(action:edit,id:rsVlan.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsVlan.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsVlan.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsVlan")
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