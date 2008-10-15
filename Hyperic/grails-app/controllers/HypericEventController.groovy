import com.ifountain.rcmdb.domain.util.ControllerUtils;


class HypericEventController {
    def final static PROPS_TO_BE_EXCLUDED = ["id": "id", "_action_Update": "_action_Update", "controller": "controller", "action": "action"]
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [hypericEventList: HypericEvent.list(params)]
    }

    def show = {
        def hypericEvent = HypericEvent.get([id: params.id])

        if (!hypericEvent) {
            flash.message = "HypericEvent not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (hypericEvent.class != HypericEvent)
            {
                def controllerName = hypericEvent.class.simpleName;                
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
                return [hypericEvent: hypericEvent]
            }
        }
    }

    def delete = {
        def hypericEvent = HypericEvent.get([id: params.id])
        if (hypericEvent) {
            try {
                hypericEvent.remove()
                flash.message = "HypericEvent ${params.id} deleted"
                redirect(action: list)
            }
            catch (e) {
                def errors = [message(code: "default.couldnot.delete", args: [HypericEvent, hypericEvent])]
                flash.errors = errors;
                redirect(action: show, id: hypericEvent.id)
            }

        }
        else {
            flash.message = "HypericEvent not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def hypericEvent = HypericEvent.get([id: params.id])

        if (!hypericEvent) {
            flash.message = "HypericEvent not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [hypericEvent: hypericEvent]
        }
    }


    def update = {
        def hypericEvent = HypericEvent.get([id: params.id])
        if (hypericEvent) {
            hypericEvent.update(ControllerUtils.getClassProperties(params, HypericEvent));
            if (!hypericEvent.hasErrors()) {
                flash.message = "HypericEvent ${params.id} updated"
                redirect(action: show, id: hypericEvent.id)
            }
            else {
                render(view: 'edit', model: [hypericEvent: hypericEvent])
            }
        }
        else {
            flash.message = "HypericEvent not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def hypericEvent = new HypericEvent()
        hypericEvent.properties = params
        return ['hypericEvent': hypericEvent]
    }

    def save = {
        def hypericEvent = HypericEvent.add(ControllerUtils.getClassProperties(params, HypericEvent))
        if (!hypericEvent.hasErrors()) {
            flash.message = "HypericEvent ${hypericEvent.id} created"
            redirect(action: show, id: hypericEvent.id)
        }
        else {
            render(view: 'create', model: [hypericEvent: hypericEvent])
        }
    }

    def addTo = {
        def hypericEvent = HypericEvent.get([id: params.id])
        if (!hypericEvent) {
            flash.message = "HypericEvent not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(hypericEvent.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [hypericEvent: hypericEvent, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: hypericEvent.id)
            }
        }
    }

    def addRelation = {
        def hypericEvent = HypericEvent.get([id: params.id])
        if (!hypericEvent) {
            flash.message = "HypericEvent not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(hypericEvent.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    hypericEvent.addRelation(relationMap);
                    if (hypericEvent.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [hypericEvent: hypericEvent, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "HypericEvent ${params.id} updated"
                        redirect(action: edit, id: hypericEvent.id)
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
        def hypericEvent = HypericEvent.get([id: params.id])
        if (!hypericEvent) {
            flash.message = "HypericEvent not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(hypericEvent.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    hypericEvent.removeRelation(relationMap);
                    if (hypericEvent.hasErrors()) {
                        render(view: 'edit', model: [hypericEvent: hypericEvent])
                    }
                    else {
                        flash.message = "HypericEvent ${params.id} updated"
                        redirect(action: edit, id: hypericEvent.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: hypericEvent.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: hypericEvent.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("HypericEvent")
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