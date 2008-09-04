package connection;
import com.ifountain.rcmdb.domain.util.ControllerUtils;

import connection.SmartsConnectionTemplate
class SmartsConnectionTemplateController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        redirect(uri: '/admin.gsp');
    }

    def show = {
        def smartsConnectionTemplate = SmartsConnectionTemplate.get([id:params.id])

        if(!smartsConnectionTemplate) {
            flash.message = "SmartsConnectionTemplate not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsConnectionTemplate.class != SmartsConnectionTemplate)
            {
                def controllerName = smartsConnectionTemplate.class.name;
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
                return [ smartsConnectionTemplate : smartsConnectionTemplate ]
            }
        }
    }

    def delete = {
        def smartsConnectionTemplate = SmartsConnectionTemplate.get( [id:params.id])
        if(smartsConnectionTemplate) {
            try{
                smartsConnectionTemplate.remove()
                flash.message = "SmartsConnectionTemplate ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [SmartsConnectionTemplate, smartsConnectionTemplate])
                flash.errors = this.errors;
                redirect(action:show, id:smartsConnectionTemplate.id)
            }

        }
        else {
            flash.message = "SmartsConnectionTemplate not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsConnectionTemplate = SmartsConnectionTemplate.get( [id:params.id] )

        if(!smartsConnectionTemplate) {
            flash.message = "SmartsConnectionTemplate not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsConnectionTemplate : smartsConnectionTemplate ]
        }
    }


    def update = {
        def smartsConnectionTemplate = SmartsConnectionTemplate.get( [id:params.id] )
        if(smartsConnectionTemplate) {
            smartsConnectionTemplate.update(ControllerUtils.getClassProperties(params, SmartsConnectionTemplate));
            if(!smartsConnectionTemplate.hasErrors()) {
                flash.message = "SmartsConnectionTemplate ${params.id} updated"
                redirect(action:show,id:smartsConnectionTemplate.id)
            }
            else {
                render(view:'edit',model:[smartsConnectionTemplate:smartsConnectionTemplate])
            }
        }
        else {
            flash.message = "SmartsConnectionTemplate not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsConnectionTemplate = new SmartsConnectionTemplate()
        smartsConnectionTemplate.properties = params
        return ['smartsConnectionTemplate':smartsConnectionTemplate]
    }

    def save = {
        def smartsConnectionTemplate = SmartsConnectionTemplate.add(ControllerUtils.getClassProperties(params, SmartsConnectionTemplate))
        if(!smartsConnectionTemplate.hasErrors()) {
            flash.message = "SmartsConnectionTemplate ${smartsConnectionTemplate.id} created"
            redirect(action:show,id:smartsConnectionTemplate.id)
        }
        else {
            render(view:'create',model:[smartsConnectionTemplate:smartsConnectionTemplate])
        }
    }

    def addTo = {
        def smartsConnectionTemplate = SmartsConnectionTemplate.get( [id:params.id] )
        if(!smartsConnectionTemplate){
            flash.message = "SmartsConnectionTemplate not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(smartsConnectionTemplate.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsConnectionTemplate:smartsConnectionTemplate, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsConnectionTemplate.id)
            }
        }
    }

    def addRelation = {
        def smartsConnectionTemplate = SmartsConnectionTemplate.get( [id:params.id] )
        if(!smartsConnectionTemplate) {
            flash.message = "SmartsConnectionTemplate not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(smartsConnectionTemplate.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsConnectionTemplate.addRelation(relationMap);
                      if(smartsConnectionTemplate.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsConnectionTemplate:smartsConnectionTemplate, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsConnectionTemplate ${params.id} updated"
                          redirect(action:edit,id:smartsConnectionTemplate.id)
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
        def smartsConnectionTemplate = SmartsConnectionTemplate.get( [id:params.id] )
        if(!smartsConnectionTemplate) {
            flash.message = "SmartsConnectionTemplate not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(smartsConnectionTemplate.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsConnectionTemplate.removeRelation(relationMap);
                      if(smartsConnectionTemplate.hasErrors()){
                          render(view:'edit',model:[smartsConnectionTemplate:smartsConnectionTemplate])
                      }
                      else{
                          flash.message = "SmartsConnectionTemplate ${params.id} updated"
                          redirect(action:edit,id:smartsConnectionTemplate.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsConnectionTemplate.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsConnectionTemplate.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SmartsConnectionTemplate")
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