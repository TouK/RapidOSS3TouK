package connector
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 12, 2009
 * Time: 2:54:11 PM
 * To change this template use File | Settings | File Templates.
 */

import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import connection.EmailConnection
import datasource.EmailDatasource;
import connection.Connection;
import datasource.BaseDatasource;

class EmailConnectorController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {

    }

    def show = {
        EmailConnector emailConnector = EmailConnector.get([id: params.id])
        if (!emailConnector) {
            flash.message = "EmailConnector not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [emailConnector: emailConnector]
        }
    }

    def delete = {
        def emailConnector = EmailConnector.get([id: params.id])
        if (emailConnector) {
            try {
                emailConnector.emailConnection.remove();
                emailConnector.remove()
                flash.message = "EmailConnector ${params.id} deleted"
                redirect(action: list);
            }
            catch (e) {
                addError("default.couldnot.delete", [EmailConnector, emailConnector])
                flash.errors = this.errors;
                redirect(action: show, id: emailConnector.id)
            }

        }
        else {
            flash.message = "EmailConnector not found with id ${params.id}"
            redirect(action: list);
        }
    }


    def create = {
        def emailConnector = new EmailConnector()
        emailConnector.properties = params
        return ['emailConnector': emailConnector, emailConnection: new EmailConnection(), emailDatasource: new EmailDatasource()]
    }
    def save = {
        def emailConnector = EmailConnector.add(ControllerUtils.getClassProperties(params, EmailConnector))
        if (!emailConnector.hasErrors()) {
            def emailConnectionParams=ControllerUtils.getClassProperties(params, EmailConnection);
            emailConnectionParams.name=EmailConnector.getEmailConnectionName(emailConnector.name)
            def emailConnection = EmailConnection.add(emailConnectionParams);
            if (!emailConnection.hasErrors()) {
                emailConnector.addRelation(emailConnection:emailConnection)
                def emailDatasource = EmailDatasource.add(name:EmailConnector.getEmailDatasourceName(emailConnector.name), connection:emailConnection);
                if(!emailDatasource.hasErrors()){
                    emailConnector.addRelation(emailDatasource:emailDatasource)
                    flash.message = "EmailConnector ${emailConnector.name} created"                    
                    redirect(action:show, id:emailConnector.id);
                }
                else{
                    emailConnection.remove();
                    emailConnector.remove();
                    render(view: 'create', model: [emailConnector: emailConnector, emailConnection:emailConnection, emailDatasource:emailDatasource])
                }

            }
            else {
               emailConnector.remove();
               render(view: 'create', model: [emailConnector: emailConnector, emailConnection:emailConnection, emailDatasouce:new EmailDatasource()])
            }
        }
        else {
            render(view: 'create', model: [emailConnector: emailConnector, emailConnection:new EmailConnection(), emailDatasouce:new EmailDatasource()])
        }

    }
    def edit = {
        def emailConnector = EmailConnector.get([id: params.id])

        if (!emailConnector) {
            flash.message = "EmailConnector not found with id ${params.id}"
            redirect(action: 'list');
        }
        else {
            return [emailConnector: emailConnector, emailConnection: emailConnector.emailConnection, emailDatasource: emailConnector.emailDatasource]
        }
    }
    def update = {
        def emailConnector = EmailConnector.get([id: params.id])
        if (emailConnector) {
            if(emailConnector.name != params.name){

                def willSendError = false;
                def emailConnection = emailConnector.emailConnection;
                def emailDatasource = emailConnector.emailDatasource;


                
                def conn = Connection.get(name:EmailConnector.getEmailConnectionName(params.name));
                if(conn){
                    emailConnection.update(name:EmailConnector.getEmailConnectionName(params.name)); //this update will generate error if connection with this name exists
                    willSendError = true;
                }
                def emailDs = BaseDatasource.get(name:EmailConnector.getEmailDatasourceName(params.name));
                if(emailDs){
                    emailDatasource.update(name:EmailConnector.getEmailDatasourceName(params.name)) //this update will generate error if connection with this name exists
                    willSendError = true;
                }

                if(willSendError){
                    render(view: 'edit', model: [emailConnector: emailConnector, emailConnection:emailConnection,  emailDatasource:emailDatasource])
                    return;
                }
            }

            emailConnector.update(ControllerUtils.getClassProperties(params, EmailConnector));
            if(!emailConnector.hasErrors()){
                def emailConnection = emailConnector.emailConnection;
                def emailConnectionParams=ControllerUtils.getClassProperties(params, EmailConnection)
                emailConnectionParams.name=EmailConnector.getEmailConnectionName(params.name)
                emailConnection.update(emailConnectionParams)                
                if(!emailConnection.hasErrors()){                   
                   emailConnector.emailDatasource.update(name:EmailConnector.getEmailDatasourceName(params.name));
                   if(!emailConnector.emailDatasource.hasErrors()){
                       flash.message = "EmailConnector ${params.id} updated"
                       redirect(action:show, id:emailConnector.id);
                   }
                   else
                   {
                      render(view: 'edit', model: [emailConnector: emailConnector, emailConnection:emailConnection, emailDatasource:emailConnector.emailDatasource]) 
                   }
                }
                else{
                    render(view: 'edit', model: [emailConnector: emailConnector, emailConnection:emailConnection, emailDatasource:emailConnector.emailDatasource])
                }
            }
            else
            {
                 render(view: 'edit', model: [emailConnector: emailConnector, emailConnection:emailConnector.emailConnection, emailDatasource:emailConnector.emailDatasource])
            }

        }
        else {
            flash.message = "EmailConnector not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def addTo = {
        def emailConnector = EmailConnector.get([id: params.id])
        if (!emailConnector) {
            flash.message = "EmailConnector not found with id ${params.id}"
            redirect(action: 'list',params:[:]);
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = DomainClassUtils.getStaticMapVariable(EmailConnector, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [emailConnector: emailConnector, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: emailConnector.id)
            }
        }
    }



    def addRelation = {
        def emailConnector = EmailConnector.get([id: params.id])
        if (!emailConnector) {
            flash.message = "EmailConnector not found with id ${params.id}"
            redirect(action: 'list',params:[:]);
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(EmailConnector, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    emailConnector.addRelation(relationMap);
                    if (emailConnector.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [emailConnector: emailConnector, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "EmailConnector ${params.id} updated"
                        redirect(action: edit, id: emailConnector.id)
                    }

                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: addTo, id: params.id, relationName: relationName)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: addTo, id: params.id, relationName: relationName)
            }
        }
    }

    def removeRelation = {
        def emailConnector = EmailConnector.get([id: params.id])
        if (!emailConnector) {
            flash.message = "EmailConnector not found with id ${params.id}"
            redirect(action: 'list',params:[:]);
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(EmailConnector, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    emailConnector.removeRelation(relationMap);
                    if (emailConnector.hasErrors()) {
                        render(view: 'edit', model: [emailConnector: emailConnector])
                    }
                    else {
                        flash.message = "EmailConnector ${params.id} updated"
                        redirect(action: edit, id: emailConnector.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: emailConnector.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: emailConnector.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("connector.EmailConnector")
        if (modelClass)
        {
            try
            {

                modelClass.metaClass.invokeStaticMethod(modelClass, "reloadOperations", [] as Object[]);
                flash.message = "Model operations reloaded"
                redirect(action: 'list',params:[:]);
            } catch (t)
            {
                flash.message = "Exception occurred while reloading model operations Reason:${t.toString()}"
                redirect(action: list)
            }
        }
        else
        {
            flash.message = "Model currently not loaded by application. You should reload application."
            redirect(action: list)
        }
    }

    def test = {
        def emailConnector = EmailConnector.get( [id:params.id] )

        if(!emailConnector) {            
            flash.message = "EmailConnector not found with id ${params.id}"
            redirect(action:list)
        }        
        else {
            def emailConnection=emailConnector.emailConnection;
            if(!emailConnection)
            {
                flash.message = "emailConnection of emailConnector not found"
                redirect(action:list)
            }
            else{
                try
                {
                    emailConnection.checkConnection();
                    flash.message = "Successfully connected to server."
                }catch(Throwable t)
                {
                    addError("connection.test.exception", [emailConnection.name, t.toString()]);
                    log.warn("", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                    flash.errors = this.errors;
                }
                redirect(action:list);
            }
            

        }
    }
}