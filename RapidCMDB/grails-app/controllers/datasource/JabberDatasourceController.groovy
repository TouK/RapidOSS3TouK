package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 3, 2009
* Time: 11:47:52 AM
*/
class JabberDatasourceController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']
    def list = {
        if (!params.sort) params.sort = "name"
        [jabberDatasourceList: JabberDatasource.search("alias:*", params).results]
    }

    def show = {
        def jabberDatasource = JabberDatasource.get([id: params.id])

        if (!jabberDatasource) {
            flash.message = "JabberDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (jabberDatasource.class != JabberDatasource)
            {
                def controllerName = jabberDatasource.class.simpleName;
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
                return [jabberDatasource: jabberDatasource]
            }
        }
    }

    def delete = {
        def jabberDatasource = JabberDatasource.get([id: params.id])
        if (jabberDatasource) {
            jabberDatasource.remove()
            flash.message = "JabberDatasource ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "JabberDatasource not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def jabberDatasource = JabberDatasource.get([id: params.id])

        if (!jabberDatasource) {
            flash.message = "JabberDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [jabberDatasource: jabberDatasource]
        }
    }


    def update = {
        def jabberDatasource = JabberDatasource.get([id: params.id])
        if (jabberDatasource) {
            jabberDatasource.update(ControllerUtils.getClassProperties(params, JabberDatasource));
            if (!jabberDatasource.hasErrors()) {
                flash.message = "JabberDatasource ${params.id} updated"
                redirect(action: show, id: jabberDatasource.id)
            }
            else {
                render(view: 'edit', model: [jabberDatasource: jabberDatasource])
            }
        }
        else {
            flash.message = "JabberDatasource not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def jabberDatasource = new JabberDatasource()
        jabberDatasource.properties = params
        return ['jabberDatasource': jabberDatasource]
    }

    def save = {
        def jabberDatasource = JabberDatasource.add(ControllerUtils.getClassProperties(params, JabberDatasource))
        if (!jabberDatasource.hasErrors()) {
            flash.message = "JabberDatasource ${jabberDatasource.id} created"
            redirect(action: show, id: jabberDatasource.id)
        }
        else {
            render(view: 'create', model: [jabberDatasource: jabberDatasource])
        }
    }

    def addTo = {
        def jabberDatasource = JabberDatasource.get([id: params.id])
        if (!jabberDatasource) {
            flash.message = "JabberDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(jabberDatasource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [jabberDatasource: jabberDatasource, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: jabberDatasource.id)
            }
        }
    }

    def addRelation = {
        def jabberDatasource = JabberDatasource.get([id: params.id])
        if (!jabberDatasource) {
            flash.message = "JabberDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(jabberDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    jabberDatasource.addRelation(relationMap);
                    if (jabberDatasource.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [jabberDatasource: jabberDatasource, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "JabberDatasource ${params.id} updated"
                        redirect(action: edit, id: jabberDatasource.id)
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
        def jabberDatasource = JabberDatasource.get([id: params.id])
        if (!jabberDatasource) {
            flash.message = "JabberDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(jabberDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    jabberDatasource.removeRelation(relationMap);
                    if (jabberDatasource.hasErrors()) {
                        render(view: 'edit', model: [jabberDatasource: jabberDatasource])
                    }
                    else {
                        flash.message = "JabberDatasource ${params.id} updated"
                        redirect(action: edit, id: jabberDatasource.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: jabberDatasource.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: jabberDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("datasource.JabberDatasource")
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