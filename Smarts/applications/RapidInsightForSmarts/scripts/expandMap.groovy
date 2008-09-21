import groovy.xml.MarkupBuilder;

def startTime = System.nanoTime();
def expandedDeviceName = params.expandedNodeName;
def nodeString = params.nodes;
def nodes = [];
if( nodeString !=  null)
{
   nodes = nodeString.splitPreserveAllTokens(";").findAll {it != ""};
}

def edgeMap = [:]


def deviceMap = [:];
nodes.each{node->
    def nodeData = node.splitPreserveAllTokens(",")
    def deviceName = nodeData[0];
    def isExpanded = nodeData[1];
    if(deviceName == expandedDeviceName)
    {
        isExpanded = "true";    
    }
    deviceMap[device.name] = [ "id" : deviceName, "model" : device.model, "type": device.creationClassName, "gauged" : "true", "expanded" : isExpanded];
}

def deviceSet = [:]
deviceMap.each{deviceName, deviceConfigMap->
    def device = RsComputerSystem.get( name : deviceName);
    if(device != null)
    {
        deviceSet[deviceName] = deviceConfigMap;
        if(deviceConfigMap.expanded == "true")
        {
            deviceConfigMap.expandable = "true"
            def links = device.connectedVia;
            links.each {link->
                def otherSide = getOtherSideName(link, deviceName);
                if(otherSide != null && !edgeMap.containsKey(deviceName + otherSide)
                            && !edgeMap.containsKey(otherSide + deviceName))
                {
                    edgeMap[deviceName + otherSide] = [ "source" : deviceName, "target" : otherSide];
                    if(!deviceMap.containsKey(otherSide) && !deviceSet.containsKey(otherSide))
                    {
                        def expandable = isExpandable(otherSide, edgeMap);
                        deviceSet[connectedSystem.name] = [ "id" : connectedSystem.name, "model" : connectedSystem.model, "type": connectedSystem.creationClassName, "gauged" : "true", "expandable" : expandable, "expanded":"false" ];
                    }
                }

            }
        }
        else
        {
            deviceConfigMap.expandable = isExpandable(deviceName, edgeMap);
        }

    }
}

def getOtherSideName(link, deviceName)
{
    def otherSide = null;
    if(link.a_ComputerSystem != deviceName)
    {
        otherSide = link.a_ComputerSystem;
    }
    else if(link.z_ComputerSystem != deviceName)
    {
        otherSide = link.z_ComputerSystem;
    }

    return otherSide;
}




// if device has a link with another device that is not in map
// returns true
def isExpandable( devName, edgeMap)
{
    def expandable = "false";
    def links = RsLink.search("a_ComputerSystemName=\"${devName}\" || z_ComputerSystemName=\"${devName}\"");
    links.each{link->
        def otherSide = getOtherSideName(link, devName);
        if( otherSide != null ){
            if( !edgeMap.containsKey(devName + otherSide)
                    && !edgeMap.containsKey(otherSide + devName) )
            {
                expandable = "true";
                return;
            }
		}
    }
    return expandable;
}

def writer = new StringWriter();
def mapBuilder = new MarkupBuilder(writer);

mapBuilder.graph()
{
    deviceSet.each {
        mapBuilder.node( it);
    }

    edgeMap.each {
        mapBuilder.edge( it);
    }
}

def endTime = System.nanoTime();
return writer.toString();