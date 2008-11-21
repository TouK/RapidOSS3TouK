import com.ifountain.rcmdb.domain.util.ControllerUtils;


class ServerController {
    def final static PROPS_TO_BE_EXCLUDED = ["id": "id", "_action_Update": "_action_Update", "controller": "controller", "action": "action"]
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [serverList: Server.list(params)]
    }

    def show = {
        def server = Server.get([id: params.id])

        if (!server) {
            flash.message = "Server not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (server.class != Server)
            {
                def controllerName = server.class.simpleName;
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
                return [server: server]
            }
        }
    }

    def delete = {
        def server = Server.get([id: params.id])
        if (server) {
            try {
                server.remove()
                flash.message = "Server ${params.id} deleted"
                redirect(action: list)
            }
            catch (e) {
                def errors = [message(code: "default.couldnot.delete", args: [Server, server])]
                flash.errors = errors;
                redirect(action: show, id: server.id)
            }

        }
        else {
            flash.message = "Server not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def server = Server.get([id: params.id])

        if (!server) {
            flash.message = "Server not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [server: server]
        }
    }


    def update = {
        def server = Server.get([id: params.id])
        if (server) {
            server.update(ControllerUtils.getClassProperties(params, Server));
            if (!server.hasErrors()) {
                flash.message = "Server ${params.id} updated"
                redirect(action: show, id: server.id)
            }
            else {
                render(view: 'edit', model: [server: server])
            }
        }
        else {
            flash.message = "Server not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def server = new Server()
        server.properties = params
        return ['server': server]
    }

    def save = {
        def server = Server.add(ControllerUtils.getClassProperties(params, Server))
        if (!server.hasErrors()) {
            flash.message = "Server ${server.id} created"
            redirect(action: show, id: server.id)
        }
        else {
            render(view: 'create', model: [server: server])
        }
    }

    def addTo = {
        def server = Server.get([id: params.id])
        if (!server) {
            flash.message = "Server not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(server.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [server: server, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: server.id)
            }
        }
    }

    def addRelation = {
        def server = Server.get([id: params.id])
        if (!server) {
            flash.message = "Server not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(server.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    server.addRelation(relationMap);
                    if (server.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [server: server, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "Server ${params.id} updated"
                        redirect(action: edit, id: server.id)
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
        def server = Server.get([id: params.id])
        if (!server) {
            flash.message = "Server not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(server.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    server.removeRelation(relationMap);
                    if (server.hasErrors()) {
                        render(view: 'edit', model: [server: server])
                    }
                    else {
                        flash.message = "Server ${params.id} updated"
                        redirect(action: edit, id: server.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: server.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: server.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("Server")
        if (modelClass)
        {
            try
            {

                modelClass.metaClass.invokeStaticMethod(modelClass, "reloadOperations", [] as Object[]);
                flash.message = "Model operations reloaded"
                if (params.targetURI) {
                    redirect(uri: params.targetURI);
                }
                else {
                    redirect(action: list)
                }
            } catch (t)
            {
                flash.message = "Exception occurred while reloading model operations Reason:${t.toString()}"
                if (params.targetURI) {
                    redirect(uri: params.targetURI);
                }
                else {
                    redirect(action: list)
                }
            }
        }
        else
        {
            flash.message = "Model currently not loaded by application. You should reload application."
            if (params.targetURI) {
                redirect(uri: params.targetURI);
            }
            else {
                redirect(action: list)
            }
        }
    }
}