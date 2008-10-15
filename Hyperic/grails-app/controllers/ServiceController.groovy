import com.ifountain.rcmdb.domain.util.ControllerUtils;


class ServiceController {
    def final static PROPS_TO_BE_EXCLUDED = ["id": "id", "_action_Update": "_action_Update", "controller": "controller", "action": "action"]
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [serviceList: Service.list(params)]
    }

    def show = {
        def service = Service.get([id: params.id])

        if (!service) {
            flash.message = "Service not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (service.class != Service)
            {
                def controllerName = service.class.simpleName;
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
                return [service: service]
            }
        }
    }

    def delete = {
        def service = Service.get([id: params.id])
        if (service) {
            try {
                service.remove()
                flash.message = "Service ${params.id} deleted"
                redirect(action: list)
            }
            catch (e) {
                def errors = [message(code: "default.couldnot.delete", args: [Service, service])]
                flash.errors = errors;
                redirect(action: show, id: service.id)
            }

        }
        else {
            flash.message = "Service not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def service = Service.get([id: params.id])

        if (!service) {
            flash.message = "Service not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [service: service]
        }
    }


    def update = {
        def service = Service.get([id: params.id])
        if (service) {
            service.update(ControllerUtils.getClassProperties(params, Service));
            if (!service.hasErrors()) {
                flash.message = "Service ${params.id} updated"
                redirect(action: show, id: service.id)
            }
            else {
                render(view: 'edit', model: [service: service])
            }
        }
        else {
            flash.message = "Service not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def service = new Service()
        service.properties = params
        return ['service': service]
    }

    def save = {
        def service = Service.add(ControllerUtils.getClassProperties(params, Service))
        if (!service.hasErrors()) {
            flash.message = "Service ${service.id} created"
            redirect(action: show, id: service.id)
        }
        else {
            render(view: 'create', model: [service: service])
        }
    }

    def addTo = {
        def service = Service.get([id: params.id])
        if (!service) {
            flash.message = "Service not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(service.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [service: service, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: service.id)
            }
        }
    }

    def addRelation = {
        def service = Service.get([id: params.id])
        if (!service) {
            flash.message = "Service not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(service.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    service.addRelation(relationMap);
                    if (service.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [service: service, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "Service ${params.id} updated"
                        redirect(action: edit, id: service.id)
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
        def service = Service.get([id: params.id])
        if (!service) {
            flash.message = "Service not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(service.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    service.removeRelation(relationMap);
                    if (service.hasErrors()) {
                        render(view: 'edit', model: [service: service])
                    }
                    else {
                        flash.message = "Service ${params.id} updated"
                        redirect(action: edit, id: service.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: service.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: service.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("Service")
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