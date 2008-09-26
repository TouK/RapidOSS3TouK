package auth

import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 26, 2008
 * Time: 2:03:41 PM
 * To change this template use File | Settings | File Templates.
 */
class GroupController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [groupList: Group.list(params)]
    }

    def show = {
        def group = Group.get([id: params.id])

        if (!group) {
            flash.message = "Group not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (group.class != Group)
            {
                def controllerName = group.class.name;
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
                return [group: group]
            }
        }
    }

    def delete = {
        def group = Group.get([id: params.id])
        if (group) {
            try {
                group.remove()
                flash.message = "Group ${params.id} deleted"
                redirect(action: list)
            }
            catch (e) {
                addError("default.couldnot.delete", [Group, group])
                flash.errors = this.errors;
                redirect(action: show, id: group.id)
            }

        }
        else {
            flash.message = "Group not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def group = Group.get([id: params.id])

        if (!group) {
            flash.message = "Group not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [group: group]
        }
    }


    def update = {
        def group = Group.get([id: params.id])
        if (group) {
            group.update(ControllerUtils.getClassProperties(params, Group));
            if (!group.hasErrors()) {
                flash.message = "Group ${params.id} updated"
                redirect(action: show, id: group.id)
            }
            else {
                render(view: 'edit', model: [group: group])
            }
        }
        else {
            flash.message = "Group not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def group = new Group()
        group.properties = params
        return ['group': group]
    }

    def save = {
        def group = Group.add(ControllerUtils.getClassProperties(params, Group))
        if (!group.hasErrors()) {
            flash.message = "Group ${group.id} created"
            redirect(action: show, id: group.id)
        }
        else {
            render(view: 'create', model: [group: group])
        }
    }

    def addTo = {
        def group = Group.get([id: params.id])
        if (!group) {
            flash.message = "Group not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = DomainClassUtils.getStaticMapVariable(Group, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [group: group, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: group.id)
            }
        }
    }



    def addRelation = {
        def group = Group.get([id: params.id])
        if (!group) {
            flash.message = "Group not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(Group, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    group.addRelation(relationMap);
                    if (group.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [group: group, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "Group ${params.id} updated"
                        redirect(action: edit, id: group.id)
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
        def group = Group.get([id: params.id])
        if (!group) {
            flash.message = "Group not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(Group, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    group.removeRelation(relationMap);
                    if (group.hasErrors()) {
                        render(view: 'edit', model: [group: group])
                    }
                    else {
                        flash.message = "Group ${params.id} updated"
                        redirect(action: edit, id: group.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: group.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: group.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("Group")
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