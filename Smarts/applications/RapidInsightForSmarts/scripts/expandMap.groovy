import groovy.xml.MarkupBuilder;

def startTime = System.nanoTime();

def postData = params.postData.data;
def data = postData.tokenize("|");

def expandedDeviceName = data[0].tokenize("=")[1];
def nodeTokens = data[1].tokenize("=");
def nodes = [];
if( nodeTokens.size()==2)
{
   nodes = nodeTokens[1].tokenize(";");
}

def edgeTokens = data[2].tokenize("=");
def edges = [];
if( edgeTokens.size()==2)
{
    edges = data[2].tokenize("=")[1].tokenize(";");
}



def edgeMap = [:]
edges.each{
    def edgeData = it.tokenize(",")
    def source = edgeData[0]
    def target = edgeData[1]

    edgeMap[source + target] = [ source : source, target : target];
}


def deviceMap = [:];
nodes.each{
    def deviceData = it.tokenize(",");
    def deviceName = deviceData[0];
    def x = deviceData[1];
    def y = deviceData[2];

    def device = RsComputerSystem.get( name : deviceName);
    def expandable = isExpandable(device, edgeMap);

    deviceMap[deviceName] = [ "id" : deviceName, "model" : device.model, "type": device.creationClassName, "gauged" : "true", "expands" : expandable ];
}

//println "time 2: ${System.nanoTime() - startTime}"
startTime = System.nanoTime();
def expandedDevice = RsComputerSystem.get( name : expandedDeviceName);
deviceMap[expandedDevice.name] =
    [ "id" : expandedDevice.name, "model" : expandedDevice.model, "type": expandedDevice.creationClassName, "gauged" : "true", "expands" : "false" ];

def links = expandedDevice.connectedVia;



links.each {
    def newDevices = it.connectedSystem;
    //println "device size " + newDevices.size();
    newDevices.each {
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
                    /*
                    println "devName ${devName}"
                    println "it.name ${it.name}"
                    println "edgeMap.containsKey(devName + it.name) ${edgeMap.containsKey(devName + it.name)}"
                    println "edgeMap.containsKey(it.name + devName) ${edgeMap.containsKey(it.name + devName))}"
                     */
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

//println "time 3: ${System.nanoTime() - startTime}"
startTime = System.nanoTime();
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
//println "time 4: ${endTime - startTime}"
return writer.toString();