import groovy.xml.MarkupBuilder;

def startTime = System.nanoTime();
def expandedDeviceName = params.expandedDeviceName;
def nodeString = params.nodes;
def nodes = [];
if( nodeString !=  null)
{
   nodes = nodeString.splitPreserveAllTokens(";").findAll {it != ""};
}

def edgeString = params.edges;
def edges = [];
if( edgeString != null)
{
    edges = edgeString.splitPreserveAllTokens(";").findAll {it != ""};
}



def edgeMap = [:]
edges.each{
    def edgeData = it.splitPreserveAllTokens(",")
    def source = edgeData[0]
    def target = edgeData[1]

    edgeMap[source + target] = [ source : source, target : target];
}


def deviceMap = [:];
nodes.each{
    def deviceData = it.splitPreserveAllTokens(",");
    def deviceName = deviceData[0];

    def device = RsComputerSystem.get( name : deviceName);
    def expandable = isExpandable(device, edgeMap);

    deviceMap[device.name] = [ "id" : deviceName, "model" : device.model, "type": device.creationClassName, "gauged" : "true", "expands" : expandable ];
}

def expandedDevice = RsComputerSystem.get( name : expandedDeviceName);
deviceMap[expandedDevice.name] =
    [ "id" : expandedDevice.name, "model" : expandedDevice.model, "type": expandedDevice.creationClassName, "gauged" : "true", "expands" : "false" ];

def links = expandedDevice.connectedVia;

links.each {
    def newDevices = it.connectedSystem;
    newDevices.each {
        // if there is no edge between expanded and new device
        if( it.name != expandedDevice.name
                && !edgeMap.containsKey(expandedDevice.name + it.name)
                && !edgeMap.containsKey(it.name + expandedDevice.name) )
        {
	        edgeMap[expandedDevice.name + it.name] = [ "source" : expandedDevice.name, "target" : it.name];
            def expandable = isExpandable(it, edgeMap);
        	deviceMap[it.name] = [ "id" : it.name, "model" : it.model, "type": it.creationClassName, "gauged" : "true", "expands" : expandable ];
        }
    }
}

// if device has a link with another device that is not in map
// returns true
def isExpandable( device, edgeMap)
{
    def devName = device.name;
    def expandable = "false";
    def connectedVias = device.connectedVia;
    connectedVias.each{
        def connectedSystems = it.connectedSystem;
        if( expandable == "false" ){
		    connectedSystems.each {
		        if( devName != it.name
		                &&!edgeMap.containsKey(devName + it.name)
		                && !edgeMap.containsKey(it.name + devName) )
		        {
                    expandable = "true";
		            return;
		        }
		    }
		}
		else
			return;
    }
    return expandable;
}

def writer = new StringWriter();
def mapBuilder = new MarkupBuilder(writer);

mapBuilder.graph()
{
    deviceMap.each {
        mapBuilder.device( id: it.value.id, model : it.value.model, type : it.value.type, gauged : it.value.gauged, expands : it.value.expands);
    }

    edgeMap.each {
        mapBuilder.edge( source : it.value.source, target : it.value.target);
    }
}

def endTime = System.nanoTime();
return writer.toString();