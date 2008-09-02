import com.ifountain.rcmdb.domain.util.ControllerUtils;


class EdgeNodeController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ edgeNodeList: EdgeNode.list( params ) ]
    }

    def show = {
        def edgeNode = EdgeNode.get([id:params.id])

        if(!edgeNode) {
            flash.message = "EdgeNode not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(edgeNode.class != EdgeNode)
            {
                def controllerName = edgeNode.class.name;
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
                return [ edgeNode : edgeNode ]
            }
        }
    }

    def delete = {
        def edgeNode = EdgeNode.get( [id:params.id])
        if(edgeNode) {
            try{
                edgeNode.remove()
                flash.message = "EdgeNode ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [EdgeNode, edgeNode])
                flash.errors = this.errors;
                redirect(action:show, id:edgeNode.id)
            }

        }
        else {
            flash.message = "EdgeNode not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def edgeNode = EdgeNode.get( [id:params.id] )

        if(!edgeNode) {
            flash.message = "EdgeNode not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ edgeNode : edgeNode ]
        }
    }


    def update = {
        def edgeNode = EdgeNode.get( [id:params.id] )
        if(edgeNode) {
            edgeNode.update(ControllerUtils.getClassProperties(params, EdgeNode));
            if(!edgeNode.hasErrors()) {
                flash.message = "EdgeNode ${params.id} updated"
                redirect(action:show,id:edgeNode.id)
            }
            else {
                render(view:'edit',model:[edgeNode:edgeNode])
            }
        }
        else {
            flash.message = "EdgeNode not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def edgeNode = new EdgeNode()
        edgeNode.properties = params
        return ['edgeNode':edgeNode]
    }

    def save = {
        def edgeNode = EdgeNode.add(ControllerUtils.getClassProperties(params, EdgeNode))
        if(!edgeNode.hasErrors()) {
            flash.message = "EdgeNode ${edgeNode.id} created"
            redirect(action:show,id:edgeNode.id)
        }
        else {
            render(view:'create',model:[edgeNode:edgeNode])
        }
    }

    def addTo = {
        def edgeNode = EdgeNode.get( [id:params.id] )
        if(!edgeNode){
            flash.message = "EdgeNode not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = edgeNode.hasMany[relationName];
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [edgeNode:edgeNode, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:edgeNode.id)
            }
        }
    }

    def addRelation = {
        def edgeNode = EdgeNode.get( [id:params.id] )
        if(!edgeNode) {
            flash.message = "EdgeNode not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = edgeNode.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      edgeNode.addRelation(relationMap);
                      if(edgeNode.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[edgeNode:edgeNode, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "EdgeNode ${params.id} updated"
                          redirect(action:edit,id:edgeNode.id)
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
        def edgeNode = EdgeNode.get( [id:params.id] )
        if(!edgeNode) {
            flash.message = "EdgeNode not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = edgeNode.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      edgeNode.removeRelation(relationMap);
                      if(edgeNode.hasErrors()){
                          render(view:'edit',model:[edgeNode:edgeNode])
                      }
                      else{
                          flash.message = "EdgeNode ${params.id} updated"
                          redirect(action:edit,id:edgeNode.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:edgeNode.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:edgeNode.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("EdgeNode")
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