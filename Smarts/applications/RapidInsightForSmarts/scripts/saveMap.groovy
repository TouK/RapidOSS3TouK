import auth.RsUser;

def groupName =  params.groupName
def mapName =  params.mapName
def nodes =  params.nodes.splitPreserveAllTokens(";").findAll {it != ""};




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
    def expandable = deviceData[3];
    def expanded = deviceData[4];
    def device = MapNode.add( nodeIdentifier : deviceId, username : user, mapName : mapName, xlocation : x, ylocation : y, expandable : expandable, expanded:expanded);
    map.addRelation( consistOfDevices : device);
}
