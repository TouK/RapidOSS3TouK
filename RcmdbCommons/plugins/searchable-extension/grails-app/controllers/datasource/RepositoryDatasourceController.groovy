package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 13, 2009
* Time: 3:50:36 PM
*/
class RepositoryDatasourceController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [repositoryDatasourceList: RepositoryDatasource.search("alias:*", params).results]
    }

    def show = {
        def repositoryDatasource = RepositoryDatasource.get([id: params.id])

        if (!repositoryDatasource) {
            flash.message = "RepositoryDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (repositoryDatasource.class != RepositoryDatasource)
            {
                def controllerName = repositoryDatasource.class.simpleName;
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
                return [repositoryDatasource: repositoryDatasource]
            }
        }
    }

    def delete = {
        def repositoryDatasource = RepositoryDatasource.get([id: params.id])
        if (repositoryDatasource) {
            repositoryDatasource.remove()
            flash.message = "RepositoryDatasource ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "RepositoryDatasource not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def repositoryDatasource = RepositoryDatasource.get([id: params.id])

        if (!repositoryDatasource) {
            flash.message = "RepositoryDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [repositoryDatasource: repositoryDatasource]
        }
    }


    def update = {
        def repositoryDatasource = RepositoryDatasource.get([id: params.id])
        if (repositoryDatasource) {
            repositoryDatasource.update(ControllerUtils.getClassProperties(params, RepositoryDatasource));
            if (!repositoryDatasource.hasErrors()) {
                flash.message = "RepositoryDatasource ${params.id} updated"
                redirect(action: show, id: repositoryDatasource.id)
            }
            else {
                render(view: 'edit', model: [repositoryDatasource: repositoryDatasource])
            }
        }
        else {
            flash.message = "RepositoryDatasource not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def repositoryDatasource = new RepositoryDatasource()
        repositoryDatasource.properties = params
        return ['repositoryDatasource': repositoryDatasource]
    }

    def save = {
        def repositoryDatasource = RepositoryDatasource.add(ControllerUtils.getClassProperties(params, RepositoryDatasource))
        if (!repositoryDatasource.hasErrors()) {
            flash.message = "RepositoryDatasource ${repositoryDatasource.id} created"
            redirect(action: show, id: repositoryDatasource.id)
        }
        else {
            render(view: 'create', model: [repositoryDatasource: repositoryDatasource])
        }
    }

    def addTo = {
        def repositoryDatasource = RepositoryDatasource.get([id: params.id])
        if (!repositoryDatasource) {
            flash.message = "RepositoryDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(repositoryDatasource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [repositoryDatasource: repositoryDatasource, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: repositoryDatasource.id)
            }
        }
    }

    def addRelation = {
        def repositoryDatasource = RepositoryDatasource.get([id: params.id])
        if (!repositoryDatasource) {
            flash.message = "RepositoryDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(repositoryDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    repositoryDatasource.addRelation(relationMap);
                    if (repositoryDatasource.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [repositoryDatasource: repositoryDatasource, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "RepositoryDatasource ${params.id} updated"
                        redirect(action: edit, id: repositoryDatasource.id)
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
        def repositoryDatasource = RepositoryDatasource.get([id: params.id])
        if (!repositoryDatasource) {
            flash.message = "RepositoryDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(repositoryDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    repositoryDatasource.removeRelation(relationMap);
                    if (repositoryDatasource.hasErrors()) {
                        render(view: 'edit', model: [repositoryDatasource: repositoryDatasource])
                    }
                    else {
                        flash.message = "RepositoryDatasource ${params.id} updated"
                        redirect(action: edit, id: repositoryDatasource.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: repositoryDatasource.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: repositoryDatasource.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("datasource.RepositoryDatasource")
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