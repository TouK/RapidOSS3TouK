import auth.RsUser
import groovy.xml.MarkupBuilder

def user = RsUser.findByUsername(web.session.username);
if(user == null){
    throw new Exception("User ${web.session.username} does not exist");
}

def username2 = web.session.username;
def mapName2 = params.mapName;

def map = TopoMap.get( mapName : mapName2, username : username2)

def edgeMap = [:];
def deviceMap = [:];

def devices =  map.consistOfDevices;

devices.each{
    def device = RsComputerSystem.get( name : it.nodeIdentifier);
    if(device != null)
    {
    	deviceMap[it.nodeIdentifier] = [ "id" : it.nodeIdentifier, "model" : device.model, "type": device.creationClassName, "gauged" : "true", "expands" : it.expands, x : it.xlocation, y : it.ylocation ];
   	}
}

deviceMap.each{deviceName,  nodeData->
    def expandable = "false";
    def device = RsComputerSystem.get( name : deviceName);
    def edgesToBeAdded = [:];
    device.connectedVia.each {link->
        link.connectedSystem.each {connectedSystem->

            if( deviceName != connectedSystem.name)
            {
                if(nodeData.expands == "true")
                {
                    if(!edgeMap.containsKey(connectedSystem.name+deviceName ))
                    {
                        edgesToBeAdded[deviceName + connectedSystem.name] =  [ source : deviceName, target : connectedSystem.name];
                    }
                }
                else
                {
                    expandable = "true";
                    return;
                }
            }
        }
        if(expandable == "true")
        {
            return;
        }
    }
    if(nodeData.expands )

}

def writer = new StringWriter();
def mapBuilder = new MarkupBuilder(writer);

mapBuilder.graph
{
    deviceMap.each {
        mapBuilder.node( id: it.value.id, model : it.value.model, type : it.value.type, gauged : it.value.gauged, expands : it.value.expands, x: it.value.x, y: it.value.y);
    }

    edges.each {
        if( !edgeMap.containsKey(it.from + it.to)
                        && !edgeMap.containsKey(it.to + it.from) )
        {
            it.remove();
        }
        else
        {
            mapBuilder.edge( source : it.from, target : it.to);
        }
    }

}


def isInMap( deviceName, devices)
{
    def inMap = false;
    devices.each {
	    println "it name : ${ it.nodeIdentifier}"
        if( it.nodeIdentifier == deviceName)
        {
            inMap = true;
            return;
        }
    }
    return inMap;
}



return writer.toString();
