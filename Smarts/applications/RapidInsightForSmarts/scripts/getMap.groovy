import auth.RsUser
import groovy.xml.MarkupBuilder

import TopoMap
import MapNode
import EdgeNode

def user = RsUser.findByUsername(web.session.username);
if(user == null){
    throw new Exception("User ${web.session.username} does not exist");
}

def username2 = web.session.username;
def mapName2 = params.mapName;

def map = TopoMap.get( mapName : mapName2, username : username2)
def edges = EdgeNode.list().findAll {edgeNode ->
    edgeNode.username == username2 && edgeNode.mapName == mapName2 };


def writer = new StringWriter();
def mapBuilder = new MarkupBuilder(writer);

mapBuilder.graph
{
    map.consistOfDevices.each {MapNode mapNode ->
        def userName = mapNode.username;
        if( userName.equals(user.username)){
           mapBuilder.device(id: mapNode.nodeIdentifier, model: "model1", type: "router",  gauged:"true", expands : "true",
                                x: mapNode.xlocation, y : mapNode.ylocation );
        }
    }

    edges.each{ EdgeNode edgeNode ->
            mapBuilder.edge( source : edgeNode.from, target : edgeNode.to)
    }
}
return writer.toString();
