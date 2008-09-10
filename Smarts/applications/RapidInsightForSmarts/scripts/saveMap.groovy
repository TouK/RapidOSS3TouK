import auth.RsUser;
import MapNode;
import EdgeNode;
import TopoMap;
import MapGroup;


def postData = params.postData.data;
def data = postData.tokenize("|");



def groupName = data[0].tokenize("=")[1];
def mapName = data[1].tokenize("=")[1];
def nodes = data[2].tokenize("=")[1].tokenize(";");
def edges = data[3].tokenize("=")[1].tokenize(";");




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
    def expands = deviceData[3];    
    def device = MapNode.add( nodeIdentifier : deviceId, username : user, mapName : mapName, xlocation : x, ylocation : y, expands : expands);
    map.addRelation( consistOfDevices : device);
}

for( def i = 0; i < edges.size(); i++)
{
    def edgeData = edges[i].tokenize(",");
    def source = edgeData[0];
    def target = edgeData[1];
    def edge1 = EdgeNode.add(  mapName : mapName, username : user, from : source, to : target);
}
