import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class OpenNmsIpInterfaceController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ openNmsIpInterfaceList: OpenNmsIpInterface.list( params ) ]
    }

    def show = {
        def openNmsIpInterface = OpenNmsIpInterface.get([id:params.id])

        if(!openNmsIpInterface) {
            flash.message = "OpenNmsIpInterface not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(openNmsIpInterface.class != OpenNmsIpInterface)
            {
                def controllerName = openNmsIpInterface.class.simpleName;
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
                return [ openNmsIpInterface : openNmsIpInterface ]
            }
        }
    }

    def delete = {
        def openNmsIpInterface = OpenNmsIpInterface.get( [id:params.id])
        if(openNmsIpInterface) {
            try{
                openNmsIpInterface.remove()
                flash.message = "OpenNmsIpInterface ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [OpenNmsIpInterface, openNmsIpInterface])
                flash.errors = this.errors;
                redirect(action:show, id:openNmsIpInterface.id)
            }

        }
        else {
            flash.message = "OpenNmsIpInterface not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def openNmsIpInterface = OpenNmsIpInterface.get( [id:params.id] )

        if(!openNmsIpInterface) {
            flash.message = "OpenNmsIpInterface not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ openNmsIpInterface : openNmsIpInterface ]
        }
    }


    def update = {
        def openNmsIpInterface = OpenNmsIpInterface.get( [id:params.id] )
        if(openNmsIpInterface) {
            openNmsIpInterface.update(ControllerUtils.getClassProperties(params, OpenNmsIpInterface));
            if(!openNmsIpInterface.hasErrors()) {
                flash.message = "OpenNmsIpInterface ${params.id} updated"
                redirect(action:show,id:openNmsIpInterface.id)
            }
            else {
                render(view:'edit',model:[openNmsIpInterface:openNmsIpInterface])
            }
        }
        else {
            flash.message = "OpenNmsIpInterface not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def openNmsIpInterface = new OpenNmsIpInterface()
        openNmsIpInterface.properties = params
        return ['openNmsIpInterface':openNmsIpInterface]
    }

    def save = {
        def openNmsIpInterface = OpenNmsIpInterface.add(ControllerUtils.getClassProperties(params, OpenNmsIpInterface))
        if(!openNmsIpInterface.hasErrors()) {
            flash.message = "OpenNmsIpInterface ${openNmsIpInterface.id} created"
            redirect(action:show,id:openNmsIpInterface.id)
        }
        else {
            render(view:'create',model:[openNmsIpInterface:openNmsIpInterface])
        }
    }

    def addTo = {
        def openNmsIpInterface = OpenNmsIpInterface.get( [id:params.id] )
        if(!openNmsIpInterface){
            flash.message = "OpenNmsIpInterface not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(OpenNmsIpInterface, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [openNmsIpInterface:openNmsIpInterface, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:openNmsIpInterface.id)
            }
        }
    }



    def addRelation = {
        def openNmsIpInterface = OpenNmsIpInterface.get( [id:params.id] )
        if(!openNmsIpInterface) {
            flash.message = "OpenNmsIpInterface not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(OpenNmsIpInterface, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      openNmsIpInterface.addRelation(relationMap);
                      if(openNmsIpInterface.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[openNmsIpInterface:openNmsIpInterface, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "OpenNmsIpInterface ${params.id} updated"
                          redirect(action:edit,id:openNmsIpInterface.id)
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
        def openNmsIpInterface = OpenNmsIpInterface.get( [id:params.id] )
        if(!openNmsIpInterface) {
            flash.message = "OpenNmsIpInterface not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(OpenNmsIpInterface, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      openNmsIpInterface.removeRelation(relationMap);
                      if(openNmsIpInterface.hasErrors()){
                          render(view:'edit',model:[openNmsIpInterface:openNmsIpInterface])
                      }
                      else{
                          flash.message = "OpenNmsIpInterface ${params.id} updated"
                          redirect(action:edit,id:openNmsIpInterface.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:openNmsIpInterface.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:openNmsIpInterface.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("OpenNmsIpInterface")
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