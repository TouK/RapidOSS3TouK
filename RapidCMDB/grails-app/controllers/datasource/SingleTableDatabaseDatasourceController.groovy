package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.text.SimpleDateFormat;
class SingleTableDatabaseDatasourceController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ singleTableDatabaseDatasourceList: SingleTableDatabaseDatasource.list( params ) ]
    }

    def show = {
        def singleTableDatabaseDatasource = SingleTableDatabaseDatasource.get([id:params.id])

        if(!singleTableDatabaseDatasource) {
            flash.message = "SingleTableDatabaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(singleTableDatabaseDatasource.class != SingleTableDatabaseDatasource)
            {
                def controllerName = singleTableDatabaseDatasource.class.name;
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
                return [ singleTableDatabaseDatasource : singleTableDatabaseDatasource ]
            }
        }
    }

    def delete = {
        def singleTableDatabaseDatasource = SingleTableDatabaseDatasource.get( [id:params.id])
        if(singleTableDatabaseDatasource) {
            try{
                singleTableDatabaseDatasource.remove()
                flash.message = "SingleTableDatabaseDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[SingleTableDatabaseDatasource, singleTableDatabaseDatasource])]
                flash.errors = errors;
                redirect(action:show, id:singleTableDatabaseDatasource.id)
            }

        }
        else {
            flash.message = "SingleTableDatabaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def singleTableDatabaseDatasource = SingleTableDatabaseDatasource.get( [id:params.id] )

        if(!singleTableDatabaseDatasource) {
            flash.message = "SingleTableDatabaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ singleTableDatabaseDatasource : singleTableDatabaseDatasource ]
        }
    }

    
    def update = {
        def singleTableDatabaseDatasource = SingleTableDatabaseDatasource.get( [id:params.id] )
        if(singleTableDatabaseDatasource) {
            singleTableDatabaseDatasource.update(ControllerUtils.getClassProperties(params, SingleTableDatabaseDatasource));
            if(!singleTableDatabaseDatasource.hasErrors()) {
                flash.message = "SingleTableDatabaseDatasource ${params.id} updated"
                redirect(action:show,id:singleTableDatabaseDatasource.id)
            }
            else {
                render(view:'edit',model:[singleTableDatabaseDatasource:singleTableDatabaseDatasource])
            }
        }
        else {
            flash.message = "SingleTableDatabaseDatasource not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def singleTableDatabaseDatasource = new SingleTableDatabaseDatasource()
        singleTableDatabaseDatasource.properties = params
        return ['singleTableDatabaseDatasource':singleTableDatabaseDatasource]
    }

    def save = {
        def singleTableDatabaseDatasource = SingleTableDatabaseDatasource.add(ControllerUtils.getClassProperties(params, SingleTableDatabaseDatasource))
        if(!singleTableDatabaseDatasource.hasErrors()) {
            flash.message = "SingleTableDatabaseDatasource ${singleTableDatabaseDatasource.id} created"
            redirect(action:show,id:singleTableDatabaseDatasource.id)
        }
        else {
            render(view:'create',model:[singleTableDatabaseDatasource:singleTableDatabaseDatasource])
        }
    }

    def addTo = {
        def singleTableDatabaseDatasource = SingleTableDatabaseDatasource.get( [id:params.id] )
        if(!singleTableDatabaseDatasource){
            flash.message = "SingleTableDatabaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = singleTableDatabaseDatasource.relations[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [singleTableDatabaseDatasource:singleTableDatabaseDatasource, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:singleTableDatabaseDatasource.id)
            }
        }
    }

    def addRelation = {
        def singleTableDatabaseDatasource = SingleTableDatabaseDatasource.get( [id:params.id] )
        if(!singleTableDatabaseDatasource) {
            flash.message = "SingleTableDatabaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = singleTableDatabaseDatasource.relations[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      singleTableDatabaseDatasource.addRelation(relationMap);
                      if(singleTableDatabaseDatasource.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[singleTableDatabaseDatasource:singleTableDatabaseDatasource, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SingleTableDatabaseDatasource ${params.id} updated"
                          redirect(action:edit,id:singleTableDatabaseDatasource.id)
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
        def singleTableDatabaseDatasource = SingleTableDatabaseDatasource.get( [id:params.id] )
        if(!singleTableDatabaseDatasource) {
            flash.message = "SingleTableDatabaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = singleTableDatabaseDatasource.relations[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      singleTableDatabaseDatasource.removeRelation(relationMap);
                      if(singleTableDatabaseDatasource.hasErrors()){
                          render(view:'edit',model:[singleTableDatabaseDatasource:singleTableDatabaseDatasource])
                      }
                      else{
                          flash.message = "SingleTableDatabaseDatasource ${params.id} updated"
                          redirect(action:edit,id:singleTableDatabaseDatasource.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:singleTableDatabaseDatasource.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:singleTableDatabaseDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SingleTableDatabaseDatasource")
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