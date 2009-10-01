package connection
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: May 21, 2009
 * Time: 1:51:25 PM
 */
import com.ifountain.rcmdb.domain.util.ControllerUtils

class SametimeConnectionController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.sort) params.sort = "name"
        [sametimeConnectionList: SametimeConnection.list(params)]
    }

    def show = {
        def sametimeConnection = SametimeConnection.get([id: params.id])

        if (!sametimeConnection) {
            flash.message = "SametimeConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (sametimeConnection.class != SametimeConnection)
            {
                def controllerName = sametimeConnection.class.simpleName;
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
                return [sametimeConnection: sametimeConnection]
            }
        }
    }

    def delete = {
        def sametimeConnection = SametimeConnection.get([id: params.id])
        if (sametimeConnection) {
            sametimeConnection.remove()
            flash.message = "SametimeConnection ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "SametimeConnection not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def sametimeConnection = SametimeConnection.get([id: params.id])

        if (!sametimeConnection) {
            flash.message = "SametimeConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [sametimeConnection: sametimeConnection]
        }
    }


    def update = {
        def sametimeConnection = SametimeConnection.get([id: params.id])
        if (sametimeConnection) {
            sametimeConnection.update(ControllerUtils.getClassProperties(params, SametimeConnection));
            if (!sametimeConnection.hasErrors()) {
                flash.message = "SametimeConnection ${params.id} updated"
                redirect(action: show, id: sametimeConnection.id)
            }
            else {
                render(view: 'edit', model: [sametimeConnection: sametimeConnection])
            }
        }
        else {
            flash.message = "SametimeConnection not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def sametimeConnection = new SametimeConnection()
        sametimeConnection.properties = params
        return ['sametimeConnection': sametimeConnection]
    }

    def save = {
        def sametimeConnection = SametimeConnection.add(ControllerUtils.getClassProperties(params, SametimeConnection))
        if (!sametimeConnection.hasErrors()) {
            flash.message = "SametimeConnection ${sametimeConnection.id} created"
            redirect(action: show, id: sametimeConnection.id)
        }
        else {
            render(view: 'create', model: [sametimeConnection: sametimeConnection])
        }
    }

    def addTo = {
        def sametimeConnection = SametimeConnection.get([id: params.id])
        if (!sametimeConnection) {
            flash.message = "SametimeConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(sametimeConnection.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [sametimeConnection: sametimeConnection, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: sametimeConnection.id)
            }
        }
    }

    def addRelation = {
        def sametimeConnection = SametimeConnection.get([id: params.id])
        if (!sametimeConnection) {
            flash.message = "SametimeConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(sametimeConnection.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    sametimeConnection.addRelation(relationMap);
                    if (sametimeConnection.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [sametimeConnection: sametimeConnection, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "SametimeConnection ${params.id} updated"
                        redirect(action: edit, id: sametimeConnection.id)
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
        def sametimeConnection = SametimeConnection.get([id: params.id])
        if (!sametimeConnection) {
            flash.message = "SametimeConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(sametimeConnection.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    sametimeConnection.removeRelation(relationMap);
                    if (sametimeConnection.hasErrors()) {
                        render(view: 'edit', model: [sametimeConnection: sametimeConnection])
                    }
                    else {
                        flash.message = "SametimeConnection ${params.id} updated"
                        redirect(action: edit, id: sametimeConnection.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: sametimeConnection.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: sametimeConnection.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("connection.SametimeConnection")
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