package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.text.SimpleDateFormat;
class SmartsTopologyDatasourceController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ smartsTopologyDatasourceList: SmartsTopologyDatasource.list( params ) ]
    }

    def show = {
        def smartsTopologyDatasource = SmartsTopologyDatasource.get([id:params.id])

        if(!smartsTopologyDatasource) {
            flash.message = "SmartsTopologyDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsTopologyDatasource.class != SmartsTopologyDatasource)
            {
                def controllerName = smartsTopologyDatasource.class.simpleName;
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
                return [ smartsTopologyDatasource : smartsTopologyDatasource ]
            }
        }
    }

    def delete = {
        def smartsTopologyDatasource = SmartsTopologyDatasource.get( [id:params.id])
        if(smartsTopologyDatasource) {
            try{
                smartsTopologyDatasource.remove()
                flash.message = "SmartsTopologyDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[SmartsTopologyDatasource, smartsTopologyDatasource])]
                flash.errors = errors;
                redirect(action:show, id:smartsTopologyDatasource.id)
            }

        }
        else {
            flash.message = "SmartsTopologyDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsTopologyDatasource = SmartsTopologyDatasource.get( [id:params.id] )

        if(!smartsTopologyDatasource) {
            flash.message = "SmartsTopologyDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsTopologyDatasource : smartsTopologyDatasource ]
        }
    }

    
    def update = {
        def smartsTopologyDatasource = SmartsTopologyDatasource.get( [id:params.id] )
        if(smartsTopologyDatasource) {
            smartsTopologyDatasource.update(ControllerUtils.getClassProperties(params, SmartsTopologyDatasource));
            if(!smartsTopologyDatasource.hasErrors()) {
                flash.message = "SmartsTopologyDatasource ${params.id} updated"
                redirect(action:show,id:smartsTopologyDatasource.id)
            }
            else {
                render(view:'edit',model:[smartsTopologyDatasource:smartsTopologyDatasource])
            }
        }
        else {
            flash.message = "SmartsTopologyDatasource not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsTopologyDatasource = new SmartsTopologyDatasource()
        smartsTopologyDatasource.properties = params
        return ['smartsTopologyDatasource':smartsTopologyDatasource]
    }

    def save = {
        def smartsTopologyDatasource = SmartsTopologyDatasource.add(ControllerUtils.getClassProperties(params, SmartsTopologyDatasource))
        if(!smartsTopologyDatasource.hasErrors()) {
            flash.message = "SmartsTopologyDatasource ${smartsTopologyDatasource.id} created"
            redirect(action:show,id:smartsTopologyDatasource.id)
        }
        else {
            render(view:'create',model:[smartsTopologyDatasource:smartsTopologyDatasource])
        }
    }

    def addTo = {
        def smartsTopologyDatasource = SmartsTopologyDatasource.get( [id:params.id] )
        if(!smartsTopologyDatasource){
            flash.message = "SmartsTopologyDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(smartsTopologyDatasource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsTopologyDatasource:smartsTopologyDatasource, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsTopologyDatasource.id)
            }
        }
    }

    def addRelation = {
        def smartsTopologyDatasource = SmartsTopologyDatasource.get( [id:params.id] )
        if(!smartsTopologyDatasource) {
            flash.message = "SmartsTopologyDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(smartsTopologyDatasource.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsTopologyDatasource.addRelation(relationMap);
                      if(smartsTopologyDatasource.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsTopologyDatasource:smartsTopologyDatasource, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsTopologyDatasource ${params.id} updated"
                          redirect(action:edit,id:smartsTopologyDatasource.id)
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
        def smartsTopologyDatasource = SmartsTopologyDatasource.get( [id:params.id] )
        if(!smartsTopologyDatasource) {
            flash.message = "SmartsTopologyDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(smartsTopologyDatasource.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsTopologyDatasource.removeRelation(relationMap);
                      if(smartsTopologyDatasource.hasErrors()){
                          render(view:'edit',model:[smartsTopologyDatasource:smartsTopologyDatasource])
                      }
                      else{
                          flash.message = "SmartsTopologyDatasource ${params.id} updated"
                          redirect(action:edit,id:smartsTopologyDatasource.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsTopologyDatasource.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsTopologyDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("datasource.SmartsTopologyDatasource")
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