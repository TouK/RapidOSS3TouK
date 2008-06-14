package datasource;
import org.codehaus.groovy.grails.plugins.searchable.util.GrailsDomainClassUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsControllerHelper
import java.text.SimpleDateFormat
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.util.ControllerUtils


class SmartsNotificationDatasourceController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ smartsNotificationDatasourceList: SmartsNotificationDatasource.list( params ) ]
    }

    def show = {
        def smartsNotificationDatasource = SmartsNotificationDatasource.get([id:params.id])

        if(!smartsNotificationDatasource) {
            flash.message = "SmartsNotificationDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(smartsNotificationDatasource.class != SmartsNotificationDatasource)
            {
                def controllerName = smartsNotificationDatasource.class.name;
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
                return [ smartsNotificationDatasource : smartsNotificationDatasource ]
            }
        }
    }

    def delete = {
        def smartsNotificationDatasource = SmartsNotificationDatasource.get( [id:params.id])
        if(smartsNotificationDatasource) {
            try{
                smartsNotificationDatasource.remove()
                flash.message = "SmartsNotificationDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[SmartsNotificationDatasource, smartsNotificationDatasource])]
                flash.errors = errors;
                redirect(action:show, id:smartsNotificationDatasource.id)
            }

        }
        else {
            flash.message = "SmartsNotificationDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def smartsNotificationDatasource = SmartsNotificationDatasource.get( [id:params.id] )

        if(!smartsNotificationDatasource) {
            flash.message = "SmartsNotificationDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ smartsNotificationDatasource : smartsNotificationDatasource ]
        }
    }

    
    def update = {
        def smartsNotificationDatasource = SmartsNotificationDatasource.get( [id:params.id] )
        if(smartsNotificationDatasource) {
            smartsNotificationDatasource.update(ControllerUtils.getClassProperties(params, SmartsNotificationDatasource));
            if(!smartsNotificationDatasource.hasErrors()) {
                flash.message = "SmartsNotificationDatasource ${params.id} updated"
                redirect(action:show,id:smartsNotificationDatasource.id)
            }
            else {
                render(view:'edit',model:[smartsNotificationDatasource:smartsNotificationDatasource])
            }
        }
        else {
            flash.message = "SmartsNotificationDatasource not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def smartsNotificationDatasource = new SmartsNotificationDatasource()
        smartsNotificationDatasource.properties = params
        return ['smartsNotificationDatasource':smartsNotificationDatasource]
    }

    def save = {
        def smartsNotificationDatasource = SmartsNotificationDatasource.add(ControllerUtils.getClassProperties(params, SmartsNotificationDatasource))
        if(!smartsNotificationDatasource.hasErrors()) {
            flash.message = "SmartsNotificationDatasource ${smartsNotificationDatasource.id} created"
            redirect(action:show,id:smartsNotificationDatasource.id)
        }
        else {
            render(view:'create',model:[smartsNotificationDatasource:smartsNotificationDatasource])
        }
    }

    def addTo = {
        def smartsNotificationDatasource = SmartsNotificationDatasource.get( [id:params.id] )
        if(!smartsNotificationDatasource){
            flash.message = "SmartsNotificationDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = smartsNotificationDatasource.hasMany[relationName];
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smartsNotificationDatasource:smartsNotificationDatasource, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:smartsNotificationDatasource.id)
            }
        }
    }

    def addRelation = {
        def smartsNotificationDatasource = SmartsNotificationDatasource.get( [id:params.id] )
        if(!smartsNotificationDatasource) {
            flash.message = "SmartsNotificationDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = smartsNotificationDatasource.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsNotificationDatasource.addRelation(relationMap);
                      if(smartsNotificationDatasource.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[smartsNotificationDatasource:smartsNotificationDatasource, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "SmartsNotificationDatasource ${params.id} updated"
                          redirect(action:edit,id:smartsNotificationDatasource.id)
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
        def smartsNotificationDatasource = SmartsNotificationDatasource.get( [id:params.id] )
        if(!smartsNotificationDatasource) {
            flash.message = "SmartsNotificationDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = smartsNotificationDatasource.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      smartsNotificationDatasource.removeRelation(relationMap);
                      if(smartsNotificationDatasource.hasErrors()){
                          render(view:'edit',model:[smartsNotificationDatasource:smartsNotificationDatasource])
                      }
                      else{
                          flash.message = "SmartsNotificationDatasource ${params.id} updated"
                          redirect(action:edit,id:smartsNotificationDatasource.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:smartsNotificationDatasource.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:smartsNotificationDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("SmartsNotificationDatasource")
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