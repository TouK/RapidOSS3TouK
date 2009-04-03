/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
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
    def x = nodeData[2];
    def y = nodeData[3];
    def device = RsComputerSystem.get( name : deviceName);
    if(device != null)
    {
        if(deviceName == expandedDeviceName)
        {
            isExpanded = "true";
        }
        deviceMap[device.name] = [ "id" : device.name, "model" : device.model, "type": device.className, "gauged" : "true", "expanded" : isExpanded, x:x, y:y];
    }

}

def deviceSet = [:]
deviceMap.each{deviceName, deviceConfigMap->
    deviceSet[deviceName] = deviceConfigMap;
    if(deviceConfigMap.expanded == "true")
    {
        deviceConfigMap.expandable = "true"
        def links=RsLink.searchEvery("a_ComputerSystemName:${deviceName.exactQuery()} OR z_ComputerSystemName:${deviceName.exactQuery()}");
        links.each {link->
            def otherSide = getOtherSideName(link, deviceName);

            if(otherSide != null && !edgeMap.containsKey(deviceName + otherSide)
                        && !edgeMap.containsKey(otherSide + deviceName))
            {
                def otherSideDevice = RsComputerSystem.get(name:otherSide);
                if(otherSideDevice != null){
                    edgeMap[deviceName + otherSide] = [ "source" : deviceName, "target" : otherSide];
                    if(!deviceMap.containsKey(otherSide) && !deviceSet.containsKey(otherSide))
                    {
                        deviceSet[otherSide] = [ "id" : otherSide, "model" : otherSideDevice.model, "type": otherSideDevice.className, "gauged" : "true", "expandable" : "false", "expanded":"false" ];
                    }
                }
            }

        }
    }
}

//we should generate isExpandable after all map is generated for only unexpanded devices
deviceSet.each{devName, devConfig->
     if(devConfig.expanded=="false")
     {
        devConfig.expandable = isExpandable(devName, edgeMap);
     }
}

def getOtherSideName(link, deviceName)
{
    def otherSide = null;
    if(link.a_ComputerSystemName != deviceName)
    {
        otherSide = link.a_ComputerSystemName;
    }
    else if(link.z_ComputerSystemName != deviceName)
    {
        otherSide = link.z_ComputerSystemName;
    }

    return otherSide;
}




// if device has a link with another device that is not in map
// returns true
def isExpandable( devName, edgeMap)
{
    def expandable = "false";
    def links = RsLink.searchEvery("a_ComputerSystemName:${devName.exactQuery()} OR z_ComputerSystemName:${devName.exactQuery()}");
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
    deviceSet.each {devName, devConfig->
        mapBuilder.node( devConfig);
    }

    edgeMap.each {edge, edgeConf->
        mapBuilder.edge( edgeConf);
    }
}

def endTime = System.nanoTime();
return writer.toString();