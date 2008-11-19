import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class SmartsNetworkAdapterController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ smartsNetworkAdapterList: SmartsNetworkAdapter.list( params ) ]
    }

    def show = {
        def smartsNetworkAdapter = SmartsNetworkAdapter.get([id:params.id])

        if(!smartsNetworkAdapter) {
            flash.message = "SmartsNetworkAdapter not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsNetworkAdapter.class != SmartsNetworkAdapter)
            {
                def controllerName = smartsNetworkAdapter.class.simpleName;
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
                return [ smartsNetworkAdapter : smartsNetworkAdapter ]
            }
        }
    }

    def delete = {
        def smartsNetworkAdapter = SmartsNetworkAdapter.get( [id:params.id])
        if(smartsNetworkAdapter) {
            try{
                smartsNetworkAdapter.remove()
                flash.message = "SmartsNetworkAdapter ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [SmartsNetworkAdapter, smartsNetworkAdapter])
                flash.errors = this.errors;
                redirect(action:show, id:smartsNetworkAdapter.id)
            }

        }
        else {
            flash.message = "SmartsNetworkAdapter not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsNetworkAdapter = SmartsNetworkAdapter.get( [id:params.id] )

        if(!smartsNetworkAdapter) {
            flash.message = "SmartsNetworkAdapter not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsNetworkAdapter : smartsNetworkAdapter ]
        }
    }


    def update = {
        def smartsNetworkAdapter = SmartsNetworkAdapter.get( [id:params.id] )
        if(smartsNetworkAdapter) {
            smartsNetworkAdapter.update(ControllerUtils.getClassProperties(params, SmartsNetworkAdapter));
            if(!smartsNetworkAdapter.hasErrors()) {
                flash.message = "SmartsNetworkAdapter ${params.id} updated"
                redirect(action:show,id:smartsNetworkAdapter.id)
            }
            else {
                render(view:'edit',model:[smartsNetworkAdapter:smartsNetworkAdapter])
            }
        }
        else {
            flash.message = "SmartsNetworkAdapter not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsNetworkAdapter = new SmartsNetworkAdapter()
        smartsNetworkAdapter.properties = params
        return ['smartsNetworkAdapter':smartsNetworkAdapter]
    }

    def save = {
        def smartsNetworkAdapter = SmartsNetworkAdapter.add(ControllerUtils.getClassProperties(params, SmartsNetworkAdapter))
        if(!smartsNetworkAdapter.hasErrors()) {
            flash.message = "SmartsNetworkAdapter ${smartsNetworkAdapter.id} created"
            redirect(action:show,id:smartsNetworkAdapter.id)
        }
        else {
            render(view:'create',model:[smartsNetworkAdapter:smartsNetworkAdapter])
        }
    }

    def addTo = {
        def smartsNetworkAdapter = SmartsNetworkAdapter.get( [id:params.id] )
        if(!smartsNetworkAdapter){
            flash.message = "SmartsNetworkAdapter not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(SmartsNetworkAdapter, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsNetworkAdapter:smartsNetworkAdapter, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsNetworkAdapter.id)
            }
        }
    }



    def addRelation = {
        def smartsNetworkAdapter = SmartsNetworkAdapter.get( [id:params.id] )
        if(!smartsNetworkAdapter) {
            flash.message = "SmartsNetworkAdapter not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(SmartsNetworkAdapter, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsNetworkAdapter.addRelation(relationMap);
                      if(smartsNetworkAdapter.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsNetworkAdapter:smartsNetworkAdapter, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsNetworkAdapter ${params.id} updated"
                          redirect(action:edit,id:smartsNetworkAdapter.id)
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
        def smartsNetworkAdapter = SmartsNetworkAdapter.get( [id:params.id] )
        if(!smartsNetworkAdapter) {
            flash.message = "SmartsNetworkAdapter not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(SmartsNetworkAdapter, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsNetworkAdapter.removeRelation(relationMap);
                      if(smartsNetworkAdapter.hasErrors()){
                          render(view:'edit',model:[smartsNetworkAdapter:smartsNetworkAdapter])
                      }
                      else{
                          flash.message = "SmartsNetworkAdapter ${params.id} updated"
                          redirect(action:edit,id:smartsNetworkAdapter.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsNetworkAdapter.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsNetworkAdapter.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SmartsNetworkAdapter")
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