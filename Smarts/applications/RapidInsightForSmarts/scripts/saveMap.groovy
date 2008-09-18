import auth.RsUser;

def groupName =  params.groupName
def mapName =  params.mapName
def nodes =  params.nodes.splitPreserveAllTokens(";").findAll {it != ""};
def edges =  params.edges.splitPreserveAllTokens(";").findAll {it != ""};




def user = RsUser.findByUsername(web.session.username);

def group = MapGroup.add( groupName : groupName, username : user );
def map = TopoMap.add( mapName : mapName, username : user);

group.addRelation( maps : map);

for( def i = 0; i < nodes.size(); i++)
{
    def deviceData = nodes[i].splitPreserveAllTokens(",");
    def deviceId = deviceData[0];
    def x = deviceData[1];
    def y = deviceData[2];
    def expands = deviceData[3];    
    def device = MapNode.add( nodeIdentifier : deviceId, username : user, mapName : mapName, xlocation : x, ylocation : y, expands : expands);
    map.addRelation( consistOfDevices : device);
}

for( def i = 0; i < edges.size(); i++)
{
    def edgeData = edges[i].splitPreserveAllTokens(",");
    def source = edgeData[0];
    def target = edgeData[1];
    def edge1 = EdgeNode.add(  mapName : mapName, username : user, from : source, to : target);
}
