import com.ifountain.rcmdb.domain.util.ControllerUtils;


class NetcoolJournalController {
    def final static PROPS_TO_BE_EXCLUDED = ["id": "id", "_action_Update": "_action_Update", "controller": "controller", "action": "action"]
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [netcoolJournalList: NetcoolJournal.list(params)]
    }

    def show = {
        def netcoolJournal = NetcoolJournal.get([id: params.id])

        if (!netcoolJournal) {
            flash.message = "NetcoolJournal not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (netcoolJournal.class != NetcoolJournal)
            {
                def controllerName = netcoolJournal.class.simpleName;
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
                return [netcoolJournal: netcoolJournal]
            }
        }
    }

    def delete = {
        def netcoolJournal = NetcoolJournal.get([id: params.id])
        if (netcoolJournal) {
            try {
                netcoolJournal.remove()
                flash.message = "NetcoolJournal ${params.id} deleted"
                redirect(action: list)
            }
            catch (e) {
                def errors = [message(code: "default.couldnot.delete", args: [NetcoolJournal, netcoolJournal])]
                flash.errors = errors;
                redirect(action: show, id: netcoolJournal.id)
            }

        }
        else {
            flash.message = "NetcoolJournal not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def netcoolJournal = NetcoolJournal.get([id: params.id])

        if (!netcoolJournal) {
            flash.message = "NetcoolJournal not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [netcoolJournal: netcoolJournal]
        }
    }


    def update = {
        def netcoolJournal = NetcoolJournal.get([id: params.id])
        if (netcoolJournal) {
            netcoolJournal.update(ControllerUtils.getClassProperties(params, NetcoolJournal));
            if (!netcoolJournal.hasErrors()) {
                flash.message = "NetcoolJournal ${params.id} updated"
                redirect(action: show, id: netcoolJournal.id)
            }
            else {
                render(view: 'edit', model: [netcoolJournal: netcoolJournal])
            }
        }
        else {
            flash.message = "NetcoolJournal not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def netcoolJournal = new NetcoolJournal()
        netcoolJournal.properties = params
        return ['netcoolJournal': netcoolJournal]
    }

    def save = {
        def netcoolJournal = NetcoolJournal.add(ControllerUtils.getClassProperties(params, NetcoolJournal))
        if (!netcoolJournal.hasErrors()) {
            flash.message = "NetcoolJournal ${netcoolJournal.id} created"
            redirect(action: show, id: netcoolJournal.id)
        }
        else {
            render(view: 'create', model: [netcoolJournal: netcoolJournal])
        }
    }

    def addTo = {
        def netcoolJournal = NetcoolJournal.get([id: params.id])
        if (!netcoolJournal) {
            flash.message = "NetcoolJournal not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(netcoolJournal.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [netcoolJournal: netcoolJournal, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: netcoolJournal.id)
            }
        }
    }

    def addRelation = {
        def netcoolJournal = NetcoolJournal.get([id: params.id])
        if (!netcoolJournal) {
            flash.message = "NetcoolJournal not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(netcoolJournal.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    netcoolJournal.addRelation(relationMap);
                    if (netcoolJournal.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [netcoolJournal: netcoolJournal, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "NetcoolJournal ${params.id} updated"
                        redirect(action: edit, id: netcoolJournal.id)
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
        def netcoolJournal = NetcoolJournal.get([id: params.id])
        if (!netcoolJournal) {
            flash.message = "NetcoolJournal not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(netcoolJournal.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    netcoolJournal.removeRelation(relationMap);
                    if (netcoolJournal.hasErrors()) {
                        render(view: 'edit', model: [netcoolJournal: netcoolJournal])
                    }
                    else {
                        flash.message = "NetcoolJournal ${params.id} updated"
                        redirect(action: edit, id: netcoolJournal.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: netcoolJournal.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: netcoolJournal.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("NetcoolJournal")
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