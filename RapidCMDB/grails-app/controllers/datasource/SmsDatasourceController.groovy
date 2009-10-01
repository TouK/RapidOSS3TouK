package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 5, 2009
* Time: 2:36:05 PM
*/
class SmsDatasourceController {
     def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']
    def list = {
        if (!params.sort) params.sort = "name"
        [smsDatasourceList: SmsDatasource.list(params)]
    }

    def show = {
        def smsDatasource = SmsDatasource.get([id: params.id])

        if (!smsDatasource) {
            flash.message = "SmsDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (smsDatasource.class != SmsDatasource)
            {
                def controllerName = smsDatasource.class.simpleName;
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
                return [smsDatasource: smsDatasource]
            }
        }
    }

    def delete = {
        def smsDatasource = SmsDatasource.get([id: params.id])
        if (smsDatasource) {
            smsDatasource.remove()
            flash.message = "SmsDatasource ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "SmsDatasource not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def smsDatasource = SmsDatasource.get([id: params.id])

        if (!smsDatasource) {
            flash.message = "SmsDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [smsDatasource: smsDatasource]
        }
    }


    def update = {
        def smsDatasource = SmsDatasource.get([id: params.id])
        if (smsDatasource) {
            smsDatasource.update(ControllerUtils.getClassProperties(params, SmsDatasource));
            if (!smsDatasource.hasErrors()) {
                flash.message = "SmsDatasource ${params.id} updated"
                redirect(action: show, id: smsDatasource.id)
            }
            else {
                render(view: 'edit', model: [smsDatasource: smsDatasource])
            }
        }
        else {
            flash.message = "SmsDatasource not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def smsDatasource = new SmsDatasource()
        smsDatasource.properties = params
        return ['smsDatasource': smsDatasource]
    }

    def save = {
        def smsDatasource = SmsDatasource.add(ControllerUtils.getClassProperties(params, SmsDatasource))
        if (!smsDatasource.hasErrors()) {
            flash.message = "SmsDatasource ${smsDatasource.id} created"
            redirect(action: show, id: smsDatasource.id)
        }
        else {
            render(view: 'create', model: [smsDatasource: smsDatasource])
        }
    }

    def addTo = {
        def smsDatasource = SmsDatasource.get([id: params.id])
        if (!smsDatasource) {
            flash.message = "SmsDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(smsDatasource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [smsDatasource: smsDatasource, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: smsDatasource.id)
            }
        }
    }

    def addRelation = {
        def smsDatasource = SmsDatasource.get([id: params.id])
        if (!smsDatasource) {
            flash.message = "SmsDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(smsDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    smsDatasource.addRelation(relationMap);
                    if (smsDatasource.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [smsDatasource: smsDatasource, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "SmsDatasource ${params.id} updated"
                        redirect(action: edit, id: smsDatasource.id)
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
        def smsDatasource = SmsDatasource.get([id: params.id])
        if (!smsDatasource) {
            flash.message = "SmsDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(smsDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    smsDatasource.removeRelation(relationMap);
                    if (smsDatasource.hasErrors()) {
                        render(view: 'edit', model: [smsDatasource: smsDatasource])
                    }
                    else {
                        flash.message = "SmsDatasource ${params.id} updated"
                        redirect(action: edit, id: smsDatasource.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: smsDatasource.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: smsDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("datasource.SmsDatasource")
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