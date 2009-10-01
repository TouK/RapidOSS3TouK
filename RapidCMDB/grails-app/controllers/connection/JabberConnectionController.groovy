package connection

import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 3, 2009
* Time: 11:46:37 AM
*/
class JabberConnectionController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.sort) params.sort = "name"
        [jabberConnectionList: JabberConnection.list(params)]
    }

    def show = {
        def jabberConnection = JabberConnection.get([id: params.id])

        if (!jabberConnection) {
            flash.message = "JabberConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (jabberConnection.class != JabberConnection)
            {
                def controllerName = jabberConnection.class.simpleName;
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
                return [jabberConnection: jabberConnection]
            }
        }
    }

    def delete = {
        def jabberConnection = JabberConnection.get([id: params.id])
        if (jabberConnection) {
            jabberConnection.remove()
            flash.message = "JabberConnection ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "JabberConnection not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def jabberConnection = JabberConnection.get([id: params.id])

        if (!jabberConnection) {
            flash.message = "JabberConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [jabberConnection: jabberConnection]
        }
    }


    def update = {
        def jabberConnection = JabberConnection.get([id: params.id])
        if (jabberConnection) {
            jabberConnection.update(ControllerUtils.getClassProperties(params, JabberConnection));
            if (!jabberConnection.hasErrors()) {
                flash.message = "JabberConnection ${params.id} updated"
                redirect(action: show, id: jabberConnection.id)
            }
            else {
                render(view: 'edit', model: [jabberConnection: jabberConnection])
            }
        }
        else {
            flash.message = "JabberConnection not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def jabberConnection = new JabberConnection()
        jabberConnection.properties = params
        return ['jabberConnection': jabberConnection]
    }

    def save = {
        def jabberConnection = JabberConnection.add(ControllerUtils.getClassProperties(params, JabberConnection))
        if (!jabberConnection.hasErrors()) {
            flash.message = "JabberConnection ${jabberConnection.id} created"
            redirect(action: show, id: jabberConnection.id)
        }
        else {
            render(view: 'create', model: [jabberConnection: jabberConnection])
        }
    }

    def addTo = {
        def jabberConnection = JabberConnection.get([id: params.id])
        if (!jabberConnection) {
            flash.message = "JabberConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(jabberConnection.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [jabberConnection: jabberConnection, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: jabberConnection.id)
            }
        }
    }

    def addRelation = {
        def jabberConnection = JabberConnection.get([id: params.id])
        if (!jabberConnection) {
            flash.message = "JabberConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(jabberConnection.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    jabberConnection.addRelation(relationMap);
                    if (jabberConnection.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [jabberConnection: jabberConnection, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "JabberConnection ${params.id} updated"
                        redirect(action: edit, id: jabberConnection.id)
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
        def jabberConnection = JabberConnection.get([id: params.id])
        if (!jabberConnection) {
            flash.message = "JabberConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(jabberConnection.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    jabberConnection.removeRelation(relationMap);
                    if (jabberConnection.hasErrors()) {
                        render(view: 'edit', model: [jabberConnection: jabberConnection])
                    }
                    else {
                        flash.message = "JabberConnection ${params.id} updated"
                        redirect(action: edit, id: jabberConnection.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: jabberConnection.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: jabberConnection.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("connection.JabberConnection")
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
}