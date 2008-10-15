package connection

import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.text.SimpleDateFormat;
class LdapConnectionController {
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ ldapConnectionList: LdapConnection.list( params ) ]
    }

    def show = {
        def ldapConnection = LdapConnection.get([id:params.id])

        if(!ldapConnection) {
            flash.message = "LdapConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(ldapConnection.class != LdapConnection)
            {
                def controllerName = ldapConnection.class.simpleName;
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
                return [ ldapConnection : ldapConnection ]
            }
        }
    }

    def delete = {
        def ldapConnection = LdapConnection.get( [id:params.id])
        if(ldapConnection) {
            try{
                ldapConnection.remove()
                flash.message = "LdapConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[LdapConnection, ldapConnection])]
                flash.errors = errors;
                redirect(action:show, id:ldapConnection.id)
            }

        }
        else {
            flash.message = "LdapConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def ldapConnection = LdapConnection.get( [id:params.id] )

        if(!ldapConnection) {
            flash.message = "LdapConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ ldapConnection : ldapConnection ]
        }
    }


    def update = {
        def ldapConnection = LdapConnection.get( [id:params.id] )
        if(ldapConnection) {
            ldapConnection.update(ControllerUtils.getClassProperties(params, LdapConnection));
            if(!ldapConnection.hasErrors()) {
                flash.message = "LdapConnection ${params.id} updated"
                redirect(action:show,id:ldapConnection.id)
            }
            else {
                render(view:'edit',model:[ldapConnection:ldapConnection])
            }
        }
        else {
            flash.message = "LdapConnection not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def ldapConnection = new LdapConnection()
        ldapConnection.properties = params
        return ['ldapConnection':ldapConnection]
    }

    def save = {
        def ldapConnection = LdapConnection.add(ControllerUtils.getClassProperties(params, LdapConnection))
        if(!ldapConnection.hasErrors()) {
            flash.message = "LdapConnection ${ldapConnection.id} created"
            redirect(action:show,id:ldapConnection.id)
        }
        else {
            render(view:'create',model:[ldapConnection:ldapConnection])
        }
    }

    def addTo = {
        def ldapConnection = LdapConnection.get( [id:params.id] )
        if(!ldapConnection){
            flash.message = "LdapConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(ldapConnection.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [ldapConnection:ldapConnection, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:ldapConnection.id)
            }
        }
    }

    def addRelation = {
        def ldapConnection = LdapConnection.get( [id:params.id] )
        if(!ldapConnection) {
            flash.message = "LdapConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(ldapConnection.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      ldapConnection.addRelation(relationMap);
                      if(ldapConnection.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[ldapConnection:ldapConnection, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "LdapConnection ${params.id} updated"
                          redirect(action:edit,id:ldapConnection.id)
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
        def ldapConnection = LdapConnection.get( [id:params.id] )
        if(!ldapConnection) {
            flash.message = "LdapConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(ldapConnection.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      ldapConnection.removeRelation(relationMap);
                      if(ldapConnection.hasErrors()){
                          render(view:'edit',model:[ldapConnection:ldapConnection])
                      }
                      else{
                          flash.message = "LdapConnection ${params.id} updated"
                          redirect(action:edit,id:ldapConnection.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:ldapConnection.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:ldapConnection.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("connection.LdapConnection")
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