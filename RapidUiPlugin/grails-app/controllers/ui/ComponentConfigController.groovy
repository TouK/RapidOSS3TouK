/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package ui;


class ComponentConfigController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [componentConfigList: ComponentConfig.search("alias:*", params).results]
    }

    def show = {
        def componentConfig = ComponentConfig.get([id: params.id])

        if (!componentConfig) {
            flash.message = "ComponentConfig not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (componentConfig.class != ComponentConfig)
            {
                def controllerName = componentConfig.class.simpleName;
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
                return [componentConfig: componentConfig]
            }
        }
    }

    def delete = {
        def componentConfig = ComponentConfig.get([id: params.id])
        if (componentConfig) {
            componentConfig.remove()
            flash.message = "ComponentConfig ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "ComponentConfig not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def componentConfig = ComponentConfig.get([id: params.id])

        if (!componentConfig) {
            flash.message = "ComponentConfig not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [componentConfig: componentConfig]
        }
    }


    def update = {
        params.username = session.username;
        def componentConfig = ComponentConfig.get([id: params.id])
        if (componentConfig) {
            componentConfig.update(com.ifountain.rcmdb.domain.util.ControllerUtils.getClassProperties(params, ComponentConfig));
            if (!componentConfig.hasErrors()) {
                flash.message = "ComponentConfig ${params.id} updated"
                redirect(action: show, id: componentConfig.id)
            }
            else {
                render(view: 'edit', model: [componentConfig: componentConfig])
            }
        }
        else {
            flash.message = "ComponentConfig not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def componentConfig = new ComponentConfig()
        componentConfig.properties = params
        return ['componentConfig': componentConfig]
    }

    def save = {
        params.username = session.username;
        def componentConfig = ComponentConfig.add(com.ifountain.rcmdb.domain.util.ControllerUtils.getClassProperties(params, ComponentConfig))
        if (!componentConfig.hasErrors()) {
            withFormat {
                html {
                    flash.message = "ComponentConfig ${componentConfig.id} created"
                    redirect(action: show, id: componentConfig.id)
                }
                xml {render(text: com.ifountain.rcmdb.domain.util.ControllerUtils.convertSuccessToXml("ComponentConfig ${componentConfig.id} created"), contentType: "text/xml")}
            }

        }
        else {
            withFormat {
                html {
                    render(view: 'create', model: [componentConfig: componentConfig])
                }
                xml {render(text: errorsToXml(componentConfig.errors), contentType: "text/xml")}
            }

        }
    }

    def get = {
        def configName = params.name;
        def url = params.url;
        def componentConfig = ComponentConfig.get(name: configName, username: session.username, url:url);
        if (componentConfig) {
            render(contentType: "text/xml") {
                ComponentConfig(name: componentConfig.name, pollingInterval: componentConfig.pollingInterval)
            }
        }
        else {
            addError("default.object.not.found", [ComponentConfig.class.name, params.name]);
            withFormat {
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
        }
    }

    def addTo = {
        def componentConfig = ComponentConfig.get([id: params.id])
        if (!componentConfig) {
            flash.message = "ComponentConfig not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = componentConfig.hasMany[relationName];
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [componentConfig: componentConfig, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: componentConfig.id)
            }
        }
    }

    def addRelation = {
        def componentConfig = ComponentConfig.get([id: params.id])
        if (!componentConfig) {
            flash.message = "ComponentConfig not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = componentConfig.hasMany[relationName];
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    componentConfig.addRelation(relationMap);
                    if (componentConfig.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [componentConfig: componentConfig, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "ComponentConfig ${params.id} updated"
                        redirect(action: edit, id: componentConfig.id)
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
        def componentConfig = ComponentConfig.get([id: params.id])
        if (!componentConfig) {
            flash.message = "ComponentConfig not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = componentConfig.hasMany[relationName];
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    componentConfig.removeRelation(relationMap);
                    if (componentConfig.hasErrors()) {
                        render(view: 'edit', model: [componentConfig: componentConfig])
                    }
                    else {
                        flash.message = "ComponentConfig ${params.id} updated"
                        redirect(action: edit, id: componentConfig.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: componentConfig.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: componentConfig.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("ComponentConfig")
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