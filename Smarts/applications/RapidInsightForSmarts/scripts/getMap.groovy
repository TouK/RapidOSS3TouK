import auth.RsUser
import groovy.xml.MarkupBuilder

def user = RsUser.findByUsername(web.session.username);
if(user == null){
    throw new Exception("User ${web.session.username} does not exist");
}

def username2 = web.session.username;
def mapName2 = params.mapName;

def map = TopoMap.get( mapName : mapName2, username : username2)

def edges = EdgeNode.list().findAll {edgeNode ->
    edgeNode.username == username2 && edgeNode.mapName == mapName2 
};

def edgeMap = [:];
def deviceMap = [:];

def devices =  map.consistOfDevices;

devices.each{
    def device = RsComputerSystem.get( name : it.nodeIdentifier);
    deviceMap[it.nodeIdentifier] = [ "id" : it.nodeIdentifier, "model" : device.model, "type": device.creationClassName,
    								"gauged" : "true", "expands" : it.expands, x : it.xlocation, y : it.ylocation ];
}

devices.each{
    if( it.expands == "false") {
        def deviceName = it.nodeIdentifier;
        def expandable = "false";
        def device = RsComputerSystem.get( name : deviceName);

        device.connectedVia.each {
            it.connectedSystem.each {

                if( deviceName != it.name)
                {
                    if( isInMap(it.name, devices))
                    {

                        if( !edgeMap.containsKey(deviceName + it.name)
                                && !edgeMap.containsKey(it.name + deviceName) )
                        {
                            edgeMap[deviceName + it.name ] = [ source : deviceName, target : it.name];
                        }

                    }
                    // device's connected system is not in map
                    // it's expandable is true
                    else
                    {
                        expandable = "true";
                    }
                }
            }
        }

        deviceMap[it.nodeIdentifier].expands = expandable;
    }
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
