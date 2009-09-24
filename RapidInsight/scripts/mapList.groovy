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

import auth.RsUser
import groovy.xml.MarkupBuilder
import ui.map.*

def user = RsUser.findByUsername(web.session.username);
if (user == null) {
    throw new Exception("User ${web.session.username} does not exist");
}

def writer = new StringWriter();
def mapBuilder = new MarkupBuilder(writer);

MapGroup.add(groupName: MapGroup.MY_MAPS, username: web.session.username);
def mapGroups = MapGroup.getVisibleGroupsForUser(web.session.username);

mapBuilder.Maps
{
    mapGroups.each {MapGroup group ->
        mapBuilder.Map(id: group.id, name: group.groupName, nodeType: "group", isPublic: group.isPublic) {
            group.maps.each {TopoMap topoMap ->
                mapBuilder.Map(id: topoMap.id, name: topoMap.mapName, nodeType: "map", isPublic: topoMap.isPublic, layout: topoMap.layout)
            }
        }
    }
}
return writer.toString();
