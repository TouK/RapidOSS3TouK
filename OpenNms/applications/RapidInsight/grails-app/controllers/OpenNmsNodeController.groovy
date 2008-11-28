import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class OpenNmsNodeController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ openNmsNodeList: OpenNmsNode.list( params ) ]
    }

    def show = {
        def openNmsNode = OpenNmsNode.get([id:params.id])

        if(!openNmsNode) {
            flash.message = "OpenNmsNode not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(openNmsNode.class != OpenNmsNode)
            {
                def controllerName = openNmsNode.class.simpleName;
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
                return [ openNmsNode : openNmsNode ]
            }
        }
    }

    def delete = {
        def openNmsNode = OpenNmsNode.get( [id:params.id])
        if(openNmsNode) {
            try{
                openNmsNode.remove()
                flash.message = "OpenNmsNode ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [OpenNmsNode, openNmsNode])
                flash.errors = this.errors;
                redirect(action:show, id:openNmsNode.id)
            }

        }
        else {
            flash.message = "OpenNmsNode not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def openNmsNode = OpenNmsNode.get( [id:params.id] )

        if(!openNmsNode) {
            flash.message = "OpenNmsNode not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ openNmsNode : openNmsNode ]
        }
    }


    def update = {
        def openNmsNode = OpenNmsNode.get( [id:params.id] )
        if(openNmsNode) {
            openNmsNode.update(ControllerUtils.getClassProperties(params, OpenNmsNode));
            if(!openNmsNode.hasErrors()) {
                flash.message = "OpenNmsNode ${params.id} updated"
                redirect(action:show,id:openNmsNode.id)
            }
            else {
                render(view:'edit',model:[openNmsNode:openNmsNode])
            }
        }
        else {
            flash.message = "OpenNmsNode not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def openNmsNode = new OpenNmsNode()
        openNmsNode.properties = params
        return ['openNmsNode':openNmsNode]
    }

    def save = {
        def openNmsNode = OpenNmsNode.add(ControllerUtils.getClassProperties(params, OpenNmsNode))
        if(!openNmsNode.hasErrors()) {
            flash.message = "OpenNmsNode ${openNmsNode.id} created"
            redirect(action:show,id:openNmsNode.id)
        }
        else {
            render(view:'create',model:[openNmsNode:openNmsNode])
        }
    }

    def addTo = {
        def openNmsNode = OpenNmsNode.get( [id:params.id] )
        if(!openNmsNode){
            flash.message = "OpenNmsNode not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(OpenNmsNode, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [openNmsNode:openNmsNode, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:openNmsNode.id)
            }
        }
    }



    def addRelation = {
        def openNmsNode = OpenNmsNode.get( [id:params.id] )
        if(!openNmsNode) {
            flash.message = "OpenNmsNode not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(OpenNmsNode, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      openNmsNode.addRelation(relationMap);
                      if(openNmsNode.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[openNmsNode:openNmsNode, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "OpenNmsNode ${params.id} updated"
                          redirect(action:edit,id:openNmsNode.id)
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
        def openNmsNode = OpenNmsNode.get( [id:params.id] )
        if(!openNmsNode) {
            flash.message = "OpenNmsNode not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(OpenNmsNode, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      openNmsNode.removeRelation(relationMap);
                      if(openNmsNode.hasErrors()){
                          render(view:'edit',model:[openNmsNode:openNmsNode])
                      }
                      else{
                          flash.message = "OpenNmsNode ${params.id} updated"
                          redirect(action:edit,id:openNmsNode.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:openNmsNode.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:openNmsNode.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("OpenNmsNode")
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