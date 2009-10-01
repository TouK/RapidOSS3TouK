package connection

import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 9, 2009
* Time: 4:43:54 PM
*/
class AolConnectionController {
  // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def index = {redirect(action: list, params: params)}

    def list = {
        if (!params.sort) params.sort = "name"
        [aolConnectionList: AolConnection.list(params)]
    }

    def show = {
        def aolConnection = AolConnection.get([id: params.id])

        if (!aolConnection) {
            flash.message = "AolConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (aolConnection.class != AolConnection)
            {
                def controllerName = aolConnection.class.simpleName;
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
                return [aolConnection: aolConnection]
            }
        }
    }

    def delete = {
        def aolConnection = AolConnection.get([id: params.id])
        if (aolConnection) {
            aolConnection.remove()
            flash.message = "AolConnection ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "AolConnection not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def aolConnection = AolConnection.get([id: params.id])

        if (!aolConnection) {
            flash.message = "AolConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [aolConnection: aolConnection]
        }
    }


    def update = {
        def aolConnection = AolConnection.get([id: params.id])
        if (aolConnection) {
            aolConnection.update(ControllerUtils.getClassProperties(params, AolConnection));
            if (!aolConnection.hasErrors()) {
                flash.message = "AolConnection ${params.id} updated"
                redirect(action: show, id: aolConnection.id)
            }
            else {
                render(view: 'edit', model: [aolConnection: aolConnection])
            }
        }
        else {
            flash.message = "AolConnection not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def aolConnection = new AolConnection()
        aolConnection.properties = params
        return ['aolConnection': aolConnection]
    }

    def save = {
        def aolConnection = AolConnection.add(ControllerUtils.getClassProperties(params, AolConnection))
        if (!aolConnection.hasErrors()) {
            flash.message = "AolConnection ${aolConnection.id} created"
            redirect(action: show, id: aolConnection.id)
        }
        else {
            render(view: 'create', model: [aolConnection: aolConnection])
        }
    }

    def addTo = {
        def aolConnection = AolConnection.get([id: params.id])
        if (!aolConnection) {
            flash.message = "AolConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(aolConnection.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [aolConnection: aolConnection, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: aolConnection.id)
            }
        }
    }

    def addRelation = {
        def aolConnection = AolConnection.get([id: params.id])
        if (!aolConnection) {
            flash.message = "AolConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(aolConnection.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    aolConnection.addRelation(relationMap);
                    if (aolConnection.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [aolConnection: aolConnection, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "AolConnection ${params.id} updated"
                        redirect(action: edit, id: aolConnection.id)
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
        def aolConnection = AolConnection.get([id: params.id])
        if (!aolConnection) {
            flash.message = "AolConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(aolConnection.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    aolConnection.removeRelation(relationMap);
                    if (aolConnection.hasErrors()) {
                        render(view: 'edit', model: [aolConnection: aolConnection])
                    }
                    else {
                        flash.message = "AolConnection ${params.id} updated"
                        redirect(action: edit, id: aolConnection.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: aolConnection.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: aolConnection.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("connection.AolConnection")
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