import MapNode;
import EdgeNode;
import TopoMap;
import MapGroup;
import auth.RsUser;

def adminUser = RsUser.RSADMIN;

def mapGroup1 = MapGroup.add( groupName : "Group 1", username : adminUser);
def mapGroup2 = MapGroup.add( groupName : "Group 2", username : adminUser);


def map1 = TopoMap.add( mapName : "Map 1", username : adminUser);
def map2 = TopoMap.add( mapName : "Map 2", username : adminUser);
def map3 = TopoMap.add( mapName : "Map 3", username : adminUser);

def dev1 = MapNode.add( nodeIdentifier : "device1", username : adminUser, mapName : "Map 1", xlocation : 50, ylocation : 50);
def dev2 = MapNode.add( nodeIdentifier : "device2", username : adminUser, mapName : "Map 1", xlocation : 100, ylocation : 50);
def dev3 = MapNode.add( nodeIdentifier : "device3", username : adminUser, mapName : "Map 1", xlocation : 100, ylocation : 100);

def dev4 = MapNode.add( nodeIdentifier : "device4", username : adminUser, mapName : "Map 2", xlocation : 50, ylocation : 50);
def dev5 = MapNode.add( nodeIdentifier : "device5", username : adminUser, mapName : "Map 2", xlocation : 100, ylocation : 50);
def dev6 = MapNode.add( nodeIdentifier : "device6", username : adminUser, mapName : "Map 2", xlocation : 50, ylocation : 100);
def dev7 = MapNode.add( nodeIdentifier : "device7", username : adminUser, mapName : "Map 2", xlocation : 100, ylocation : 100);

def dev8 = MapNode.add( nodeIdentifier : "device8", username : adminUser, mapName : "Map 3", xlocation : 50, ylocation : 50);

def edge1 = EdgeNode.add( mapName: "Map 1", username : adminUser, from : "device1", to : "device2");
def edge2 = EdgeNode.add( mapName: "Map 1", username : adminUser, from : "device1", to : "device3");

def edge3 = EdgeNode.add( mapName: "Map 2", username : adminUser, from : "device4", to : "device5");
def edge4 = EdgeNode.add( mapName: "Map 2", username : adminUser, from : "device6", to : "device5");
def edge5 = EdgeNode.add( mapName: "Map 2", username : adminUser, from : "device7", to : "device4");
def edge6 = EdgeNode.add( mapName: "Map 2", username : adminUser, from : "device7", to : "device6");

mapGroup1.addRelation( maps : map1);
mapGroup1.addRelation( maps : map2);

mapGroup2.addRelation( maps : map3);

map1.addRelation( consistOfDevices : dev1);
map1.addRelation( consistOfDevices : dev2);
map1.addRelation( consistOfDevices : dev3);

map2.addRelation( consistOfDevices : dev4);
map2.addRelation( consistOfDevices : dev5);
map2.addRelation( consistOfDevices : dev6);
map2.addRelation( consistOfDevices : dev7);

map3.addRelation( consistOfDevices : dev8);

