package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 21, 2008
* Time: 9:57:19 AM
*/
class HypericDatasourceController {
   def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ hypericDatasourceList: HypericDatasource.list( params ) ]
    }

    def show = {
        def hypericDatasource = HypericDatasource.get([id:params.id])

        if(!hypericDatasource) {
            flash.message = "HypericDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(hypericDatasource.class != HypericDatasource)
            {
                def controllerName = hypericDatasource.class.simpleName;
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
                return [ hypericDatasource : hypericDatasource ]
            }
        }
    }

    def delete = {
        def hypericDatasource = HypericDatasource.get( [id:params.id])
        if(hypericDatasource) {
            try{
                hypericDatasource.remove()
                flash.message = "HypericDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[HypericDatasource, hypericDatasource])]
                flash.errors = errors;
                redirect(action:show, id:hypericDatasource.id)
            }

        }
        else {
            flash.message = "HypericDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def hypericDatasource = HypericDatasource.get( [id:params.id] )

        if(!hypericDatasource) {
            flash.message = "HypericDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ hypericDatasource : hypericDatasource ]
        }
    }


    def update = {
        def hypericDatasource = HypericDatasource.get( [id:params.id] )
        if(hypericDatasource) {
            hypericDatasource.update(ControllerUtils.getClassProperties(params, HypericDatasource));
            if(!hypericDatasource.hasErrors()) {
                flash.message = "HypericDatasource ${params.id} updated"
                redirect(action:show,id:hypericDatasource.id)
            }
            else {
                render(view:'edit',model:[hypericDatasource:hypericDatasource])
            }
        }
        else {
            flash.message = "HypericDatasource not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def hypericDatasource = new HypericDatasource()
        hypericDatasource.properties = params
        return ['hypericDatasource':hypericDatasource]
    }

    def save = {
        def hypericDatasource = HypericDatasource.add(ControllerUtils.getClassProperties(params, HypericDatasource))
        if(!hypericDatasource.hasErrors()) {
            flash.message = "HypericDatasource ${hypericDatasource.id} created"
            redirect(action:show,id:hypericDatasource.id)
        }
        else {
            render(view:'create',model:[hypericDatasource:hypericDatasource])
        }
    }

    def addTo = {
        def hypericDatasource = HypericDatasource.get( [id:params.id] )
        if(!hypericDatasource){
            flash.message = "HypericDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(hypericDatasource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [hypericDatasource:hypericDatasource, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:hypericDatasource.id)
            }
        }
    }

    def addRelation = {
        def hypericDatasource = HypericDatasource.get( [id:params.id] )
        if(!hypericDatasource) {
            flash.message = "HypericDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(hypericDatasource.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      hypericDatasource.addRelation(relationMap);
                      if(hypericDatasource.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[hypericDatasource:hypericDatasource, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "HypericDatasource ${params.id} updated"
                          redirect(action:edit,id:hypericDatasource.id)
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
        def hypericDatasource = HypericDatasource.get( [id:params.id] )
        if(!hypericDatasource) {
            flash.message = "HypericDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(hypericDatasource.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      hypericDatasource.removeRelation(relationMap);
                      if(hypericDatasource.hasErrors()){
                          render(view:'edit',model:[hypericDatasource:hypericDatasource])
                      }
                      else{
                          flash.message = "HypericDatasource ${params.id} updated"
                          redirect(action:edit,id:hypericDatasource.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:hypericDatasource.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:hypericDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("datasource.HypericDatasource")
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