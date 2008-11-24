package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 21, 2008
* Time: 9:57:19 AM
*/
class OpenNMSHttpDatasourceController {
   def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ openNMSHttpDatasourceList: OpenNMSHttpDatasource.list( params ) ]
    }

    def show = {
        def openNMSHttpDatasource = OpenNMSHttpDatasource.get([id:params.id])

        if(!openNMSHttpDatasource) {
            flash.message = "OpenNMSHttpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(openNMSHttpDatasource.class != OpenNMSHttpDatasource)
            {
                def controllerName = openNMSHttpDatasource.class.simpleName;
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
                return [ openNMSHttpDatasource : openNMSHttpDatasource ]
            }
        }
    }

    def delete = {
        def openNMSHttpDatasource = OpenNMSHttpDatasource.get( [id:params.id])
        if(openNMSHttpDatasource) {
            try{
                openNMSHttpDatasource.remove()
                flash.message = "OpenNMSHttpDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[OpenNMSHttpDatasource, openNMSHttpDatasource])]
                flash.errors = errors;
                redirect(action:show, id:openNMSHttpDatasource.id)
            }

        }
        else {
            flash.message = "OpenNMSHttpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def openNMSHttpDatasource = OpenNMSHttpDatasource.get( [id:params.id] )

        if(!openNMSHttpDatasource) {
            flash.message = "OpenNMSHttpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ openNMSHttpDatasource : openNMSHttpDatasource ]
        }
    }


    def update = {
        def openNMSHttpDatasource = OpenNMSHttpDatasource.get( [id:params.id] )
        if(openNMSHttpDatasource) {
            openNMSHttpDatasource.update(ControllerUtils.getClassProperties(params, OpenNMSHttpDatasource));
            if(!openNMSHttpDatasource.hasErrors()) {
                flash.message = "OpenNMSHttpDatasource ${params.id} updated"
                redirect(action:show,id:openNMSHttpDatasource.id)
            }
            else {
                render(view:'edit',model:[openNMSHttpDatasource:openNMSHttpDatasource])
            }
        }
        else {
            flash.message = "OpenNMSHttpDatasource not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def openNMSHttpDatasource = new OpenNMSHttpDatasource()
        openNMSHttpDatasource.properties = params
        return ['openNMSHttpDatasource':openNMSHttpDatasource]
    }

    def save = {
        def openNMSHttpDatasource = OpenNMSHttpDatasource.add(ControllerUtils.getClassProperties(params, OpenNMSHttpDatasource))
        if(!openNMSHttpDatasource.hasErrors()) {
            flash.message = "OpenNMSHttpDatasource ${openNMSHttpDatasource.id} created"
            redirect(action:show,id:openNMSHttpDatasource.id)
        }
        else {
            render(view:'create',model:[openNMSHttpDatasource:openNMSHttpDatasource])
        }
    }

    def addTo = {
        def openNMSHttpDatasource = OpenNMSHttpDatasource.get( [id:params.id] )
        if(!openNMSHttpDatasource){
            flash.message = "OpenNMSHttpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(openNMSHttpDatasource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [openNMSHttpDatasource:openNMSHttpDatasource, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:openNMSHttpDatasource.id)
            }
        }
    }

    def addRelation = {
        def openNMSHttpDatasource = OpenNMSHttpDatasource.get( [id:params.id] )
        if(!openNMSHttpDatasource) {
            flash.message = "OpenNMSHttpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(openNMSHttpDatasource.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      openNMSHttpDatasource.addRelation(relationMap);
                      if(openNMSHttpDatasource.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[openNMSHttpDatasource:openNMSHttpDatasource, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "OpenNMSHttpDatasource ${params.id} updated"
                          redirect(action:edit,id:openNMSHttpDatasource.id)
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
        def openNMSHttpDatasource = OpenNMSHttpDatasource.get( [id:params.id] )
        if(!openNMSHttpDatasource) {
            flash.message = "OpenNMSHttpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(openNMSHttpDatasource.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      openNMSHttpDatasource.removeRelation(relationMap);
                      if(openNMSHttpDatasource.hasErrors()){
                          render(view:'edit',model:[openNMSHttpDatasource:openNMSHttpDatasource])
                      }
                      else{
                          flash.message = "OpenNMSHttpDatasource ${params.id} updated"
                          redirect(action:edit,id:openNMSHttpDatasource.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:openNMSHttpDatasource.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:openNMSHttpDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("datasource.OpenNMSHttpDatasource")
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