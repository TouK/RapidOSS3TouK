import com.ifountain.rcmdb.domain.util.ControllerUtils;


class RsLinkController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsLinkList: RsLink.list( params ) ]
    }

    def show = {
        def rsLink = RsLink.get([id:params.id])

        if(!rsLink) {
            flash.message = "RsLink not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsLink.class != RsLink)
            {
                def controllerName = rsLink.class.name;
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
                return [ rsLink : rsLink ]
            }
        }
    }

    def delete = {
        def rsLink = RsLink.get( [id:params.id])
        if(rsLink) {
            try{
                rsLink.remove()
                flash.message = "RsLink ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsLink, rsLink])
                flash.errors = this.errors;
                redirect(action:show, id:rsLink.id)
            }

        }
        else {
            flash.message = "RsLink not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsLink = RsLink.get( [id:params.id] )

        if(!rsLink) {
            flash.message = "RsLink not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsLink : rsLink ]
        }
    }


    def update = {
        def rsLink = RsLink.get( [id:params.id] )
        if(rsLink) {
            rsLink.update(ControllerUtils.getClassProperties(params, RsLink));
            if(!rsLink.hasErrors()) {
                flash.message = "RsLink ${params.id} updated"
                redirect(action:show,id:rsLink.id)
            }
            else {
                render(view:'edit',model:[rsLink:rsLink])
            }
        }
        else {
            flash.message = "RsLink not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsLink = new RsLink()
        rsLink.properties = params
        return ['rsLink':rsLink]
    }

    def save = {
        def rsLink = RsLink.add(ControllerUtils.getClassProperties(params, RsLink))
        if(!rsLink.hasErrors()) {
            flash.message = "RsLink ${rsLink.id} created"
            redirect(action:show,id:rsLink.id)
        }
        else {
            render(view:'create',model:[rsLink:rsLink])
        }
    }

    def addTo = {
        def rsLink = RsLink.get( [id:params.id] )
        if(!rsLink){
            flash.message = "RsLink not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = rsLink.hasMany[relationName];
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsLink:rsLink, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsLink.id)
            }
        }
    }

    def addRelation = {
        def rsLink = RsLink.get( [id:params.id] )
        if(!rsLink) {
            flash.message = "RsLink not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = rsLink.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsLink.addRelation(relationMap);
                      if(rsLink.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsLink:rsLink, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsLink ${params.id} updated"
                          redirect(action:edit,id:rsLink.id)
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
        def rsLink = RsLink.get( [id:params.id] )
        if(!rsLink) {
            flash.message = "RsLink not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = rsLink.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsLink.removeRelation(relationMap);
                      if(rsLink.hasErrors()){
                          render(view:'edit',model:[rsLink:rsLink])
                      }
                      else{
                          flash.message = "RsLink ${params.id} updated"
                          redirect(action:edit,id:rsLink.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsLink.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsLink.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsLink")
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