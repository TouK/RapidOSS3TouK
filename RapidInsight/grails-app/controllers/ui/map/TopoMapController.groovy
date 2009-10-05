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

class TopoMapController {
    def index = {}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: ['POST', 'GET']]

    def listWithGroups = {
        def username =  session.username;

        def writer = new StringWriter();
        def mapBuilder = new MarkupBuilder(writer);

        MapGroup.add(groupName: MapGroup.MY_MAPS(), username: username);
        def mapGroups = MapGroup.getVisibleGroupsForUser(username);

        mapBuilder.Maps
        {
            mapGroups.each {MapGroup group ->
                def actionsAllowed=actionsAllowed(username,group.username);
                mapBuilder.Map(id: group.id, name: group.groupName, nodeType: "group", isPublic: group.isPublic,actionsAllowed:actionsAllowed) {
                    group.maps.each {TopoMap topoMap ->
                        mapBuilder.Map(id: topoMap.id, name: topoMap.mapName, nodeType: "map", isPublic: topoMap.isPublic, layout: topoMap.layout,actionsAllowed:actionsAllowed)
                    }
                }
            }
        }
        render(text: writer.toString(), contentType: "text/xml")
    }
    def actionsAllowed(currentUsername,ownerUsername)
    {
        return currentUsername==ownerUsername;
    }
    def delete = {
        def topoMap = TopoMap.get( [id:params.id])
        if(topoMap) {
            def username =  session.username;
            def mapName = topoMap.mapName;
            topoMap.remove();
            withFormat {
                html {
                    flash.message = "TopoMap ${params.id} deleted"
                    redirect(action: list)
                }
                xml {render(text: ControllerUtils.convertSuccessToXml("TopoMap ${topoMap.id} deleted"), contentType: "text/xml")}
            }
        }
        else {
            addError("default.object.not.found", [TopoMap.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action: list)
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
        }
    }

    def save= {

        def groupName =  params.groupName
        if(!groupName || groupName == "" || groupName.equalsIgnoreCase(MapGroup.MY_MAPS())){
            groupName = MapGroup.MY_MAPS();
        }
        def mapName =  params.mapName
        def layout =  params.layout
        def nodes =  params.nodes;
        def username = session.username;


        def group = MapGroup.get( groupName : groupName, username : username );
        if(group == null)
        {
            group= MapGroup.add( groupName : groupName, username : username );
        }

        def map = TopoMap.addUnique( mapName : mapName, username : username, layout : layout, group:group, mapProperties:params.mapProperties,mapPropertyList:params.mapPropertyList,nodePropertyList:params.nodePropertyList,nodes:nodes);
        if(!map.hasErrors())
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
            def groupName =  params.groupName
            def mapName =  params.mapName
            def layout =  params.layout
            def nodes =  params.nodes;
            def username = session.username;

            if(map.username != username)
            {
               render(text: ControllerUtils.convertErrorToXml("TopoMap ${mapName} belongs to other user, you can not update it."), contentType: "text/xml");
               return;
            }
            if(!groupName || groupName == "" || groupName.equalsIgnoreCase(MapGroup.MY_MAPS())){
                groupName = MapGroup.MY_MAPS();
            }
            def group = MapGroup.get( groupName : groupName, username : username );
            if(group == null)
            {
                group= MapGroup.add( groupName : groupName, username : username );
            }

            map.update( mapName : mapName, username : username, layout : layout, group:group, mapProperties:params.mapProperties,mapPropertyList:params.mapPropertyList,nodePropertyList:params.nodePropertyList,nodes:nodes);

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

    def load={
        def map = TopoMap.get( id:params.id);

        def res = CmdbScript.runScript("expandMap", [params:[nodes:map.nodes,nodePropertyList:map.nodePropertyList,mapProperties:map.mapProperties,mapPropertyList:map.mapPropertyList]]);
        def slurper = new XmlSlurper().parseText(res);
        def nodeXmls = slurper.node;
        def edgeXmls = slurper.edge;

        def writer = new StringWriter();
        def mapBuilder = new MarkupBuilder(writer);

        mapBuilder.graph(layout:map.layout,mapProperties:map.mapProperties,mapPropertyList:map.mapPropertyList,nodePropertyList:map.nodePropertyList)
        {
            nodeXmls.each {
                mapBuilder.node( it.attributes());
            }

            edgeXmls.each {
                mapBuilder.edge( it.attributes());
            }

        }
        render(text: writer.toString(), contentType: "text/xml")
    }


}