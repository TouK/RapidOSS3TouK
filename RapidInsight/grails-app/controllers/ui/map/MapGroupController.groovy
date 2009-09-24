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
package ui.map

import grails.converters.XML;

class MapGroupController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: ['POST', 'GET'], save: ['POST', 'GET'], update: ['POST', 'GET']]

    def list = {
        if (!params.max) params.max = 10
        //[ mapGroupList: MapGroup.list( params ) ]
        def mapGroups = MapGroup.list(params);
        withFormat {
            html mapGroupList: mapGroups
            xml {render mapGroups as XML}
        }
    }

    def show = {
        def mapGroup = MapGroup.get([id: params.id])

        if (!mapGroup) {
            addError("default.object.not.found", [MapGroup.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action: list)
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }

        }
        else {
            withFormat {
                html {render(view: "show", model: [mapGroup: mapGroup])}
                xml {render mapGroup as XML}
            }
        }
    }

    def delete = {
        def mapGroup = MapGroup.get([id: params.id])
        if (mapGroup) {
            try{
                def maps = mapGroup.maps;
                mapGroup.remove();
                def username = session.username;
                maps.each {
                    it.remove();
                }
                withFormat {
                    html {
                        flash.message = "MapGroup ${params.id} deleted"
                        redirect(action: list)
                    }
                    xml {render(text: com.ifountain.rcmdb.domain.util.ControllerUtils.convertSuccessToXml("MapGroup ${params.id} deleted"), contentType: "text/xml")}
                }
            }
             catch (e) {
                addError("default.custom.error", [e.getMessage()])

                withFormat {
                    html {
                        flash.errors = errors;
                        redirect(action: show, id: mapGroup.id)
                    }
                    xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                }

            }
        }
        else {
            addError("default.object.not.found", [MapGroup.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action: list)
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
        }
    }

    def edit = {
        def mapGroup = MapGroup.get([id: params.id])

        if (!mapGroup) {
            addError("default.object.not.found", [MapGroup.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action: list)
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
        }
        else {
            withFormat {
                html {
                    return [mapGroup: mapGroup]
                }
                xml {
                    render(contentType: 'text/xml') {
                        Edit {
                            name(mapGroup.groupName)
                            userName(mapGroup.username)
                        }
                    }

                }
            }
        }
    }


    def update = {
        def mapGroup = MapGroup.get([id: params.id])
        if (mapGroup) {
            mapGroup.update(com.ifountain.rcmdb.domain.util.ControllerUtils.getClassProperties(params, MapGroup));
            if (!mapGroup.hasErrors()) {
                withFormat {
                    html {
                        flash.message = "MapGroup ${params.id} updated"
                        redirect(action: list)
                    }
                    xml {render(text: com.ifountain.rcmdb.domain.util.ControllerUtils.convertSuccessToXml("MapGroup ${params.id} updated"), contentType: "text/xml")}
                }
            }
            else {
                withFormat {
                    html {
                        render(view: 'edit', model: [mapGroup: mapGroup])
                    }
                    xml {render(text: errorsToXml(mapGroup.errors), contentType: "text/xml")}
                }
            }
        }
        else {
            addError("default.object.not.found", [MapGroup.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action: edit, id: params.id)
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
        }
    }

    def create = {
        def mapGroup = new MapGroup()
        mapGroup.properties = params
        return ['mapGroup': mapGroup]
    }

    def save = {
        params["username"] = session.username;
        def mapGroup = MapGroup.add(com.ifountain.rcmdb.domain.util.ControllerUtils.getClassProperties(params, MapGroup))
        if (!mapGroup.hasErrors()) {
            withFormat {
                html {
                    flash.message = "MapGroup ${mapGroup.id} created"
                    redirect(action: show, id: mapGroup.id)
                }
                xml {render(text: com.ifountain.rcmdb.domain.util.ControllerUtils.convertSuccessToXml("MapGroup ${params.name} created"), contentType: "text/xml")}
            }
        }
        else {
            withFormat {
                html {
                    render(view: 'create', model: [mapGroup: mapGroup])
                }
                xml {render(text: errorsToXml(mapGroup.errors), contentType: "text/xml")}
            }

        }
    }

    def addTo = {
        def mapGroup = MapGroup.get([id: params.id])
        if (!mapGroup) {
            flash.message = "MapGroup not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = mapGroup.hasMany[relationName];
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [mapGroup: mapGroup, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: mapGroup.id)
            }
        }
    }

    def addRelation = {
        def mapGroup = MapGroup.get([id: params.id])
        if (!mapGroup) {
            flash.message = "MapGroup not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = mapGroup.hasMany[relationName];
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    mapGroup.addRelation(relationMap);
                    if (mapGroup.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [mapGroup: mapGroup, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "MapGroup ${params.id} updated"
                        redirect(action: edit, id: mapGroup.id)
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
        def mapGroup = MapGroup.get([id: params.id])
        if (!mapGroup) {
            flash.message = "MapGroup not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = mapGroup.hasMany[relationName];
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    mapGroup.removeRelation(relationMap);
                    if (mapGroup.hasErrors()) {
                        render(view: 'edit', model: [mapGroup: mapGroup])
                    }
                    else {
                        flash.message = "MapGroup ${params.id} updated"
                        redirect(action: edit, id: mapGroup.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: mapGroup.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: mapGroup.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("MapGroup")
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