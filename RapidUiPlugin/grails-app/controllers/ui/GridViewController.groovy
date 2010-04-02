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
import auth.RsUser

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
        def gridViewQuery = "username:${session.username.exactQuery()} OR (username:${auth.RsUser.RSADMIN.exactQuery()} AND isPublic:true)"
        if(params.type){
            gridViewQuery = "type:${params.type.exactQuery()} AND (${gridViewQuery})";
        }
        def gridViews = GridView.searchEvery(gridViewQuery, params);

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

    def delete = {
        def isAdmin =  RsUser.hasRole(session.username, auth.Role.ADMINISTRATOR)
        def gridView = GridView.get([id: params.id]);
        if (!isAdmin && gridView.isPublic) {
            addError("gridview.not.authorized", []);
            render(text: errorsToXml(errors), contentType: "text/xml")
            return;
        }
        if (gridView) {
            gridView.remove()
            render(text: com.ifountain.rcmdb.domain.util.ControllerUtils.convertSuccessToXml("GridView ${gridView.id} deleted"), contentType: "text/xml")
        }
        else {
            addError("default.object.not.found", [GridView.class.name, params.id ? params.id : params.name]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }
    }

    def add = {
        def isPublic = params.isPublic;
        params.username = isPublic == "true" ? auth.RsUser.RSADMIN : session.username;
        def isAdmin = RsUser.hasRole(session.username, auth.Role.ADMINISTRATOR)
        def gridView = GridView.get(name: params.name, username: auth.RsUser.RSADMIN, type:params.type);
        if (!isAdmin && ((gridView && gridView.isPublic) || isPublic == "true")) {
            addError("gridview.not.authorized", []);
            render(text: errorsToXml(errors), contentType: "text/xml")
            return;
        }
        gridView = GridView.add([name: params.name, username: params.username, type:params.type, defaultSortColumn: params.defaultSortColumn, sortOrder: params.sortOrder, isPublic: params.isPublic]);
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
                    GridColumn.add(gridView: gridView, gridViewId: gridView.id, attributeName: attributeName, header: header, width: width, columnIndex: columnIndex)
                    columnIndex++;
                }
            }
            render(text: com.ifountain.rcmdb.domain.util.ControllerUtils.convertSuccessToXml("GridView ${gridView.id} created"), contentType: "text/xml")

        }
        else {
            render(text: errorsToXml(gridView.errors), contentType: "text/xml")
        }
    }

}