package connection
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: May 13, 2009
 * Time: 3:48:30 PM
 */
import com.ifountain.rcmdb.domain.util.ControllerUtils

class RepositoryConnectionController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [repositoryConnectionList: RepositoryConnection.search("alias:*", params).results]
    }

    def show = {
        def repositoryConnection = RepositoryConnection.get([id: params.id])

        if (!repositoryConnection) {
            flash.message = "RepositoryConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (repositoryConnection.class != RepositoryConnection)
            {
                def controllerName = repositoryConnection.class.simpleName;
                if (controllerName.length() == 1)
                {
                    controllerName = controllerName.toLowerCase();
                }
                else
                {
                    controllerName = controllerName.substring(0, 1).toLowerCase() + controllerName.substring(1);
                }
                redirect(action: show, controller: controllerName, id: params.id)
            }
            else
            {
                return [repositoryConnection: repositoryConnection]
            }
        }
    }

    def delete = {
        def repositoryConnection = RepositoryConnection.get([id: params.id])
        if (repositoryConnection) {
            repositoryConnection.remove()
            flash.message = "RepositoryConnection ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "RepositoryConnection not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def repositoryConnection = RepositoryConnection.get([id: params.id])

        if (!repositoryConnection) {
            flash.message = "RepositoryConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [repositoryConnection: repositoryConnection]
        }
    }


    def update = {
        def repositoryConnection = RepositoryConnection.get([id: params.id])
        if (repositoryConnection) {
            repositoryConnection.update(ControllerUtils.getClassProperties(params, RepositoryConnection));
            if (!repositoryConnection.hasErrors()) {
                flash.message = "RepositoryConnection ${params.id} updated"
                redirect(action: show, id: repositoryConnection.id);
            }
            else {
                render(view: 'edit', model: [repositoryConnection: repositoryConnection])
            }
        }
        else {
            flash.message = "RepositoryConnection not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def repositoryConnection = new RepositoryConnection()
        repositoryConnection.properties = params
        return ['repositoryConnection': repositoryConnection]
    }

    def save = {
        def repositoryConnection = RepositoryConnection.add(ControllerUtils.getClassProperties(params, RepositoryConnection))
        if (!repositoryConnection.hasErrors()) {
            flash.message = "RepositoryConnection ${repositoryConnection.id} created"
            redirect(action: show, id: repositoryConnection.id)
        }
        else {
            render(view: 'create', model: [repositoryConnection: repositoryConnection])
        }
    }

    def addTo = {
        def repositoryConnection = RepositoryConnection.get([id: params.id])
        if (!repositoryConnection) {
            flash.message = "RepositoryConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(repositoryConnection.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [repositoryConnection: repositoryConnection, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: repositoryConnection.id)
            }
        }
    }

    def addRelation = {
        def repositoryConnection = RepositoryConnection.get([id: params.id])
        if (!repositoryConnection) {
            flash.message = "RepositoryConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(repositoryConnection.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    repositoryConnection.addRelation(relationMap);
                    if (repositoryConnection.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [repositoryConnection: repositoryConnection, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "RepositoryConnection ${params.id} updated"
                        redirect(action: edit, id: repositoryConnection.id)
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
        def repositoryConnection = RepositoryConnection.get([id: params.id])
        if (!repositoryConnection) {
            flash.message = "RepositoryConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(repositoryConnection.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    repositoryConnection.removeRelation(relationMap);
                    if (repositoryConnection.hasErrors()) {
                        render(view: 'edit', model: [repositoryConnection: repositoryConnection])
                    }
                    else {
                        flash.message = "RepositoryConnection ${params.id} updated"
                        redirect(action: edit, id: repositoryConnection.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: repositoryConnection.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: repositoryConnection.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("connection.RepositoryConnection")
        if (modelClass)
        {
            try
            {

                modelClass.metaClass.invokeStaticMethod(modelClass, "reloadOperations", [] as Object[]);
                flash.message = "Model operations reloaded"
                redirect(action: list)
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
        def repositoryConnection = RepositoryConnection.get([id: params.id])

        if (!repositoryConnection) {
            flash.message = "RepositoryConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            try
            {
                repositoryConnection.checkConnection();
                flash.message = "Successfully connected to server."
            } catch (Throwable t)
            {
                addError("connection.test.exception", [repositoryConnection.name, t.toString()]);
                log.warn(this.errors.getAllErrors()[0]?.toString(), org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                flash.errors = this.errors;
            }
            redirect(action: list);
        }
    }
}