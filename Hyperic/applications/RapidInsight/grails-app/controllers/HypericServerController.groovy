import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class HypericServerController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ hypericServerList: HypericServer.list( params ) ]
    }

    def show = {
        def hypericServer = HypericServer.get([id:params.id])

        if(!hypericServer) {
            flash.message = "HypericServer not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(hypericServer.class != HypericServer)
            {
                def controllerName = hypericServer.class.simpleName;
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
                return [ hypericServer : hypericServer ]
            }
        }
    }

    def delete = {
        def hypericServer = HypericServer.get( [id:params.id])
        if(hypericServer) {
            try{
                hypericServer.remove()
                flash.message = "HypericServer ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [HypericServer, hypericServer])
                flash.errors = this.errors;
                redirect(action:show, id:hypericServer.id)
            }

        }
        else {
            flash.message = "HypericServer not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def hypericServer = HypericServer.get( [id:params.id] )

        if(!hypericServer) {
            flash.message = "HypericServer not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ hypericServer : hypericServer ]
        }
    }


    def update = {
        def hypericServer = HypericServer.get( [id:params.id] )
        if(hypericServer) {
            hypericServer.update(ControllerUtils.getClassProperties(params, HypericServer));
            if(!hypericServer.hasErrors()) {
                flash.message = "HypericServer ${params.id} updated"
                redirect(action:show,id:hypericServer.id)
            }
            else {
                render(view:'edit',model:[hypericServer:hypericServer])
            }
        }
        else {
            flash.message = "HypericServer not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def hypericServer = new HypericServer()
        hypericServer.properties = params
        return ['hypericServer':hypericServer]
    }

    def save = {
        def hypericServer = HypericServer.add(ControllerUtils.getClassProperties(params, HypericServer))
        if(!hypericServer.hasErrors()) {
            flash.message = "HypericServer ${hypericServer.id} created"
            redirect(action:show,id:hypericServer.id)
        }
        else {
            render(view:'create',model:[hypericServer:hypericServer])
        }
    }

    def addTo = {
        def hypericServer = HypericServer.get( [id:params.id] )
        if(!hypericServer){
            flash.message = "HypericServer not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(HypericServer, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [hypericServer:hypericServer, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:hypericServer.id)
            }
        }
    }



    def addRelation = {
        def hypericServer = HypericServer.get( [id:params.id] )
        if(!hypericServer) {
            flash.message = "HypericServer not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(HypericServer, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      hypericServer.addRelation(relationMap);
                      if(hypericServer.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[hypericServer:hypericServer, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "HypericServer ${params.id} updated"
                          redirect(action:edit,id:hypericServer.id)
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
        def hypericServer = HypericServer.get( [id:params.id] )
        if(!hypericServer) {
            flash.message = "HypericServer not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(HypericServer, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      hypericServer.removeRelation(relationMap);
                      if(hypericServer.hasErrors()){
                          render(view:'edit',model:[hypericServer:hypericServer])
                      }
                      else{
                          flash.message = "HypericServer ${params.id} updated"
                          redirect(action:edit,id:hypericServer.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:hypericServer.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:hypericServer.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("HypericServer")
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