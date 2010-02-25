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

import com.ifountain.rcmdb.domain.util.ControllerUtils
import auth.RsUser
import script.CmdbScript
import groovy.xml.MarkupBuilder
import auth.Role

class TopoMapController {
    def index = {}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: ['POST', 'GET']]

    def listWithGroups = {
        def username = session.username;

        def writer = new StringWriter();
        def mapBuilder = new MarkupBuilder(writer);

        MapGroup.add(groupName: MapGroup.MY_MAPS(), username: username, expanded: true);
        def mapGroups = MapGroup.getVisibleGroupsForUser(username);

        mapBuilder.Maps
        {
            mapGroups.each {MapGroup group ->
                mapBuilder.Map(id: group.id, name: group.groupName, nodeType: "group", isPublic: group.isPublic, expanded: group.expanded) {
                    group.maps.each {TopoMap topoMap ->
                        if (topoMap.username == username || topoMap.isPublic) {
                            mapBuilder.Map(id: topoMap.id, name: topoMap.mapName, nodeType: "map", isPublic: topoMap.isPublic, layout: topoMap.layout)
                        }
                    }
                }
            }
        }
        render(text: writer.toString(), contentType: "text/xml")
    }
    def delete = {
        def topoMap = TopoMap.get([id: params.id])
        if (topoMap) {
            if (topoMap.isPublic && !RsUser.hasRole(session.username, Role.ADMINISTRATOR)) {
                addError("default.not.authorized", []);
                render(text: errorsToXml(this.errors), contentType: "text/xml")
                return;
            }
            topoMap.remove();
            render(text: ControllerUtils.convertSuccessToXml("TopoMap ${topoMap.id} deleted"), contentType: "text/xml")
        }
        else {
            addError("default.object.not.found", [TopoMap.class.name, params.id]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }
    }

    def save = {
        def topoMapParams = ControllerUtils.getClassProperties(params, TopoMap);
        if (topoMapParams.isPublic && !RsUser.hasRole(session.username, Role.ADMINISTRATOR)) {
            addError("default.not.authorized", []);
            render(text: errorsToXml(this.errors), contentType: "text/xml")
            return;
        }
        def groupName = params.groupName
        if (!groupName || groupName == "" || groupName.equalsIgnoreCase(MapGroup.MY_MAPS())) {
            groupName = MapGroup.MY_MAPS();
        }
        def username = session.username;
        def group = MapGroup.get(groupName: groupName, username: username);
        if (group == null)
        {
            group = MapGroup.get(groupName: groupName, username: RsUser.RSADMIN)
            if (group == null || !group.isPublic) {
                group = MapGroup.add(groupName: groupName, username: username);
            }
        }

        topoMapParams['username'] = topoMapParams.isPublic ? RsUser.RSADMIN : username
        topoMapParams['group'] = group;
        def map = TopoMap.addUnique(topoMapParams);
        if (!map.hasErrors())
        {
            render(text: ControllerUtils.convertSuccessToXml("TopoMap ${map.id} created"), contentType: "text/xml")
        }
        else
        {
            render(text: errorsToXml(map.errors), contentType: "text/xml")
        }
    }

    def update = {
        def map = TopoMap.get([id: params.id])
        if (map) {
            if (map.isPublic && !RsUser.hasRole(session.username, Role.ADMINISTRATOR)) {
                addError("default.not.authorized", []);
                render(text: errorsToXml(this.errors), contentType: "text/xml")
                return;
            }
            def groupName = params.groupName
            def username = session.username;

            if (!groupName || groupName == "" || groupName.equalsIgnoreCase(MapGroup.MY_MAPS())) {
                groupName = MapGroup.MY_MAPS();
            }
            def group = MapGroup.get(groupName: groupName, username: username);
            if (group == null)
            {
                group = MapGroup.get(groupName: groupName, username: RsUser.RSADMIN)
                if (group == null || !group.isPublic) {
                    group = MapGroup.add(groupName: groupName, username: username);
                }
            }
            def topoMapParams = ControllerUtils.getClassProperties(params, TopoMap)
            topoMapParams['username'] = topoMapParams.isPublic ? RsUser.RSADMIN : username
            topoMapParams['group'] = group;
            map.update(topoMapParams);
            if (!map.hasErrors()) {
                render(text: ControllerUtils.convertSuccessToXml("TopoMap ${map.id} updated"), contentType: "text/xml")
            }
            else {
                render(text: errorsToXml(map.errors), contentType: "text/xml")
            }
        }
        else {
            addError("default.object.not.found", [TopoMap.class.name, params.id]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }
    }

    def load = {
        def map = TopoMap.get(id: params.id);

        def res = CmdbScript.runScript("expandMap", [params: [nodes: map.nodes, nodePropertyList: map.nodePropertyList, mapProperties: map.mapProperties, mapPropertyList: map.mapPropertyList]]);
        def slurper = new XmlSlurper().parseText(res);
        def nodeXmls = slurper.node;
        def edgeXmls = slurper.edge;

        def writer = new StringWriter();
        def mapBuilder = new MarkupBuilder(writer);

        mapBuilder.graph(layout: map.layout, mapProperties: map.mapProperties, mapPropertyList: map.mapPropertyList, nodePropertyList: map.nodePropertyList) {
            nodeXmls.each {
                mapBuilder.node(it.attributes());
            }
            edgeXmls.each {
                mapBuilder.edge(it.attributes());
            }
        }
        render(text: writer.toString(), contentType: "text/xml")
    }

}