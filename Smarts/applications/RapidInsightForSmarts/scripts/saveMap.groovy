import auth.RsUser;
import MapNode;
import EdgeNode;
import TopoMap;
import MapGroup;

def groupName = params.groupName;
def mapName = params.mapName;
def nodes = params.nodes.tokenize(";");
def edges = params.edges.tokenize(";");

    
def user = RsUser.findByUsername(web.session.username);

def group = MapGroup.add( groupName : groupName, username : user );
def map = TopoMap.add( mapName : mapName, username : user);

group.addRelation( maps : map);

for( def i = 0; i < nodes.size(); i++)
{
    def deviceData = nodes[i].tokenize(",");
    def deviceId = deviceData[0];
    def x = deviceData[1];
    def y = deviceData[2];
    def device = MapNode.add( nodeIdentifier : deviceId, username : user, mapName : mapName, xlocation : x, ylocation : y);
    map.addRelation( consistOfDevices : device);
}

for( def i = 0; i < edges.size(); i++)
{
    def edgeData = edges[i].tokenize(",");
    def source = edgeData[0];
    def target = edgeData[1];
    def edge1 = EdgeNode.add(  mapName : mapName, username : user, from : source, to : target);
}
