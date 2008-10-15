import com.ifountain.rcmdb.domain.util.ControllerUtils;


class PlatformController {
    def final static PROPS_TO_BE_EXCLUDED = ["id": "id", "_action_Update": "_action_Update", "controller": "controller", "action": "action"]
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [platformList: Platform.list(params)]
    }

    def show = {
        def platform = Platform.get([id: params.id])

        if (!platform) {
            flash.message = "Platform not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (platform.class != Platform)
            {
                def controllerName = platform.class.simpleName;
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
                return [platform: platform]
            }
        }
    }

    def delete = {
        def platform = Platform.get([id: params.id])
        if (platform) {
            try {
                platform.remove()
                flash.message = "Platform ${params.id} deleted"
                redirect(action: list)
            }
            catch (e) {
                def errors = [message(code: "default.couldnot.delete", args: [Platform, platform])]
                flash.errors = errors;
                redirect(action: show, id: platform.id)
            }

        }
        else {
            flash.message = "Platform not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def platform = Platform.get([id: params.id])

        if (!platform) {
            flash.message = "Platform not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [platform: platform]
        }
    }


    def update = {
        def platform = Platform.get([id: params.id])
        if (platform) {
            platform.update(ControllerUtils.getClassProperties(params, Platform));
            if (!platform.hasErrors()) {
                flash.message = "Platform ${params.id} updated"
                redirect(action: show, id: platform.id)
            }
            else {
                render(view: 'edit', model: [platform: platform])
            }
        }
        else {
            flash.message = "Platform not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def platform = new Platform()
        platform.properties = params
        return ['platform': platform]
    }

    def save = {
        def platform = Platform.add(ControllerUtils.getClassProperties(params, Platform))
        if (!platform.hasErrors()) {
            flash.message = "Platform ${platform.id} created"
            redirect(action: show, id: platform.id)
        }
        else {
            render(view: 'create', model: [platform: platform])
        }
    }

    def addTo = {
        def platform = Platform.get([id: params.id])
        if (!platform) {
            flash.message = "Platform not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(platform.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [platform: platform, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: platform.id)
            }
        }
    }

    def addRelation = {
        def platform = Platform.get([id: params.id])
        if (!platform) {
            flash.message = "Platform not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(platform.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    platform.addRelation(relationMap);
                    if (platform.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [platform: platform, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "Platform ${params.id} updated"
                        redirect(action: edit, id: platform.id)
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
        def platform = Platform.get([id: params.id])
        if (!platform) {
            flash.message = "Platform not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(platform.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    platform.removeRelation(relationMap);
                    if (platform.hasErrors()) {
                        render(view: 'edit', model: [platform: platform])
                    }
                    else {
                        flash.message = "Platform ${params.id} updated"
                        redirect(action: edit, id: platform.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: platform.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: platform.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("Platform")
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