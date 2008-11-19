import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class SmartsLinkController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ smartsLinkList: SmartsLink.list( params ) ]
    }

    def show = {
        def smartsLink = SmartsLink.get([id:params.id])

        if(!smartsLink) {
            flash.message = "SmartsLink not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsLink.class != SmartsLink)
            {
                def controllerName = smartsLink.class.simpleName;
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
                return [ smartsLink : smartsLink ]
            }
        }
    }

    def delete = {
        def smartsLink = SmartsLink.get( [id:params.id])
        if(smartsLink) {
            try{
                smartsLink.remove()
                flash.message = "SmartsLink ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [SmartsLink, smartsLink])
                flash.errors = this.errors;
                redirect(action:show, id:smartsLink.id)
            }

        }
        else {
            flash.message = "SmartsLink not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsLink = SmartsLink.get( [id:params.id] )

        if(!smartsLink) {
            flash.message = "SmartsLink not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsLink : smartsLink ]
        }
    }


    def update = {
        def smartsLink = SmartsLink.get( [id:params.id] )
        if(smartsLink) {
            smartsLink.update(ControllerUtils.getClassProperties(params, SmartsLink));
            if(!smartsLink.hasErrors()) {
                flash.message = "SmartsLink ${params.id} updated"
                redirect(action:show,id:smartsLink.id)
            }
            else {
                render(view:'edit',model:[smartsLink:smartsLink])
            }
        }
        else {
            flash.message = "SmartsLink not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsLink = new SmartsLink()
        smartsLink.properties = params
        return ['smartsLink':smartsLink]
    }

    def save = {
        def smartsLink = SmartsLink.add(ControllerUtils.getClassProperties(params, SmartsLink))
        if(!smartsLink.hasErrors()) {
            flash.message = "SmartsLink ${smartsLink.id} created"
            redirect(action:show,id:smartsLink.id)
        }
        else {
            render(view:'create',model:[smartsLink:smartsLink])
        }
    }

    def addTo = {
        def smartsLink = SmartsLink.get( [id:params.id] )
        if(!smartsLink){
            flash.message = "SmartsLink not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(SmartsLink, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsLink:smartsLink, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsLink.id)
            }
        }
    }



    def addRelation = {
        def smartsLink = SmartsLink.get( [id:params.id] )
        if(!smartsLink) {
            flash.message = "SmartsLink not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(SmartsLink, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsLink.addRelation(relationMap);
                      if(smartsLink.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsLink:smartsLink, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsLink ${params.id} updated"
                          redirect(action:edit,id:smartsLink.id)
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
        def smartsLink = SmartsLink.get( [id:params.id] )
        if(!smartsLink) {
            flash.message = "SmartsLink not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(SmartsLink, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsLink.removeRelation(relationMap);
                      if(smartsLink.hasErrors()){
                          render(view:'edit',model:[smartsLink:smartsLink])
                      }
                      else{
                          flash.message = "SmartsLink ${params.id} updated"
                          redirect(action:edit,id:smartsLink.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsLink.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsLink.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SmartsLink")
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