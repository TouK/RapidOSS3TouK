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
import ui.map.*;

def groupName =  params.groupName
if(!groupName || groupName == ""){
    groupName = "Default";
}
def mapName =  params.mapName
def layout =  params.layout
def nodes =  params.nodes;


def map = TopoMap.get(mapName:mapName, username:RsUser.RSADMIN, isPublic:true);
if(map){
    throw new Exception("There is a public map with name ${mapName}. Save is not allowed.")
}
def user = RsUser.findByUsername(web.session.username);

def group = MapGroup.add( groupName : groupName, username : user );
if(params.mapId && params.mapId != ""){
    map = TopoMap.get(id:params.mapId);
    map.update(mapName:mapName, layout:layout, mapType:params.mapType,nodePropertyList:params.nodePropertyList,nodes:nodes)
}
else{
    map = TopoMap.add( mapName : mapName, username : user, layout : layout, mapType:params.mapType,nodePropertyList:params.nodePropertyList,nodes:nodes);
}

group.addRelation( maps : map);


