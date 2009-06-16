package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 9, 2009
* Time: 4:45:02 PM
*/
class AolDatasourceController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']
    def list = {
        if (!params.max) params.max = 10
        [aolDatasourceList: AolDatasource.list(params)]
    }

    def show = {
        def aolDatasource = AolDatasource.get([id: params.id])

        if (!aolDatasource) {
            flash.message = "AolDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (aolDatasource.class != AolDatasource)
            {
                def controllerName = aolDatasource.class.simpleName;
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
                return [aolDatasource: aolDatasource]
            }
        }
    }

    def delete = {
        def aolDatasource = AolDatasource.get([id: params.id])
        if (aolDatasource) {
            aolDatasource.remove()
            flash.message = "AolDatasource ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "AolDatasource not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def aolDatasource = AolDatasource.get([id: params.id])

        if (!aolDatasource) {
            flash.message = "AolDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [aolDatasource: aolDatasource]
        }
    }


    def update = {
        def aolDatasource = AolDatasource.get([id: params.id])
        if (aolDatasource) {
            aolDatasource.update(ControllerUtils.getClassProperties(params, AolDatasource));
            if (!aolDatasource.hasErrors()) {
                flash.message = "AolDatasource ${params.id} updated"
                redirect(action: show, id: aolDatasource.id)
            }
            else {
                render(view: 'edit', model: [aolDatasource: aolDatasource])
            }
        }
        else {
            flash.message = "AolDatasource not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def aolDatasource = new AolDatasource()
        aolDatasource.properties = params
        return ['aolDatasource': aolDatasource]
    }

    def save = {
        def aolDatasource = AolDatasource.add(ControllerUtils.getClassProperties(params, AolDatasource))
        if (!aolDatasource.hasErrors()) {
            flash.message = "AolDatasource ${aolDatasource.id} created"
            redirect(action: show, id: aolDatasource.id)
        }
        else {
            render(view: 'create', model: [aolDatasource: aolDatasource])
        }
    }

    def addTo = {
        def aolDatasource = AolDatasource.get([id: params.id])
        if (!aolDatasource) {
            flash.message = "AolDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(aolDatasource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [aolDatasource: aolDatasource, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: aolDatasource.id)
            }
        }
    }

    def addRelation = {
        def aolDatasource = AolDatasource.get([id: params.id])
        if (!aolDatasource) {
            flash.message = "AolDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(aolDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    aolDatasource.addRelation(relationMap);
                    if (aolDatasource.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [aolDatasource: aolDatasource, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "AolDatasource ${params.id} updated"
                        redirect(action: edit, id: aolDatasource.id)
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
        def aolDatasource = AolDatasource.get([id: params.id])
        if (!aolDatasource) {
            flash.message = "AolDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(aolDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    aolDatasource.removeRelation(relationMap);
                    if (aolDatasource.hasErrors()) {
                        render(view: 'edit', model: [aolDatasource: aolDatasource])
                    }
                    else {
                        flash.message = "AolDatasource ${params.id} updated"
                        redirect(action: edit, id: aolDatasource.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: aolDatasource.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: aolDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("datasource.AolDatasource")
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