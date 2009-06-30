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
package ui

import groovy.xml.MarkupBuilder

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Sep 22, 2008
* Time: 2:50:18 PM
*/
class GridViewController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        def isAdmin = org.jsecurity.SecurityUtils.subject.hasRole(auth.Role.ADMINISTRATOR)
        def gridViews = GridView.searchEvery("username:${session.username.exactQuery()} OR (username:${auth.RsUser.RSADMIN.exactQuery()} AND isPublic:true)", params);

        withFormat {
            xml {
                def sw = new StringWriter();
                def builder = new MarkupBuilder(sw);
                builder.Views {
                    for (view in gridViews) {
                        builder.View(id: view.id, name: view.name, defaultSortColumn: view.defaultSortColumn, sortOrder: view.sortOrder, isPublic: view.isPublic, updateAllowed: (!view.isPublic || isAdmin)) {
                            view.gridColumns.each {GridColumn gridColumn ->
                                builder.Column(attributeName: gridColumn.attributeName, header: gridColumn.header, width: gridColumn.width, columnIndex: gridColumn.columnIndex);
                            }
                        }
                    }
                }
                render(contentType: 'text/xml', text: sw.toString())
            }
        }
    }

    def show = {
        def gridView = GridView.get([id: params.id])

        if (!gridView) {
            flash.message = "GridView not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (gridView.class != GridView)
            {
                def controllerName = gridView.class.simpleName;
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
                return [gridView: gridView]
            }
        }
    }

    def delete = {
        def isAdmin = org.jsecurity.SecurityUtils.subject.hasRole(auth.Role.ADMINISTRATOR)
        def gridView = GridView.get([id: params.id]);
        if (!isAdmin && gridView.isPublic) {
            addError("gridview.not.authorized", []);
            withFormat {
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
            return;
        }
        if (gridView) {
            gridView.remove()
            withFormat {
                html {
                    flash.message = "GridView ${params.id} deleted"
                    redirect(action: list)
                }
                xml {render(text: com.ifountain.rcmdb.domain.util.ControllerUtils.convertSuccessToXml("GridView ${gridView.id} deleted"), contentType: "text/xml")}
            }
        }
        else {
            addError("default.object.not.found", [GridView.class.name, params.id ? params.id : params.name]);
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
        def gridView = GridView.get([id: params.id])

        if (!gridView) {
            flash.message = "GridView not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [gridView: gridView]
        }
    }


    def update = {
        def gridView = GridView.get([id: params.id])
        if (gridView) {
            gridView.update(com.ifountain.rcmdb.domain.util.ControllerUtils.getClassProperties(params, GridView));
            if (!gridView.hasErrors()) {
                flash.message = "GridView ${params.id} updated"
                redirect(action: show, id: gridView.id)
            }
            else {
                render(view: 'edit', model: [gridView: gridView])
            }
        }
        else {
            flash.message = "GridView not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def gridView = new GridView()
        gridView.properties = params
        return ['gridView': gridView]
    }

    def save = {
        def gridView = GridView.add(com.ifountain.rcmdb.domain.util.ControllerUtils.getClassProperties(params, GridView))
        if (!gridView.hasErrors()) {
            flash.message = "GridView ${gridView.id} created"
            redirect(action: show, id: gridView.id)
        }
        else {
            render(view: 'create', model: [gridView: gridView])
        }
    }

    def add = {
        def isPublic = params.isPublic;
        params.username = isPublic == "true" ? auth.RsUser.RSADMIN : session.username;
        def isAdmin = org.jsecurity.SecurityUtils.subject.hasRole(auth.Role.ADMINISTRATOR)
        def gridView = GridView.get(name: params.name, username: auth.RsUser.RSADMIN);
        if (!isAdmin && ((gridView && gridView.isPublic) || isPublic == "true")) {
            addError("gridview.not.authorized", []);
            withFormat {
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
            return;
        }
        gridView = GridView.add([name: params.name, username: session.username, defaultSortColumn: params.defaultSortColumn, sortOrder: params.sortOrder, isPublic: params.isPublic]);
        if (!gridView.hasErrors()) {
            gridView.gridColumns.each {
                it.remove();
            }
            def columns = [:];
            def columnString = params["columns"].trim();
            if (columnString.length() > 0) {
                def columnsList = columnString.split("::");
                def columnIndex = 1;
                columnsList.each {
                    def props = it.split(";;");
                    def attributeName = props[0]
                    def header = props[1]
                    def width = Long.parseLong(props[2]);
                    GridColumn.add(gridView: gridView, gridViewId:gridView.id, attributeName: attributeName, header: header, width: width, columnIndex: columnIndex)
                    columnIndex++;
                }
            }
            withFormat {
                xml {render(text: com.ifountain.rcmdb.domain.util.ControllerUtils.convertSuccessToXml("GridView ${gridView.id} created"), contentType: "text/xml")}
            }

        }
        else {
            withFormat {
                xml {render(text: errorsToXml(gridView.errors), contentType: "text/xml")}
            }
        }
    }

    def addTo = {
        def gridView = GridView.get([id: params.id])
        if (!gridView) {
            flash.message = "GridView not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = gridView.hasMany[relationName];
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [gridView: gridView, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: gridView.id)
            }
        }
    }

    def addRelation = {
        def gridView = GridView.get([id: params.id])
        if (!gridView) {
            flash.message = "GridView not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = gridView.hasMany[relationName];
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    gridView.addRelation(relationMap);
                    if (gridView.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [gridView: gridView, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "GridView ${params.id} updated"
                        redirect(action: edit, id: gridView.id)
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
        def gridView = GridView.get([id: params.id])
        if (!gridView) {
            flash.message = "GridView not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = gridView.hasMany[relationName];
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    gridView.removeRelation(relationMap);
                    if (gridView.hasErrors()) {
                        render(view: 'edit', model: [gridView: gridView])
                    }
                    else {
                        flash.message = "GridView ${params.id} updated"
                        redirect(action: edit, id: gridView.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: gridView.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: gridView.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("GridView")
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