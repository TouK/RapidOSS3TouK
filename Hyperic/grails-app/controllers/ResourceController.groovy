import com.ifountain.rcmdb.domain.util.ControllerUtils;


class ResourceController {
    def final static PROPS_TO_BE_EXCLUDED = ["id": "id", "_action_Update": "_action_Update", "controller": "controller", "action": "action"]
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [resourceList: Resource.list(params)]
    }

    def show = {
        def resource = Resource.get([id: params.id])

        if (!resource) {
            flash.message = "Resource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (resource.class != Resource)
            {
                def controllerName = resource.class.name;
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
                return [resource: resource]
            }
        }
    }

    def delete = {
        def resource = Resource.get([id: params.id])
        if (resource) {
            try {
                resource.remove()
                flash.message = "Resource ${params.id} deleted"
                redirect(action: list)
            }
            catch (e) {
                def errors = [message(code: "default.couldnot.delete", args: [Resource, resource])]
                flash.errors = errors;
                redirect(action: show, id: resource.id)
            }

        }
        else {
            flash.message = "Resource not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def resource = Resource.get([id: params.id])

        if (!resource) {
            flash.message = "Resource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [resource: resource]
        }
    }


    def update = {
        def resource = Resource.get([id: params.id])
        if (resource) {
            resource.update(ControllerUtils.getClassProperties(params, Resource));
            if (!resource.hasErrors()) {
                flash.message = "Resource ${params.id} updated"
                redirect(action: show, id: resource.id)
            }
            else {
                render(view: 'edit', model: [resource: resource])
            }
        }
        else {
            flash.message = "Resource not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def resource = new Resource()
        resource.properties = params
        return ['resource': resource]
    }

    def save = {
        def resource = Resource.add(ControllerUtils.getClassProperties(params, Resource))
        if (!resource.hasErrors()) {
            flash.message = "Resource ${resource.id} created"
            redirect(action: show, id: resource.id)
        }
        else {
            render(view: 'create', model: [resource: resource])
        }
    }

    def addTo = {
        def resource = Resource.get([id: params.id])
        if (!resource) {
            flash.message = "Resource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(resource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [resource: resource, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: resource.id)
            }
        }
    }

    def addRelation = {
        def resource = Resource.get([id: params.id])
        if (!resource) {
            flash.message = "Resource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(resource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    resource.addRelation(relationMap);
                    if (resource.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [resource: resource, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "Resource ${params.id} updated"
                        redirect(action: edit, id: resource.id)
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
        def resource = Resource.get([id: params.id])
        if (!resource) {
            flash.message = "Resource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(resource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    resource.removeRelation(relationMap);
                    if (resource.hasErrors()) {
                        render(view: 'edit', model: [resource: resource])
                    }
                    else {
                        flash.message = "Resource ${params.id} updated"
                        redirect(action: edit, id: resource.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: resource.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: resource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("Resource")
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