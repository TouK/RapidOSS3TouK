import com.ifountain.rcmdb.domain.util.ControllerUtils;


class RsManagementServerController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsManagementServerList: RsManagementServer.list( params ) ]
    }

    def show = {
        def rsManagementServer = RsManagementServer.get([id:params.id])

        if(!rsManagementServer) {
            flash.message = "RsManagementServer not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsManagementServer.class != RsManagementServer)
            {
                def controllerName = rsManagementServer.class.name;
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
                return [ rsManagementServer : rsManagementServer ]
            }
        }
    }

    def delete = {
        def rsManagementServer = RsManagementServer.get( [id:params.id])
        if(rsManagementServer) {
            try{
                rsManagementServer.remove()
                flash.message = "RsManagementServer ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsManagementServer, rsManagementServer])
                flash.errors = this.errors;
                redirect(action:show, id:rsManagementServer.id)
            }

        }
        else {
            flash.message = "RsManagementServer not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsManagementServer = RsManagementServer.get( [id:params.id] )

        if(!rsManagementServer) {
            flash.message = "RsManagementServer not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsManagementServer : rsManagementServer ]
        }
    }


    def update = {
        def rsManagementServer = RsManagementServer.get( [id:params.id] )
        if(rsManagementServer) {
            rsManagementServer.update(ControllerUtils.getClassProperties(params, RsManagementServer));
            if(!rsManagementServer.hasErrors()) {
                flash.message = "RsManagementServer ${params.id} updated"
                redirect(action:show,id:rsManagementServer.id)
            }
            else {
                render(view:'edit',model:[rsManagementServer:rsManagementServer])
            }
        }
        else {
            flash.message = "RsManagementServer not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsManagementServer = new RsManagementServer()
        rsManagementServer.properties = params
        return ['rsManagementServer':rsManagementServer]
    }

    def save = {
        def rsManagementServer = RsManagementServer.add(ControllerUtils.getClassProperties(params, RsManagementServer))
        if(!rsManagementServer.hasErrors()) {
            flash.message = "RsManagementServer ${rsManagementServer.id} created"
            redirect(action:show,id:rsManagementServer.id)
        }
        else {
            render(view:'create',model:[rsManagementServer:rsManagementServer])
        }
    }

    def addTo = {
        def rsManagementServer = RsManagementServer.get( [id:params.id] )
        if(!rsManagementServer){
            flash.message = "RsManagementServer not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = rsManagementServer.relations[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsManagementServer:rsManagementServer, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsManagementServer.id)
            }
        }
    }

    def addRelation = {
        def rsManagementServer = RsManagementServer.get( [id:params.id] )
        if(!rsManagementServer) {
            flash.message = "RsManagementServer not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = rsManagementServer.relations[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsManagementServer.addRelation(relationMap);
                      if(rsManagementServer.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsManagementServer:rsManagementServer, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsManagementServer ${params.id} updated"
                          redirect(action:edit,id:rsManagementServer.id)
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
        def rsManagementServer = RsManagementServer.get( [id:params.id] )
        if(!rsManagementServer) {
            flash.message = "RsManagementServer not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = rsManagementServer.relations[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsManagementServer.removeRelation(relationMap);
                      if(rsManagementServer.hasErrors()){
                          render(view:'edit',model:[rsManagementServer:rsManagementServer])
                      }
                      else{
                          flash.message = "RsManagementServer ${params.id} updated"
                          redirect(action:edit,id:rsManagementServer.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsManagementServer.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsManagementServer.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsManagementServer")
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