package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.text.SimpleDateFormat;
class BaseDatasourceController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ baseDatasourceList: BaseDatasource.list( params ) ]
    }

    def show = {
        def baseDatasource = BaseDatasource.get([id:params.id])

        if(!baseDatasource) {
            flash.message = "BaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(baseDatasource.class != BaseDatasource)
            {
                def controllerName = baseDatasource.class.name;
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
                return [ baseDatasource : baseDatasource ]
            }
        }
    }

    def delete = {
        def baseDatasource = BaseDatasource.get( [id:params.id])
        if(baseDatasource) {
            try{
                baseDatasource.remove()
                flash.message = "BaseDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[BaseDatasource, baseDatasource])]
                flash.errors = errors;
                redirect(action:show, id:baseDatasource.id)
            }

        }
        else {
            flash.message = "BaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def baseDatasource = BaseDatasource.get( [id:params.id] )

        if(!baseDatasource) {
            flash.message = "BaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ baseDatasource : baseDatasource ]
        }
    }

    
    def update = {
        def baseDatasource = BaseDatasource.get( [id:params.id] )
        if(baseDatasource) {
            baseDatasource.update(ControllerUtils.getClassProperties(params, BaseDatasource));
            if(!baseDatasource.hasErrors()) {
                flash.message = "BaseDatasource ${params.id} updated"
                redirect(action:show,id:baseDatasource.id)
            }
            else {
                render(view:'edit',model:[baseDatasource:baseDatasource])
            }
        }
        else {
            flash.message = "BaseDatasource not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def baseDatasource = new BaseDatasource()
        baseDatasource.properties = params
        return ['baseDatasource':baseDatasource]
    }

    def save = {
        def baseDatasource = BaseDatasource.add(ControllerUtils.getClassProperties(params, BaseDatasource))
        if(!baseDatasource.hasErrors()) {
            flash.message = "BaseDatasource ${baseDatasource.id} created"
            redirect(action:show,id:baseDatasource.id)
        }
        else {
            render(view:'create',model:[baseDatasource:baseDatasource])
        }
    }

    def addTo = {
        def baseDatasource = BaseDatasource.get( [id:params.id] )
        if(!baseDatasource){
            flash.message = "BaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(baseDatasource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [baseDatasource:baseDatasource, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:baseDatasource.id)
            }
        }
    }

    def addRelation = {
        def baseDatasource = BaseDatasource.get( [id:params.id] )
        if(!baseDatasource) {
            flash.message = "BaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(baseDatasource.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      baseDatasource.addRelation(relationMap);
                      if(baseDatasource.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[baseDatasource:baseDatasource, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "BaseDatasource ${params.id} updated"
                          redirect(action:edit,id:baseDatasource.id)
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
        def baseDatasource = BaseDatasource.get( [id:params.id] )
        if(!baseDatasource) {
            flash.message = "BaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(baseDatasource.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      baseDatasource.removeRelation(relationMap);
                      if(baseDatasource.hasErrors()){
                          render(view:'edit',model:[baseDatasource:baseDatasource])
                      }
                      else{
                          flash.message = "BaseDatasource ${params.id} updated"
                          redirect(action:edit,id:baseDatasource.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:baseDatasource.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:baseDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("datasource.BaseDatasource")
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