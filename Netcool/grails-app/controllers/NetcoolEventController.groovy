import com.ifountain.rcmdb.domain.util.ControllerUtils;


class NetcoolEventController {
    def final static PROPS_TO_BE_EXCLUDED = ["id": "id", "_action_Update": "_action_Update", "controller": "controller", "action": "action"]
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [netcoolEventList: NetcoolEvent.list(params)]
    }

    def show = {
        def netcoolEvent = NetcoolEvent.get([id: params.id])

        if (!netcoolEvent) {
            flash.message = "NetcoolEvent not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (netcoolEvent.class != NetcoolEvent)
            {
                def controllerName = netcoolEvent.class.name;
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
                return [netcoolEvent: netcoolEvent]
            }
        }
    }

    def delete = {
        def netcoolEvent = NetcoolEvent.get([id: params.id])
        if (netcoolEvent) {
            try {
                netcoolEvent.remove()
                flash.message = "NetcoolEvent ${params.id} deleted"
                redirect(action: list)
            }
            catch (e) {
                def errors = [message(code: "default.couldnot.delete", args: [NetcoolEvent, netcoolEvent])]
                flash.errors = errors;
                redirect(action: show, id: netcoolEvent.id)
            }

        }
        else {
            flash.message = "NetcoolEvent not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def netcoolEvent = NetcoolEvent.get([id: params.id])

        if (!netcoolEvent) {
            flash.message = "NetcoolEvent not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [netcoolEvent: netcoolEvent]
        }
    }


    def update = {
        def netcoolEvent = NetcoolEvent.get([id: params.id])
        if (netcoolEvent) {
            netcoolEvent.update(ControllerUtils.getClassProperties(params, NetcoolEvent));
            if (!netcoolEvent.hasErrors()) {
                flash.message = "NetcoolEvent ${params.id} updated"
                redirect(action: show, id: netcoolEvent.id)
            }
            else {
                render(view: 'edit', model: [netcoolEvent: netcoolEvent])
            }
        }
        else {
            flash.message = "NetcoolEvent not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def netcoolEvent = new NetcoolEvent()
        netcoolEvent.properties = params
        return ['netcoolEvent': netcoolEvent]
    }

    def save = {
        def netcoolEvent = NetcoolEvent.add(ControllerUtils.getClassProperties(params, NetcoolEvent))
        if (!netcoolEvent.hasErrors()) {
            flash.message = "NetcoolEvent ${netcoolEvent.id} created"
            redirect(action: show, id: netcoolEvent.id)
        }
        else {
            render(view: 'create', model: [netcoolEvent: netcoolEvent])
        }
    }

    def addTo = {
        def netcoolEvent = NetcoolEvent.get([id: params.id])
        if (!netcoolEvent) {
            flash.message = "NetcoolEvent not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = netcoolEvent.hasMany[relationName];
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [netcoolEvent: netcoolEvent, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: netcoolEvent.id)
            }
        }
    }

    def addRelation = {
        def netcoolEvent = NetcoolEvent.get([id: params.id])
        if (!netcoolEvent) {
            flash.message = "NetcoolEvent not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = netcoolEvent.hasMany[relationName];
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    netcoolEvent.addRelation(relationMap);
                    if (netcoolEvent.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [netcoolEvent: netcoolEvent, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "NetcoolEvent ${params.id} updated"
                        redirect(action: edit, id: netcoolEvent.id)
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
        def netcoolEvent = NetcoolEvent.get([id: params.id])
        if (!netcoolEvent) {
            flash.message = "NetcoolEvent not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = netcoolEvent.hasMany[relationName];
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    netcoolEvent.removeRelation(relationMap);
                    if (netcoolEvent.hasErrors()) {
                        render(view: 'edit', model: [netcoolEvent: netcoolEvent])
                    }
                    else {
                        flash.message = "NetcoolEvent ${params.id} updated"
                        redirect(action: edit, id: netcoolEvent.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: netcoolEvent.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: netcoolEvent.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("NetcoolEvent")
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